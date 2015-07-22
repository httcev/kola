package kola

import org.apache.http.*
import org.apache.http.client.*
import org.apache.http.entity.*
import org.apache.http.impl.client.*
import org.apache.http.message.*
import org.apache.http.params.*
import org.springframework.security.access.annotation.Secured
import org.apache.commons.codec.binary.Hex
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac

class CouchProxyController {
    def static FILTERED_REQUEST_HEADERS = ["authorization", "cookie", "content-length"]
	def springSecurityService
    def couchdbProtocol
    def couchdbHost
    def couchdbPort
    def couchdbSecret

    // TODO: 301 from couch does not work (example: GET /_utils)
	@Secured(['ROLE_USER'])
    def proxy() {
    	def path = request.forwardURI.replace(request.contextPath + "/db", "")
    	def query = request.queryString ? "?" + request.queryString : ""
    	def user = springSecurityService.principal?.username
    	def hcRequest = new BasicHttpEntityEnclosingRequest(request.method, path + query)
    	request.headerNames.each {
    		if (!FILTERED_REQUEST_HEADERS.contains(it.toLowerCase())) {
//	    		println "--- setting request header '$it' to " + request.getHeader(it)
    			hcRequest.setHeader(it, request.getHeader(it))
    		}
    	}
    	if (user) {
	        hcRequest.setHeader("X-Auth-CouchDB-UserName", user)
	        hcRequest.setHeader("X-Auth-CouchDB-Roles", springSecurityService.principal?.authorities?.join(","))
	        hcRequest.setHeader("X-Auth-CouchDB-Token", hmacSha1(couchdbSecret, user))
	    }
        if (request.JSON) {
	        hcRequest.setEntity(new StringEntity(request.JSON.toString()))
	    }
        // prevent any redirect handling by apache http client
        HttpParams params = new BasicHttpParams()
        params.setParameter("http.protocol.handle-redirects", false)
        hcRequest.setParams(params)

    	def host = new HttpHost(couchdbHost, couchdbPort as int, couchdbProtocol)
    	def client = new HttpClientBuilder().create().build()
    	client.execute(host, hcRequest, new ResponseHandler() {
    		def handleResponse(HttpResponse hcResponse) {
    			response.status = hcResponse.statusLine.statusCode
//    			println "status=" + response.status
    			hcResponse.allHeaders.each {
//	    		println "--- setting response header '$it.name' to '$it.value'"
                    def value = it.value
                    if ("location".equalsIgnoreCase(it.name)) {
                        println "___ LOC INPUT=" + value
                        // rewrite "location" header to point to this controller
                        def uri = new URI(value)
                        value = uri.scheme + "://" + uri.host + ":" + uri.port + request.contextPath + "/db" + uri.path
                        if (uri.query) {
                            value += "?" + uri.query
                        }
                        println "___ LOC OUTPUT=" + value
                    }
    				response.setHeader(it.name, value)
    			}
    			response.outputStream << hcResponse.entity?.content
    			response.outputStream.flush()
    		}
    	})
    }

    /*
		Let OPTIONS requests pass through unauthenticated
    */
	@Secured(['permitAll'])
    def proxyOptionsRequest() {
    	proxy()
    }

    private String hmacSha1(String secret, String user) {
        // Get an hmac_sha1 key from the raw key bytes
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes("utf-8"), "HmacSHA1");
        // Get an hmac_sha1 Mac instance and initialize with the signing key
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        // Compute the hmac on input data bytes
        byte[] rawHmac = mac.doFinal(user.getBytes("utf-8"));
        // Convert raw bytes to Hex
        byte[] hexBytes = new Hex().encode(rawHmac);
        //  Covert array of Hex bytes to a String
        return new String(hexBytes, "UTF-8");
    }
}
