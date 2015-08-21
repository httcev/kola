<html>
<head>
	<title><g:message code="kola.user.forgotPassword"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
	<div id="loginWrapper" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h1 class="panel-title"><b><g:message code="kola.user.forgotPassword" /></b></h1>
			</div>
			<form action="forgotPassword" method="POST" role="form" autocomplete="off">
				<div class="panel-body">
					<g:if test="${emailSent}">
						<div class="alert alert-success">
							<g:message code="kola.user.forgotPassword.sent"/>
						</div>
					</g:if>
					<g:else>
						<p><g:message code="kola.user.forgotPassword.prompt"/></p>
						<div class="form-group">
							<label for="username" class="control-label"><g:message code="kola.user.loginName" />:</label>
							<input type="text" name="username" class="form-control" id="username" placeholder="<g:message code="kola.user.loginName"/>..." autocapitalize="off" autofocus>
						</div>
					</g:else>
				</div>
				<g:if test="${!emailSent}">
					<div class="panel-footer text-center">
						<button type="submit" class="btn btn-default"><g:message code="kola.user.forgotPassword.button" /></button>
					</div>
				</g:if>
			</form>
		</div>
	</div>
</body>
</html>
