package de.httc.plugins.common

import grails.transaction.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import de.httc.plugins.user.Profile
import de.httc.plugins.taxonomy.Taxonomy
import de.httc.plugins.taxonomy.TaxonomyTerm

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
				user {
					and {
						eq("accountLocked", false)
						eq("enabled", true)
					}
				}
				order("lastName", "asc")
				order("firstName", "asc")
				order("id", "asc")
			}
		}
		else {
			def currentCompany = springSecurityService.currentUser?.profile?.company
			return Profile.createCriteria().list {
				eq("company", currentCompany)
				user {
					and {
						eq("accountLocked", false)
						eq("enabled", true)
					}
				}

				order("lastName", "asc")
				order("firstName", "asc")
				order("id", "asc")
			}
		}
	}

	def getAssignableOrganisationsFirstLevel() {
		if (SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_TEACHER')) {
			return Taxonomy.findByLabel("organisations")?.children
		}
		else {
			return null
		}
	}

	def getAssignableOrganisations() {
		if (SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_TEACHER')) {
			return TaxonomyTerm.findAllByTaxonomy(Taxonomy.findByLabel("organisations"))
		}
		else {
			return null
		}
	}
}
