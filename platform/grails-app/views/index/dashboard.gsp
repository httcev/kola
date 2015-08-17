<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to KOLA</title>
	</head>
	<body>
		<div class="row">
			<div class="col-sm-9">
				<h1 class="page-header">Dashboard</h1>
				Hier kommen dann wichtige Daten hin...
			</div>

			<div class="col-sm-3">
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h1 class="panel-title"><b>Anlegen:</b></h1>
					</div>
					<div class="panel-body">
						<g:link class="btn btn-default" style="margin-bottom:10px" controller="task" action="createFromTemplate"><i class="fa fa-plus"></i> Arbeitsauftrag</g:link><br>
						<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_TASK_TEMPLATE_CREATOR">
							<g:link class="btn btn-default" style="margin-bottom:10px" controller="task" action="createTemplate"><i class="fa fa-plus"></i> Arbeitsprozessbeschreibung</g:link><br>
						</sec:ifAnyGranted>
						<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REPOSITORY_ADMIN">
							<g:link class="btn btn-default" style="margin-bottom:10px" controller="asset" action="create"><i class="fa fa-plus"></i> Lernressource</g:link><br>
						</sec:ifAnyGranted>
						<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REFLECTION_QUESTION_CREATOR">
							<g:link class="btn btn-default" style="margin-bottom:10px" controller="reflectionQuestion" action="create"><i class="fa fa-plus"></i> Reflexionsaufforderung</g:link><br>
						</sec:ifAnyGranted>
						<sec:ifAnyGranted roles="ROLE_ADMIN">
							<g:link class="btn btn-default" style="margin-bottom:10px" controller="user" action="create"><i class="fa fa-plus"></i> Benutzer</g:link>
						</sec:ifAnyGranted>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
