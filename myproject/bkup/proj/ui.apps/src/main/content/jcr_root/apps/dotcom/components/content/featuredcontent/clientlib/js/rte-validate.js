(function($, $document) {
    var CORAL_RTE = ".coral-RichText",

        fieldErrorEl = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS'" +
            "data-init='quicktip' data-quicktip-type='error' />");

    foundationReg = $(window).adaptTo("foundation-registry");
    $(document).on("foundation-contentloaded", function(e) {

        $(CORAL_RTE).after("<input type=text style='display:none'/>");

        $(CORAL_RTE).on("input", function() {
            var $invisibleText = $(this).nextAll("input:text").val($(this).text().trim());

            $invisibleText.checkValidity();

        })
    });

    foundationReg.register("foundation.validation.validator", {
        selector: ".richtext-container > input:text",
        validate: function(el) {

            var $form = $(el).closest("form.foundation-form");
            var $hidden = $form.find("input[type=hidden].coral-Textfield");

            isRequired = $hidden.attr("required") === true ||
                $hidden.attr("aria-required") === "true";

            var $check =  $hidden.closest("coral-Form-field coral-Textfield");
            if (isRequired && _.isEmpty($hidden.val())) {

               return $(el).message("validation.required") || "Please fill out this field";
            }
            return null;
        },
        show: function(el, message, ctx) {

            this.clear(el);
            fieldErrorEl.clone()
                .attr("data-quicktip-content", message)
                .insertAfter($(el));
            $(el).attr("aria-invalid", "true").toggleClass("is-invalid", true);
        },
        clear: function (el) {
             var $hid = $(el).removeAttr("aria-invalid").removeClass("is-invalid").nextAll(".coral-Form-fielderror");
          $hid.tooltip("hide").remove();

        }

    });

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='featured-content-image-alt']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-featured-content');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                //$(el).nextAll(".coral-Form-fielderror").tooltip("hide").show();
                return "Please fill out this field.";
    		}
        }
	});

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='featured-content-image-title']",
        validate: function(el) {
            var imageDiv = $(el).closest('form').find('#file-upload-featured-content');
            if (imageDiv.hasClass('is-filled') && (el.value == '' || el.value == undefined)) {
                //$(el).nextAll(".coral-Form-fielderror").tooltip("hide").show();
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
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='featured-content-link1-title']",
        validate: function(el) {
            var linkURL = $(el).parents().find("[name='./url1']");
            if ((linkURL.val() != '' && linkURL.val() != undefined) && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='featured-content-link2-title']",
        validate: function(el) {
            var linkURL = $(el).parents().find("[name='./url2']");
            if ((linkURL.val() != '' && linkURL.val() != undefined) && (el.value == '' || el.value == undefined)) {
                return "Please fill out this field.";
    		}
        }
	});


    /*$(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation='featuredimagefile']",
        clear: function(el) {

            var imageFile = $("form [data-validation='featuredimagefile']");
            var imageDiv = imageFile.closest('form').find('#file-upload-featured-content');
            console.log("image removed, imageDiv"+imageDiv);
            console.log("image has file?"+imageDiv.hasClass('is-filled'));
            var altText = $("form [data-validation='featured-content-image-alt']");
            var title = $("form [data-validation='featured-content-image-title']");
            console.log("altText.val():"+altText.val());
            console.log("cond:"+altText.val());
            if (!imageDiv.hasClass('is-filled') && (altText.val() == '' || altText.val() == undefined)) {
                console.log("yyyyyyyyyyyyy");
                altText.setCustomValidity(null);
                //altText.updateErrorUI();
                altText.adaptTo("foundation-field").setInvalid(false);
                //altText.updateErrorUI();
                altText.removeAttr("aria-invalid").removeClass("is-invalid");
                altText.nextAll(".coral-Form-fielderror").tooltip("hide").hide();
                altText.nextAll(".coral-Form-fieldinfo").removeClass("u-coral-screenReaderOnly");
    		}
        }
	});*/
}(jQuery));