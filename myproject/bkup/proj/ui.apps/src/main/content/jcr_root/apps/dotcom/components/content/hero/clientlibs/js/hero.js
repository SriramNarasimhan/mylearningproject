(function(window, $) {

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='image-alt']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-hero');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='image-title']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-hero');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='bg-image-alt']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-hero-bg');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='bg-image-title']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-hero-bg');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='hero-link-title']",
        validate: function(el) {
            var linkURL = $(el).parents().find("[name='./linkURL']");
            if ((linkURL.val() != '' && linkURL.val() != undefined) && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});



})(window, jQuery);
