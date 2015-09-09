package kola

import java.util.UUID

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
        id generator: "assigned"
        name type: "text"
        description type: "text"
    }

    String id = UUID.randomUUID().toString()
    String name
    String description
    boolean deleted

    List<Asset> resources       // defined as list to keep order in which elements got added
    List<Asset> attachments     // defined as list to keep order in which elements got added

    static _exported = ["name", "description", "deleted"]
    static _referenced = ["resources", "attachments"]
    static {
        grails.converters.JSON.registerObjectMarshaller(TaskStep) { step ->
            def doc = step.properties.findAll { k, v ->
                k in _exported
            }
            _referenced.each {
                doc."$it" = step."$it"?.collect {
                    it.id
                }
            }
            doc.id = step.id
            return [id:step.id, doc:doc]
        }
    }
}
