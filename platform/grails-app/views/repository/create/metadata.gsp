<html>
	<head>
		<meta name="layout" content="createAsset">
	</head>
	<body>
		<div class="form-group ${hasErrors(bean: assetInstance, field: 'name', 'error')} required">
			<label for="name" class="col-sm-2 control-label">
				<g:message code="asset.name.label" default="Name" />
				<span class="required-indicator">*</span>
			</label>
			<div class="col-sm-10"><g:textField name="name" class="form-control" required="" value="${assetInstance?.name}" autofocus=""/></div>
		</div>
		<div class="form-group ${hasErrors(bean: assetInstance, field: 'description', 'error')} required">
			<label for="description" class="col-sm-2 control-label">
				<g:message code="asset.description.label" default="Description" />
				<span class="required-indicator">*</span>
			</label>
			<div class="col-sm-10"><g:textArea name="description" class="form-control" rows="10" required="" value="${assetInstance?.description}"/></div>
		</div>
		<div class="form-group ${hasErrors(bean: assetInstance, field: 'mimeType', 'error')}">
			<div class="col-sm-2">
				<label class="pull-right"><g:message code="asset.mimeType.label" default="Mime Type" /></label>
			</div>
			<div class="col-sm-10">${assetInstance?.mimeType}</div>
		</div>
		<g:if test="${assetInstance?.anchor}">
			<div class="form-group ${hasErrors(bean: assetInstance, field: 'anchor', 'error')}">
				<div class="col-sm-2">
					<label class="pull-right"><g:message code="asset.anchor.label" default="Anchor" /></label>
				</div>
				<div class="col-sm-10">${assetInstance?.anchor}</div>
			</div>
		</g:if>
		<div class="buttons pull-right">
			<button name="_eventId_submit" class="save btn btn-success"><i class="fa fa-save"></i> <g:message code="default.button.create.label" default="Create" /></button>
		</div>
	</body>
</html>
