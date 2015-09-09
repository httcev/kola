package kola

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured
import java.text.SimpleDateFormat
import java.text.DateFormat

@Secured(['IS_AUTHENTICATED_FULLY'])
class ChangesController {
//	static allowedMethods = [index: "GET"]
	static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
    def springSecurityService

    def index() {
    	try {
    		def clientData = request.JSON
	    	// TODO: incorporate current user in changes feed (return only relevant data)
	    	def user = springSecurityService.currentUser
	    	def since = clientData?.info?.lastSyncDate ? DATEFORMAT.parse(clientData?.info?.lastSyncDate) : new Date(0)

	    	def now = new Date()
	    	println "between $since and $now"

	    	def tasks = Task.findAllByLastUpdatedBetween(since, now)
	    	def taskSteps = new HashSet()
	    	tasks?.each { 
	    		taskSteps.addAll(it.steps)
    		}

	    	def result = [
	    		"now"  : DATEFORMAT.format(now),
	    		"data" : [
		    		"user" : User.findAllByLastUpdatedBetween(since, now),
		    		"reflectionQuestion" : ReflectionQuestion.findAllByLastUpdatedBetween(since, now),
		    		"asset" : Asset.findAllByLastUpdatedBetween(since, now),
		    		"taskStep" : taskSteps,
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
}
