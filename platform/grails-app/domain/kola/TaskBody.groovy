package kola

class TaskBody {
    static constraints = {
    	name blank:false
    	description nullable:true
    }
	static hasMany = [resources:Asset, attachments:Asset]

    String name
    String description

    List<Asset> resources			// defined as list to keep order in which elements got added
    List<Asset> attachments	// defined as list to keep order in which elements got added
}
