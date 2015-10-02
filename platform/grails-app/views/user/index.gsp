
<%@ page import="kola.User" %>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.user')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.users')}" />
		<title>${entitiesName}</title>
	</head>
	<body>
		<h1 class="page-header clearfix">
			${entitiesName}
			<g:link class="create btn btn-primary pull-right" action="create" title="${message(code: 'default.new.label', args:[entityName])}">
				<i class="fa fa-user-plus"></i>
			</g:link>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<table class="table table-striped">
			<thead>
				<tr>
					<g:sortableColumn property="username" title="${message(code: 'kola.user.loginName')}" />
					<g:sortableColumn property="profile?.displayName" title="${message(code: 'kola.user.displayName')}" />
					<g:sortableColumn property="profile?.company" title="${message(code: 'kola.user.company')}" />
				</tr>
			</thead>
			<tbody>
			<g:each in="${userInstanceList}" var="userInstance">
				<tr>
					<td><g:link action="edit" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>
					<td>${fieldValue(bean: userInstance.profile, field: "displayName")}</td>
					<td>${fieldValue(bean: userInstance.profile, field: "company")}</td>
				</tr>
			</g:each>
			</tbody>
		</table>
		<div class="pagination pull-right">
			<g:paginate total="${userInstanceCount ?: 0}" />
		</div>
	</body>
</html>
