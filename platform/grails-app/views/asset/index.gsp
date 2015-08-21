
<%@ page import="kola.Asset" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.asset')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.assets')}" />
		<title>${entitiesName}</title>
	</head>
	<body>
		<h1 class="page-header">
			${entitiesName}
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
					<g:sortableColumn property="name" title="${message(code: 'kola.meta.name')}" />
					<g:sortableColumn property="description" title="${message(code: 'kola.meta.description')}" />
					<g:sortableColumn property="lastUpdated" title="${message(code: 'kola.meta.lastUpdated')}" />
					<g:sortableColumn property="mimeType" title="${message(code: 'kola.meta.mimeType')}" />
				</tr>
			</thead>
			<tbody>
			<g:each in="${assetInstanceList}" var="assetInstance">
				<tr>
					<td><g:link action="show" id="${assetInstance.id}">${fieldValue(bean: assetInstance, field: "name")}</g:link></td>
					<td>${fieldValue(bean: assetInstance, field: "description")?.take(100)}</td>
					<td><g:formatDate date="${assetInstance.lastUpdated}" type="date"/></td>
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