<html>
	<head>
		<meta name="layout" content="editor">
		<title><g:message code="kola.task.assign.group.header" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="app.home" /></g:link></li>
			<li><g:link action="index" params="[isTemplate:false]">${message(code: 'kola.tasks')}</g:link></li>
			<li><g:link action="edit" id="${task.id}"><g:message code="default.edit.label" args="${[message(code: 'kola.task')]}" /></g:link></li>
			<li class="active"><g:message code="kola.task.assign.group.header" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<g:form url="[resource:task, action:'assign']" method="POST" class="form-horizontal" autocomplete="off">
			<input type="hidden" name="group" value="${group.id}">
			<h1 class="page-header clearfix">
				<g:message code="kola.task.assign.group.header" />
				<div class="buttons pull-right">
					<g:link action="edit" id="${task.id}" class="cancel btn btn-default"><i class="fa fa-close"></i> <g:message code="default.button.cancel.label" default="Cancel" /></g:link>
					<button class="save btn btn-success"><i class="fa fa-check-circle"></i> <g:message code="default.button.confirm.label" default="Confirm" /></button>
				</div>
			</h1>
			<div class="padding-bottom"><g:message code="kola.task.assign.group.header.info" args="${[group.label, userProfiles.size()]}" /></div>
			<ul class="list-group">
			<g:each var="profile" in="${userProfiles}">
				<li class="list-group-item"><g:render model="${[profile:profile, reverseName:true]}" template="/profile/show" /></li>
			</g:each>
			</ul>
		</g:form>
	</body>
</html>
