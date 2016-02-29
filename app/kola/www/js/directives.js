angular.module('kola.directives', [])

.directive('learningResources', function() {
    return {
        restrict: 'E',
        //replace: true,
        require: '^ngModel',
        scope: {
            ngModel: '=',
            hideHeader: '@',
            editMode: '@'
        },
        templateUrl: 'templates/directive-learning-resources.html',
        link: function($scope) {
            $scope.openUrlNative = function(url) {
                if (ionic.Platform.isWebView()) {
                    navigator.startApp.start([
                            ["action", "VIEW"],
                            [url]
                        ], function(message) {
                            console.log(message);
                        },
                        function(error) {
                            console.log(error);
                        });
                } else {
                    window.open(url);
                }
            }
        }
    };
})

.directive('mediaAttachments', function($q, $ionicModal, $ionicLoading, $ionicPopup, $timeout) {
    return {
        restrict: 'E',
        //replace: true,
        require: '^ngModel',
        scope: {
            ngModel: '=',
            hideHeader: '@',
            editMode: '@'
        },
        templateUrl: 'templates/directive-media-attachments.html',
        link: function($scope, $element, $attrs, ngModel) {
            /*
		$scope.$watch("ngModel", function() {
			if (ngModel.$modelValue && ngModel.$modelValue._attachments) {
				$scope.$watchCollection("ngModel.$modelValue._attachments", updateAttachmentUrls);
			}
			updateAttachmentUrls();
		});
*/

            var unbindWatch = $scope.$watch("ngModel", function(newValue) {
                var doc = newValue;
                if (doc && doc.attachments) {
                    unbindWatch();
                    $scope.$watchCollection("ngModel.attachments", updateAttachmentUrls);
                }
            }, true);


            function updateAttachmentUrls(newValue, oldValue) {
                var doc = ngModel.$modelValue;
                var attachmentUrls = [];
                if (doc && doc.attachments) {
                    angular.forEach(doc.attachments, function(attachment, attachmentKey) {
                        if (attachment._localURL) {
                            attachmentUrls.push(attachment._localURL);
                        }
                        /*
                        					else if (attachment.content) {
                        						promises.push($timeout(function() {
                        							attachmentUrls.push(URL.createObjectURL(new Blob([new Uint8Array(attachment.content)], { type: attachment.mimeType } )));
                        						}));
                        					}
                        */
                        else {
                            attachmentUrls.push(attachment.url);
                        }
                    });
                }
                $scope.attachmentUrls = attachmentUrls;
            }

            $scope.removeAttachment = function(index) {
                var confirmPopup = $ionicPopup.confirm({
                    title: "<b>Anhang entfernen</b>",
                    template: "Soll der Anhang wirklich entfernt werden?"
                });
                confirmPopup.then(function(res) {
                    if (res) {
                        ngModel.$modelValue.attachments.splice(index, 1);
                    }
                });
            }

            $scope.showImage = function(index, imageUrls) {
                $scope.activeSlide = index;
                $scope.imageUrls = imageUrls;
                showImageModal('templates/image-popover.html');
            };

            function showImageModal(templateUrl) {
                $ionicModal.fromTemplateUrl(templateUrl, {
                    scope: $scope,
                    animation: 'slide-in-up'
                }).then(function(modal) {
                    $scope.modal = modal;
                    $scope.modal.show();
                });
            };

            // Close the modal
            $scope.closeImageModal = function() {
                $scope.modal.hide();
                $scope.modal.remove()
            };

            $scope.openUrlNative = function(url, mimeType) {
                if (ionic.Platform.isWebView()) {
                    console.log("opening " + url + ", type=" + mimeType);
                    navigator.startApp.start([
                            ["action", "VIEW"],
                            [url, mimeType]
                        ], function(message) {
                            console.log(message);
                        },
                        function(error) {
                            console.log(error);
                            $ionicLoading.show({
                                template: error,
                                duration: 4000
                            });
                        });
                } else {
                    window.open(url);
                }
            };
        }
    };
})

.directive('syncControl', function($state, $ionicPopup, $rootScope, dbService) {
    return {
        restrict: 'E',
        //replace: true,
        /*
        require: '^ngModel',
        scope: {
        	ngModel: '='
        },
        */
        templateUrl: 'templates/directive-sync-control.html',
        link: function($scope) {
            $scope.sync = function() {
                dbService.sync();
            };

            $scope.showOfflineInfo = function() {
                $ionicPopup.alert({
                    title: "Kein Netzzugang",
                    template: "Das Gerät hat momentan keinen Netzzugang. Alle Änderungen werden zunächst nur lokal auf dem Gerät gespeichert und synchronisiert, sobald eine Internetverbindung besteht"
                });
            };

            $rootScope.$watch("onlineState.isSyncing", function(state) {
                console.log("--- sync state changed", state);
                $scope.syncing = state;
            })
        }
    };
})

.directive('authorInfo', function($ionicModal, $ionicLoading) {
    return {
        restrict: 'E',
        //replace: true,
        require: '^ngModel',
        scope: {
            ngModel: '='
        },
        templateUrl: 'templates/directive-author-info.html',
        link: function($scope) {
            $scope.showAuthorDetailsModal = function(author) {
                $scope.author = author;
                $ionicModal.fromTemplateUrl("templates/author-info-popover.html", {
                    scope: $scope,
                    animation: 'slide-in-up'
                }).then(function(modal) {
                    $scope.modal = modal;
                    $scope.modal.show();
                });
            };

            // Close the modal
            $scope.closeAuthorDetailsModal = function() {
                $scope.modal.hide();
                $scope.modal.remove()
            };

            $scope.openUrlNative = function(url) {
                if (ionic.Platform.isWebView()) {
                    console.log("opening " + url);
                    navigator.startApp.start([
                            ["action", "VIEW"],
                            [url]
                        ], function(message) {
                            console.log(message);
                        },
                        function(error) {
                            console.log(error);
                            $ionicLoading.show({
                                template: error,
                                duration: 4000
                            });
                        });
                } else {
                    window.open(url);
                }
            };
        }
    };
});
