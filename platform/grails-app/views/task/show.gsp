
<%@ page import="kola.Task" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-task" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-task" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list task">
			
				<g:if test="${taskInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="task.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${taskInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="task.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${taskInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.due}">
				<li class="fieldcontain">
					<span id="due-label" class="property-label"><g:message code="task.due.label" default="Due" /></span>
					
						<span class="property-value" aria-labelledby="due-label"><g:formatDate date="${taskInstance?.due}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.template}">
				<li class="fieldcontain">
					<span id="template-label" class="property-label"><g:message code="task.template.label" default="Template" /></span>
					
						<span class="property-value" aria-labelledby="template-label"><g:link controller="taskTemplate" action="show" id="${taskInstance?.template?.id}">${taskInstance?.template?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.assignee}">
				<li class="fieldcontain">
					<span id="assignee-label" class="property-label"><g:message code="task.assignee.label" default="Assignee" /></span>
					
						<span class="property-value" aria-labelledby="assignee-label"><g:link controller="user" action="show" id="${taskInstance?.assignee?.id}">${taskInstance?.assignee?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.attachments}">
				<li class="fieldcontain">
					<span id="attachments-label" class="property-label"><g:message code="task.attachments.label" default="Attachments" /></span>
					
						<g:each in="${taskInstance.attachments}" var="a">
						<span class="property-value" aria-labelledby="attachments-label"><g:link controller="attachment" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.creator}">
				<li class="fieldcontain">
					<span id="creator-label" class="property-label"><g:message code="task.creator.label" default="Creator" /></span>
					
						<span class="property-value" aria-labelledby="creator-label"><g:link controller="user" action="show" id="${taskInstance?.creator?.id}">${taskInstance?.creator?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="task.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${taskInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.done}">
				<li class="fieldcontain">
					<span id="done-label" class="property-label"><g:message code="task.done.label" default="Done" /></span>
					
						<span class="property-value" aria-labelledby="done-label"><g:formatBoolean boolean="${taskInstance?.done}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="task.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${taskInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.resources}">
				<li class="fieldcontain">
					<span id="resources-label" class="property-label"><g:message code="task.resources.label" default="Resources" /></span>
					
						<g:each in="${taskInstance.resources}" var="r">
						<span class="property-value" aria-labelledby="resources-label"><g:link controller="asset" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${taskInstance?.steps}">
				<li class="fieldcontain">
					<span id="steps-label" class="property-label"><g:message code="task.steps.label" default="Steps" /></span>
					
						<g:each in="${taskInstance.steps}" var="s">
						<span class="property-value" aria-labelledby="steps-label"><g:link controller="taskStep" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:taskInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${taskInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:link class="export" action="export" resource="${taskInstance}"><g:message code="default.button.export.label" default="Export" /></g:link>

					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
