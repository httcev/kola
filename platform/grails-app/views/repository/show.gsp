
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
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="default.home.label" default="Home" /></g:link></li>
			<li><g:link controller="repository" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			<li class="active"><g:message code="default.show.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<h1 class="page-header">
			${assetInstance?.name}
			<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REPOSITORY_ADMIN">
				<div class="buttons pull-right">
					<g:link class="delete btn btn-danger" action="delete" id="${assetInstance.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
					<g:link class="edit btn btn-primary" action="edit" id="${assetInstance.id}" title="${message(code: 'default.button.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
				</div>
			</sec:ifAnyGranted>
		</h1>
		<g:if test="${assetInstance?.description}">
			<p>${assetInstance.description}</p>
		</g:if>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="asset.mimeType.label" default="Mime type" />:</label></div>
			<div class="col-sm-10"><code>${assetInstance.mimeType}</code></div>
		</div>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="asset.lastUpdated.label" default="Last updated" />:</label></div>
			<div class="col-sm-10"><g:formatDate date="${assetInstance.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></div>
		</div>
		<div class="row">
			<div class="col-sm-2"><label>URL:</label></div>
			<g:set var="url" value="${assetService.createEncodedLink(assetInstance)}" />
			<div class="col-sm-10"><a href="${url}" target="_blank"><i class="fa fa-external-link"></i> ${url}</a></div>
		</div>
	</body>
</html>
