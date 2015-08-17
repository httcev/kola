<%@ page import="kola.ReflectionQuestion" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'reflectionQuestion.label', default: 'Reflexionsaufforderung')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			<li><g:link class="index" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			<li class="active"><g:message code="default.edit.label" args="[entityName]" /></li>
		</ol>
		<g:form url="[resource:reflectionQuestionInstance, action:'update']" method="PUT" class="form-horizontal" autocomplete="off">
			<h1 class="page-header">
				<g:message code="default.edit.label" args="[entityName]" />
				<div class="buttons pull-right">
					<g:link class="delete btn btn-danger" action="delete" id="${reflectionQuestionInstance.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
						<i class="fa fa-times"></i>
					</g:link>
<%--				
					<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
--%>					
				</div>
			</h1>
			<g:hasErrors bean="${reflectionQuestionInstance}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${reflectionQuestionInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:hiddenField name="version" value="${reflectionQuestionInstance?.version}" />
			<g:render template="form"/>
			<div class="buttons pull-right">
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
			</div>
		</g:form>
	</body>
</html>
