package kola

import java.util.UUID
import org.apache.commons.collections.list.LazyList
import org.apache.commons.collections.FactoryUtils
import de.httc.plugins.user.User
import de.httc.plugins.repository.Asset
import de.httc.plugins.qaa.QuestionReference

class Task extends QuestionReference {
    static searchable = {
        all = [analyzer: 'german']
        only = ['name', 'description', 'deleted']
        name boost:3.0
        description boost:2.0
    }
    static mappedBy = [documentations:"reference"]
    static hasMany = [steps:TaskStep, documentations:TaskDocumentation, reflectionQuestions:ReflectionQuestion, resources:Asset, attachments:Asset]
    static constraints = {
        name blank:false
        description nullable:true
        due nullable:true
        template nullable:true
        assignee nullable:true
    }
    static mapping = {
        steps cascade: "all-delete-orphan"
        name type: "text"
        description type: "text"
//		lastDocumented formula:"(select d.LAST_UPDATED from TASK_DOCUMENTATION d where d.DELETED='false' and (d.REFERENCE_ID=ID or d.REFERENCE_ID in (select s.TASK_STEP_ID from TASK_TASK_STEP s where s.TASK_STEPS_ID=ID)) order by d.LAST_UPDATED desc limit 1)"
//		lastDocumented formula:"(select d.LAST_UPDATED from TASK_DOCUMENTATION d where d.DELETED='false' and (d.REFERENCE_ID=ID) order by d.LAST_UPDATED desc limit 1)"
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

    static _embedded = ["name", "description", "done", "deleted", "due", "isTemplate", "lastUpdated", "dateCreated"]
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
