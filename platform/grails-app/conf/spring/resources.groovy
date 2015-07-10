// Place your Spring DSL code here
beans = {
	hashIds(org.hashids.Hashids) { bean ->
        bean.constructorArgs = [ 'some random salt!!' ]
    }

	repoDir(java.io.File) { bean ->
        bean.constructorArgs = [ application.config.kola.repository.directory ]
    }

	couchdbUrl(java.lang.String) { bean ->
        bean.constructorArgs = [ application.config.kola.couchdb.url ]
    }
	couchdbAdminUser(java.lang.String) { bean ->
        bean.constructorArgs = [ application.config.kola.couchdb.admin.user ]
    }
	couchdbAdminPass(java.lang.String) { bean ->
        bean.constructorArgs = [ application.config.kola.couchdb.admin.pass ]
    }
}
