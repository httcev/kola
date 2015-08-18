<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Willkommen bei KOLA</title>
	</head>
	<body>
		<div class="row">
			<div class="col-sm-9">
				<div class="panel panel-success">
					<div class="panel-heading">
						<h3 class="panel-title">
							Zu bearbeitende Aufträge:
							<a href="${createLink(controller:'task', action:'index', params:[assigned:'on'])}" class="pull-right">alle &gt;</a>
						</h3>
					</div>
					<g:if test="${assignedTasks?.size() > 0}">
						<ul class="list-group">
							<g:each var="task" in="${assignedTasks}">
								<a href="${createLink(resource:task, action:'show')}" class="list-group-item">
									<h4 class="list-group-item-heading">${task.name}</h4>
									<p class="list-group-item-text">
										${task.description?.take(100)}<br>
										<g:if test="${task.due}">
											<span class="text-danger"><g:message code="task.due.label" default="Fällig" />: <g:formatDate date="${task.due}" type="date"/></span>
										</g:if>
									</p>
								</a>
							</g:each>
						</ul>
					</g:if>
					<g:else>
						<div class="panel-body">
							<p class="text-muted">Momentan stehen keine zu bearbeitende Aufträge an.</p>
						</div>
					</g:else>
				</div>				

				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">Neueste Lernressourcen:</h3>
					</div>
					<ul class="list-group">
						<g:each var="asset" in="${latestAssets}">
							<a href="${createLink(resource:asset, action:'show')}" class="list-group-item">
								<h4 class="list-group-item-heading">${asset.name}</h4>
								<p class="list-group-item-text">
									${asset.description?.take(100)}<br>
								</p>
							</a>
						</g:each>
					</ul>
				</div>				
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
