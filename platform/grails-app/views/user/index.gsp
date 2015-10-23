
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
		<p class="margin text-muted small"><g:message code="kola.search.hits.displaying" args="${[entitiesName, params.offset + 1, Math.min(params.offset + params.max, userInstanceCount), userInstanceCount]}" />:</p>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<g:sortableColumn property="username" title="${message(code: 'kola.user.loginName')}" />
						<g:sortableColumn property="profile.displayName" title="${message(code: 'kola.user.displayName')}" />
						<g:sortableColumn property="profile.company" title="${message(code: 'kola.user.company')}" />
						<g:sortableColumn property="enabled" title="${message(code: 'kola.user.enabled')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${userInstanceList}" var="userInstance">
					<tr>
						<td><g:link action="edit" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>
						<td>${fieldValue(bean: userInstance.profile, field: "displayName")}</td>
						<td>${fieldValue(bean: userInstance.profile, field: "company")}</td>
						<td><i class="fa fa-lg fa-${userInstance.enabled ? 'check text-success' : 'minus text-warning'}"></i></td>
					</tr>
				</g:each>
				</tbody>
			</table>
		</div>
		<g:if test="${params.max < userInstanceCount}">
			<div class="pagination pull-right">
				<g:paginate total="${userInstanceCount ?: 0}" />
			</div>
		</g:if>
	</body>
</html>
