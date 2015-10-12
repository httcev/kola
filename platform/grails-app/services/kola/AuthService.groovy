package kola

import grails.transaction.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils

@Transactional(readOnly = true)
class AuthService {
    def springSecurityService

    // e.g.: is user allowed to create documentations or question answers on document?
    def canAttach(domain, user = springSecurityService.currentUser) {
        if (user) {
            if (domain?.hasProperty("creator") && domain.creator == user) {
                return true
            }
            if (domain?.hasProperty("assignee") && domain.assignee == user) {
                return true
            }
            return SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')
        }
        return false
    }

    def canEdit(domain, user = springSecurityService.currentUser) {
        if (user) {
            if (domain?.hasProperty("creator") && domain.creator == user) {
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
