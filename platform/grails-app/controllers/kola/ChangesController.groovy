package kola

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional
import java.text.SimpleDateFormat
import java.text.DateFormat
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import de.httc.plugins.user.User

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
@Transactional
class ChangesController {
    private static final Log LOG = LogFactory.getLog('usagetracking')

	static allowedMethods = [index: ["GET", "POST"], upload:"POST"]
	static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    static DOMAIN_CLASS_MAPPING = ["asset":Asset, "taskStep":TaskStep, "taskDocumentation":TaskDocumentation, "reflectionAnswer":ReflectionAnswer, "task":Task]
    def springSecurityService
    def taskService
    def sessionFactory

    def index() {
    	try {
	    	def user = springSecurityService.currentUser
    		def clientData = request.JSON
    		_update(clientData, user)

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
    		def assets = Asset.findAllByLastUpdatedBetweenAndSubType(since, now, "learning-resource") as Set

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
	    	tasks?.each { task ->
	    		// add modified attachments
	    		_addModifiedDocs(task.attachments, assets, since, now)
	    		task.steps?.each { step ->
		    		taskSteps.add(step)
		    		// add modified attachments
		    		_addModifiedDocs(step.attachments, assets, since, now)
	    		}
    		}
	    	def taskDocumentations = TaskDocumentation.findAllByLastUpdatedBetweenAndCreator(since, now, user);
	    	taskDocumentations?.each { taskDocumentation ->
	    		// add modified attachments
	    		_addModifiedDocs(taskDocumentation.attachments, assets, since, now)
	    	}

	    	def result = [
	    		"now"  : DATEFORMAT.format(now),
	    		"data" : [
		    		"user" : users,
		    		// reflection questions may change without having a changed task, so hand out all modified ones.
		    		"reflectionQuestion" : ReflectionQuestion.findAllByLastUpdatedBetween(since, now),
		    		"reflectionAnswer" : ReflectionAnswer.findAllByLastUpdatedBetweenAndCreator(since, now, user),
		    		"asset" : assets,
		    		"taskStep" : taskSteps,
		    		"task" : tasks,
		    		"taskDocumentation" : taskDocumentations
	    		]
    		]
    		println result
	    	render result as JSON
	    }
	    catch(e) {
	    	e.printStackTrace()
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

    		asset.content = request.getFile("file").bytes
    		if (asset.save(true)) {
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
	    	e.printStackTrace()
	    	render status:400, contentType:"text/plain", text:e.message
	    }
    }

    def _addModifiedDocs(docs, target, start, end) {
    	docs?.each { doc ->
    		if (!doc) {
    			println "--- doc is null"
    			println docs
    			println target
    		}
    		if (doc) {
    			// TODO: clean this up. we currently deliver ALL assets to handle the following case:
    			// 1. Create task with attachments, do not assign to user.
    			// 2. Sync in App
    			// 3. Assign task to user.
    			// -> now, if we don't deliver all assets, the App will end up with unknown IDs...
	    		if (true || !(doc.lastUpdated.before(start) || doc.lastUpdated.after(end))) {
	    			target.add(doc)
	    		}
	    	}
    	}
    }

    def _update(clientData, user) {
    	def modified = false
    	["asset", "taskStep", "reflectionAnswer", "task", "taskDocumentation"].each { table ->
    		def domainClass = DOMAIN_CLASS_MAPPING[table]
    		clientData.data?."$table"?.each {
				def doc = JSON.parse(it.doc)
				println doc
				def model = domainClass.get(doc.id)
				if (!model) {
					println "--- creating new " + domainClass + " with id " + doc.id + ", user=" + user
					LOG.info("changes/create_${table}/${doc.id}")
					model = domainClass.newInstance()
					model.id = doc.id
				}
				else {
					LOG.info("changes/update_${table}/${doc.id}")
					println "--- found existing " + domainClass + " with id " + doc.id
				}
				model.properties = doc
				if (!model.creator) {
					model.creator = user
				}
				// in case of a "task", delegate saving to taskService ti handle push notifications
				def success = "task" == table ? taskService.save(model) : model.save()
				if (success) {
					modified = true
					println "------------- SAVED:"
					println model
				}
				else {
	    			model.errors.allErrors.each { println it }
				}
    		}
		}
		if (modified) {
			// flushing the session lets hibernate set the lastUpdated field on the new objects so that they can directly be returned by the response
			sessionFactory.getCurrentSession()?.flush()
		}
    }
}
