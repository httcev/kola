<div class="comments-list clearfix">
	<g:if test="${commentable?.comments?.size() > 0}">
		<h4 class="header"><g:message code="de.httc.plugin.qaa.comments" /></h4>
		<g:each var="comment" in="${commentable?.comments}">
			<g:if test="${!comment.deleted}">
				<div class="comment clearfix">
					${comment?.text}
					<div class="pull-right">
						<g:render model="${[profile:comment?.creator?.profile, hideImage:true]}" template="/profile/show" />,
						<g:formatDate date="${comment?.dateCreated}" type="date" />
					</div>
				</div>
			</g:if>
		</g:each>
	</g:if>
	<button type="button" class="comment-button btn btn-default btn-small pull-right" onclick="$(this).hide().next('.new-comment').removeClass('hidden').find('.comment-text').focus()"><i class="fa fa-plus"></i> <g:message code="default.add.label" args="[message(code:'de.httc.plugin.qaa.comment')]"/></button>
	<div class="new-comment hidden">
		<h1><g:message code="default.add.label" args="[message(code:'de.httc.plugin.qaa.comment')]"/>:</h1>
		<g:form class="form" action="saveComment">
			<input type="hidden" name="reference" value="${commentable.id}">
			<input type="text" name="text" class="form-control comment-text" required>
			<div class="text-right form-padding"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'de.httc.plugin.qaa.comment')]"/></button></div>
		</g:form>
	</div>
</div>
