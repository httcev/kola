package kola

import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional
import de.httc.plugins.repository.Asset

@Secured(['permitAll'])
@Transactional(readOnly = true)
class IndexController {
	def springSecurityService

    def index() {
    	if (springSecurityService.isLoggedIn()) {
    		def assignedTasks = Task.where { (assignee == springSecurityService.currentUser || creator == springSecurityService.currentUser) && isTemplate != true && done != true && deleted != true }.list().sort { a, b ->
                // sort order: first tasks having a due date (sorted ascending), then tasks having no due date (sorted by lastUpdated descending)
                a.due ? (b.due ? a.due <=> b.due : -1) : (b.due ? 1 : b.lastUpdated <=> a.lastUpdated)
            }
    		def latestAssets = Asset.where { typeLabel == 'learning-resource' && deleted == false }.list(sort:"lastUpdated", order:"desc", max:4)
    		render(view:"dashboard", model:[assignedTasks:assignedTasks, latestAssets:latestAssets])
    	}
    	else {
    		render(view:"index", model:[welcomeHeader:Settings.settings.welcomeHeader, welcomeBody:Settings.settings.welcomeBody])
    	}
    }
}
