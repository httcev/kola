package kola

import org.springframework.context.i18n.LocaleContextHolder as LCH

class TermsOfUseFilters {
    def springSecurityService
    def messageSource

    def filters = {
        all(controller:'*', action:'*', controllerExclude:'termsOfUse|logout|changes') {
            before = {
                // only redirect if logged in and not accepted already
                if (springSecurityService.loggedIn && !springSecurityService.currentUser.termsOfUseAccepted) {
                    // only redirect if terms of use are configured
                    if (Settings.settings.termsOfUse) {
                        flash.message = messageSource.getMessage("kola.termsOfUse.accept.please", null , LCH.getLocale())
                        redirect(controller:"termsOfUse", action:"index")
                        return false
                    }
                }
            }
        }
    }
}
