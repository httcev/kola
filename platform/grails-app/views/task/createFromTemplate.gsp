
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code:'kola.task')}" />
		<g:set var="entitiesName" value="${message(code:'kola.tasks')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<ol class="breadcrumb">
			<li><g:link uri="/"><g:message code="kola.home" default="Home" /></g:link></li>
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
				<button class="choose btn btn-primary pull-right" name="template.id" value=""><i class="fa fa-check-square-o"></i> <g:message code="default.button.choose.label" default="Auswählen" /></button>
			</div>
			<ul class="list-group">
				<g:each var="taskInstance" in="${taskInstanceList}">
					<li class="list-group-item clearfix">
						<h4 class="list-group-item-heading">
							<a href="${createLink(resource:taskInstance, action:"show")}" target="_blank">${taskInstance.name}</a>
							<button class="choose btn btn-primary pull-right" name="template.id" value="${taskInstance.id}"><i class="fa fa-check-square-o"></i> <g:message code="kola.choose" /></button>
						</h4>
						<p class="list-group-item-text">
							${taskInstance.description}
						</p>
					</li>
				</g:each>
			</ul>
		</g:form>
		<g:if test="${taskInstanceCount > 0}">
			<div class="pagination pull-right">
				<g:paginate total="${taskInstanceCount ?: 0}" />
			</div>
		</g:if>
	</body>
</html>