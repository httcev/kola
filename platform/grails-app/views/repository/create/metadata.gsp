<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'asset.label', default: 'Asset')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#create-asset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="create-asset" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${assetInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${assetInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form action="create">
				<fieldset class="form">
					<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'name', 'error')} required">
						<label for="name">
							<g:message code="asset.name.label" default="Name" />
							<span class="required-indicator">*</span>
						</label>
						<g:textField name="name" required="" value="${assetInstance?.name}"/>

					</div>
					<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'description', 'error')} required">
						<label for="description">
							<g:message code="asset.description.label" default="Description" />
							<span class="required-indicator">*</span>
						</label>
						<g:textArea name="description" required="" value="${assetInstance?.description}"/>
					</div>
					<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'mimeType', 'error')}">
						<label>
							<g:message code="asset.mimeType.label" default="Mime Type" />
						</label>
						${assetInstance?.mimeType}
					</div>
					<g:if test="${assetInstance?.anchor}">
						<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'anchor', 'error')}">
							<label>
								<g:message code="asset.anchor.label" default="Anchor" />
							</label>
							${assetInstance?.anchor}
						</div>
					</g:if>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="submit" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
