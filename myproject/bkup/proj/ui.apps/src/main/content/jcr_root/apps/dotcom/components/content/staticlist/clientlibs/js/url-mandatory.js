(function (document, $, ns) {
    console.log("Entered script");
 "use strict";
    $(document).on("click", ".cq-dialog-layouttoggle", function(e){
        if($('body .static-list-url-field').length !=0){
            var submitFlag = 0;
            $('.Mandatorytooltip').remove();
            $('.mandatory-validator').remove();
            $(".cq-dialog-floating .Mandatorytooltip.alt-text").css({
                "position": "absolute",
                "left":"0",
                "color":"#e14132"
            }); 
            $(".coral-Dialog--fullscreen .Mandatorytooltip.alt-text").css({
                "top":"70px",
                "color":"#e14132"
            });
            $(".coral-Dialog--fullscreen .static-list-url-field .mandatory-validator").css("cssText", "top: -28px !important; right: 20px !important");
            $(".coral-Dialog--fullscreen .static-list-url-field .tooltip").css("cssText", "top: 30px !important; left: 0 !important; position: absolute; color: #e14132");
            $(".coral-Dialog--fullscreen .static-list-url-field .Mandatorytooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");

            if($('.coral-Dialog--fullscreen').length == 0){
                $(".coral-Textfield").css({
                        "border-color": "#d0d0d0"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#d0d0d0"
                });
            } else{
                $(".coral-Textfield").css({
                        "border-color": "#d0d0d0"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#d0d0d0"
                });
            }    

            if(($( ".static-list-url-field input" ).val() == "") && submitFlag == 0){
                $(".cq-dialog-submit").trigger( "click" );
                submitFlag = 1;
            }
            
            
            $( ".static-list-url-field input" ).each(function( index ) { 
                if($( this ).val() == "" && submitFlag == 0){
                    $(".cq-dialog-submit").trigger( "click" );
                    submitFlag = 1;
                }
            });
        }
    });

    $(document).on("click", ".cq-dialog-submit", function (e) {
        if($('body .static-list-url-field').length !=0){
            var flag=0;
            var mandatoryCheckFlag=0;
            var urlFieldFocusFlag=0;
            /**********/
            if($('.coral-Dialog--fullscreen').length == 0){
                $(".coral-Textfield").css({
                        "border-color": "#d0d0d0"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#d0d0d0"
                });
            } else{
				$(".coral-Textfield").css({
                        "border-color": "#d0d0d0"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#d0d0d0"
                });
            }
            $('.Mandatorytooltip').remove();
            $('.mandatory-validator').remove();


            $( ".static-list-url-field input" ).each(function( index ) { 
                var currentElement1 = $(this);
                var nextBrowseIcon = $(this).next('.coral-InputGroup-button');
                if($( this ).val() == ""){
                    $(this).css({
                        "border-color": "#e14132"
                    });
                    $(this).closest("span").find(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#e14132"
                    });
                     if(urlFieldFocusFlag == 0){
                        $(this).focus();
                        urlFieldFocusFlag=1;
                    }
                    $('<coral-icon class="mandatory-validator coral-Icon coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter(nextBrowseIcon);
					$('.dynamic-list-url-field .mandatory-validator.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "top: -50px !important; right: 50px !important;");
                    var nearestAlertIcon = $(currentElement1).next().next('.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
                    $('<span class="Mandatorytooltip" style="position: absolute;top: 37px;left: 0;color: #e14132;"><span>This is required field</span></span>').insertAfter(nearestAlertIcon);
                    mandatoryCheckFlag = 1;

                    $(".coral-Dialog .static-list-url-field .mandatory-validator").css("cssText", "top: -28px !important; right: 20px !important"); <!-- smallscreen control-->
                    $(".coral-Dialog--fullscreen .static-list-url-field .mandatory-validator").css("cssText", "top: -28px !important; right: 20px !important"); <!-- fullscreen control-->
                    $(".coral-Dialog--fullscreen .static-list-url-field .Mandatorytooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");
                }
            });
    

            if(mandatoryCheckFlag == 1){
                e.stopPropagation();
                e.stopImmediatePropagation();
                e.preventDefault();
            }
            

        }
    });
    console.log("Exit script");
})(document, Granite.$, Granite.author);