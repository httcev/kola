databaseChangeLog = {
	include file: 'initial-schema.groovy'
	include file: 'repository-to-plugin.groovy'
	include file: 'refactor-tasks.groovy'
	include file: 'create-qaa-schema.groovy'
	include file: 'refactor-task-steps.groovy'
}
