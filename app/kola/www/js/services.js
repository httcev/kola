angular.module('kola.services', ['uuid'])
.service('dbService', function ($rootScope, $q, $state, $ionicPlatform, $ionicLoading, $cordovaFile, $cordovaFileTransfer, $window, serverUrl, authenticationService, rfc4122) {
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
    "question" : {
      "creator": { "refTable":"user" },
      "attachments": { "refTable":"asset" },
      "answers": { "refTable":"answer" },
      "comments": { "refTable":"comment" },
      "acceptedAnswer": { "refTable":"answer" }
    },
    "answer" : {
      "creator": { "refTable":"user" },
      "attachments": { "refTable":"asset" },
      "comments": { "refTable":"comment" }
    },
    "comment" : {
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
    {tableName : 'question'},
    {tableName : 'answer'},
    {tableName : 'comment'},
    {tableName : 'task'}
  ];
self.sync = dependOnInit(function() {
    var deferred = $q.defer();
    if (canSync()) {
        $rootScope.onlineState.isSyncing = true;
        DBSYNC.syncNow(onSyncProgress, function(result) {
            $rootScope.onlineState.isSyncing = false;
            if (result && result.status == 401) {
                openAccountTab("Login fehlgeschlagen. Bitte überprüfen Sie Nutzernamen und Passwort.");
                deferred.reject("sync failed");
            } else {
                $rootScope.$broadcast("syncFinished");
                if (result && result.syncOK === true) {
                    // synchronized successfully
                    deferred.resolve();
                }
                else {
                    deferred.reject("sync failed");
                }
            }
        });
    } else {
        deferred.reject("sync denied");
    }
    return deferred.promise;
});

self.all = dependOnInit(function(tableName) {
    var d = $q.defer();
    self.db.transaction(function(t) {
        t.executeSql("select doc from " + tableName, [], function(tx, results) {
            var docs = []
            for (var i = 0; i < results.rows.length; i++) {
                var doc = JSON.parse(results.rows.item(i).doc);
                doc._table = tableName;
                docs.push(doc);
            }
            d.resolve(docs);
        }, function(err) {
            d.reject(err);
        });
    }, function(err) {
        d.reject(err);
    });
    return d.promise;
});

self.save = dependOnInit(function(doc) {
    var d = $q.defer();
    self.db.transaction(function(tx) {
        var promises = [];
        if (angular.isArray(doc)) {
            angular.forEach(doc, function(o) {
                promises.push(_save(o, tx));
            });
        } else {
            promises.push(_save(doc, tx));
        }
        $q.all(promises).then(function() {
            d.resolve();
        }, function(err) {
            d.reject(err);
        })
    }, function(err) {
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
    self.db.transaction(function(tx) {
        tx.executeSql("select doc from " + tableName + " where id=?", [id], function(tx, results) {
            if (results.rows.length == 1) {
                var doc = JSON.parse(results.rows.item(0).doc);
                doc._table = tableName;
                if (tableName == "asset" && doc.typeLabel == "attachment") {
                    self._setLocalURL(doc).then(function() {
                        d.resolve(doc);
                    }, function(err) {
                        console.log(err);
                        d.reject();
                    });
                } else {
                    d.resolve(doc);
                }
            } else {
                d.reject("no document with id '" + id + "' in table '" + tableName + "'");
            }
        }, function(err) {
            console.log(err);
            d.reject(err);
        });
    }, function(err) {
        console.log(err);
        d.reject(err);
    });
    return d.promise;
});

self._setLocalURL = dependOnInit(function(attachment) {
    var d = $q.defer();
    if (window.cordova) {
        $cordovaFile.checkFile(self._assetsDirName, attachment.id).then(function(fileEntry) {
            // file exists.
            // for images (and TODO: videos) use cdvfile:// url to let the cordova webview load the attachments.
            // for other file types (e.g. PDFs), use fileEntry's nativeURL, so that Android can open it natively.
            if (attachment.mimeType.indexOf("image/") == 0) {
                fileEntry.file(function(f) {
                    attachment._localURL = f.localURL;
                    d.resolve();
                });
            } else {
                attachment._localURL = fileEntry.nativeURL;
                d.resolve();
            }
        }, function() {
            // file not found
            d.reject("asset file NOT found " + attachment.id);
        });
    } else {
        d.resolve();
    }
    return d.promise;
});

function _init() {
    var user = authenticationService.getCredentials().user;
    if (user) {
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
                tx.executeSql('CREATE TABLE IF NOT EXISTS question (id varchar(255) not null primary key, doc text not null)');
                tx.executeSql('CREATE TABLE IF NOT EXISTS answer (id varchar(255) not null primary key, doc text not null)');
                tx.executeSql('CREATE TABLE IF NOT EXISTS comment (id varchar(255) not null primary key, doc text not null)');
            } catch (err) {
                console.log(err);
            }
        });
        DBSYNC.initSync(TABLES_TO_SYNC, self.db, {
            foo: "bar"
        }, serverUrl + "/api/changes", serverUrl + "/api/upload", self._assetsDirName, function() {
            self.initDeferred.resolve();
            if (firstRun) {
                $rootScope.$watch("onlineState.isOnline", onOnlineStateChanged);
                firstRun = false;
            }
        }, $cordovaFileTransfer, $cordovaFile, $q, authenticationService);
    } else {
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
                openAccountTab("Willkommen bei KOLA! Bitte geben Sie Ihren Nutzernamen und Passwort ein.");
            } else {
                console.log(err);
            }
            return $q.reject("init failed");
        });
    }
}

