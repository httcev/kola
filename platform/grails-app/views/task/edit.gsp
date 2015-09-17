<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: taskInstance.isTemplate?.toBoolean() ? 'kola.taskTemplate' : 'kola.task')}" />
		<g:set var="entitiesName" value="${message(code: taskInstance.isTemplate?.toBoolean() ? 'kola.taskTemplates' : 'kola.tasks')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
		<asset:stylesheet src="bootstrap-markdown.min.css"/>
		<asset:stylesheet src="picker-default.css"/>
		<asset:stylesheet src="picker-default.date.css"/>
		<asset:javascript src="bootstrap-markdown.js"/>
		<asset:javascript src="Sortable.min.js"/>
		<asset:javascript src="picker.js"/>
		<asset:javascript src="picker.date.js"/>
		<asset:javascript src="picker.de_DE.js"/>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="kola.home" /></g:link></li>
			<li><g:link action="index" params="[isTemplate:taskInstance.isTemplate]">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.edit.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<g:form url="[resource:taskInstance, action:'update']" method="PUT" class="form-horizontal" enctype="multipart/form-data" autocomplete="off">
			<h1 class="page-header">
				<g:message code="default.edit.label" args="[entityName]" />
				<div class="buttons pull-right">
					<g:link class="delete btn btn-danger" action="delete" id="${taskInstance.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
						<i class="fa fa-times"></i>
					</g:link>
					<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
				</div>
			</h1>
			<g:hasErrors bean="${taskInstance}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${taskInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<g:render template="form"/>
			<div class="buttons pull-right">
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
			</div>
		</g:form>
		<g:render template="assetModal" />
		<g:render template="reflectionQuestionModal" />
	</body>
</html>
