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

        name viewAsset: "/v/$id"{
            controller = "asset"
            action = "viewAsset"
            constraints {
                // apply constraints here
            }
        }
        name viewAssetFile: "/v/$id/$file**"{
            controller = "asset"
            action = "viewAsset"
            constraints {
                // apply constraints here
            }
        }
/*
        "/tasks"(resources:"task") {
            "/steps"(resources:"taskStep")
        }
*/
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller:"index")
        "500"(view:'/error')
	}
}
