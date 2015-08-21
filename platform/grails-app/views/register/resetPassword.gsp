<html>
<head>
	<title><g:message code="kola.user.resetPassword"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
	<div id="loginWrapper" class="container">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h1 class="panel-title"><b><g:message code="kola.user.resetPassword" /></b></h1>
			</div>
			<form action="resetPassword" method="POST" role="form" autocomplete="off">
				<g:hiddenField name='t' value='${token}'/>
				<div class="panel-body">
					<g:if test="${flash.message}">
						<div class="alert alert-danger">
							${flash.message}
						</div>
					</g:if>
					<p><g:message code="kola.user.resetPassword.prompt"/></p>
					<div class="form-group">
						<label for="password" class="control-label"><g:message code="kola.user.updatePassword.new" />:</label>
						<input type="password" name="password" class="form-control" id="password" placeholder="<g:message code="kola.user.password"/>..." autofocus>
					</div>
					<div class="form-group">
						<label for="password2" class="control-label"><g:message code="kola.user.updatePassword.new2" />:</label>
						<input type="password" name="password2" class="form-control" id="password2" placeholder="<g:message code="kola.user.password"/>..." autofocus>
					</div>
				</div>
				<div class="panel-footer text-center">
					<button type="submit" class="btn btn-default"><g:message code="kola.user.updatePassword" /></button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
