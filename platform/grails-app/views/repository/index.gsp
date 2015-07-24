
<%@ page import="kola.Asset" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'asset.label', default: 'Asset')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<h1 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
			<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REPOSITORY_ADMIN">
				<g:link class="create btn btn-primary pull-right" action="create" title="${message(code: 'default.new.label', args:[entityName])}"><i class="fa fa-plus"></i></g:link>
			</sec:ifAnyGranted>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<table class="table">
			<thead>
				<tr>
					<g:sortableColumn property="name" title="${message(code: 'asset.name.label', default: 'Name')}" />
					<g:sortableColumn property="description" title="${message(code: 'asset.description.label', default: 'Description')}" />
					<g:sortableColumn property="lastUpdated" title="${message(code: 'asset.lastUpdated.label', default: 'Last updated')}" />
					<g:sortableColumn property="mimeType" title="${message(code: 'asset.mimeType.label', default: 'Mime type')}" />
				</tr>
			</thead>
			<tbody>
			<g:each in="${assetInstanceList}" status="i" var="assetInstance">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<td><g:link action="show" id="${assetInstance.id}">${fieldValue(bean: assetInstance, field: "name")}</g:link></td>
					<td>${fieldValue(bean: assetInstance, field: "description").take(100)}</td>
					<td>${fieldValue(bean: assetInstance, field: "lastUpdated")}</td>
					<td>${fieldValue(bean: assetInstance, field: "mimeType")}</td>
				</tr>
			</g:each>
			</tbody>
		</table>
		<div class="pagination pull-right">
			<g:paginate total="${assetInstanceCount ?: 0}" />
		</div>
	</body>
</html>
