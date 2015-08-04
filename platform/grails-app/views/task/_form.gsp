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
		<%--
		<g:select name="resources" from="${kola.Asset.list()}" multiple="multiple" optionKey="id" size="5" value="${taskInstance?.resources*.id}" class="form-control"/>
		--%>
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskInstance, field: 'steps', 'error')} ">
	<label for="steps" class="col-sm-2 control-label">
		<g:message code="task.steps.label" default="Teilschritte" />:
	</label>
	<div class="col-sm-10">
		<g:if test="${taskInstance?.steps?.size() > 0}">
			<ul class="list-group sortable">
				<g:each var="step" in="${taskInstance?.steps}">
					<li class="list-group-item clearfix">
						<input type="hidden" name="steps" value="${step.id}">
						<h4 class="list-group-item-heading">
							${step.name}
							<button type="button" class="btn btn-danger pull-right" onclick="$(this).closest('li').remove()"><i class="fa fa-times"></i></button>
						</h4>
						<p class="list-group-item-text">
							${step.description?.take(100)}
						</p>
					</li>
				</g:each>
			</ul>
		</g:if>
		<g:actionSubmit class="btn btn-primary" value="Teilschritt hinzufügen" action="addStep" />
<%--	
		<g:select name="steps" from="${kola.TaskStep.list()}" multiple="multiple" optionKey="id" size="5" value="${taskInstance?.steps*.id}" class="form-control"/>
--%>		
	</div>
</div>

<script>
	$(document).ready(function() {
		$(".sortable").each(function() {
			Sortable.create(this, { handle:".drag-handle" });
		})
		$(document).on("change", ".new-attachment", function() {
			var emptyFileChooserCount = $("#attachments-container input:file").filter(function() { return $(this).val() == ""; }).length;
			if (emptyFileChooserCount == 0) {
				$("#attachments-container").append($("<input type='file' name='_newAttachment' class='new-attachment form-padding'>"));
			}
		});
	});
</script>

