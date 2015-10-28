package kola

import org.springframework.context.i18n.LocaleContextHolder as LCH

class TermsOfUseFilters {
    def springSecurityService
    def grailsApplication
    def messageSource

    def filters = {
        all(controller:'*', action:'*', controllerExclude:'termsOfUse|logout|changes') {
            before = {
                // only redirect if terms of use are configured and user is logged in and has not accepted already
                if (grailsApplication.config.kola.termsOfUseExisting && springSecurityService.loggedIn && !session.termsOfUseAccepted) {
                    if (!springSecurityService.currentUser.termsOfUseAccepted) {
                        flash.message = messageSource.getMessage("kola.termsOfUse.accept.please", null , LCH.getLocale())
                        redirect(controller:"termsOfUse", action:"index")
                        return false
                    }
                    else {
                        // need to cache acceptance in session, otherwise we'll get an error in web flows (like creation of learning resources)
                        session.termsOfUseAccepted = true        
                    }
                }
            }
        }
    }
}
