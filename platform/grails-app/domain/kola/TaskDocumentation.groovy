package kola

import java.util.UUID
import java.util.SortedSet
import de.httc.plugins.user.User
import de.httc.plugins.repository.Asset
import de.httc.plugins.qaa.QuestionReference
import de.httc.plugins.qaa.Commentable
import de.httc.plugins.qaa.Comment

class TaskDocumentation extends Commentable {
	static hasMany = [ attachments:Asset, comments:Comment ]
	static belongsTo = [ reference:QuestionReference ]
    static constraints = {
    	text nullable:true
    }
    static mapping = {
        text type: "text"
		comments sort:"dateCreated", "id"
    }

    String text
    User creator
    Date lastUpdated
    boolean deleted

    List<Asset> attachments     // defined as list to keep order in which elements got added
	SortedSet<Comment> comments       // sorted by Comment.compareTo

    static _exported = ["text", "lastUpdated", "deleted"]
    static _referenced = ["attachments", "creator", "reference"]
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
