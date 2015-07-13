class UrlMappings {

	static mappings = {
        "/db/**"{
            controller = "couchProxy"
            action = [GET:"index", OPTIONS:"optionsRequest", POST:"index", HEAD:"index", PUT:"index", DELETE:"index"]
        }
        "/db"{
            controller = "couchProxy"
            action = [GET:"index", OPTIONS:"optionsRequest", POST:"index", HEAD:"index", PUT:"index", DELETE:"index"]
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

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
