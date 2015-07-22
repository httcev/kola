package kola

class Task {
    static constraints = {
    	name blank:false
    	description nullable:true
    	due nullable:true
    	template nullable:true
    	assignee nullable:true
    }
	static hasMany = [resources:Asset, attachments:Attachment, steps:TaskStep]

    String name
    String description
    boolean done

    List<Asset> resources			// defined as list to keep order in which elements got added
    List<Attachment> attachments	// defined as list to keep order in which elements got added
    List<TaskStep> steps			// defined as list to keep order in which elements got added

    Date dateCreated
    Date lastUpdated

    // metadata
    Date due
    TaskTemplate template
    User assignee
    User creator
}
