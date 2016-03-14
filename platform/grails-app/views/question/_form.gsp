<div class="form-group">
	<label for="question-title"><g:message code="de.httc.plugin.qaa.question.title" />:</label>
	<input type="text" name="title" class="form-control" id="question-title" placeholder="${message(code:'de.httc.plugin.qaa.question.title.placeholder')}" required value="${question.title}">
</div>
<div class="form-group">
	<label for="question-text"><g:message code="de.httc.plugin.qaa.question.text" />:</label>
	<textarea name="text" id="question-text" class="form-control" rows="5" placeholder="${message(code:'de.httc.plugin.qaa.question.text.placeholder')}" required>${question.text}</textarea>
</div>
<g:render model="${[attachments:question.attachments, mode:'edit']}" template="/task/attachments" />
<div class="text-right form-padding-all"><button type="submit" class="btn btn-success"><i class="fa fa-save"></i> <g:message code="default.save.label" args="[message(code:'de.httc.plugin.qaa.question')]" /></button></div>
