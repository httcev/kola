<head>
	<meta name="layout" content="main" />
	<title><g:message code="springSecurity.login.title" /></title>
</head>
<body>
	<div id="loginWrapper" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h1 class="panel-title"><b><g:message code="kola.user.updatePassword.prompt" /></b></h1>
			</div>
			<form action="updatePassword" method="POST" role="form" autocomplete="off">
				<div class="panel-body">
					<g:if test="${flash.message}">
						<div class="alert alert-danger">
							${flash.message}
						</div>
					</g:if>
					<div class="form-group">
						<label for="username" class="control-label"><g:message code="springSecurity.login.username.label" />:</label>
						<span class="form-control">${username}</span>
					</div>
					<div class="form-group">
						<label for="password" class="control-label"><g:message code="kola.user.updatePassword.current" />:</label>
						<input name="password" type="password" class="form-control" id="password" placeholder="<g:message code="springSecurity.login.password.label"/>...">
					</div>
					<div class="form-group">
						<label for="password_new" class="control-label"><g:message code="kola.user.updatePassword.new" />:</label>
						<input name="password_new" type="password" class="form-control" id="password_new" placeholder="<g:message code="springSecurity.login.password.label"/>...">
					</div>
					<div class="form-group">
						<label for="password_new_2" class="control-label"><g:message code="kola.user.updatePassword.new2" />:</label>
						<input name="password_new_2" type="password" class="form-control" id="password_new_2" placeholder="<g:message code="springSecurity.login.password.label"/>...">
					</div>
				</div>
				<div class="panel-footer text-center">
					<button type="submit" class="btn btn-default"><g:message code="kola.user.updatePassword" /></button>
				</div>
			</form>
		</div>
	</div>
</body>
