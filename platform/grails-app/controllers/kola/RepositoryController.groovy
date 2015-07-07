package kola

import org.apache.commons.io.IOUtils
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

@Transactional(readOnly = true)
class RepositoryController {
    def hashIds
	def repoDir

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Asset.list(params), model:[assetInstanceCount: Asset.count()]
    }

    def show(Asset assetInstance) {
    	println  Asset.search("described").searchResults
        respond assetInstance
    }

    def create() {
        println "--- CREATE"
        respond new Asset(params)
    }
/*
    def createFlow = {
        init {
           action {
              [assetInstance: new Asset()]
           }
           on("success").to "uploadOrLink"
           on(Exception).to "handleError"
        }
        uploadOrLink {

        }
        metadata ()
        //respond new Asset(params)

    }
*/
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
                redirect assetInstance
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

    def download(String id) {
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
    	render(file:repoFile, contentType:contentType, fileName:asset.filename)

    	println "show $decodedId"
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

	            //println assetInstance.properties
	        }
	        else {
	            inputStream = new BufferedInputStream(new URL(assetInstance.externalUrl).openStream())
	            println "opened " + assetInstance.externalUrl
	        }

            MediaType mediaType = detector.detect(inputStream, metadata)
            assetInstance.mimeType = mediaType.toString()
            println "--- SET MIME TO " + assetInstance.mimeType

            def titleAndText = extractText(inputStream, assetInstance.mimeType)
            if (titleAndText.title && !assetInstance.name) {
            	println "--- SETTING TITLE TO " + titleAndText.title
            	assetInstance.name = titleAndText.title
            }
            if (titleAndText.text) {
            	println "--- SETTING TEXT TO " + titleAndText.text
            	assetInstance.indexText = titleAndText.text
            }
        }
        catch(e) {
            println e
        }
        finally {
            if (inputStream) {
                inputStream.close()
            }
        }

        if (!assetInstance.mimeType) {
            assetInstance.mimeType = "application/octet-stream"
        }
        assetInstance.validate()
        println "--- MIME IS " + assetInstance.mimeType
        assetInstance.errors.allErrors.each { println it }
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
