package kola

import grails.transaction.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils

@Transactional(readOnly = true)
class AuthService {
    def springSecurityService

    def canEdit(domain, user = springSecurityService.currentUser) {
        if (user) {
            if (domain?.creator && domain.creator == user) {
                return true
            }
            return SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')
        }
        return false
    }

    def canDelete(domain, user = springSecurityService.currentUser) {
        return canEdit(domain, user)
    }
}
