angular.module('kola.services', ['uuid'])

.factory('Chats', function() {
  // Might use a resource here that returns a JSON array

  // Some fake testing data
  var chats = [{
    id: 0,
    name: 'Ben Sparrow',
    lastText: 'You on your way?',
    face: 'https://pbs.twimg.com/profile_images/514549811765211136/9SgAuHeY.png'
  }, {
    id: 1,
    name: 'Max Lynx',
    lastText: 'Hey, it\'s me',
    face: 'https://avatars3.githubusercontent.com/u/11214?v=3&s=460'
  },{
    id: 2,
    name: 'Adam Bradleyson',
    lastText: 'I should buy a boat',
    face: 'https://pbs.twimg.com/profile_images/479090794058379264/84TKj_qa.jpeg'
  }, {
    id: 3,
    name: 'Perry Governor',
    lastText: 'Look at my mukluks!',
    face: 'https://pbs.twimg.com/profile_images/598205061232103424/3j5HUXMY.png'
  }, {
    id: 4,
    name: 'Mike Harrington111',
    lastText: 'This is wicked good ice cream.',
    face: 'https://pbs.twimg.com/profile_images/578237281384841216/R3ae1n61.png'
  }];

  return {
    all: function() {
      return chats;
    },
    remove: function(chat) {
      chats.splice(chats.indexOf(chat), 1);
    },
    get: function(chatId) {
      for (var i = 0; i < chats.length; i++) {
        if (chats[i].id === parseInt(chatId)) {
          return chats[i];
        }
      }
      return null;
    }
  };
})

