<g:set var="assetService" bean="assetService"/>

<g:if test="${results?.total > 0}">
	<p>Zeige Treffer ${params.offset + 1} - ${Math.min(params.offset + params.max, results?.total)} von ${results?.total}</p>
	<g:set var="searchResults" value="${results?.searchResults}"/>
	<g:set var="highlights" value="${results?.highlight}"/>
	<ul class="search-result">
	<g:each var="hit" in="${searchResults}" status="i">
		<li class="search-result-hit" id="${hit.id}">
			<div><a href="${assetService.createEncodedLink(hit)}" class="search-result-link">${hit.name}</a></div>
			<g:each var="field" in="['description', 'indexText']">
				<g:each var="fragment" in="${highlights[i][field]?.fragments}">
					<div>${field}: ${raw(fragment.toString())}</div>
				</g:each>
			</g:each>
		</li>
	</g:each>
	</ul>
	<div class="pagination pull-right">
		<g:paginate total="${results.total ?: 0}" params="${[q:params.q]}"/>
	</div>
</g:if>
<g:elseif test="${params.q}">
	<div class="alert alert-warning margin">Die Suche nach '${params.q}' ergab keinen Treffer.</div>
</g:elseif>
