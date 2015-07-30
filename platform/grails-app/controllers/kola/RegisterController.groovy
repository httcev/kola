package kola

class RegisterController extends grails.plugin.springsecurity.ui.RegisterController {
	def index() {
		response.sendError(404)
	}

	/**
		Overridden because default behavior does not work in proxied production environment (links to private IP)
	**/
	protected String generateLink(String action, linkParams) {
		createLink(controller:"register", action:action, params:linkParams, absolute:true)
	}
}
