
<%@ page import="kola.Task" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="default.home.label" default="Home" /></g:link></li>
			<li><g:link action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			<li class="active"><g:message code="default.show.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<h1 class="page-header">
			${taskInstance?.name}
			<div class="buttons pull-right">
				<g:link class="export btn btn-primary" action="export" resource="${taskInstance}" title="${message(code: 'default.button.export.label', args:[entityName])}"><i class="fa fa-download"></i></g:link>
				<g:link class="edit btn btn-primary" action="edit" resource="${taskInstance}" title="${message(code: 'default.button.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
			</div>
		</h1>
		<g:if test="${taskInstance?.description}">
			<p>${taskInstance.description}</p>
		</g:if>
		<ol class="property-list task">
		
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
	</body>
</html>
