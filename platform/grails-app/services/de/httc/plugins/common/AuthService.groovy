package de.httc.plugins.common

import grails.transaction.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import de.httc.plugins.user.Profile

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
			if (domain instanceof kola.TaskStep) {
				if (domain.task.assignee == user) {
					return true
				}
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
        if (SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_TEACHER')) {
			return Profile.createCriteria().list {
				and {
					order("lastName", "asc")
					order("firstName", "asc")
					order("id", "asc")
				}
			}
        }
        else {
            def currentCompany = springSecurityService.currentUser?.profile?.company
			return Profile.createCriteria().list {
				and {
					eq("company", currentCompany)

					order("lastName", "asc")
					order("firstName", "asc")
					order("id", "asc")
				}
			}
        }
    }
}
