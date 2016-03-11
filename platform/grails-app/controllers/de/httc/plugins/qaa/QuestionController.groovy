package de.httc.plugins.qaa

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional
import de.httc.plugins.repository.Asset
import de.httc.plugins.repository.AssetContent

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
@Transactional(readOnly = true)
class QuestionController {
  def springSecurityService
  def authService
  def questionService

  def index(Integer max) {
      params.offset = params.offset && !params.resetOffset ? (params.offset as int) : 0
      params.max = Math.min(max ?: 10, 100)
      params.sort = params.sort ?: "lastUpdated"
      params.order = params.order ?: "desc"

      def user = springSecurityService.currentUser
      def userCompany = user.profile?.company
      def filtered = params.own || params.ownCompany

      def results = Question.createCriteria().list(max:params.max, offset:params.offset) {
          // left join allows null values in the association
          createAlias('creator', 'c', org.hibernate.Criteria.LEFT_JOIN)
          createAlias('c.profile', 'cp', org.hibernate.Criteria.LEFT_JOIN)

          eq("deleted", false)
          if (filtered) {
              or {
                  if (params.own) {
                      eq("creator", user)
                  }
                  if (params.ownCompany) {
                      eq("cp.company", userCompany)
                  }
              }
          }
          order(params.sort, params.order)
      }
      respond results, model:[questionCount:results.totalCount]
  }

  def show(Question question) {
      if (question == null) {
          notFound()
          return
      }
	  def sortedAnswers = question.answers?.sort { a,b ->
		  if (question.acceptedAnswer?.id == a.id) {
			  return -1
		  }
		  else if (question.acceptedAnswer?.id == b.id) {
			  return 1
		  }
		  return a.dateCreated.compareTo(b.dateCreated)
	  }
      respond question, model:[sortedAnswers:sortedAnswers]
  }

  @Transactional
  def rate(Question question) {
      if (question == null) {
          notFound()
          return
      }
	  question.setRated(!question.getRated())
	  question.save()
	  redirect action: "show", id:question.id, method: "GET"
  }

    @Transactional
    def rateAnswer() {
  	  if (!params.questionId || !params.answerId) {
  		  notFound()
            return
  	  }
  	  def answer = Answer.get(params.answerId)
  	  if (answer == null) {
  		  notFound()
  		  return
        }
  	  answer.setRated(!answer.getRated())
  	  answer.save()
  	  redirect action: "show", id:params.questionId, method: "GET"
    }

  @Transactional
  def acceptAnswer() {
	  if (!params.questionId || !params.answerId) {
		  notFound()
          return
	  }
	  def question = Question.get(params.questionId)
	  def answer = Answer.get(params.answerId)
	  if (question == null || answer == null) {
		  notFound()
		  return
      }
	  if (!authService.canEdit(question)) {
		  forbidden()
		  return
	  }
	  question.acceptedAnswer = answer
	  question.save()
	  redirect action: "show", id:question.id, method: "GET"
  }

  @Transactional
  def saveAnswer(Answer answer) {
	  def attachments = []
	  request.multiFileMap?.each { k,files ->
		  if ("_newAttachment" == k) {
			  files?.each { f ->
				  if (!f.empty) {
					  attachments.add(new Asset(name:f.originalFilename, typeLabel:"attachment", mimeType:f.getContentType(), content:new AssetContent(data:f.bytes)))
				  }
			  }
		  }
	  }

	  answer.creator = springSecurityService.currentUser
	  attachments.each {
		  answer.addToAttachments(it)
	  }
	  if (!questionService.saveAnswer(answer)) {
		  respond answer.errors, view:'show'
		  return
	  }

	  flash.message = message(code: 'default.created.message.single', args: [message(code: 'de.httc.plugin.qaa.answer')])
	  redirect action:"show", method:"GET", id:answer.question.id
  }

  @Transactional
  def updateAnswer(Answer answer) {
	  def questionId = params.questionId
	  if (!authService.canEdit(answer)) {
		  forbidden()
	  }

	  answer.attachments?.clear()
	  bindData(answer, params, [include: ['attachments']])
	  // attach uploaded files
	  request.multiFileMap?.each { k,files ->
		  if ("_newAttachment" == k) {
			  files?.each { f ->
				  if (!f.empty) {
					  def asset = new Asset(name:f.originalFilename, typeLabel:"attachment", mimeType:f.getContentType(), content:new AssetContent(data:f.bytes))
					  if (!asset.save(true)) {
						  asset.errors.allErrors.each { println it }
					  }
					  else {
						  answer.addToAttachments(asset)
					  }
				  }
			  }
		  }
	  }


	  def msg
	  if (answer.text || answer.attachments?.size() > 0) {
		  if (!questionService.saveAnswer(answer)) {
			  flash.error = answer.errors
			  redirect action:"show", method:"GET", id:questionId
			  return
		  }
		  msg = message(code: 'default.updated.message.single', args: [message(code: 'de.httc.plugin.qaa.answer')])
	  }
	  else {
		  answer.text = "DELETED"
		  answer.deleted =true
		  questionService.saveAnswer(answer)
		  msg = message(code: 'default.deleted.message', args: [message(code: 'de.httc.plugin.qaa.answer'), answer.id])
	  }

	  flash.message = msg
	  redirect action:"show", method:"GET", id:questionId
  }

  @Transactional
  def saveComment(Comment comment) {
	  comment.creator = springSecurityService.currentUser
	  if (!questionService.saveComment(comment)) {
		  respond comment.errors, view:'show'
		  return
	  }

	  def question = comment.reference
	  if (!(question instanceof Question)) {
		  question = question.question
	  }
	  flash.message = message(code: 'default.created.message.single', args: [message(code: 'de.httc.plugin.qaa.comment')])
	  redirect action:"show", method:"GET", id:question.id
  }

  protected void notFound() {
	  flash.message = message(code: 'default.not.found.message', args: [message(code:'de.httc.plugin.qaa.question', default: 'Question'), params.id])
	  redirect action: "index", method: "GET"
  }

  protected void forbidden() {
	  flash.message = message(code: 'default.forbidden.message', args: [message(code:'de.httc.plugin.qaa.question', default: 'Question'), params.id])
	  redirect action: "index", method: "GET"
  }
}
