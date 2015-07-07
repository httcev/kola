package kola

class Asset {

    static constraints = {
    	// Limit upload file size to 100MB
        content maxSize: 1024 * 1024 * 100
        externalUrl nullable: true
        anchor nullable: true
    }
    static mapping = {
		content lazy: true
    }

    String name
    String description
    String mimeType
    String type = "learning-resource"

    // only external assets
    String externalUrl

    // only local assets
    byte[] content
    // only for local zip files
    String anchor
}