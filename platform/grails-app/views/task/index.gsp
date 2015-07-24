
<%@ page import="kola.Task" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'task.label', default: 'Task')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<h1 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
			<g:link class="create btn btn-primary pull-right" action="create" title="${message(code: 'default.new.label', args:[entityName])}">
				<i class="fa fa-plus"></i>
			</g:link>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<table class="table">
			<thead>
				<tr>
					<g:sortableColumn property="name" title="${message(code: 'task.name.label', default: 'Name')}" />
					<g:sortableColumn property="description" title="${message(code: 'task.description.label', default: 'Description')}" />
					<g:sortableColumn property="due" title="${message(code: 'task.due.label', default: 'Due')}" />
					<g:sortableColumn property="due" title="${message(code: 'task.assignee.label', default: 'Assignee')}" />
					<g:sortableColumn property="due" title="${message(code: 'task.creator.label', default: 'Creator')}" />
				</tr>
			</thead>
			<tbody>
			<g:each in="${taskInstanceList}" var="taskInstance">
				<tr>
					<td><g:link action="show" id="${taskInstance.id}">${fieldValue(bean: taskInstance, field: "name")}</g:link></td>
					<td>${fieldValue(bean: taskInstance, field: "description")?.take(100)}</td>
					<td><g:formatDate date="${taskInstance.due}" /></td>
					<td>${taskInstance?.assignee?.profile?.displayName}</td>
					<td>${taskInstance?.creator?.profile?.displayName}</td>
				</tr>
			</g:each>
			</tbody>
		</table>
		<div class="pagination pull-right">
			<g:paginate total="${taskInstanceCount ?: 0}" />
		</div>
	</body>
</html>
