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
			<g:hasErrors>
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:each in="${settingList}" status="i" var="setting">
				<div class="form-group ${hasErrors(bean: setting, field: 'value', 'error')} ${setting.required ? ' required' : ''}">
					<input type="hidden" name="settings[${i}].id" value="${setting.id}">
					<input type="hidden" name="settings[${i}].version" value="${setting.version}">
					<label for="${setting.key}" class="col-sm-2 control-label">
						<g:message code="${setting.prefix}.${setting.key}" />:
						<g:if test="${setting.required}"><span class="required-indicator">*</span>:</g:if>
					</label>
					<div class="col-sm-10">
						<g:if test="${setting.multiline}">
							<textArea id="${setting.key}" name="settings[${i}].value" class="form-control" rows="8" data-provide="markdown" data-iconlibrary="fa" data-language="de" data-hidden-buttons="cmdImage cmdCode cmdQuote"${setting.required ? " required" : ""}>${setting.value}</textArea>
						</g:if>
						<g:else>
							<input id="${setting.key}" type="text" name="settings[${i}].value" value="${setting.value}" class="form-control"${setting.required ? " required" : ""}>
						</g:else>
					</div>
				</div>
			</g:each>
			<div class="buttons pull-right">
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
			</div>
		</g:form>
	</body>
</html>
