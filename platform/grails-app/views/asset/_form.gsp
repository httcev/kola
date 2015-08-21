<%@ page import="kola.Asset" %>

<div class="form-group ${hasErrors(bean: assetInstance, field: 'name', 'error')} required">
	<label for="name" class="col-sm-2 control-label"><g:message code="kola.meta.name" /><span class="required-indicator">*</span>:</label>
	<div class="col-sm-10"><g:textField name="name" value="${assetInstance?.name}" class="form-control" required="" /></div>
</div>
<div class="form-group ${hasErrors(bean: assetInstance, field: 'description', 'error')}">
	<label for="description" class="col-sm-2 control-label"><g:message code="kola.meta.description" />:</label>
	<div class="col-sm-10"><g:textArea name="description" rows="8" value="${assetInstance?.description}" class="form-control" /></div>
</div>

