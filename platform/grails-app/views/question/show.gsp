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
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<h1 class="page-header clearfix">
			${question?.title}
			<div class="buttons pull-right">
				<g:if test="${authService.canDelete(question)}">
					<g:link class="delete btn btn-danger" action="delete" id="${question.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
				</g:if>
				<g:if test="${authService.canEdit(question)}">
					<g:link class="edit btn btn-primary" action="edit" id="${question.id}" title="${message(code: 'default.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
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

		<g:if test="${question?.answers?.size() > 0}">
			<h3><g:message code="de.httc.plugin.qaa.answers"/></h3>
			<g:each var="answer" in="${question?.answers}">
				<div class="answer clearfix">
					<div class="text-center">
						<g:render bean="${answer}" template="ratingControl" var="rateable" />
						<div>
							<g:if test="${question?.acceptedAnswer?.id == answer.id}"><i class="text-success fa fa-check fa-2x"></i></g:if>
							<g:elseif test="${authService.canEdit(question)}">
								<g:link class="accept-answer-button btn btn-default btn-small" action="acceptAnswer" id="${question.id}"><i class="fa fa-check fa-2x"></i></g:link>
							</g:elseif>
						</div>
					</div>
					<div class="full-width padding-left">
						${answer?.text}
						<div class="clearfix">
							<div class="pull-right">
								<g:render model="${[profile:answer?.creator?.profile]}" template="/profile/show" />,
								<g:formatDate date="${answer?.dateCreated}" type="date" />
							</div>
						</div>
						<g:if test="${answer?.attachments?.size() > 0}">
							<g:render model="${[attachments:answer?.attachments]}" template="/task/attachments" />
						</g:if>
						<g:render bean="${answer}" template="comments" var="commentable" />
					</div>
				</div>
			</g:each>
		</g:if>
		<div class="text-center margin">add answer</div>
	</body>
</html>
