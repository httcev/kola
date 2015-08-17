<g:set var="assetService" bean="assetService"/>
<g:set var="authService" bean="authService"/>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'name', 'error')} required">
	<label for="name" class="col-sm-2 control-label">
		<g:message code="task.name.label" default="Name" />
		<span class="required-indicator">*</span>:
	</label>
	<div class="col-sm-10"><g:textField name="name" class="form-control" required="" value="${taskInstance?.name}"/></div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'description', 'error')} ">
	<label for="description" class="col-sm-2 control-label">
		<g:message code="task.description.label" default="Beschreibung" />:
	</label>
	<div class="col-sm-10"><g:textArea rows="8" name="description" class="form-control" data-provide="markdown" data-iconlibrary="fa" value="${taskInstance?.description}"/></div>
</div>

<g:if test="${!taskInstance?.isTemplate}">
	<div class="form-group ${hasErrors(bean: taskInstance, field: 'assignee', 'error')} ">
		<label for="assignee" class="col-sm-2 control-label">
			<g:message code="task.assignee.label" default="Zuweisen an" />:
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
			<g:message code="task.due.label" default="Fällig" />:
		</label>
		<div class="col-sm-10"><input type="date" class="form-control" name="due" value="${formatDate(format:'yyyy-MM-dd',date:taskInstance?.due)}" placeholder="yyyy-MM-dd"></div>
	</div>
</g:if>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'attachments', 'error')} ">
	<label for="attachments" class="col-sm-2 control-label">
		<g:message code="task.attachments.label" default="Anhänge" />:
	</label>
	<div class="col-sm-10" id="attachments-container">
		<g:if test="${taskInstance?.attachments?.size() > 0}">
			<ul class="list-group sortable">
				<g:each var="assetInstance" in="${taskInstance?.attachments}">
					<li class="list-group-item clearfix">
						<input type="hidden" name="attachments" value="${assetInstance.id}">
						<h4 class="list-group-item-heading">
							<g:if test="${taskInstance?.attachments?.size() > 1}">
								<div class="btn btn-default drag-handle" title="Verschieben mit Drag&amp;Drop"><i class="fa fa-arrows-v fa-lg"></i></div>
							</g:if>
							<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank">${assetInstance.name}</a>
							<button type="button" class="btn btn-danger pull-right" onclick="$(this).closest('li').remove()"><i class="fa fa-times"></i></button>
						</h4>
						<p class="list-group-item-text">
							<label><g:message code="asset.mimeType.label" default="Mime type" />:</label>
							<code>${assetInstance.mimeType}</code>
						</p>
					</li>
				</g:each>
			</ul>
		</g:if>
		<div class="form-padding"><label><g:message code="task.addAttachment.label" default="Neuer Anhang" />:</label></div> <input type="file" name="_newAttachment" class="new-attachment form-padding">
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'resources', 'error')} ">
	<label for="resources" class="col-sm-2 control-label">
		<g:message code="task.resources.label" default="Lernressourcen" />:
	</label>
	<div class="col-sm-10">
		<ul id="asset-list" class="list-group sortable">
			<g:each var="assetInstance" in="${taskInstance?.resources}">
				<li class="list-group-item clearfix">
					<input type="hidden" name="resources" value="${assetInstance.id}">
					<h4 class="list-group-item-heading">
						<g:if test="${taskInstance?.resources?.size() > 1}">
							<div class="btn btn-default drag-handle" title="Verschieben mit Drag&amp;Drop"><i class="fa fa-arrows-v fa-lg"></i></div>
						</g:if>
						<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank">${assetInstance.name}</a>
						<button type="button" class="btn btn-danger pull-right" onclick="$(this).closest('li').remove()"><i class="fa fa-times"></i></button>
					</h4>
					<p class="list-group-item-text">
						${assetInstance.description?.take(100)}
					</p>
				</li>
			</g:each>
		</ul>
		<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#assetModal"><i class="fa fa-plus"></i> Lernressource hinzufügen</button>
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'steps', 'error')} ">
	<label for="steps" class="col-sm-2 control-label">
		<g:message code="task.steps.label" default="Teilschritte" />:
	</label>
	<div class="col-sm-10">
		<ul id="step-list" class="list-group sortable">
			<g:each var="step" in="${taskInstance?.steps}" status="i">
				<li class="list-group-item clearfix">
					ID=${step.id}
					<g:if test="${step.id}"><input type="hidden" name="steps[${i}].id" value="${step.id}"></g:if>
					<input type="hidden" name="steps[${i}].deleted" class="deleteFlag" value="false">
					<h4 class="list-group-item-heading">
						<div class="btn btn-default drag-handle" title="Verschieben mit Drag&amp;Drop"><i class="fa fa-arrows-v fa-lg"></i></div>
						<input type="text" name="steps[${i}].name" value="${step.name}">
						<button type="button" class="btn btn-danger pull-right" onclick="deleteStep($(this))"><i class="fa fa-times"></i></button>
					</h4>
					<p class="list-group-item-text">
						<textarea name="steps[${i}].description">${step.description}</textarea>
					</p>
				</li>
			</g:each>
		</ul>
		<button type="button" class="btn btn-primary" onclick="addStep()"><i class="fa fa-plus"></i></button>
