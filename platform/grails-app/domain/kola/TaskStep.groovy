package kola

import java.util.UUID
import de.httc.plugins.user.User
import de.httc.plugins.repository.Asset
import de.httc.plugins.qaa.QuestionReference

class TaskStep extends QuestionReference {
	static mappedBy = [documentations:"reference"]
	static hasMany = [documentations:TaskDocumentation, resources:Asset, attachments:Asset]
	static belongsTo = [task:Task]
	static constraints = {
		name blank:false
		description nullable:true
		deleted bindable:true
		creator nullable:true
	}
	static transients = ["deleted"]
	static mapping = {
		name type: "text"
		description type: "text"
	}

	String name
	String description
	boolean deleted
	User creator

	List<Asset> resources	  	// defined as list to keep order in which elements got added
	List<Asset> attachments	 	// defined as list to keep order in which elements got added

	static _exported = ["name", "description", "deleted"]
	static _referenced = ["resources", "attachments", "creator", "task"]
	static {
		grails.converters.JSON.registerObjectMarshaller(TaskStep) { step ->
			def doc = step.properties.findAll { k, v ->
				k in _exported
			}
			_referenced.each {
				if (step."$it" instanceof List) {
					doc."$it" = step."$it"?.collect {
						it?.id
					}
				}
				else {
					doc."$it" = step."$it"?.id
				}
			}
			doc.id = step.id
			return [id:step.id, doc:doc]
		}
	}
}
