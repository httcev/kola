<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Backup</title>
	</head>
	<body>
		<g:form url="[action:'restore']" method="POST" class="form-horizontal" enctype="multipart/form-data" autocomplete="off">
			<div class="form-group">
				<label class="col-sm-3 control-label">Backup herunterladen:</label>
				<div class="col-sm-9">
					<a href="${createLink(action:'export')}" class="btn btn-primary"><i class="fa fa-cloud-download"></i> Herunterladen</a>
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-3 control-label" for="file">Backup einspielen:</label>
				<div class="col-sm-9">
					<input type="file" id="file" name="file" class="form-padding" required>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-9 col-sm-offset-3">
					<button class="btn btn-danger"><i class="fa fa-cloud-upload"></i> Hochladen</button>
				</div>
			</div>
		</g:form>
	</body>
</html>
