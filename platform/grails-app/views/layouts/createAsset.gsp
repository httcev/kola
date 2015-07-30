<g:applyLayout name="main">
<html>
	<head>
		<g:set var="entityName" value="${message(code: 'asset.label', default: 'Asset')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="default.home.label" default="Home" /></g:link></li>
			<li><g:link controller="repository" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			<li class="active"><g:message code="default.create.label" args="[entityName]" /></li>
		</ol>
		<h1 class="page-header"><g:message code="default.create.label" args="[entityName]" /></h1>
		<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
		</g:if>
		<g:hasErrors bean="${cmd}">
		<ul class="errors alert alert-danger" role="alert">
		<g:eachError bean="${cmd}" var="error">
			<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
		</g:eachError>
		</ul>
		</g:hasErrors>
		<g:form action="create" enctype="multipart/form-data" class="form-horizontal">
			<g:layoutBody />
		</g:form>
	</body>
</html>
</g:applyLayout>