
<g:set var="assetService" bean="assetService"/>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.asset')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.assets')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="kola.home" /></g:link></li>
			<li><g:link action="index">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.show.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<h1 class="page-header clearfix">
			${asset?.name}
			<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REPOSITORY_ADMIN">
				<div class="buttons pull-right">
					<g:link class="delete btn btn-danger" action="delete" id="${asset.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
					<g:link class="edit btn btn-primary" action="edit" id="${asset.id}" title="${message(code: 'default.button.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
				</div>
			</sec:ifAnyGranted>
		</h1>
		<g:if test="${asset?.description}">
			<p>${asset.description}</p>
		</g:if>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="kola.meta.mimeType" />:</label></div>
			<div class="col-sm-10"><code>${asset.mimeType}</code></div>
		</div>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="kola.meta.lastUpdated" />:</label></div>
			<div class="col-sm-10"><g:formatDate date="${asset.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></div>
		</div>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="kola.asset.link" />:</label></div>
			<g:set var="url" value="${assetService.createEncodedLink(asset)}" />
			<div class="col-sm-10"><a href="${url}" target="_blank"><i class="fa fa-external-link"></i> ${url}</a></div>
		</div>
	</body>
</html>
