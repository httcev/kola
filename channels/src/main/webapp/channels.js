(function($) {

	$.fn.commentList = function(options) {
        return this.each(function() {
        	var settings = $.extend({}, $.fn.commentList.defaults, options);
        	settings.container = this;
            settings.widgetWrapper = $("<div/>").addClass("channel-widget").appendTo(settings.container);
            settings.commentsWrapper = $("<div/>").addClass("comment-list").appendTo(settings.widgetWrapper);

            $.get(settings.apiBase + "channel/" + settings.channelId, function(data) {
            	alert(data);
            });

            var $input = $("<input>").attr("type", "text").addClass("form-control").val(settings.value).data("val", settings.value).attr("title", settings.title).attr("placeholder", settings.placeholder).attr("maxlength", settings.maxlength).appendTo(settings.widgetWrapper);


            $input.on("change keyup", function(evt) {
            	var value = $input.val();
    			// check if value has changed
            	if ($input.data("val") != value && (evt.type != "keyup" || settings.keyPress)) {
            		$input.data("val", value);
            		settings.onChange.call(this, value);
            	}
            });	
        });
    };
    
	$.fn.commentList.defaults = {
		apiBase : "http://localhost:8080/channels/api/",
	    value : "",
	    title : "",
	    placeholder : "Neues Kommentar hier eingeben",
	    maxlength: 100,
	    keyPress : false, // fire change events on key presses? If "false" only fires when underlying input field changes its value.
	    onChange : function(value) { },
	    onFilterChange : function(value) { }
    };
}(jQuery));



$(document).ready(function() {
	var masterId = $("body").data("channel-master") || document.location.href;
	console.log(masterId);
	$("*[data-channel]").each(function(index, value) {
		var channelId = $(this).data("channel") || masterId;
		$(this).commentList({
			channelId : channelId
		});
	});
});