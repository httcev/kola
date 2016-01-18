<%@ page import="kola.TaskStep" %>

<g:set var="assetService" bean="assetService"/>
<g:set var="authService" bean="authService"/>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'name', 'error')} required">
	<label for="name" class="col-sm-2 control-label">
		<g:message code="kola.meta.name" />
		<span class="required-indicator">*</span>:
	</label>
	<div class="col-sm-10"><g:textField name="name" class="form-control" required="" value="${taskInstance?.name}"/></div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'description', 'error')} ">
	<label for="description" class="col-sm-2 control-label">
		<g:message code="kola.meta.description" />:
	</label>
	<div class="col-sm-10"><g:textArea rows="8" name="description" class="form-control" data-provide="markdown" data-iconlibrary="fa" data-language="de" data-hidden-buttons="cmdImage cmdCode cmdQuote" value="${taskInstance?.description}"/></div>
</div>

<g:if test="${!taskInstance?.isTemplate?.toBoolean()}">
	<div class="form-group ${hasErrors(bean: taskInstance, field: 'assignee', 'error')} ">
		<label for="assignee" class="col-sm-2 control-label">
			<g:message code="kola.task.assign" />:
		</label>
		<div class="col-sm-10">
			<select name="assignee.id" class="form-control" value="${taskInstance?.assignee?.id}">
				<option value=""></option>
				<g:each var="profile" in="${authService.assignableUserProfiles}">
					<option value="${profile.user.id}"${taskInstance?.assignee?.id == profile.user.id ? ' selected' : ''}>${profile.displayNameFormal}</option>
				</g:each>
			</select>
		</div>
	</div>

	<div class="form-group ${hasErrors(bean: taskInstance, field: 'due', 'error')} ">
		<label for="due" class="col-sm-2 control-label">
			<g:message code="kola.task.due" />:
		</label>
		<div class="col-sm-10"><input type="date" id="due" class="form-control" name="due" value="${formatDate(format:'yyyy-MM-dd',date:taskInstance?.due)}" placeholder="yyyy-MM-dd"></div>
	</div>

	<div class="form-group ${hasErrors(bean: taskInstance, field: 'done', 'error')}">
		<label for="done" class="col-sm-2 control-label">
			<g:message code="kola.task.done" />:
		</label>
		<div class="col-sm-10">
			<div class="checkbox">
				<label>
					<g:checkBox name="done" value="${taskInstance?.done}" /> <g:message code="kola.task.done" />
				</label>
			</div>
		</div>
	</div>
</g:if>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'attachments', 'error')} ">
	<label class="col-sm-2 control-label">
		<g:message code="kola.task.attachments" />:
	</label>
	<div class="col-sm-10">
		<g:render model="${[attachments:taskInstance?.attachments, mode:'edit']}" template="attachments" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'resources', 'error')} ">
	<label for="resources" class="col-sm-2 control-label">
		<g:message code="kola.assets" />:
	</label>
	<div class="col-sm-10">
		<ul id="asset-list" class="list-group sortable">
			<g:each var="assetInstance" in="${taskInstance?.resources}">
				<li class="list-group-item clearfix">
					<input type="hidden" name="resources" value="${assetInstance.id}">
					<h4 class="list-group-item-heading">
						<g:if test="${taskInstance?.resources?.size() > 1}">
							<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
						</g:if>
						<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank">${assetInstance.name}</a>
						<button type="button" class="btn btn-danger pull-right" onclick="$(this).closest('li').remove()" title="${message(code:'default.button.delete.label')}"><i class="fa fa-times"></i></button>
					</h4>
					<p class="list-group-item-text">
						${assetInstance.description?.take(100)}
					</p>
				</li>
			</g:each>
		</ul>
		<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#assetModal"><i class="fa fa-plus"></i> <g:message code="default.add.label" args="${[message(code:'kola.asset')]}" /></button>
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'steps', 'error')} ">
	<label for="steps" class="col-sm-2 control-label">
		<g:message code="kola.task.steps" />:
	</label>
	<div class="col-sm-10">
		<ul id="step-list" class="list-group sortable">
			<g:each var="step" in="${taskInstance?.steps}" status="i">
				<g:render template="stepEditor" model="${[step:step, index:i]}" />
			</g:each>
		</ul>
		<button type="button" class="btn btn-primary" onclick="addStep()"><i class="fa fa-plus"></i> <g:message code="default.add.label" args="${[message(code:'kola.task.step')]}" /></button>
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'reflectionQuestions', 'error')} ">
	<label for="reflectionQuestions" class="col-sm-2 control-label">
		<g:message code="kola.reflectionQuestions" />:
	</label>
	<div class="col-sm-10">
		<ul id="reflectionQuestion-list" class="list-group sortable">
			<g:each var="reflectionQuestionInstance" in="${taskInstance?.reflectionQuestions}">
				<li class="list-group-item clearfix">
					<input type="hidden" name="reflectionQuestions" value="${reflectionQuestionInstance.id}">
					<h4 class="list-group-item-heading">
						<g:if test="${taskInstance?.reflectionQuestions?.size() > 1}">
							<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
						</g:if>
						${reflectionQuestionInstance.name}
						<button type="button" class="btn btn-danger pull-right" onclick="$(this).closest('li').remove()"><i class="fa fa-times" title="${message(code:'default.button.delete.label')}"></i></button>
					</h4>
				</li>
			</g:each>
		</ul>
		<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#reflectionQuestionModal"><i class="fa fa-plus"></i> <g:message code="default.add.label" args="${[message(code:'kola.reflectionQuestion')]}" /></button>
	</div>
</div>

<script id="newStepTemplate" type="text/template">
	<g:render template="stepEditor" model="${[step:new TaskStep(), index:1, isNew:true]}" />
</script>

<script>
	function deleteStep($button) {
		var $li = $button.closest("li");
		// if step has not been saved simply remove dom node
		if ($(".stepId", $li).length == 0) {
			$li.remove();
		}
		else {
			$(".deleteFlag", $li).val("true");
			$li.hide();
		}
		updateStepIndices();
	}

	function addStep() {
		$("#step-list").append($("#newStepTemplate").html());
		updateStepIndices();
	}
</script>

