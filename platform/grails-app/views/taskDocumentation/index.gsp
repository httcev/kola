<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.task.documentation')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.task.documentations')}" />
		<title>${entitiesName}</title>
		<asset:stylesheet src="chosen-adapted.css"/>
		<asset:javascript src="chosen.jquery.js"/>
	</head>
	<body>
		<h1 class="page-header clearfix">
			${entitiesName}
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>

		<form method="get" class="well" id="filter-form">
			<input type="hidden" name="isTemplate" value="${params.isTemplate}">
			<b><g:message code="app.filter" />:</b>
			<div class="row filter">
				<div class="col-md-4 margin-vertical">
					<select id="createdChooser" name="createdBy" class="form-control-dummy" value="${params.createdBy}" data-placeholder="${message(code:'app.meta.createdBy')}...">
						<option value="all"${params.createdBy=="all" ? " selected" : ""}></option>
						<option value="own"${params.createdBy=="own" ? " selected" : ""}><g:message code="app.meta.createdBy" />: <g:message code="app.meta.me" /></option>
						<option value="company"${params.createdBy=="company" ? " selected" : ""}><g:message code="app.meta.createdBy" />: <g:message code="app.meta.myCompany" /></option>
					</select>
				</div>
			</div>
		</form>
		<g:if test="${taskDocumentationList?.size() > 0}">
			<p class="margin text-muted small"><g:message code="app.search.hits.displaying" args="${[entitiesName, params.offset + 1, Math.min(params.offset + params.max, taskDocumentationCount), taskDocumentationCount]}" />:</p>
			<g:set var="filterParams" value="${[createdBy:params.createdBy]}" />
			<g:set var="sortParams" value="${[resetOffset:true] << filterParams}" />
			<div class="table-responsive">
				<table class="table table-striped">
					<thead>
						<tr>
							<th><g:message code="kola.task.documentation" /></th>
							<g:sortableColumn property="cp.lastName" title="${message(code: 'app.meta.createdBy')}" params="${sortParams}" />
							<g:sortableColumn property="cpc.label" title="${message(code: 'de.httc.plugin.user.company')}" params="${sortParams}" />
							<g:sortableColumn property="lastUpdated" title="${message(code: 'app.meta.lastUpdated')}" params="${sortParams}" />
							<th><g:message code="kola.task" /></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${taskDocumentationList}" status="i" var="taskDocumentation">
            <g:set var="task" value="${taskDocumentation.reference instanceof kola.TaskStep ? taskDocumentation.reference.task : taskDocumentation.reference}" />
            <g:set var="text">
              <g:if test="${taskDocumentation.text?.length() > 0}"><httc:abbreviate>${taskDocumentation.text}</httc:abbreviate></g:if>
              <g:else>${taskDocumentation.attachments.size()} <g:message code="${taskDocumentation.attachments.size() == 1 ? 'kola.task.attachment' : 'kola.task.attachments'}" /></g:else>
            </g:set>
						<tr>
							<td><g:link resource="${task}" action="show" fragment="documentations_${taskDocumentation.id}">${text}</g:link></td>
							<td>${fieldValue(bean: taskDocumentation.creator?.profile, field: "displayNameReverse")}</td>
							<td>${taskDocumentation.creator?.profile?.company?.label}</td>
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

		<script>
			$(function() {
				$("#filter-form select").change(function() {
					$(this).closest('form').submit();
				});
				$("#createdChooser").chosen({
					disable_search_threshold: 10
					, width:"100%"
					, allow_single_deselect: true
					, inherit_select_classes: true
				});
			});
		</script>
	</body>
</html>
