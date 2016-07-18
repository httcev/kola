
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
				<i class="fa fa-plus"></i> <g:message code="default.button.create.label" />
			</g:link>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<div class="panel panel-default">
			<div class="panel-heading">
				<form method="get">
					<input type="hidden" name="isTemplate" value="${params.isTemplate}">
					<b><g:message code="kola.filter" /></b>:
					<div class="row filter">
						<div class="col-md-3"><span class="checkbox">
							<label><input name="own" type="checkbox" onclick="$(this).closest('form').submit()"${params.own ? ' checked' : ''}> <g:message code="kola.filter.own" /></label>
						</span></div>
						<g:if test="${!params.isTemplate?.toBoolean()}">
							<div class="col-md-3"><span class="checkbox">
								<label><input name="assigned" type="checkbox" onclick="$(this).closest('form').submit()"${params.assigned ? ' checked' : ''}> <g:message code="kola.filter.assigned" /></label>
							</span></div>
						</g:if>
						<div class="col-md-3"><span class="checkbox">
							<label><input name="ownCompany" type="checkbox" onclick="$(this).closest('form').submit()"${params.ownCompany ? ' checked' : ''}> <g:message code="kola.filter.ownCompany" /></label>
						</span></div>
						<div class="col-md-3">
							<div class="input-group">
								<input type="text" name="key" value="${params.key}" class="form-control" placeholder="${message(code:'kola.search.query')}...">
								<span class="input-group-btn">
									<button class="btn btn-default" type="button" onclick="$(this).closest('form').submit()"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></button>
								</span>
							  </div>
						</div>
					</div>
				</form>
				<g:if test="${taskList?.size() > 0}">
					<div class="margin-top text-muted small"><g:message code="kola.search.hits.displaying" args="${[entitiesName, params.offset + 1, Math.min(params.offset + params.max, taskCount), taskCount]}" />:</div>
				</g:if>
			</div>
			<g:if test="${taskList?.size() > 0}">
				<g:set var="filterParams" value="${[own:params.own, ownCompany:params.ownCompany, assigned:params.assigned, isTemplate:params.isTemplate]}" />
				<g:set var="sortParams" value="${[resetOffset:true] << filterParams}" />
				<div class="table-responsive">
					<table class="table table-striped">
						<thead>
							<tr>
								<g:sortableColumn property="name" title="${message(code: 'kola.task.title')}" params="${sortParams}" />
								<g:if test="${!params.isTemplate?.toBoolean()}">
									<g:sortableColumn property="due" title="${message(code: 'kola.task.due')}" params="${sortParams}" />
									<g:sortableColumn property="done" class="text-center" title="${message(code: 'kola.task.done')}" params="${sortParams}" />
									<g:sortableColumn property="ap.lastName" title="${message(code: 'kola.task.assignee')}" params="${sortParams}" />
								</g:if>
								<g:sortableColumn property="cp.lastName" title="${message(code: 'kola.meta.creator')}" params="${sortParams}" />
								<g:sortableColumn property="cp.company" title="${message(code: 'de.httc.plugin.user.company')}" params="${sortParams}" />
								<g:sortableColumn property="lastUpdated" title="${message(code: 'kola.meta.lastUpdated')}" params="${sortParams}" />
							</tr>
						</thead>
						<tbody>
						<g:each in="${taskList}" status="i" var="task">
							<tr>
								<td><g:link action="show" id="${task.id}" params="${[isTemplate:params.isTemplate]}">${fieldValue(bean: task, field: "name")}</g:link></td>
								<g:if test="${!params.isTemplate?.toBoolean()}">
									<td><g:formatDate date="${task.due}" type="date"/></td>
									<td class="text-center"><i class="fa fa-fw ${task.done ? 'fa-check text-success' : 'fa-minus text-warning'}"></i></td>
									<td>${fieldValue(bean: task.assignee?.profile, field: "displayNameReverse")}</td>
								</g:if>
								<td>${fieldValue(bean: task.creator?.profile, field: "displayNameReverse")}</td>
								<td>${fieldValue(bean: task.creator?.profile, field: "company")}</td>
								<td><g:formatDate date="${task.lastUpdated}" type="date"/></td>
							</tr>
						</g:each>
						</tbody>
					</table>
				</div>
				<g:if test="${params.max < taskCount}">
					<div class="pagination pull-right">
						<g:paginate total="${taskCount ?: 0}" params="${filterParams}" />
					</div>
				</g:if>
			</g:if>
		</div>
		<g:if test="${!(taskList?.size() > 0)}">
			<div class="alert alert-danger margin"><g:message code="app.filter.empty" args="${[entitiesName]}" /></div>
		</g:if>
	</body>
</html>
