<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: params.isTemplate?.toBoolean() ? 'kola.taskTemplate' : 'kola.task')}" />
		<g:set var="entitiesName" value="${message(code: params.isTemplate?.toBoolean() ? 'kola.taskTemplates' : 'kola.tasks')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
		<asset:stylesheet src="bootstrap-markdown.min.css"/>
		<asset:javascript src="bootstrap-markdown.js"/>
		<asset:javascript src="Sortable.min.js"/>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="kola.home" /></g:link></li>
			<li><g:link action="index" params="[isTemplate:params.isTemplate]">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.create.label" args="[entityName]" /></li>
		</ol>
		<g:form action="save" class="form-horizontal" autocomplete="off" enctype="multipart/form-data">
			<input type="hidden" name="isTemplate" value="${taskInstance?.isTemplate}">
			<input type="hidden" name="template" value="${taskInstance?.template?.id}">
			<h1 class="page-header">
				<g:message code="default.create.label" args="[entityName]" />
				<div class="buttons pull-right">
					<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.create.label" default="Create" /></button>
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
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.create.label" default="Create" /></button>
			</div>
		</g:form>
		<g:render template="assetModal" />
		<g:render template="reflectionQuestionModal" />
	</body>
</html>
