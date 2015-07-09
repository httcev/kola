<g:set var="hashIds" bean="hashIds"/>

<html>
	<head>
		<meta name="layout" content="main">
		<title>Suche</title>
	</head>
	<body>
		<div id="show-search" class="content scaffold-search" role="main">
			<g:form action="search" method="GET">
				<input type="search" name="query" value="${params.query}">
				<button type="submit" class="search">${message(code: 'default.button.search.label', default: 'Search')}</button>
			</g:form>
			<g:if test="${params.query}">
				<g:if test="${results?.total > 0}">
					<p>Anzahl Treffer: ${results?.total}</p>
					<g:set var="searchResults" value="${results?.searchResults}"/>
					<g:set var="highlights" value="${results?.highlight}"/>
					<ul>
					<g:each var="hit" in="${searchResults}" status="i">
						<li>
							<div><a href="${createLink(mapping:'viewAsset', id:hashIds.encode(hit.id))}">${hit.name}</a></div>
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
					Die Suche nach '${params.query}' ergab keine Treffer.
				</g:else>
			</g:if>
		</div>
	</body>
</html>