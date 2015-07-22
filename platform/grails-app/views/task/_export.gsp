<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<g:set var="assetService" bean="assetService"/>
<html>
	<head>
		<link rel="stylesheet" href="${createLink(uri:'/assets/export.css')}" type="text/css" media="all" />
	</head>
<body>
<h1>${taskInstance.name}</h1>
<p>${taskInstance.description}</p>
<g:if test="${taskInstance.steps}">
<h2>Teilschritte:</h2>
<ol>
<g:each var="step" in="${taskInstance.steps}">
<li>
	${step.name}
	<p>${step.description}</p>
</li>
</g:each>
</ol>
</g:if>

<g:if test="${taskInstance.resources}">
<h2>Lernressourcen:</h2>
<ul>
<g:each var="resource" in="${taskInstance.resources}">
<li>
	<a href="${assetService.createEncodedLink(resource)}" target="_blank">${resource.name}</a>
</li>
</g:each>
</ul>
</g:if>

<g:if test="${taskInstance.attachments}">
<h2>Anhänge:</h2>
<g:each var="attachment" in="${taskInstance.attachments}">
	<img src="data:${attachment.mimeType};base64,${attachment.content}" />
</g:each>
</g:if>

</body>
</html>