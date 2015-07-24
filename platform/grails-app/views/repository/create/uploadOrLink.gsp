<html>
	<head>
		<meta name="layout" content="createAsset">
	</head>
	<body>
		<div class="form-group ${hasErrors(bean: assetInstance, field: 'content', 'error')} required">
			<label for="content" class="col-sm-2 control-label">
				<g:message code="asset.content.label" default="Content" />
			</label>
			<div class="col-sm-10"><input type="file" id="content" name="content" class="form-control" /></div>
		</div>
		<div class="form-group ${hasErrors(bean: assetInstance, field: 'externalUrl', 'error')} ">
			<label for="externalUrl" class="col-sm-2 control-label">
				<g:message code="asset.externalUrl.label" default="External URL" />
			</label>
			<div class="col-sm-10"><g:textField name="externalUrl" class="form-control" value="${assetInstance?.externalUrl}"/></div>
		</div>
		<div class="buttons pull-right">
			<button name="_eventId_submit" class="next btn btn-primary"><g:message code="default.button.next.label" default="Next" /> <i class="fa fa-chevron-right"></i></button>
		</div>
	</body>
</html>
