package kola

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class UserController {
    def thumbnailService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond User.list(params), model:[userInstanceCount: User.count()]
    }

    def create() {
        respond new User(params)
    }

    @Transactional
    def save(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (params['_photo']?.bytes?.length > 0) {
            userInstance.profile.photo = thumbnailService.createThumbnailBytes(params['_photo'].bytes)
        }
        else if (params['_deletePhoto'] == 'true') {
            userInstance.profile.photo = null
        }

        userInstance.save flush:true
        if (userInstance.hasErrors() || userInstance.profile?.hasErrors()) {
            respond userInstance.errors, view:'create'
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect action:"index", method:"GET"
    }

    def edit(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }
        respond userInstance
    }

    @Transactional
    def update(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (params['_photo']?.bytes?.length > 0) {
            userInstance.profile.photo = thumbnailService.createThumbnailBytes(params['_photo'].bytes)
        }
        else if (params['_deletePhoto'] == 'true') {
            userInstance.profile.photo = null
        }

        userInstance.save flush:true
        if (userInstance.hasErrors() || userInstance.profile?.hasErrors()) {
            respond userInstance.errors, view:'edit'
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
        redirect action:"index", method:"GET"
    }

    @Transactional
    def delete(User userInstance) {

        if (userInstance == null) {
            notFound()
            return
        }

        userInstance.delete flush:true

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
        redirect action:"index", method:"GET"
    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
        redirect action: "index", method: "GET"
    }
}
