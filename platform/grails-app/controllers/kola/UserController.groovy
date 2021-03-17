package kola

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import org.springframework.security.access.annotation.Secured
import org.hibernate.criterion.Order

import de.httc.plugins.user.User
import de.httc.plugins.user.UserRole
import de.httc.plugins.user.Role
import de.httc.plugins.pushNotification.PushToken
import de.httc.plugins.qaa.Question
import de.httc.plugins.qaa.Answer
import de.httc.plugins.qaa.Comment
import de.httc.plugins.repository.Asset

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN', 'ROLE_USER_ADMIN'])
class UserController extends de.httc.plugins.user.UserController {
	static namespace = "kola"

	def index(Integer max) {
		params.offset = params.offset && !params.resetOffset ? (params.offset as int) : 0
		params.max = Math.min(max ?: 20, 10000)
		params.sort = params.sort ?: "username"
		params.order = params.order ?: "asc"

		def hibernateOrder = params.order == "asc" ? Order.asc(params.sort).ignoreCase() : Order.desc(params.sort).ignoreCase()

		def results = User.createCriteria().list(max:params.max, offset:params.offset) {
			// left join allows null values in the association
			createAlias('profile', 'p', org.hibernate.Criteria.LEFT_JOIN)
			createAlias('p.company', 'pc', org.hibernate.Criteria.LEFT_JOIN)

			if (params.filter) {
				or {
					ilike("username", "%${params.filter}%")
					ilike("p.firstName", "%${params.filter}%")
					ilike("p.lastName", "%${params.filter}%")
				}
			}

			order(hibernateOrder)
			// add secondary sort key to keep sorting consistent on reload
			order("id", "asc")
		}
		respond results, model:[userCount:results.totalCount]
	}
  
  @Transactional
	@Override
	def delete(User userInstance) {
		if (userInstance == null) {
			notFound()
			return
		}

    // delete all user data
    TaskDocumentation.findAll { creator == userInstance }.each { it.delete(flush: true) }
    ReflectionAnswer.findAll { creator == userInstance }.each { it.delete(flush: true) }
    Task.findAll { isTemplate == false && (creator == userInstance || assignee == userInstance) }.each { taskDoc ->
      taskDoc.steps.each { step ->
        Question.findAll { reference == step }.each { it.delete(flush: true) }
        // delete all task documentations from step - an admin can create documentations on behalf of the user which would result in not being able to delete the task
        TaskDocumentation.findAll { reference == step }.each {
          it.delete(flush: true)
        }
      }
      Question.findAll { reference == taskDoc }.each {
        it.delete(flush: true)
      }
      // delete reflection answers from task - an admin can create refl. answers on behalf of the user which would result in not being able to delete the task
      ReflectionAnswer.findAll { task == taskDoc }.each {
        it.delete(flush: true)
      }
      // delete all task documentations from task - an admin can create documentations on behalf of the user which would result in not being able to delete the task
      TaskDocumentation.findAll { reference == taskDoc }.each {
        it.delete(flush: true)
      }
      taskDoc.delete(flush: true)
    }
		Question.findAll { creator == userInstance }.each { it.delete(flush: true) }
		Answer.findAll { creator == userInstance }.each { it.delete(flush: true) }
		Comment.findAll { creator == userInstance }.each { it.delete(flush: true) }
    PushToken.where { user == userInstance }.deleteAll()
    Asset.findAll { creator == userInstance }.each { it.delete(flush: true) }

    userInstance.delete flush:true

		flash.message = message(code: 'default.deleted.message', args: [message(code: 'de.httc.plugin.user.user', default: 'User'), userInstance.profile.displayName])
		redirect action:"index", method:"GET"
	}
}
