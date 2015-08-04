
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: params.isTemplate ? 'taskTemplate.label' : 'task.label', default: params.isTemplate ? 'Arbeitsprozessbeschreibung' : 'Arbeitsauftrag')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<h1 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
			<g:link class="create btn btn-primary pull-right" action="createFromTemplate" title="${message(code: 'default.new.label', args:[entityName])}">
				<i class="fa fa-plus"></i>
			</g:link>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<table class="table table-striped">
			<thead>
				<tr>
				
					<g:sortableColumn property="name" title="${message(code: 'task.name.label', default: 'Name')}" />
				
					<g:sortableColumn property="description" title="${message(code: 'task.description.label', default: 'Description')}" />
				
					<g:sortableColumn property="creator?.profile?.displayName" title="${message(code: 'task.creator.label', default: 'Creator')}" />
				
					<g:sortableColumn property="creator?.profile?.company" title="${message(code: 'task.company.label', default: 'Organisation')}" />
				
					<g:sortableColumn property="lastUpdated" title="${message(code: 'task.lastUpdated.label', default: 'Last Updated')}" />
				
				</tr>
			</thead>
			<tbody>
			<g:each in="${taskInstanceList}" status="i" var="taskInstance">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				
					<td><g:link action="show" id="${taskInstance.id}">${fieldValue(bean: taskInstance, field: "name")}</g:link></td>
				
					<td>${fieldValue(bean: taskInstance, field: "description")?.take(100)}</td>
				
					<td>${fieldValue(bean: taskInstance.creator?.profile, field: "displayName")}</td>
				
					<td>${fieldValue(bean: taskInstance.creator?.profile, field: "company")}</td>
				
					<td><g:formatDate date="${taskInstance.lastUpdated}" /></td>
				
				</tr>
			</g:each>
			</tbody>
		</table>
		<div class="pagination pull-right">
			<g:paginate total="${taskInstanceCount ?: 0}" />
		</div>
	</body>
</html>
