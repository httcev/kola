<%@ page import="de.httc.plugins.qaa.Answer" %>
<div class="rating-control text-center">
	<g:if test="${rateable instanceof Answer}">
		<g:link class="rate${rateable.rated ? ' rated' : ''}" mapping="rateAnswer" params="${[questionId:rateable.question.id, answerId:rateable.id]}" questionId="${rateable.question.id}" answerId="${rateable.id}"><i class="fa fa-caret-up fa-3x"></i></g:link>
	</g:if>
	<g:else>
		<g:link class="rate${rateable.rated ? ' rated' : ''}" action="rate" id="${rateable.id}"><i class="fa fa-caret-up fa-3x"></i></g:link>
	</g:else>
	<div class="rating">${rateable.rating}</div>
</div>
