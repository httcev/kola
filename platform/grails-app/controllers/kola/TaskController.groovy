package kola



import grails.converters.JSON
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
            eq("deleted", false)
        }
        def result = query.list(params)
        respond result, model:[taskInstanceCount: result.totalCount]
    }

    def show(Task taskInstance) {
        if (taskInstance == null) {
            notFound()
            return
        }
        def reflectionAnswers = [:]
        def taskDocumentations = [:]
        if (!taskInstance.isTemplate) {
            taskInstance.reflectionQuestions?.each { reflectionQuestion ->
                reflectionAnswers[reflectionQuestion.id] = ReflectionAnswer.findAllByTaskAndQuestionAndDeleted(taskInstance, reflectionQuestion, false)
            }
            
            def docs = TaskDocumentation.findAllByTaskAndDeleted(taskInstance, false)
            if (docs) {
                taskDocumentations[taskInstance.id] = docs
            }
            taskInstance.steps?.each { step ->
                docs = TaskDocumentation.findAllByStepAndDeleted(step, false)
                if (docs) {
                    taskDocumentations[step.id] = docs
                }
            }
        }

        ["taskInstance":taskInstance, "reflectionAnswers":reflectionAnswers, "taskDocumentations":taskDocumentations]
    }

    def create() {
        params.isTemplate = false
        def task = new Task(params)
        // auto link reflection questions
        task.reflectionQuestions = ReflectionQuestion.where { autoLink == true && deleted != true }.list()
        if (task.template) {
            // copy relevant values from template into new task
            task.name = task.template.name
            task.description = task.template.description
            task.template = task.template
            task.attachments = task.template.attachments
            task.resources = task.template.resources
            // join reflection questions from template
            task.reflectionQuestions.addAll(task.template.reflectionQuestions)
            task.reflectionQuestions.unique()

            task.template.steps?.each { step ->
                task.addToSteps(new TaskStep(name:step.name, description:step.description, attachments:step.attachments))
            }
        }
        respond task
    }

    def createFromTemplate(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        params.sort = params.sort ?: "lastUpdated"
        params.order = params.order ?: "desc"
        def query = Task.where { isTemplate == true && deleted == false }
        respond query.list(params), model:[taskInstanceCount: query.count()]
    }

    @Secured(['ROLE_ADMIN', 'ROLE_TASK_TEMPLATE_CREATOR'])
    def createTemplate() {
        params.isTemplate = true
        // auto link reflection questions
        params.reflectionQuestions = ReflectionQuestion.where { autoLink == true && deleted != true }.list()
        respond new Task(params), view:"create"
    }

    @Transactional
    def save(Task taskInstance) {
        // TODO: data binding is somehow broken because of the steps in the form- need to bind manually here.
        taskInstance.name = params.name
        taskInstance.description = params.description
        taskInstance.assignee = params.assignee?.id ? User.get(params.assignee.id) : null
        taskInstance.due = params.due ? new java.text.SimpleDateFormat("yyyy-MM-dd").parse(params.due) : null
        taskInstance.creator = springSecurityService.currentUser
        updateFromParams(taskInstance)
        taskInstance.validate()

        if (taskInstance.hasErrors()) {
            taskInstance.errors.allErrors.each { println it }
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

        flash.message = message(code: 'default.updated.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
        redirect action:"edit", id:taskInstance.id
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

        taskInstance.deleted = true
        taskInstance.save flush:true

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'Task.label', default: 'Task'), taskInstance.id])
        redirect action:"index", method:"GET"
    }

    def export(Task taskInstance) {
        def reflectionAnswers = [:]
        def taskDocumentations = [:]
        if (!taskInstance.isTemplate) {
            taskInstance.reflectionQuestions?.each { reflectionQuestion ->
                reflectionAnswers[reflectionQuestion.id] = ReflectionAnswer.findAllByTaskAndQuestionAndDeleted(taskInstance, reflectionQuestion, false)
            }
            
            def docs = TaskDocumentation.findAllByTaskAndDeleted(taskInstance, false)
            if (docs) {
                taskDocumentations[taskInstance.id] = docs
            }
            taskInstance.steps?.each { step ->
                docs = TaskDocumentation.findAllByStepAndDeleted(step, false)
                if (docs) {
                    taskDocumentations[step.id] = docs
                }
            }
        }
        renderPdf(template:"export", model:["taskInstance":taskInstance, "reflectionAnswers":reflectionAnswers, "taskDocumentations":taskDocumentations], filename:taskInstance.name + ".pdf")
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
        // update steps
        taskInstance.steps?.clear()
        // TODO: UGLY HACK!!
        for (i in 0..19) {
            def stepDef = params["steps[$i]"]
            if (stepDef && stepDef.deleted != "true") {
                println "--- $i=" + stepDef
                def step = stepDef.id ? TaskStep.get(stepDef.id) : new TaskStep()
                step.properties = stepDef
                taskInstance.addToSteps(step)
            }
        }
/*
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
*/
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
        request.multiFileMap?.each { k,files ->
            println "--- upload file with key " + k
            println files
            def domainName = k - "._newAttachment" - "_newAttachment"
            println "--- domainName=" + domainName
            def domain = taskInstance
            
            if (domainName.length() > 0) {
                def matcher = domainName =~ /(.*)\[(.*)\]/
                if (matcher.matches()) {
                    def prop = matcher[0][1]
                    def index = matcher[0][2] as Integer
                    //domain = taskInstance."${domainName}"
                    println "--- prop=${prop}"
                    println taskInstance.properties
                    println taskInstance."${prop}"
                    if (taskInstance."${prop}"?.size() > index) {
                        domain = taskInstance."${prop}"?.get(index)
                    }
                }
            }
            
            println "--- domain=" + domain
            files?.each { f ->
                if (!f.empty) {
                    def asset = new Asset(name:f.originalFilename, subType:"attachment", mimeType:f.getContentType(), content:f.bytes)
                    if (!asset.save(true)) {
                        asset.errors.allErrors.each { println it }
                    }
                    else {
                        domain?.addToAttachments(asset)
                    }
                }
            }
        }
    }
}
