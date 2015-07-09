class UrlMappings {

	static mappings = {
        name viewAsset: "/v/$id"{
            controller = "repository"
            action = "view"
            constraints {
                // apply constraints here
            }
        }
        "/v/$id/$file**?(.$format)?"{
            controller = "repository"
            action = "view"
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
