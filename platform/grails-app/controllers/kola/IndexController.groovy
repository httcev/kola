package kola

import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class IndexController {
	def springSecurityService

    def index() {
    	if (springSecurityService.isLoggedIn()) {
    		render(view:"dashboard")
    	}
    	else {
    		render(view:"index")
    	}
    }
}
