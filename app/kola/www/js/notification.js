angular.module('kola.notification', ['kola.services'])
.run(function($rootScope, $http, $q, $ionicLoading, $sanitize, $state, authenticationService, dbService) {
	var registrationId;
	var credentials = authenticationService.getCredentials();

	function onCredentialsChanged(event, args) {
		credentials = args;
		transmitRegistrationId();
	}

	function onOnlineStateChanged() {
		if ($rootScope.onlineState.isOnline) {
			requestSenderId().then(function(gcmSenderID) {
				if (gcmSenderID) {
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
										else if ("assigned_tasks" === collapseKey || "documented_tasks" === collapseKey) {
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
						});
					});

					push.on('error', function(e) {
						console.log("on error: ", e);
					});
				}
			});
		}
	}

	function transmitRegistrationId() {
		if ($rootScope.onlineState.isOnline && registrationId && credentials && credentials.server && credentials.user && credentials.password) {
			$http.post(credentials.server + "/api/pushToken", {
				"token": registrationId
			}, {
				headers: {
					"Authorization": "Basic " + btoa(credentials.user + ":" + credentials.password)
				}
			});
		}
	}

	function requestSenderId() {
		if ($rootScope.onlineState.isOnline && credentials && credentials.server && credentials.user && credentials.password) {
			return $http.get(credentials.server + "/api/pushToken").then(function(payload) {
				var senderId;
				if (payload && payload.data) {
					senderId = payload.data.senderId;
				}
				return senderId;
			}, function(err) {
				return $q.reject();
			});
		}
		else {
			return $q.reject();
		}
	}

	// only use push notifications on native devices
	if (ionic.Platform.isWebView()) {
		$rootScope.$on("credentialsChanged", onCredentialsChanged);
		$rootScope.$watch("onlineState.isOnline", onOnlineStateChanged);
	}
});
