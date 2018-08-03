(function(document, $) {
    "use strict";

    var INPUT_CLASS = '.coral-Textfield',
        VALIDATION_VALUE = 'unique-vanityurl',
        SERVLET_URL = '/bin/wcm/duplicateVanityCheckST';

    $.validator.register({
        selector: INPUT_CLASS,
        validate: function (el) {

            var valid = true,
                parentEl,
                currentVal,
                fieldName,
                errorMsg,
                pagePath,
                validationAttribute;

            if (el.hasClass('coral-Textfield')) {      //textfield
                parentEl = el;
                currentVal = el.val();
                fieldName = el.attr('name');
                errorMsg = parentEl.data('errormsg');
            }

            //getting the current page path
            pagePath = getCurrentPagePath();

            //check for the validation attribute on the element
			   validationAttribute = parentEl.data("validation");
            if (validationAttribute && validationAttribute.indexOf(VALIDATION_VALUE) >= 0 && currentVal) {

                //Search all page except self
                // Perform AJAX request to validation servlet
                $.ajaxSetup({"async": false});
                $.getJSON(SERVLET_URL, {
                    "vanityPath": currentVal,
                    "pagePath": pagePath
                }, function (json) {

                    if (json.length > 0) {
                        valid = false;
                        var paths = json[0];
                        paths = paths.replace('/jcr:content', '');
                        if (errorMsg === undefined) {
                            errorMsg = "Already exists";
                        }
                        errorMsg = errorMsg + "  for path : " + paths;
                    }
                    if (json.error) {
                        errorMsg = json.error;
                    }
                }).fail(function () {
                    errorMsg = "Validation Request Failed!";
                });

            }
            // if error message field id not set on the field
            if (errorMsg === undefined) {
                errorMsg = "Value already exists!";
            }
            // if the validation fails
            if (!valid) {
                return errorMsg;
            }
            $(this).updateErrorUI();

        }
    });


    function getCurrentPagePath() {

        var pagePath =  window.location.pathname.replace('/apps/freestyle-cms/wcm/createitemwizard.html', '').replace('/libs/wcm/core/content/sites/properties.html', '').replace('/editor.html', '').replace('.html', '');

        if (pagePath.indexOf(".") > 0) {
            pagePath = pagePath.substring(0, pagePath.indexOf("."));
        }
        var paths = getUrlParameter("item");
        if (paths.length > 0) {
            pagePath = decodeURIComponent(paths[0]);
        }
        if (!(pagePath.indexOf("/jcr:content") > 0)) {
            pagePath = pagePath + "/jcr:content";
        }
            return pagePath;

    }

    function getUrlParameter(strParam) {
        var values = [];
        var pageURL = window.location.search.substring(1);
        var urlVariables = pageURL.split('&');
        for (var i = 0; i < urlVariables.length; i++) {
            var parameterName = urlVariables[i].split('=');
            if (parameterName[0] == strParam) {
                values.push(parameterName[1])
            }
        }
        return values;
    }


}(document, Granite.$));
