// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better
// to create separate JavaScript files as needed.
//
//= require jquery
//= require bootstrap
//= require_self

if (typeof jQuery !== 'undefined') {
	(function($) {
		$(document).ready(function() {
			$('[data-toggle="tooltip"]').tooltip({html:true});
			$('#spinner').ajaxStart(function() {
				$(this).fadeIn();
			}).ajaxStop(function() {
				$(this).fadeOut();
			});
			$("#footer-links").on("show.bs.collapse", function () {
				$("html, body").animate({ scrollTop: $(document).height() }, "slow");
			});
			$(".btn-group-rating > .btn").click(function(){
			    $(this).addClass("active").find("i.fa").addClass("fa-inverse");
				$(this).siblings().removeClass("active").find("i.fa").removeClass("fa-inverse");
				$(this).closest("form").find("input[name='rating']").val($(this).val());
			});
		});
	})(jQuery);
}
