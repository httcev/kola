package kola

import grails.converters.JSON
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import de.httc.plugins.user.User

@Transactional(readOnly = true)
@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class TaskController {
    def springSecurityService
    def authService
    def taskService

    def index(Integer max) {
        params.offset = params.offset && !params.resetOffset ? (params.offset as int) : 0
        params.max = Math.min(max ?: 10, 100)
        params.sort = params.sort ?: "lastUpdated"
        params.order = params.order ?: "desc"
        
        def user = springSecurityService.currentUser
        def userCompany = user.profile?.company
        def filtered = params.own || params.assigned || params.ownCompany

        def results = Task.createCriteria().list(max:params.max, offset:params.offset) {
            // left join allows null values in the association
            createAlias('creator', 'c', org.hibernate.Criteria.LEFT_JOIN)
            createAlias('c.profile', 'cp', org.hibernate.Criteria.LEFT_JOIN)
            createAlias('assignee', 'a', org.hibernate.Criteria.LEFT_JOIN)
            createAlias('a.profile', 'ap', org.hibernate.Criteria.LEFT_JOIN)

            eq("deleted", false)
            eq("isTemplate", params.isTemplate?.toBoolean() ? true : false)
            if (filtered) {
                or {
                    if (params.own) {
                        eq("creator", user)
                    }
                    if (params.assigned) {
                        eq("assignee", user)
                    }
                    if (params.ownCompany) {
                        or {
                            eq("cp.company", userCompany)
                            eq("ap.company", userCompany)
                        }
                    }
                }
            }
            order(params.sort, params.order)
        }
        respond results, model:[taskCount:results.totalCount]
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
                reflectionAnswers[reflectionQuestion.id] = ReflectionAnswer.findAllByTaskAndQuestionAndDeleted(taskInstance, reflectionQuestion, false, [sort:'lastUpdated', order:'asc'])
            }
            
            def docs = TaskDocumentation.findAllByTaskAndDeleted(taskInstance, false, [sort:'lastUpdated', order:'asc'])
            if (docs) {
                taskDocumentations[taskInstance.id] = docs
            }
            taskInstance.steps?.each { step ->
                docs = TaskDocumentation.findAllByStepAndDeleted(step, false, [sort:'lastUpdated', order:'asc'])
                if (docs) {
                    taskDocumentations[step.id] = docs
                }
            }
        }

        respond taskInstance, model:["reflectionAnswers":reflectionAnswers, "taskDocumentations":taskDocumentations]
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
        respond query.list(params), model:[taskCount: query.count()]
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
        taskInstance.done = params.done ? params.done : false
        taskInstance.creator = springSecurityService.currentUser
        updateFromParams(taskInstance)
        taskInstance.validate()

        if (taskInstance.hasErrors()) {
            taskInstance.errors.allErrors.each { println it }
            respond taskInstance.errors, view:'create'
            return
        }

        taskService.save taskInstance

        flash.message = message(code: 'default.created.message', args: [message(code: taskInstance.isTemplate ? 'kola.taskTemplate.noshy' : 'kola.task', default: 'Task'), taskInstance.name])
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

        taskService.save taskInstance

        flash.message = message(code: 'default.updated.message', args: [message(code: taskInstance.isTemplate ? 'kola.taskTemplate.noshy' : 'kola.task', default: 'Task'), taskInstance.name])
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
        taskService.save taskInstance

        flash.message = message(code: 'default.deleted.message', args: [message(code: taskInstance.isTemplate ? 'kola.taskTemplate.noshy' : 'kola.task', default: 'Task'), taskInstance.name])
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
        def filename = taskInstance.name?.replaceAll("\\W+", "_");
        if (!filename) {
            filename = "export"
        }
        if (params.preview) {
            render(template:"export", model:["task":taskInstance, "reflectionAnswers":reflectionAnswers, "taskDocumentations":taskDocumentations])
        }
        else {
            renderPdf(template:"export", model:["task":taskInstance, "reflectionAnswers":reflectionAnswers, "taskDocumentations":taskDocumentations], filename:"${filename}.pdf")
        }
    }

    @Transactional
    def saveTaskDocumentation(TaskDocumentation taskDocumentationInstance) {
        if (taskDocumentationInstance == null || !taskDocumentationInstance.task) {
            throw new RuntimeException("no task for new documentation")
        }
        if (!authService.canAttach(taskDocumentationInstance.task)) {
            forbidden()
        }
        def attachments = []
        request.multiFileMap?.each { k,files ->
            if ("_newAttachment" == k) {
                files?.each { f ->
                    if (!f.empty) {
                        attachments.add(new Asset(name:f.originalFilename, subType:"attachment", mimeType:f.getContentType(), content:f.bytes))
                    }
                }
            }
        }

        if (taskDocumentationInstance.text || attachments.size() > 0) {
            taskDocumentationInstance.creator = springSecurityService.currentUser
            attachments.each {
                taskDocumentationInstance.addToAttachments(it)
            }
            taskDocumentationInstance.save flush:true
            if (taskDocumentationInstance.hasErrors()) {
                respond taskDocumentationInstance.errors, view:'show'
                return
            }
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'kola.task.documentation'), taskDocumentationInstance.id])
        redirect action:"show", method:"GET", id:taskDocumentationInstance.task.id
    }

    @Transactional
    def updateTaskDocumentation(TaskDocumentation taskDocumentationInstance) {
        if (taskDocumentationInstance == null || !(taskDocumentationInstance.task || taskDocumentationInstance.step)) {
            throw new RuntimeException("no task or step for update documentation")
        }
        if (!authService.canEdit(taskDocumentationInstance)) {
            forbidden()
        }

        taskDocumentationInstance.attachments?.clear()
        bindData(taskDocumentationInstance, params, [include: ['attachments']])
        // attach uploaded files
        request.multiFileMap?.each { k,files ->
            if ("_newAttachment" == k) {
                files?.each { f ->
                    if (!f.empty) {
                        def asset = new Asset(name:f.originalFilename, subType:"attachment", mimeType:f.getContentType(), content:f.bytes)
                        if (!asset.save(true)) {
                            asset.errors.allErrors.each { println it }
                        }
                        else {
                            taskDocumentationInstance.addToAttachments(asset)
                        }
                    }
                }
            }
        }


        def msg
        if (taskDocumentationInstance.text || taskDocumentationInstance.attachments?.size() > 0) {
            taskDocumentationInstance.save flush:true
            if (taskDocumentationInstance.hasErrors()) {
                respond taskDocumentationInstance.errors, view:'show'
                return
            }
            msg = message(code: 'default.updated.message', args: [message(code: 'kola.task.documentation'), taskDocumentationInstance.id])
        }
        else {
            taskDocumentationInstance.text = "DELETED"
            taskDocumentationInstance.deleted =true
            taskDocumentationInstance.save flush:true
            msg = message(code: 'default.deleted.message', args: [message(code: 'kola.task.documentation'), taskDocumentationInstance.id])
        }

        flash.message = msg
        def taskId = taskDocumentationInstance.task ? taskDocumentationInstance.task.id : params.parentTask
        redirect action:"show", method:"GET", id:taskId
    }

    @Transactional
    def saveReflectionAnswer(ReflectionAnswer reflectionAnswerInstance) {
        if (reflectionAnswerInstance == null || !reflectionAnswerInstance.task || !reflectionAnswerInstance.question) {
            throw new RuntimeException("no task or question for new answer")
        }
        if (!authService.canAttach(reflectionAnswerInstance.task)) {
            forbidden()
        }
        if (reflectionAnswerInstance.text) {
            reflectionAnswerInstance.creator = springSecurityService.currentUser

            reflectionAnswerInstance.save flush:true
            if (reflectionAnswerInstance.hasErrors()) {
                respond reflectionAnswerInstance.errors, view:'show'
                return
            }
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'kola.reflectionAnswer'), reflectionAnswerInstance.id])
        redirect action:"show", method:"GET", id:reflectionAnswerInstance.task.id
    }

    @Transactional
    def updateReflectionAnswer(ReflectionAnswer reflectionAnswerInstance) {
        if (reflectionAnswerInstance == null || !reflectionAnswerInstance.task || !reflectionAnswerInstance.question) {
            throw new RuntimeException("no task or question for update answer")
        }
        if (!authService.canEdit(reflectionAnswerInstance)) {
            forbidden()
        }

        def msg
        if (reflectionAnswerInstance.text) {
            reflectionAnswerInstance.save flush:true
            if (reflectionAnswerInstance.hasErrors()) {
                reflectionAnswerInstance.errors.each {
                    println it
                }
            }
            msg = message(code: 'default.updated.message', args: [message(code: 'kola.reflectionAnswer'), reflectionAnswerInstance.id])
        }
        else {
            reflectionAnswerInstance.text = "DELETED"
            reflectionAnswerInstance.deleted = true
            reflectionAnswerInstance.save flush:true
            if (reflectionAnswerInstance.hasErrors()) {
                reflectionAnswerInstance.errors.each {
                    println it
                }
            }
            msg = message(code: 'default.deleted.message', args: [message(code: 'kola.reflectionAnswer'), reflectionAnswerInstance.id])
        }

        flash.message = msg
        redirect action:"show", method:"GET", id:reflectionAnswerInstance.task.id
    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code:'kola.task', default: 'Task'), params.id])
        redirect action: "index", method: "GET"
    }

    protected void forbidden() {
        flash.message = message(code: 'default.forbidden.message', args: [message(code:'kola.task', default: 'Task'), params.id])
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
                def step = stepDef.id ? TaskStep.get(stepDef.id) : null
                if (!step) {
                    step = new TaskStep()
                }
                step.properties = stepDef
                taskInstance.addToSteps(step)
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
            def domainName = k - "._newAttachment" - "_newAttachment"
            def domain = taskInstance
            
            if (domainName.length() > 0) {
                def matcher = domainName =~ /(.*)\[(.*)\]/
                if (matcher.matches()) {
                    def prop = matcher[0][1]
                    def index = matcher[0][2] as Integer
                    //domain = taskInstance."${domainName}"
                    if (taskInstance."${prop}"?.size() > index) {
                        domain = taskInstance."${prop}"?.get(index)
                    }
                }
            }
            
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