function onCredentialsChanged() {
    self.initDeferred = $q.defer();
    _init();
    return self.initDeferred.promise;
}

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
    return angular.extend({
        id: rfc4122.v4(),
        _table: tableName,
        _isNew: true
    }, props);
}

function createTask() {
    var task = _create("task", {
        isTemplate: false,
        reflectionQuestions: []
    });
    // auto link standard reflection questions
    return self.all("reflectionQuestion").then(function(reflectionQuestions) {
        angular.forEach(reflectionQuestions, function(reflectionQuestion) {
            if (reflectionQuestion.autoLink) {
                task.reflectionQuestions.push(reflectionQuestion);
            }
        });
        return task;
    });
}

function createAttachment(parent) {
    var attachment = _create("asset", {
        typeLabel: "attachment"
    });
    parent.attachments = parent.attachments || [];
    parent.attachments.push(attachment);
    return attachment;
}

function createTaskDocumentation() {
    /*
      function createTaskDocumentation(targetId, isStep) {
        var options = {};
        if (isStep) {
          options.step = targetId;
        }
        else {
          options.task = targetId;
        }
        return _create("taskDocumentation", options);
    */
    return _create("taskDocumentation");
}

function createReflectionAnswer(taskId, questionId) {
    return _create("reflectionAnswer", {
        task: taskId,
        question: questionId
    });
}

function createQuestion(referenceId) {
    return _create("question", {
        reference: referenceId
    });
}

function createAnswer(question) {
    var answer = _create("answer", {
        question: question.id
    });
    question.answers = question.answers || [];
    question.answers.push(answer);
    return answer;
}

function createComment(target) {
    var comment = _create("comment");
    target.comments = target.comments || [];
    target.comments.push(comment);
    return comment;
}

function get(id, tableName, resolve) {
    return self._get(id, tableName).then(function(doc) {
        if (resolve !== false) {
            return _resolveIds(doc, CONVERSIONS[tableName]).then(function() {
                return doc;
            });
        } else {
            return doc;
        }
    });
}

function _save(doc, tx) {
    var d = $q.defer();
    if (!doc._table) {
        d.reject("Object has no _table property");
    } else {
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

        tx.executeSql("INSERT OR REPLACE INTO " + doc._table + " (id, doc) values (?, ?)", [copy.id, JSON.stringify(copy)], function(tx, results) {
            if (doc._table == "asset" && copy.typeLabel == "attachment" && localURL) {
                window.resolveLocalFileSystemURL(localURL, function(fileEntry) {
                    // move attachment data to assets dir if it is not already there
                    if (fileEntry.nativeURL != (self._assetsDirName + copy.id)) {
                        // check if the asset is in the app's cache folder. if yes, move the asset, if not copy it (to avoid removing files selected from the photo library).
                        if (fileEntry.nativeURL.indexOf(self._cacheDirName) == 0) {
                            fileEntry.moveTo(self._assetsDir, copy.id, function() {
                                d.resolve();
                            }, function(err) {
                                // error
                                d.reject(err);
                            });
                        } else {
                            fileEntry.copyTo(self._assetsDir, copy.id, function() {
                                d.resolve();
                            }, function(err) {
                                // error
                                d.reject(err);
                            });
                        }
                    } else {
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
            } else {
                d.resolve();
            }
        }, function(err) {
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
            } else {
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
            } else {
                if (values.id) {
                    target[key] = values.id;
                }
            }
        }
    });
}

function openAccountTab(message) {
    $ionicLoading.show({
        template: message,
        duration: 2000
    });
    $state.go("settings");
}

$rootScope.$on("credentialsChanged", onCredentialsChanged);
$ionicPlatform.ready(_init);


return {
    sync: self.sync,
    all: self.all,
    save: self.save,
    get: get,
    resolveIds: resolveIds,
    createTask: createTask,
    createAttachment: createAttachment,
    createTaskDocumentation: createTaskDocumentation,
    createReflectionAnswer: createReflectionAnswer,
    createQuestion: createQuestion,
    createAnswer: createAnswer,
    createComment: createComment
}
})

