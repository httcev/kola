package kola

import java.util.UUID
import de.httc.plugins.user.User

class ReflectionAnswer {
	static enum Rating { POSITIVE, NEUTRAL, NEGATIVE }
    static constraints = {
    	text nullable:true
		rating nullable:true
    }
    static mapping = {
        id generator: "assigned"
        text type:"text"
    }
    /*
	static searchable = {
		all = [analyzer: 'german']
        only = ['text', 'deleted']
    }
    */
    static belongsTo = [task:Task, question:ReflectionQuestion]

    String id = UUID.randomUUID().toString()
    String text
	Rating rating
    Date lastUpdated
    User creator
    boolean deleted

    static _exported = ["text", "rating", "lastUpdated", "deleted"]
    static _referenced = ["creator", "task", "question"]
    static {
        grails.converters.JSON.registerObjectMarshaller(ReflectionAnswer) { answer ->
            def doc = answer.properties.findAll { k, v ->
                k in _exported
            }
            _referenced.each {
                if (answer."$it" instanceof List) {
                    doc."$it" = answer."$it"?.collect {
                        it?.id
                    }
                }
                else {
                    doc."$it" = answer."$it"?.id
                }
            }
            doc.id = answer.id
            return [id:answer.id, doc:doc]
        }
    }
}
