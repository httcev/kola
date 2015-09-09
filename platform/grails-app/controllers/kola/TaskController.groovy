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
        def filtered = params.own || params.assigned
        def query = new grails.gorm.DetachedCriteria(Task).build {
            if (filtered) {
                or {
                    if (params.own) {
                        eq("creator", springSecurityService.currentUser)
                    }
                    if (params.assigned) {
                        eq("assignee", springSecurityService.currentUser)
                    }
                }
            }
            eq("isTemplate", params.isTemplate?.toBoolean() ? true : false)
        }
        def result = query.list(params)
        respond result, model:[taskInstanceCount: result.totalCount]
    }

    def show(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }
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
            task.template.steps?.each {
                task.addToSteps(new TaskStep(name:it.name, description:it.description, attachments:it.attachments))
            }
            task.attachments = task.template.attachments
            task.resources = task.template.resources
            task.reflectionQuestions = task.template.reflectionQuestions
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
        println params
        println taskInstance.name
        if (taskInstance == null) {
            notFound()
            return
        }
        taskInstance.creator = springSecurityService.currentUser
        updateFromParams(taskInstance)
        taskInstance.validate()

        taskInstance.errors.allErrors.each { println it }

        if (taskInstance.hasErrors()) {
            respond taskInstance.errors, view:'create'
            return
        }

        taskInstance.save flush:true

        flash.message = message(code: 'default.created.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
        redirect action:"edit", id:taskInstance.id
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
        taskInstance.validate()

        if (taskInstance.hasErrors()) {
            respond taskInstance.errors, view:'edit'
            return
        }

        taskInstance.save flush:true
        println "--- steps after save -> " + taskInstance.steps*.name

        flash.message = message(code: 'default.updated.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
        redirect action:"edit", id:taskInstance.id
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
        // update resources
        taskInstance.resources?.clear()
        params.list("resources")?.unique(false).each {
            def asset = Asset.get(it)
            if (asset) {
                taskInstance.addToResources(asset)
            }
            else {
                log.error "Couldn't add resource: Asset not found: ${it}"
            }
        }
        // update attachments
        taskInstance.attachments?.clear()
        params.list("attachments")?.unique(false).each {
            def asset = Asset.get(it)
            if (asset) {
                taskInstance.addToAttachments(asset)
            }
            else {
                log.error "Couldn't add attachment: Asset not found: ${it}"
            }
        }
        taskInstance.steps?.eachWithIndex { step, index ->
            step?.attachments?.clear()
            params.list("steps[$index].attachments")?.unique(false).each {
                def asset = Asset.get(it)
                if (asset) {
                    step.addToAttachments(asset)
                }
                else {
                    log.error "Couldn't add attachment: Asset not found: ${it}"
                }
            }
        }  
        // update steps
        taskInstance.steps?.removeAll{ it == null || it.deleted }
        // update reflection questions
        taskInstance.reflectionQuestions?.clear()
        params.list("reflectionQuestions")?.unique(false).each {
            def reflectionQuestion = ReflectionQuestion.get(it)
            if (reflectionQuestion) {
                taskInstance.addToReflectionQuestions(reflectionQuestion)
            }
            else {
                log.error "Couldn't add reflection question: ReflectionQuestion not found: ${it}"
            }
        }
        // create new attachments
        def f = request.multiFileMap?.each { k,files ->
            println "--- upload file with key " + k
            def domainName = k - "._newAttachment" - "_newAttachment"
            println "--- domainName=" + domainName
            def domain = taskInstance
            
            if (domainName.length() > 0) {
                def matcher = domainName =~ /(.*)\[(.*)\]/
                if (matcher.matches()) {
                    def prop = matcher[0][1]
                    def index = matcher[0][2] as Integer
                    //domain = taskInstance."${domainName}"
                    domain = taskInstance."${prop}"?.get(index)
                }
            }
            
            println "--- domain=" + domain
            files?.each { f ->
                if (!f.empty) {
                    def asset = new Asset(name:f.originalFilename, subType:"attachment", mimeType:f.getContentType(), content:f.bytes)
                    if (!asset.save(true)) {
                        asset.errors.allErrors.each { println it }
                    }
                    domain?.addToAttachments(asset)
                }
            }
        }
    }
}
