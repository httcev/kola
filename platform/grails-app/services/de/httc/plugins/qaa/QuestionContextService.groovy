package de.httc.plugins.qaa

import grails.transaction.Transactional
import kola.Task
import kola.TaskStep

@Transactional(readOnly = true)
class QuestionContextService {
	def authService

	def getPossibleQuestionReferences(contextId) {
		def result
		if (contextId) {
			try {
				def task = Task.get(contextId)
				if (task && authService.canAttach(task)) {
					result = _getTaskAndSteps(task)
				}
				else {
					def taskStep = TaskStep.get(contextId)
					if (taskStep && authService.canAttach(taskStep)) {
						result = _getTaskAndSteps(taskStep.task)
					}
				}
			}
			catch(e) {
				log.error e
			}
		}
		return result
	}

	def _getTaskAndSteps(task) {
		def result = []
		result << task
		task.steps?.each { step ->
			result << step
		}
		return result
	}
}
