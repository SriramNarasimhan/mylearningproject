(function (document, $, ns) {
 "use strict";
    $(document).on("dialog-ready", function() {
    	if($('.waivedropdown').length!=0){
    		if($('.andorwaive-subheading').length<2){
    			$(".cq-dialog-submit").attr("disabled","disabled");
    		}
    	}
    });

    $(document).on("click", ".cq-dialog-submit", function (e) {
		if($('.waivedropdown').length!=0){
            if($('.andorwaive-subheading').length<2){
                e.stopPropagation();
                e.stopImmediatePropagation();
                e.preventDefault();	
                $(".cq-dialog-submit").attr("disabled","disabled");
                $(window).adaptTo("foundation-ui").alert("Invalid Input", "Please choose minimum 2 multifields");
            }
        }

    });

    $(document).on("click", ".js-coral-Multifield-add", function (e) {
        if($('.waivedropdown').length!=0){
            if($('.andorwaive-subheading').length>=2){
                $(".cq-dialog-submit").removeAttr("disabled");           
            }
            if($('.andorwaive-subheading').length<2){
                $(".cq-dialog-submit").attr("disabled","disabled");           
            }
        }

    });

})(document, Granite.$, Granite.author);