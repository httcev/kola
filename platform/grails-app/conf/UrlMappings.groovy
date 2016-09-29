class UrlMappings {
	static mappings = {
		"/api/changes"{
			controller = "changes"
			action = [GET:"index", POST:"index"]
		}
		"/api/upload/$id"{
			controller = "changes"
			action = [POST:"uploadAttachment"]
		}
		"/api/pushToken"{
			controller = "pushToken"
			action = [POST:"update"]
		}

		"/$controller/$action?/$id?(.$format)?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:"index")
		"500"(view:'/error')
	}
}
