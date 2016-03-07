angular.module('kola.services', ['uuid'])
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
