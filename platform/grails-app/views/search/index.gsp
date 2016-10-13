<html>
	<head>
		<g:if test="${!request.xhr}">
			<meta name="layout" content="main">
		</g:if>
		<title><g:message code="kola.search.title" /></title>
	</head>
	<body>
		<g:form action="." method="GET" class="form-horizontal">
			<div class="form-group">
				<div class="col-xs-8">
					<input type="search" name="q" class="form-control" value="${params.q}" placeholder="${message(code:'kola.search.query')}..." onfocus="this.value = this.value;" autofocus>
				</div>
				<div class="col-xs-4">
					<button type="submit" class="search btn btn-default"><i class="fa fa-search"></i> <g:message code="kola.search" /></button>
				</div>
			</div>
			<g:if test="${params.hideFilter}">
				<input type="hidden" name="type" value="${params.type}">
				<input type="hidden" name="typeLabel" value="${params.typeLabel}">
				<input type="hidden" name="hideFilter" value="true">
			</g:if>
			<g:else>
<%--
				<div class="form-group">
					<label class="col-sm-2 control-label"><g:message code="app.filter"/>:</label>
					<div class="col-sm-3">
						<div class="checkbox">
							<label>
					        	<input type="checkbox"> 
					        </label>
						</div>
					</div>
				</div>
--%>
			</g:else>
		</g:form>
		<g:if test="${params.q}">
			<g:render template="searchResult" />
		</g:if>
	</body>
</html>