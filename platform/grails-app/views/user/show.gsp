
<%@ page import="kola.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-user" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list user">
			
				<g:if test="${userInstance?.username}">
				<li class="fieldcontain">
					<span id="username-label" class="property-label"><g:message code="user.username.label" default="Username" /></span>
					
						<span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${userInstance}" field="username"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.password}">
				<li class="fieldcontain">
					<span id="password-label" class="property-label"><g:message code="user.password.label" default="Password" /></span>
					
						<span class="property-value" aria-labelledby="password-label"><g:fieldValue bean="${userInstance}" field="password"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.displayName}">
				<li class="fieldcontain">
					<span id="displayName-label" class="property-label"><g:message code="user.displayName.label" default="Display Name" /></span>
					
						<span class="property-value" aria-labelledby="displayName-label"><g:fieldValue bean="${userInstance}" field="displayName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.company}">
				<li class="fieldcontain">
					<span id="company-label" class="property-label"><g:message code="user.company.label" default="Company" /></span>
					
						<span class="property-value" aria-labelledby="company-label"><g:fieldValue bean="${userInstance}" field="company"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.department}">
				<li class="fieldcontain">
					<span id="department-label" class="property-label"><g:message code="user.department.label" default="Department" /></span>
					
						<span class="property-value" aria-labelledby="department-label"><g:fieldValue bean="${userInstance}" field="department"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.phone}">
				<li class="fieldcontain">
					<span id="phone-label" class="property-label"><g:message code="user.phone.label" default="Phone" /></span>
					
						<span class="property-value" aria-labelledby="phone-label"><g:fieldValue bean="${userInstance}" field="phone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.mobile}">
				<li class="fieldcontain">
					<span id="mobile-label" class="property-label"><g:message code="user.mobile.label" default="Mobile" /></span>
					
						<span class="property-value" aria-labelledby="mobile-label"><g:fieldValue bean="${userInstance}" field="mobile"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.email}">
				<li class="fieldcontain">
					<span id="email-label" class="property-label"><g:message code="user.email.label" default="Email" /></span>
					
						<span class="property-value" aria-labelledby="email-label"><g:fieldValue bean="${userInstance}" field="email"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.accountExpired}">
				<li class="fieldcontain">
					<span id="accountExpired-label" class="property-label"><g:message code="user.accountExpired.label" default="Account Expired" /></span>
					
						<span class="property-value" aria-labelledby="accountExpired-label"><g:formatBoolean boolean="${userInstance?.accountExpired}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.accountLocked}">
				<li class="fieldcontain">
					<span id="accountLocked-label" class="property-label"><g:message code="user.accountLocked.label" default="Account Locked" /></span>
					
						<span class="property-value" aria-labelledby="accountLocked-label"><g:formatBoolean boolean="${userInstance?.accountLocked}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.enabled}">
				<li class="fieldcontain">
					<span id="enabled-label" class="property-label"><g:message code="user.enabled.label" default="Enabled" /></span>
					
						<span class="property-value" aria-labelledby="enabled-label"><g:formatBoolean boolean="${userInstance?.enabled}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="user.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${userInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${userInstance?.passwordExpired}">
				<li class="fieldcontain">
					<span id="passwordExpired-label" class="property-label"><g:message code="user.passwordExpired.label" default="Password Expired" /></span>
					
						<span class="property-value" aria-labelledby="passwordExpired-label"><g:formatBoolean boolean="${userInstance?.passwordExpired}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:userInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${userInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
