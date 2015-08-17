package kola

class TaskStep {
	static hasMany = [attachments:Asset]
	static belongsTo = [ task:Task ]
    static constraints = {
    	name blank:false
    	description nullable:true
        deleted bindable:true
    }
    static transients = ["deleted"]

    String name
    String description
    boolean deleted

    List<Asset> attachments		// defined as list to keep order in which elements got added
}
