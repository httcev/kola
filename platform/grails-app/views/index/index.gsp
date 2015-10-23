<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="kola.welcome.to" /></title>
	</head>
	<body>
		<div class="jumbotron">
			<h1>${welcomeHeader}</h1>
			<kola:markdown>${welcomeBody}</kola:markdown>
			<p><g:link controller="login" class="btn btn-primary btn-lg" role="button"><i class="fa fa-sign-in"></i> <g:message code="kola.signin" /></g:link></p>
		</div>
	</body>
</html>
