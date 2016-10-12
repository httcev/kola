package kola

import grails.converters.JSON
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import de.httc.plugins.user.User
import de.httc.plugins.user.Profile
import de.httc.plugins.qaa.Question
import de.httc.plugins.qaa.Comment
import de.httc.plugins.repository.Asset
import de.httc.plugins.repository.AssetContent
import de.httc.plugins.taxonomy.TaxonomyTerm
import de.httc.plugins.taxonomy.Taxonomy

@Transactional(readOnly = true)
@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class TaskController {
	def springSecurityService
	def authService
	def taskService
	def questionService

	def index(Integer max) {
		params.offset = params.offset && !params.resetOffset ? (params.offset as int) : 0
		params.max = Math.min(max ?: 10, 100)
		params.sort = params.sort ?: "lastUpdated"
		params.order = params.order ?: "desc"

		def user = springSecurityService.currentUser
		def userCompany = user.profile?.company
		def filtered = params.own || params.assigned || params.ownCompany || params.key

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
				if (params.key) {
					or {
						ilike("name", "%${params.key}%")
						ilike("description", "%${params.key}%")
					}
				}
			}
			and {
				order(params.sort, params.order)
				// add secondary sort key to keep sorting consistent on reload
				order("id", "asc")
			}
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
		def taskQuestions = [:]
		if (!taskInstance.isTemplate) {
			taskInstance.reflectionQuestions?.each { reflectionQuestion ->
				reflectionAnswers[reflectionQuestion.id] = ReflectionAnswer.findAllByTaskAndQuestionAndDeleted(taskInstance, reflectionQuestion, false, [sort:'lastUpdated', order:'asc'])
			}

			def documentations = TaskDocumentation.findAllByReferenceAndDeleted(taskInstance, false, [sort:'lastUpdated', order:'asc'])
			def questions = Question.findAllByReferenceAndDeleted(taskInstance, false, [sort:'lastUpdated', order:'asc'])
			if (documentations) {
				taskDocumentations[taskInstance.id] = documentations
			}
			if (questions) {
				taskQuestions[taskInstance.id] = questions
			}
			taskInstance.steps?.each { step ->
				documentations = TaskDocumentation.findAllByReferenceAndDeleted(step, false, [sort:'lastUpdated', order:'asc'])
				questions = Question.findAllByReferenceAndDeleted(step, false, [sort:'lastUpdated', order:'asc'])
				if (documentations) {
					taskDocumentations[step.id] = documentations
				}
				if (questions) {
					taskQuestions[step.id] = questions
				}
			}
		}
		def reflectionAnswersCount = 0
		reflectionAnswers.each { k, v ->
			reflectionAnswersCount += v.size()
		}
		def taskDocumentationsCount = 0
		taskDocumentations.each { k, v ->
			taskDocumentationsCount += v.size()
		}
		def taskQuestionsCount = 0
		taskQuestions.each { k, v ->
			taskQuestionsCount += v.size()
		}
		respond taskInstance, model:["reflectionAnswers":reflectionAnswers, "reflectionAnswersCount":reflectionAnswersCount, "taskDocumentations":taskDocumentations, "taskDocumentationsCount":taskDocumentationsCount, "taskQuestions":taskQuestions, "taskQuestionsCount":taskQuestionsCount]
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
			task.type = task.template.type
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
		// TODO: data binding is somehow broken because of the steps in the form -> need to bind manually here.
		taskInstance.name = params.name
		taskInstance.description = params.description
		taskInstance.assignee = null
		if (params.assigneeType == "person" && params.assignee?.id) {
			println "--- assigning to single person..."
			taskInstance.assignee = User.get(params.assignee.id)
		}
//		taskInstance.assignee = params.assignee?.id ? User.get(params.assignee.id) : null
		taskInstance.type = params.type?.id ? TaxonomyTerm.get(params.type.id) : null
		taskInstance.due = params.due ? new java.text.SimpleDateFormat("yyyy-MM-dd").parse(params.due) : null
		taskInstance.done = params.done ? params.done : false
		taskInstance.creator = springSecurityService.currentUser
		updateFromParams(taskInstance)

		if (!taskService.save(taskInstance)) {
			taskInstance.errors.allErrors.each { println it }
			respond taskInstance.errors, view:"create"
			return
		}

		if (params.assigneeType == "group" && params.assigneeGroup) {
			redirect action:"confirmAssign", id:taskInstance.id, params:[group:params.assigneeGroup]
		}
		else {
			flash.message = message(code: 'default.created.message', args: [message(code: taskInstance.isTemplate ? 'kola.taskTemplate.noshy' : 'kola.task', default: 'Task'), taskInstance.name])
			redirect action:"index"
		}
	}

	def edit(Task taskInstance) {
		if (taskInstance == null) {
			notFound()
			return
		}
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
		if (!taskService.save(taskInstance)) {
			respond taskInstance.errors, view:"edit"
			return
		}

		if (params.assigneeType == "group" && params.assigneeGroup) {
			redirect action:"confirmAssign", id:taskInstance.id, params:[group:params.assigneeGroup]
		}
		else {
			flash.message = message(code: 'default.updated.message', args: [message(code: taskInstance.isTemplate ? 'kola.taskTemplate.noshy' : 'kola.task', default: 'Task'), taskInstance.name])
			redirect action:"show", id:taskInstance.id
		}
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

	def confirmAssign(Task task) {
		def group = params.group ? TaxonomyTerm.get(params.group) : null
		def userProfiles = validateTaskAndGroupForAssignment(task, group)
		if (userProfiles) {
			respond task, model:[group:group, userProfiles:userProfiles]
		}
	}

	@Transactional
	def assign(Task task) {
		def group = params.group ? TaxonomyTerm.get(params.group) : null
		def userProfiles = validateTaskAndGroupForAssignment(task, group)
		if (userProfiles) {
			userProfiles.eachWithIndex { profile, index ->
				if (0 == index) {
					// first user gets the original task, all others get a copy
					task.assignee = profile.user
					if (!task.save()) {
						task.errors.allErrors.each { println it }
					}
				}
				else {
					def copy = new Task()
					copy.name = task.name
					copy.description = task.description
					copy.due = task.due
					copy.template = task.template
					copy.type = task.type
					copy.done = task.done
					copy.deleted = task.deleted
					copy.creator = task.creator
					task.steps?.each {
						copy.addToSteps(new TaskStep(name:it.name, description:it.description, attachments:it.attachments))
					}
					task.reflectionQuestions?.each {
						copy.addToReflectionQuestions(it)
					}
					task.resources?.each {
						copy.addToResources(it)
					}
					task.attachments?.each {
						copy.addToAttachments(it)
					}

					copy.assignee = profile.user
					if (!copy.save()) {
						copy.errors.allErrors.each { println it }
					}
				}
			}
			flash.message = message(code:"kola.task.assigned.to", args: [task.name, userProfiles.size()])
			redirect action:"index", method:"GET"
		}
	}

	def export(Task taskInstance) {
		def reflectionAnswers = [:]
		def taskDocumentations = [:]
		if (!taskInstance.isTemplate) {
			taskInstance.reflectionQuestions?.each { reflectionQuestion ->
				reflectionAnswers[reflectionQuestion.id] = ReflectionAnswer.findAllByTaskAndQuestionAndDeleted(taskInstance, reflectionQuestion, false)
			}

			def docs = TaskDocumentation.findAllByReferenceAndDeleted(taskInstance, false)
			if (docs) {
				taskDocumentations[taskInstance.id] = docs
			}
			taskInstance.steps?.each { step ->
				docs = TaskDocumentation.findAllByReferenceAndDeleted(step, false)
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
		if (taskDocumentationInstance == null || !taskDocumentationInstance.reference) {
			throw new RuntimeException("no task for new documentation")
		}
		if (!authService.canAttach(taskDocumentationInstance.reference)) {
			forbidden()
			return
		}
		def attachments = []
		request.multiFileMap?.each { k,files ->
			if ("_newAttachment" == k) {
				files?.each { f ->
					if (!f.empty) {
						attachments.add(new Asset(name:f.originalFilename, typeLabel:"attachment", mimeType:f.getContentType(), content:new AssetContent(data:f.bytes)))
					}
				}
			}
		}

		if (taskDocumentationInstance.text || attachments.size() > 0) {
			taskDocumentationInstance.creator = springSecurityService.currentUser
			attachments.each {
				taskDocumentationInstance.addToAttachments(it)
			}
			taskService.saveTaskDocumentation(taskDocumentationInstance)
			if (taskDocumentationInstance.hasErrors()) {
				respond taskDocumentationInstance.errors, view:'show'
				return
			}
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'kola.task.documentation'), taskDocumentationInstance.id])
		def task = taskDocumentationInstance.reference
		if (task instanceof TaskStep) {
			task = task.task
		}
		redirect action:"show", method:"GET", id:task.id, fragment:"documentations"
	}

	@Transactional
	def updateTaskDocumentation(TaskDocumentation taskDocumentationInstance) {
		if (taskDocumentationInstance == null || !taskDocumentationInstance.reference) {
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
						def asset = new Asset(name:f.originalFilename, typeLabel:"attachment", mimeType:f.getContentType(), content:new AssetContent(data:f.bytes))
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
			taskService.saveTaskDocumentation(taskDocumentationInstance)
			if (taskDocumentationInstance.hasErrors()) {
				respond taskDocumentationInstance.errors, view:'show'
				return
			}
			msg = message(code: 'default.updated.message', args: [message(code: 'kola.task.documentation'), taskDocumentationInstance.id])
		}
		else {
			taskDocumentationInstance.text = "DELETED"
			taskDocumentationInstance.deleted =true
			taskService.saveTaskDocumentation(taskDocumentationInstance)
			msg = message(code: 'default.deleted.message', args: [message(code: 'kola.task.documentation'), taskDocumentationInstance.id])
		}

		flash.message = msg
		def task = taskDocumentationInstance.reference
		if (task instanceof TaskStep) {
			task = task.task
		}
		redirect action:"show", method:"GET", id:task.id, fragment:"documentations"
	}

	@Transactional
	def saveComment(Comment comment) {
		def documentation = comment.reference
		def task = documentation.reference
		if (task instanceof TaskStep) {
			task = task.task
		}

		comment.creator = springSecurityService.currentUser
		if (!questionService.saveComment(comment, task)) {
			respond comment.errors, view:'show'
			return
		}

		flash.message = message(code: 'default.created.message.single', args: [message(code: 'de.httc.plugin.qaa.comment')])
		redirect action:"show", method:"GET", id:task.id, fragment:"documentations_${documentation.id}"
	}

	@Transactional
	def saveReflectionAnswer(ReflectionAnswer reflectionAnswerInstance) {
		if (reflectionAnswerInstance == null || !reflectionAnswerInstance.task || !reflectionAnswerInstance.question) {
			throw new RuntimeException("no task or question for new answer")
		}
		if (!authService.canAttach(reflectionAnswerInstance.task)) {
			forbidden()
		}
		if (reflectionAnswerInstance.rating) {
			reflectionAnswerInstance.creator = springSecurityService.currentUser

			reflectionAnswerInstance.save flush:true
			if (reflectionAnswerInstance.hasErrors()) {
				respond reflectionAnswerInstance.errors, view:'show'
				return
			}
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'kola.reflectionAnswer'), reflectionAnswerInstance.id])
		redirect action:"show", method:"GET", id:reflectionAnswerInstance.task.id, fragment:"reflectionQuestions"
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
		if (reflectionAnswerInstance.rating) {
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
		redirect action:"show", method:"GET", id:reflectionAnswerInstance.task.id, fragment:"reflectionQuestions"
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
		for (i in 0..29) {
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
					def asset = new Asset(name:f.originalFilename, typeLabel:"attachment", mimeType:f.getContentType(), content:new AssetContent(data:f.bytes))
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

	protected Collection<Profile> validateTaskAndGroupForAssignment(task, group) {
		if (task == null) {
			notFound()
			return null
		}
		if (!authService.canEdit(task)) {
			forbidden()
			return null
		}
		if (task.isTemplate) {
			flash.error = message(code:"kola.task.assign.group.error.template")
			redirect action:"edit", id:task.id
			return null
		}
		if (!group || group.taxonomy != Taxonomy.findByLabel("organisations")) {
			flash.error = message(code:"kola.task.assign.group.error.groupNotFound")
			redirect action:"edit", id:task.id
			return null
		}
		if (!authService.assignableOrganisations?.contains(group)) {
			flash.error = message(code:"kola.task.assign.group.error.groupNotAccessible")
			redirect action:"edit", id:task.id
			return null
		}
		if (task.assignee) {
			// task is already assigned -> error
			flash.error = message(code:"kola.task.assign.group.error.alreadyAssigned")
			redirect action:"index"
			return null
		}
		def userProfiles = Profile.createCriteria().list {
			organisations {
				eq("id", group.id)
			}
			order("lastName", "asc")
			order("firstName", "asc")
		}
		if (userProfiles.empty) {
			flash.error = message(code:"kola.task.assign.group.error.empty", args:[group.label])
			redirect action:"edit", id:task.id
			return null
		}
		return userProfiles
	}
}
