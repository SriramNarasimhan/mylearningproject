(function (document, $, ns) {
"use strict";
    $(document).on("dialog-ready", function() {
                // hide tabs on load based on radio option selection
                if($("#id-form-type").find("input[id='loginform']").is(":checked")){ 
                                showHideTabs("loginform","block");
                }else{                    
                                showHideTabs("freeform","none");
                }              
                // hide tabs on change of radio option selection from free form to loginform
                $("#id-form-type").find("input[id='loginform']").on("click", function(e){
                                showHideTabs("loginform","block");
                });
                // hide tabs on change of radio option selection from loginform to free form
                $("#id-form-type").find("input[id='freeform']").on("click", function(e){
                                showHideTabs("freeform","none");
                });
                function showHideTabs(formType, displayMode){
                                if(formType =="loginform"){
                                                $("a[aria-controls='id-freeformmode-tab-hide']").css( "display", "none");                                            
                                }else{
                                                $("a[aria-controls='id-freeformmode-tab-hide']").css( "display", "block");                                            
                                }              
                                $("a[aria-controls='id-serviceloginformolb-tab-hide']").css( "display", displayMode);
                    			$("a[aria-controls='id-serviceloginformocm-tab-hide']").css( "display", displayMode);
                                $("a[aria-controls='id-hiddenfields-tab-hide']").css( "display", displayMode);
                                $("a[aria-controls='id-supportolb-tab-hide']").css( "display", displayMode);
                                $("a[aria-controls='id-supportocm-tab-hide']").css( "display", displayMode);
                                $("a[aria-controls='id-otherservicesolb-tab-hide']").css( "display", displayMode);
                                $("a[aria-controls='id-otherservicesocm-tab-hide']").css( "display", displayMode);
                    			$("a[aria-controls='id-scripts-tab-hide']").css( "display", displayMode);
								$("#id-service-order").parent('.coral-Form-fieldwrapper').css( "display", displayMode);
			                    $("#olb-service-label").parent('.coral-Form-fieldwrapper').css( "display", displayMode);
			                    $("#ocm-service-label").parent('.coral-Form-fieldwrapper').css( "display", displayMode);


                }      
    }); 
})(document, Granite.$, Granite.author);
