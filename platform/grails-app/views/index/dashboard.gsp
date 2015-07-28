<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to KOLA</title>
	</head>
	<body>
		<div class="panel panel-primary pull-right">
			<div class="panel-heading">
				<h1 class="panel-title"><b>Schnellzugriff:</b></h1>
			</div>
			<div class="panel-body">
				<g:link class="btn btn-default" style="margin-bottom:10px" controller="task" action="create"><i class="fa fa-plus"></i> Neue Aufgabe</g:link><br>
				<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_TASK_TEMPLATE_CREATOR">
					<g:link class="btn btn-default" style="margin-bottom:10px" controller="taskTemplate" action="create"><i class="fa fa-plus"></i> Neuer Arbeitsprozess</g:link><br>
				</sec:ifAnyGranted>
				<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REPOSITORY_ADMIN">
					<g:link class="btn btn-default" style="margin-bottom:10px" controller="repository" action="create"><i class="fa fa-plus"></i> Neue Lernressource</g:link><br>
				</sec:ifAnyGranted>
				<sec:ifAnyGranted roles="ROLE_ADMIN">
					<g:link class="btn btn-default" style="margin-bottom:10px" controller="user" action="create"><i class="fa fa-plus"></i> Neuer Benutzer</g:link>
				</sec:ifAnyGranted>
			</div>
		</div>
	</body>
</html>