.service('authenticationService', function($ionicPlatform, $rootScope, $cordovaNetwork) {
    var credentials = loadCredentials();
    $rootScope.onlineState = {
        "isOnline": false,
        "isWifi": false,
        "isSyncing": false
    };

    function onOnlineStateChange(event, networkState) {
        $rootScope.onlineState.isOnline = networkState !== "none";
        $rootScope.onlineState.isWifi = networkState === "wifi";
    }

    function loadCredentials() {
        return {
            "user": localStorage["user"],
            "password": localStorage["password"]
        };
    }

    function getCredentials() {
        return credentials;
    }

    function updateCredentials(user, password) {
        localStorage["user"] = user;
        localStorage["password"] = password;
        credentials = loadCredentials();
        $rootScope.$broadcast("credentialsChanged", credentials);
    }

    function canEdit(doc) {
        return doc && (!doc.creator || doc.creator.username === credentials.user);
    }

    $ionicPlatform.ready(function() {
        // only use ngCordova on native devices
        if (ionic.Platform.isWebView()) {
            // listen for network connection events
            $rootScope.$on('$cordovaNetwork:online', onOnlineStateChange);
            $rootScope.$on('$cordovaNetwork:offline', onOnlineStateChange);
            onOnlineStateChange(null, $cordovaNetwork.getNetwork());
        } else {
            // on non-native devices, we assume to be online all the time
            onOnlineStateChange(null, "wifi");
        }
    });

    return {
        updateCredentials: updateCredentials,
        getCredentials: getCredentials,
        canEdit: canEdit
    }
})

.service('modalDialog', function($ionicModal) {
    this.createModalDialog = function(scope, templateUrl, focusFirstInput, openFunctionName, closeFunctionName) {
        openFunctionName = typeof openFunctionName !== "undefined" ? openFunctionName : "openModal";
        closeFunctionName = typeof closeFunctionName !== "undefined" ? closeFunctionName : "closeModal";
        focusFirstInput = typeof focusFirstInput !== "undefined" ? focusFirstInput : true;
        console.log("--- focusFirstInput=" + focusFirstInput);
        var modalId = Math.random().toString(36).slice(2);
        console.log("--- creating modal with id '" + modalId + "'");

        scope[openFunctionName] = function() {
            scope[modalId].show();
        }

        scope[closeFunctionName] = function() {
            scope[modalId].hide();
        };

        scope.$on("$destroy", function() {
            scope[modalId].remove();
            console.log("--- destroyed modal " + modalId);
        });

        return $ionicModal.fromTemplateUrl(templateUrl, {
            scope: scope,
            animation: "slide-in-up",
            focusFirstInput: focusFirstInput
        }).then(function(modal) {
            scope[modalId] = modal;
            return modal;
        });
    }
})

.service('mediaAttachment', function($cordovaCamera, $cordovaCapture, $ionicLoading, $rootScope, $cordovaFile, $q, dbService, rfc4122) {
    this.attachPhoto = function(doc) {
        var options = {
            quality: 90,
            //      destinationType: Camera.DestinationType.DATA_URL,
            destinationType: Camera.DestinationType.FILE_URI,
            sourceType: Camera.PictureSourceType.CAMERA,
            allowEdit: false,
            //encodingType: Camera.EncodingType.JPEG,
            popoverOptions: CameraPopoverOptions,
            saveToPhotoAlbum: false
        };

        if ((localStorage["scaleImages"] || "true") === "true") {
            options.targetWidth = 1280;
            options.targetHeight = 1280;
        }

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
                $ionicLoading.show({
                    template: err,
                    duration: 2000
                });
            }
        });
    };

    this.attachChosenMedia = function(doc) {
        var options = {
            destinationType: Camera.DestinationType.FILE_URI,
            sourceType: Camera.PictureSourceType.SAVEDPHOTOALBUM,
            allowEdit: false,
            MediaType: Camera.MediaType.ALLMEDIA
        };
        if ((localStorage["scaleImages"] || "true") === "true") {
            options.targetWidth = 1280;
            options.targetHeight = 1280;
        }
        return $cordovaCamera.getPicture(options).then(function(imageUrl) {
            var d = $q.defer();
            if (imageUrl.substring(0, 21) == "content://com.android") {
                photo_split = imageUrl.split("%3A");
                imageUrl = "content://media/external/images/media/" + photo_split[1];
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
                $ionicLoading.show({
                    template: err,
                    duration: 2000
                });
            });
            return d.promise;
        }, function(err) {
            // An error occurred. Show a message to the user, but only if not a normal "Canceled" event.
            console.log(err);
            if (typeof err == "string" && err.indexOf("cancel") < 0) {
                $ionicLoading.show({
                    template: err,
                    duration: 2000
                });
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
                $ionicLoading.show({
                    template: err,
                    duration: 2000
                });
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
            $ionicLoading.show({
                template: err,
                duration: 2000
            });
        });
    };
})
