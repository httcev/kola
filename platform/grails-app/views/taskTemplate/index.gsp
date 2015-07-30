
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'taskTemplate.label', default: 'Arbeitsprozessbeschreibung')}" />
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
		<table class="table table-striped">
			<thead>
				<tr>
				
					<g:sortableColumn property="name" title="${message(code: 'taskTemplate.name.label', default: 'Name')}" />
				
					<g:sortableColumn property="description" title="${message(code: 'taskTemplate.description.label', default: 'Description')}" />
				
					<g:sortableColumn property="creator?.profile?.displayName" title="${message(code: 'taskTemplate.creator.label', default: 'Creator')}" />
				
					<g:sortableColumn property="creator?.profile?.company" title="${message(code: 'taskTemplate.company.label', default: 'Organisation')}" />
				
					<g:sortableColumn property="lastUpdated" title="${message(code: 'taskTemplate.lastUpdated.label', default: 'Last Updated')}" />
				
				</tr>
			</thead>
			<tbody>
			<g:each in="${taskTemplateInstanceList}" status="i" var="taskTemplateInstance">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				
					<td><g:link action="show" id="${taskTemplateInstance.id}">${fieldValue(bean: taskTemplateInstance, field: "name")}</g:link></td>
				
					<td>${fieldValue(bean: taskTemplateInstance, field: "description")?.take(100)}</td>
				
					<td>${fieldValue(bean: taskTemplateInstance.creator?.profile, field: "displayName")}</td>
				
					<td>${fieldValue(bean: taskTemplateInstance.creator?.profile, field: "company")}</td>
				
					<td><g:formatDate date="${taskTemplateInstance.lastUpdated}" /></td>
				
				</tr>
			</g:each>
			</tbody>
		</table>
		<div class="pagination pull-right">
			<g:paginate total="${taskTemplateInstanceCount ?: 0}" />
		</div>
	</body>
</html>