.service('dbService', function ($rootScope, $q, $state, $ionicPlatform, $ionicLoading, $cordovaFile, $cordovaFileTransfer, $window, onlineStateService, serverUrl, rfc4122) {
  var self = this;
  var firstRun = true;
  self.initDeferred = $q.defer();

  var CONVERSIONS = {
    "task" : {
      "creator": { "refTable":"user" },
      "assignee": { "refTable":"user" },
      "steps": { "refTable":"taskStep" },
      "attachments": { "refTable":"asset" },
      "resources": { "refTable":"asset" },
      "reflectionQuestions": { "refTable":"reflectionQuestion" }
    },
    "taskStep" : {
      "creator": { "refTable":"user" },
      "attachments": { "refTable":"asset" },
      "resources": { "refTable":"asset" },
      "reflectionQuestions": { "refTable":"reflectionQuestion" }
    },
    "taskDocumentation" : {
      "creator": { "refTable":"user" },
      "attachments": { "refTable":"asset" }
    },
    "reflectionAnswer" : {
      "creator": { "refTable":"user" }
    },
    "asset" : {
      "creator": { "refTable":"user" }
    }
  }
  var TABLES_TO_SYNC = [
    {tableName : 'user'},
    {tableName : 'asset'},
    {tableName : 'reflectionQuestion'},
    {tableName : 'reflectionAnswer'},
    {tableName : 'taskStep'},
    {tableName : 'taskDocumentation'},
    {tableName : 'task'}
  ];

  self.sync = dependOnInit(function() {
    var deferred = $q.defer();
    if (canSync()) {
      $rootScope.onlineState.isSyncing = true;
      DBSYNC.syncNow(onSyncProgress, function(result) {
        $rootScope.onlineState.isSyncing = false;
        if (result && result.syncOK === true) {
          // synchronized successfully
        }
        if (result && result.status == 401) {
          openAccountTab("Login fehlgeschlagen. Bitte überprüfen Sie Nutzernamen und Passwort.");
          deferred.reject("sync failed");
        }
        else {
          $rootScope.$broadcast("syncFinished");
          deferred.resolve();
        }
      });
    }
    else {
      deferred.reject("sync denied");
    }
    return deferred.promise;
  });

  self.all = dependOnInit(function(tableName) {
    var d = $q.defer();
    self.db.transaction(function (t) {
      t.executeSql("select doc from " + tableName, [], function (tx, results) {
        var docs = []
        for (var i=0; i<results.rows.length; i++) {
          var doc = JSON.parse(results.rows.item(i).doc);
          doc._table = tableName;
          docs.push(doc);
        }
        d.resolve(docs);
      }, function (err) {
        d.reject(err);
      });
    }, function (err) {
        d.reject(err);
    });
    return d.promise;
  });

  self.save = dependOnInit(function(doc) {
    var d = $q.defer();
    self.db.transaction(function (tx) {
      var promises = [];
      if (angular.isArray(doc)) {
        angular.forEach(doc, function(o) {
          promises.push(_save(o, tx));
        });
      }
      else {
        promises.push(_save(doc, tx));
      }
      $q.all(promises).then(function() {
        d.resolve();
      }, function(err) {
        d.reject(err);
      })
    }, function (err) {
        throw err;
    });

    return d.promise.then(function() {
      // saving should succeed in offline mode. since sync() rejects when offline, check canSync() first.
      if (canSync()) {
        return self.sync();
      }
    });
  });

  self._get = dependOnInit(function(id, tableName) {
    var d = $q.defer();
    self.db.transaction(function (tx) {
      tx.executeSql("select doc from " + tableName + " where id=?", [id], function (tx, results) {
        if (results.rows.length == 1) {
          var doc = JSON.parse(results.rows.item(0).doc);
          doc._table = tableName;
          if (tableName == "asset" && doc.subType == "attachment") {
            self._setLocalURL(doc).then(function() {
              d.resolve(doc);
            }, function(err) {
              console.log(err);
              d.reject();
            });
          }
          else {
            d.resolve(doc);
          }
        }
        else {
          d.reject("no document with id '" + id + "' in table '" + tableName + "'");
        }
      }, function (err) {
        console.log(err);
        d.reject(err);
      });
    }, function (err) {
        console.log(err);
        d.reject(err);
    });
    return d.promise;
  });

  self._setLocalURL = dependOnInit(function(attachment) {
    var d = $q.defer();
    if (window.cordova) {
      $cordovaFile.checkFile(self._assetsDirName, attachment.id).then(function (fileEntry) {
        // file exists.
        // for images (and TODO: videos) use cdvfile:// url to let the cordova webview load the attachments.
        // for other file types (e.g. PDFs), use fileEntry's nativeURL, so that Android can open it natively.
        if (attachment.mimeType.indexOf("image/") == 0) {
          fileEntry.file(function(f) {
            attachment._localURL = f.localURL;
            d.resolve();
          });
        }
        else {
            attachment._localURL = fileEntry.nativeURL;
            d.resolve();
        }
      }, function() {
        // file not found
        d.reject("asset file NOT found " + attachment.id);        
      });
    }
    else {
      d.resolve();
    }
    return d.promise;
  });

  function _init() {
    var user = localStorage["user"];
    if (user) {
      var password = localStorage["password"];
      if (window.cordova) {
  //      self._assetsDirName = cordova.file.dataDirectory + "assets/";
        self._cacheDirName = cordova.file.externalCacheDirectory;
        self._dataDirName = cordova.file.externalDataDirectory;
        self._assetsDirName = self._dataDirName + "assets/";
        $cordovaFile.createDir(self._dataDirName, "assets", false).finally(function() {
          window.resolveLocalFileSystemURL(self._assetsDirName, function(dir) {
            self._assetsDir = dir;
          });
        });
      }
      self.db = (window.cordova ? window.sqlitePlugin : window).openDatabase(user, '1', 'kola DB', 1024 * 1024 * 100);
      self.db.transaction(function(tx) {
        try {
          tx.executeSql('CREATE TABLE IF NOT EXISTS user (id integer primary key, doc text not null)');
          tx.executeSql('CREATE TABLE IF NOT EXISTS asset (id varchar(255) not null primary key, doc text not null)');
          tx.executeSql('CREATE TABLE IF NOT EXISTS reflectionQuestion (id integer primary key, doc text not null)');
          tx.executeSql('CREATE TABLE IF NOT EXISTS reflectionAnswer (id varchar(255) not null primary key, doc text not null)');
          tx.executeSql('CREATE TABLE IF NOT EXISTS task (id varchar(255) not null primary key, doc text not null)');
          tx.executeSql('CREATE TABLE IF NOT EXISTS taskStep (id varchar(255) not null primary key, doc text not null)');
          tx.executeSql('CREATE TABLE IF NOT EXISTS taskDocumentation (id varchar(255) not null primary key, doc text not null)');
        }
        catch(err) {
          console.log(err);
        }
      });
      DBSYNC.initSync(TABLES_TO_SYNC, self.db, {foo:"bar"}, serverUrl + "/api/changes", serverUrl + "/api/upload", self._assetsDirName, function() {
        self.initDeferred.resolve();
        if (firstRun) {
          $rootScope.$watch("onlineState.isOnline", onOnlineStateChanged);
          firstRun = false;
        }
      }, $cordovaFileTransfer, $cordovaFile, $q, user, password);
    }
    else {
      self.initDeferred.reject("no_user");
    }
  }

  function dependOnInit(fn) {
    return function() {
      var args = arguments;
      return self.initDeferred.promise.then(function() {
        return fn.apply(this, args);
      }, function(err) {
        if ("no_user" == err) {
          openAccountTab("Willommen bei KOLA! Bitte geben Sie Ihren Nutzernamen und Passwort ein.");
        }
        else {
          console.log(err);
        }
        return $q.reject("init failed");
      });
    }
  }

  function updateLogin() {
    self.initDeferred = $q.defer();
    _init();
    return self.initDeferred.promise;
  };

  function onOnlineStateChanged() {
    //console.log("--- online state changed", $rootScope.onlineState);
    self.sync();
  }

  function onSyncProgress(msg) {
    //console.log(msg);
  }

  function canSync() {
    return $rootScope.onlineState.isOnline && !$rootScope.onlineState.isSyncing;
  }

  function _create(tableName, props) {
    return angular.extend({ id:rfc4122.v4(), _table:tableName }, props);
  }

  function createTask() {
    var task = _create("task", { isTemplate:false, reflectionQuestions:[] });
    // auto link standard reflection questions
    return all("reflectionQuestion").then(function(reflectionQuestions) {
      angular.forEach(reflectionQuestions, function(reflectionQuestion) {
        if (reflectionQuestion.autoLink) {
          task.reflectionQuestions.push(reflectionQuestion);
        }
      });
      return task;
    });
  }

  function createAttachment(parent) {
    var attachment = _create("asset", { subType:"attachment" });
    parent.attachments = parent.attachments || [];
    parent.attachments.push(attachment);
    return attachment;
  }

  function createTaskDocumentation(targetId, isStep) {
    var options = {};
    if (isStep) {
      options.step = targetId;
    }
    else {
      options.task = targetId;
    }
    return _create("taskDocumentation", options);
  }

  function createReflectionAnswer(taskId, questionId) {
    return _create("reflectionAnswer", { task:taskId, question:questionId });
  }

  function get(id, tableName) {
    return self._get(id, tableName).then(function(doc) {
      return _resolveIds(doc, CONVERSIONS[tableName]).then(function() {
        return doc;
      });
    });
  }

  function _save(doc, tx) {
    var d = $q.defer();
    if (!doc._table) {
      d.reject("Object has no _table property");
    }
    else {
      if (!doc.id) {
        doc.id = rfc4122.v4();
      }
      var copy = angular.copy(doc);
      var localURL = copy._localURL;

      // delete local properties (all properties beginning with an underscore)
      for (var property in copy) {
        if (property.indexOf("_") == 0) {
          delete copy[property];
        }
      }

      _replaceIds(copy, CONVERSIONS[doc._table]);
      console.log("--- saving", copy);

      tx.executeSql("INSERT OR REPLACE INTO " + doc._table + " (id, doc) values (?, ?)", [copy.id, JSON.stringify(copy)], function (tx, results) {
        if (doc._table == "asset" && copy.subType == "attachment" && localURL) {
          window.resolveLocalFileSystemURL(localURL, function(fileEntry) {
            // move attachment data to assets dir if it is not already there
            if (fileEntry.nativeURL != (self._assetsDirName + copy.id)) {
              // check if the asset is in the app's cache folder. if yes, move the asset, if not copy it (to avoid removing files selected from the photo library).
              if (fileEntry.nativeURL.indexOf(self._cacheDirName) == 0) {
                fileEntry.moveTo(self._assetsDir, copy.id, function() {
                  d.resolve();
                }, function (err) {
                  // error
                  d.reject(err);
                });
              }
              else {
                fileEntry.copyTo(self._assetsDir, copy.id, function() {
                  d.resolve();
                }, function (err) {
                  // error
                  d.reject(err);
                });
              }
            }
            else {
              // file is already in assets dir
              d.resolve();
            }
            /*
            var name = fileEntry.name;
            var dir = fileEntry.nativeURL.split(name)[0];
            console.log("--- moving attachment from " + (dir + name) + " to " + (self._assetsDirName + copy.id));
            $cordovaFile.moveFile(dir, name, self._assetsDirName, copy.id).then(function (success) {
              d.resolve();
            }, function (error) {
              // error
              console.log(error);
              d.reject();
            });
*/
          });
        }
        else {
          d.resolve();
        }
      }, function (err) {
        d.reject(err);
      });
    }
    return d.promise;
  }

  function resolveIds(target, tableName) {
    return _resolveIds(target, CONVERSIONS[tableName]);
  }

  function _resolveIds(target, tableConversions) {
    var promises = [];
    angular.forEach(tableConversions, function(conversionDef, key) {
      var ids = target[key];
      if (ids != null) {
        if (angular.isArray(ids)) {
          angular.forEach(ids, function(id, index) {
            promises.push(self._get(id, conversionDef.refTable).then(function(doc) {
              target[key][index] = doc;
              return _resolveIds(doc, CONVERSIONS[conversionDef.refTable]);
            }));
          });
        }
        else {
          promises.push(self._get(ids, conversionDef.refTable).then(function(doc) {
            target[key] = doc;
            return _resolveIds(doc, CONVERSIONS[conversionDef.refTable]);
          }));
        }
      }
    });
    return $q.all(promises);
  }

  function _replaceIds(target, tableConversions) {
    angular.forEach(tableConversions, function(conversionDef, key) {
      var values = target[key];
      if (values != null) {
        if (angular.isArray(values)) {
          angular.forEach(values, function(value, index) {
            if (value.id) {
              target[key][index] = value.id;
            }
          });
        }
        else {
          if (values.id) {
            target[key] = values.id;
          }
        }
      }
    });
  }

  function openAccountTab(message) {
    $ionicLoading.show({template:message, duration:2000});
    $state.go("tab.account");
  }

  $ionicPlatform.ready(_init);


  return {
    sync:self.sync,
    all:self.all,
    save:self.save,
    updateLogin:updateLogin,
    get:get,
    resolveIds:resolveIds,
    createTask:createTask,
    createAttachment:createAttachment,
    createTaskDocumentation:createTaskDocumentation,
    createReflectionAnswer:createReflectionAnswer
  }
})

