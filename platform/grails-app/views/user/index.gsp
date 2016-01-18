<html>
	<head>
		<meta name="layout" content="admin">
		<g:set var="entityName" value="${message(code: 'de.httc.plugin.user.user')}" />
		<g:set var="entitiesName" value="${message(code: 'de.httc.plugin.user.users')}" />
		<title>${entitiesName}</title>
	</head>
	<body>
		<h1 class="page-header clearfix">
			${entitiesName}
			<g:link class="create btn btn-primary pull-right" action="create" namespace="admin" title="${message(code: 'default.new.label', args:[entityName])}">
				<i class="fa fa-user-plus"></i><span class="button-label"> <g:message code="default.button.create.label" /></span>
			</g:link>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<p class="margin text-muted small"><g:message code="de.httc.search.hits.displaying" args="${[entitiesName, params.offset + 1, Math.min(params.offset + params.max, userInstanceCount), userInstanceCount]}" />:</p>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<g:sortableColumn property="username" namespace="admin" title="${message(code: 'de.httc.plugin.user.loginName')}" />
						<g:sortableColumn property="profile.lastName" namespace="admin" title="${message(code: 'de.httc.plugin.user.lastName')}" />
						<g:sortableColumn property="profile.firstName" namespace="admin" title="${message(code: 'de.httc.plugin.user.firstName')}" />
						<g:sortableColumn property="profile.company" namespace="admin" title="${message(code: 'de.httc.plugin.user.company')}" />
						<g:sortableColumn property="enabled" namespace="admin" title="${message(code: 'de.httc.plugin.user.enabled')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${userList}" var="userInstance">
					<tr>
						<td><g:link action="edit" namespace="admin" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>
						<td>${fieldValue(bean: userInstance.profile, field: "lastName")}</td>
						<td>${fieldValue(bean: userInstance.profile, field: "firstName")}</td>
						<td>${fieldValue(bean: userInstance.profile, field: "company")}</td>
						<td><i class="fa fa-lg fa-${userInstance.enabled ? 'check text-success' : 'minus text-warning'}"></i></td>
					</tr>
				</g:each>
				</tbody>
			</table>
		</div>
		<g:if test="${params.max < userInstanceCount}">
			<div class="pagination pull-right">
				<g:paginate total="${userInstanceCount ?: 0}" namespace="admin" />
			</div>
		</g:if>
	</body>
</html>
