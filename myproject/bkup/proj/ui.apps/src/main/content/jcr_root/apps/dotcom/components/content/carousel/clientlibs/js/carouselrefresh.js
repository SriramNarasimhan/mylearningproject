(function($, ns) {

	$(document).on('cq-layer-activated', function(event) {
		// Reload the content frame when switching between authoring modes, so that
		// functionality based on whether the user is Editing or Previewing works
		// correctly w/o having to manually refresh the page.
		if (event.prevLayer && event.layer !== event.prevLayer) {
			if (event.prevLayer !== 'Annotate' && event.layer !== 'Annotate') {
       		ns.ContentFrame.reload();
			}
        }
	});
})(jQuery, Granite.author);