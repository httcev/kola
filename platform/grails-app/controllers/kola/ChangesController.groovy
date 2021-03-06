package kola

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional
import java.text.SimpleDateFormat
import java.text.DateFormat
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import de.httc.plugins.user.User
import de.httc.plugins.repository.Asset
import de.httc.plugins.repository.AssetContent
import de.httc.plugins.qaa.Question
import de.httc.plugins.qaa.Answer
import de.httc.plugins.qaa.Comment

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
@Transactional
class ChangesController {
	private static final Log LOG = LogFactory.getLog('usagetracking')

	static allowedMethods = [index: ["GET", "POST"], upload:"POST"]
	static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	static DOMAIN_CLASS_MAPPING = ["asset":Asset, "taskStep":TaskStep, "task":Task, "taskDocumentation":TaskDocumentation, "reflectionAnswer":ReflectionAnswer, "question":Question, "answer":Answer, "comment":Comment ]
	def springSecurityService
	def taskService
	def settingService
	def questionService
	def sessionFactory

	def index() {
		try {
			def user = springSecurityService.currentUser
			def clientData = request.JSON
			def updatedIds = _update(clientData, user)

			def since = clientData?.info?.lastSyncDate ? DATEFORMAT.parse(clientData?.info?.lastSyncDate) : new Date(0)

			def now = new Date()
			println "handing out changes for $user between $since and $now"

			def users = User.withCriteria() {
				or {
					between('lastUpdated', since, now)
					profile {
						between('lastUpdated', since, now)
					}
				}
			}

			// learning resources may change without having a changed task, so hand out all modified ones.
			def assets = Asset.findAllByLastUpdatedBetweenAndTypeLabel(since, now, "learning-resource") as Set

			def allTasks = Task.createCriteria().list {
				// between("lastUpdated", since, now)
				or {
					// always deliver task templates
					eq("isTemplate", true)
					// otherwise deliver only tasks created by or assigned to requesting user
					//or {
						eq("creator", user)
						eq("assignee", user)
					//}
				}
			}

			def tasks = [] as Set
			def taskSteps = [] as Set
			def questions = [] as Set
			def answers = [] as Set
			def comments = [] as Set
			def taskDocumentations = [] as Set

			allTasks.each { task ->
				if (_isMofifiedBetween(task, since, now)) {
					// we treat all data editable in the task editor as having changed
					tasks.add(task)
					// add task attachments
					task.attachments?.each { attachment ->
						assets.add(attachment)
					}
					// add task steps
					task.steps?.each { step ->
						taskSteps.add(step)
						// add step attachments
						step.attachments?.each { attachment ->
							assets.add(attachment)
						}
					}
				}

				task.documentations.each { taskDocumentation ->
					if (_isMofifiedBetween(taskDocumentation, since, now)) {
						taskDocumentations.add(taskDocumentation)
					}
					// add modified task documentation attachments
					_addModifiedDocs(taskDocumentation.attachments, assets, since, now)
					// add modified task documentation comments
					taskDocumentation.comments.each { comment ->
						if (_isMofifiedBetween(comment, since, now)) {
							comments.add(comment)
							//taskDocumentations.add(taskDocumentation)
						}
					}
				}

				// add modified task questions and related answers/comments/attachments
				Question.findAllByReference(task).each { question ->
					_addModifiedQuestionTree(question, questions, answers, comments, assets, since, now)
				}

				task.steps?.each { step ->
					step.documentations.each { taskDocumentation ->
						if (_isMofifiedBetween(taskDocumentation, since, now)) {
							taskDocumentations.add(taskDocumentation)
						}
						// add modified task documentation attachments
						_addModifiedDocs(taskDocumentation.attachments, assets, since, now)
						// add modified task documentation comments
						taskDocumentation.comments.each { comment ->
							if (_isMofifiedBetween(comment, since, now)) {
								comments.add(comment)
								//taskDocumentations.add(taskDocumentation)
							}
						}
					}

					// add modified task step questions and related answers/comments/attachments
					Question.findAllByReference(step).each { question ->
						_addModifiedQuestionTree(question, questions, answers, comments, assets, since, now)
					}
				}
			}

			// now deliver questions without task reference depending on channel setting
			def questionChannelGeneral = settingService.getValue("questionChannelGeneral")
			Question.where {
				and {
					between("lastUpdated", since, now)
					isNull("reference")
				}
			}.list().each { question ->
				def deliver = false
				if (question.creator == user) {
					println "--- 1 deliver because user is question creator"
					deliver = true
				}
				else {
					if ("broadcast" == questionChannelGeneral) {
						println "--- 1 deliver because channel is 'broadcast'"
						deliver = true
					}
					else if ("company" == questionChannelGeneral) {
						def creatorCompany = question.creator?.profile?.company
						if (creatorCompany != null && creatorCompany.id == user.profile?.company?.id) {
							println "--- 2 deliver because channel is 'company' and user has same company [${user.profile?.company}]"
							deliver = true
						}
					}
					else if ("groups" == questionChannelGeneral) {
						def creatorGroups = question.creator?.profile?.organisations
						def userGroups = user.profile?.organisations
						if (creatorGroups?.size() > 0 && userGroups?.size() > 0 && creatorGroups.intersect(userGroups).size() > 0) {
							println "--- 3 deliver because channel is 'groups' and user intersects groups [${creatorGroups}]"
							deliver = true
						}
					}
				}
				if (deliver) {
					_addModifiedQuestionTree(question, questions, answers, comments, assets, since, now)
				}
			}
/*
			// TODO: we miss some task documentations in the following scenario:
			// 1. user A assigns task to user B
			// 2. user B creates task documentation
			// 3. user A receives push notification (regarding 2.), opens app and doesn't see the documentation
			def usersTaskDocumentations = TaskDocumentation.findAllByCreator(user)
			usersTaskDocumentations?.each { taskDocumentation ->
				if (_isMofifiedBetween(taskDocumentation, since, now)) {
					taskDocumentations.add(taskDocumentation)
				}
				taskDocumentation.comments.each { comment ->
					if (_isMofifiedBetween(comment, since, now)) {
						comments.add(comment)
						taskDocumentations.add(taskDocumentation)
					}
				}
			}
			taskDocumentations?.each { taskDocumentation ->
				// add modified attachments
				_addModifiedDocs(taskDocumentation.attachments, assets, since, now)
			}
*/
			def result = [
				"now"  : DATEFORMAT.format(now),
				"updated" : updatedIds,
				"data" : [
					"user" : users,
					// reflection questions may change without having a changed task, so hand out all modified ones.
					"reflectionQuestion" : ReflectionQuestion.findAllByLastUpdatedBetween(since, now) as Set,
					"reflectionAnswer" : ReflectionAnswer.findAllByLastUpdatedBetweenAndCreator(since, now, user),
					"asset" : assets,
					"taskStep" : taskSteps,
					"task" : tasks,
					"taskDocumentation" : taskDocumentations,
					"question" : questions,
					"answer" : answers,
					"comment" : comments
				]
			]
			print result as JSON
			render result as JSON
		}
		catch(e) {
			log.error e
			render status:400, contentType:"text/plain", text:e?.message?:"unknown error"
		}
		finally {
			// this is needed to prevent the java heap from growing full
			sessionFactory.getCurrentSession().clear()
		}
	}

