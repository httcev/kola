<g:set var="assetService" bean="assetService"/>

<html>
	<head>
		<meta name="layout" content="main">
		<title>Suche</title>
	</head>
	<body>
		<div id="show-search" class="content scaffold-search" role="main">
			<g:form action="." method="GET" class="form-inline">
				<div class="form-group">
					<input type="search" name="q" class="form-control" value="${params.q}" placeholder="Search" autofocus>
					<button type="submit" class="search btn btn-default">${message(code: 'default.button.search.label', default: 'Search')}</button>
				</div>
			</g:form>
			<g:if test="${params.q}">
				<g:if test="${results?.total > 0}">
					<p>Anzahl Treffer: ${results?.total}</p>
					<g:set var="searchResults" value="${results?.searchResults}"/>
					<g:set var="highlights" value="${results?.highlight}"/>
					<ul>
					<g:each var="hit" in="${searchResults}" status="i">
						<li>
							<div><a href="${assetService.createEncodedLink(hit)}">${hit.name}</a></div>
							<g:each var="field" in="['description', 'indexText']">
								<g:each var="fragment" in="${highlights[i][field]?.fragments}">
									<div>${field}: ${raw(fragment.toString())}</div>
								</g:each>
							</g:each>
						</li>
					</g:each>
					</ul>
				</g:if>
				<g:else>
					Die Suche nach '${params.q}' ergab keinen Treffer.
				</g:else>
			</g:if>
		</div>
	</body>
</html>