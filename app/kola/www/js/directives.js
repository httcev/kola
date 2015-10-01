angular.module('kola.directives', [])

.directive('learningResources', function() {
  return {
  	restrict: 'E',
    //replace: true,
	require: '^ngModel',
  	scope: {
		ngModel: '=',
		hideHeader: '@'
	},
    templateUrl: 'templates/directive-learning-resources.html',
    link: function($scope) {
    	$scope.openUrlNative = function(url) {
		    if (ionic.Platform.isWebView()) {
		      navigator.startApp.start([["action", "VIEW"], [url]], function(message) {
		        console.log(message);
		      }, 
		      function(error) {
		          console.log(error);
		      });
		    }
		    else {
		      window.open(url);
		    }
    	}
    }
  };
})

.directive('mediaAttachments', function($q, $ionicModal, $timeout, dbService) {
  return {
  	restrict: 'E',
    //replace: true,
	require: '^ngModel',
  	scope: {
		ngModel: '=',
		hideHeader: '@'
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
		      navigator.startApp.start([["action", "VIEW"], [url]], function(message) {
		        console.log(message);
		      }, 
		      function(error) {
		          console.log(error);
		      });
		    }
		    else {
		      window.open(url);
		    }
    	};
    }
  };
})

.directive('syncControl', function($state, dbService) {
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
    	}
    }
  };
});

