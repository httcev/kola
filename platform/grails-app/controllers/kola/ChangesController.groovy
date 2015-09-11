package kola

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional
import java.text.SimpleDateFormat
import java.text.DateFormat

@Secured(['IS_AUTHENTICATED_FULLY'])
@Transactional
class ChangesController {
//	static allowedMethods = [index: "GET"]
	static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    def springSecurityService

    def index() {
    	try {
	    	def user = springSecurityService.currentUser
    		def clientData = request.JSON
    		_update(clientData, user)
println clientData?.info?.lastSyncDate
	    	// TODO: incorporate current user in changes feed (return only relevant data)
	    	def since = clientData?.info?.lastSyncDate ? DATEFORMAT.parse(clientData?.info?.lastSyncDate) : new Date(0)

	    	def now = new Date()
	    	since = new Date(0)
	    	println "between $since and $now"

	    	def tasks = Task.findAllByLastUpdatedBetween(since, now)
	    	def taskSteps = new HashSet()
	    	def taskDocumentations = new HashSet()
	    	tasks?.each { 
	    		taskSteps.addAll(it.steps)
	    		taskDocumentations.addAll(TaskDocumentation.findAllByTaskAndCreator(it, user))
    		}
    		def assets = Asset.findAllByLastUpdatedBetween(since, now)
    		def blobs = new ArrayList()
    		assets?.each {
    			if (it.subType == "attachment") {
    				blobs.add([id:it.id, content:it.content])
    			}
    		}


	    	def result = [
	    		"now"  : DATEFORMAT.format(now),
	    		"data" : [
		    		"user" : User.findAllByLastUpdatedBetween(since, now),
		    		"reflectionQuestion" : ReflectionQuestion.findAllByLastUpdatedBetween(since, now),
		    		"asset" : assets,
		    		"blob" : blobs,
		    		"taskStep" : taskSteps,
		    		"taskDocumentation" : taskDocumentations,
		    		"task" : tasks
	    		]
    		]
	    	render result as JSON
	    }
	    catch(e) {
	    	e.printStackTrace()
	    	render status:400, contentType:"text/plain", text:e.message
	    }
    }

    def static DOMAIN_CLASS_MAPPING = ["asset":Asset, "taskStep":TaskStep, "taskDocumentation":TaskDocumentation, "reflectionAnswer":ReflectionAnswer, "task":Task]

    def _update(clientData, user) {
    	["asset", "taskStep", "taskDocumentation", "reflectionAnswer", "task"].each { table ->
    		def domainClass = DOMAIN_CLASS_MAPPING[table]
    		clientData.data?."$table"?.each {
				def doc = JSON.parse(it.doc)
				println doc
				def model = domainClass.get(doc.id)
				if (!model) {
					println "--- creating new " + domainClass + " with id " + doc.id
					model = domainClass.newInstance()
					model.id = doc.id
				}
				else {
					println "--- found existing " + domainClass + " with id " + doc.id
				}
				model.properties = doc
				if (!model.creator) {
					model.creator = user
				}
				if (!model.save(true)) {
	    			model.errors.allErrors.each { println it }
				}
				else {
					println "------------- SAVED:"
					println model
				}
    		}
		}
/*
    	clientData.data?.each { table, objects ->
    		println "--- table=" + table
    		def domainClass
    		switch(table) {
    			case "taskDocumentation":
    				domainClass = TaskDocumentation
    				break;
    		}
    		if (domainClass) {
	    		objects?.each {
	    			def doc = JSON.parse(it.doc)
	    			println doc
	    			def model = domainClass.get(doc.id)
	    			if (!model) {
	    				println "--- creating new " + domainClass + " with id " + doc.id
	    				model = domainClass.newInstance()
	    				model.id = doc.id
	    			}
	    			else {
	    				println "--- found existing " + domainClass + " with id " + doc.id
	    			}
	    			model.properties = doc
	    			if (doc.taskId) {
	    				model.task = Task.get(doc.taskId)
	    			}
	    			if (!model.creator) {
	    				model.creator = user
	    			}
	    			if (!model.save(true)) {
	        			model.errors.allErrors.each { println it }
	    			}
	    			else {
	    				println "------------- SAVED:"
	    				println model
	    			}
	    		}
	    	}
    	}
*/    	
    }
}
