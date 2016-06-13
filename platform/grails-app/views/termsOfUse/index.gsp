<g:set var="settings" value="${kola.Settings.getSettings()}"/>

<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="kola.termsOfUse" /></title>
	</head>
	<body>
		<h1 class="page-header"><g:message code="kola.termsOfUse" /></h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-danger" role="status">${flash.message}</div>
		</g:if>
		<httc:markdown>${terms}</httc:markdown>
		<g:if test="${showAcceptControls}">
			<g:form controller="termsOfUse" action="accept" method="post" class="form form-inline">
				<div class="checkbox">
					<label><g:checkBox name="accepted" value="" /> <g:message code="kola.termsOfUse.agree" /></label>
				</div>
				<button type="submit" class="btn btn-primary"><g:message code="kola.termsOfUse.accept" /></button>
			</g:form>
		</g:if>
	</body>
</html>
