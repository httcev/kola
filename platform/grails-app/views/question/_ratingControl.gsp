<div class="rating-control text-center">
	<g:link class="rate${rateable.rated ? ' rated' : ''}" action="rate" id="${rateable.id}"><i class="fa fa-caret-up fa-3x"></i></g:link>
	<div class="rating">${rateable.rating}</div>
</div>
