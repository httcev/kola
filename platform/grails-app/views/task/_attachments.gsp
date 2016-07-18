<%@ page import="java.util.UUID" %>

<g:set var="galleryId" value="id-${UUID.randomUUID().toString()}"/>
<g:set var="repositoryService" bean="repositoryService"/>

<g:if test="${mode == 'edit'}">
	<g:if test="${attachments?.size() > 0}">
		<ul class="list-group sortable margin">
			<g:each var="asset" in="${attachments}">
				<li class="list-group-item clearfix">
					<input type="hidden" name="${prefix}attachments" value="${asset.id}">
					<h4 class="list-group-item-heading">
						<g:if test="${attachments.size() > 1}">
							<div class="btn btn-default drag-handle" title="${message(code:'kola.dnd')}"><i class="fa fa-arrows-v fa-lg"></i></div>
						</g:if>
						<a href="${repositoryService.createEncodedLink(asset)}" target="_blank">${asset.name}</a>
						<button type="button" class="btn btn-danger pull-right" title="${message(code:'default.delete.label', args:[message(code:'kola.task.attachment')])}" onclick="$(this).closest('li').remove()"><i class="fa fa-times"></i></button>
					</h4>
					<p class="list-group-item-text">
						<label><g:message code="kola.meta.mimeType" />:</label>
						<code>${asset.mimeType}</code>
					</p>
				</li>
			</g:each>
		</ul>
	</g:if>
	<div class="form-padding">
		<label class="text-muted"><g:message code="default.add.label" args="${[message(code:'kola.task.attachment')]}" />: </label>
	</div>
	<input type="file" name="${prefix}_newAttachment" class="new-attachment form-padding">
<%--
	<input type="file" name="${prefix}_newAttachment" class="new-attachment form-padding" onchange="uploadFile(this)">
    <div class="progress" style="display:none">
        <div class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
    </div>
    <script>
        function uploadFile(fileInputElement) {
            var fd = new FormData();
            var $progressBar = $(fileInputElement).next(".progress");
            $progressBar.show();
            console.log($progressBar);
            fd.append("file", fileInputElement.files[0]);

            $.ajax({
              xhr: function() {
                var xhr = new window.XMLHttpRequest();

                xhr.upload.addEventListener("progress", function(evt) {
                  if (evt.lengthComputable) {
                    var percentComplete = evt.loaded / evt.total;
                    percentComplete = parseInt(percentComplete * 100);
                    console.log(percentComplete);
                    $(".progress-bar", $progressBar).width(percentComplete + "%");
                    if (percentComplete === 100) {
                        $progressBar.hide();
                    }
                  }
                }, false);
                return xhr;
              },
              url: "${createLink(controller:'task', action:'')}/" + fileInputElement.name + "/attach",
              type: "POST",
              data: fd,
              /*,
              contentType: "application/json",
              dataType: "json",
              */
              processData: false,
              contentType: false,
              success: function(result) {
                console.log(result);
              }
            });
        }
    </script>
--%>
</g:if>
<g:else>
	<div class="attachments clearfix form-padding">
		<g:each var="asset" in="${attachments}">
			<g:set var="url" value="${repositoryService.createEncodedLink(asset)}" />
			<a href="${url}"
				<g:if test="${asset.mimeType?.startsWith("image") || asset.mimeType?.startsWith("video")}">
					data-gallery="#${galleryId}" type="${asset.mimeType}"
				</g:if>
				<g:else>
					target="_blank"
				</g:else>
			>
			<div class="thumbnail text-center pull-left">
				<g:if test="${asset.mimeType?.startsWith("image")}">
					<img src="${url}" class="img-responsive">
				</g:if>
				<g:else>
					<g:if test="${asset.mimeType?.startsWith("video/")}">
						<i class="fa fa-film fa-3x"></i>
					</g:if>
					<g:elseif test="${asset.mimeType?.startsWith("audio/")}">
						<i class="fa fa-audio-o fa-3x"></i>
					</g:elseif>
					<g:elseif test="${asset.mimeType?.indexOf("pdf") > -1}">
						<i class="fa fa-file-pdf-o fa-3x"></i>
					</g:elseif>
					<g:else>
						<i class="fa fa-external-link fa-3x"></i>
					</g:else>
					<div class="caption">
						<p>${asset.name}</p>
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
