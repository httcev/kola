<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.task.documentation')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.task.documentations')}" />
		<title>${entitiesName}</title>
	</head>
	<body>
		<h1 class="page-header clearfix">
			${entitiesName}
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<form method="get">
			<div class="row filter">
				<div class="col-xs-12">
					<b><g:message code="kola.filter" /></b>:
					<span class="checkbox">
						<label><input name="own" type="checkbox" onclick="$(this).closest('form').submit()"${params.own ? ' checked' : ''}> <g:message code="kola.filter.own" /></label>
					</span>
					<span class="checkbox">
						<label><input name="ownCompany" type="checkbox" onclick="$(this).closest('form').submit()"${params.ownCompany ? ' checked' : ''}> <g:message code="kola.filter.ownCompany" /></label>
					</span>
				</div>
			</div>
		</form>
		<g:if test="${taskDocumentationList?.size() > 0}">
			<p class="margin text-muted small"><g:message code="kola.search.hits.displaying" args="${[entitiesName, params.offset + 1, Math.min(params.offset + params.max, taskDocumentationCount), taskDocumentationCount]}" />:</p>
			<g:set var="filterParams" value="${[own:params.own, ownCompany:params.ownCompany]}" />
			<g:set var="sortParams" value="${[resetOffset:true] << filterParams}" />
			<div class="table-responsive">
				<table class="table table-striped">
					<thead>
						<tr>
							<th><g:message code="kola.task.documentation" /></th>
							<g:sortableColumn property="cp.lastName" title="${message(code: 'kola.meta.creator')}" params="${sortParams}" />
							<g:sortableColumn property="cp.company" title="${message(code: 'de.httc.plugin.user.company')}" params="${sortParams}" />
							<g:sortableColumn property="lastUpdated" title="${message(code: 'kola.meta.lastUpdated')}" params="${sortParams}" />
							<th><g:message code="kola.task" /></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${taskDocumentationList}" status="i" var="taskDocumentation">
                        <g:set var="task" value="${taskDocumentation.reference instanceof kola.TaskStep ? taskDocumentation.reference.task : taskDocumentation.reference}" />
                        <g:set var="text">
                            <g:if test="${taskDocumentation.text?.length() > 0}"><kola:abbreviate>${taskDocumentation.text}</kola:abbreviate></g:if>
                            <g:else>${taskDocumentation.attachments.size()} <g:message code="${taskDocumentation.attachments.size() == 1 ? 'kola.task.attachment' : 'kola.task.attachments'}" /></g:else>
                        </g:set>
						<tr>
							<td><g:link resource="${task}" action="show" fragment="documentations_${taskDocumentation.id}">${text}</g:link></td>
							<td>${fieldValue(bean: taskDocumentation.creator?.profile, field: "displayNameReverse")}</td>
							<td>${fieldValue(bean: taskDocumentation.creator?.profile, field: "company")}</td>
							<td><g:formatDate date="${taskDocumentation.lastUpdated}" type="date"/></td>
							<td>${fieldValue(bean: task, field: "name")}</td>
						</tr>
					</g:each>
					</tbody>
				</table>
			</div>
			<g:if test="${params.max < taskDocumentationCount}">
				<div class="pagination pull-right">
					<g:paginate total="${taskDocumentationCount ?: 0}" params="${filterParams}" />
				</div>
			</g:if>
		</g:if>
		<g:else>
			<div class="alert alert-danger margin"><g:message code="app.filter.empty" args="${[entitiesName]}" /></div>
		</g:else>
	</body>
</html>
