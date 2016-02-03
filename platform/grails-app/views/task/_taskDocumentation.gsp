<g:set var="authService" bean="authService"/>
<li class="list-group-item">
	<div class="list-group-item-text clearfix">
		<div class="taskDocumentation">
			<p class="formatted">${taskDocumentation.text}</p>
			<g:render model="${[attachments:taskDocumentation.attachments]}" template="attachments" />
		</div>
		<g:if test="${authService.canEdit(taskDocumentation)}">
			<g:form class="form hidden" action="updateTaskDocumentation" id="${taskDocumentation.id}" method="PUT" enctype="multipart/form-data">
				<input type="hidden" name="parentTask" value="${task.id}">
				<textarea name="text" class="form-control" rows="5" placeholder="${message(code:'kola.task.documentation.placeholder')}">${taskDocumentation.text}</textarea>
				<g:render model="${[attachments:taskDocumentation.attachments, mode:'edit']}" template="attachments" />
				<div class="text-right form-padding-all"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'kola.task.documentation')]" /></button></div>
			</g:form>
		</g:if>
		<small class="pull-right">
			<g:if test="${authService.canEdit(taskDocumentation)}">
				<button type="button" class="btn btn-default" onclick="$(this).hide().parent().prevAll('.taskDocumentation').hide().nextAll('.form').first().removeClass('hidden').find('textarea').focus()"><i class="fa fa-pencil"></i> <g:message code="default.button.edit.label" /></button>
			</g:if>
			<g:render bean="${taskDocumentation.creator.profile}" template="/profile/show" var="profile" />,
			<g:formatDate date="${taskDocumentation.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/>
		</small>
	</div>
</li>
