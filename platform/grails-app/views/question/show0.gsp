<g:set var="authService" bean="authService"/>
<g:set var="repositoryService" bean="repositoryService"/>
<html>
	<head>
		<meta name="layout" content="editor">
		<g:set var="entityName" value="${message(code: 'de.httc.plugin.qaa.question')}" />
		<g:set var="entitiesName" value="${message(code: 'de.httc.plugin.qaa.questions')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<asset:stylesheet src="blueimp-gallery.min.css"/>
		<asset:javascript src="jquery.blueimp-gallery.min.js"/>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="app.home" /></g:link></li>
			<li><g:link action="index">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.show.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.error}">
			<div class="message alert alert-danger" role="status">${flash.error}</div>
		</g:if>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<div class="question-wrapper">
			<h1 class="page-header clearfix">
				${question?.title}
				<div class="buttons pull-right">
					<g:if test="${authService.canDelete(question)}">
						<g:link class="delete btn btn-danger" action="delete" id="${question.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
					</g:if>
					<g:if test="${authService.canEdit(question)}">
						<button type="button" class="btn btn-default" onclick="$(this).closest('.question-wrapper').hide().next('.form').removeClass('hidden').find('textarea').focus()"><i class="fa fa-pencil"></i> <g:message code="default.button.edit.label" /></button>
					</g:if>
				</div>
			</h1>
			<div class="question">
				<g:render bean="${question}" template="ratingControl" var="rateable" />
				<div class="full-width padding-left">
					<kola:markdown>${question?.text}</kola:markdown>
					<div class="clearfix">
						<div class="pull-right">
							<g:render model="${[profile:question?.creator?.profile]}" template="/profile/show" />,
							<g:formatDate date="${question?.dateCreated}" type="date" />
						</div>
					</div>
					<g:if test="${question?.attachments?.size() > 0}">
						<g:render model="${[attachments:question?.attachments]}" template="/task/attachments" />
					</g:if>
					<g:render bean="${question}" template="comments" var="commentable" />
				</div>
			</div>
		</div>
		<g:if test="${authService.canEdit(question)}">
			<g:form class="form hidden" action="update" id="${question.id}" method="PUT" enctype="multipart/form-data">
				<g:render model="${[question:question]}" template="form" />
			</g:form>
		</g:if>

		<g:if test="${question?.answers?.size() > 0}">
			<h3><g:message code="de.httc.plugin.qaa.answers"/></h3>
			<g:each var="answer" in="${sortedAnswers}">
				<g:render model="${[answer:answer, question:question]}" template="answer" />
			</g:each>
		</g:if>
		<div class="text-center margin">
			<button type="button" class="btn btn-success" onclick="$(this).hide().parent().next('.new-answer').removeClass('hidden').find('textarea').focus()"><i class="fa fa-plus"></i> <g:message code="default.add.label" args="[message(code:'de.httc.plugin.qaa.answer')]"/></button>
		</div>
		<div class="new-answer hidden">
			<h1><g:message code="default.add.label" args="[message(code:'de.httc.plugin.qaa.answer')]"/>:</h1>
			<g:form class="form" action="saveAnswer" enctype="multipart/form-data">
				<input type="hidden" name="question" value="${question.id}">
				<textarea name="text" class="form-control" rows="5" placeholder="${message(code:'de.httc.plugin.qaa.answer.placeholder')}" required></textarea>
				<g:render model="${[attachments:[], mode:'edit']}" template="/task/attachments" />
				<div class="text-right form-padding"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'de.httc.plugin.qaa.answer')]"/></button></div>
			</g:form>
		</div>
	</body>
</html>
