databaseChangeLog = {
	include file: 'initial-schema.groovy'
	include file: 'repository-to-plugin.groovy'
	include file: 'refactor-tasks.groovy'
	include file: 'create-qaa-schema.groovy'
	include file: 'refactor-task-steps.groovy'
	include file: 'documentations-commentable.groovy'
	include file: 'task-type-taxonomy.groovy'
	include file: 'reflection-answer-rating.groovy'
	include file: 'settings-to-common-plugin.groovy'
}
