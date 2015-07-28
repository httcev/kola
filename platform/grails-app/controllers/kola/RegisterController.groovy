package kola

class RegisterController extends grails.plugin.springsecurity.ui.RegisterController {
	def index() {
		response.sendError(404)
	}
}
