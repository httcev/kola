package kola

class DeleteExpiredTasksJob  {
	static triggers = {
		cron name: 'myTrigger', cronExpression: "0 0 0 1/1 * ? *"
		// cron name: 'myTrigger', cronExpression: "0 0/1 * 1/1 * ? *"
	}
	def group = "DefaultGroup"
	def description = "Deletes expired tasks"

	def execute() {
		def now = new Date()
		Task.where { expires != null && isTemplate != true && deleted != true && expires < now }.list().each {
			log.warn "automatically deleting expired ${it}"
			it.deleted = true
			it.save()
		}
	}
}
