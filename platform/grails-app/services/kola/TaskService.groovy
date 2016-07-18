package kola

import grails.transaction.Transactional

@Transactional
class TaskService {
    def pushNotificationService
    def springSecurityService
    def messageSource

    def save(Task task) {
        def assigneeDirty = task.assignee && (!task.attached || task.isDirty("assignee"))
        if (assigneeDirty && springSecurityService.currentUser != task.assignee) {
            sendAssignedNotification(task)
        }
        return task.save()
    }

    private void sendAssignedNotification(task) {
        def msg = [
                "title":messageSource.getMessage("kola.push.assigned.title", null, Locale.GERMAN),
                "message":task.name,
                "style":"inbox",
                "collapse_key":"assigned_tasks",
                "summaryText":messageSource.getMessage("kola.push.assigned.summaryText", null, Locale.GERMAN),
				"referenceId":task.id,
				"referenceClass":Task.class.simpleName
        ]
        pushNotificationService.sendPushNotification(task.assignee, msg)
    }
}
