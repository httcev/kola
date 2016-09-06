package kola

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import de.httc.plugins.common.Setting

@Transactional(readOnly = true)
@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class TermsOfUseController {
    static allowedMethods = [index:"GET", accept:"POST"]
    def springSecurityService

    @Secured(['permitAll'])
    def index() {
        def showAcceptControls = springSecurityService.loggedIn && !springSecurityService.currentUser.termsOfUseAccepted
        [terms:Setting.getValue("termsOfUse"), showAcceptControls:showAcceptControls]
    }

    @Transactional
    @Secured(['IS_AUTHENTICATED_REMEMBERED'])
    def accept() {
        if (params.accepted == null) {
            flash.message = message(code:"kola.termsOfUse.agree.check")
            redirect action:"index"
        }
        else {
            springSecurityService.currentUser.termsOfUseAccepted = true
            springSecurityService.currentUser.save()
            redirect controller:"index", action:"index"
        }
    }
}
