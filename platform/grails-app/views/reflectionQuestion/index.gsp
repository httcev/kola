
<%@ page import="kola.ReflectionQuestion" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'reflectionQuestion.label', default: 'Reflexionsaufforderung')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<h1 class="page-header">
			<g:message code="default.list.label" args="[entityName]" />
			<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REFLECTION_QUESTION_CREATOR">
				<g:link class="create btn btn-primary pull-right" action="create" title="${message(code: 'default.new.label', args:[entityName])}">
					<i class="fa fa-plus"></i>
				</g:link>
			</sec:ifAnyGranted>
		</h1>
		<g:if test="${flash.message}">
			<div class="message alert alert-success" role="status">${flash.message}</div>
		</g:if>
		<ul class="list-group">
			<g:each in="${reflectionQuestionInstanceList}" var="reflectionQuestionInstance">
				<li class="list-group-item clearfix">
					<a href="${createLink(resource:reflectionQuestionInstance, action:'show')}">${reflectionQuestionInstance.name?.take(100)}</a>
				</li>
			</g:each>
		</ul>
		<div class="pagination pull-right">
			<g:paginate total="${reflectionQuestionInstanceCount ?: 0}" />
		</div>
	</body>
</html>
