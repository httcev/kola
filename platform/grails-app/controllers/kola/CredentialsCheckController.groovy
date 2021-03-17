package kola

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class CredentialsCheckController {
	def springSecurityService

	def index() {
		def result = [
			"status" : "ok",
			"userId" : springSecurityService.currentUser.id
		]
		render result as JSON
	}
}
