class UrlMappings {

	static mappings = {
        "/repository/view/$id/$file**?(.$format)?"{
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
