class UrlMappings {

	static mappings = {
        "/db/**?"{
            controller = "couchProxy"
            action = [OPTIONS:"proxyOptionsRequest", GET:"proxy", POST:"proxy", PUT:"proxy", DELETE:"proxy", PATCH:"proxy", TRACE:"proxy", HEAD:"proxy"]
        }

        name viewAsset: "/v/$id"{
            controller = "repository"
            action = "viewAsset"
            constraints {
                // apply constraints here
            }
        }
        name viewAssetFile: "/v/$id/$file**"{
            controller = "repository"
            action = "viewAsset"
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
