
<%@ page import="kola.Asset" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'asset.label', default: 'Asset')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-asset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-asset" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
				
						<g:sortableColumn property="name" title="${message(code: 'asset.name.label', default: 'Name')}" />

						<g:sortableColumn property="description" title="${message(code: 'asset.description.label', default: 'Description')}" />
					
						<g:sortableColumn property="mimeType" title="${message(code: 'asset.mimeType.label', default: 'Mime Type')}" />
					
						<g:sortableColumn property="externalUrl" title="${message(code: 'asset.externalUrl.label', default: 'External Url')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${assetInstanceList}" status="i" var="assetInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show" id="${assetInstance.id}">${fieldValue(bean: assetInstance, field: "name")}</g:link></td>

						<td>${fieldValue(bean: assetInstance, field: "description")}</td>
					
						<td>${fieldValue(bean: assetInstance, field: "mimeType")}</td>
					
						<td>${fieldValue(bean: assetInstance, field: "externalUrl")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${assetInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