.service('onlineStateService', function ($ionicPlatform, $rootScope, $cordovaNetwork) {
  $rootScope.onlineState = {"isOnline":false, "isWifi":false, "isSyncing":false};
  function onOnlineStateChange(event, networkState) {
    $rootScope.onlineState.isOnline = networkState !== "none";
    $rootScope.onlineState.isWifi = networkState === "wifi";
  }

  $rootScope.$on('$cordovaNetwork:online', onOnlineStateChange);
  // listen for Offline event
  $rootScope.$on('$cordovaNetwork:offline', onOnlineStateChange);

  $ionicPlatform.ready(function() {
    // only use ngCordova on native devices
    if (ionic.Platform.isWebView()) {
      onOnlineStateChange(null, $cordovaNetwork.getNetwork());
    }
    else {
      onOnlineStateChange(null, "wifi");
    }
  });
})

.service('modalDialog', function ($ionicModal) {
  this.createModalDialog = function(scope, templateUrl) {
    $ionicModal.fromTemplateUrl(templateUrl, {
      scope: scope,
      animation: 'slide-in-up'
    }).then(function(modal) {
      scope.modal = modal
    })  

    scope.openModal = function() {
      scope.modal.show()
    }

    scope.closeModal = function() {
      scope.modal.hide();
    };

    scope.$on('$destroy', function() {
      scope.modal.remove();
    });
  }
})

