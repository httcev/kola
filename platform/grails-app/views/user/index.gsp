
<%@ page import="kola.User" %>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<h1 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
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
					<g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}" />
					<g:sortableColumn property="profile.displayName" title="${message(code: 'user.displayName.label', default: 'Display Name')}" />
					<g:sortableColumn property="profile.company" title="${message(code: 'user.company.label', default: 'Company')}" />
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
