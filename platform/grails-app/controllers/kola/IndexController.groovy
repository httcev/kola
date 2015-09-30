package kola

import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class IndexController {
	def springSecurityService

    def index() {
    	if (springSecurityService.isLoggedIn()) {
    		def assignedTasks = Task.where { assignee == springSecurityService.currentUser && done != true && deleted != true }.list()
    		def latestAssets = Asset.where { subType == 'learning-resource' && deleted != true }.list(sort:"lastUpdated", order:"desc", max:4)

    		render(view:"dashboard", model:[assignedTasks:assignedTasks, latestAssets:latestAssets])
    	}
    	else {
    		render(view:"index")
    	}
    }
}
