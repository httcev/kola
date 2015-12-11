package kola

import groovy.json.JsonBuilder

class PushNotificationService {
	def grailsApplication
	def messageSource

	def sendPushNotification(User target, message) {
		// decouple from current thead
		PushToken.async.task {
			try {
				def pushToken = PushToken.get(target.id)
				if (!pushToken) {
					log.warn "No push notification token registered for user ${target.username}. Not sending push notification."
					return
				}
				log.info "Sending push notification to user ${target.username}: " + message
				pushToken.properties?.each {
					log.info it
				}
				URL url = new URL(grailsApplication.config.kola.pushNotification.gcmUrl);
			    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			    conn.setRequestMethod("POST")
			    conn.setRequestProperty("Accept", "application/json");
			    conn.setRequestProperty("Authorization", "key=" + grailsApplication.config.kola.pushNotification.gcmApiKey);
			    conn.setRequestProperty("Content-Type", "application/json");
			    conn.setDoOutput(true);
			    conn.setDoInput(true);

			    def notification = ["to" : pushToken.token, "data" : message]
			    def builder = new JsonBuilder(notification)
			    def payload = builder.toString().bytes
			    conn.setFixedLengthStreamingMode(payload.length);
			    conn.getOutputStream().write(payload)

				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					log.debug(line);
			    }
			    reader.close();
			}
			catch(e) {
				log.error "Sending push notification failed", e
			}
		}
	}

	def sendAssignedNotification(Task task) {
        def message = [
        	"title":messageSource.getMessage("kola.push.assigned.title", null, Locale.GERMAN),
        	"message":task.name,
        	"style":"inbox",
        	"collapse_key":"assigned_tasks",
        	"summaryText":messageSource.getMessage("kola.push.assigned.summaryText", null, Locale.GERMAN)
    	]
        sendPushNotification(task.assignee, message)
    }
}