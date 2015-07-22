<div class="form-group ${hasErrors(bean: userInstance, field: 'username', 'error')} required">
	<label for="username" class="col-sm-2 control-label"><g:message code="user.name.label" default="Login name" /><span class="required-indicator">*</span></label>
	<div class="col-sm-10"><g:textField name="username" value="${userInstance?.username}" class="form-control" required="" autocomplete="off"/></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
	<label for="password" class="col-sm-2 control-label"><g:message code="user.password.label" default="Password" /><span class="required-indicator">*</span></label>
	<div class="col-sm-10"><input type="password" name="password" value="${userInstance?.password}" class="form-control" id="password" required autocomplete="off"></div>
</div>
