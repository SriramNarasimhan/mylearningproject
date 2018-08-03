 /*
 * This code is used for following
 * Hide or show the reviewer pickers based on the skip checkboxes selection
 * AEM 6.3 user picker mandatory(Required attribute) validation is not working and added additional code to handle the functionality.
 * How to use:
 * - add the granite:id and id attributes for reviewer items to hide and show the reviewer drop downs
 */
(function(document, $) {
    "use strict";
	var legalReviewerId = '#legal-reviewer-userpicker-unpub';

    $(document).on("change", "#legal-skip-review-unpub", function(e) {		
		/*setOptionalOrRequired($(this), legalReviewerId);*/  
		hideOrShowUserPicker($(this), legalReviewerId);
    });

    function hideOrShowUserPicker(el, itemTarget){
        if ($(el).attr("type") === "checkbox"){
            if ($(el).prop('checked')){
					$(itemTarget).parent().closest('div').css( "display", "none" );
                } else {
					$(itemTarget).parent().closest('div').css( "display", "block" );
            }
        }
    }
    function setOptionalOrRequired(el, itemTarget){
         if ($(el).attr("type") === "checkbox"){
            if ($(el).prop('checked')){
					$(itemTarget).removeAttr("required");                	
                } else {
					$(itemTarget).attr("required","");                  
            }
        }
    }
})(document,Granite.$);