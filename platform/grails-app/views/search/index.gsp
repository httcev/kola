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
		</g:form>
		<g:if test="${params.q}">
			<g:render template="searchResult" />
		</g:if>
	</body>
</html>