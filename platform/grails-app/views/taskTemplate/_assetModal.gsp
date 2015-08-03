<!-- Modal -->
<div class="modal fade" id="assetModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Lernressource hinzufügen</h4>
			</div>
			<div class="modal-body">
			<%--
				<form id="assetForm" action="${createLink(controller:'search', action:'index')}.json">
				    Book Id: <input name="q" type="text" /> <button>clicjk</button>
				</form>
				--%>
				<%--
				<g:form controller="search" action="index" method="GET" class="form-inline">
					<div class="form-group">
						<input type="search" name="q" class="form-control" value="${params.q}" placeholder="Search" autofocus>
						<input type="hidden" name="type" value="learning-resource">
						<button type="submit" class="search btn btn-default">${message(code: 'default.button.search.label', default: 'Search')}</button>
					</div>
				</g:form>
				--%>
				<div id="assetResult" class="clearfix"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	function updateModalContent(data) {
		$("#assetResult").html(data);
		var $form = $("#assetModal form");
		$form.submit(function() {
			$.ajax({
				type: "GET",
				url: $form.attr("action"),
				data: $form.serialize(),
				success: function(data) {
					updateModalContent(data);
				}
			});
			return false;
		});
		$("#assetResult .pagination a").click(function() {
			$.get($(this).attr("href"), function(data) {
				updateModalContent(data);
			});
			return false;
		});
		$("#assetResult :not(.pagination) a").attr("target", "_blank");

		var $button = $("<button type='button' class='btn btn-primary' onclick='createAssetRelation($(this).parent().attr(\"id\"))'>Use</button>");
		$("#assetResult .search-result-hit").append($button);
	}

	function createAssetRelation(id) {
		var $form = $("form").first();
		$form.append($("<input type='hidden' name='resources' value='"+id+"'>"));
		$form.attr("action", "${createLink(action:'updateRelations', id:params.id)}");
		console.log($form.attr("action"));
		$form.submit();
	}

	$(document).ready(function() {
		//updateModalContent();
		$.get("${createLink(controller:'search', action:'index')}", function(data) {
			updateModalContent(data);
		});
		$("#assetModal").on("shown.bs.modal", function () {
		    $("#assetModal input[type='search']").focus();
		});
	});
</script>