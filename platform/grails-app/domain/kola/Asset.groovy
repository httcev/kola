package kola

class Asset {
	def hashIds

	static searchable = {
        only = ['name', 'description', 'mimeType', 'externalUrl', 'filename', 'anchor', 'indexText']
    }
    static constraints = {
    	// Limit upload file size to 100MB
        content maxSize: 1024 * 1024 * 100, nullable:true
        externalUrl nullable: true
        anchor nullable: true
        filename nullable: true
        indexText nullable: true
    }
    static mapping = {
		content lazy: true
    }
    static transients = ['encodeId', 'hashIds', 'indexText']

    String name
    String description
    String mimeType
    String type = "learning-resource"

    // only external assets
    String externalUrl

    // only local assets
    byte[] content
    String filename
    // only for local zip files
    String anchor

    String indexText

    String encodeId() {
    	hashIds.encode(id)
    }
}
