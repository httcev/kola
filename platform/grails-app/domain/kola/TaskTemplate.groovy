package kola

class TaskTemplate extends TaskBody {
	static hasMany = [steps:TaskBody]

    List<TaskBody> steps			// defined as list to keep order in which elements got added

    Date dateCreated
    Date lastUpdated

    // metadata
    User creator
}
