<%@ page import="java.util.UUID" %>

<g:set var="assetService" bean="assetService"/>

<div class="attachments clearfix">
	<g:each var="assetInstance" in="${attachments}">
		<g:set var="url" value="${assetService.createEncodedLink(assetInstance)}" />
		<div class="text-center pull-left">
			<g:if test="${assetInstance.mimeType?.startsWith("image")}">
				<img src="${url}" />
			</g:if>
			<g:else>
				<a href="${url}">
					<g:if test="${assetInstance.mimeType?.startsWith("video")}">
						<i class="fa fa-film fa-lg"></i>
					</g:if>
					<g:else>
						<g:if test="${assetInstance.mimeType?.indexOf("pdf") > -1}">
							<i class="fa fa-file-pdf-o fa-lg"></i>
						</g:if>
						<g:else>
							<i class="fa fa-external-link fa-lg"></i>
						</g:else>
						<div class="caption">
							<p>${assetInstance.name}</p>
						</div>
					</g:else>
				</a>
			</g:else>
		</div>
	</g:each>
</div>