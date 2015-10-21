package kola

import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
@Secured(['IS_AUTHENTICATED_FULLY'])
class ProfileController {
	def springSecurityService
    def imageService
    def grailsApplication

    def index() {
		def userInstance = springSecurityService.currentUser
		respond userInstance
    }

    @Transactional
    def update() {
		def userInstance = springSecurityService.currentUser
		bindData(userInstance, params, [exclude: ['id', 'authorities', 'enabled', 'accountExpired', 'accountLocked', 'passwordExpired']])
        if (params['_photo']?.bytes?.length > 0) {
            userInstance.profile.photo = imageService.createThumbnailBytes(params['_photo'].bytes, grailsApplication.config.kola.avatarSize)
        }
        else if (params['_deletePhoto'] == 'true') {
            userInstance.profile.photo = null
        }

        userInstance.save flush:true
        if (userInstance.hasErrors() || userInstance.profile?.hasErrors()) {
            respond userInstance.errors, view:'index'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'kola.user', default: 'User'), userInstance.username])
                redirect action:"index", method:"GET"
            }
            '*'{ respond userInstance, [status: OK] }
        }
    }
}
