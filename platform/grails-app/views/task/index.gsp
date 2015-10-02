
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: params.isTemplate?.toBoolean() ? 'kola.taskTemplate' : 'kola.task')}" />
		<g:set var="entitiesName" value="${message(code: params.isTemplate?.toBoolean() ? 'kola.taskTemplates' : 'kola.tasks')}" />
		<title>${entitiesName}</title>
	</head>
	<body>
		<h1 class="page-header clearfix">
			${entitiesName}
			<g:link class="create btn btn-primary pull-right" action="${params.isTemplate?.toBoolean() ? 'createTemplate' : 'createFromTemplate'}" title="${message(code: 'default.new.label', args:[entityName])}">
				<i class="fa fa-plus"></i>
			</g:link>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<form method="get">
			<input type="hidden" name="isTemplate" value="${params.isTemplate}">
			<div class="row filter">
				<div class="col-sm-1 margin"><b><g:message code="kola.filter" /></b>:</div>
				<div class="col-sm-3">
					<div class="checkbox">
						<label><input name="own" type="checkbox" onclick="$(this).closest('form').submit()"${params.own ? ' checked' : ''}> <g:message code="kola.filter.own" /></label>
					</div>
				</div>
				<g:if test="${!params.isTemplate?.toBoolean()}">
					<div class="col-sm-3">
						<div class="checkbox">
							<label><input name="assigned" type="checkbox" onclick="$(this).closest('form').submit()"${params.assigned ? ' checked' : ''}> <g:message code="kola.filter.assigned" /></label>
						</div>
					</div>
				</g:if>
			</div>
		</form>
		<g:if test="${taskInstanceList?.size() > 0}">
			<g:set var="filterParams" value="${[own:params.own, assigned:params.assigned, isTemplate:params.isTemplate]}" />
			<table class="table table-striped">
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'kola.meta.name')}" params="${filterParams}" />
						<g:sortableColumn property="description" title="${message(code: 'kola.meta.description')}" params="${filterParams}" />
						<g:sortableColumn property="creator.profile.displayName" title="${message(code: 'kola.meta.creator')}" params="${filterParams}" />
						<g:sortableColumn property="creator.profile.company" title="${message(code: 'kola.user.company')}" params="${filterParams}" />
						<g:sortableColumn property="lastUpdated" title="${message(code: 'kola.meta.lastUpdated')}" params="${filterParams}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${taskInstanceList}" status="i" var="taskInstance">
					<tr>
						<td><g:link action="show" id="${taskInstance.id}" params="${[isTemplate:params.isTemplate]}">${fieldValue(bean: taskInstance, field: "name")}</g:link></td>
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
		</g:if>
		<g:else>
			<div class="alert alert-danger"><g:message code="kola.filter.empty" args="${[entitiesName]}" /></div>
		</g:else>
	</body>
</html>
