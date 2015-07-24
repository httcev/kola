package kola

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
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.Files;
import org.springframework.security.access.annotation.Secured

import kola.ZipUtil

// Non "readOnly" Transactional is needed for the "create" Webflow to work
@Transactional
@Secured(['ROLE_USER'])
class RepositoryController {
	def assetService

    static allowedMethods = [save: "POST", update: ["PUT", "POST"], delete: "DELETE"]

    def index(Integer max) {
            log.warn "-------------------------------------------------- MESSAGE2"
        params.max = Math.min(max ?: 10, 100)
        params.sort = params.sort ?: "lastUpdated"
        params.order = params.order ?: "desc"
        respond Asset.list(params), model:[assetInstanceCount: Asset.count()]
    }

    def show(Asset assetInstance) {
        respond assetInstance
    }

/*
    def create() {
        respond new Asset(params)
    }
    */

    def createFlow = {
        initiliaze {
			action {
				flow.assetInstance = new Asset()
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
		        	if (flow.assetInstance.content && "application/zip".equals(flow.assetInstance.mimeType?.toLowerCase())) {
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
                    log.error e
		        	if (flow.assetInstance.externalUrl) {
		        		flow.assetInstance.errors.rejectValue('externalUrl', 'urlNotValid')
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
        		bindData(flow.assetInstance, params)
    		}.to "metadata" 
        }
        metadata {
        	on("submit") {
        		bindData(flow.assetInstance, params)
                if (flow.assetInstance.save(true)) {
                    println "--- assetService 2=" + assetService
                    assetService.deleteRepositoryFile(flow.assetInstance)
                    println "- FINISH DELETE"
                    return success()
                }
                else {
                    return error()  
                }
    		}.to "finish"
        }
        finish {
            redirect(controller:"repository", action:"index")
        }
    }
/*
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
*/
    def edit(Asset assetInstance) {
        println "-- HERE"
        if (assetInstance == null) {
            notFound()
            return
        }
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
        assetService.deleteRepositoryFile(assetInstance)

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

    @Secured(['permitAll'])
    def viewAsset(String id, String file) {
    	def asset = assetService.readAsset(id)
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
    	def repoFile = assetService.getOrCreateRepositoryFile(asset)
		if (repoFile.isDirectory()) {
			// handle special case: zip files
			viewZipFile(asset, file, repoFile)
			return
		}
    	renderFile(repoFile, asset.mimeType)
    }

    protected void viewZipFile(Asset asset, String file, File repoFile) {
    	if (!file) {
    		log.debug "redirecting to anchor " + asset.anchor
    		redirect(url: assetService.createEncodedLink(asset, asset.anchor))
    		return
    	}
    	File zipEntry = new File(repoFile, file)
    	if (!zipEntry.exists()) {
    		render status: NOT_FOUND
    		return
    	}
    	renderFile(zipEntry, Files.probeContentType(zipEntry.toPath()))
    }

    protected void renderFile(File file, String contentType) {
		response.setHeader("Accept-Ranges", "bytes")
		String range = request.getHeader("range")
		if (range != null && range.length() > 0) {
			def matcher = range =~ /bytes=(\d+)-(\d*)/
			def start = matcher[0][1].toInteger()
			def end = matcher[0][2]
			def fileLength = file.length()
			if (!end) {
				end = fileLength - 1
			}
			else {
				end = end.toInteger()
			}
			// check bounds and conditionally return status 416 ("Requested Range not satisfiable")
			if (end < start || start < 0 || end < 0 || end >= fileLength) {
				response.status = 416
				return
			}
			def length = end - start + 1
			response.status = 206
			response.contentLength = length
			response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength)
			// TODO: try to not allocate array in memory
			RandomAccessFile raf = new RandomAccessFile(file, "r")
			raf.seek(start)
			byte[] buf = new byte[length]
			raf.readFully(buf)

			//response.outputStream << buf
    		render(file:buf, contentType:contentType)
		}
		else {
    		render(file:file, contentType:contentType)
	    }
    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'asset.label', default: 'Asset'), params.id])
        redirect action: "index", method: "GET"
    }

    protected void enrichAsset(Asset assetInstance) {
        def detector = TikaConfig.getDefaultConfig().getDetector();
        Metadata metadata = new Metadata()
        def inputStream
        if (assetInstance.content || assetInstance.externalUrl) {
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

	            def titleAndText = extractText(inputStream, assetInstance.mimeType)
	            if (titleAndText.title && !assetInstance.name) {
	            	assetInstance.name = titleAndText.title
	            }
	            if (titleAndText.text) {
	            	assetInstance.indexText = titleAndText.text
	            }
	        }
	        finally {
	            if (inputStream) {
	                inputStream.close()
	            }
	        }

	        if (!assetInstance.mimeType) {
	            assetInstance.mimeType = "application/octet-stream"
	        }
	    }
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
