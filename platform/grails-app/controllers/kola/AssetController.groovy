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
import org.springframework.web.context.request.RequestContextHolder
import kola.ZipUtil

@Transactional
@Secured(['ROLE_ADMIN', 'ROLE_REPOSITORY_ADMIN'])
class AssetController {
	def assetService

    //static allowedMethods = [save: "POST", update: ["PUT", "POST"], delete: "DELETE"]

    @Secured(['IS_AUTHENTICATED_REMEMBERED'])
    def index(Integer max) {
        params.offset = params.offset ? (params.offset as int) : 0
        params.max = Math.min(max ?: 10, 100)
        params.sort = params.sort ?: "lastUpdated"
        params.order = params.order ?: "desc"
        def query = Asset.where { subType == "learning-resource" && deleted == false }
        respond query.list(params), model:[assetInstanceCount: query.count()]
    }

    @Secured(['IS_AUTHENTICATED_REMEMBERED'])
    def show(Asset assetInstance) {
        if (assetInstance == null) {
            notFound()
            return
        }
        respond assetInstance
    }

    def createFlow = {
        initiliaze {
			action {
				flow.cmd = new CreateAssetCommand()
           	}
           	on("success").to "uploadOrLink"
           	on(Exception).to "error"
        }
        
        uploadOrLink {
        	on("submit") {
        		bindData(flow.cmd, params)
        		if (!flow.cmd.content && !flow.cmd.externalUrl) {
	        		flow.cmd.errors.rejectValue('content', 'nullable')
	        		flow.cmd.errors.rejectValue('externalUrl', 'nullable')
        			error()
        		}
    		}.to "checkUploadOrLink"
        }
        checkUploadOrLink {
        	action {
        		try {
		        	enrichAsset(flow.cmd)
		        	if (flow.cmd.content && "application/zip".equals(flow.cmd.mimeType?.toLowerCase())) {
						ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(flow.cmd.content)))
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
		        	if (flow.cmd.externalUrl) {
		        		flow.cmd.errors.rejectValue('externalUrl', 'urlNotValid')
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
        		bindData(flow.cmd, params)
    		}.to "metadata" 
        }
        metadata {
        	on("submit") {
        		bindData(flow.cmd, params)
                if (!flow.cmd.validate()) {
                    return error()
                }
                def assetInstance = new Asset()
                assetInstance.properties = flow.cmd
                //println "---saved instance: " + assetInstance.indexText


                assetInstance.validate()
                assetInstance.errors.allErrors.each { println it }

                if (assetInstance.save(true)) {
                    RequestContextHolder.currentRequestAttributes().flashScope.message = message(code: 'default.created.message', args: [message(code: 'kola.asset', default: 'Asset'), assetInstance.name])
                    // need to clear the flow's persistence context, otherwise we get a NotSerializableException for the newly saved Asset
                    flow.persistenceContext.clear()
                    return success()
                }
                else {
                    return error()  
                }
                return success()
    		}.to "finish"
        }
        finish {
            redirect(action:"index")
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

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'kola.asset', default: 'Asset'), assetInstance.name])
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

        assetInstance.deleted = true
        assetInstance.save flush:true
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'kola.asset', default: 'Asset'), assetInstance.name])
        redirect action:"index", method:"GET"
    }

    @Secured(['permitAll'])
    def viewAsset(String id, String file) {
    	def asset = assetService.readAsset(id)
    	if (!asset) {
            println "--- no asset with id " + id
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
        if (!repoFile) {
            println "--- no content for attachment with id " + id
            response.sendError(404)
            return
        }
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
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'kola.asset', default: 'Asset'), params.id])
        redirect action: "index", method: "GET"
    }

    protected void enrichAsset(assetOrCommand) {
        def detector = TikaConfig.getDefaultConfig().getDetector();
        Metadata metadata = new Metadata()
        def inputStream
        if (assetOrCommand.content || assetOrCommand.externalUrl) {
	        try {
		        if (assetOrCommand.content) {
		            // remove externalUrl since we're now expecting a local asset
		            assetOrCommand.externalUrl = null
                    def uploadFile = request.getFile("content")
                    if (uploadFile) {
    		            assetOrCommand.filename = uploadFile.getOriginalFilename()
                    }

		            inputStream = new ByteArrayInputStream(assetOrCommand.content)
		            metadata.add(Metadata.RESOURCE_NAME_KEY, assetOrCommand.filename)
		        }
		        else {
		            inputStream = new BufferedInputStream(new URL(assetOrCommand.externalUrl).openStream())
		        }

	            MediaType mediaType = detector.detect(inputStream, metadata)
	            assetOrCommand.mimeType = mediaType.toString()

	            def titleAndText = extractText(inputStream, assetOrCommand.mimeType)
	            if (titleAndText.title && !assetOrCommand.name) {
	            	assetOrCommand.name = titleAndText.title
	            }
	            if (titleAndText.text) {
	            	assetOrCommand.indexText = titleAndText.text
	            }
	        }
	        finally {
	            if (inputStream) {
	                inputStream.close()
	            }
	        }

	        if (!assetOrCommand.mimeType) {
	            assetOrCommand.mimeType = "application/octet-stream"
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

@grails.validation.Validateable
class CreateAssetCommand implements Serializable {
    private static final long serialVersionUID = 42L;

    static constraints = {
        // Limit upload file size to 100MB
        content maxSize: 1024 * 1024 * 100, nullable:true
        externalUrl nullable: true
        anchor nullable: true
        name blank:false
        description blank:false
        filename nullable: true
        indexText nullable: true
    }

    String name
    String description
    String mimeType
    String subType = "learning-resource"

    // only external assets
    String externalUrl

    // only local assets
    byte[] content
    String filename
    // only for local zip files
    String anchor

    String indexText
}