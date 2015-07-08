package kola

import org.apache.commons.io.IOUtils
import org.apache.commons.io.FileUtils
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.apache.tika.mime.MediaType
import org.apache.tika.metadata.Metadata
import org.apache.tika.config.TikaConfig
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.xml.sax.ContentHandler;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.Files;

import kola.ZipUtil

@Transactional(readOnly = true)
class RepositoryController {
    def hashIds
	def repoDir

    static allowedMethods = [save: "POST", update: ["PUT", "POST"], delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Asset.list(params), model:[assetInstanceCount: Asset.count()]
    }

    def show(Asset assetInstance) {
    	println  Asset.search("klick").searchResults
        [assetInstance:assetInstance, encodedId:encodeId(assetInstance.id)]
    }
/*
    def create() {
        respond new Asset(params)
    }
    */

    def createFlow = {
    	
        initiliaze {
			action {
				flow.assetInstance = new Asset(params)
           	}
           	on("success").to "uploadOrLink"
           	on(Exception).to "error"
        }
        
        uploadOrLink {
        	on("submit") {
        		bindData(flow.assetInstance, params)
        		if (!flow.assetInstance.content && !flow.assetInstance.externalUrl) {
	        		flow.assetInstance.errors.rejectValue('content', 'nullable')
	        		flow.assetInstance.errors.rejectValue('externalUrl', 'nullable')
        			error()
        		}
    		}.to "checkUploadOrLink"
        }
        checkUploadOrLink {
        	action {
        		try {
		        	enrichAsset(flow.assetInstance)
		        	if (flow.assetInstance.content && "application/zip".equals(flow.assetInstance.mimeType)) {
		        		println "--- LOCAL ZIP"
						ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(flow.assetInstance.content)))
						def possibleAnchors = ZipUtil.getFilenames(zin, false)
						if (possibleAnchors.length > 0) {
							flow.possibleAnchors = possibleAnchors
							return chooseAnchor()
						}

		        	}
			        return metadata()
		        }
		        catch(e) {
		        	if (flow.assetInstance.externalUrl) {
		        		flow.assetInstance.errors.rejectValue('externalUrl', 'urlNotValid')
		        		println "----------------------------"
		        		        flow.assetInstance.errors.allErrors.each { println it }

		        	}
		        	return error()
		        }
        	}
        	on("chooseAnchor").to "chooseAnchor"
        	on("metadata").to "metadata"
        	on("error").to "uploadOrLink"
        }
        chooseAnchor {
        	on("submit") {
        		printl "--- INCOMING ANCHOR IS " +params.anchor
        		bindData(flow.assetInstance, params)
        		println "--- NEW ANCHOR IS " + flow.assetInstance.anchor 
    		}.to "metadata" 
        }
        metadata {
        	on("submit") {
        		bindData(flow.assetInstance, params)
        		flow.assetInstance.save() ? success() : error()
    		}.to "finish"
        }
        finish {
        	redirect(action: "index")
        }
    }

    @Transactional
    def save(Asset assetInstance) {
        if (assetInstance == null) {
            notFound()
            return
        }

        enrichAsset(assetInstance)

        if (assetInstance.hasErrors()) {
            respond assetInstance.errors, view:'create'
            return
        }

        assetInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'asset.label', default: 'Asset'), assetInstance.id])
                redirect action:"show", id:assetInstance.id
            }
            '*' { respond assetInstance, [status: CREATED] }
        }
    }

    def edit(Asset assetInstance) {
        respond assetInstance
    }

    @Transactional
    def update(Asset assetInstance) {
        if (assetInstance == null) {
            notFound()
            return
        }

        enrichAsset(assetInstance)

        if (assetInstance.hasErrors()) {
            respond assetInstance.errors, view:'edit'
            return
        }

        assetInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Asset.label', default: 'Asset'), assetInstance.id])
                redirect action:"show", id:assetInstance.id
            }
            '*'{ respond assetInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Asset assetInstance) {

        if (assetInstance == null) {
            notFound()
            return
        }

        assetInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Asset.label', default: 'Asset'), assetInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    def view(String id, String file) {
    	def hashId = hashIds.decode(id)
    	if (!hashId) {
			response.sendError(404)
			return
    	}
    	def decodedId = hashId[0]
    	log.info "viewing asset $decodedId"
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
		if ("application/zip" == contentType && asset.anchor) {
			// handle special case: zip files
			viewZipFile(asset, file, repoFile)
			return
		}
		if (repoFile.exists() && repoFile.isDirectory()) {
			log.info "deleting repo dir " + repoDir.getName()
			FileUtils.deleteDirectory(repoFile)
		}
    	if (!repoFile.exists()) {
    		// non-zip files
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
    	render(file:repoFile, contentType:contentType, fileName:asset.filename)
    }

    def encodeId(long id) {
    	return hashIds.encode(id)
    }

    protected void viewZipFile(Asset asset, String file, File repoFile) {
    	// check if zip has been extracted
    	if (repoFile.exists() && !repoFile.isDirectory()) {
    		repoFile.delete()
    	}
    	if (!repoFile.exists()) {
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(asset.content)))
			ZipUtil.unzip(zin, repoFile)
    	}
    	if (!file) {
    		log.debug "redirecting to anchor " + asset.anchor
    		redirect(uri: "/repository/view/" + encodeId(asset.id) + "/" + asset.anchor)
    		return
    	}
    	File zipEntry = new File(repoFile, file)
    	if (!zipEntry.exists()) {
    		render status: NOT_FOUND
    		return
    	}
    	def contentType = Files.probeContentType(zipEntry.toPath())
    	render(file:zipEntry, contentType:contentType)
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'asset.label', default: 'Asset'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    protected void enrichAsset(Asset assetInstance) {
        def detector = TikaConfig.getDefaultConfig().getDetector();
        Metadata metadata = new Metadata()
        def inputStream

        try {
	        if (assetInstance.content) {
	            // remove externalUrl since we're now expecting a local asset
	            assetInstance.externalUrl = null
	            assetInstance.filename = request.getFile("content").getOriginalFilename()

	            inputStream = new ByteArrayInputStream(assetInstance.content)
	            metadata.add(Metadata.RESOURCE_NAME_KEY, assetInstance.filename)
	        }
	        else {
	            inputStream = new BufferedInputStream(new URL(assetInstance.externalUrl).openStream())
	        }

            MediaType mediaType = detector.detect(inputStream, metadata)
            assetInstance.mimeType = mediaType.toString()
/*
            def titleAndText = extractText(inputStream, assetInstance.mimeType)
            if (titleAndText.title && !assetInstance.name) {
            	assetInstance.name = titleAndText.title
            }
            if (titleAndText.text) {
            	assetInstance.indexText = titleAndText.text
            }
*/            
        }
        finally {
            if (inputStream) {
                inputStream.close()
            }
        }

        if (!assetInstance.mimeType) {
            assetInstance.mimeType = "application/octet-stream"
        }
        /*
        assetInstance.validate()
        println "--- MIME IS " + assetInstance.mimeType
        assetInstance.errors.allErrors.each { println it }
        */
    }

    protected Object extractText(InputStream inputStream, String mimeType) {
		StringWriter writer = new StringWriter();
		ContentHandler handler;
		if (mimeType.toLowerCase().indexOf("html") > -1) {
			// BoilerpipeContentHandler extracts the "main" content from HTML pages
			handler = new BoilerpipeContentHandler(new BodyContentHandler(writer));
		}
		else {
			handler = new WriteOutContentHandler(writer);
		}

		Metadata metadata = new Metadata();
		metadata.add(Metadata.CONTENT_TYPE, mimeType);

		AutoDetectParser parser = new AutoDetectParser();
		parser.parse(inputStream, handler, metadata);
    	[title:metadata.get(TikaCoreProperties.TITLE), text:writer.toString().trim()]
    }
}
