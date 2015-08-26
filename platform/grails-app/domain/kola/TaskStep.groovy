package kola

class TaskStep {
	static hasMany = [resources:Asset, attachments:Asset]
	static belongsTo = [ task:Task ]
    static constraints = {
    	name blank:false
    	description nullable:true
        deleted bindable:true
    }
    static transients = ["deleted"]
    static mapping = {
        description type:"text"
    }

    String name
    String description
    boolean deleted

    List<Asset> resources       // defined as list to keep order in which elements got added
    List<Asset> attachments     // defined as list to keep order in which elements got added
}
