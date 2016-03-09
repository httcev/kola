<div class="comments-list clearfix">
	<g:if test="${commentable?.comments?.size() > 0}">
		<h4 class="header"><g:message code="de.httc.plugin.qaa.comments" /></h4>
		<g:each var="comment" in="${commentable?.comments}">
			<div class="comment clearfix">
				${comment?.text}
				<div class="pull-right">
					<g:render model="${[profile:comment?.creator?.profile, hideImage:true]}" template="/profile/show" />,
					<g:formatDate date="${comment?.dateCreated}" type="date" />
				</div>
			</div>
		</g:each>
	</g:if>
	<g:link class="comment-button btn btn-default btn-small pull-right" action="addComment" id="${commentable.id}"><g:message code="default.add.label" args="${[message(code:'de.httc.plugin.qaa.comment')]}"/></g:link>
</div>
