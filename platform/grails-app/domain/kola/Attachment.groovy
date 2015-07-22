package kola

class Attachment {
	static searchable = {
		all = [analyzer: 'german']
        only = ['name', 'mimeType', 'filename']
        name boost:2.0
    }
    static constraints = {
    	// Limit upload file size to 100MB
        content maxSize: 1024 * 1024 * 100, nullable:true
        filename nullable: true
    }
    static mapping = {
		content lazy: true
    }

    String name
    String mimeType

    byte[] content
    String filename

    Date dateCreated
    Date lastUpdated
}
