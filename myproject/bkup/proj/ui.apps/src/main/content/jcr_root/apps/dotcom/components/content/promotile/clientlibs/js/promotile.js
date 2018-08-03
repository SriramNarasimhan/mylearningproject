(function(window, $) {
    var SELECT_REQUIRED_VALIDATOR = "link-anchor",
        foundationReg = $(window).adaptTo("foundation-registry");
    foundationReg.register("foundation.validation.validator", {
        selector: "[data-validation='" + SELECT_REQUIRED_VALIDATOR + "']",
        validate: function(el) {
            var field = $(el);
            var anchorUrl = $(field).parents().find("[name='./linkurl']");
            var anchorUrlVal;
            if (anchorUrl || anchorUrl != '') {
                anchorUrlVal = anchorUrl.val();
            }
            var value = el.value;
            if ((anchorUrlVal!=undefined && anchorUrlVal != '') && (!value || value === '')) {
                return "Please fill out this field."
            }
        }
     });
})(window, jQuery);