	def uploadAttachment(String id) {
		println "-------------------------- uploading " + id
		if (!id) {
			println "--- ERROR 400, no id in url"
			render status:400, contentType:"text/plain", text:"missing id in URL"
			return
		}
		if (!params.file) {
			println "--- ERROR 400, no file in request"
			render status:400, contentType:"text/plain", text:"missing parameter 'file'"
			return
		}
		try {
			LOG.info("changes/uploadAttachment/$id")
			// TODO: check write access
			Asset asset = Asset.get(id)
			if (!asset) {
				println "--- ERROR 404"
				render status:404, contentType:"text/plain", text:"not found"
				return
			}

			// remove possible extsting asset content
			if (!asset.content) {
				asset.content = new AssetContent()
			}
			asset.content.data = request.getFile("file").bytes
			if (!asset.creator) {
				asset.creator = springSecurityService.currentUser
			}
			if (asset.save()) {
				println "--- SUCCESS"
				render status:204
				return
			}
			else {
				println "--- ERROR 400, couldn't save"
				asset.errors.allErrors.each { println it }
				render status:500
			}
		}
		catch(e) {
			log.error e
			render status:400
		}
	}

	def _addModifiedDocs(docs, target, start, end) {
		docs?.each { doc ->
			// TODO: clean this up. we currently deliver ALL assets to handle the following case:
			// 1. Create task with attachments, do not assign to user.
			// 2. Sync in App
			// 3. Assign task to user.
			// -> now, if we don't deliver all assets, the App will end up with unknown IDs...
			if (_isMofifiedBetween(doc, start, end)) {
				target.add(doc)
			}
		}
	}

