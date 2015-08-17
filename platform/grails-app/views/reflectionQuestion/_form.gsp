<%@ page import="kola.ReflectionQuestion" %>

<div class="form-group ${hasErrors(bean: reflectionQuestionInstance, field: 'question', 'error')} required">
	<label for="question" class="col-sm-2 control-label"><g:message code="reflectionQuestion.question.label" default="Aufforderung" /><span class="required-indicator">*</span>:</label>
	<div class="col-sm-10">
		<textarea name="name" class="form-control" id="question" rows="4" required>${reflectionQuestionInstance?.name}</textarea>
	</div>
</div>
