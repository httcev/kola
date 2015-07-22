<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'displayName', 'error')} required">
	<label for="displayName" class="col-sm-2 control-label"><g:message code="user.displayName.label" default="Display name" /><span class="required-indicator">*</span></label>
	<div class="col-sm-10"><g:textField name="profile.displayName" required="" value="${userInstance?.profile?.displayName}" class="form-control"/></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'company', 'error')} ">
	<label for="company" class="col-sm-2 control-label"><g:message code="user.company.label" default="Company" /></label>
	<div class="col-sm-10"><g:textField name="profile.company" value="${userInstance?.profile?.company}" class="form-control"/></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'department', 'error')} ">
	<label for="department" class="col-sm-2 control-label"><g:message code="user.department.label" default="Department" /></label>
	<div class="col-sm-10"><g:textField name="profile.department" value="${userInstance?.profile?.department}" class="form-control"/></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'phone', 'error')} ">
	<label for="phone" class="col-sm-2 control-label"><g:message code="user.phone.label" default="Phone" /></label>
	<div class="col-sm-10"><input type="tel" name="profile.phone" value="${userInstance?.profile?.phone}" class="form-control" id="phone"></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'mobile', 'error')} ">
	<label for="mobile" class="col-sm-2 control-label"><g:message code="user.mobile.label" default="Mobile" /></label>
	<div class="col-sm-10"><input type="tel" name="profile.mobile" value="${userInstance?.profile?.mobile}" class="form-control" id="mobile"></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'email', 'error')} ">
	<label for="email" class="col-sm-2 control-label"><g:message code="user.email.label" default="Email" /></label>
	<div class="col-sm-10"><input type="email" name="profile.email" value="${userInstance?.profile?.email}" class="form-control" id="email"></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'photo', 'error')} ">
	<label for="photo" class="col-sm-2 control-label"><g:message code="user.photo.label" default="Photo" /></label>
	<div class="col-sm-10">
		<g:if test="${userInstance.profile?.photo?.length > 0}">
			<div class="row">
			  <div class="col-xs-3 col-md-2">
			  	<div class="thumbnail">
			  	<div class="avatar-container">
					<img class="avatar" src="data:image/png;base64,${userInstance.profile.photo.encodeBase64().toString()}">
					</div>
					</div>
			  </div>
			</div>
		</g:if>
		<input type="file" name="profile.photo" class="form-control" id="photo">
	</div>
</div>
