<li class="list-group-item clearfix">
	<g:if test="${!isNew}">
		<input type="hidden" name="steps[${index}].id" value="${step.id}" class="stepId">
	</g:if>
	<input type="hidden" name="steps[${index}].deleted" class="deleteFlag" value="false">
	<h4 class="list-group-item-heading clearfix">
		<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
		<span class="text-muted"><g:message code="kola.task.step" /> <span class="step-index">${index+1}</span></span>
		<button type="button" class="btn btn-danger pull-right" onclick="deleteStep($(this))"><i class="fa fa-times" title="${message(code:'default.button.delete.label')}"></i></button>
	</h4>
	<div class="list-group-item-text">
		<div class="form-group">
			<label class="col-sm-2 control-label">
				<g:message code="kola.meta.name" />
				<span class="required-indicator">*</span>:
			</label>
			<div class="col-sm-10"><input type="text" name="steps[${index}].name" class="form-control" value="${step.name}" required></div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">
				<g:message code="kola.meta.description" />:
			</label>
			<div class="col-sm-10"><textarea name="steps[${index}].description" class="form-control" rows="6" data-provide="markdown" data-iconlibrary="fa" data-language="de" data-hidden-buttons="cmdImage cmdCode cmdQuote cmdPreview">${step.description}</textarea></div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">
				<g:message code="kola.task.attachments" />:
			</label>
			<div class="col-sm-10">
				<g:render model="${[attachments:step.attachments, mode:'edit', prefix:'steps['+index+'].']}" template="attachments" />
			</div>
		</div>
	</div>
</li>
