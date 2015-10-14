<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.settings')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
		<asset:stylesheet src="bootstrap-markdown.min.css"/>
		<asset:javascript src="marked.min.js"/>
		<asset:javascript src="bootstrap-markdown.js"/>
		<asset:javascript src="bootstrap-markdown.de.js"/>
	</head>
	<body>
		<g:form class="form-horizontal" url="[controller:'settings', action:'update']" method="put"  enctype="multipart/form-data">
			<h1 class="page-header clearfix">
				<g:message code="default.edit.label" args="[entityName]" />
				<div class="buttons pull-right">
					<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
				</div>
			</h1>
			<g:if test="${flash.message}">
				<div class="message alert alert-success" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${settingsInstance}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${settingsInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:hiddenField name="version" value="${settingsInstance?.version}" />

			<div class="form-group ${hasErrors(bean: settingsInstance, field: 'welcomeHeader', 'error')} required">
				<label for="welcomeHeader" class="col-sm-2 control-label"><g:message code="kola.settings.welcome.header" /><span class="required-indicator">*</span>:</label>
				<div class="col-sm-10"><g:textField name="welcomeHeader" value="${settingsInstance?.welcomeHeader}" class="form-control" required="" /></div>
			</div>
			<div class="form-group ${hasErrors(bean: settingsInstance, field: 'welcomeBody', 'error')} required">
				<label for="welcomeBody" class="col-sm-2 control-label"><g:message code="kola.settings.welcome.body" />:</label>
				<div class="col-sm-10"><g:textArea name="welcomeBody" rows="8" value="${settingsInstance?.welcomeBody}" class="form-control" data-provide="markdown-editable" data-iconlibrary="fa" data-language="de" data-hidden-buttons="cmdImage cmdCode cmdQuote" /></div>
			</div>

			<div class="buttons pull-right">
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
			</div>
		</g:form>
	</body>
</html>
