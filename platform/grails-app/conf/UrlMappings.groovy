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

		"/user/$action?/$id?(.$format)?" { controller = "user"; namespace = "admin"; plugin = "httcUser" }
		"/taxonomies/$action?/$id?(.$format)?" { controller = "taxonomies"; plugin = "httcTaxonomy" }
		"/admin/taxonomies/$action?/$id?(.$format)?" { controller = "taxonomies"; namespace = "admin"; plugin = "httcTaxonomy" }
		"/register/$action?" { controller = "register"; plugin = "httcUser" }

		name rateAnswer: "/question/$questionId/answer/$answerId/rate"{
			controller = "question"
			action = "rateAnswer"
			constraints {
				// apply constraints here
			}
		}

		name acceptAnswer: "/question/$questionId/answer/$answerId/accept"{
			controller = "question"
			action = "acceptAnswer"
			constraints {
				// apply constraints here
			}
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
