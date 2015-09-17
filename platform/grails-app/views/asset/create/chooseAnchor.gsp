<html>
	<head>
		<meta name="layout" content="createAsset">
	</head>
	<body>
		<p class="text-danger"><b><g:message code="kola.asset.anchor.choose" />:</b></p>
		<g:radioGroup name="anchor" values="${possibleAnchors}" labels="${possibleAnchors}" value="${assetInstance?.anchor}" >
			<div class="radio"><label>${it.radio}${it.label}</label></div>
		</g:radioGroup>
		<div class="buttons pull-right">
			<button name="_eventId_submit" class="next btn btn-primary"><g:message code="kola.next" /> <i class="fa fa-chevron-right"></i></button>
		</div>
	</body>
</html>