	def _addModifiedQuestionTree(question, questions, answers, comments, assets, start, end) {
		if (_isMofifiedBetween(question, start, end)) {
			questions.add(question)
			println "--- 1 question is modified"
		}
		question.comments.each { comment ->
			if (_isMofifiedBetween(comment, start, end)) {
				println "--- 2 question comment is modified"
				comments.add(comment)
				questions.add(question)
			}
		}
		question.attachments.each { attachment ->
			if (_isMofifiedBetween(attachment, start, end)) {
				println "--- 3 question attachment is modified"
				assets.add(attachment)
			}
		}
		question.answers.each { answer ->
			if (_isMofifiedBetween(answer, start, end)) {
				println "--- 4 answer is modified"
				answers.add(answer)
				questions.add(question)
			}
			answer.comments.each { comment ->
				if (_isMofifiedBetween(comment, start, end)) {
					println "--- 5 answer comment is modified"
					answers.add(answer)
					comments.add(comment)
				}
			}
			answer.attachments.each { attachment ->
				if (_isMofifiedBetween(attachment, start, end)) {
					println "--- 6 answer attachment is modified"
					assets.add(attachment)
				}
			}
		}
	}

	def _isMofifiedBetween(doc, start, end) {
		doc && !(doc.lastUpdated.before(start) || doc.lastUpdated.after(end))
	}

	def _update(clientData, user) {
		def modified = false
		def updatedIds = []
		DOMAIN_CLASS_MAPPING.each { table, domainClass ->
			clientData.data?."$table"?.each {
				def doc = JSON.parse(it.doc)
				def model = domainClass.get(doc.id)
				if (!model) {
					LOG.info("changes/create_${table}/${doc.id}")
					println "--- creating new " + domainClass + " with id " + doc.id + ", user=" + user
					model = domainClass.newInstance()
					model.id = doc.id
				}
				else {
					LOG.info("changes/update_${table}/${doc.id}")
					println "--- found existing " + domainClass + " with id " + doc.id
				}
				if (!model.creator) {
					model.creator = user
				}
				// check write access to model
				if (model.creator == user) {
					model.properties = doc
				}
				else if ((model instanceof Task) && (model.assignee == user)) {
					// special case for Task: allow write access to assignee to update "done" field
					model.done = doc.done
				}
				else if (model instanceof Question || model instanceof Answer) {
					// special cases for questions/answers: allow to update rating
					model.setRated(doc.rated == true)
					println "--- UPDATED RATING -> " + model.rated
				}
				if (_saveModel(model)) {
					modified = true
					updatedIds.push(model.id)
					println "--- SUCCESSFUL SAVE on " + model.class.simpleName + " with id " + model.id + ", user=" + user
				}
				else {
					println "--- FAILED SAVE on " + model.class.simpleName + " with id " + model.id + ", user=" + user
					model.errors.allErrors.each {
						println it
					}
				}
			}
		}


		if (modified) {
			// flushing the session lets hibernate set the lastUpdated field on the new objects so that they can directly be returned by the response
			sessionFactory.getCurrentSession()?.flush()
		}
		return updatedIds
	}

	def _saveModel(model) {
		// in case of a "task", delegate saving to taskService to handle push notifications
		if (model instanceof Task) {
			return taskService.save(model)
		}
		else if (model instanceof TaskDocumentation) {
			return taskService.saveTaskDocumentation(model)
		}
		else if (model instanceof Question) {
			return questionService.saveQuestion(model)
		}
		else if (model instanceof Answer) {
			return questionService.saveAnswer(model)
		}
		else if (model instanceof Comment) {
			return questionService.saveComment(model)
		}
		else {
			return model.save()
		}
	}
}
