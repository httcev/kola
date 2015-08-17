<g:if test="${results?.total > 0}">
	<p class="margin">Zeige Treffer ${params.offset + 1} - ${Math.min(params.offset + params.max, results?.total)} von ${results?.total}</p>
	<g:set var="searchResults" value="${results?.searchResults}"/>
	<g:set var="highlights" value="${results?.highlight}"/>
	<ul class="search-result list-group">
	<g:each var="hit" in="${searchResults}" status="i">
		<li class="search-result-hit list-group-item clearfix" id="${hit.id}">
			<h4 class="list-group-item-heading">
				<a href="${createLink(resource:hit, action:'show')}" class="search-result-link">${hit.name?.take(100)}</a>
			</h4>
			<p class="list-group-item-text">
			<g:each var="field" in="['description', 'indexText']">
				<g:each var="fragment" in="${highlights[i][field]?.fragments}">
					<div>${raw(fragment.toString())}</div>
				</g:each>
			</g:each>
			</p>
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
