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

.service('dbService', function ($q, $http, $cordovaSQLite, changesUrl, rfc4122) {
  var self = this;

  var conversions = {
    "task" : {
      "creatorId": { "refTable":"user", "replaceField":"creator" },
      "assigneeId": { "refTable":"user", "replaceField":"assignee" },
      "steps": { "refTable":"taskStep" },
      "attachments": { "refTable":"asset" },
      "resources": { "refTable":"asset" },
      "reflectionQuestions": { "refTable":"reflectionQuestion" }
    },
    "taskStep" : {
      "attachments": { "refTable":"asset" },
      "resources": { "refTable":"asset" },
      "reflectionQuestions": { "refTable":"reflectionQuestion" }
    },
    "taskDocumentation" : {
      "creatorId": { "refTable":"user", "replaceField":"creator" },
      "attachments": { "refTable":"asset" }
    }
  }

  var dbApiHolder = window;
  var db;
  if (window.cordova) {
    db = window.sqlitePlugin.openDatabase("kola.db", '1', 'kola DB', 1024 * 1024 * 100); // device
  }
  else {
    db = window.openDatabase("kola.db", '1', 'kola DB', 1024 * 1024 * 100); // browser
  }

  var TABLES_TO_SYNC = [
    {tableName : 'user'},
    {tableName : 'asset'},
    {tableName : 'reflectionQuestion'},
    {tableName : 'taskStep'},
    {tableName : 'taskDocumentation'},
    {tableName : 'task'}
  ];
  db.transaction(function(tx) {
    try {
/*
      tx.executeSql('CREATE TABLE IF NOT EXISTS user (id integer primary key, email varchar(255), company varchar(255), displayName varchar(255), mobile varchar(255), phone varchar(255), photo binary(512000))');
      tx.executeSql('CREATE TABLE IF NOT EXISTS asset (id varchar(255) not null primary key, content binary(104857600), description text, url text, mimeType varchar(255), name text, subType varchar(255))');
      tx.executeSql('CREATE TABLE IF NOT EXISTS reflectionQuestion (id integer primary key, name text)');
      tx.executeSql('CREATE TABLE IF NOT EXISTS task (id varchar(255) not null primary key, assigneeId varchar(255), creatorId varchar(255), dateCreated timestamp, description text, done boolean, due timestamp, isTemplate boolean, lastUpdated timestamp, name text, templateId varchar(255), FOREIGN KEY (assigneeId) REFERENCES user(assigneeId), FOREIGN KEY (creatorId) REFERENCES user(creatorId), FOREIGN KEY (templateId) REFERENCES task(templateId))');
      tx.executeSql('CREATE TABLE IF NOT EXISTS taskStep (id varchar(255) not null primary key, name text, description text, taskId varchar(255) not null, deleted boolean, FOREIGN KEY (taskId) REFERENCES task(taskId))');

      tx.executeSql('CREATE TABLE IF NOT EXISTS task_asset (task_attachments_id varchar(255), assetId varchar(255), attachments_idx integer, task_resources_id varchar(255), resources_idx integer, FOREIGN KEY (assetId) REFERENCES asset(assetId))');
      tx.executeSql('CREATE TABLE IF NOT EXISTS task_reflection_question (task_reflection_questions_id varchar(255), reflection_question_id bigint, reflection_questions_idx integer, FOREIGN KEY (reflection_question_id) REFERENCES reflectionQuestion(reflection_question_id))');
      tx.executeSql('CREATE TABLE IF NOT EXISTS task_step_asset (task_step_attachments_id bigint, asset_id varchar(255), attachments_idx integer, task_step_resources_id bigint, resources_idx integer, foreign key (asset_id) references asset(asset_id))');
*/
      tx.executeSql('CREATE TABLE IF NOT EXISTS user (id integer primary key, doc text not null)');
      tx.executeSql('CREATE TABLE IF NOT EXISTS asset (id varchar(255) not null primary key, doc text not null)');
      tx.executeSql('CREATE TABLE IF NOT EXISTS reflectionQuestion (id integer primary key, doc text not null)');
      tx.executeSql('CREATE TABLE IF NOT EXISTS task (id varchar(255) not null primary key, doc text not null)');
      tx.executeSql('CREATE TABLE IF NOT EXISTS taskStep (id varchar(255) not null primary key, doc text not null)');
      tx.executeSql('CREATE TABLE IF NOT EXISTS taskDocumentation (id varchar(255) not null primary key, doc text not null)');

      // local
      tx.executeSql('CREATE TABLE IF NOT EXISTS profile (id integer primary key, name text, password text)');
    }
    catch(e) {
      console.log(e);
    }
  });
  getProfile().then(function(profile) {
    DBSYNC.initSync(TABLES_TO_SYNC, db, {foo:"bar"}, changesUrl, function() {
      // INIT FINISHED
      DBSYNC.syncNow();
    }, profile.name, profile.password);
  });

  function getProfile() {
    var deferred = $q.defer();
    db.transaction(function(tx) {
      tx.executeSql('INSERT OR REPLACE INTO profile (id, name, password) values (1, "admin", "admin")');
    });
    db.transaction(function(tx) {
      tx.executeSql('SELECT * FROM profile where id=1', [], function (tx, results) {
        if (results.rows.length == 1) {
          deferred.resolve(results.rows.item(0));
        }
        else {
          deferred.reject();
        }
      });
    });
    return deferred.promise;
  }

  function setProfile(profile) {
    /*
    return getProfile().then(function(dbProfile) {
      angular.extend(dbProfile, profile);
      return localDatabase.put(dbProfile).catch(function (err) {
        console.log(err);
        throw err;
      });
    });
    */
  }

  function all(tableName) {
    var d = $q.defer();
    db.transaction(function (t) {
      t.executeSql("select doc from " + tableName, [], function (tx, results) {
        var docs = []
        for (var i=0; i<results.rows.length; i++) {
          docs.push(JSON.parse(results.rows.item(i).doc));
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
    var d = $q.defer();
    db.transaction(function (t) {
      t.executeSql("select doc from " + tableName + " where id=?", [id], function (tx, results) {
        if (results.rows.length == 1) {
          var doc = JSON.parse(results.rows.item(0).doc);
          var promises = []
          _resolveIds(doc, conversions[tableName], promises, tx);
          $q.all(promises).then(function () {
            d.resolve(doc);
          });
        }
        else {
          d.reject("object not found in DB: " + id);
        }
      }, function (err) {
        d.reject(err);
      });
    }, function (err) {
        d.reject(err);
    });

    return d.promise;
  }

  function save(doc, tableName) {
    if (!doc.id) {
      doc.id = rfc4122.v4();
    }
    console.log("--- saving", doc);
    var d = $q.defer();
    db.transaction(function (t) {
      t.executeSql("INSERT OR REPLACE INTO " + tableName + " (id, doc) values (?, ?)", [doc.id, JSON.stringify(doc)], function (tx, results) {
        d.resolve();
        DBSYNC.syncNow();
      }, function (err) {
        d.reject(err);
      });
    }, function (err) {
        d.reject(err);
    });

    return d.promise;
  }

  function resolveIds(target, tableName) {
    var d = $q.defer();
    db.transaction(function (tx) {
      var promises = []
      _resolveIds(target, conversions[tableName], promises, tx);
      $q.all(promises).then(function () {
        d.resolve(target);
      });
    }, function (err) {
        d.reject(err);
    });

    return d.promise;
  }

  function _resolveIds(target, tableConversions, promises, tx) {
    angular.forEach(tableConversions, function(conversionDef, key) {
      var ids = target[key];
      if (ids != null) {
        if (angular.isArray(ids)) {
          angular.forEach(ids, function(id, index) {
            var d = $q.defer();
            promises.push(d.promise);
//            console.log("will resolve id " + id + " from table " +conversionDef.refTable );
            tx.executeSql("select doc from " + conversionDef.refTable + " where id=?", [id], function (tx, results) {
              if (results.rows.length == 1) {
                var resolved = JSON.parse(results.rows.item(0).doc);
                target[key][index] = resolved;
                _resolveIds(resolved, conversions[conversionDef.refTable], promises, tx);
                d.resolve();
              }
              else {
                d.reject("no document with id '" + id + "' in table '" + conversionDef.refTable + "'");
              }
            }, function (err) {
              d.reject(err);
            });
          });
        }
        else {
          var d = $q.defer();
          promises.push(d.promise);
//          console.log("will resolve id " + ids + " from table " +conversionDef.refTable );
          tx.executeSql("select doc from " + conversionDef.refTable + " where id=?", [ids], function (tx, results) {
            if (results.rows.length == 1) {
              var resolved = JSON.parse(results.rows.item(0).doc);
              if (conversionDef.replaceField) {
                delete target[key];
                target[conversionDef.replaceField] = resolved;
              }
              else {
                target[key] = resolved;
              }
              _resolveIds(resolved, conversions[conversionDef.refTable], promises, tx);
              d.resolve();
            }
            else {
              d.reject("no document with id '" + ids + "' in table '" + conversionDef.refTable + "'");
            }
          }, function (err) {
            d.reject(err);
          });
        }
      }
    });
  }

  return {
    getProfile:getProfile,
    setProfile:setProfile,
    all:all,
    get:get,
    save:save,
    resolveIds:resolveIds
  }
})

.service('syncService', function ($rootScope, $interval, $interval, onlineStateService, dbService) {
  function onSyncInfoChanged(newValue, oldValue) {
    console.log("--- online state changed: isOnline="+$rootScope.onlineState.isOnline+", isWifi="+$rootScope.onlineState.isWifi);

    function callBackSyncProgress() {
    }
    var intervalPromise;

    if ($rootScope.onlineState.isOnline) {
/*      
      intervalPromise = $interval(function () {
        DBSYNC.syncNow(callBackSyncProgress, function(result) {
           if (result.syncOK === true) {
               //Synchronized successfully
           }
        });
      }, 5000);
*/      
    }
    else {
      $interval.cancel(intervalPromise);
      $rootScope.onlineState.isSyncing = false;
    }
  }
  $rootScope.$watch("onlineState.isOnline", onSyncInfoChanged);
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

.service('mediaAttachment', function ($cordovaCamera, $cordovaCapture, $cordovaFile, dbService, rfc4122) {
  this.attachPhoto = function(doc) {
    /*
    var options = {
      quality: 90,
      destinationType: Camera.DestinationType.DATA_URL,
      sourceType: Camera.PictureSourceType.CAMERA,
      allowEdit: false,
      encodingType: Camera.EncodingType.JPEG,
//      targetWidth: 100,
//      targetHeight: 100,
      popoverOptions: CameraPopoverOptions,
      saveToPhotoAlbum: false
    };

    return $cordovaCamera.getPicture(options).then(function(imageData) {
      if (doc._rev) {
        console.log("--- attaching image to existing document");
        return dbService.localDatabase.putAttachment(doc._id, rfc4122.v4(), doc._rev, imageData, 'image/jpg')
        .catch(function (err) {
          console.log(err);
          for (var property in err) {
            console.log(property + " -> " + err[property]);
          }
        }).finally(function() {
          console.log("--- 1");
          //bindTask($scope.task._id);
        });
      }
      else {
        console.log("--- attaching image to NEW document");
        doc._attachments = doc._attachments || {};
        var attachment = { "content_type":"image/jpeg", "data":imageData };
        doc._attachments[rfc4122.v4()] = attachment;
      }
    }, function(err) {
      console.log("--- Error:");
      console.log(err);
      for (var property in err) {
        console.log(property);
      }
    });
*/
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

  }

  this.attachVideo = function(doc) {
    $cordovaCapture.captureVideo().then(function(mediaFiles) {
      if (mediaFiles.length === 1) {
        var name = mediaFiles[0].name;
        var type = mediaFiles[0].type;
        var url = mediaFiles[0].localURL;
        console.log("--- TYPE=" + type + ", NAME=" + name + ", URL=" + url);
        $cordovaFile.readAsArrayBuffer(url).then(function (buffer) {
          console.log("--- SUCCESS -> " + buffer);
          // success
          dbService.localDatabase.post({
            title:"test3",
            "_attachments": {
              name: {
                "content_type": type,
                "data": buffer
              }
            }
          });
        }, function (error) {
          // error
          console.log(error);
        });
      }
    }, function(err) {
      // An error occurred. Show a message to the user
    });
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