
<g:set var="authService" bean="authService"/>
<g:set var="assetService" bean="assetService"/>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'taskTemplate.label', default: 'Arbeitsprozessbeschreibung')}" />
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
			${taskTemplateInstance?.name}
			<div class="buttons pull-right">
				<g:if test="${authService.canDelete(taskTemplateInstance)}">
					<g:link class="delete btn btn-danger" action="delete" id="${taskTemplateInstance.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
				</g:if>
				<g:if test="${authService.canEdit(taskTemplateInstance)}">
					<g:link class="edit btn btn-primary" action="edit" id="${taskTemplateInstance.id}" title="${message(code: 'default.button.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
				</g:if>
			</div>
		</h1>
		<g:if test="${taskTemplateInstance?.description}">
			<div class="row">
				<div class="col-sm-2"><label><g:message code="taskTemplateInstance.description.label" default="Description" />:</label></div>
				<div class="col-sm-10"><kola:markdown>${taskTemplateInstance.description}</kola:markdown></div>
			</div>
		</g:if>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="taskTemplateInstance.creator.label" default="Creator" />:</label></div>
			<div class="col-sm-10"><g:render bean="${taskTemplateInstance.creator.profile}" template="/profile/show" var="profile" /></div>
		</div>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="taskTemplateInstance.lastUpdated.label" default="Last updated" />:</label></div>
			<div class="col-sm-10"><g:formatDate date="${taskTemplateInstance.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></div>
		</div>
		<g:if test="${taskTemplateInstance?.attachments?.size() > 0}">
		<div class="row">
			<div class="col-sm-2"><label><g:message code="taskTemplateInstance.attachments.label" default="Anhänge" />:</label></div>
			<div class="col-sm-10">
				<g:render bean="${taskTemplateInstance?.attachments}" template="attachments" var="attachments" />
			</div>
		</div>
		</g:if>
		<g:if test="${taskTemplateInstance?.resources?.size() > 0}">
		<div class="row">
			<div class="col-sm-2"><label><g:message code="taskTemplateInstance.resources.label" default="Lernressourcen" />:</label></div>
			<div class="col-sm-10">
				<ul class="list-group">
					<g:each var="assetInstance" in="${taskTemplateInstance?.resources}">
						<li class="list-group-item">
							<h4 class="list-group-item-heading">
								<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank"><i class="fa fa-external-link"></i> ${assetInstance.name}</a>
							</h4>
							<p class="list-group-item-text">
								${assetInstance.description?.take(100)}
							</p>
						</li>
					</g:each>
				</ul>
			</div>
		</div>
		</g:if>
<%--
		<a href="#show-taskTemplate" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-taskTemplate" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list taskTemplate">
			

				<g:if test="${taskTemplateInstance?.attachments}">
				<li class="fieldcontain">
					<span id="attachments-label" class="property-label"><g:message code="taskTemplate.attachments.label" default="Attachments" /></span>
					
						<g:each in="${taskTemplateInstance.attachments}" var="a">
						<span class="property-value" aria-labelledby="attachments-label"><g:link controller="attachment" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			

				<g:if test="${taskTemplateInstance?.resources}">
				<li class="fieldcontain">
					<span id="resources-label" class="property-label"><g:message code="taskTemplate.resources.label" default="Resources" /></span>
					
						<g:each in="${taskTemplateInstance.resources}" var="r">
						<span class="property-value" aria-labelledby="resources-label"><g:link controller="asset" action="show" id="${r.id}">${r?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${taskTemplateInstance?.steps}">
				<li class="fieldcontain">
					<span id="steps-label" class="property-label"><g:message code="taskTemplate.steps.label" default="Steps" /></span>
					
						<g:each in="${taskTemplateInstance.steps}" var="s">
						<span class="property-value" aria-labelledby="steps-label"><g:link controller="taskStep" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>

		</div>
--%>		
	</body>
</html>
