(function($, $document) {

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='featured-content-image-alt']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-featured-contentst');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='featured-content-image-title']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-featured-contentst');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='featured-content-link-title']",
        validate: function(el) {
            var linkURL = $(el).parents().find("[name='./linkURL']");
            if ((linkURL.val() != '' && linkURL.val() != undefined) && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

}(jQuery));