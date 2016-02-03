<div class="form-group ${hasErrors(bean: reflectionQuestion, field: 'question', 'error')} required">
	<label for="question" class="col-sm-2 control-label"><g:message code="kola.reflectionQuestion" /><span class="required-indicator">*</span>:</label>
	<div class="col-sm-10">
		<textarea name="name" class="form-control" id="question" rows="4" required autofocus>${reflectionQuestion?.name}</textarea>
	</div>
</div>
<div class="col-sm-offset-2 col-sm-10">
	<div class="checkbox">
		<label><g:checkBox name="autoLink" value="${reflectionQuestion?.autoLink}"/> <g:message code="kola.reflectionQuestion.autoLink" /></label>
	</div>
</div>
