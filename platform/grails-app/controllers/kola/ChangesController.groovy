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
    static DOMAIN_CLASS_MAPPING = ["asset":Asset, "taskStep":TaskStep, "task":Task, "taskDocumentation":TaskDocumentation, "reflectionAnswer":ReflectionAnswer, "comment":Comment, "answer":Answer, "question":Question]
    def springSecurityService
    def taskService
    def sessionFactory

    def index() {
    	try {
	    	def user = springSecurityService.currentUser
    		def clientData = request.JSON
    		def updatedIds = _update(clientData, user)

	    	// TODO: incorporate current user in changes feed (return only relevant data)
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

			def c = Task.createCriteria()
	    	def tasks = c {
	    		between("lastUpdated", since, now)
	    		or {
	    			// always deliver task templates
	    			eq("isTemplate", true)
	    			// otherwise deliver only tasks created by or assigned to requesting user
		    		or {
		    			eq("creator", user)
		    			eq("assignee", user)
		    		}
		    	}
	    	}
	    	def taskSteps = [] as Set
	    	def questions = [] as Set
	    	def answers = [] as Set
	    	def comments = [] as Set
	    	tasks?.each { task ->
	    		// add modified task attachments
	    		_addModifiedDocs(task.attachments, assets, since, now)
	    		// add modified task questions and related answers/comments/attachments
	    		Question.findAllByReference(task).each { question ->
		    		_addModifiedQuestionTree(question, questions, answers, comments, assets, since, now)
	    		}
	    		task.steps?.each { step ->
		    		taskSteps.add(step)
		    		// add modified task step attachments
		    		_addModifiedDocs(step.attachments, assets, since, now)
		    		// add modified task step questions and related answers/comments/attachments
		    		Question.findAllByReference(step).each { question ->
			    		_addModifiedQuestionTree(question, questions, answers, comments, assets, since, now)
		    		}
	    		}
    		}

            Question.getAll().each { question ->
                _addModifiedQuestionTree(question, questions, answers, comments, assets, since, now)
            }


	    	def taskDocumentations = TaskDocumentation.findAllByLastUpdatedBetweenAndCreator(since, now, user);
	    	taskDocumentations?.each { taskDocumentation ->
	    		// add modified attachments
	    		_addModifiedDocs(taskDocumentation.attachments, assets, since, now)
	    	}

	    	def result = [
	    		"now"  : DATEFORMAT.format(now),
	    		"updated" : updatedIds,
	    		"data" : [
		    		"user" : users,
		    		// reflection questions may change without having a changed task, so hand out all modified ones.
		    		"reflectionQuestion" : ReflectionQuestion.findAllByLastUpdatedBetween(since, now),
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
    		println result
	    	render result as JSON
	    }
	    catch(e) {
	    	log.error e
	    	render status:400, contentType:"text/plain", text:e.message
	    }
    }

    def uploadAttachment(String id) {
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
	    	def user = springSecurityService.currentUser
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
    		if (asset.save()) {
    			println "--- SUCCESS"
		    	render status:200, contentType:"text/plain", text:"success"
		    	return
		    }
		    else {
    			println "--- ERROR 400, couldn't save"
	    		render status:400, contentType:"text/plain", text:"couldn't save"
		    }
	    }
	    catch(e) {
	    	log.error e
	    	render status:400, contentType:"text/plain", text:e.message
	    }
    }

    def _addModifiedDocs(docs, target, start, end) {
    	docs?.each { doc ->
    		if (doc) {
    			// TODO: clean this up. we currently deliver ALL assets to handle the following case:
    			// 1. Create task with attachments, do not assign to user.
    			// 2. Sync in App
    			// 3. Assign task to user.
    			// -> now, if we don't deliver all assets, the App will end up with unknown IDs...
	    		if (true || _isMofifiedBetween(doc, start, end)) {
	    			target.add(doc)
	    		}
	    	}
    	}
    }

    def _addModifiedQuestionTree(question, questions, answers, comments, assets, start, end) {
        println "--- checking modified question tree for qid " + question.id
    	if (_isMofifiedBetween(question, start, end)) {
    		questions.add(question)
            println "--- 1 question is modified"
    	}
    	question.comments.each { comment ->
	    	if (_isMofifiedBetween(comment, start, end)) {
                println "--- 2 question comment is modified"
	    		comments.add(comment)
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
	    	}
	    	answer.comments.each { comment ->
		    	if (_isMofifiedBetween(comment, start, end)) {
                    println "--- 5 answer comment is modified"
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
				def isNew = false
				if (!model) {
					LOG.info("changes/create_${table}/${doc.id}")
					println "--- creating new " + domainClass + " with id " + doc.id + ", user=" + user
					model = domainClass.newInstance()
					model.id = doc.id
					isNew = true
				}
				else {
					LOG.info("changes/update_${table}/${doc.id}")
					println "--- found existing " + domainClass + " with id " + doc.id
				}
				model.properties = doc
				if (!model.creator) {
					model.creator = user
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
    	else {
    		return model.save()
    	}
    }
}
