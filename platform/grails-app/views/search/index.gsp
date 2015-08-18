<html>
	<head>
		<g:if test="${!request.xhr}">
			<meta name="layout" content="main">
		</g:if>
		<title>Suche</title>
	</head>
	<body>
		<g:form action="." method="GET" class="form-inline">
			<div class="form-group">
				<input type="search" name="q" class="form-control" value="${params.q}" placeholder="Search" autofocus>
				<button type="submit" class="search btn btn-default">${message(code: 'default.button.search.label', default: 'Search')}</button>
			</div>
			<g:if test="${params.hideFilter}">
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