.service('mediaAttachment', function ($cordovaCamera, $cordovaCapture, $ionicLoading, $rootScope, $cordovaFile, $q, dbService, rfc4122) {
  this.attachPhoto = function(doc) {
    var options = {
      quality: 90,
//      destinationType: Camera.DestinationType.DATA_URL,
      destinationType: Camera.DestinationType.FILE_URI,
      sourceType: Camera.PictureSourceType.CAMERA,
      allowEdit: false,
      //encodingType: Camera.EncodingType.JPEG,
//      targetWidth: 100,
//      targetHeight: 100,
      popoverOptions: CameraPopoverOptions,
      saveToPhotoAlbum: false
    };
    
    return $cordovaCamera.getPicture(options).then(function(imageUrl) {
      var d = $q.defer();
      window.resolveLocalFileSystemURL(imageUrl, function(fileEntry) {
        fileEntry.file(function(file) {
          var attachment = dbService.createAttachment(doc);
          attachment.name = file.name;
          attachment.mimeType = file.type;
          attachment._localURL = file.localURL;
          if (!$rootScope.$$phase) {
            $rootScope.$apply();
          }
          d.resolve(attachment);
        });
      });
      return d.promise;
    }, function(err) {
      // An error occurred. Show a message to the user, but only if not a normal "Canceled" event.
      console.log(err);
      if (typeof err == "string" && err.indexOf("cancel") < 0) {
        $ionicLoading.show({template:err, duration:2000});
      }
    });
  };

  this.attachChosenMedia = function(doc) {
    var options = {
      destinationType: Camera.DestinationType.FILE_URI,
      sourceType: Camera.PictureSourceType.SAVEDPHOTOALBUM,
      allowEdit: false,
      MediaType : Camera.MediaType.ALLMEDIA
    };
    return $cordovaCamera.getPicture(options).then(function(imageUrl) {
      var d = $q.defer();
      if (imageUrl.substring(0,21)=="content://com.android") {
        photo_split=imageUrl.split("%3A");
        imageUrl="content://media/external/images/media/"+photo_split[1];
      }
      window.resolveLocalFileSystemURL(imageUrl, function(fileEntry) {
        fileEntry.file(function(file) {
          var attachment = dbService.createAttachment(doc);
          attachment.name = file.name;
          attachment.mimeType = file.type;
          attachment._localURL = file.localURL;
          if (!$rootScope.$$phase) {
            $rootScope.$apply();
          }
          d.resolve(attachment);
        });
      }, function(err) {
        console.log(err);
        $ionicLoading.show({template:err, duration:2000});
      });
      return d.promise;
    }, function(err) {
      // An error occurred. Show a message to the user, but only if not a normal "Canceled" event.
      console.log(err);
      if (typeof err == "string" && err.indexOf("cancel") < 0) {
        $ionicLoading.show({template:err, duration:2000});
      }
    });
  };

  this.attachVideo = function(doc) {
    return $cordovaCapture.captureVideo().then(function(mediaFiles) {
      if (mediaFiles.length === 1) {
        var file = mediaFiles[0];
        var attachment = dbService.createAttachment(doc);
        attachment.name = file.name;
        attachment.mimeType = file.type;
        attachment._localURL = file.fullPath;
        if (!$rootScope.$$phase) {
          $rootScope.$apply();
        }
      }
    }, function(err) {
      // An error occurred. Show a message to the user, but only if not a normal "Canceled" event.
      console.log(err);
      if (err && err.code != 3) {
        $ionicLoading.show({template:err, duration:2000});
      }
    });
  };

  this.attachAudio = function(doc) {
    return $cordovaCapture.captureAudio().then(function(mediaFiles) {
      if (mediaFiles.length === 1) {
        var file = mediaFiles[0];
        console.log("--- captured audio file -> ", file);
        var attachment = dbService.createAttachment(doc);
        attachment.name = file.name;
        attachment.mimeType = file.type;
        attachment._localURL = file.fullPath;
        console.log("--- new attachment", attachment);
        console.log("--- doc after attachment creation -> ", doc);
        if (!$rootScope.$$phase) {
          $rootScope.$apply();
        }
      }
    }, function(err) {
      // An error occurred. Show a message to the user
      console.log(err);
      $ionicLoading.show({template:err, duration:2000});
    });
  };
})
