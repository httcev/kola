<g:set var="authService" bean="authService"/>
<g:if test="${!answer.deleted}">
	<div class="answer clearfix">
		<div class="text-center">
			<g:render bean="${answer}" template="ratingControl" var="rateable" />
			<div>
				<g:if test="${question?.acceptedAnswer?.id == answer.id}"><i class="text-success fa fa-check fa-2x"></i></g:if>
				<g:elseif test="${authService.canEdit(question)}">
					<g:link class="accept-answer-button btn btn-default btn-small" mapping="acceptAnswer" params="${[questionId:question.id, answerId:answer.id]}"><i class="fa fa-check fa-2x"></i></g:link>
				</g:elseif>
			</div>
		</div>
		<div class="full-width padding-left">
			<div class="answer-content">
				<kola:markdown>${answer?.text}</kola:markdown>
				<div class="clearfix">
					<div class="pull-right">
						<g:if test="${authService.canEdit(answer)}">
							<button type="button" class="btn btn-default" onclick="$(this).closest('.answer-content').hide().next('.form').removeClass('hidden').find('textarea').focus()"><i class="fa fa-pencil"></i> <g:message code="default.button.edit.label" /></button>
						</g:if>
						<g:render model="${[profile:answer?.creator?.profile]}" template="/profile/show" />,
						<g:formatDate date="${answer?.dateCreated}" type="date" />
					</div>
				</div>
				<g:if test="${answer?.attachments?.size() > 0}">
					<g:render model="${[attachments:answer?.attachments]}" template="/task/attachments" />
				</g:if>
			</div>
			<g:if test="${authService.canEdit(answer)}">
				<g:form class="form hidden" action="updateAnswer" id="${answer.id}" method="PUT" enctype="multipart/form-data">
					<input type="hidden" name="questionId" value="${question.id}">
					<textarea name="text" class="form-control" rows="5" placeholder="${message(code:'de.httc.plugin.qaa.answer.placeholder')}" required>${answer.text}</textarea>
					<g:render model="${[attachments:answer.attachments, mode:'edit']}" template="/task/attachments" />
					<div class="text-right form-padding-all"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'de.httc.plugin.qaa.answer')]" /></button></div>
				</g:form>
			</g:if>
			<g:render bean="${answer}" template="comments" var="commentable" />
		</div>
	</div>
</g:if>
