/*
eventConfigureTomcat = {tomcat ->
    def connector = new org.apache.catalina.connector.Connector("org.apache.coyote.http11.Http11NioProtocol")
    connector.port = System.getProperty("server.port", "8080").toInteger()
    connector.redirectPort = 8443
    connector.protocol = "HTTP/1.1"
    connector.maxPostSize = 0
 
    tomcat.connector = connector
    tomcat.service.addConnector connector
}
*/