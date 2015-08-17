<html>
	<head>
		<meta name="layout" content="createAsset">
	</head>
	<body>
		<div class="form-group ${hasErrors(bean: cmd, field: 'name', 'error')} required">
			<label for="name" class="col-sm-2 control-label">
				<g:message code="asset.name.label" default="Name" />
				<span class="required-indicator">*</span>
			</label>
			<div class="col-sm-10"><g:textField name="name" class="form-control" required="" value="${cmd?.name}" autofocus=""/></div>
		</div>
		<div class="form-group ${hasErrors(bean: cmd, field: 'description', 'error')}">
			<label for="description" class="col-sm-2 control-label">
				<g:message code="asset.description.label" default="Description" />
			</label>
			<div class="col-sm-10"><g:textArea name="description" class="form-control" rows="10" required="" value="${cmd?.description}"/></div>
		</div>
		<div class="form-group ${hasErrors(bean: cmd, field: 'mimeType', 'error')}">
			<div class="col-sm-2">
				<label class="pull-right"><g:message code="asset.mimeType.label" default="Mime Type" /></label>
			</div>
			<div class="col-sm-10">${cmd?.mimeType}</div>
		</div>
		<g:if test="${cmd?.anchor}">
			<div class="form-group ${hasErrors(bean: cmd, field: 'anchor', 'error')}">
				<div class="col-sm-2">
					<label class="pull-right"><g:message code="asset.anchor.label" default="Anchor" /></label>
				</div>
				<div class="col-sm-10">${cmd?.anchor}</div>
			</div>
		</g:if>
		<div class="buttons pull-right">
			<button name="_eventId_submit" class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.create.label" default="Create" /></button>
		</div>
	</body>
</html>
