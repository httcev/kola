package kola

class Task extends TaskTemplate {
    static constraints = {
    	due nullable:true
    	template nullable:true
    	assignee nullable:true
    }
    boolean done


    // metadata
    Date due
    TaskTemplate template
    User assignee
}
