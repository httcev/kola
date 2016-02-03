
<%@ page import="kola.ReflectionQuestion" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'kola.reflectionQuestion')}" />
		<g:set var="entitiesName" value="${message(code: 'kola.reflectionQuestions')}" />
		<title>${entitiesName}</title>
	</head>
	<body>
		<h1 class="page-header clearfix">
			${entitiesName}
			<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REFLECTION_QUESTION_CREATOR">
				<g:link class="create btn btn-primary pull-right" action="create" title="${message(code: 'default.new.label', args:[entityName])}">
					<i class="fa fa-plus"></i> <g:message code="default.button.create.label" />
				</g:link>
			</sec:ifAnyGranted>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'kola.reflectionQuestion')}"/>
						<g:sortableColumn property="autoLink" title="${message(code: 'kola.reflectionQuestion.autoLink.short')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${reflectionQuestionList}" var="reflectionQuestion">
					<tr>
						<td><g:link action="show" id="${reflectionQuestion.id}">${fieldValue(bean: reflectionQuestion, field: "name")}</g:link></td>
						<td class="text-center"><i class="fa fa-fw ${reflectionQuestion.autoLink ? 'fa-check text-success' : 'fa-minus text-muted'}"></i></td>
					</tr>
				</g:each>
				</tbody>
			</table>
		</div>

		<g:if test="${params.max < reflectionQuestionCount}">
			<div class="pagination pull-right">
				<g:paginate total="${reflectionQuestionCount ?: 0}" />
			</div>
		</g:if>
	</body>
</html>
