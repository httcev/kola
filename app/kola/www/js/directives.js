angular.module('kola.directives', [])

.directive('learningResources', function() {
  return {
  	restrict: 'E',
    //replace: true,
	require: '^ngModel',
  	scope: {
		ngModel: '='
	},
    templateUrl: 'templates/directive-learning-resources.html',
    link: function($scope) {
    	$scope.openResource = function(resource) {
		    if (ionic.Platform.isWebView()) {
		      navigator.startApp.start([["action", "VIEW"], [resource.url]], function(message) {
		        console.log(message);
		      }, 
		      function(error) {
		          console.log(error);
		      });
		    }
		    else {
		      window.open(resource.url);
		    }
    	}
    }
  };
})

.directive('mediaAttachments', function($q, $ionicModal, dbService) {
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
            if (doc && doc._attachments) {
            	unbindWatch();
				$scope.$watchCollection("ngModel._attachments", updateAttachmentUrls);
            }
		}, true);


        function updateAttachmentUrls(newValue, oldValue) {
        	console.log(newValue, oldValue);
			var doc = ngModel.$modelValue;
            var attachmentUrls = false;
    		var promises = [];
            if (doc && doc._attachments) {
	            attachmentUrls = [];
				angular.forEach(doc._attachments, function(attachment, attachmentKey) {
					if (attachment.stub) {
						promises.push(dbService.localDatabase.getAttachment(doc._id, attachmentKey).then(function(blob) {
							attachmentUrls.push(URL.createObjectURL(blob));
						}));
					}
					else {
						attachmentUrls.push('data:'+attachment.content_type+';base64,'+attachment.data);
					}
				});
            }
			$q.all(promises).then(function() {
				console.log("--- SETTING URLS TO:", attachmentUrls);
				$scope.attachmentUrls = attachmentUrls;
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
    }
  };
});
