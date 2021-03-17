<%@ page import="java.util.UUID" %>

<g:set var="dialogId" value="id-${UUID.randomUUID().toString()}"/>
<a href="#" data-toggle="modal" data-target="#${dialogId}" class="profile-link">
	<g:if test="${!hideImage}">
		<g:if test="${profile.photo?.length > 0}">
			<img class="avatar img-circle" src="data:image/png;base64,${profile.photo.encodeBase64().toString()}">
		</g:if>
		<g:else>
			<i class="fa fa-user fa-lg text-muted"></i>
		</g:else>
	</g:if>
	<g:if test="${reverseName}">${profile.displayNameReverse}</g:if>
	<g:else>${profile.displayName}</g:else>
</a>
<!-- Modal -->
<div class="modal fade profile-dialog" id="${dialogId}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">${profile.displayName}</h4>
			</div>
			<div class="modal-body">
				<g:if test="${profile.company}">
					<div class="row padding-vertical">
						<label class="col-sm-3"><g:message code="de.httc.plugin.user.company" />:</label>
						<div class="col-sm-9">${profile.company.label}</div>
					</div>
				</g:if>
				<g:if test="${profile.phone}">
					<div class="row padding-vertical">
						<label class="col-sm-3"><g:message code="de.httc.plugin.user.phone" />:</label>
						<div class="col-sm-9">
							<a href="tel:${profile.phone}" class="btn btn-success"><i class="fa fa-phone fa-lg fa-fw"></i></a> ${profile.phone}
						</div>
					</div>
				</g:if>
				<g:if test="${profile.mobile}">
					<div class="row padding-vertical">
						<label class="col-sm-3"><g:message code="de.httc.plugin.user.mobile" />:</label>
						<div class="col-sm-9">
							<a href="tel:${profile.mobile}" class="btn btn-success"><i class="fa fa-phone fa-lg fa-fw"></i></a> ${profile.mobile}
						</div>
					</div>
				</g:if>
				<g:if test="${profile.user.email}">
					<div class="row padding-vertical">
						<label class="col-sm-3"><g:message code="de.httc.plugin.user.email" />:</label>
						<div class="col-sm-9">
							<a href="mailto:${profile.user.email}" class="btn btn-success"><i class="fa fa-envelope fa-lg fa-fw"></i></a> ${profile.user.email}
						</div>
					</div>
				</g:if>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal"><g:message code="kola.close" /></button>
			</div>
		</div>
	</div>
</div>
