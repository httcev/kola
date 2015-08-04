
<g:set var="authService" bean="authService"/>
<g:set var="assetService" bean="assetService"/>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: taskInstance.isTemplate ? 'taskTemplate.label' : 'task.label', default: taskInstance.isTemplate ? 'Arbeitsprozessbeschreibung' : 'Arbeitsauftrag')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<asset:stylesheet src="blueimp-gallery.min.css"/>
		<asset:javascript src="jquery.blueimp-gallery.min.js"/>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="default.home.label" default="Home" /></g:link></li>
			<li><g:link action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			<li class="active"><g:message code="default.show.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<h1 class="page-header">
			${taskInstance?.name}
			<div class="buttons pull-right">
				<g:if test="${authService.canDelete(taskInstance)}">
					<g:link class="delete btn btn-danger" action="delete" id="${taskInstance.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
				</g:if>
				<g:if test="${authService.canEdit(taskInstance)}">
					<g:link class="edit btn btn-primary" action="edit" id="${taskInstance.id}" title="${message(code: 'default.button.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
				</g:if>
			</div>
		</h1>
		<g:if test="${taskInstance?.description}">
			<div class="row">
				<div class="col-sm-2"><label><g:message code="taskInstance.description.label" default="Description" />:</label></div>
				<div class="col-sm-10"><kola:markdown>${taskInstance.description}</kola:markdown></div>
			</div>
		</g:if>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="taskInstance.creator.label" default="Creator" />:</label></div>
			<div class="col-sm-10"><g:render bean="${taskInstance.creator.profile}" template="/profile/show" var="profile" /></div>
		</div>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="taskInstance.lastUpdated.label" default="Last updated" />:</label></div>
			<div class="col-sm-10"><g:formatDate date="${taskInstance.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></div>
		</div>
		<g:if test="${taskInstance?.attachments?.size() > 0}">
		<div class="row">
			<div class="col-sm-2"><label><g:message code="taskInstance.attachments.label" default="Anhänge" />:</label></div>
			<div class="col-sm-10">
				<g:render bean="${taskInstance?.attachments}" template="attachments" var="attachments" />
			</div>
		</div>
		</g:if>
		<g:if test="${taskInstance?.resources?.size() > 0}">
		<div class="row">
			<div class="col-sm-2"><label><g:message code="taskInstance.resources.label" default="Lernressourcen" />:</label></div>
			<div class="col-sm-10">
				<ul class="list-group">
					<g:each var="assetInstance" in="${taskInstance?.resources}">
						<li class="list-group-item">
							<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank">
								<h4 class="list-group-item-heading">
									<i class="fa fa-external-link"></i> ${assetInstance.name}
								</h4>
								<p class="list-group-item-text text-default">
									${assetInstance.description?.take(100)}
								</p>
							</a>
						</li>
					</g:each>
				</ul>
			</div>
		</div>
		</g:if>
	</body>
</html>
