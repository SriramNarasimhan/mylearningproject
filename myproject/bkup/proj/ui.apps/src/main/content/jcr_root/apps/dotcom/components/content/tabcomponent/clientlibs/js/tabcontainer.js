$(document).on("dialog-ready", function(e) {
	fixUIError();
	removeDialogError();

	$('.tab-class').on('click', '.js-coral-Multifield-remove', function() {
		fixDialogError();
	});

	$('.tab-class').on('click', '.js-coral-Multifield-add', function() {
		fixUIError();
	});

});

function fixUIError() {
	$('div.tab-class').parent().find('span.coral-Form-fielderror').css("right",
			"25px");
}

function fixDialogError() {

	var $tabNameField = $('.tab-class').find('.coral-Form-field.coral-Textfield[aria-required="true"]');

	if ($tabNameField) {
		var flg = 'true';

		$($tabNameField).each(function() {
			if ($(this).val().trim() == '') {
				flg = 'false';
			}
		});

		if (flg === 'true') {

			removeDialogError();
		}
	}
}

function removeDialogError() {
	var $dialogTabs = $('.tabcomp-container').parentsUntil('.coral-TabPanel-navigation').find('.coral-TabPanel-tab');
	var $errorIcon = $('div.tab-class').parent().find('span.coral-Form-fielderror');
	if ($dialogTabs.eq(1).hasClass('is-invalid')) {
		$dialogTabs.eq(1).removeClass('is-invalid');
	}
	$errorIcon.remove();
}