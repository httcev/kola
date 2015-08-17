<!-- Modal -->
<div class="modal fade" id="reflectionQuestionModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Reflexionsaufforderung hinzufügen</h4>
			</div>
			<div class="modal-body">
				<div id="reflectionQuestionResult" class="clearfix"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
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
		var $button = $("<button type='button' class='choose btn btn-primary pull-right' onclick='createReflectionQuestionRelation($(this).parent())'><i class='fa fa-check-square-o'></i> <g:message code='default.button.choose.label' default='Auswählen' /></button>");
		$("#reflectionQuestionResult .search-result-hit").append($button);
	}

	function createReflectionQuestionRelation($searchResultNode) {
		var id = $searchResultNode.attr("id");
		var $link = $(".search-result-link", $searchResultNode).clone();
		var $li = $("<li class='list-group-item clearfix'>");
		$li.append($("<input type='hidden' name='reflectionQuestions' value='"+id+"'>"));
		var $h4 = $("<h4 class='list-group-item-heading'>");
		$li.append($h4);
		$h4.append($("<div class='btn btn-default drag-handle' title='Verschieben mit Drag&amp;Drop'><i class='fa fa-arrows-v fa-lg'></i></div>"));
		$h4.append("\n");
		$h4.append($link);
		$("#reflectionQuestion-list").append($li);
		$("#reflectionQuestionModal").modal("hide");
	}

	$(document).ready(function() {
		$.get("${raw(createLink(controller:'search', action:'index', params:[hideFilter:true, type:'reflectionQuestion']))}", function(data) {
			updateReflectionQuestionModalContent(data);
		});
		$("#reflectionQuestionModal").on("shown.bs.modal", function () {
		    $("#reflectionQuestionModal input[type='search']").focus();
		});
	});
</script>