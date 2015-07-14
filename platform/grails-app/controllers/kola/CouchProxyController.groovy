package kola

import org.apache.http.*
import org.apache.http.client.*
import org.apache.http.entity.*
import org.apache.http.impl.client.*
import org.apache.http.message.*
import org.springframework.security.access.annotation.Secured

class CouchProxyController {
    def static FILTERED_REQUEST_HEADERS = ["authorization", "cookie", "content-length"]
	def springSecurityService
    def couchdbProtocol
    def couchdbHost
    def couchdbPort

    // TODO: 301 from couch does not work (example: GET /_utils)
	@Secured(['ROLE_USER'])
    def proxy() {
    	def path = request.forwardURI.replace(request.contextPath + "/db", "")
    	def query = request.queryString ? "?" + request.queryString : ""
    	def hcRequest = new BasicHttpEntityEnclosingRequest(request.method, path + query)
    	request.headerNames.each {
    		if (!FILTERED_REQUEST_HEADERS.contains(it.toLowerCase())) {
//	    		println "--- setting request header '$it' to " + request.getHeader(it)
    			hcRequest.setHeader(it, request.getHeader(it))
    		}
    	}
        hcRequest.setHeader("X-Auth-CouchDB-UserName", springSecurityService.principal.username)
        hcRequest.setHeader("X-Auth-CouchDB-Roles", springSecurityService.principal.authorities?.join(","))
        if (request.JSON) {
	        hcRequest.setEntity(new StringEntity(request.JSON.toString()))
	    }
//        println hcRequest

    	def host = new HttpHost(couchdbHost, couchdbPort as int, couchdbProtocol)
    	def client = new HttpClientBuilder().create().build()
    	client.execute(host, hcRequest, new ResponseHandler() {
    		def handleResponse(HttpResponse hcResponse) {
    			println hcResponse
    			response.status = hcResponse.statusLine.statusCode
//    			println "status=" + response.status
    			hcResponse.allHeaders.each {
//	    		println "--- setting response header '$it.name' to '$it.value'"
    				response.setHeader(it.name, it.value)
    			}
    			response.outputStream << hcResponse.entity?.content
    			response.outputStream.flush()
    		}
    	})
    }

    /*
		Let OPTIONS requests path through unauthenticated
    */
	@Secured(['permitAll'])
    def optionsRequest() {
    	proxy()
    }
}
