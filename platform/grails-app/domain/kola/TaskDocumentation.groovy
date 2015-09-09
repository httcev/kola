package kola

import java.util.UUID

class TaskDocumentation {
	static hasMany = [attachments:Asset]
	static belongsTo = [ task:Task ]
    static constraints = {
    	text nullable:true
        deleted bindable:true
    }
    static transients = ["deleted"]
    static mapping = {
        id generator: "assigned"
        text type: "text"
    }

    String id = UUID.randomUUID().toString()
    String text
    User creator
    Date lastUpdated
    boolean deleted

    List<Asset> attachments     // defined as list to keep order in which elements got added

    static _exported = ["text", "creatorId", "taskId", "lastUpdated", "deleted"]
    static _referenced = ["attachments"]
    static {
        grails.converters.JSON.registerObjectMarshaller(TaskDocumentation) { taskDocumentation ->
            def doc = taskDocumentation.properties.findAll { k, v ->
                k in _exported
            }
            _referenced.each {
                doc."$it" = taskDocumentation."$it"?.collect {
                    it.id
                }
            }
            doc.id = taskDocumentation.id
            return [id:taskDocumentation.id, doc:doc]
        }
    }
}
