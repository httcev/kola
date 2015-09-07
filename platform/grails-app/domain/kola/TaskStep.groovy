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

    static _exported = ["name", "description", "deleted"]
    static _referenced = ["resources", "attachments"]
    static {
        grails.converters.JSON.registerObjectMarshaller(TaskStep) { step ->
            def result = step.properties.findAll { k, v ->
                k in _exported
            }
            _referenced.each {
                result."$it" = step."$it"?.collect {
                    it.id
                }
            }
            result.id = step.id
            return result
        }
    }
}
