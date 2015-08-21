<html>
	<head>
		<g:if test="${!request.xhr}">
			<meta name="layout" content="main">
		</g:if>
		<title><g:message code="kola.search.title" /></title>
	</head>
	<body>
		<g:form action="." method="GET" class="form-inline">
			<div class="form-group">
				<input type="search" name="q" class="form-control" value="${params.q}" placeholder="${message(code:'kola.search.query')}..." autofocus>
				<button type="submit" class="search btn btn-default"><g:message code="kola.search" /></button>
			</div>
			<g:if test="${params.hideFilter}">
				<input type="hidden" name="type" value="${params.type}">
				<input type="hidden" name="subType" value="${params.subType}">
				<input type="hidden" name="hideFilter" value="true">
			</g:if>
			<g:else>
				SEARCH FILTER
			</g:else>
		</g:form>
		<g:if test="${params.q}">
			<g:render template="searchResult" />
		</g:if>
	</body>
</html>