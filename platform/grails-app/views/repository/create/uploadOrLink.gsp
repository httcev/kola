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
			<g:if test="${request.message}">
			<div class="message" role="status">${request.message}</div>
			</g:if>
			<g:hasErrors bean="${assetInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${assetInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form action="create" enctype="multipart/form-data">
				<fieldset class="form">
					<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'content', 'error')} required">
						<label for="content">
							<g:message code="asset.content.label" default="Content" />
						</label>
						<input type="file" id="content" name="content" />
					</div>
					<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'externalUrl', 'error')} ">
						<label for="externalUrl">
							<g:message code="asset.externalUrl.label" default="External URL" />
							
						</label>
						<g:textField name="externalUrl" value="${assetInstance?.externalUrl}"/>
					</div>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="submit" class="save" value="${message(code: 'default.button.next.label', default: 'Next')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>