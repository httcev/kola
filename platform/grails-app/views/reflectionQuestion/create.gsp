<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.reflectionQuestion')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.reflectionQuestions')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="app.home" /></g:link></li>
			<li><g:link action="index">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.create.label" args="[entityName]" /></li>
		</ol>
		<g:form url="[resource:reflectionQuestion, action:'save']" class="form-horizontal" autocomplete="off">
			<h1 class="page-header">
				<g:message code="default.create.label" args="[entityName]" />
			</h1>
			<g:hasErrors bean="${reflectionQuestion}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${reflectionQuestion}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:render template="form"/>
			<div class="buttons pull-right">
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.create.label" default="Create" /></button>
			</div>
		</g:form>
	</body>
</html>
