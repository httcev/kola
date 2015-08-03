package kola



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN', 'ROLE_TASK_TEMPLATE_CREATOR'])
class TaskTemplateController {

    def springSecurityService
    def authService

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond TaskTemplate.list(params), model:[taskTemplateInstanceCount: TaskTemplate.count()]
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def show(TaskTemplate taskTemplateInstance) {
        respond taskTemplateInstance
    }

    def create() {
        respond new TaskTemplate(params)
    }

    @Transactional
    def save(TaskTemplate taskTemplateInstance) {
        if (taskTemplateInstance == null) {
            notFound()
            return
        }
        taskTemplateInstance.creator = springSecurityService.currentUser
        updateFromParams(taskTemplateInstance)
        taskTemplateInstance.validate()

        if (taskTemplateInstance.hasErrors()) {
            respond taskTemplateInstance.errors, view:'create'
            return
        }

        taskTemplateInstance.save flush:true

        flash.message = message(code: 'default.created.message', args: [message(code: 'taskTemplate.label', default: 'TaskTemplate'), taskTemplateInstance.id])
        redirect taskTemplateInstance
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def edit(TaskTemplate taskTemplateInstance) {
        if (!authService.canEdit(taskTemplateInstance)) {
            forbidden()
            return
        }
        respond taskTemplateInstance
    }

    @Transactional
    @Secured(['IS_AUTHENTICATED_FULLY'])
    def update(TaskTemplate taskTemplateInstance) {
        if (taskTemplateInstance == null) {
            notFound()
            return
        }

        if (!authService.canEdit(taskTemplateInstance)) {
            forbidden()
            return
        }

        updateFromParams(taskTemplateInstance)

        if (taskTemplateInstance.hasErrors()) {
            respond taskTemplateInstance.errors, view:'edit'
            return
        }

        taskTemplateInstance.save flush:true
        println "--- attachments after save -> " + taskTemplateInstance.attachments

        flash.message = message(code: 'default.updated.message', args: [message(code: 'TaskTemplate.label', default: 'TaskTemplate'), taskTemplateInstance.id])
        redirect taskTemplateInstance
    }

    @Transactional
    @Secured(['IS_AUTHENTICATED_FULLY'])
    def updateRelations(TaskTemplate taskTemplateInstance) {
        if (taskTemplateInstance == null) {
            notFound()
            return
        }

        if (!authService.canEdit(taskTemplateInstance)) {
            forbidden()
            return
        }

        updateFromParams(taskTemplateInstance)

        if (taskTemplateInstance.hasErrors()) {
            respond taskTemplateInstance.errors, view:'edit'
            return
        }

        taskTemplateInstance.save flush:true
  
//        flash.message = message(code: 'default.updated.message', args: [message(code: 'TaskTemplate.label', default: 'TaskTemplate'), taskTemplateInstance.id])
        redirect action:"edit", id:taskTemplateInstance.id
    }

    @Transactional
    @Secured(['IS_AUTHENTICATED_FULLY'])
    def addStep(TaskTemplate taskTemplateInstance) {
        if (taskTemplateInstance == null) {
            notFound()
            return
        }

        if (!authService.canEdit(taskTemplateInstance)) {
            forbidden()
            return
        }

        updateFromParams(taskTemplateInstance)

        redirect controller:"taskStep", action:"create"
    }

    @Transactional
    @Secured(['IS_AUTHENTICATED_FULLY'])
    def delete(TaskTemplate taskTemplateInstance) {
        if (taskTemplateInstance == null) {
            notFound()
            return
        }
        if (!authService.canDelete(taskTemplateInstance)) {
            forbidden()
            return
        }

        taskTemplateInstance.delete flush:true

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'TaskTemplate.label', default: 'TaskTemplate'), taskTemplateInstance.id])
        redirect action:"index", method:"GET"
    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'taskTemplate.label', default: 'TaskTemplate'), params.id])
        redirect action: "index", method: "GET"
    }

    protected void forbidden() {
        flash.message = message(code: 'default.forbidden.message', args: [message(code: 'taskTemplate.label', default: 'TaskTemplate'), params.id])
        redirect action: "index", method: "GET"
    }

    protected void updateFromParams(TaskTemplate taskTemplateInstance) {
        taskTemplateInstance.resources?.clear()
        params.list("resources")?.unique(false).each {
            taskTemplateInstance.addToResources(Asset.get(it))
        }
        taskTemplateInstance.attachments?.clear()
        params.list("attachments")?.unique(false).each {
            taskTemplateInstance.addToAttachments(Asset.get(it))
        }
        def f = request.multiFileMap?.each { k,files ->
            files?.each { f ->
                if (!f.empty) {
                    def asset = new Asset(name:f.originalFilename, type:"attachment", mimeType:f.getContentType(), content:f.bytes)
                    if (!asset.save(true)) {
                        asset.errors.allErrors.each { println it }
                    }
                    taskTemplateInstance.addToAttachments(asset)
                }
            }
        }
    }
}
