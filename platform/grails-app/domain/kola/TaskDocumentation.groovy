package kola

import java.util.UUID
import de.httc.plugins.user.User

class TaskDocumentation {
	static hasMany = [ attachments:Asset ]
	static belongsTo = [ task:Task, step:TaskStep ]
    static constraints = {
        // ensure that either task or step is set
        task(nullable: true, validator: {field, inst -> inst.step || field})
        step(nullable: true)
    	text nullable:true
//        deleted bindable:true
    }
//    static transients = ["deleted"]
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

    static _exported = ["text", "lastUpdated", "deleted"]
    static _referenced = ["attachments", "creator", "task", "step"]
    static {
        grails.converters.JSON.registerObjectMarshaller(TaskDocumentation) { taskDocumentation ->
            def doc = taskDocumentation.properties.findAll { k, v ->
                k in _exported
            }
            _referenced.each {
                if (taskDocumentation."$it" instanceof List) {
                    doc."$it" = taskDocumentation."$it"?.collect {
                        it?.id
                    }
                }
                else {
                    doc."$it" = taskDocumentation."$it"?.id
                }
            }
            doc.id = taskDocumentation.id
            return [id:taskDocumentation.id, doc:doc]
        }
    }
}
