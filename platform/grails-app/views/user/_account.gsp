<%@ page import="kola.User" %>
<%@ page import="kola.Role" %>

<div class="form-group ${hasErrors(bean: userInstance, field: 'enabled', 'error')}">
	<div class="col-sm-offset-2 col-sm-10">
		<div class="checkbox">
			<label><g:checkBox name="enabled" value="${userInstance?.enabled}"/> <g:message code="user.enabled.label" default="Enabled" /></label>
		</div>
	</div>
</div>
<div class="form-group ${hasErrors(bean: userInstance, field: 'accountExpired', 'error')}">
	<div class="col-sm-offset-2 col-sm-10">
		<div class="checkbox">
			<label><g:checkBox name="accountExpired" value="${userInstance?.accountExpired}"/> <g:message code="user.accountExpired.label" default="Account Expired" /></label>
		</div>
	</div>
</div>
<div class="form-group ${hasErrors(bean: userInstance, field: 'accountLocked', 'error')}">
	<div class="col-sm-offset-2 col-sm-10">
		<div class="checkbox">
			<label><g:checkBox name="accountLocked" value="${userInstance?.accountLocked}"/> <g:message code="user.accountLocked.label" default="Account Locked" /></label>
		</div>
	</div>
</div>
<div class="form-group ${hasErrors(bean: userInstance, field: 'passwordExpired', 'error')}">
	<div class="col-sm-offset-2 col-sm-10">
		<div class="checkbox">
			<label><g:checkBox name="passwordExpired" value="${userInstance?.passwordExpired}"/> <g:message code="user.passwordExpired.label" default="Password Expired" /></label>
		</div>
	</div>
</div>
<div class="form-group">
	<label class="col-sm-2 control-label"><g:message code="user.roles.label" default="Roles" /></label>
	<div class="col-sm-10">
		<g:each in="${Role.list()}" var="role" >
			<div class="checkbox">
				<label><g:checkBox name="role" value="${role.id}" checked="${userInstance?.authorities?.contains(role)}"/> <g:message code="role.${role.authority}.label" default="${role.authority}" /></label>
			</div>
		</g:each>
	</div>
</div>
