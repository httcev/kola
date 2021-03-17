<%@ page import="kola.TaskStep" %>
<%@ page import="de.httc.plugins.taxonomy.Taxonomy" %>

<g:set var="repositoryService" bean="repositoryService"/>
<g:set var="authService" bean="authService"/>

<div class="form-group ${hasErrors(bean: task, field: 'name', 'error')} required">
	<label for="name" class="col-sm-2 control-label">
		<g:message code="kola.task.title" />
		<span class="required-indicator">*</span>:
	</label>
	<div class="col-sm-10"><g:textField name="name" class="form-control" required="" value="${task?.name}" placeholder="${message(code:'kola.task.title.placeholder')}"/></div>
</div>

<div class="form-group ${hasErrors(bean: task, field: 'description', 'error')} ">
	<label for="description" class="col-sm-2 control-label">
		<g:message code="app.meta.description" />:
	</label>
	<div class="col-sm-10"><g:textArea rows="8" name="description" class="form-control" data-provide="markdown" data-iconlibrary="fa" data-language="de" data-hidden-buttons="cmdImage cmdCode cmdQuote cmdPreview" value="${task?.description}" placeholder="${message(code:'kola.task.description.placeholder')}"/></div>
</div>

<g:set var="typeTaxonomy" value="${Taxonomy.findByLabel("taskType")}" />
<g:if test="${typeTaxonomy?.termCount > 0}">
	<div class="form-group ${hasErrors(bean: task, field: 'type', 'error')} ">
		<label for="type" class="col-sm-2 control-label">
			<g:message code="de.httc.plugin.taxonomy.label.taskType" />:
		</label>
		<div class="col-sm-10">
			<g:render template="/taxonomies/termSelect" plugin="httcTaxonomy" model="${[name:"type.id", terms:typeTaxonomy.children, selectedValues:[task?.type?.id], placeholder:" "]}" />
		</div>
	</div>
</g:if>
<g:if test="${!task?.isTemplate?.toBoolean()}">
	<g:if test="${task.assignee}">
		<div class="row">
			<label class="col-sm-2 control-label">
				<g:message code="kola.task.assignee" />:
			</label>
			<div class="col-sm-10 form-padding padding-bottom">
				<g:render bean="${task.assignee.profile}" template="/profile/show" var="profile" />
			</div>
		</div>
	</g:if>
	<g:else>
		<div class="form-group ${hasErrors(bean: task, field: 'assignee', 'error')} ">
			<label for="assignee" class="col-sm-2 control-label">
				<g:message code="kola.task.assign" />:
			</label>
			<g:set var="assignableOrganisations" value="${authService.assignableOrganisationsFirstLevel}" />
			<g:if test="${assignableOrganisations}">
				<div class="col-sm-2">
					<select id="assigneeTypeChooser" name="assigneeType" class="form-control">
						<option value="person" selected><g:message code="kola.task.assign.person" /></option>
						<option value="group"><g:message code="kola.task.assign.group" /></option>
					</select>
				</div>
				<div class="col-sm-8 select-container" style="display:none">
					<g:render template="/taxonomies/termSelect" plugin="httcTaxonomy" model="${[id:"groupChooser", name:"assigneeGroup", terms:assignableOrganisations, placeholder:message(code:'kola.task.assign.group.placeholder')]}" />
				</div>
			</g:if>
			<g:else>
				<input type="hidden" name="assigneeType" value="person">
			</g:else>
			<div class="${assignableOrganisations ? 'col-sm-8' : 'col-sm-10'} select-container">
				<select id="personChooser" name="assignee.id" class="form-control-dummy" value="${task?.assignee?.id}" data-placeholder="${message(code:'kola.task.assign.person.placeholder')}">
					<option value=""></option>
					<g:each var="profile" in="${authService.assignableUserProfiles}">
						<option value="${profile.user.id}"${task?.assignee?.id == profile.user.id ? ' selected' : ''}>${profile.displayNameReverse}</option>
					</g:each>
				</select>
			</div>
		</div>
	</g:else>

	<div class="form-group ${hasErrors(bean: task, field: 'due', 'error')} ">
		<label for="due" class="col-sm-2 control-label">
			<g:message code="kola.task.due" />:
		</label>
		<div class="col-sm-10"><input type="date" id="due" class="form-control" name="due" value="${formatDate(format:'yyyy-MM-dd',date:task?.due)}" placeholder="yyyy-MM-dd"></div>
	</div>

	<div class="form-group ${hasErrors(bean: task, field: 'expires', 'error')} ">
		<label for="expires" class="col-sm-2 control-label">
			<g:message code="kola.task.expires" />:
		</label>
		<div class="col-sm-10"><input type="date" id="expires" class="form-control" name="expires" value="${formatDate(format:'yyyy-MM-dd',date:task?.expires)}" placeholder="yyyy-MM-dd"></div>
	</div>

	<g:if test="${task?.attached}">
		<div class="form-group ${hasErrors(bean: task, field: 'done', 'error')}">
			<label for="done" class="col-sm-2 control-label">
				<g:message code="kola.task.done" />:
			</label>
			<div class="col-sm-10">
				<div class="checkbox">
					<label>
						<g:checkBox name="done" value="${task?.done}" /> <g:message code="kola.task.done" />
					</label>
				</div>
			</div>
		</div>
	</g:if>
