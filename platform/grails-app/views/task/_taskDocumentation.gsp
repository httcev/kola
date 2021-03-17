<g:set var="authService" bean="authService"/>
<li class="list-group-item">
	<div id="${taskDocumentation.id}" class="list-group-item-text clearfix">
		<div class="taskDocumentation">
			<httc:markdown>${taskDocumentation.text}</httc:markdown>
			<g:render model="${[attachments:taskDocumentation.attachments]}" template="attachments" />
		</div>
		<g:if test="${authService.canEdit(taskDocumentation)}">
			<g:form class="hidden form-horizontal" action="updateTaskDocumentation" id="${taskDocumentation.id}" method="PUT" enctype="multipart/form-data">
				<div class="form-group">
					<label for="reference" class="col-sm-3 control-label"><g:message code="kola.task.documentation.for" />:</label>
					<div class="col-sm-9">
						<select id="reference" name="reference" class="form-control">
							<option value="${task.id}"<g:if test="${taskDocumentation.reference?.id==task.id}"> selected</g:if>><g:message code="kola.task.documentation.forTask" /></option>
							<g:each var="step" in="${task?.steps}">
								<option value="${step.id}"<g:if test="${taskDocumentation.reference?.id==step.id}"> selected</g:if>><g:message code="kola.task.documentation.forStep" /> "${step.name}"</option>
							</g:each>
						</select>
					</div>
				</div>
				<g:textArea rows="5" name="text" class="form-control" value="${taskDocumentation.text}" data-provide="markdown" data-iconlibrary="fa" data-language="de" data-hidden-buttons="cmdImage cmdCode cmdQuote cmdPreview" placeholder="${message(code:'kola.task.documentation.placeholder')}" />
				<g:render model="${[attachments:taskDocumentation.attachments, mode:'edit']}" template="attachments" />
				<div class="text-right form-padding-all"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'kola.task.documentation')]" /></button></div>
			</g:form>
		</g:if>
		<div class="clearfix">
			<small class="pull-right">
				<g:if test="${authService.canEdit(taskDocumentation)}">
					<button type="button" class="btn btn-default" onclick="$(this).hide().closest('.list-group-item').addClass('list-group-item-warning').find('.taskDocumentation,.comments-list').hide().nextAll('form').first().removeClass('hidden').find('textarea').focus()"><i class="fa fa-pencil"></i> <g:message code="default.button.edit.label" /></button>
				</g:if>
				<g:render bean="${taskDocumentation.creator.profile}" template="/profile/show" var="profile" />,
				<g:formatDate date="${taskDocumentation.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/>
			</small>
		</div>
		<g:render bean="${taskDocumentation}" template="/question/comments" var="commentable" plugin="httcQaa"/>
	</div>
</li>
