<%@ page import="kola.TaskTemplate" %>
<g:set var="assetService" bean="assetService"/>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'name', 'error')} required">
	<label for="name" class="col-sm-2 control-label">
		<g:message code="taskTemplate.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<div class="col-sm-10"><g:textField name="name" class="form-control" required="" value="${taskTemplateInstance?.name}"/></div>
</div>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'description', 'error')} ">
	<label for="description" class="col-sm-2 control-label">
		<g:message code="taskTemplate.description.label" default="Beschreibung" />
	</label>
	<div class="col-sm-10"><g:textArea rows="8" name="description" class="form-control" data-provide="markdown" data-iconlibrary="fa" value="${taskTemplateInstance?.description}"/></div>
</div>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'attachments', 'error')} ">
	<label for="attachments" class="col-sm-2 control-label">
		<g:message code="taskTemplate.attachments.label" default="Anhänge" />
	</label>
	<div class="col-sm-10" id="attachments-container">
		<g:if test="${taskTemplateInstance?.attachments?.size() > 0}">
			<ul class="list-group sortable">
				<g:each var="assetInstance" in="${taskTemplateInstance?.attachments}">
					<li class="list-group-item clearfix">
						<input type="hidden" name="attachments" value="${assetInstance.id}">
						<h4 class="list-group-item-heading">
							<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank"><i class="fa fa-external-link"></i> ${assetInstance.name}</a>
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
		<div class="form-padding"><g:message code="taskTemplate.addAttachment.label" default="Neuer Anhang" />:</div> <input type="file" name="_newAttachment" class="new-attachment form-padding">
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'resources', 'error')} ">
	<label for="resources" class="col-sm-2 control-label">
		<g:message code="taskTemplate.resources.label" default="Lernressourcen" />
	</label>
	<div class="col-sm-10">
		<g:if test="${taskTemplateInstance?.resources?.size() > 0}">
			<ul class="list-group sortable">
				<g:each var="assetInstance" in="${taskTemplateInstance?.resources}">
					<li class="list-group-item clearfix">
						<input type="hidden" name="resources" value="${assetInstance.id}">
						<h4 class="list-group-item-heading">
							<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank"><i class="fa fa-external-link"></i> ${assetInstance.name}</a>
							<button type="button" class="btn btn-danger pull-right" onclick="$(this).closest('li').remove()"><i class="fa fa-times"></i></button>
						</h4>
						<p class="list-group-item-text">
							${assetInstance.description?.take(100)}
						</p>
					</li>
				</g:each>
			</ul>
		</g:if>
		<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#assetModal"><i class="fa fa-plus"></i> Lernressource hinzufügen</button>
		<%--
		<g:select name="resources" from="${kola.Asset.list()}" multiple="multiple" optionKey="id" size="5" value="${taskTemplateInstance?.resources*.id}" class="form-control"/>
		--%>
	</div>
</div>

<div class="form-group ${hasErrors(bean: taskTemplateInstance, field: 'steps', 'error')} ">
	<label for="steps" class="col-sm-2 control-label">
		<g:message code="taskTemplate.steps.label" default="Teilschritte" />
	</label>
	<div class="col-sm-10">
		<g:if test="${taskTemplateInstance?.steps?.size() > 0}">
			<ul class="list-group sortable">
				<g:each var="step" in="${taskTemplateInstance?.steps}">
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
		<g:select name="steps" from="${kola.TaskStep.list()}" multiple="multiple" optionKey="id" size="5" value="${taskTemplateInstance?.steps*.id}" class="form-control"/>
--%>		
	</div>
</div>

<script>
	$(document).ready(function() {
		$(".sortable").each(function() {
			Sortable.create(this);
		})
		$(document).on("change", ".new-attachment", function() {
			var emptyFileChooserCount = $("#attachments-container input:file").filter(function() { return $(this).val() == ""; }).length;
			if (emptyFileChooserCount == 0) {
				$("#attachments-container").append($("<input type='file' name='_newAttachment' class='new-attachment form-padding'>"));
			}
		});
	});
</script>

