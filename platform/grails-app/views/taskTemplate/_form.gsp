<%@ page import="kola.TaskTemplate" %>



<div class="fieldcontain ${hasErrors(bean: taskTemplateInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="taskTemplate.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${taskTemplateInstance?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskTemplateInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="taskTemplate.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${taskTemplateInstance?.description}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskTemplateInstance, field: 'attachments', 'error')} ">
	<label for="attachments">
		<g:message code="taskTemplate.attachments.label" default="Attachments" />
		
	</label>
	<g:select name="attachments" from="${kola.Attachment.list()}" multiple="multiple" optionKey="id" size="5" value="${taskTemplateInstance?.attachments*.id}" class="many-to-many"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskTemplateInstance, field: 'creator', 'error')} required">
	<label for="creator">
		<g:message code="taskTemplate.creator.label" default="Creator" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="creator" name="creator.id" from="${kola.User.list()}" optionKey="id" required="" value="${taskTemplateInstance?.creator?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskTemplateInstance, field: 'resources', 'error')} ">
	<label for="resources">
		<g:message code="taskTemplate.resources.label" default="Resources" />
		
	</label>
	<g:select name="resources" from="${kola.Asset.list()}" multiple="multiple" optionKey="id" size="5" value="${taskTemplateInstance?.resources*.id}" class="many-to-many"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskTemplateInstance, field: 'steps', 'error')} ">
	<label for="steps">
		<g:message code="taskTemplate.steps.label" default="Steps" />
		
	</label>
	<g:select name="steps" from="${kola.TaskStep.list()}" multiple="multiple" optionKey="id" size="5" value="${taskTemplateInstance?.steps*.id}" class="many-to-many"/>

</div>

