 /*
 * This code is used for following
 * Hide or show the reviewer pickers based on the skip checkboxes selection
 * AEM 6.3 user picker mandatory(Required attribute) validation is not working and added additional code to handle the functionality.
 * How to use:
 * - add the granite:id and id attributes for reviewer items to hide and show the reviewer drop downs
 */
(function(document, $) {
    "use strict";
	var complianceReviewerId1 = '#11-compliance-reviewer-userpicker';
    var complianceReviewerId2 = '#12-compliance-reviewer-userpicker';
    var complianceReviewerId3 = '#13-compliance-reviewer-userpicker';
    var complianceReviewerId4 = '#14-compliance-reviewer-userpicker';
    var complianceReviewerId5 = '#15-compliance-reviewer-userpicker';

	$(document).on("change", "#1-1-skip-review", function(e) {
        setOptionalOrRequired($(this), complianceReviewerId1);
	    hideOrShowUserPicker($(this), complianceReviewerId1);
    });	
    $(document).on("change", "#1-2-skip-review", function(e) {
        setOptionalOrRequired($(this), complianceReviewerId2);
	    hideOrShowUserPicker($(this), complianceReviewerId2);
    });
    $(document).on("change", "#1-3-skip-review", function(e) {
        setOptionalOrRequired($(this), complianceReviewerId3);
	    hideOrShowUserPicker($(this), complianceReviewerId3);
    });
    $(document).on("change", "#1-4-skip-review", function(e) {
        setOptionalOrRequired($(this), complianceReviewerId4);
	    hideOrShowUserPicker($(this), complianceReviewerId4);
    });
    $(document).on("change", "#1-5-skip-review", function(e) {
        setOptionalOrRequired($(this), complianceReviewerId5);
	    hideOrShowUserPicker($(this), complianceReviewerId5);
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