package kola

import org.apache.commons.io.IOUtils

class RepositoryController {
	def hashIds
	def repoDir

    def show(String id) {
    	println "encode=" + hashIds.encode(2)
    	println "--- input=$id"
    	def hashId = hashIds.decode(id)
    	if (!hashId) {
			response.sendError(404)
			return
    	}
    	def decodedId = hashId[0]
    	println "--- decoded=$decodedId"

    	def asset = Asset.read(decodedId)
    	if (!asset) {
			response.sendError(404)
			return
    	}

    	if (asset.externalUrl) {
    		// external asset
    		response.sendRedirect(asset.externalUrl)
    		return
    	}

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