<%--	
		<g:link resource="task/step" action="create" taskId="${taskInstance.id}" class="btn btn-primary"><i class="fa fa-plus"></i></g:link>
		<g:actionSubmit class="btn btn-primary" value="Teilschritt hinzufügen" action="addStep" />
		<g:select name="steps" from="${kola.TaskStep.list()}" multiple="multiple" optionKey="id" size="5" value="${taskInstance?.steps*.id}" class="form-control"/>
--%>		
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'reflectionQuestions', 'error')} ">
	<label for="reflectionQuestions" class="col-sm-2 control-label">
		<g:message code="task.reflectionQuestions.label" default="Reflexionsaufforderungen" />:
	</label>
	<div class="col-sm-10">
		<ul id="reflectionQuestion-list" class="list-group sortable">
			<g:each var="reflectionQuestionInstance" in="${taskInstance?.reflectionQuestions}">
				<li class="list-group-item clearfix">
					<input type="hidden" name="reflectionQuestions" value="${reflectionQuestionInstance.id}">
					<h4 class="list-group-item-heading">
						<g:if test="${taskInstance?.reflectionQuestions?.size() > 1}">
							<div class="btn btn-default drag-handle" title="Verschieben mit Drag&amp;Drop"><i class="fa fa-arrows-v fa-lg"></i></div>
						</g:if>
						${reflectionQuestionInstance.name}
						<button type="button" class="btn btn-danger pull-right" onclick="$(this).closest('li').remove()"><i class="fa fa-times"></i></button>
					</h4>
				</li>
			</g:each>
		</ul>
		<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#reflectionQuestionModal"><i class="fa fa-plus"></i> Reflexionsaufforderung hinzufügen</button>
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
			/*
			if ($(this).attr("id") == "step-list") {
				sortable.option("onUpdate", function() {
					// update all step indices according to new sort order
					$("#step-list li").each(function(index) {
						var prefix = "step[" + index + "]";
						$("input", $(this)).each(function() {
							var field = $(this);
							var replaced = field.attr("name").replace(/steps\[.*?\]/, prefix);
							field.attr("name", replaced);
							console.log(field.attr("name") + "=" + field.val());
						})
					});

				});
			}
			*/
		})
		$(document).on("change", ".new-attachment", function() {
			var emptyFileChooserCount = $("#attachments-container input:file").filter(function() { return $(this).val() == ""; }).length;
			if (emptyFileChooserCount == 0) {
				$("#attachments-container").append($("<input type='file' name='_newAttachment' class='new-attachment form-padding'>"));
			}
		});
	});
</script>

