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
	<div class="col-sm-10"><g:textArea rows="8" name="description" class="form-control" data-provide="markdown" data-iconlibrary="fa" value="${taskInstance?.description}"/></div>
</div>

<g:if test="${!taskInstance?.isTemplate}">
	<div class="form-group ${hasErrors(bean: taskInstance, field: 'assignee', 'error')} ">
		<label for="assignee" class="col-sm-2 control-label">
			<g:message code="kola.task.assign" />:
		</label>
		<div class="col-sm-10">
			<select name="assignee.id" class="form-control" value="${taskInstance?.assignee?.id}">
				<option value=""></option>
				<g:each var="profile" in="${authService.assignableUserProfiles}">
					<option value="${profile.user.id}"${taskInstance?.assignee?.id == profile.user.id ? ' selected' : ''}>${profile.displayName}</option>
				</g:each>
			</select>
		</div>
	</div>

	<div class="form-group ${hasErrors(bean: taskInstance, field: 'due', 'error')} ">
		<label for="due" class="col-sm-2 control-label">
			<g:message code="kola.task.due" />:
		</label>
		<div class="col-sm-10"><input type="date" class="form-control" name="due" value="${formatDate(format:'yyyy-MM-dd',date:taskInstance?.due)}" placeholder="yyyy-MM-dd"></div>
	</div>
</g:if>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'attachments', 'error')} ">
	<label for="attachments" class="col-sm-2 control-label">
		<g:message code="kola.task.attachments" />:
	</label>
	<div class="col-sm-10" id="attachments-container">
		<g:if test="${taskInstance?.attachments?.size() > 0}">
			<ul class="list-group sortable">
				<g:each var="assetInstance" in="${taskInstance?.attachments}">
					<li class="list-group-item clearfix">
						<input type="hidden" name="attachments" value="${assetInstance.id}">
						<h4 class="list-group-item-heading">
							<g:if test="${taskInstance?.attachments?.size() > 1}">
								<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
							</g:if>
							<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank">${assetInstance.name}</a>
							<button type="button" class="btn btn-danger pull-right" title="${message(code:'default.button.delete.label')}" onclick="$(this).closest('li').remove()"><i class="fa fa-times"></i></button>
						</h4>
						<p class="list-group-item-text">
							<label><g:message code="kola.meta.mimeType" />:</label>
							<code>${assetInstance.mimeType}</code>
						</p>
					</li>
				</g:each>
			</ul>
		</g:if>
		<div class="form-padding pull-left">
			<label><g:message code="default.add.label" args="${[message(code:'kola.task.attachment')]}" />: </label>
		</div>
		<input type="file" name="_newAttachment" class="new-attachment form-padding">
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
				<li class="list-group-item clearfix">
					<g:if test="${step.id}"><input type="hidden" name="steps[${i}].id" value="${step.id}"></g:if>
					<input type="hidden" name="steps[${i}].deleted" class="deleteFlag" value="false">
					<h4 class="list-group-item-heading clearfix">
						<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
						<span class="text-muted"><g:message code="kola.task.step" /> <span class="step-index">${i+1}</span></span>
						<button type="button" class="btn btn-danger pull-right" onclick="deleteStep($(this))"><i class="fa fa-times" title="${message(code:'default.button.delete.label')}"></i></button>
					</h4>
					<div class="list-group-item-text">
						<div class="form-group">
							<label for="name" class="col-sm-2 control-label">
								<g:message code="kola.meta.name" />
								<span class="required-indicator">*</span>:
							</label>
							<div class="col-sm-10"><input type="text" name="steps[${i}].name" class="form-control" value="${step.name}" required></div>
						</div>
						<div class="form-group">
							<label for="name" class="col-sm-2 control-label">
								<g:message code="kola.meta.description" />
							</label>
							<div class="col-sm-10"><textarea name="steps[${i}].description" class="form-control" rows="6" data-provide="markdown" data-iconlibrary="fa">${step.description}</textarea></div>
						</div>
					</div>
				</li>
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

<script>
	function deleteStep($button) {
		var $li = $button.closest("li");
		$(".deleteFlag", $li).val("true");
		$li.hide()
	}

	function addStep() {
		var stepCount = $("#step-list li").size()
		var $li = $("<li class='list-group-item clearfix'>");
		$li.append($("<input type='text' name='steps["+stepCount+"].name'>"));
		$("#step-list").append($li);
	}

	$(document).ready(function() {
		$(".sortable").each(function() {
			var sortable = Sortable.create(this, { handle:".drag-handle" });
			
			if ($(this).attr("id") == "step-list") {
				sortable.option("onUpdate", function() {
					// update all step indices according to new sort order
					$("#step-list li").each(function(index) {
						var prefix = "step[" + index + "]";
						$(":input", $(this)).each(function() {
							var field = $(this);
							var name = field.attr("name");
							if (name) {
								var replaced = name.replace(/steps\[.*?\]/, prefix);
								field.attr("name", replaced);
								console.log(field.attr("name") + "=" + field.val());
							}
						})
					});

				});
			}
		})
		$(document).on("change", ".new-attachment", function() {
			var emptyFileChooserCount = $("#attachments-container input:file").filter(function() { return $(this).val() == ""; }).length;
			if (emptyFileChooserCount == 0) {
				$("#attachments-container").append($("<input type='file' name='_newAttachment' class='new-attachment form-padding'>"));
			}
		});
	});
</script>

