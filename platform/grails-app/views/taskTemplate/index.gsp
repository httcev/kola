
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'taskTemplate.label', default: 'Arbeitsprozessbeschreibung')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-taskTemplate" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-taskTemplate" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'taskTemplate.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="description" title="${message(code: 'taskTemplate.description.label', default: 'Description')}" />
					
						<th><g:message code="taskTemplate.creator.label" default="Creator" /></th>
					
						<g:sortableColumn property="dateCreated" title="${message(code: 'taskTemplate.dateCreated.label', default: 'Date Created')}" />
					
						<g:sortableColumn property="lastUpdated" title="${message(code: 'taskTemplate.lastUpdated.label', default: 'Last Updated')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${taskTemplateInstanceList}" status="i" var="taskTemplateInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${taskTemplateInstance.id}">${fieldValue(bean: taskTemplateInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: taskTemplateInstance, field: "description")}</td>
					
						<td>${fieldValue(bean: taskTemplateInstance, field: "creator")}</td>
					
						<td><g:formatDate date="${taskTemplateInstance.dateCreated}" /></td>
					
						<td><g:formatDate date="${taskTemplateInstance.lastUpdated}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${taskTemplateInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
