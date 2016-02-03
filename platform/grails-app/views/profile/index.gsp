<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.profile')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<g:form action="update" method="PUT" class="form-horizontal" autocomplete="off" enctype="multipart/form-data">
			<h1 class="page-header clearfix">
				<g:message code="default.edit.label" args="[entityName]" />
				<div class="buttons pull-right">
					<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
				</div>
			</h1>
			<g:if test="${flash.message}">
				<div class="message alert alert-success" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${user}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${user}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:hasErrors bean="${user?.profile}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${user.profile}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:hiddenField name="version" value="${user?.version}" />
			<g:render template="/user/form" plugin="user"/>
			<g:render template="/user/profile" plugin="user"/>
			<div class="buttons pull-right">
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
			</div>
		</g:form>
	</body>
</html>
