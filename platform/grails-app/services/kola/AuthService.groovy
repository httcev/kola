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

    def getAssignableUserProfiles() {
        if (SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
            return Profile.list(sort:"displayName")
        }
        else {
            def currentCompany = springSecurityService.currentUser?.profile?.company
            /*
            def query = User.where { profile?.company == company }.order("profile?.displayName")
            return query.list()
            */
            return Profile.findAll(sort:"displayName") {
                company == currentCompany
            }
        }
    }
}
