package kola

import grails.transaction.Transactional

@Transactional
class TaskService {
	def pushNotificationService
	def springSecurityService
	def messageSource
	def lrsService
	def grailsLinkGenerator

	def save(Task task) {
		def assigneeDirty = task.assignee && (!task.attached || task.isDirty("assignee"))
		if (assigneeDirty && springSecurityService.currentUser != task.assignee) {
			// log to LRS
			def activity = new gov.adlnet.xapi.model.Activity()
			activity.setId(grailsLinkGenerator.link(absolute:true, controller:"task", action:"show", id:task.id))
			activity.setDefinition(new gov.adlnet.xapi.model.ActivityDefinition(["de-DE":task.name], [:]))
			lrsService.log(task.assignee, gov.adlnet.xapi.model.Verbs.initialized(), activity)

			sendAssignedNotification(task)
		}
		if (task.isDirty("done") && task.done && task.assignee) {
			// log to LRS
			def activity = new gov.adlnet.xapi.model.Activity()
			activity.setId(grailsLinkGenerator.link(absolute:true, controller:"task", action:"show", id:task.id))
			activity.setDefinition(new gov.adlnet.xapi.model.ActivityDefinition(["de-DE":task.name], [:]))
			lrsService.log(task.assignee, gov.adlnet.xapi.model.Verbs.completed(), activity)
		}
		return task.save()
	}

	def saveTaskDocumentation(TaskDocumentation documentation) {
		def isNew = !documentation.isAttached()
		def result = documentation.save()
		if (result && isNew) {
			def task = documentation.reference
			if (task instanceof TaskStep) {
				task = task.task
			}
			if (task.creator != documentation.creator) {
				sendDocumentedNotification(task)
			}

			// log to LRS
			def activity = new gov.adlnet.xapi.model.Activity()
			activity.setId(grailsLinkGenerator.link(absolute:true, controller:"task", action:"show", id:task.id))
			activity.setDefinition(new gov.adlnet.xapi.model.ActivityDefinition(["de-DE":documentation.text], [:]))
			lrsService.log(gov.adlnet.xapi.model.Verbs.shared(), activity)
		}
		return result
	}

	private void sendAssignedNotification(task) {
		def msg = [
			"data" : [
				"title" : messageSource.getMessage("kola.push.assigned.title", null, Locale.GERMAN),
				"body" : task.name,
				"category" : "assigned_tasks",
				"referenceId":task.id,
				"referenceClass":Task.class.simpleName
			]
		]
		pushNotificationService.sendPushNotification(task.assignee, msg)
	}

	private void sendDocumentedNotification(task) {
		def msg = [
			"data" : [
				"title" : messageSource.getMessage("kola.push.documented.title", null, Locale.GERMAN),
				"body" : task.name,
				"category" : "documented_tasks",
				"referenceId":task.id,
				"referenceClass":Task.class.simpleName
			]
		]
		pushNotificationService.sendPushNotification(task.creator, msg)
	}
}
