(function($, $document) {
    "use strict";
 
    $document.on("dialog-ready", function() {
        var fieldErrorEl = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS' " +
            "data-init='quicktip' data-quicktip-type='error' />");
        var currentTimeMillis = new Date().getTime();
        var countValidatorId = "multifield-validator-" + parseInt(currentTimeMillis);
 
        var $countValidatorField = $("#" + countValidatorId),
            $multifield = $(".mustache_class div.coral-Multifield"),
            fieldLimit = 2,
            count = $multifield.find(".coral-Multifield-input").length;
        $.validator.register({
            selector: $countValidatorField,
            validate: validate,
            show: show,
            clear: clear
        });
        adjustMultifieldUI();
        $multifield.on("click", ".js-coral-Multifield-add", function(e) {
            console.log("Add Called");
            ++count;
            adjustMultifieldUI();
        });
 
        $multifield.on("click", ".js-coral-Multifield-remove", function(e) {
            --count;
            adjustMultifieldUI();
        });
        function adjustMultifieldUI() {
            $countValidatorField.checkValidity();
            $countValidatorField.updateErrorUI();
            var $addButton = $multifield.find(".js-coral-Multifield-add");
            if (count >= fieldLimit) {
                $addButton.attr('disabled', 'disabled');
                var arrow = $multifield.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";
                fieldErrorEl.clone()
                    .attr("data-quicktip-arrow", arrow)
                    .attr("data-quicktip-content", "Limit is 2")
                    .insertAfter($multifield);
                $('span.coral-Form-fielderror').next('span.coral-Form-fieldinfo').hide();
 
            } else {
                $addButton.removeAttr('disabled');
                clear();
            }
        }
        function validate() {
            if (count <= fieldLimit) {
                return null;
            }
            return "Limit set to " + fieldLimit;
        }
        function show($el, message) {
            this.clear($countValidatorField);
 
            var arrow = $multifield.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";
 
            fieldErrorEl.clone()
                .attr("data-quicktip-arrow", arrow)
                .attr("data-quicktip-content", message)
                .insertAfter($multifield);
        }
        function clear() {
            $('span.coral-Form-fielderror').next('span.coral-Form-fieldinfo').show();
            $multifield.nextAll(".coral-Form-fielderror").tooltip("hide").remove();
        }
    });
})($, $(document));