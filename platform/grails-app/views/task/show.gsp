<g:set var="authService" bean="authService"/>
<g:set var="repositoryService" bean="repositoryService"/>
<html>
	<head>
		<meta name="layout" content="editor">
		<g:set var="entityName" value="${message(code: task.isTemplate?.toBoolean() ? 'kola.taskTemplate' : 'kola.task')}" />
		<g:set var="entitiesName" value="${message(code: task.isTemplate?.toBoolean() ? 'kola.taskTemplates' : 'kola.tasks')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
		<asset:stylesheet src="blueimp-gallery.min.css"/>
		<asset:javascript src="jquery.blueimp-gallery.min.js"/>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="app.home" /></g:link></li>
			<li><g:link action="index" params="[isTemplate:task.isTemplate]">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.show.label" args="[entityName]" /></li>
		</ol>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<h1 class="page-header clearfix">
			${task?.name}
			<div class="buttons pull-right">
				<g:if test="${authService.canDelete(task)}">
					<g:link class="delete btn btn-danger" action="delete" id="${task.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"><i class="fa fa-times"></i></g:link>
				</g:if>
				<g:link class="export btn btn-primary" action="export" id="${task.id}" title="${message(code: 'default.export.label', args:[entityName])}"><i class="fa fa-cloud-download"></i></g:link>
				<g:if test="${authService.canEdit(task)}">
					<g:link class="edit btn btn-primary" action="edit" id="${task.id}" title="${message(code: 'default.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
				</g:if>
			</div>
		</h1>
		<div class="row">
			<div class="col-md-4 col-md-push-8">
				<div class="well">
					<table class="task-info-table">
						<g:if test="${task?.due}">
							<tr class="text-danger">
								<td><g:message code="kola.task.due" />:</td>
								<td><g:formatDate date="${task.due}" type="date"/></td>
							</tr>
						</g:if>
						<g:if test="${task?.assignee}">
							<tr>
								<td><g:message code="kola.task.assignee" />:</td>
								<td><g:render bean="${task.assignee.profile}" template="/profile/show" var="profile" /></td>
							</tr>
						</g:if>
						<g:if test="${!task?.isTemplate}">
							<tr>
								<td><g:message code="kola.task.done" />:</td>
								<td><i class="fa fa-lg fa-${task.done ? 'check text-success' : 'minus text-warning'}"></i></td>
							</tr>
						</g:if>
						<tr>
							<td><g:message code="kola.meta.creator" />:</td>
							<td><g:render bean="${task.creator.profile}" template="/profile/show" var="profile" /></td>
						</tr>
						<tr>
							<td><g:message code="kola.meta.lastUpdated" />:</td>
							<td><g:formatDate date="${task.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></td>
						</tr>
					</table>
				</div>
			</div>
			<div class="col-md-8 col-md-pull-4"><kola:markdown>${task.description}</kola:markdown></div>
		</div>
		<g:if test="${task?.attachments?.size() > 0}">
		<g:render model="${[attachments:task?.attachments]}" template="attachments" />
		</g:if>
		<g:if test="${task?.resources?.size() > 0}">
		<div class="panel panel-default">
			<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.assets" /></h3></div>
			<div class="list-group">
				<g:each var="asset" in="${task?.resources}">
					<a href="${repositoryService.createEncodedLink(asset)}" class="list-group-item" target="_blank">
						<h4 class="list-group-item-heading">
							<i class="fa fa-external-link"></i> ${asset.name}
						</h4>
						<p class="list-group-item-text text-default formatted"><kola:abbreviate>${asset.description}</kola:abbreviate></p>
					</a>
				</g:each>
			</div>
		</div>
		</g:if>
		<g:if test="${task?.steps?.size() > 0}">
		<div class="panel panel-default">
			<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.task.steps" /></h3></div>
			<ul class="list-group steps">
				<g:each var="step" in="${task?.steps}">
					<li class="list-group-item">
						<h4 class="list-group-item-heading">${step.name}</h4>
						<p class="list-group-item-text"><kola:markdown>${step.description}</kola:markdown></p>
						<g:if test="${step.attachments?.size() > 0}">
							<g:render model="${[attachments:step.attachments]}" template="attachments" />
						</g:if>
					</li>
				</g:each>
			</ul>
		</div>
		</g:if>
		<g:if test="${task?.reflectionQuestions?.size() > 0}">
		<div class="panel panel-default">
			<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.reflectionQuestions" /></h3></div>
			<ul class="list-group">
				<g:each var="reflectionQuestion" in="${task?.reflectionQuestions}">
					<li class="list-group-item clearfix">
						<b class="text-warning">${reflectionQuestion.name}</b>
						<g:if test="${authService.canAttach(task)}">
							<button type="button" title="${message(code:'kola.reflectionAnswer.create')}" class="btn btn-default pull-right" onclick="$(this).parent().nextAll('.new-answer').first().removeClass('hidden').find('textarea').focus()"><i class="fa fa-comment-o"></i></button>
						</g:if>
					</li>
					<g:each var="reflectionAnswer" in="${reflectionAnswers[reflectionQuestion.id]}">
						<li class="list-group-item">
							<div class="list-group-item-text clearfix">
								<p class="formatted reflectionAnswer">${reflectionAnswer.text}</p>
								<g:if test="${authService.canEdit(reflectionAnswer)}">
									<g:form class="form hidden" action="updateReflectionAnswer" id="${reflectionAnswer.id}" method="PUT">
										<textarea name="text" class="form-control" rows="5" placeholder="${message(code:'kola.reflectionAnswer.placeholder')}">${reflectionAnswer.text}</textarea>
										<div class="text-right form-padding-all"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'kola.reflectionAnswer')]" /></button></div>
									</g:form>
								</g:if>
								<small class="pull-right">
									<g:if test="${authService.canEdit(reflectionAnswer)}">
										<button type="button" class="btn btn-default" onclick="$(this).hide().parent().prevAll('.reflectionAnswer').hide().next('.form').removeClass('hidden').find('textarea').focus()"><i class="fa fa-pencil"></i> <g:message code="default.button.edit.label" /></button>
									</g:if>
									<g:render bean="${reflectionAnswer.creator.profile}" template="/profile/show" var="profile" />,
									<g:formatDate date="${reflectionAnswer.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/>
								</small>
							</div>
						</li>
					</g:each>
					<li class="list-group-item new-answer hidden">
						<g:form class="form" action="saveReflectionAnswer">
							<input type="hidden" name="task" value="${task.id}">
							<input type="hidden" name="question" value="${reflectionQuestion.id}">
							<textarea name="text" class="form-control" rows="5" placeholder="${message(code:'kola.reflectionAnswer.placeholder')}"></textarea>
							<div class="text-right form-padding"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'kola.reflectionAnswer')]"/></button></div>
						</g:form>
					</li>
				</g:each>
			</ul>
		</div>
		</g:if>
		<g:if test="${taskDocumentations?.size() > 0 || authService.canAttach(task)}">
		<div class="panel panel-success">
			<div class="panel-heading clearfix">
				<h3 class="panel-title clearfix">
					<g:message code="kola.task.documentations" />
					<g:if test="${authService.canAttach(task)}">
						<button type="button" class="btn btn-default pull-right" onclick="$(this).hide().closest('.panel').find('.new-documentation').removeClass('hidden').find('textarea').focus()"><i class="fa fa-plus"></i> <g:message code="kola.task.documentation.add" /></button>
					</g:if>
				</h3>
			</div>
			<ul class="list-group">
				<g:each var="taskDocumentation" in="${taskDocumentations[task.id]}">
					<g:render bean="${taskDocumentation}" template="taskDocumentation" var="taskDocumentation" />
				</g:each>
				<g:each var="step" in="${task?.steps}">
					<g:if test="${taskDocumentations[step.id]?.size() > 0}">
					<li class="list-group-item"><b><g:message code="kola.task.documentation.forStep"/> "${step.name}":</b></li>
					<g:each var="taskDocumentation" in="${taskDocumentations[step.id]}">
						<g:render bean="${taskDocumentation}" template="taskDocumentation" var="taskDocumentation" />
					</g:each>
					</g:if>
				</g:each>
				<g:if test="${authService.canAttach(task)}">
					<li class="list-group-item new-documentation hidden">
						<g:form class="form" action="saveTaskDocumentation" enctype="multipart/form-data">
							<input type="hidden" name="reference" value="${task.id}">
							<textarea name="text" class="form-control" rows="5" placeholder="${message(code:'kola.task.documentation.placeholder')}" required></textarea>
							<g:render model="${[attachments:[], mode:'edit']}" template="attachments" />
							<div class="text-right form-padding"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'kola.task.documentation')]"/></button></div>
						</g:form>
					</li>
				</g:if>
			</ul>
		</div>
		</g:if>
	</body>
</html>
