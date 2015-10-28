package kola

import org.springframework.context.i18n.LocaleContextHolder as LCH

class TermsOfUseFilters {
    def springSecurityService
    def grailsApplication
    def messageSource
    def sessionFactory

    def filters = {
        all(controller:'*', action:'*', controllerExclude:'termsOfUse|logout|changes') {
            before = {
                // only redirect if terms of use are configured and user is logged in and has not accepted already
                if (grailsApplication.config.kola.termsOfUseExisting && springSecurityService.loggedIn) {
                    if (sessionFactory.currentSession != null && !springSecurityService.currentUser.termsOfUseAccepted) {
                        flash.message = messageSource.getMessage("kola.termsOfUse.accept.please", null , LCH.getLocale())
                        redirect(controller:"termsOfUse", action:"index")
                        return false
                    }
                }
            }
        }
    }
}
