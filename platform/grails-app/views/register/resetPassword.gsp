<html>
<head>
	<title><g:message code="spring.security.ui.resetPassword.title"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
	<div id="loginWrapper" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h1 class="panel-title"><b><g:message code="spring.security.ui.resetPassword.header" /></b></h1>
			</div>
			<form action="resetPassword" method="POST" role="form" autocomplete="off">
				<g:hiddenField name='t' value='${token}'/>
				<div class="panel-body">
					<g:if test="${flash.message}">
						<div class="alert alert-danger">
							${flash.message}
						</div>
					</g:if>
					<p><g:message code="spring.security.ui.resetPassword.description"/></p>
					<div class="form-group">
						<label for="password" class="control-label"><g:message code="springSecurity.login.password.label" /></label>
						<input type="password" name="password" class="form-control" id="password" placeholder="<g:message code="springSecurity.login.password.label"/>" autofocus>
					</div>
					<div class="form-group">
						<label for="password2" class="control-label"><g:message code="springSecurity.login.password.label" /></label>
						<input type="password" name="password2" class="form-control" id="password2" placeholder="<g:message code="springSecurity.login.password.label"/>" autofocus>
					</div>
				</div>
				<div class="panel-footer text-center">
					<button type="submit" class="btn btn-default"><g:message code="spring.security.ui.resetPassword.submit" /></button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
