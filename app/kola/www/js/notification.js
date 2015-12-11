angular.module('kola.notification', ['kola.services'])
.run(function($rootScope, $http, $ionicLoading, $sanitize, gcmSenderID, serverUrl, authenticationService, dbService) {
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
			        //forceShow: true,
			        icon: "notification",
		        	iconColor: "#a11d21"
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
			    console.log("on notification: " + data.message);
			    // only display toast if app is currently in foreground
			    if (data.additionalData && data.additionalData.foreground) {
				    $ionicLoading.show({template:"<h3>" + $sanitize(data.title) + "</h3>" + $sanitize(data.message), duration:4000});
				}
			    dbService.sync();
			});

			push.on('error', function(e) {
			    // e.message
			    console.log("on error: ", e);
			});
		}
	}

	function transmitRegistrationId() {
		if ($rootScope.onlineState.isOnline && registrationId && credentials && credentials.user && credentials.password) {
			$http.post(serverUrl + "/api/pushToken", { "token":registrationId }, { headers:{"Authorization":"Basic " + btoa(credentials.user + ":" + credentials.password)} });
		}
	}

    // only use push notifications on native devices
    if (ionic.Platform.isWebView()) {
		$rootScope.$on("credentialsChanged", onCredentialsChanged);
        $rootScope.$watch("onlineState.isOnline", onOnlineStateChanged);
    }
});
