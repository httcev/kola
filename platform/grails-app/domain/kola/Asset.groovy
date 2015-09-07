package kola

class Asset {
    def assetService

	static searchable = {
		all = [analyzer: 'german']
        only = ['name', 'description', 'mimeType', 'subType', 'externalUrl', 'filename', 'anchor', 'indexText']
        name boost:3.0
        description boost:2.0
        subType index:"not_analyzed"
    }
    static constraints = {
    	// Limit upload file size to 100MB
        content maxSize: 1024 * 1024 * 100, nullable:true
        externalUrl nullable: true
        anchor nullable: true
        description nullable: true
        filename nullable: true
        indexText nullable: true
    }
    static mapping = {
		content lazy: true
        indexText lazy: true, type:"text"
    }

    //static transients = ['indexText']

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

    Date lastUpdated

    def getUrl() {
        assetService.createEncodedLink(this)
    }

    def afterInsert() {
        assetService.deleteRepositoryFile(this)
    }

    def afterUpdate() {
        assetService.deleteRepositoryFile(this)
    }

    def afterDelete() {
        assetService.deleteRepositoryFile(this)
    }


    static _exported = ["name", "description", "mimeType", "subType", "lastUpdated", "url"]
    static {
        grails.converters.JSON.registerObjectMarshaller(Asset) {
            def result = it.properties.findAll { k, v ->
                k in _exported || (it.subType == "attachment" && k == "content")
            }
            result.id = it.id
            return result
        }
    }
}
