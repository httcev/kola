
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'de.httc.plugin.qaa.question')}" />
		<g:set var="entitiesName" value="${message(code: 'de.httc.plugin.qaa.questions')}" />
		<title>${entitiesName}</title>
	</head>
	<body>
		<h1 class="page-header clearfix">
			${entitiesName}
			<g:link class="create btn btn-primary pull-right" action="create" title="${message(code: 'default.new.label', args:[entityName])}">
				<i class="fa fa-plus"></i> <g:message code="default.button.create.label" />
			</g:link>
		</h1>
		<g:if test="${flash.error}">
			<div class="message alert alert-danger" role="status">${flash.error}</div>
		</g:if>
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
		<g:if test="${questionList?.size() > 0}">
			<p class="margin text-muted small"><g:message code="kola.search.hits.displaying" args="${[entitiesName, params.offset + 1, Math.min(params.offset + params.max, questionCount), questionCount]}" />:</p>
			<g:set var="filterParams" value="${[own:params.own, ownCompany:params.ownCompany]}" />
			<g:set var="sortParams" value="${[resetOffset:true] << filterParams}" />
			<div class="table-responsive">
				<table class="table table-striped">
					<thead>
						<tr>
							<g:sortableColumn property="title" title="${message(code: 'kola.meta.name')}" params="${sortParams}" />
							<g:sortableColumn property="cp.lastName" title="${message(code: 'kola.meta.creator')}" params="${sortParams}" />
							<g:sortableColumn property="cp.company" title="${message(code: 'de.httc.plugin.user.company')}" params="${sortParams}" />
							<g:sortableColumn property="lastUpdated" title="${message(code: 'kola.meta.lastUpdated')}" params="${sortParams}" />
						</tr>
					</thead>
					<tbody>
					<g:each in="${questionList}" status="i" var="question">
						<tr>
							<td><g:link action="show" id="${question.id}">${fieldValue(bean: question, field: "title")}</g:link></td>
							<td>${fieldValue(bean: question.creator?.profile, field: "displayNameReverse")}</td>
							<td>${fieldValue(bean: question.creator?.profile, field: "company")}</td>
							<td><g:formatDate date="${question.lastUpdated}" type="date"/></td>
						</tr>
					</g:each>
					</tbody>
				</table>
			</div>
			<g:if test="${params.max < questionCount}">
				<div class="pagination pull-right">
					<g:paginate total="${questionCount ?: 0}" params="${filterParams}" />
				</div>
			</g:if>
		</g:if>
		<g:else>
			<div class="alert alert-danger margin"><g:message code="app.filter.empty" args="${[entitiesName]}" /></div>
		</g:else>
	</body>
</html>
