
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
		<form method="get">
			<div class="checkbox">
				<label><input name="own" type="checkbox" onclick="$(this).closest('form').submit()"${params.own ? ' checked' : ''}> Von mir erstellte</label>
				<label><input name="assigned" type="checkbox" onclick="$(this).closest('form').submit()"${params.assigned ? ' checked' : ''}> Mir zugewiesene</label>
			</div>
		</form>
		<g:set var="filterParams" value="${[own:params.own, assigned:params.assigned]}" />
		<table class="table table-striped">
			<thead>
				<tr>
					<g:sortableColumn property="name" title="${message(code: 'task.name.label', default: 'Name')}" params="${filterParams}" />
					<g:sortableColumn property="description" title="${message(code: 'task.description.label', default: 'Description')}" params="${filterParams}" />
					<g:sortableColumn property="creator?.profile?.displayName" title="${message(code: 'task.creator.label', default: 'Creator')}" params="${filterParams}" />
					<g:sortableColumn property="creator?.profile?.company" title="${message(code: 'task.company.label', default: 'Organisation')}" params="${filterParams}" />
					<g:sortableColumn property="lastUpdated" title="${message(code: 'task.lastUpdated.label', default: 'Last Updated')}" params="${filterParams}" />
				</tr>
			</thead>
			<tbody>
			<g:each in="${taskInstanceList}" status="i" var="taskInstance">
				<tr>
					<td><g:link action="show" id="${taskInstance.id}">${fieldValue(bean: taskInstance, field: "name")}</g:link></td>
					<td>${fieldValue(bean: taskInstance, field: "description")?.take(100)}</td>
					<td>${fieldValue(bean: taskInstance.creator?.profile, field: "displayName")}</td>
					<td>${fieldValue(bean: taskInstance.creator?.profile, field: "company")}</td>
					<td><g:formatDate date="${taskInstance.lastUpdated}" type="date"/></td>
				</tr>
			</g:each>
			</tbody>
		</table>
		<div class="pagination pull-right">
			<g:paginate total="${taskInstanceCount ?: 0}" params="${filterParams}" />
		</div>
	</body>
</html>
