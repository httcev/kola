<%@ page import="kola.Task" %>



<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="task.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${taskInstance?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="task.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${taskInstance?.description}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'due', 'error')} ">
	<label for="due">
		<g:message code="task.due.label" default="Due" />
		
	</label>
	<g:datePicker name="due" precision="day"  value="${taskInstance?.due}" default="none" noSelection="['': '']" />

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'template', 'error')} ">
	<label for="template">
		<g:message code="task.template.label" default="Template" />
		
	</label>
	<g:select id="template" name="template.id" from="${kola.TaskTemplate.list()}" optionKey="id" value="${taskInstance?.template?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'assignee', 'error')} ">
	<label for="assignee">
		<g:message code="task.assignee.label" default="Assignee" />
		
	</label>
	<g:select id="assignee" name="assignee.id" from="${kola.User.list()}" optionKey="id" value="${taskInstance?.assignee?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'attachments', 'error')} ">
	<label for="attachments">
		<g:message code="task.attachments.label" default="Attachments" />
		
	</label>
	<g:select name="attachments" from="${kola.Attachment.list()}" multiple="multiple" optionKey="id" size="5" value="${taskInstance?.attachments*.id}" class="many-to-many"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'creator', 'error')} required">
	<label for="creator">
		<g:message code="task.creator.label" default="Creator" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="creator" name="creator.id" from="${kola.User.list()}" optionKey="id" required="" value="${taskInstance?.creator?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'done', 'error')} ">
	<label for="done">
		<g:message code="task.done.label" default="Done" />
		
	</label>
	<g:checkBox name="done" value="${taskInstance?.done}" />

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'resources', 'error')} ">
	<label for="resources">
		<g:message code="task.resources.label" default="Resources" />
		
	</label>
	<g:select name="resources" from="${kola.Asset.list()}" multiple="multiple" optionKey="id" size="5" value="${taskInstance?.resources*.id}" class="many-to-many"/>

</div>

<div class="fieldcontain ${hasErrors(bean: taskInstance, field: 'steps', 'error')} ">
	<label for="steps">
		<g:message code="task.steps.label" default="Steps" />
		
	</label>
	<g:select name="steps" from="${kola.TaskStep.list()}" multiple="multiple" optionKey="id" size="5" value="${taskInstance?.steps*.id}" class="many-to-many"/>

</div>

