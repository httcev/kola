import kola.Asset

class BootStrap {
	def repoDir

    def init = { servletContext ->
    	if (!repoDir.exists()) {
    		repoDir.mkdirs()
    	}

    	def asset = new Asset(name:"Asset 1", description:"Huhu sfsdf", mimeType:"application/pdf", content:[1,2,3] as byte[])
    	if (!asset.save(true)) {
			asset.errors.allErrors.each { println it }
    	}
    }
    def destroy = {
    }
}
