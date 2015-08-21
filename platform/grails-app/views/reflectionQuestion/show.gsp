
<g:set var="authService" bean="authService"/>
<g:set var="assetService" bean="assetService"/>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.reflectionQuestion')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.reflectionQuestions')}" />
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
		<h1 class="page-header">
			<g:message code="default.show.label" args="[entityName]" />
			<div class="buttons pull-right">
				<g:if test="${authService.canDelete(reflectionQuestionInstance)}">
					<g:link class="delete btn btn-danger" action="delete" id="${reflectionQuestionInstance.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
				</g:if>
				<g:if test="${authService.canEdit(reflectionQuestionInstance)}">
					<g:link class="edit btn btn-primary" action="edit" id="${reflectionQuestionInstance.id}" title="${message(code: 'default.button.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
				</g:if>
			</div>
		</h1>
		<div class="row">
			<div class="col-sm-2"><label><g:message code="kola.reflectionQuestion.question" />:</label></div>
			<div class="col-sm-10">${reflectionQuestionInstance?.name}</div>
		</div>
	</body>
</html>
