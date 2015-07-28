<%@ page import="kola.TaskTemplate" %>



<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'name', 'error')} required">
	<label for="name" class="col-sm-2 control-label">
		<g:message code="taskTemplate.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<div class="col-sm-10"><g:textField name="name" class="form-control" required="" value="${taskTemplateInstance?.name}"/></div>
</div>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'description', 'error')} ">
	<label for="description" class="col-sm-2 control-label">
		<g:message code="taskTemplate.description.label" default="Beschreibung" />
		
	</label>
	<div class="col-sm-10"><g:textArea rows="8" name="description" class="form-control" value="${taskTemplateInstance?.description}"/></div>
</div>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'attachments', 'error')} ">
	<label for="attachments" class="col-sm-2 control-label">
		<g:message code="taskTemplate.attachments.label" default="Anhänge" />
		
	</label>
	<div class="col-sm-10"><g:select name="attachments" from="${kola.Attachment.list()}" multiple="multiple" optionKey="id" size="5" value="${taskTemplateInstance?.attachments*.id}" class="form-control"/></div>
</div>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'resources', 'error')} ">
	<label for="resources" class="col-sm-2 control-label">
		<g:message code="taskTemplate.resources.label" default="Lernressourcen" />
		
	</label>
	<div class="col-sm-10"><g:select name="resources" from="${kola.Asset.list()}" multiple="multiple" optionKey="id" size="5" value="${taskTemplateInstance?.resources*.id}" class="form-control"/></div>
</div>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'steps', 'error')} ">
	<label for="steps" class="col-sm-2 control-label">
		<g:message code="taskTemplate.steps.label" default="Teilschritte" />
		
	</label>
	<div class="col-sm-10"><g:select name="steps" from="${kola.TaskStep.list()}" multiple="multiple" optionKey="id" size="5" value="${taskTemplateInstance?.steps*.id}" class="form-control"/></div>
</div>

