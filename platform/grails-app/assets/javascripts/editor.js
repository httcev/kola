//= require marked.min
//= require bootstrap-markdown
//= require bootstrap-markdown.de
//= require Sortable.min
//= require picker
//= require picker.time
//= require picker.date
//= require picker.de_DE
//= require_self

function updateStepIndices() {
	$("#step-list>li").each(function(index) {
		$(".step-index", this).text(index + 1);
		var prefix = "steps[" + index + "]";
		$(":input", this).each(function() {
			var field = $(this);
			var name = field.attr("name");
			if (name) {
				var replaced = name.replace(/steps\[.*?\]/, prefix);
				field.attr("name", replaced);
			}
		})
	});
}

$(document).ready(function() {
	$('#spinner').ajaxStart(function() {
		$(this).fadeIn();
	}).ajaxStop(function() {
		$(this).fadeOut();
	});
	$(document).on("change", ".new-attachment", function() {
		var $parent = $(this).parent();
		var emptyFileChooserCount = $("input:file", $parent).filter(function() { return $(this).val() == ""; }).length;
		if (emptyFileChooserCount == 0) {
			var $newChooser = $(this.cloneNode());
			$newChooser.val(null);
			$newChooser.insertAfter(this);
			//$parent.append($newChooser);
		}
	});
	$(".sortable").each(function() {
		var sortable = Sortable.create(this, { handle:".drag-handle" });
		
		if ($(this).attr("id") == "step-list") {
			sortable.option("onUpdate", function() {
				// update all step indices according to new sort order
				updateStepIndices();
			});
		}
	});
	$("input[type=date]").pickadate({format: 'yyyy-mm-dd'});
});
