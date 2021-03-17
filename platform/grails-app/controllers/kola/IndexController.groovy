package kola

import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional
import de.httc.plugins.repository.Asset
import de.httc.plugins.qaa.Question

@Secured(['permitAll'])
@Transactional(readOnly = true)
class IndexController {
	def springSecurityService
	def settingService

	def index() {
		if (springSecurityService.isLoggedIn()) {
			/*
			de.httc.plugins.qaa.Comment.findAll().each {
				it.delete(flush:true)
			}

			de.httc.plugins.qaa.Question.findAll().each {
				it.delete(flush:true)
			}

			de.httc.plugins.qaa.Answer.findAll().each {
				it.delete(flush:true)
			}

			de.httc.plugins.qaa.Commentable.findAll().each {
				it.delete(flush:true)
			}

			ReflectionAnswer.findAll().each {
				it.delete(flush:true)
			}

			TaskStep.findAll().each {
				it.delete(flush:true)
			}

			TaskDocumentation.findAll().each {
				it.delete(flush:true)
			}

			Task.executeUpdate('delete from Task')
*/
			def assignedTasks = Task.where { (assignee == springSecurityService.currentUser || creator == springSecurityService.currentUser) && isTemplate != true && done != true && deleted != true }.list().sort { a, b ->
				// sort order: first tasks having a due date (sorted ascending), then tasks having no due date (sorted by lastUpdated descending)
				a.due ? (b.due ? a.due <=> b.due : -1) : (b.due ? 1 : b.lastUpdated <=> a.lastUpdated)
			}
			def latestAssets = Asset.where { typeLabel == 'learning-resource' && deleted == false }.list(sort:"lastUpdated", order:"desc", max:4)
			def latestQuestions = Question.where { deleted == false }.list(sort:"lastUpdated", order:"desc", max:4)
			render(view:"dashboard", model:[assignedTasks:assignedTasks, latestAssets:latestAssets, latestQuestions:latestQuestions])
		}
		else {
			render(view:"index", model:[welcomeHeader:settingService.getValue("welcomeHeader"), welcomeBody:settingService.getValue("welcomeBody")])
		}
	}
}
