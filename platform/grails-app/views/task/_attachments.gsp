<%@ page import="java.util.UUID" %>

<g:set var="galleryId" value="id-${UUID.randomUUID().toString()}"/>
<g:set var="assetService" bean="assetService"/>

<div class="attachments clearfix">
	<g:each var="assetInstance" in="${attachments}">
		<g:set var="url" value="${assetService.createEncodedLink(assetInstance)}" />
		<div class="thumbnail text-center pull-left">
			<g:if test="${assetInstance.mimeType?.startsWith("image")}">
				<a href="${url}" data-gallery="#${galleryId}"><img src="${url}" class="img-responsive"></a>
			</g:if>
			<g:elseif test="${assetInstance.mimeType?.startsWith("video")}">
				<a href="${url}" type="${assetInstance.mimeType}" data-gallery><i class="fa fa-film fa-lg"></i></a>
			</g:elseif>
			<g:else>
				<a href="${url}" target="_blank"><i class="fa fa-external-link"></i> ${assetInstance.name}</a>
			</g:else>
		</div>
	</g:each>
</div>
<div id="${galleryId}" class="blueimp-gallery blueimp-gallery-controls">
    <div class="slides"></div>
    <h3 class="title"></h3>
    <a class="prev">‹</a>
    <a class="next">›</a>
    <a class="close">×</a>
    <a class="play-pause"></a>
    <ol class="indicator"></ol>
</div>