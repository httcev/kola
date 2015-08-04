package kola

class Task extends TaskBody {
    static constraints = {
    	due nullable:true
    	template nullable:true
    	assignee nullable:true
    }
    static hasMany = [steps:TaskBody]

    // task
    boolean done
    Date due
    boolean isTemplate
    Task template
    User assignee

    // task template
    Date dateCreated
    Date lastUpdated
    User creator
    List<TaskBody> steps            // defined as list to keep order in which elements got added
}
