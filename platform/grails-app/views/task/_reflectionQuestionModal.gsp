<!-- Modal -->
<div class="modal fade" id="reflectionQuestionModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="${message(code: 'kola.close')}"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel"><g:message code="default.add.label" args="${[message(code:'kola.reflectionQuestion')]}" /></h4>
			</div>
			<div class="modal-body">
				<div id="reflectionQuestionResult" class="clearfix"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal"><g:message code="kola.close" /></button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	function updateReflectionQuestionModalContent(data) {
		$("#reflectionQuestionResult").html(data);
		var $form = $("#reflectionQuestionModal form");
		$form.submit(function() {
			$.ajax({
				type: "GET",
				url: $form.attr("action"),
				data: $form.serialize(),
				success: function(data) {
					updateReflectionQuestionModalContent(data);
				}
			});
			return false;
		});
		$("#reflectionQuestionResult .pagination a").click(function() {
			$.get($(this).attr("href"), function(data) {
				updateReflectionQuestionModalContent(data);
			});
			return false;
		});
		$("#reflectionQuestionResult :not(.pagination) a").attr("target", "_blank");
		var $button = $("<button type='button' class='choose btn btn-primary pull-right' onclick='createReflectionQuestionRelation($(this).parent())'><i class='fa fa-check-square-o'></i> <g:message code='kola.choose' /></button>");
		$("#reflectionQuestionResult .search-result-hit").append($button);
	}

	function createReflectionQuestionRelation($searchResultNode) {
		var id = $searchResultNode.attr("id");
		var $link = $(".search-result-link", $searchResultNode).clone().text();
		var $li = $("<li class='list-group-item clearfix'>");
		$li.append($("<input type='hidden' name='reflectionQuestions' value='"+id+"'>"));
		var $h4 = $("<h4 class='list-group-item-heading'>");
		$li.append($h4);
		$h4.append($("<div class='btn btn-default drag-handle' title='${message(code:'kola.dnd')}'><i class='fa fa-arrows-v fa-lg'></i></div>"));
		$h4.append("\n");
		$h4.append($link);
		$h4.append("<button type='button' class='btn btn-danger pull-right' onclick=\"$(this).closest('li').remove()\"><i class='fa fa-times' title='${message(code:'default.button.delete.label')}''></i></button>");
		$("#reflectionQuestion-list").append($li);
		$("#reflectionQuestionModal").modal("hide");
	}

	$(document).ready(function() {
		$.get("${raw(createLink(controller:'search', action:'index', params:[q:'*', hideFilter:true, type:'reflectionQuestion']))}", function(data) {
			updateReflectionQuestionModalContent(data);
		});
		$("#reflectionQuestionModal").on("shown.bs.modal", function () {
		    $("#reflectionQuestionModal input[type='search']").focus();
		});
	});
</script>