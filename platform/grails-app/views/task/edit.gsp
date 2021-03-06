<html>
	<head>
		<meta name="layout" content="editor">
		<g:set var="entityName" value="${message(code: task.isTemplate?.toBoolean() ? 'kola.taskTemplate' : 'kola.task')}" />
		<g:set var="entitiesName" value="${message(code: task.isTemplate?.toBoolean() ? 'kola.taskTemplates' : 'kola.tasks')}" />
		<title><g:message code="default.edit.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="app.home" /></g:link></li>
			<li><g:link action="index" params="[isTemplate:task.isTemplate]">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.edit.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<g:if test="${flash.error}">
			<div class="message alert alert-danger" role="status">${flash.error}</div>
		</g:if>
		<g:form url="[resource:task, action:'update']" method="PUT" class="form-horizontal" enctype="multipart/form-data" autocomplete="off">
			<h1 class="page-header clearfix">
				<g:message code="default.edit.label" args="[entityName]" />
				<div class="buttons pull-right">
					<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
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
				<button class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.update.label" default="Update" /></button>
			</div>
		</g:form>
		<g:render template="assetModal" />
		<g:render template="reflectionQuestionModal" />
	</body>
</html>
