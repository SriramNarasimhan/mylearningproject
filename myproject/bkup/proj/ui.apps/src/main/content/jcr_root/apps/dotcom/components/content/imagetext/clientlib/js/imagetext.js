(function($, $document) {

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='image-text-alt']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-image-text');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='image-text-title']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-image-text');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='imagetext-link-title']",
        validate: function(el) {
            var linkURL = $(el).parents().find("[name='./linkURL']");
            if ((linkURL.val() != '' && linkURL.val() != undefined) && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

}(jQuery));