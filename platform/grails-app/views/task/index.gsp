<%@ page import="de.httc.plugins.taxonomy.Taxonomy" %>
<g:set var="authService" bean="authService"/>
<g:set var="typeTaxonomy" value="${Taxonomy.findByLabel("taskType")}" />
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: params.isTemplate?.toBoolean() ? 'kola.taskTemplate' : 'kola.task')}" />
		<g:set var="entitiesName" value="${message(code: params.isTemplate?.toBoolean() ? 'kola.taskTemplates' : 'kola.tasks')}" />
		<title>${entitiesName}</title>
		<asset:stylesheet src="chosen-adapted.css"/>
		<asset:javascript src="chosen.jquery.js"/>
	</head>
	<body>
		<h1 class="page-header clearfix">
			<i class="httc-task text-muted"></i> ${entitiesName}
			<g:link class="create btn btn-primary pull-right" action="${params.isTemplate?.toBoolean() ? 'createTemplate' : 'createFromTemplate'}" title="${message(code: 'default.new.label', args:[entityName])}">
				<i class="fa fa-plus"></i> <g:message code="default.button.create.label" />
			</g:link>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<form method="get" class="well" id="filter-form">
			<input type="hidden" name="isTemplate" value="${params.isTemplate}">
			<b><g:message code="app.filter" />:</b>
			<div class="row filter">
				<g:if test="${!params.isTemplate?.toBoolean()}">
					<div class="col-md-3 margin-vertical">
						<span class="checkbox">
							<label><input name="assigned" type="checkbox"${params.assigned ? ' checked' : ''}> <g:message code="app.filter.assigned" /></label>
						</span>
					</div>
				</g:if>
				<div class="col-md-3 margin-vertical">
					<select id="createdChooser" name="createdBy" class="form-control-dummy" value="${params.createdBy}" data-placeholder="${message(code:'app.meta.createdBy')}...">
						<option value="all"${params.createdBy=="all" ? " selected" : ""}></option>
						<option value="own"${params.createdBy=="own" ? " selected" : ""}><g:message code="app.meta.createdBy" />: <g:message code="app.meta.me" /></option>
						<option value="company"${params.createdBy=="company" ? " selected" : ""}><g:message code="app.meta.createdBy" />: <g:message code="app.meta.myCompany" /></option>
					</select>
				</div>
				<g:if test="${!params.isTemplate?.toBoolean()}">
					<div class="col-md-3 margin-vertical">
						<select id="personChooser" name="assigneeId" class="form-control-dummy" value="${params.assigneeId}" data-placeholder="${message(code:'kola.task.assignee')}...">
							<option value=""></option>
							<g:each var="profile" in="${authService.assignableUserProfiles}">
								<option value="${profile.user.id}"${params.assigneeId == profile.user.id ? ' selected' : ''}>${profile.displayNameReverse}</option>
							</g:each>
						</select>
					</div>
				</g:if>
				<g:if test="${typeTaxonomy?.termCount > 0}">
					<div class="col-md-3 margin-vertical">
						<g:render template="/taxonomies/termSelect" plugin="httcTaxonomy" model="${[name:"taskType", terms:typeTaxonomy.children, selectedValues:[params.taskType], placeholder:"${message(code:'de.httc.plugin.taxonomy.label.taskType')}..."]}" />
					</div>
				</g:if>
			</div>
		</form>
		<g:if test="${taskList?.size() > 0}">
			<div class="margin-top text-muted small"><g:message code="app.search.hits.displaying" args="${[entitiesName, params.offset + 1, Math.min(params.offset + params.max, taskCount), taskCount]}" />:</div>
		</g:if>
		<g:if test="${taskList?.size() > 0}">
			<g:set var="filterParams" value="${[createdBy:params.createdBy, assigned:params.assigned, isTemplate:params.isTemplate, assigneeId:params.assigneeId, taskType:params.taskType]}" />
			<g:set var="sortParams" value="${[resetOffset:true] << filterParams}" />
			<g:form action="bulkdelete" method="POST" params="${[sort:params.sort, order:params.order, offset:params.offset] << filterParams}" id="bulkdelete-form">
				<div class="table-responsive">
					<table class="table table-striped">
						<thead>
							<tr>
								<th><input id="toggle-all-delete" type="checkbox" onclick="toggleCheckboxes(this.checked)"/></th>
								<g:sortableColumn property="name" title="${message(code: 'kola.task.title')}" params="${sortParams}" />
								<g:if test="${typeTaxonomy}">
									<g:sortableColumn property="type" title="${message(code: 'de.httc.plugin.taxonomy.label.taskType')}" params="${sortParams}" />
								</g:if>
								<g:if test="${!params.isTemplate?.toBoolean()}">
									<g:sortableColumn property="due" title="${message(code: 'kola.task.due')}" params="${sortParams}" />
									<g:sortableColumn property="done" class="text-center" title="${message(code: 'kola.task.done')}" params="${sortParams}" />
									<g:sortableColumn property="ap.lastName" title="${message(code: 'kola.task.assignee')}" params="${sortParams}" />
								</g:if>
								<g:sortableColumn property="cp.lastName" title="${message(code: 'app.meta.createdBy')}" params="${sortParams}" />
								<g:sortableColumn property="cpc.label" title="${message(code: 'de.httc.plugin.user.company')}" params="${sortParams}" />
								<g:sortableColumn property="lastUpdated" title="${message(code: 'app.meta.lastUpdated')}" params="${sortParams}" />
							</tr>
						</thead>
						<tbody>
						<g:set var="canDelete" value="${false}" />
						<g:each in="${taskList}" status="i" var="task">
							<tr>
								<td>
									<g:if test="${authService.canDelete(task)}">
										<g:set var="canDelete" value="${true}" />
										<input type="checkbox" name="taskToDelete" value="${task.id}" />
									</g:if>
								</td>
								<td><g:link action="show" id="${task.id}" params="${[isTemplate:params.isTemplate]}">${fieldValue(bean: task, field: "name")}</g:link></td>
								<g:if test="${typeTaxonomy}">
									<td>${task.type?.label}</td>
								</g:if>
								<g:if test="${!params.isTemplate?.toBoolean()}">
									<td><g:formatDate date="${task.due}" type="date"/></td>
									<td class="text-center">${task.done ? 'ja' : ''}</td>
									<td>${fieldValue(bean: task.assignee?.profile, field: "displayNameReverse")}</td>
								</g:if>
								<td>${fieldValue(bean: task.creator?.profile, field: "displayNameReverse")}</td>
								<td>${task.creator?.profile?.company?.label}</td>
								<td><g:formatDate date="${task.lastUpdated}" type="date"/></td>
							</tr>
						</g:each>
						</tbody>
					</table>
				</div>
				<g:if test="${params.max < taskCount}">
					<div class="pagination pull-right">
						<g:paginate total="${taskCount ?: 0}" params="${filterParams}" />
					</div>
				</g:if>
				<g:if test="${canDelete}">
					<button class="btn btn-default" id="bulk-delete-button"><g:message code="kola.tasks.delete.selected" /></button>
				</g:if>
			</g:form>
		</g:if>

		<g:if test="${!(taskList?.size() > 0)}">
			<div class="alert alert-danger margin"><g:message code="app.filter.empty" args="${[entitiesName]}" /></div>
		</g:if>

		<script>
			function toggleCheckboxes(selected) {
				$("input[name='taskToDelete']").prop("checked", selected).trigger("change");
			}

			function updateBulkDeleteButtonState() {
				let anySelected = false;
				$("input[name='taskToDelete']").each(function() {
					anySelected = anySelected || $(this).prop("checked");
				})
				$("#bulk-delete-button").prop("disabled", !anySelected);
			}

			$(function() {
				$("#filter-form :checkbox").click(function() {
					$(this).closest('form').submit();
				});
				$("#filter-form select").change(function() {
					$(this).closest('form').submit();
				});
				$("#personChooser").chosen({
					no_results_text:'<g:message code="app.filter.empty" args="${[message(code: 'kola.task.assign.person', default: 'Person')]}" default="No {0} found."/>'
					, disable_search_threshold: 10
					, width:"100%"
					, allow_single_deselect: true
					, inherit_select_classes: true
				});
				$("#createdChooser").chosen({
					disable_search_threshold: 10
					, width:"100%"
					, allow_single_deselect: true
					, inherit_select_classes: true
				});
				$("input[name='taskToDelete']").change(function() {
					let allSelected = true;
					$("input[name='taskToDelete']").each(function() {
						allSelected = allSelected && $(this).prop("checked");
					})
					$("#toggle-all-delete").prop("checked", allSelected);
					updateBulkDeleteButtonState();
				});
				updateBulkDeleteButtonState();
			});
		</script>
	</body>
</html>
