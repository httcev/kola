angular.module('kola.notification', ['kola.services'])
.run(function($rootScope, $http, gcmSenderID, serverUrl, authenticationService) {
	var registrationId;
	var credentials = authenticationService.getCredentials();

	function onCredentialsChanged(creds) {
		credentials = creds;
		transmitRegistrationId();
	}

	function onOnlineStateChanged() {
		if ($rootScope.onlineState.isOnline) {
			var push = PushNotification.init({
			    android: {
			        senderID: gcmSenderID,
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
