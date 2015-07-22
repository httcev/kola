package kola

class TaskStep {
    static constraints = {
    	name blank:false
    }
	static hasMany = [resources:Asset, attachments:Attachment]

    String name
    String description
    boolean done

    List<Asset> resources			// defined as list to keep order in which elements got added
    List<Attachment> attachments	// defined as list to keep order in which elements got added

    Date dateCreated
    Date lastUpdated
}
