<%@ page import="kola.Profile" %>

<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'displayName', 'error')} required">
	<label for="displayName" class="col-sm-2 control-label"><g:message code="user.displayName.label" default="Display name" /><span class="required-indicator">*</span></label>
	<div class="col-sm-10"><g:textField name="profile.displayName" required="" value="${userInstance?.profile?.displayName}" class="form-control"/></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'company', 'error')} required">
	<label for="company" class="col-sm-2 control-label"><g:message code="user.company.label" default="Company" /><span class="required-indicator">*</span></label>
	<div class="col-sm-10">
		<input type="text" list="companies" name="profile.company" value="${userInstance?.profile?.company}" class="form-control" required="">
		<datalist id="companies">
			<g:each in="${Profile.executeQuery("select distinct p.company from Profile p")}">
				<option value="${it}">
			</g:each>
		</datalist>
	</div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'phone', 'error')} ">
	<label for="phone" class="col-sm-2 control-label"><g:message code="user.phone.label" default="Phone" /></label>
	<div class="col-sm-10"><input type="tel" name="profile.phone" value="${userInstance?.profile?.phone}" class="form-control" id="phone"></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'mobile', 'error')} ">
	<label for="mobile" class="col-sm-2 control-label"><g:message code="user.mobile.label" default="Mobile" /></label>
	<div class="col-sm-10"><input type="tel" name="profile.mobile" value="${userInstance?.profile?.mobile}" class="form-control" id="mobile"></div>
</div>
<div class="form-group ${hasErrors(bean: userInstance.profile, field: 'photo', 'error')} ">
	<label for="photo" class="col-sm-2 control-label"><g:message code="user.photo.label" default="Photo" /></label>
	<div class="col-sm-10">
		<input type="hidden" name="_deletePhoto" id="deletePhoto" value="false">
		<g:if test="${userInstance.profile?.photo?.length > 0}">
			<div id="avatar-container">
				<img class="avatar pull-left" src="data:image/png;base64,${userInstance.profile.photo.encodeBase64().toString()}">
				
				<button type="button" class="delete btn btn-danger" title="Bild löschen" onclick="$('#avatar-container').remove(); $('#deletePhoto').val('true');" style="margin-left:10px"><i class="fa fa-times"></i></button>
			</div>
		</g:if>
		<input type="file" name="_photo" class="form-control" id="photo">
	</div>
</div>