</g:if>

<div class="form-group ${hasErrors(bean: task, field: 'attachments', 'error')} ">
	<label class="col-sm-2 control-label">
		<g:message code="kola.task.attachments" />:
	</label>
	<div class="col-sm-10">
		<g:render model="${[attachments:task?.attachments, mode:'edit']}" template="attachments" />
	</div>
</div>

<div class="form-group ${hasErrors(bean: task, field: 'resources', 'error')} ">
	<label for="resources" class="col-sm-2 control-label">
		<g:message code="kola.assets" />:
	</label>
	<div class="col-sm-10">
		<ul id="asset-list" class="list-group sortable">
			<g:each var="asset" in="${task?.resources}">
				<li class="list-group-item clearfix">
					<input type="hidden" name="resources" value="${asset.id}">
					<h4 class="list-group-item-heading">
						<g:if test="${task?.resources?.size() > 1}">
							<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
						</g:if>
						<a href="${repositoryService.createEncodedLink(asset)}" target="_blank">${asset.name}</a>
						<button type="button" class="btn btn-danger pull-right" onclick="$(this).closest('li').remove()" title="${message(code:'default.button.delete.label')}"><i class="fa fa-times"></i></button>
					</h4>
					<p class="list-group-item-text">
						<httc:abbreviate>${asset.description}</httc:abbreviate>
					</p>
				</li>
			</g:each>
		</ul>
		<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#assetModal"><i class="fa fa-plus"></i> <g:message code="default.add.label" args="${[message(code:'kola.asset')]}" /></button>
	</div>
</div>

<div class="form-group ${hasErrors(bean: task, field: 'steps', 'error')} ">
	<label for="steps" class="col-sm-2 control-label">
		<g:message code="kola.task.steps" />:
	</label>
	<div class="col-sm-10">
		<ul id="step-list" class="list-group sortable">
			<g:each var="step" in="${task?.steps}" status="i">
				<g:render template="stepEditor" model="${[step:step, index:i]}" />
			</g:each>
		</ul>
		<button type="button" class="btn btn-primary" onclick="addStep()"><i class="fa fa-plus"></i> <g:message code="default.add.label" args="${[message(code:'kola.task.step')]}" /></button>
	</div>
</div>

<div class="form-group ${hasErrors(bean: task, field: 'reflectionQuestions', 'error')} ">
	<label for="reflectionQuestions" class="col-sm-2 control-label">
		<g:message code="kola.reflectionQuestions" />:
	</label>
	<div class="col-sm-10">
		<ul id="reflectionQuestion-list" class="list-group sortable">
			<g:each var="reflectionQuestion" in="${task?.reflectionQuestions}">
				<li class="list-group-item clearfix">
					<input type="hidden" name="reflectionQuestions" value="${reflectionQuestion.id}">
					<h4 class="list-group-item-heading">
						<g:if test="${task?.reflectionQuestions?.size() > 1}">
							<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
						</g:if>
						${reflectionQuestion.name}
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
		var $step = $($("#newStepTemplate").html()).appendTo($("#step-list"));
		updateStepIndices();
		$step.find("textarea").first().markdown();
		$step.find("input[type='text']").first().focus();
	}

	$(function() {
		$("#personChooser").chosen({
			no_results_text:'<g:message code="app.filter.empty" args="${[message(code: 'kola.task.assign.person', default: 'Person')]}" default="No {0} found."/>'
			, disable_search_threshold: 10
			, width:"100%"
			, allow_single_deselect: true
			, inherit_select_classes: true
		});
		$("#assigneeTypeChooser").on("change", function() {
			if ($(this).val() === "group") {
				$("#personChooser_chosen").parent(".select-container").hide();
				$("#groupChooser_chosen").parent(".select-container").show();
			}
			else {
				$("#groupChooser_chosen").parent(".select-container").hide();
				$("#personChooser_chosen").parent(".select-container").show();
			}
		});
	});
</script>
