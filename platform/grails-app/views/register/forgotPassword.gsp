<html>
<head>
	<title><g:message code="spring.security.ui.forgotPassword.title"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
	<div id="loginWrapper" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h1 class="panel-title"><b><g:message code="spring.security.ui.forgotPassword.header" /></b></h1>
			</div>
			<form action="forgotPassword" method="POST" role="form" autocomplete="off">
				<div class="panel-body">
					<g:if test="${emailSent}">
						<div class="alert alert-success">
							<g:message code="spring.security.ui.forgotPassword.sent"/>
						</div>
					</g:if>
					<g:else>
						<p><g:message code="spring.security.ui.forgotPassword.description"/></p>
						<div class="form-group">
							<label for="username" class="control-label"><g:message code="spring.security.ui.forgotPassword.username" /></label>
							<input type="text" name="username" class="form-control" id="username" placeholder="<g:message code="springSecurity.login.username.label"/>" autocapitalize="off" autofocus>
						</div>
					</g:else>
				</div>
				<g:if test="${!emailSent}">
					<div class="panel-footer text-center">
						<button type="submit" class="btn btn-default"><g:message code="spring.security.ui.forgotPassword.submit" /></button>
					</div>
				</g:if>
			</form>
		</div>
	</div>
</body>
</html>
