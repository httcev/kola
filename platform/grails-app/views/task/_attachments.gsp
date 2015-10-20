<%@ page import="java.util.UUID" %>

<g:set var="galleryId" value="id-${UUID.randomUUID().toString()}"/>
<g:set var="assetService" bean="assetService"/>

<g:if test="${mode == 'edit'}">
	<g:if test="${attachments?.size() > 0}">
		<ul class="list-group sortable">
			<g:each var="assetInstance" in="${attachments}">
				<li class="list-group-item clearfix">
					<input type="hidden" name="${prefix}attachments" value="${assetInstance.id}">
					<h4 class="list-group-item-heading">
						<g:if test="${attachments.size() > 1}">
							<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
						</g:if>
						<a href="${assetService.createEncodedLink(assetInstance)}" target="_blank">${assetInstance.name}</a>
						<button type="button" class="btn btn-danger pull-right" title="${message(code:'default.delete.label', args:[message(code:'kola.task.attachment')])}" onclick="$(this).closest('li').remove()"><i class="fa fa-times"></i></button>
					</h4>
					<p class="list-group-item-text">
						<label><g:message code="kola.meta.mimeType" />:</label>
						<code>${assetInstance.mimeType}</code>
					</p>
				</li>
			</g:each>
		</ul>
	</g:if>
	<div class="form-padding">
		<label class="text-muted"><g:message code="default.add.label" args="${[message(code:'kola.task.attachment')]}" />: </label>
	</div>
	<input type="file" name="${prefix}_newAttachment" class="new-attachment form-padding">
</g:if>
<g:else>
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
</g:else>