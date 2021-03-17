package kola

import static org.springframework.http.HttpStatus.*
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional

@Transactional(readOnly = true)
class TaskDocumentationController {
	def springSecurityService

	@Secured(['IS_AUTHENTICATED_REMEMBERED'])
	def index(Integer max) {
		params.offset = params.offset && !params.resetOffset ? (params.offset as int) : 0
		params.max = Math.min(max ?: 10, 1000)
		params.sort = params.sort ?: "lastUpdated"
		params.order = params.order ?: "desc"

		def user = springSecurityService.currentUser
		def userCompany = user.profile?.company
		params.createdBy = params.createdBy ?: "company"
		def filtered = (params.createdBy != "all")

		def results = TaskDocumentation.createCriteria().list(max:params.max, offset:params.offset) {
			// left join allows null values in the association
			createAlias('creator', 'c', org.hibernate.Criteria.LEFT_JOIN)
			createAlias('c.profile', 'cp', org.hibernate.Criteria.LEFT_JOIN)
			createAlias('cp.company', 'cpc', org.hibernate.Criteria.LEFT_JOIN)

			eq("deleted", false)
			if (filtered) {
				or {
					if (params.createdBy == "own") {
						eq("creator", user)
					}
					if (params.createdBy == "company") {
						eq("cp.company", userCompany)
					}
				}
			}
			order(params.sort, params.order)
		}
		respond results, model:[taskDocumentationCount:results.totalCount]
	}
}
