<%@ page import="kola.ReflectionAnswer.Rating" %>

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
					<g:link class="delete btn btn-danger" action="delete" id="${task.id}" title="${message(code: 'default.button.delete.label', args:[entityName])}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', args: [entityName])}');"><i class="fa fa-times"></i></g:link>
				</g:if>
				<g:link class="export btn btn-primary" action="export" id="${task.id}" title="${message(code: 'default.export.label', args:[entityName])}"><i class="fa fa-cloud-download"></i></g:link>
				<g:if test="${authService.canEdit(task)}">
					<g:link class="edit btn btn-primary" action="edit" id="${task.id}" title="${message(code: 'default.edit.label', args:[entityName])}"><i class="fa fa-pencil"></i></g:link>
				</g:if>
				<g:if test="${authService.canAttach(task)}">
					<button type="button" class="btn btn-primary" title="${message(code: 'kola.task.documentation.add')}" onclick="addDocumentation()"><i class="httc-compose-add"></i></button>
					<g:link controller="question" action="create" params="[context:task.id]" class="btn btn-primary" title="${message(code: 'de.httc.plugin.qaa.question.create')}"><i class="httc-question-add"></i></g:link>
				</g:if>
			</div>
		</h1>

		<!-- Nav tabs -->
		<ul class="nav nav-tabs nav-justified" role="tablist">
			<li role="presentation" class="active"><a href="#task" aria-controls="task" role="tab" data-toggle="tab"><i class="httc-task"></i> <g:message code="kola.task"/></a></li>
			<g:if test="${!task.isTemplate}">
				<li role="presentation"><a href="#documentations" aria-controls="documentations" role="tab" data-toggle="tab"><i class="httc-compose"></i> <g:message code="kola.task.documentations" /><g:if test="${taskDocumentationsCount > 0}"> <span class="badge">${taskDocumentationsCount}</span></g:if></a></li>
				<li role="presentation"><a href="#questions" aria-controls="questions" role="tab" data-toggle="tab"><i class="httc-comments"></i> <g:message code="de.httc.plugin.qaa.questions" /><g:if test="${taskQuestionsCount > 0}"> <span class="badge">${taskQuestionsCount}</span></g:if></a></li>
			</g:if>
			<g:if test="${task?.reflectionQuestions?.size() > 0}">
				<li role="presentation"><a href="#reflectionQuestions" aria-controls="reflection-questions" role="tab" data-toggle="tab"><i class="fa fa-lightbulb-o"></i> <g:message code="kola.reflectionQuestions" /><g:if test="${reflectionAnswersCount > 0}"> <span class="badge">${reflectionAnswersCount}</span></g:if></a></li>
			</g:if>
		</ul>

		<!-- Tab panes -->
		<div class="tab-content">
			<div role="tabpanel" class="tab-pane fade in active" id="task">
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
					<div class="col-md-8 col-md-pull-4"><httc:markdown>${task.description}</httc:markdown></div>
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
								<p class="list-group-item-text text-default formatted"><httc:abbreviate>${asset.description}</httc:abbreviate></p>
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
							<li class="list-group-item" id="${step.id}">
								<h4 class="list-group-item-heading">${step.name}</h4>
								<p class="list-group-item-text"><httc:markdown>${step.description}</httc:markdown></p>
								<g:if test="${step.attachments?.size() > 0}">
									<g:render model="${[attachments:step.attachments]}" template="attachments" />
								</g:if>
							</li>
						</g:each>
					</ul>
				</div>
				</g:if>
			</div>
			<g:if test="${!task.isTemplate}">
				<div role="tabpanel" class="tab-pane" id="documentations">
					<ul class="list-group">
						<g:if test="${taskDocumentations[task.id]?.size() > 0}">
							<li class="list-group-item disabled"><b><g:message code="kola.task.documentation.forTask"/>:</b></li>
							<g:each var="taskDocumentation" in="${taskDocumentations[task.id]}">
								<g:render bean="${taskDocumentation}" template="taskDocumentation" var="taskDocumentation" />
							</g:each>
						</g:if>
						<g:each var="step" in="${task?.steps}">
							<g:if test="${taskDocumentations[step.id]?.size() > 0}">
								<li class="list-group-item disabled"><b><g:message code="kola.task.documentation.forStep"/> "${step.name}":</b></li>
								<g:each var="taskDocumentation" in="${taskDocumentations[step.id]}">
									<g:render bean="${taskDocumentation}" template="taskDocumentation" var="taskDocumentation" />
								</g:each>
							</g:if>
						</g:each>
						<g:if test="${authService.canAttach(task)}">
							<div id="new-documentation-controls" class="text-center margin">
								<g:if test="${taskDocumentationsCount < 1}">
									<div class="margin-vertical-large text-center"><g:message code="kola.task.documentation.empty" /></div>
								</g:if>
								<button type="button" class="btn btn-primary" title="${message(code: 'kola.task.documentation.add')}" onclick="addDocumentation()"><i class="httc-compose-add"></i> <g:message code="kola.task.documentation.add" /></button>
							</div>
							<li class="list-group-item list-group-item-warning new-documentation hidden" id="new-documentation">
								<g:form class="form-horizontal" action="saveTaskDocumentation" enctype="multipart/form-data">
									<div class="form-group">
										<label for="reference" class="col-sm-3 control-label"><g:message code="kola.task.documentation.for" />:</label>
										<div class="col-sm-9">
											<select id="reference" name="reference" class="form-control">
												<option value="${task.id}" selected><g:message code="kola.task.documentation.forTask" /></option>
												<g:each var="step" in="${task?.steps}">
													<option value="${step.id}"><g:message code="kola.task.documentation.forStep" /> "${step.name}"</option>
												</g:each>
											</select>
										</div>
									</div>
									<textarea name="text" class="form-control" rows="5" placeholder="${message(code:'kola.task.documentation.placeholder')}" required></textarea>
									<g:render model="${[attachments:[], mode:'edit']}" template="attachments" />
									<div class="text-right form-padding"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'kola.task.documentation')]"/></button></div>
								</g:form>
							</li>
						</g:if>
					</ul>
				</div>
				<div role="tabpanel" class="tab-pane fade" id="questions">
					<div class="list-group">
						<g:if test="${taskQuestions[task.id]?.size() > 0}">
							<div class="list-group-item disabled"><b><g:message code="kola.task.documentation.forTask"/>:</b></div>
							<g:each var="taskQuestion" in="${taskQuestions[task.id]}">
								<g:render bean="${taskQuestion}" var="question" template="/question/questionListItem" plugin="httcQAA" />
							</g:each>
						</g:if>
						<g:each var="step" in="${task?.steps}">
							<g:if test="${taskQuestions[step.id]?.size() > 0}">
								<div class="list-group-item disabled"><b><g:message code="kola.task.documentation.forStep"/> "${step.name}":</b></div>
								<g:each var="taskQuestion" in="${taskQuestions[step.id]}">
									<g:render bean="${taskQuestion}" var="question" template="/question/questionListItem" plugin="httcQAA" />
								</g:each>
							</g:if>
						</g:each>
					</div>
					<div id="new-question-controls" class="text-center margin">
						<g:if test="${taskQuestionsCount < 1}">
							<div class="margin-vertical-large text-center"><g:message code="kola.task.questions.empty" /></div>
						</g:if>
						<g:if test="${authService.canAttach(task)}"><g:link controller="question" action="create" params="[context:task.id]" class="btn btn-primary" title="${message(code: 'de.httc.plugin.qaa.question.create')}"><i class="httc-question-add"></i> <g:message code="de.httc.plugin.qaa.question.create" /></g:link></g:if>
					</div>
				</div>
			</g:if>
			<g:if test="${task?.reflectionQuestions?.size() > 0}">
			<div role="tabpanel" class="tab-pane fade" id="reflectionQuestions">
				<ul class="list-group">
					<g:each var="reflectionQuestion" in="${task?.reflectionQuestions}">
						<li class="list-group-item clearfix">
							<b class="text-warning">${reflectionQuestion.name}</b>
							<g:if test="${!task.isTemplate && authService.canAttach(task)}">
								<button type="button" title="${message(code:'kola.reflectionAnswer.create')}" class="btn btn-primary pull-right create-rating-button" onclick="addReflectionRating($(this))"><i class="httc-rating-positive"></i> <g:message code="kola.reflectionAnswer.create" /></button>
							</g:if>
						</li>
						<g:each var="reflectionAnswer" in="${reflectionAnswers[reflectionQuestion.id]}">
							<li class="list-group-item">
								<div class="list-group-item-text clearfix">
									<div class="media reflectionAnswerDisplay">
										<div class="media-left">
											<g:if test="${reflectionAnswer.rating}">
												<span class="fa-stack fa-lg pull-left display-rating" title="${message(code:'kola.reflectionAnswer.' + reflectionAnswer.rating.toString().toLowerCase())}">
													<i class="fa fa-circle fa-stack-2x ${reflectionAnswer.rating == Rating.POSITIVE ? 'display-rating-positive' : reflectionAnswer.rating == Rating.NEUTRAL ? 'display-rating-neutral' : 'display-rating-negative'}"></i>
													<i class="fa fa-stack-1x fa-inverse ${reflectionAnswer.rating == Rating.POSITIVE ? 'httc-rating-positive' : reflectionAnswer.rating == Rating.NEUTRAL ? 'httc-rating-neutral' : 'httc-rating-negative'}"></i>
												</span>
											</g:if>
										</div>
										<div class="media-body">
											<p class="formatted reflectionAnswer">${reflectionAnswer.text}</p>
										</div>
									</div>
									<g:if test="${authService.canEdit(reflectionAnswer)}">
										<g:form class="form hidden" action="updateReflectionAnswer" id="${reflectionAnswer.id}" method="PUT">
											<input type="hidden" name="rating" value="${reflectionAnswer.rating}" />
											<div class="row">
												<div class="col-md-3">
													<div class="btn-group btn-group-lg btn-group-rating form-padding-all" role="group">
														<button type="button" value="${Rating.POSITIVE}" title="${message(code:'kola.reflectionAnswer.positive')}" class="positive btn btn-default${reflectionAnswer.rating == Rating.POSITIVE ? ' active' : ''}"><i class="fa httc-rating-positive${reflectionAnswer.rating == Rating.POSITIVE ? ' fa-inverse' : ''}"></i></button>
														<button type="button" value="${Rating.NEUTRAL}" title="${message(code:'kola.reflectionAnswer.neutral')}" class="neutral btn btn-default${reflectionAnswer.rating == Rating.NEUTRAL ? ' active' : ''}"><i class="fa httc-rating-neutral${reflectionAnswer.rating == Rating.NEUTRAL ? ' fa-inverse' : ''}"></i></button>
														<button type="button" value="${Rating.NEGATIVE}" title="${message(code:'kola.reflectionAnswer.negative')}" class="negative btn btn-default${reflectionAnswer.rating == Rating.NEGATIVE ? ' active' : ''}"><i class="fa httc-rating-negative${reflectionAnswer.rating == Rating.NEGATIVE ? ' fa-inverse' : ''}"></i></button>
													</div>
												</div>
												<div class="col-md-5"><div class="form-padding-all"><input name="text" type="text" class="form-control" rows="5" placeholder="${message(code:'kola.reflectionAnswer.placeholder')}" value="${reflectionAnswer.text}"></div></div>
												<div class="col-md-4"><div class="form-padding-all"><button type="submit" class="btn btn-success pull-right"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'kola.reflectionAnswer')]" /></button></div></div>
											</div>
										</g:form>
									</g:if>
									<small class="pull-right">
										<g:render bean="${reflectionAnswer.creator.profile}" template="/profile/show" var="profile" />,
										<g:formatDate date="${reflectionAnswer.lastUpdated}" type="datetime" style="LONG" timeStyle="SHORT"/>
										<g:if test="${authService.canEdit(reflectionAnswer)}">
											<button type="button" class="btn btn-default edit-rating-button" onclick="editReflectionRating($(this))"><i class="fa fa-pencil"></i> <g:message code="default.button.edit.label" /></button>
										</g:if>
									</small>
								</div>
							</li>
						</g:each>
						<li class="list-group-item list-group-item-warning new-answer hidden">
							<g:form class="form" action="saveReflectionAnswer">
								<input type="hidden" name="task" value="${task.id}">
								<input type="hidden" name="question" value="${reflectionQuestion.id}">
								<input type="hidden" name="rating" value="" />
								<div class="row">
									<div class="col-md-3">
										<div class="btn-group btn-group-lg btn-group-rating form-padding-all" role="group">
											<button type="button" value="${Rating.POSITIVE}" title="${message(code:'kola.reflectionAnswer.positive')}" class="positive btn btn-default"><i class="fa httc-rating-positive"></i></button>
											<button type="button" value="${Rating.NEUTRAL}" title="${message(code:'kola.reflectionAnswer.neutral')}" class="neutral btn btn-default"><i class="fa httc-rating-neutral"></i></button>
											<button type="button" value="${Rating.NEGATIVE}" title="${message(code:'kola.reflectionAnswer.negative')}" class="negative btn btn-default"><i class="fa httc-rating-negative"></i></button>
										</div>
									</div>
									<div class="col-md-5"><div class="form-padding-all"><input name="text" type="text" class="form-control" rows="5" placeholder="${message(code:'kola.reflectionAnswer.placeholder')}" value=""></div></div>
									<div class="col-md-4"><div class="form-padding-all"><button type="submit" class="btn btn-success pull-right"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'kola.reflectionAnswer')]" /></button></div></div>
								</div>
							</g:form>
						</li>
					</g:each>
				</ul>
			</div>
			</g:if>
		</div>
		<script>
			function addDocumentation() {
				$('#new-documentation-controls').hide();
				$('#new-documentation').removeClass('hidden');
				$('.nav-tabs').find('a[href=#documentations]').on('shown.bs.tab', function() {
					$('#new-documentation').find('textarea').focus();
					$('.nav-tabs').find('a[href=#documentations]').off('shown.bs.tab');
				}).tab('show');
				$('#new-documentation').find('textarea').focus();
			}

			function addReflectionRating($button) {
				$(".create-rating-button,.edit-rating-button").hide();
				$button.parent().nextAll('.new-answer').first().removeClass('hidden').find("input[type='text']").focus();
			}

			function editReflectionRating($button) {
				$(".create-rating-button,.edit-rating-button").hide();
				$button.hide().parent().prevAll('.reflectionAnswerDisplay').hide().next('.form').removeClass('hidden').find("input[type='text']").focus();
				$button.parents("li.list-group-item").addClass("list-group-item-warning");
			}

			$(document).ready(function() {
				function scrollToElement(elem) {
					$('html, body').animate({
						scrollTop: elem.offset().top
					}, 1000);
				}

				// show active tab on reload
				if (location.hash !== '') {
					var splitted = location.hash.split("_");
					var tab = $('a[href="' + splitted[0] + '"]');
					if (splitted.length > 1) {
						// this scrolls to a specific anchor id when being linked to it (e.g. from the "task documentations" list)
						// check if tab to select is already the active one
						if (tab.parent().hasClass("active")) {
							scrollToElement($("#" + splitted[1]));
						}
						else {
							tab.on('shown.bs.tab', function() {
								tab.off('shown.bs.tab');
								scrollToElement($("#" + splitted[1]));
							});
						}
					}
					tab.tab('show');
				}
				// remember the hash in the URL without jumping
				$('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
					if (history.replaceState) {
						history.replaceState(null, null, '#'+$(e.target).attr('href').substr(1));
					}
					else {
						location.hash = '#'+$(e.target).attr('href').substr(1);
					}
				});
				// reflection rating buttons
				$(".btn-group-rating > .btn").click(function(){
				    $(this).addClass("active").find("i.fa").addClass("fa-inverse");
					$(this).siblings().removeClass("active").find("i.fa").removeClass("fa-inverse");
					$(this).closest("form").find("input[name='rating']").val($(this).val());
					$(this).parents(".row").find("input[type='text']").focus();
				});
			});
		</script>
	</body>
</html>
