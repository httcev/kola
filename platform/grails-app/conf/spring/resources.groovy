// Place your Spring DSL code here
beans = {
	hashIds(org.hashids.Hashids) { bean ->
        bean.constructorArgs = [ 'some random salt!!' ]
    }

	repoDir(java.io.File) { bean ->
        bean.constructorArgs = [ application.config.kola.repository.directory ]
    }

    couchdbProtocol(java.lang.String) { bean ->
        bean.constructorArgs = [ application.config.kola.couchdb.protocol ]
    }
    couchdbHost(java.lang.String) { bean ->
        bean.constructorArgs = [ application.config.kola.couchdb.host ]
    }
    couchdbPort(java.lang.String) { bean ->
        bean.constructorArgs = [ application.config.kola.couchdb.port ]
    }
	couchdbAdminUser(java.lang.String) { bean ->
        bean.constructorArgs = [ application.config.kola.couchdb.admin.user ]
    }
	couchdbAdminPass(java.lang.String) { bean ->
        bean.constructorArgs = [ application.config.kola.couchdb.admin.pass ]
    }
}
