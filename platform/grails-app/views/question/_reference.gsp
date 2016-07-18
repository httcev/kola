<g:if test="${mode == 'edit'}">
	<g:if test="${possibleReferences?.size() > 0}">
		<div class="form-group">
			<label for="reference" class="control-label"><g:message code="kola.question.for" />:</label>
			<select id="reference" name="reference" class="form-control">
				<g:each var="ref" in="${possibleReferences}">
					<option value="${ref.id}"<g:if test="${question.reference?.id==ref.id}"> selected</g:if>>${ref.name}</option>
				</g:each>
			</select>
		</div>
	</g:if>
</g:if>
<g:else>
	<g:if test="${question.reference}">
		<div class="question-reference">
			<g:message code="kola.question.refersToTask" />
			<g:if test="${question.reference instanceof kola.TaskStep}">
				<g:link resource="${question.reference.task}" action="show">${question.reference.task.name}</g:link>,
				<g:message code="kola.task.step" /> <g:link resource="${question.reference.task}" action="show" fragment="task_${question.reference.id}">${question.reference.name}</g:link>
			</g:if>
			<g:else>
				<g:link resource="${question.reference}" action="show">${question.reference.name}</g:link>
			</g:else>
		</div>
	</g:if>
</g:else>
