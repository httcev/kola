
<%@ page import="kola.Asset" %>
<g:set var="assetService" bean="assetService"/>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'asset.label', default: 'Asset')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-asset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-asset" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list asset">
				<g:if test="${assetInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="asset.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${assetInstance}" field="name"/></span>
					
				</li>
				</g:if>
				<g:if test="${assetInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="asset.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${assetInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assetInstance?.mimeType}">
				<li class="fieldcontain">
					<span id="mimeType-label" class="property-label"><g:message code="asset.mimeType.label" default="Mime Type" /></span>
					
						<span class="property-value" aria-labelledby="mimeType-label"><g:fieldValue bean="${assetInstance}" field="mimeType"/></span>
					
				</li>
				</g:if>

				<g:if test="${assetInstance?.externalUrl}">
				<li class="fieldcontain">
					<span id="externalUrl-label" class="property-label"><g:message code="asset.externalUrl.label" default="External Url" /></span>
					
						<span class="property-value" aria-labelledby="externalUrl-label"><g:fieldValue bean="${assetInstance}" field="externalUrl"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assetInstance?.anchor}">
				<li class="fieldcontain">
					<span id="anchor-label" class="property-label"><g:message code="asset.anchor.label" default="Anchor" /></span>
					
						<span class="property-value" aria-labelledby="anchor-label"><g:fieldValue bean="${assetInstance}" field="anchor"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${assetInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="asset.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${assetInstance}" field="type"/></span>
					
				</li>
				</g:if>
			
				<li class="fieldcontain">
					<span id="url-label" class="property-label"><g:message code="asset.url.label" default="URL" /></span>
					
						<span class="property-value" aria-labelledby="url-label">
						<g:set var="url" value="${assetService.createEncodedLink(assetInstance)}" />
						<a href="${url}" target="_blank">${url}</a>
						</span>
				</li>
			</ol>
			<g:form url="[controller:"repository", id:assetInstance.id, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" controller="repository" id="${assetInstance.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
