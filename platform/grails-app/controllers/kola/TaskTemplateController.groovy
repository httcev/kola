package kola



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured

@Transactional(readOnly = true)
@Secured(['IS_AUTHENTICATED_FULLY'])
class TaskTemplateController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond TaskTemplate.list(params), model:[taskTemplateInstanceCount: TaskTemplate.count()]
    }

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

        if (taskTemplateInstance.hasErrors()) {
            respond taskTemplateInstance.errors, view:'create'
            return
        }

        taskTemplateInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'taskTemplate.label', default: 'TaskTemplate'), taskTemplateInstance.id])
                redirect taskTemplateInstance
            }
            '*' { respond taskTemplateInstance, [status: CREATED] }
        }
    }

    def edit(TaskTemplate taskTemplateInstance) {
        respond taskTemplateInstance
    }

    @Transactional
    def update(TaskTemplate taskTemplateInstance) {
        if (taskTemplateInstance == null) {
            notFound()
            return
        }

        if (taskTemplateInstance.hasErrors()) {
            respond taskTemplateInstance.errors, view:'edit'
            return
        }

        taskTemplateInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'TaskTemplate.label', default: 'TaskTemplate'), taskTemplateInstance.id])
                redirect taskTemplateInstance
            }
            '*'{ respond taskTemplateInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(TaskTemplate taskTemplateInstance) {

        if (taskTemplateInstance == null) {
            notFound()
            return
        }

        taskTemplateInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'TaskTemplate.label', default: 'TaskTemplate'), taskTemplateInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'taskTemplate.label', default: 'TaskTemplate'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
