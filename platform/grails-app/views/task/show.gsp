
<g:set var="authService" bean="authService"/>
<g:set var="assetService" bean="assetService"/>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: taskInstance.isTemplate?.toBoolean() ? 'kola.taskTemplate' : 'kola.task')}" />
		<g:set var="entitiesName" value="${message(code: taskInstance.isTemplate?.toBoolean() ? 'kola.taskTemplates' : 'kola.tasks')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<asset:stylesheet src="blueimp-gallery.min.css"/>
		<asset:javascript src="jquery.blueimp-gallery.min.js"/>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="kola.home" /></g:link></li>
			<li><g:link action="index" params="[isTemplate:taskInstance.isTemplate]">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.show.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<h1 class="page-header clearfix">
			${taskInstance?.name}
			<div class="buttons pull-right">
				<g:if test="${authService.canDelete(taskInstance)}">
					<g:link class="delete btn btn-danger" action="delete" id="${taskInstance.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
				</g:if>
				<g:if test="${authService.canEdit(taskInstance)}">
					<g:link class="export btn btn-primary" action="export" id="${taskInstance.id}" title="${message(code: 'default.export.label', args:[entityName])}"><i class="fa fa-cloud-download"></i></g:link>
					<g:link class="edit btn btn-primary" action="edit" id="${taskInstance.id}" title="${message(code: 'default.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
				</g:if>
			</div>
		</h1>
		<div class="row">
			<div class="col-md-4 col-md-push-8">
				<div class="well">
					<table class="task-info-table">
						<g:if test="${taskInstance?.due}">
							<tr class="text-danger">
								<td><g:message code="kola.task.due" />:</td>
								<td><g:formatDate date="${taskInstance.due}" type="date"/></td>
							</tr>
						</g:if>
						<g:if test="${taskInstance?.assignee}">
							<tr>
								<td><g:message code="kola.task.assignee" />:</td>
								<td><g:render bean="${taskInstance.assignee.profile}" template="/profile/show" var="profile" /></td>
							</tr>
						</g:if>
						<g:if test="${!taskInstance?.isTemplate}">
							<tr>
								<td><g:message code="kola.task.done" />:</td>
								<td><i class="fa fa-lg fa-${taskInstance.done ? 'check text-success' : 'minus text-warning'}"></i></td>
							</tr>
						</g:if>
						<tr>
							<td><g:message code="kola.meta.creator" />:</td>
							<td><g:render bean="${taskInstance.creator.profile}" template="/profile/show" var="profile" /></td>
						</tr>
						<tr>
							<td><g:message code="kola.meta.lastUpdated" />:</td>
							<td><g:formatDate date="${taskInstance.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></td>
						</tr>
					</table>
				</div>
			</div>
			<div class="col-md-8 col-md-pull-4"><kola:markdown>${taskInstance.description}</kola:markdown></div>
		</div>
		<g:if test="${taskInstance?.attachments?.size() > 0}">
			<g:render bean="${taskInstance?.attachments}" template="attachments" var="attachments" />
		</g:if>
		<g:if test="${taskInstance?.resources?.size() > 0}">
		<div class="panel panel-default">
			<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.assets" /></h3></div>
			<div class="list-group">
				<g:each var="assetInstance" in="${taskInstance?.resources}">
					<a href="${assetService.createEncodedLink(assetInstance)}" class="list-group-item" target="_blank">
						<h4 class="list-group-item-heading">
							<i class="fa fa-external-link"></i> ${assetInstance.name}
						</h4>
						<p class="list-group-item-text text-default formatted">${assetInstance.description?.take(100)}</p>
					</a>
				</g:each>
			</div>
		</div>
		</g:if>
		<g:if test="${taskInstance?.steps?.size() > 0}">
		<div class="panel panel-default">
			<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.task.steps" /></h3></div>
			<ul class="list-group steps">
				<g:each var="step" in="${taskInstance?.steps}">
					<li class="list-group-item">
						<h4 class="list-group-item-heading">${step.name}</h4>
						<p class="list-group-item-text"><kola:markdown>${step.description}</kola:markdown></p>
						<g:if test="${step.attachments?.size() > 0}">
							<g:render bean="${step.attachments}" template="attachments" var="attachments" />
						</g:if>
					</li>
				</g:each>
			</ul>
		</div>
		</g:if>
		<g:if test="${taskInstance?.reflectionQuestions?.size() > 0}">
		<div class="panel panel-default">
			<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.reflectionQuestions" /></h3></div>
			<ul class="list-group">
				<g:each var="reflectionQuestion" in="${taskInstance?.reflectionQuestions}">
					<li class="list-group-item"><b class="text-warning">${reflectionQuestion.name}</b></li>
					<g:each var="reflectionAnswer" in="${reflectionAnswers[reflectionQuestion.id]}">
						<li class="list-group-item">
							<div class="list-group-item-text clearfix">
								<p class="formatted">${reflectionAnswer.text}</p>
								<small class="pull-right">
									<g:render bean="${reflectionAnswer.creator.profile}" template="/profile/show" var="profile" />,
									<g:formatDate date="${reflectionAnswer.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/>
								</small>
							</div>
						</li>
					</g:each>
				</g:each>
			</ul>
		</div>
		</g:if>
		<g:if test="${taskDocumentations?.size() > 0}">
		<div class="panel panel-success">
			<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.task.documentations" /></h3></div>
			<ul class="list-group">
				<g:each var="taskDocumentation" in="${taskDocumentations[taskInstance.id]}">
					<li class="list-group-item">
						<div class="list-group-item-text clearfix">
							<p class="formatted">${taskDocumentation.text}</p>
							<g:render bean="${taskDocumentation.attachments}" template="attachments" var="attachments" />
							<small class="pull-right">
								<g:render bean="${taskDocumentation.creator.profile}" template="/profile/show" var="profile" />,
								<g:formatDate date="${taskDocumentation.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/>
							</small>
						</div>
					</li>
				</g:each>
				<g:each var="step" in="${taskInstance?.steps}">
					<g:if test="${taskDocumentations[step.id]?.size() > 0}">
					<li class="list-group-item"><b><g:message code="kola.task.documentation.forStep"/> "${step.name}":</b></li>
					<g:each var="taskDocumentation" in="${taskDocumentations[step.id]}">
						<li class="list-group-item">
							<div class="list-group-item-text clearfix">
								<p class="formatted">${taskDocumentation.text}</p>
								<g:render bean="${taskDocumentation.attachments}" template="attachments" var="attachments" />
								<small class="pull-right">
									<g:render bean="${taskDocumentation.creator.profile}" template="/profile/show" var="profile" />,
									<g:formatDate date="${taskDocumentation.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/>
								</small>
							</div>
						</li>
					</g:each>
					</g:if>
				</g:each>
			</ul>
		</div>
		</g:if>
	</body>
</html>
