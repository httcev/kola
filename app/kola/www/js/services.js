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
  var assetsDir = null;
  if (window.cordova) {
    assetsDir = cordova.file.dataDirectory + "assets/";
    $cordovaFile.createDir(cordova.file.dataDirectory, "assets", false);//.then(function() {alert("created asset dir")},function() {alert("not created asset dir")});
  }

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

  function onOnlineStateChanged() {
    console.log("--- online state changed:", $rootScope.onlineState);
    sync();
  }

  function onEndSync(result) {
    if (result && result.syncOK === true) {
      // synchronized successfully
    }
    if (result && result.status == 401) {
      $ionicLoading.show({template: "Login fehlgeschlagen. Bitte überprüfen Sie Nutzernamen und Passwort.", duration:2000});
      $state.go("tab.account");
    }

    $rootScope.onlineState.isSyncing = false;
    if (!$rootScope.$$phase) {
      $rootScope.$apply();
    }
  }

  function onSyncProgress(msg) {
  }

  function canSync() {
    return $rootScope.onlineState.isOnline && !$rootScope.onlineState.isSyncing;
  }

  function sync() {
    var d = $q.defer();
    if (canSync()) {
      $rootScope.onlineState.isSyncing = true;
      DBSYNC.syncNow(onSyncProgress, function(result) {
        if (result && result.syncOK === true) {
          // synchronized successfully
        }
        if (result && result.status == 401) {
          $ionicLoading.show({template: "Login fehlgeschlagen. Bitte überprüfen Sie Nutzernamen und Passwort.", duration:2000});
          $state.go("tab.account");
          d.reject();
        }

        $rootScope.onlineState.isSyncing = false;
        if (!$rootScope.$$phase) {
          $rootScope.$apply();
        }
        d.resolve();
      });
    }
    else {
      d.reject();
    }
    return d.promise;
  }

  function initSync() {
    var deferred = $q.defer();
    // theTablesToSync, dbObject, theSyncInfo, theServerUrl, assetUrl, assetDir, callBack, $cordovaFileTransfer, username, password, timeout) 
    DBSYNC.initSync(TABLES_TO_SYNC, db, {foo:"bar"}, serverUrl + "/api/changes", serverUrl + "/api/upload", assetsDir, function() {
      if (canSync()) {
        sync().then(function() {
          deferred.resolve();
        }, function() {
          deferred.reject();
        });
      }
      else {
        deferred.resolve();
      }
    }, $cordovaFileTransfer, $q, localStorage["user"], localStorage["password"]);
    return deferred.promise;
  }

  function _create(tableName, props) {
    return angular.extend({ id:rfc4122.v4(), _table:tableName }, props);
  }

  function createAttachment(parent) {
    var attachment = _create("asset", { subType:"attachment" });
    parent.attachments = parent.attachments || [];
    parent.attachments.push(attachment);
    return attachment;
  }

  function createTaskDocumentation(taskId) {
    return _create("taskDocumentation", { task:taskId });
  }

  function createReflectionAnswer(taskId, questionId) {
    return _create("reflectionAnswer", { task:taskId, question:questionId });
  }

  function all(tableName) {
    var d = $q.defer();
    db.transaction(function (t) {
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
  }

  function get(id, tableName) {
    return _get(id, tableName).then(function(doc) {
      return _resolveIds(doc, CONVERSIONS[tableName]).then(function() {
        return doc;
      });
    });
  }

  function save(doc) {
    var d = $q.defer();
    db.transaction(function (tx) {
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
        if (doc._table == "asset" && copy.subType == "attachment" && localURL && localURL.indexOf(assetsDir) != 0) {
          // copy attachment data from temp dir to assets dir
          window.resolveLocalFileSystemURL(localURL, function(fileEntry) {
            console.log(fileEntry);
            console.log("--- moving attachment from " + (fileEntry.filesystem.root.nativeURL + fileEntry.name) + " to " + (assetsDir + copy.id));
            $cordovaFile.moveFile(fileEntry.filesystem.root.nativeURL, fileEntry.name, assetsDir, copy.id).then(function (success) {
              console.log("MOOOVED!");
              d.resolve();
            }, function (error) {
              // error
              console.log("ERROR MOOOVING!");
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
    var d = $q.defer();
    db.transaction(function (tx) {
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
          d.reject("no document with id '" + id + "' in table '" + conversionDef.refTable + "'");
        }
      }, function (err) {
        d.reject(err);
      });
    }, function (err) {
        d.reject(err);
    });
    return d.promise;    
  }

  function _setLocalURL(attachment) {
    var d = $q.defer();
    if (window.cordova) {
      console.log("--- replacing attachment url with local url -> " + attachment.id);

      $cordovaFile.checkFile(assetsDir, attachment.id).then(function (fileEntry) {
        console.log("--- asset file found " + attachment.id)
        // file exists
        fileEntry.file(function(f) {
          attachment._localURL = f.localURL;
          d.resolve();
        });
      }, function() {
        console.log("--- asset file NOT found " + attachment.id)
        // file not found
        d.reject();        
      });
/*
      , function () {
        // file not found
      console.log("--- url=" +attachment.url);
      console.log("--- target=" +assetsDir + attachment.id);
      var options = {};
      try {

      $cordovaFileTransfer.download(attachment.url, assetsDir + attachment.id, options, false)
      .then(function(fileEntry) {
        console.log("--- success");
        console.log(fileEntry);
        fileEntry.file(function(f) {
          attachment.url = f.localURL;
          d.resolve();
        });
        // Success!
      }, function(err) {
        // Error
        console.log("--- error");
        console.error(err);
        d.reject();
      }, function (progress) {
        console.log("--- progress: " +((progress.loaded / progress.total) * 100));
      });
    }catch(e) {
      console.error(e);
        d.reject();
    }


      });
*/      
    }
    else {
      d.resolve();
    }
    return d.promise;
  }

  function _replaceIds(target, tableConversions) {
    angular.forEach(tableConversions, function(conversionDef, key) {
      var values = target[key];
      if (values != null) {
        if (angular.isArray(values)) {
          angular.forEach(values, function(value, index) {
            target[key][index] = value.id;
          });
        }
        else {
          target[key] = values.id;
        }
      }
    });
  }

  var db = (window.cordova ? window.sqlitePlugin : window).openDatabase("kola.db", '1', 'kola DB', 1024 * 1024 * 100);
  db.transaction(function(tx) {
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

  initSync();
  $rootScope.$watch("onlineState.isOnline", onOnlineStateChanged);

/*
  window.requestFileSystem  = window.requestFileSystem || window.webkitRequestFileSystem;
  navigator.webkitPersistentStorage.requestQuota(500*1024*1024, function(grantedBytes) {
    window.requestFileSystem(window.PERSISTENT, grantedBytes, function(fs) {
      self.fileSystem = fs;
      console.log(fs);
    });
  }, function(e) {
    console.log('Error', e);
  });
*/
  return {
    initSync:initSync,
    sync:sync,
    all:all,
    get:get,
    save:save,
    resolveIds:resolveIds,
    db:db,
    createAttachment:createAttachment,
    createTaskDocumentation:createTaskDocumentation,
    createReflectionAnswer:createReflectionAnswer,
    assetsDir:assetsDir
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
    
/*
$cordovaCamera.getPicture(options).then(
    function(imageURI) {
      window.resolveLocalFileSystemURL(imageURI, function(fileEntry) {
        var picData = fileEntry.nativeURL;
        console.log(picData);
        
        });
      $ionicLoading.show({template: 'Foto acquisita...', duration:500});
    },
    function(err){
      $ionicLoading.show({template: 'Errore di caricamento...', duration:500});
    });
*/
/*
            var attachment = { test:"hallo" };
            attachment.name = "Test";
            attachment.mimeType = "image/jpeg";
            var int8 = new Int8Array(1);
            int8[0] = 42;
            attachment.content = Array.prototype.slice.call(int8);
            var doc = JSON.stringify(attachment);
            console.log(doc);
*/

    return $cordovaCamera.getPicture(options).then(function(imageUrl) {
      var d = $q.defer();
      window.resolveLocalFileSystemURL(imageUrl, function(fileEntry) {
        console.log(fileEntry);
        fileEntry.file(function(file) {
          console.log(file);
          var attachment = dbService.createAttachment(doc);
          attachment.name = file.name;
          attachment.mimeType = file.type;
          attachment._localURL = file.localURL;
          console.log("--- new attachment", attachment);
          console.log("--- doc after attachment creation -> ", doc);
          if (!$rootScope.$$phase) {
            $rootScope.$apply();
          }
          d.resolve(attachment);
        });
      });
      return d.promise;
    });

//    return dbService.save(doc, tableName);


/*
    $cordovaCapture.captureImage().then(function(mediaFiles) {
      if (mediaFiles.length === 1) {
        var name = mediaFiles[0].name;
        var type = mediaFiles[0].type;
        var url = mediaFiles[0].fullPath;
        console.log("--- TYPE=" + type + ", NAME=" + name + ", URL=" + url);
        $cordovaFile.readAsArrayBuffer(url).then(function (buffer) {
          console.log("--- SUCCESS -> " + buffer);
          // success
          
        }, function (error) {
          // error
          console.log(error);
        });
      }
    }, function(err) {
      // An error occurred. Show a message to the user
    });
*/
/*
      navigator.camera.getPicture(function(result) {
        console.log(result);
      }, function(err) {
        
      }, {});
*/
  }

  this.attachVideo = function(doc) {
    $cordovaCapture.captureVideo().then(function(mediaFiles) {
      if (mediaFiles.length === 1) {
        var file = mediaFiles[0];
        console.log("--- captured video file -> ", file);
        var attachment = dbService.createAttachment(doc);
        attachment.name = file.name;
        attachment.mimeType = file.type;
        attachment._localURL = file.fullPath;
//        attachment._localURL = file.localURL;
        console.log("--- new attachment", attachment);
        console.log("--- doc after attachment creation -> ", doc);
        if (!$rootScope.$$phase) {
          $rootScope.$apply();
        }
      }
    }, function(err) {
      // An error occurred. Show a message to the user
    });
  }

  _createAttachment = function(doc, fileEntry) {
    return _moveMediaFile(fileEntry).then(function(assetId, assetFile) {
      console.log("--- move result asset file -> ", assetFile);
      var attachment = dbService.createAttachment(doc, assetId);
      attachment.name = fileEntry.name;
      attachment.mimeType = fileEntry.type;
      attachment.url = assetFile.localURL;
      console.log("--- new attachment", attachment);
      console.log("--- doc after attachment creation -> ", doc);
      if (!$rootScope.$$phase) {
        $rootScope.$apply();
      }
      return attachment;
    });
  }

  _moveMediaFile = function(fileEntry) {
    var d = $q.defer();
    // copy attachment data from temp dir to assets dir
    var assetId = rfc4122.v4();
    console.log(fileEntry);
    console.log("--- moving attachment from " + (fileEntry.filesystem.root.nativeURL + fileEntry.name) + " to " + (dbService.assetsDir + assetId));
/*    
    $cordovaFile.moveFile(fileEntry.filesystem.root.nativeURL, fileEntry.name, dbService.assetsDir, assetId).then(function (success) {
      console.log("MOOOVED!");
      d.resolve(assetId, success);
    }, function (error) {
      // error
      console.log("ERROR MOOOVING!");
      console.log(error);
      d.reject();
    });
*/
    window.resolveLocalFileSystemURL(dbService.assetsDir, function(targetDir) {
      fileEntry.moveTo(targetDir, assetId, function(success) {
        console.log("MOOOVED!");
        d.resolve(assetId, success);
      }, function (error) {
        // error
        console.log("ERROR MOOOVING!");
        console.log(error);
        d.reject();
      });
    }, function(error) {
      console.log(error);
      d.reject(error);
    });
    return d.promise;
  }
})

/*
.service('Tasks', function(rfc4122, ngPouch) {
  ngPouch.saveSettings({ database: "http://130.83.139.161:5984/tasks", username: undefined, password: undefined, stayConnected: true });

  return {
    destroy: function(obj) {
      ngPouch.db.remove(obj.doc);
    },

    update: function(obj) {
      ngPouch.db.put(obj.doc);
    },

    add: function(obj) {
      obj._id = 'task_'+rfc4122.v4();
      obj.type = 'task';
      obj.created_at = new Date();
      return ngPouch.db.put(obj);
    },

    get: function(id) {
      return ngPouch.db.get(id);
    },

    all: function() {
      var allTasks = function(doc) {
        if (doc.type === 'task') {
          emit(doc.due, doc._id);
        }
      }
      return ngPouch.db.query(allTasks, {descending: true, include_docs : true});
    }
  };
})

.service('Notes', function(rfc4122, ngPouch) {
  return {
    destroy: function(obj) {
      ngPouch.db.remove(obj.doc);
    },

    update: function(obj) {
      ngPouch.db.put(obj.doc);
    },

    add: function(obj, taskId) {
      obj._id = 'note_'+rfc4122.v4();
      obj.type = 'note';
      obj.taskId = taskId;
      obj.created_at = new Date();
      return ngPouch.db.put(obj);
    },

    get: function(id) {
      return ngPouch.db.get(id);
    },

    all: function(taskId) {
      var allNotes = function(doc) {
        if (doc.type === 'note' && doc.taskId === taskId) {
          emit(doc.created_at, doc._id);
        }
      }
      return ngPouch.db.query(allNotes, {include_docs : true});
    }
  };
});
*/