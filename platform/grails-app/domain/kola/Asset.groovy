package kola

import java.util.UUID

class Asset {
    def transient assetService

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
        creator nullable:true
    }
    static mapping = {
        id generator: "assigned"
        description type: "text"
		content lazy: true
        indexText lazy: true, type:"text"
    }

    //static transients = ['indexText']

    String id = UUID.randomUUID().toString()
    String name
    String description
    String mimeType
    String subType = "learning-resource"
    User creator

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


    static _exported = ["name", "description", "mimeType", "subType", "url"]
    static _referenced = ["creator"]
    static {
        grails.converters.JSON.registerObjectMarshaller(Asset) { asset ->
            def doc = asset.properties.findAll { k, v ->
                k in _exported || (asset.subType == "attachment" && k == "content")
            }
            _referenced.each {
                if (asset."$it" instanceof List) {
                    doc."$it" = asset."$it"?.collect {
                        it.id
                    }
                }
                else {
                    doc."$it" = asset."$it"?.id
                }
            }
            doc.id = asset.id
            return [id:asset.id, doc:doc]
        }
    }
}
