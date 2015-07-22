package kola

class TaskTemplate {
    static constraints = {
    	name blank:false
    	description nullable:true
    }
	static hasMany = [resources:Asset, attachments:Attachment, steps:TaskStep]

    String name
    String description

    List<Asset> resources			// defined as list to keep order in which elements got added
    List<Attachment> attachments	// defined as list to keep order in which elements got added
    List<TaskStep> steps			// defined as list to keep order in which elements got added

    Date dateCreated
    Date lastUpdated

    // metadata
    User creator
}
