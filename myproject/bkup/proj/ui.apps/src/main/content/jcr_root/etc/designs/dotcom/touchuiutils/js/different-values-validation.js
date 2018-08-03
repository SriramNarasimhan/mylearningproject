(function (document, $) {
    "use strict";

    var INPUT_CLASS = '.js-coral-Multifield-input, .coral-Textfield',
        DATA_VALIDATION_VALUE = "different-values-validation";

    $(document).on("dialog-ready", function (e) {
        // triggers the validation on click event on the field
        $(document).on('click', ".js-coral-Multifield-input", function (event) {
            $('[data-validation="' + DATA_VALIDATION_VALUE + '"]').each(function () {
                $(this).checkValidity();
                $(this).updateErrorUI();
            });
        });
        // triggers the validation on change value event on the field
        $(document).on('change:value', ".js-coral-Multifield-input", function (event) {
            $('[data-validation="' + DATA_VALIDATION_VALUE + '"]').each(function () {
                $(this).checkValidity();
                $(this).updateErrorUI();
            });
        });

    });



    $.validator.register({
        selector: INPUT_CLASS,
        validate: function (el) {

            var valid = true,
                values = [],
                parentEl,
                currentVal,
                groupId,
                $inputs,
                errorMsg,
                validationAttribute;

            if (el.hasClass('coral-Textfield')) {      //textfield
                parentEl = el;
                currentVal = el.val();
                groupId = $(parentEl).data('differentValuesGroup');
                $inputs = $('input[data-different-values-group="' + groupId + '"][type!="hidden"]');
                errorMsg = parentEl.data('errormsgfield');
            }

            //check for the validation attribute on the element
            validationAttribute = parentEl.data("validation");

            if (validationAttribute && validationAttribute.indexOf(DATA_VALIDATION_VALUE) >= 0 && currentVal) {
                //iterates over the list of field values to check for duplicate entries
                $inputs.each(function (idx, elem) {
                    var val = $(elem).val();
                    if (val && values.indexOf(val.toLowerCase()) !== -1) {
                        if (val === currentVal) {
                            valid = false;
                        }
                    } else {
                        values.push(val.toLowerCase());
                    }
                });
                // if the field has required check
                if (el.data('required') && el.val().length <= 0) {
                    errorMsg = "Field is required";
                    valid = false;
                }
            }
            // if the validation fails
            if (!valid) {
                return errorMsg;
            }
            $(this).updateErrorUI();
        }
    });

}(document, Granite.$));
