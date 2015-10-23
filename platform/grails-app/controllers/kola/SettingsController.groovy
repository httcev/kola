package kola

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class SettingsController {
    def grailsApplication

    def index() {
        respond Settings.getSettings()
    }

    @Transactional
    def update() {
        def settings = Settings.getSettings()
        bindData(settings, params)

        settings.save flush:true
        if (settings.hasErrors()) {
            respond settings.errors, view:'index'
            return
        }

        // update terms of use existing cache
        grailsApplication.config.kola.termsOfUseExisting = settings.termsOfUse?.length() > 0

        flash.message = message(code: 'default.updated.message', args: [message(code: 'kola.settings', default: 'Settings'), settings.id])
        redirect action:"index", method:"GET"
    }
}
