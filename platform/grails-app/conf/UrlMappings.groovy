class UrlMappings {
	static mappings = {
		"/api/changes"{
			controller = "changes"
			action = [GET:"index", POST:"index"]
		}
		"/api/check"{
			controller = "credentialsCheck"
			action = [GET:"index"]
		}
		"/api/upload/$id"{
			controller = "changes"
			action = [POST:"uploadAttachment"]
		}
		"/api/pushToken"{
			controller = "pushToken"
			action = [GET:"index", POST:"update"]
		}

		"/user/$action?/$id?(.$format)?" {
			controller = "user"
			namespace = "kola"
		}

		"/admin/$controller/$action?/$id?(.$format)?" { namespace = "admin" }

		"/$controller/$action?/$id?(.$format)?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:"index")
		"500"(view:'/error')
	}
}
