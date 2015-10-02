package kola



import static org.springframework.http.HttpStatus.*
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN', 'ROLE_REFLECTION_QUESTION_CREATOR'])
class ReflectionQuestionController {
    //static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def query = ReflectionQuestion.where { deleted == false }
        respond query.list(params), model:[reflectionQuestionInstanceCount: query.count()]
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def show(ReflectionQuestion reflectionQuestionInstance) {
        if (reflectionQuestionInstance == null) {
            notFound()
            return
        }
        respond reflectionQuestionInstance
    }

    def create() {
        respond new ReflectionQuestion(params)
    }

    @Transactional
    def save(ReflectionQuestion reflectionQuestionInstance) {
        if (reflectionQuestionInstance == null) {
            notFound()
            return
        }

        reflectionQuestionInstance.save flush:true
        if (reflectionQuestionInstance.hasErrors()) {
            respond reflectionQuestionInstance.errors, view:'create'
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'reflectionQuestion.label', default: 'Reflexionsaufforderung'), reflectionQuestionInstance.id])
        redirect action:"index", method:"GET"
    }

    def edit(ReflectionQuestion reflectionQuestionInstance) {
        respond reflectionQuestionInstance
    }

    @Transactional
    def update(ReflectionQuestion reflectionQuestionInstance) {
        if (reflectionQuestionInstance == null) {
            notFound()
            return
        }

        reflectionQuestionInstance.save flush:true
        if (reflectionQuestionInstance.hasErrors()) {
            respond reflectionQuestionInstance.errors, view:'edit'
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'reflectionQuestion.label', default: 'Reflexionsaufforderung'), reflectionQuestionInstance.id])
        redirect action:"index", method:"GET"
    }

    @Transactional
    def delete(ReflectionQuestion reflectionQuestionInstance) {
        if (reflectionQuestionInstance == null) {
            notFound()
            return
        }

        reflectionQuestionInstance.deleted = true
        reflectionQuestionInstance.save flush:true

        flash.message = message(code: 'default.deleted.message', args: [message(code: 'reflectionQuestion.label', default: 'Reflexionsaufforderung'), reflectionQuestionInstance.id])
        redirect action:"index", method:"GET"
    }

    protected void notFound() {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'reflectionQuestion.label', default: 'Reflexionsaufforderung'), params.id])
        redirect action: "index", method: "GET"
    }
}
