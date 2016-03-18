angular.module('kola.directives', [])

.directive('learningResources', function() {
	return {
		restrict: 'E',
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
		require: '^ngModel',
		scope: {
			ngModel: '=',
			hideHeader: '@',
			editMode: '@'
		},
		templateUrl: 'templates/directive-media-attachments.html',
		link: function($scope, $element, $attrs, ngModel) {
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
						} else {
							attachmentUrls.push(attachment.url);
						}
					});
				}
				$scope.attachmentUrls = attachmentUrls;
			}

			$scope.removeAttachment = function(index) {
				var confirmPopup = $ionicPopup.confirm({
					title: "<b>Anhang entfernen</b>",
					template: "Soll der Anhang wirklich entfernt werden?",
					cancelText: "Abbrechen"
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
		templateUrl: 'templates/directive-sync-control.html',
		link: function($scope) {
			$scope.click = function() {
				if (!$scope.syncing) {
					if ($scope.online) {
						dbService.sync();
					} else {
						$ionicPopup.alert({
							title: "<b>Kein Netzzugang</b>",
							template: "Das Gerät hat momentan keinen Netzzugang. <span class='assertive bold'>Alle Änderungen werden zunächst nur lokal auf dem Gerät gespeichert</span> und synchronisiert, sobald eine Internetverbindung besteht"
						});
					}
				}
			};
			$rootScope.$watch("onlineState.isSyncing", function(state) {
				$scope.syncing = state;
			});
			$rootScope.$watch("onlineState.isOnline", function(state) {
				$scope.online = state;
			});
		}
	};
})

.directive('authorInfo', function($ionicModal, $ionicLoading, $ionicPopup) {
	return {
		restrict: 'E',
		require: '^ngModel',
		scope: {
			ngModel: '=',
			hideImage: '@',
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

			$scope.showUnsavedWarning = function() {
				$ionicPopup.alert({
					title: "<b>Noch nicht synchronisiert</b>",
					template: "Dieser Eintrag wurde zunächst nur lokal auf dem Gerät gespeichert. Er wird synchronisiert, sobald eine Internetverbindung besteht."
				});
			}
		}
	};
})

.directive('commentsList', function(dbService) {
	return {
		restrict: 'E',
		require: 'ngModel',
		scope: {
			ngModel: '='
		},
		templateUrl: 'templates/directive-comments-list.html',
		link: function($scope) {
			$scope.createComment = function() {
				$scope.newComment = {
					text: ""
				};
			}

			$scope.saveComment = function() {
				if ($scope.newComment && $scope.newComment.text.length > 0) {
					var comment = dbService.createComment($scope.ngModel);
					comment.text = $scope.newComment.text;
					$scope.saving = true;
					$scope.ngModel._comments.push(comment);
					dbService.save(comment).then(function() {
						$scope.newComment = null;
						$scope.saving = false;
					});
				} else {
					// close edit mode when user didn't enter comment text
					$scope.newComment = null;
				}
			}
		}
	};
})

.directive('ratingControl', function(dbService) {
	return {
		restrict: 'E',
		require: '^ngModel',
		scope: {
			ngModel: '='
		},
		templateUrl: 'templates/directive-rating-control.html',
		link: function($scope) {
			$scope.click = function() {
				if ($scope.ngModel.rated) {
					$scope.ngModel.rated = false;
					$scope.ngModel.rating--;
				} else {
					$scope.ngModel.rated = true;
					$scope.ngModel.rating = ($scope.ngModel.rating || 0) + 1;
				}
				dbService.save($scope.ngModel);
			}
		}
	};
});
