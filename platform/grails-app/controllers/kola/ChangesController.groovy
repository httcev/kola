package kola

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured

@Secured(['IS_AUTHENTICATED_FULLY'])
class ChangesController {
	static allowedMethods = [index: "GET"]
    def springSecurityService

    def index() {
    	try {
	    	// TODO: incorporate current user in changes feed (return only relevant data)
	    	def user = springSecurityService.currentUser
	    	def since = params.since ? new Date().parse("yyyy-MM-dd'T'hh:mm:ss'Z'", params.since) : new Date(0)

	    	def now = new Date()
	    	println "between $since and $now"
	    	def result = [
	    		"now"    : now,
	    		"users"  : User.findAllByLastUpdatedBetween(since, now),
	    		"tasks"  : Task.findAllByLastUpdatedBetween(since, now),
	    		"assets" : Asset.findAllByLastUpdatedBetween(since, now)
    		]

	    	render result as JSON
	    }
	    catch(e) {
	    	render status:400, contentType:"text/plain", text:e.message
	    }
    }
}
