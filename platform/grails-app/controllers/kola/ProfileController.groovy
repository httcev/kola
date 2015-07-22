package kola

import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
@Secured(['ROLE_USER'])
class ProfileController {
	def springSecurityService

    def index() {
		def userInstance = springSecurityService.currentUser
		respond userInstance
    }

    @Transactional
    def update() {
		def userInstance = springSecurityService.currentUser
		bindData(userInstance, params, [exclude: ['id', 'authorities', 'enabled', 'accountExpired', 'accountLocked', 'passwordExpired']])

        userInstance.save flush:true
        if (userInstance.hasErrors() || userInstance.profile?.hasErrors()) {
            respond userInstance.errors, view:'index'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ respond userInstance, [status: OK] }
        }
    }
}
