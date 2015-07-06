angular.module('kola.services', ['pouchdb', 'kola.pouchBinding', 'uuid'])

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

.service('dbService', function ($q, pouchDB, localDatabaseName, remoteDatabaseUrl) {
  var self = this;
  var localDatabase = pouchDB(localDatabaseName, { adapter:"websql" });
  localDatabase.info().then(function(info) { console.log(info)});

  var remoteDatabase = undefined;
/*
  function getWithAttachmentUrls(docId) {
    return localDatabase.get(docId).then(function(result) {
      return generateAttachmentUrls(result).then(function() {
        return result;
      });
    });
  }

  function generateAttachmentUrls(docs) {
    var promises = [];
    var _self = this;
    this._do = function(doc) {
      if (doc._attachments) {
        doc.attachmentUrls = [];
        angular.forEach(doc._attachments, function(attachment, attachmentKey) {
          promises.push(localDatabase.getAttachment(doc._id, attachmentKey).then(function(blob) {
            var url = URL.createObjectURL(blob);
            console.log(url);
            doc.attachmentUrls.push(url);
          }));
        });
      }
    }

    if (angular.isArray(docs)) {
      angular.forEach(docs, function(doc) {
        _self._do(doc);
      });
    }
    else {
      _self._do(docs);
    }

    return $q.all(promises);
  }
*/
  function getProfile() {
    var profileDocId = "_local/profile";
    return localDatabase.get(profileDocId).catch(function (err) {
      if (err.status === 404) { // not found!
        return { "_id":profileDocId };
      } else {
        console.log(err);
        throw err;
      }
    });
  }

  function setProfile(profile) {
    return getProfile().then(function(dbProfile) {
      angular.extend(dbProfile, profile);
      return localDatabase.put(dbProfile).catch(function (err) {
        console.log(err);
        throw err;
      });
    });
  }

  function getRemoteDatabase() {
    if (angular.isDefined(remoteDatabase)) {
      return $q.when(remoteDatabase);
    }
    else {
      return getProfile().then(function(profile) {
        remoteDatabase = pouchDB(remoteDatabaseUrl, { skipSetup:true, ajax:{ headers:{ "Authorization":"Basic " + window.btoa(profile.name + ':' + profile.password) }}});
        return remoteDatabase;
      });
    }
  }

  return {
    localDatabase:localDatabase,
    getRemoteDatabase:getRemoteDatabase,
    getProfile:getProfile,
    setProfile:setProfile
  }
})

.service('syncService', function ($rootScope, onlineStateService, dbService) {
  var replicationTo, replicationFrom;

  function onSyncInfoChanged(newValue, oldValue) {
    console.log("--- online state changed: isOnline="+$rootScope.onlineState.isOnline+", isWifi="+$rootScope.onlineState.isWifi);

    // first cancel possible previous replication processes and remote DB
    if (angular.isDefined(replicationTo)) {
      replicationTo.cancel();
      replicationTo = undefined;
    }
    if (angular.isDefined(replicationFrom)) {
      replicationFrom.cancel();
      replicationFrom = undefined;
    }

    if ($rootScope.onlineState.isOnline) {
      dbService.getRemoteDatabase().then(function(remoteDatabase) {
        console.log("-- huehue");
        console.log(remoteDatabase);
        replicationTo = dbService.localDatabase.replicate.to(remoteDatabase, { live:true, retry:false })
        .on('change', function (info) {
          // handle change
          console.log("--- outgoing sync changed");
          console.log(info);
        }).on('paused', function () {
          // replication paused (e.g. user went offline)
          console.log("--- outgoing sync paused");
          $rootScope.onlineState.isSyncing = false;
        }).on('active', function () {
          // replicate resumed (e.g. user went back online)
          console.log("--- outgoing sync active");
          $rootScope.onlineState.isSyncing = true;
        }).on('denied', function (info) {
          // a document failed to replicate, e.g. due to permissions
          console.log("--- outgoing sync denied");
          $rootScope.onlineState.isSyncing = false;
        }).on('complete', function (info) {
          // handle complete
          console.log("--- outgoing sync complete");
          $rootScope.onlineState.isSyncing = false;
        }).on('error', console.log.bind(console));

        replicationFrom = dbService.localDatabase.replicate.from(remoteDatabase, { live:true, retry:false, filter:"all/filterByUser" })
        .on('change', function (info) {
          // handle change
          console.log("--- incoming sync changed");
          console.log(info);
        }).on('paused', function () {
          // replication paused (e.g. user went offline)
          console.log("--- incoming sync paused");
          $rootScope.onlineState.isSyncing = false;
        }).on('active', function () {
          // replicate resumed (e.g. user went back online)
          console.log("--- incoming sync active");
          $rootScope.onlineState.isSyncing = true;
        }).on('denied', function (info) {
          // a document failed to replicate, e.g. due to permissions
          console.log("--- incoming sync denied");
          $rootScope.onlineState.isSyncing = false;
        }).on('complete', function (info) {
          // handle complete
          console.log("--- incoming sync complete");
          $rootScope.onlineState.isSyncing = false;
        }).on('error', console.log.bind(console));
      });
    }
    else {
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