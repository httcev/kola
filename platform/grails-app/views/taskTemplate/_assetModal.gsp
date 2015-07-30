<!-- Modal -->
<div class="modal fade" id="assetModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Lernressource hinzufügen</h4>
			</div>
			<div class="modal-body">
				<g:formRemote name="myForm" on403="alert('forbidden!')" on404="alert('not found!')" update="updateMe" url="[controller: 'repository', action:'index']">
				    Book Id: <input name="id" type="text" /> <button>clicjk</button>
				</g:formRemote>
				<div id="updateMe">this div is updated with the result of the show call</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>