// Place your Spring DSL code here
beans = {
	hashIds(org.hashids.Hashids) { bean ->
        bean.constructorArgs = [ 'some random salt!!' ]
    }

	repoDir(java.io.File) { bean ->
        bean.constructorArgs = [ application.config.kola.repository.directory ]
    }
}
