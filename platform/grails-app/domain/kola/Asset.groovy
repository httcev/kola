package kola

class Asset implements Serializable {
	private static final long serialVersionUID = 42L;

	static searchable = {
		all = [analyzer: 'german']
        only = ['name', 'description', 'mimeType', 'externalUrl', 'filename', 'anchor', 'indexText']
        name boost:3.0
        description boost:2.0
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
    static transients = ['indexText']

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
}
