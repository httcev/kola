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

.service('dbService', function ($rootScope, $q, $state, $ionicLoading, $cordovaFile, $cordovaFileTransfer, $window, onlineStateService, databaseName, serverUrl, rfc4122) {
  var self = this;
  self._assetsDir = null;

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

  var initDeferred = $q.defer();
  var initPromise = initDeferred.promise;
  if (window.cordova) {
    document.addEventListener('deviceready', function () {
      _init();
    });
  }
  else {
    _init();
  }

  function _init() {
    if (window.cordova) {
//      self._assetsDir = cordova.file.dataDirectory + "assets/";
      self._assetsDir = cordova.file.externalDataDirectory + "assets/";
      $cordovaFile.createDir(cordova.file.dataDirectory, "assets", false);//.then(function() {alert("created asset dir")},function() {alert("not created asset dir")});
    }
    self.db = (window.cordova ? window.sqlitePlugin : window).openDatabase("kola.db", '1', 'kola DB', 1024 * 1024 * 100);
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
      catch(e) {
        console.log(e);
      }
    });
    _initSync().then(function() {
      $rootScope.$watch("onlineState.isOnline", onOnlineStateChanged);
      initDeferred.resolve();
    });
  }

  function onOnlineStateChanged() {
    console.log("--- online state changed:", $rootScope.onlineState);
    sync();
  }

  function onSyncProgress(msg) {
  }

  function canSync() {
    return $rootScope.onlineState.isOnline && !$rootScope.onlineState.isSyncing;
  }

  function sync() {
    return initPromise.then(function() {
      if (canSync()) {
        $rootScope.onlineState.isSyncing = true;
        DBSYNC.syncNow(onSyncProgress, function(result) {
          $rootScope.onlineState.isSyncing = false;
          if (result && result.syncOK === true) {
            // synchronized successfully
          }
          if (result && result.status == 401) {
            var message;
            if (localStorage["user"]) {
              message = "Login fehlgeschlagen. Bitte überprüfen Sie Nutzernamen und Passwort.";
            }
            else {
              message = "Willommen bei KOLA! Bitte geben Sie Ihren Nutzernamen und Passwort ein.";
            }
            $ionicLoading.show({template:message, duration:2000});
            $state.go("tab.account");
            return $q.reject("sync failed");
          }
          $rootScope.$broadcast("syncFinished");
        });
      }
      else {
        return $q.reject("cannot sync, because either offline or currently syncing already.");
      }
    });
  }

  function _initSync() {
    var deferred = $q.defer();
    // theTablesToSync, dbObject, theSyncInfo, theServerUrl, assetUrl, assetDir, callBack, $cordovaFileTransfer, username, password, timeout) 
    DBSYNC.initSync(TABLES_TO_SYNC, self.db, {foo:"bar"}, serverUrl + "/api/changes", serverUrl + "/api/upload", self._assetsDir, function() {
      deferred.resolve();
    }, $cordovaFileTransfer, $cordovaFile, $q, localStorage["user"], localStorage["password"]);
    return deferred.promise;
  }

  function updateLogin() {
    return initPromise.then(function() {
      return _initSync();
    });
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

  function all(tableName) {
    return initPromise.then(function() {
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
  }

  function get(id, tableName) {
    return _get(id, tableName).then(function(doc) {
      return _resolveIds(doc, CONVERSIONS[tableName]).then(function() {
        return doc;
      });
    });
  }

  function save(doc) {
    return initPromise.then(function() {
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
          return sync();
        }
        return $q.resolve();
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
      console.log("--- saving", doc);
      var copy = angular.copy(doc);
      var localURL = copy._localURL;

      delete copy._table;
      delete copy._localURL;

      _replaceIds(copy, CONVERSIONS[doc._table]);
      console.log("--- replaced", copy);

      tx.executeSql("INSERT OR REPLACE INTO " + doc._table + " (id, doc) values (?, ?)", [copy.id, JSON.stringify(copy)], function (tx, results) {
        if (doc._table == "asset" && copy.subType == "attachment" && localURL && localURL.indexOf(self._assetsDir) != 0) {
          // copy attachment data from temp dir to assets dir
          window.resolveLocalFileSystemURL(localURL, function(fileEntry) {
            console.log(fileEntry);
            var name = fileEntry.name;
            var dir = fileEntry.nativeURL.split(name)[0];
            console.log("--- moving attachment from " + (dir + name) + " to " + (self._assetsDir + copy.id));
            $cordovaFile.moveFile(dir, name, self._assetsDir, copy.id).then(function (success) {
              d.resolve();
            }, function (error) {
              // error
              console.log(error);
              d.reject();
            });
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
            promises.push(_get(id, conversionDef.refTable).then(function(doc) {
              target[key][index] = doc;
              return _resolveIds(doc, CONVERSIONS[conversionDef.refTable]);
            }));
          });
        }
        else {
          promises.push(_get(ids, conversionDef.refTable).then(function(doc) {
            target[key] = doc;
            return _resolveIds(doc, CONVERSIONS[conversionDef.refTable]);
          }));
        }
      }
    });
    return $q.all(promises);
  }

  function _get(id, tableName) {
    return initPromise.then(function() {
      var d = $q.defer();
      self.db.transaction(function (tx) {
        tx.executeSql("select doc from " + tableName + " where id=?", [id], function (tx, results) {
          if (results.rows.length == 1) {
            var doc = JSON.parse(results.rows.item(0).doc);
            doc._table = tableName;
            if (tableName == "asset" && doc.subType == "attachment") {
              _setLocalURL(doc).then(function() {
                d.resolve(doc);
              }, function() {
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
          d.reject(err);
        });
      }, function (err) {
          d.reject(err);
      });
      return d.promise;
    });
  }

  function _setLocalURL(attachment) {
    return initPromise.then(function() {
      var d = $q.defer();
      if (window.cordova) {
        console.log("--- replacing attachment url with local url -> " + attachment.id);

        $cordovaFile.checkFile(self._assetsDir, attachment.id).then(function (fileEntry) {
          console.log("--- asset file found " + attachment.id)
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

  return {
    updateLogin:updateLogin,
    sync:sync,
    all:all,
    get:get,
    save:save,
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
