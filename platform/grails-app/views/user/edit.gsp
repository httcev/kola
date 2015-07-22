<%@ page import="kola.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/admin"><g:message code="default.admin.label" default="Administration" /></g:link></li>
			<li><g:link class="index" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			<li class="active"><g:message code="default.edit.label" args="[entityName]" /></li>
		</ol>
		<h1 class="page-header">
			<g:message code="default.edit.label" args="[entityName]" />
			<g:link class="delete btn btn-danger btn-sm pull-right" action="delete" id="${userInstance.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
				<span class="fa-stack">
				  <i class="fa fa-user fa-stack-2x"></i>
				  <i class="fa fa-remove fa-stack-2x text-primary"></i>
				</span>
			</g:link>
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
		<g:form url="[resource:userInstance, action:'update']" method="PUT" class="form-horizontal" enctype="multipart/form-data" autocomplete="off">
			<g:hiddenField name="version" value="${userInstance?.version}" />
			<g:render template="form"/>
			<g:render template="account"/>
			<g:render template="profile"/>
			<fieldset class="buttons">
				<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
			</fieldset>
		</g:form>
	</body>
</html>
