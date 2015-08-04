package kola



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize

@Transactional(readOnly = true)
@Secured(['IS_AUTHENTICATED_FULLY'])
class TaskController {

    def springSecurityService
    def authService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Task.list(params), model:[taskInstanceCount: Task.count()]
    }

    def show(Task taskInstance) {
        respond taskInstance
    }

    def create() {
        params.isTemplate = false
        def task = new Task(params)
        if (task.template) {
            // copy relevant values from template into new task
            task.name = task.template.name
            task.description = task.template.description
            task.template = task.template
            task.steps = task.template.steps
            task.attachments = task.template.attachments
            task.resources = task.template.resources
        }
        respond task
    }

    def createFromTemplate(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        params.sort = params.sort ?: "lastUpdated"
        params.order = params.order ?: "desc"
        def query = Task.where { isTemplate == true }
        respond query.list(params), model:[taskInstanceCount: query.count()]
    }

    @Secured(['ROLE_ADMIN', 'ROLE_TASK_TEMPLATE_CREATOR'])
    def createTemplate() {
        params.isTemplate = true
        respond new Task(params), view:"create"
    }

    @Transactional
    def save(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }
        taskInstance.creator = springSecurityService.currentUser
        updateFromParams(taskInstance)
        taskInstance.validate()

        if (taskInstance.hasErrors()) {
            respond taskInstance.errors, view:'create'
            return
        }

        taskInstance.save flush:true

        flash.message = message(code: 'default.created.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
        redirect taskInstance
    }

    def edit(Task taskInstance) {
        if (!authService.canEdit(taskInstance)) {
            forbidden()
            return
        }
        respond taskInstance
    }

    @Transactional
    def update(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }

        if (!authService.canEdit(taskInstance)) {
            forbidden()
            return
        }

        updateFromParams(taskInstance)

        if (taskInstance.hasErrors()) {
            respond taskInstance.errors, view:'edit'
            return
        }

        taskInstance.save flush:true
        println "--- attachments after save -> " + taskInstance.attachments

        flash.message = message(code: 'default.updated.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
        redirect taskInstance
    }

    @Transactional
    def addStep(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }

        if (!authService.canEdit(taskInstance)) {
            forbidden()
            return
        }

        updateFromParams(taskInstance)

        redirect controller:"taskStep", action:"create"
    }

    @Transactional
    def delete(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }
        if (!authService.canDelete(taskInstance)) {
            forbidden()
            return
        }

        taskInstance.delete flush:true

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
        redirect action:"index", method:"GET"
    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'Task.label', default: 'Task'), params.id])
        redirect action: "index", method: "GET"
    }

    protected void forbidden() {
        flash.message = message(code: 'default.forbidden.message', args: [message(code: 'Task.label', default: 'Task'), params.id])
        redirect action: "index", method: "GET"
    }

    protected void updateFromParams(Task taskInstance) {
        taskInstance.resources?.clear()
        params.list("resources")?.unique(false).each {
            taskInstance.addToResources(Asset.get(it))
        }
        taskInstance.attachments?.clear()
        params.list("attachments")?.unique(false).each {
            taskInstance.addToAttachments(Asset.get(it))
        }
        def f = request.multiFileMap?.each { k,files ->
            files?.each { f ->
                if (!f.empty) {
                    def asset = new Asset(name:f.originalFilename, type:"attachment", mimeType:f.getContentType(), content:f.bytes)
                    if (!asset.save(true)) {
                        asset.errors.allErrors.each { println it }
                    }
                    taskInstance.addToAttachments(asset)
                }
            }
        }
    }
}
