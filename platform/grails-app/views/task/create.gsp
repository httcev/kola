<html>
	<head>
		<meta name="layout" content="editor">
		<g:set var="entityName" value="${message(code: params.isTemplate?.toBoolean() ? 'kola.taskTemplate' : 'kola.task')}" />
		<g:set var="entitiesName" value="${message(code:params.isTemplate?.toBoolean() ? 'kola.taskTemplates' : 'kola.tasks')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="app.home" /></g:link></li>
			<li><g:link action="index" params="[isTemplate:params.isTemplate]">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.create.label" args="[entityName]" /></li>
		</ol>
		<g:form action="save" class="form-horizontal" autocomplete="off" enctype="multipart/form-data">
			<input type="hidden" name="isTemplate" value="${task?.isTemplate}">
			<input type="hidden" name="template" value="${task?.template?.id}">
			<h1 class="page-header clearfix">
				<g:message code="default.create.label" args="[entityName]" />
				<div class="buttons pull-right">
					<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.create.label" default="Create" /></button>
				</div>
			</h1>
			<g:hasErrors bean="${task}">
				<ul class="errors alert alert-danger" role="alert">
					<g:eachError bean="${task}" var="error">
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
