package kola

import java.util.UUID
import org.apache.commons.collections.list.LazyList
import org.apache.commons.collections.FactoryUtils
import de.httc.plugins.user.User
import de.httc.plugins.repository.Asset
import de.httc.plugins.qaa.QuestionReference
import de.httc.plugins.taxonomy.TaxonomyTerm

class Task extends QuestionReference {
    static searchable = {
        all = [analyzer: 'german']
        only = ['name', 'description', 'deleted']
        name boost:3.0
        description boost:2.0
    }
    static mappedBy = [documentations:"reference", steps:"task"]
    static hasMany = [steps:TaskStep, documentations:TaskDocumentation, reflectionQuestions:ReflectionQuestion, resources:Asset, attachments:Asset]
    static constraints = {
        name blank:false
        description nullable:true
        due nullable:true
        template nullable:true
        assignee nullable:true
		type nullable:true, validator: { val, obj ->
			def label = obj.type?.taxonomy?.label
			return label == null || label == "taskType"
		}
    }
    static mapping = {
        steps cascade: "all-delete-orphan"
        name type: "text"
        description type: "text"
    }

    String name
    String description

    // task
    boolean done
    boolean deleted
    Date due
    boolean isTemplate
    Task template
    User assignee

    // task and task template
    Date dateCreated
    Date lastUpdated
//    Date lastDocumented
    User creator
    List<Asset> resources                           // defined as list to keep order in which elements got added
    List<Asset> attachments                         // defined as list to keep order in which elements got added
    List<TaskStep> steps                            // defined as list to keep order in which elements got added
    List<ReflectionQuestion> reflectionQuestions    // defined as list to keep order in which elements got added

	TaxonomyTerm type

    static _embedded = ["name", "description", "done", "deleted", "due", "isTemplate", "lastUpdated", "dateCreated", "type"]
    static _referenced = ["steps", "resources", "attachments", "reflectionQuestions", "template", "creator", "assignee"]

    static {
        grails.converters.JSON.registerObjectMarshaller(Task) { task ->
            def doc = task.properties.findAll { k, v ->
                k in _embedded
            }
            _referenced.each {
                if (task."$it" instanceof List) {
                    doc."$it" = task."$it"?.collect {
                        it?.id
                    }
                }
                else {
                    doc."$it" = task."$it"?.id
                }
            }
            doc.id = task.id
            return [id:task.id, doc:doc]
        }
    }
}
