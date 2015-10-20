<g:if test="${results?.total > 0}">
	<p class="margin text-muted small"><g:message code="kola.search.hits.displaying" args="${[message(code:'kola.search.hits'), params.offset + 1, Math.min(params.offset + params.max, results?.total), results?.total]}" />:</p>
	<g:set var="searchResults" value="${results?.searchResults}"/>
	<g:set var="highlights" value="${results?.highlight}"/>
	<ul class="search-result list-group">
	<g:each var="hit" in="${searchResults}" status="hitIndex">
		<li class="search-result-hit list-group-item clearfix" id="${hit.id}">
			<h4 class="list-group-item-heading">
				<a href="${createLink(resource:hit, action:'show')}" class="search-result-link ${(hit.class.name-'kola.').toLowerCase()}">
					${hit.name?.take(100)}
				</a>
			</h4>
			<p class="list-group-item-text">
			<g:if test="${highlights[hitIndex]['indexText']?.fragments}">
				<g:each var="fragment" in="${highlights[hitIndex]['indexText']?.fragments}">
					<div>${raw(fragment.toString())}</div>
				</g:each>
			</g:if>
			<g:elseif test="${highlights[hitIndex]['description']?.fragments}">
				<g:each var="fragment" in="${highlights[hitIndex]['description']?.fragments}">
					<kola:markdown>${raw(fragment.toString())}</kola:markdown>
				</g:each>
			</g:elseif>
			<g:elseif test="${hit.hasProperty('description')}">
				<kola:markdown>${hit.description?.take(100)}</kola:markdown>
			</g:elseif>
			</p>
		</li>
	</g:each>
	</ul>
	<g:if test="${params.max < results?.total}">
		<div class="pagination pull-right">
			<g:paginate total="${results.total}" params="${params}"/>
		</div>
	</g:if>
</g:if>
<g:elseif test="${params.q}">
	<div class="alert alert-warning margin"><g:message code="kola.search.noHits" args="${[params.q]}" /></div>
</g:elseif>
