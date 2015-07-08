<%@ page import="kola.Asset" %>


<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="asset.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${assetInstance?.name}"/>

</div>
<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'description', 'error')} required">
	<label for="description">
		<g:message code="asset.description.label" default="Description" />
		<span class="required-indicator">*</span>
	</label>
	<g:textArea name="description" required="" value="${assetInstance?.description}"/>

</div>


<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'content', 'error')} required">
	<label for="content">
		<g:message code="asset.content.label" default="Content" />
		<span class="required-indicator">*</span>
	</label>
	<input type="file" id="content" name="content" />

</div>

<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'externalUrl', 'error')} ">
	<label for="externalUrl">
		<g:message code="asset.externalUrl.label" default="External Url" />
		
	</label>
	<g:textField name="externalUrl" value="${assetInstance?.externalUrl}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: assetInstance, field: 'anchor', 'error')} ">
	<label for="anchor">
		<g:message code="asset.anchor.label" default="Anchor" />
		
	</label>
	<g:textField name="anchor" value="${assetInstance?.anchor}"/>

</div>

