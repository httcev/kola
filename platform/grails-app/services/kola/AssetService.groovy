package kola

import kola.Asset
import grails.transaction.Transactional
import java.io.File
import java.util.zip.ZipInputStream
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

@Transactional(readOnly = true)
class AssetService {
	def grailsLinkGenerator
    def hashIds
	def repoDir

    def readAsset(encodedId) {
    	def id = hashIds.decode(encodedId)
    	if (id) {
	    	id = id[0]
	    	return Asset.read(id)
    	}
    }

    def createEncodedLink(asset, file=null) {
    	def link
    	if (file) {
	    	link = grailsLinkGenerator.link(mapping:"viewAssetFile", id:hashIds.encode(asset.id), params:[file:file], absolute:true)
    	}
    	else {
	    	link = grailsLinkGenerator.link(mapping:"viewAsset", id:hashIds.encode(asset.id), absolute:true)
	    }
	    println link
	    return link
    }

    def getOrCreateRepositoryFile(asset) {
    	def file = getRepositoryFile(asset)
    	if (!file.exists()) {
    		if (asset.mimeType == "application/zip" && asset.anchor) {
				ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(asset.content)))
				ZipUtil.unzip(zin, file)
    		}
    		else {
				// non-zip files
				def input = new ByteArrayInputStream(asset.content)
				def output = new FileOutputStream(file)
				try {
					IOUtils.copy(input, output)
				}
				finally {
					input.close()
					output.close()
				}
			}
		}
		return file
    }

    def getRepositoryFile(asset) {
    	/*
    	if (asset.externalUrl) {
    		throw new IOException("asset is not local")
    	}
    	*/
        if (asset?.id) {
        	return new File(repoDir, asset.id as String)
        }
    }

    def deleteRepositoryFile(asset) {
    	def file = getRepositoryFile(asset)
    	if (file?.exists()) {
    		log.debug "deleting repository file " + file.getName()
    		if (file.isDirectory()) {
    			FileUtils.deleteDirectory(file)
    		}
    		else {
    			file.delete()
    		}
    	}
    }
}
