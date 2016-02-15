
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code:'kola.task')}" />
		<g:set var="entitiesName" value="${message(code:'kola.tasks')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="app.home" default="Home" /></g:link></li>
			<li><g:link action="index">${entitiesName}</g:link></li>
			<li class="active"><g:message code="default.create.label" args="[entityName]" /></li>
		</ol>
		<g:form url="[controller:'task', action:'create']" class="form-horizontal" autocomplete="off">
			<h1 class="page-header">
				<g:message code="default.create.label" args="[entityName]" />
			</h1>
			<p class="text-danger"><b><g:message code="kola.task.chooseTemplate.prompt" /></b></p>
			<div class="well">
				<g:message code="kola.task.chooseTemplate.none" />
				<button class="choose btn btn-primary pull-right" name="template.id" value=""><i class="fa fa-check-square-o"></i> <g:message code="kola.choose" /></button>
			</div>
			<ul class="list-group">
				<g:each var="task" in="${taskList}">
					<li class="list-group-item clearfix">
						<h4 class="list-group-item-heading">
							<a href="${createLink(resource:task, action:"show")}" target="_blank">${task.name}</a>
							<button class="choose btn btn-primary pull-right" name="template.id" value="${task.id}"><i class="fa fa-check-square-o"></i> <g:message code="kola.choose" /></button>
						</h4>
						<p class="list-group-item-text">
							<kola:markdown>${task.description?.take(400)}</kola:markdown>
						</p>
					</li>
				</g:each>
			</ul>
		</g:form>
		<g:if test="${params.max < taskCount}">
			<div class="pagination pull-right">
				<g:paginate total="${taskCount ?: 0}" />
			</div>
		</g:if>
	</body>
</html>
