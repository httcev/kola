import kola.Asset

class BootStrap {
	def repoDir

    def init = { servletContext ->
    	if (!repoDir.exists()) {
    		repoDir.mkdirs()
    	}

    	def asset = new Asset(name:"Asset 1", description:"Huhu sfsdf", mimeType:"text/plain", content:"Das ist ein Text!" as byte[])
    	if (!asset.save(true)) {
			asset.errors.allErrors.each { println it }
    	}
    }
    def destroy = {
    }
}
