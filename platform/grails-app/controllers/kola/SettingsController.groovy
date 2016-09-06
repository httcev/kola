package kola

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class SettingsController {
	def grailsApplication

	def index() {
		respond Setting.listOrderByWeight()
	}

	@Transactional
	def update(SettingsCommand cmd) {
		cmd?.settings?.each { setting ->
			if (!setting.save()) {
				errors.rejectValue("value", "401", [message(code:"${setting.prefix}.${setting.key}")] as Object[], message(code:"kola.settings.required"))
			}

		}
		if (hasErrors()) {
			respond cmd?.settings, view:'index'
			return
		}
		// update "terms of use existing" cache
		grailsApplication.config.kola.termsOfUseExisting = Setting.getValue("termsOfUse")?.length() > 0
		flash.message = message(code: 'default.updated.message.single', args: [message(code: 'kola.settings', default: 'Settings')])
		redirect action:"index", method:"GET"
	}
}

class SettingsCommand {
	List<Setting> settings = [].withLazyDefault {
		new Setting()
	}
}
