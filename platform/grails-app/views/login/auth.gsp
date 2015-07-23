<head>
	<meta name="layout" content="main" />
	<title><g:message code="springSecurity.login.title" /></title>
</head>
<body>
	<div id="loginWrapper" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h1 class="panel-title"><b><g:message code="springSecurity.login.header" /></b></h1>
			</div>
			<form action="${postUrl}" method="POST" role="form" autocomplete="off">
				<div class="panel-body">
					<g:if test="${flash.message}">
						<div class="alert alert-danger">
							${flash.message}
						</div>
					</g:if>
					<div class="form-group">
						<label for="username" class="control-label"><g:message code="springSecurity.login.username.label" /></label>
						<input type="text" name="j_username" class="form-control" id="username" placeholder="<g:message code="springSecurity.login.username.label"/>" autocapitalize="off" autofocus>
					</div>
					<div class="form-group">
						<label for="password" class="control-label"><g:message code="springSecurity.login.password.label" /></label>
						<input name="j_password" type="password" class="form-control" id="password" placeholder="<g:message code="springSecurity.login.password.label"/>">
					</div>
					<div class="form-group">
						<div class="checkbox">
							<label><input type="checkbox" name="${rememberMeParameter}" <g:if test='${hasCookie}'>checked='checked'</g:if>> <g:message code="springSecurity.login.remember.me.label" /></label>
						</div>
					</div>
				</div>
				<div class="panel-footer text-center">
					<button type="submit" class="btn btn-default"><g:message code="springSecurity.login.button" /></button>
				</div>
			</form>
		</div>
	</div>
</body>
