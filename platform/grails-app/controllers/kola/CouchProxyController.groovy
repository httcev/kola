package kola

import groovyx.net.http.*
import org.springframework.security.access.annotation.Secured

class CouchProxyController {
	def springSecurityService

	@Secured(['ROLE_USER'])
    def synchronized index() {
    	def outerResponse = response
    	def outerRequest = request
//    	def http = new HTTPBuilder("http://koladb.httc.de")
    	def http = new HTTPBuilder("http://10.0.17.7:5984")
    	def origin = request.getHeader("origin")

println "----------------------------------------"
println "METHOD=" + request.method
	    http.request(request.method as Method) {
	        uri.path = outerRequest.forwardURI.replace(outerRequest.contextPath + "/db", "")
	       // uri.query = outerRequest.queryString
	        println "PATH=" + uri.path
	        uri.rawQuery = outerRequest.queryString

	    	outerRequest.headerNames.each {
	    		if (!it?.equalsIgnoreCase("authorization")) {
		    		println "--- setting request header '$it' to " + outerRequest.getHeader(it)
	    			headers[it] = outerRequest.getHeader(it)
	    		}
	    	}
	        headers.'X-Auth-CouchDB-UserName' = springSecurityService.principal.username
	        headers.'X-Auth-CouchDB-Roles' = springSecurityService.principal.authorities?.join(",")
/*
	        // add possible headers
	        requestHeaders.each { key, value ->
	            headers."${key}" = "${value}"
	        }
*/
	        // response handler for success and failure
	        def responseHandler = { resp, reader ->
	        	// copy response status
				outerResponse.status = resp.status
	        	// copy response headers
		    	resp.allHeaders?.each {
		    		println "--- setting response header '$it.name' to " + it.value
		    		outerResponse.setHeader(it.name, it.value)
		    	}
		    	resp.entity.properties.each {
		    		println it
		    	}
				outerResponse.outputStream << resp.entity.content
	        }
	        response.success = responseHandler
	        response.failure = responseHandler
	    }
    }

    @Secured(['permitAll'])
    def optionsRequest() {
    	println "--- OPTIONS!!!"
    	//index()
    	def origin = request.getHeader("origin")
    	println "origin=" + origin
    	response.status = 200
    	response.setHeader("Access-Control-Allow-Origin", origin == null ? "*" : origin)
		response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, HEAD, OPTIONS, DELETE, PATCH")
		response.setHeader("Access-Control-Allow-Headers", "accept, authorization, content-type, origin, referer, x-csrf-token, x-requested-with")
		response.setHeader("Access-Control-Allow-Credentials", "true")
		response.outputStream.flush()
    }
}
