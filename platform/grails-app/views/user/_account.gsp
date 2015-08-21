<%@ page import="kola.Role" %>

<div class="form-group">
	<label class="col-sm-2 control-label"><g:message code="kola.user.account" />:</label>
	<div class="col-sm-10">
		<div class="checkbox">
			<label><g:checkBox name="enabled" value="${userInstance?.enabled}"/> <g:message code="kola.user.enabled" /></label>
		</div>
	</div>
	<div class="col-sm-offset-2 col-sm-10">
		<div class="checkbox">
			<label><g:checkBox name="passwordExpired" value="${userInstance?.passwordExpired}"/> <g:message code="kola.user.passwordExpired" /></label>
		</div>
	</div>
</div>
<%--
<div class="form-group ${hasErrors(bean: userInstance, field: 'accountExpired', 'error')}">
	<div class="col-sm-offset-2 col-sm-10">
		<div class="checkbox">
			<label><g:checkBox name="accountExpired" value="${userInstance?.accountExpired}"/> <g:message code="kola.user.accountExpired" /></label>
		</div>
	</div>
</div>
<div class="form-group ${hasErrors(bean: userInstance, field: 'accountLocked', 'error')}">
	<div class="col-sm-offset-2 col-sm-10">
		<div class="checkbox">
			<label><g:checkBox name="accountLocked" value="${userInstance?.accountLocked}"/> <g:message code="kola.user.accountLocked" /></label>
		</div>
	</div>
</div>
--%>
<div class="form-group">
	<label class="col-sm-2 control-label"><g:message code="kola.user.roles" />:</label>
	<div class="col-sm-10">
		<g:each in="${Role.list()}" var="role" >
			<div class="checkbox">
				<label><g:checkBox name="role" value="${role.id}" checked="${userInstance?.authorities?.contains(role)}"/> <g:message code="role.${role.authority}.label" default="${role.authority}" /></label>
			</div>
		</g:each>
	</div>
</div>
