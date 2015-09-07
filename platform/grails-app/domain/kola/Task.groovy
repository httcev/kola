package kola

import org.apache.commons.collections.list.LazyList
import org.apache.commons.collections.FactoryUtils

class Task {
    static searchable = {
        all = [analyzer: 'german']
        only = ['name', 'description']
        name boost:3.0
        description boost:2.0
    }
    static hasMany = [steps:TaskStep, reflectionQuestions:ReflectionQuestion, resources:Asset, attachments:Asset]
    static constraints = {
        name blank:false
        description nullable:true
        due nullable:true
        template nullable:true
        assignee nullable:true
    }
    static mapping = {
        steps cascade:"all-delete-orphan"
        description type:"text"
    }
 
    String name
    String description

    // task
    boolean done
    Date due
    boolean isTemplate
    Task template
    User assignee

    // task and task template
    Date dateCreated
    Date lastUpdated
    User creator
    List<Asset> resources                           // defined as list to keep order in which elements got added
    List<Asset> attachments                         // defined as list to keep order in which elements got added
    List<TaskStep> steps                            // defined as list to keep order in which elements got added
    List<ReflectionQuestion> reflectionQuestions    // defined as list to keep order in which elements got added

    static _embedded = ["name", "description", "steps", "done", "due", "isTemplate", "templateId", "creatorId", "assigneeId", "lastUpdated"]
    static _referenced = ["resources", "attachments", "reflectionQuestions"]
    static {
        grails.converters.JSON.registerObjectMarshaller(Task) { task ->
            def result = task.properties.findAll { k, v ->
                k in _embedded
            }
            _referenced.each {
                result."$it" = task."$it"?.collect {
                    it.id
                }
            }
            result.id = task.id
            return result
        }
    }
}
