<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="kola.welcome.to" /></title>
	</head>
	<body>
		<div class="row">
			<div class="col-sm-9">
				<g:form controller="search" method="GET" class="form-horizontal">
					<div class="form-group">
						<div class="col-xs-9 col-sm-10">
							<input type="search" name="q" class="form-control" placeholder="${message(code:'kola.search.query')}..." autofocus>
						</div>
						<div class="col-xs-3 col-sm-2">
							<button type="submit" class="search btn btn-default pull-right"><i class="fa fa-search"></i> <g:message code="kola.search" /></button>
						</div>
					</div>
				</g:form>

				<div class="panel panel-success">
					<div class="panel-heading">
						<h3 class="panel-title">
							<g:message code="kola.tasks.assigned" />:
							<a href="${createLink(controller:'task', action:'index', params:[own:'on', assigned:'on'])}" class="pull-right"><g:message code="kola.all" /> <i class="fa fa-chevron-right"></i></a>
						</h3>
					</div>
					<g:if test="${assignedTasks?.size() > 0}">
						<ul class="list-group">
							<g:each var="task" in="${assignedTasks}">
								<a href="${createLink(resource:task, action:'show')}" class="list-group-item">
									<h4 class="list-group-item-heading">${task.name}</h4>
									<div class="list-group-item-text">
										<div>
											<g:message code="kola.meta.creator" />:
											${task.creator?.profile?.displayName}
										</div>
										<g:if test="${task.due}">
											<div class="text-danger"><g:message code="kola.task.due" />: <g:formatDate date="${task.due}" type="date"/></div>
										</g:if>
									</div>
								</a>
							</g:each>
						</ul>
					</g:if>
					<g:else>
						<div class="panel-body">
							<p class="text-muted"><g:message code="kola.tasks.assigned.empty" /></p>
						</div>
					</g:else>
				</div>				

				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<g:message code="kola.asset.newest" />:
							<a href="${createLink(controller:'asset', action:'index')}" class="pull-right"><g:message code="kola.all" /> <i class="fa fa-chevron-right"></i></a>
						</h3>
					</div>
					<g:if test="${latestAssets?.size() > 0}">
						<ul class="list-group">
							<g:each var="asset" in="${latestAssets}">
								<a href="${createLink(resource:asset, action:'show')}" class="list-group-item">
									<h4 class="list-group-item-heading">${asset.name}</h4>
									<p class="list-group-item-text">
										${asset.description?.take(100)}
									</p>
								</a>
							</g:each>
						</ul>
					</g:if>
					<g:else>
						<div class="panel-body">
							<p class="text-muted"><g:message code="kola.filter.empty" args="${[message(code: 'kola.assets')]}" /></p>
						</div>
					</g:else>
				</div>				
			</div>

			<div class="col-sm-3">
				<div class="panel panel-primary dashboard-create-links-panel">
					<div class="panel-heading">
						<h1 class="panel-title"><b><g:message code="default.button.create.label" />:</b></h1>
					</div>
					<div class="panel-body">
						<g:link class="btn btn-default btn-block" controller="task" action="createFromTemplate"><i class="fa fa-plus"></i> <g:message code="kola.task" /></g:link>
						<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_TASK_TEMPLATE_CREATOR">
							<g:link class="btn btn-default btn-block" controller="task" action="createTemplate"><i class="fa fa-plus"></i> <g:message code="kola.taskTemplate" /></g:link>
						</sec:ifAnyGranted>
						<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REPOSITORY_ADMIN">
							<g:link class="btn btn-default btn-block" controller="asset" action="create"><i class="fa fa-plus"></i> <g:message code="kola.asset" /></g:link>
						</sec:ifAnyGranted>
						<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REFLECTION_QUESTION_CREATOR">
							<g:link class="btn btn-default btn-block" controller="reflectionQuestion" action="create"><i class="fa fa-plus"></i> <g:message code="kola.reflectionQuestion" /></g:link>
						</sec:ifAnyGranted>
						<sec:ifAnyGranted roles="ROLE_ADMIN">
							<g:link class="btn btn-default btn-block" controller="user" action="create"><i class="fa fa-plus"></i> <g:message code="de.httc.plugin.user.user" /></g:link>
						</sec:ifAnyGranted>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
