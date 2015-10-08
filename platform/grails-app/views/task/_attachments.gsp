<%@ page import="java.util.UUID" %>

<g:set var="galleryId" value="id-${UUID.randomUUID().toString()}"/>
<g:set var="assetService" bean="assetService"/>

<div class="attachments clearfix form-padding">
	<g:each var="assetInstance" in="${attachments}">
		<g:set var="url" value="${assetService.createEncodedLink(assetInstance)}" />
		<a href="${url}"
			<g:if test="${assetInstance.mimeType?.startsWith("image") || assetInstance.mimeType?.startsWith("video")}">
				data-gallery="#${galleryId}" type="${assetInstance.mimeType}"
			</g:if>
			<g:else>
				target="_blank"
			</g:else>
		>
		<div class="thumbnail text-center pull-left">
			<g:if test="${assetInstance.mimeType?.startsWith("image")}">
				<img src="${url}" class="img-responsive">
			</g:if>
			<g:else>
				<g:if test="${assetInstance.mimeType?.startsWith("video/")}">
					<i class="fa fa-film fa-3x"></i>
				</g:if>
				<g:elseif test="${assetInstance.mimeType?.startsWith("audio/")}">
					<i class="fa fa-audio-o fa-3x"></i>
				</g:elseif>
				<g:elseif test="${assetInstance.mimeType?.indexOf("pdf") > -1}">
					<i class="fa fa-file-pdf-o fa-3x"></i>
				</g:elseif>
				<g:else>
					<i class="fa fa-external-link fa-3x"></i>
				</g:else>
				<div class="caption">
					<p>${assetInstance.name}</p>
				</div>
			</g:else>
		</div>
		</a>
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