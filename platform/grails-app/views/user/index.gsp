
<%@ page import="kola.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/admin"><g:message code="default.admin.label" default="Administration" /></g:link></li>
			<li class="active"><g:message code="default.list.label" args="[entityName]" /></li>
		</ol>
		<h1 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
			<g:link class="create btn btn-default btn-sm pull-right" action="create" title="${message(code: 'default.new.label', args:[entityName])}">
				<span class="fa-stack">
				  <i class="fa fa-user fa-stack-2x"></i>
				  <i class="fa fa-plus fa-stack-1x text-primary"></i>
				</span>
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
			<g:each in="${userInstanceList}" status="i" var="userInstance">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
				
					<td><g:link action="edit" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>
				
					<td>${fieldValue(bean: userInstance.profile, field: "displayName")}</td>
				
					<td>${fieldValue(bean: userInstance.profile, field: "company")}</td>
				
				</tr>
			</g:each>
			</tbody>
		</table>
		<div class="pagination">
			<g:paginate total="${userInstanceCount ?: 0}" />
		</div>
	</body>
</html>
