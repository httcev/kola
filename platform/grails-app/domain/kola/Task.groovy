package kola

import org.apache.commons.collections.list.LazyList
import org.apache.commons.collections.FactoryUtils

class Task {
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
}
