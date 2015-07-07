package kola

import org.apache.commons.io.IOUtils

class RepositoryController {
	def hashIds
	def repoDir

    def show(String id) {
    	println "encode=" + hashIds.encode(1)
    	println "--- input=$id"
    	def decodedId = hashIds.decode(id)[0]
    	println "--- decoded=$decodedId"

    	// check if 

    	def asset = Asset.read(decodedId)
    	if (!asset) {
			response.sendError(404)
    	}
    	else {
	    	if (asset.externalUrl) {
	    		// external asset
	    		response.sendRedirect(asset.externalUrl)
	    	}
	    	else {
	    		// local asset
	    		def contentType = asset.mimeType
		    	def repoFile = new File(repoDir, decodedId as String)
		    	if (!repoFile.exists()) {
		    		def input = new ByteArrayInputStream(asset.content)
		    		def output = new FileOutputStream(repoFile)
		    		try {
		    			IOUtils.copy(input, output)
		    		}
		    		finally {
		    			input.close()
		    			output.close()
		    		}
		    	}
		    	render(file:repoFile, contentType:contentType)

		    	println "show $decodedId"
		    }
	    }
    }
}
