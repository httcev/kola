class UrlMappings {

	static mappings = {
        "/api/db/**?"{
            controller = "couchProxy"
            action = [OPTIONS:"proxyOptionsRequest", GET:"proxy", POST:"proxy", PUT:"proxy", DELETE:"proxy", PATCH:"proxy", TRACE:"proxy", HEAD:"proxy"]
        }
        "/api/changes"{
            controller = "changes"
            action = [GET:"index", POST:"index"]
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
