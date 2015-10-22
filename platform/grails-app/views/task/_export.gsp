
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<g:set var="assetService" bean="assetService"/>
<html>
	<head>
		<title>${taskInstance?.name}</title>
		<link rel="stylesheet" href="${resource(dir:'assets', file:'application.css')}" type="text/css" media="all" />

		<style  type="text/css">
		  @page { size:210mm 297mm; @bottom-left { content: element(header); }}
		  *::after, *::before { display:block !important; }
		  #print-footer { position:running(header); font-size:10px; line-height:11px; vertical-align: middle; text-align: right }
		  img { width: 200px }
		  .panel { page-break-inside: avoid; }
		</style>
	</head>
	<body>
		<div id="print-footer">
			<img src="${assetPath(src: 'Kola-h-100.png', absolute:true)}" width="44"/>
			Kompetenzorientiertes Lernen im Arbeitsprozess mit digitalen Medien
		</div>
		<h1 class="page-header">
			${taskInstance?.name}
		</h1>
		<div class="well pull-right">
			<table class="task-info-table">
				<g:if test="${taskInstance?.due}">
					<tr>
						<td><g:message code="kola.task.due" />:</td>
						<td><g:formatDate date="${taskInstance.due}" type="date"/></td>
					</tr>
				</g:if>
				<g:if test="${taskInstance?.assignee}">
					<tr>
						<td><g:message code="kola.task.assignee" />:</td>
						<td>${taskInstance.assignee.profile.displayName}</td>
					</tr>
				</g:if>
				<g:if test="${!taskInstance?.isTemplate}">
					<tr>
						<td><g:message code="kola.task.done" />:</td>
						<td><g:message code="kola.${taskInstance.done ? 'yes' : 'no'}" /></td>
					</tr>
				</g:if>
				<tr>
					<td><g:message code="kola.meta.creator" />:</td>
					<td>${taskInstance.creator.profile.displayName}</td>
				</tr>
				<tr>
					<td><g:message code="kola.meta.lastUpdated" />:</td>
					<td><g:formatDate date="${taskInstance.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/></td>
				</tr>
			</table>
		</div>
		<kola:markdown>${taskInstance.description}</kola:markdown>
		<g:if test="${taskInstance?.attachments?.size() > 0}">
			<g:render bean="${taskInstance?.attachments}" template="exportAttachments" var="attachments" />
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
							<p class="list-group-item-text text-default formatted">${assetInstance.description}</p>
						</a>
					</g:each>
				</div>
			</div>
		</g:if>
		<g:if test="${taskInstance?.steps?.size() > 0}">
			<div class="panel panel-default">
				<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.task.steps" /></h3></div>
				<ul class="list-group steps">
					<g:each var="step" in="${taskInstance?.steps}" status="i">
						<li class="list-group-item">
							<h4 class="list-group-item-heading"><span class="text-muted">${i+1}.</span> ${step.name}</h4>
							<p class="list-group-item-text"><kola:markdown>${step.description}</kola:markdown></p>
							<g:if test="${step.attachments?.size() > 0}">
								<g:render bean="${step.attachments}" template="exportAttachments" var="attachments" />
							</g:if>
						</li>
					</g:each>
				</ul>
			</div>
		</g:if>
		<g:if test="${taskInstance?.reflectionQuestions?.size() > 0}">
			<div class="panel panel-default">
				<div class="panel-heading"><h3 class="panel-title"><g:message code="kola.reflectionQuestions"/></h3></div>
				<ul class="list-group">
					<g:each var="reflectionQuestion" in="${taskInstance?.reflectionQuestions}">
						<li class="list-group-item"><b class="text-warning">${reflectionQuestion.name}</b></li>
						<g:each var="reflectionAnswer" in="${reflectionAnswers[reflectionQuestion.id]}">
							<li class="list-group-item">
								<div class="list-group-item-text clearfix">
									<p class="formatted">${reflectionAnswer.text}</p>
									<small class="pull-right">
										${reflectionAnswer.creator.profile.displayName},
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
								<g:render bean="${taskDocumentation.attachments}" template="exportAttachments" var="attachments" />
								<small class="pull-right">
									${taskDocumentation.creator.profile.displayName},
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
										<g:render bean="${taskDocumentation.attachments}" template="exportAttachments" var="attachments" />
										<small class="pull-right">
											${taskDocumentation.creator.profile.displayName},
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
