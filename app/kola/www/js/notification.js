angular.module('kola.notification', ['kola.services'])
.run(function($rootScope, $http, $ionicLoading, $sanitize, $state, gcmSenderID, serverUrl, authenticationService, dbService) {
	var registrationId;
	var credentials = authenticationService.getCredentials();

	function onCredentialsChanged(event, args) {
		credentials = args;
		transmitRegistrationId();
	}

	function onOnlineStateChanged() {
		if ($rootScope.onlineState.isOnline) {
			var push = PushNotification.init({
				android: {
					senderID: gcmSenderID,
					forceShow: true,
					icon: "notification",
					iconColor: "#a11d21",
					clearNotifications: false
				},
				ios: {
					alert: "true",
					badge: "true",
					sound: "true"
				},
				windows: {}
			});

			push.on('registration', function(data) {
				registrationId = data.registrationId;
				transmitRegistrationId();
			});

			push.on('notification', function(data) {
				// data.message,
				// data.title,
				// data.count,
				// data.sound,
				// data.image,
				// data.additionalData
				console.log("on notification: ", data);
				// only display toast if app is currently in foreground
				/*
				if (data.additionalData && data.additionalData.foreground) {
					$ionicLoading.show({
						template: "<h3>" + $sanitize(data.title) + "</h3>" + $sanitize(data.message),
						duration: 4000
					});
				}
				*/
				dbService.sync().then(function() {
					try {
						if (data.additionalData) {
							var collapseKey = data.additionalData.collapse_key;
							var referenceId = data.additionalData.referenceId;
							var referenceClass = data.additionalData.referenceClass;
							if (referenceId) {
								if ("new_comments" === collapseKey) {
									if ("TaskDocumentation" === referenceClass) {
										$state.go("task.documentation", {taskId:referenceId});
									}
									else {
										$state.go("question", {questionId:referenceId});
									}
								}
								if ("new_questions" === collapseKey || "new_answers" === collapseKey) {
									$state.go("question", {questionId:referenceId});
								}
								else if ("assigned_tasks" === collapseKey) {
									$state.go("task.detail", {taskId:referenceId});
								}
							}
						}
					}
					catch(e) {
						console.log(e);
					}
				});
				push.finish(function() {
				   console.log("processing of push data is finished");
			   });
			});

			push.on('error', function(e) {
				// e.message
				console.log("on error: ", e);
			});
		}
	}

	function transmitRegistrationId() {
		if ($rootScope.onlineState.isOnline && registrationId && credentials && credentials.user && credentials.password) {
			$http.post(serverUrl + "/api/pushToken", {
				"token": registrationId
			}, {
				headers: {
					"Authorization": "Basic " + btoa(credentials.user + ":" + credentials.password)
				}
			});
		}
	}

	// only use push notifications on native devices
	if (ionic.Platform.isWebView()) {
		$rootScope.$on("credentialsChanged", onCredentialsChanged);
		$rootScope.$watch("onlineState.isOnline", onOnlineStateChanged);
	}
});
