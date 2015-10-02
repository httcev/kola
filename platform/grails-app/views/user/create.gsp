<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.user')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.users')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="kola.home" /></g:link></li>
			<li><g:link action="index">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.create.label" args="[entityName]" /></li>
		</ol>
		<g:form url="[resource:userInstance, action:'save']" class="form-horizontal" autocomplete="off" enctype="multipart/form-data">
			<h1 class="page-header clearfix">
				<g:message code="default.create.label" args="[entityName]" />
				<div class="buttons pull-right">
					<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.create.label" default="Create" /></button>
				</div>
			</h1>
			<g:hasErrors bean="${userInstance}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${userInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:hasErrors bean="${userInstance.profile}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${userInstance.profile}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:render template="form"/>
			<g:render template="account"/>
			<g:render template="profile"/>
			<div class="buttons pull-right">
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.create.label" default="Create" /></button>
			</div>
		</g:form>
	</body>
</html>
