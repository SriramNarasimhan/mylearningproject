(function (document, $, ns) {
 "use strict";
    $(document).on("dialog-ready", function() {
        $( "input.dynamic-list-alt-text" ).each(function( index ) { 
            $(this).attr("maxLength","100");
        });
        $( "input.dynamic-list-title-tag" ).each(function( index ) { 
            $(this).attr("maxLength","100");
        });

    });
    $(document).on("click", ".cq-dialog-layouttoggle", function(e){
        if($('body .dynamic-list-context-path').length !=0){
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
            $(".coral-Dialog--fullscreen .dynamic-list-result-limit .mandatory-validator").css("cssText", "top: -27px !important; right: 24px !important;");
            $(".coral-Dialog--fullscreen .dynamic-list-context-path .mandatory-validator").css("cssText", "top: -27px !important; right: 24px !important;");
            $(".coral-Dialog--fullscreen .dynamic-list-url-field .mandatory-validator").css("cssText", "top: -27px !important; right: 24px !important;");
            $(".coral-Dialog--fullscreen .dynamic-list-url-field .duplicate-validator").css("cssText", "top: -27px !important; right: 24px !important;");
            $(".coral-Dialog--fullscreen .dynamic-list-alt-text").siblings(".mandatory-validator").css("cssText", "right: 24px !important;");
            $(".coral-Dialog--fullscreen .dynamic-list-url-field .tooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");
           $(".coral-Dialog--fullscreen .dynamic-list-url-field .Mandatorytooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");

            if($('.coral-Dialog--fullscreen').length == 0){
                $(".coral-Textfield").css({
                        "border-color": "#d0d0d0"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#d0d0d0"
                });
                $('.dynamic-list-result-limit').find("span.coral-InputGroup-button button").css({
                    "border-color": "#d0d0d0"
                });
            } else{
                $(".coral-Textfield").css({
                        "border-color": "#d0d0d0"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#d0d0d0"
                });

                $('.dynamic-list-result-limit').find("span.coral-InputGroup-button button").css({
                    "border-color": "#d0d0d0"
                });
            }    

            if(($( ".dynamic-list-context-path input" ).val() == "" || $( ".dynamic-list-result-limit input" ).val() == "") && submitFlag == 0){
                $(".cq-dialog-submit").trigger( "click" );
                submitFlag = 1;
            }
            
            $( "input.dynamic-list-alt-text" ).each(function( index ) { 
                if($( this ).val() == "" && submitFlag == 0){
                    $(".cq-dialog-submit").trigger( "click" );
                    submitFlag = 1;
                }
            });
            
            $( ".dynamic-list-url-field input" ).each(function( index ) { 
                if($( this ).val() == "" && submitFlag == 0){
                    $(".cq-dialog-submit").trigger( "click" );
                    submitFlag = 1;
                }
            });
        }
    });

    $(document).on("click", ".cq-dialog-submit", function (e) {
        if($('body .dynamic-list-context-path').length !=0){
            var flag=0;
            var mandatoryCheckFlag=0;
            var altTextFocusFlag=0;
            var urlFieldFocusFlag=0;
            var contextPathFocusFlag=0;
            var resultLimitFocusFlag=0;
            //var titleFieldFocusFlag=0;
            /**********/
            if($('.coral-Dialog--fullscreen').length == 0){
                $(".coral-Textfield").css({
                        "border-color": "#d0d0d0"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#d0d0d0"
                });
                $('.dynamic-list-result-limit').find("span.coral-InputGroup-button button").css({
					"border-color": "#d0d0d0"
				});
            } else{
				$(".coral-Textfield").css({
                        "border-color": "#d0d0d0"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                        "border-color": "#d0d0d0"
                });

                $('.dynamic-list-result-limit').find("span.coral-InputGroup-button button").css({
                    "border-color": "#d0d0d0"
                });
            }
            $('.Mandatorytooltip').remove();
            $('.mandatory-validator').remove();

            /*dynamic - rules release*/
            var contextPathElement = $( ".dynamic-list-context-path input" );
            var nextBrowseIcon = $(contextPathElement).next('.coral-InputGroup-button');
            if($( contextPathElement ).val() == ""){
                $(contextPathElement).css({
                    "border-color": "#e14132"
                });
                $(contextPathElement).closest("span").find(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                    "border-color": "#e14132"
                });
                 if(contextPathFocusFlag == 0){
                    $(contextPathElement).focus();
                    contextPathFocusFlag=1;
                }
                $('<coral-icon class="mandatory-validator coral-Icon coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter(nextBrowseIcon);
				$('.dynamic-list-context-path .mandatory-validator.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "top: -28px !important; right: 22px !important;");
                var nearestAlertIcon = $(contextPathElement).next().next('.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
                $('<span class="Mandatorytooltip" style="position: absolute;top: 38px;left: 0;color: #e14132;"><span>This is required field</span></span>').insertAfter(nearestAlertIcon);
                mandatoryCheckFlag = 1;

                $(".coral-Dialog--fullscreen .dynamic-list-context-path .mandatory-validator").css("cssText", "top: -27px !important; right: 24px !important;");
                $(".coral-Dialog--fullscreen .dynamic-list-context-path .Mandatorytooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");
            }

            var resultLimitElement = $( ".dynamic-list-result-limit input" );
            var nextBrowseIcon = $(resultLimitElement).siblings().last('span.coral-InputGroup-button');
            if($( resultLimitElement ).val() == ""){
                $(resultLimitElement).css({
                    "border-color": "#e14132"
                });
                $('.dynamic-list-result-limit').find("span.coral-InputGroup-button button").css({
                    "border-color": "#e14132"
                });
                 if(resultLimitFocusFlag == 0){
                    $(resultLimitFocusFlag).focus();
                    resultLimitFocusFlag=1;
                }
                $('<coral-icon class="mandatory-validator coral-Icon coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter(nextBrowseIcon);
				$('.dynamic-list-result-limit .mandatory-validator.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "top: -28px !important; right: 22px !important;");
                var nearestAlertIcon = $(".dynamic-list-result-limit .coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert");
                $('<span class="Mandatorytooltip" style="position: absolute;top: 38px;left: 0;color: #e14132;"><span>This is required field</span></span>').insertAfter(nearestAlertIcon);
                mandatoryCheckFlag = 1;
            
                $(".coral-Dialog--fullscreen .dynamic-list-result-limit .mandatory-validator").css("cssText", "top: -27px !important; right: 24px !important;");
                $(".coral-Dialog--fullscreen .dynamic-list-result-limit .Mandatorytooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");
            }            
            /*dynamic - rules release*/

            $( "input.dynamic-list-alt-text" ).each(function( index ) { 
                var currentElement3 = $(this);    
                if($( this ).val() == ""){
                    $(this).css({
                        "border-color": "#e14132"
                    });
                    if(altTextFocusFlag == 0){
                        $(this).focus();
                        altTextFocusFlag=1;
                    }
                    $('<coral-icon class="mandatory-validator coral-Icon coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter(currentElement3);
					$('input.dynamic-list-alt-text').next('.mandatory-validator.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "right: 22px !important;");
                    var nearestAlertIcon = $(currentElement3).next('.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
                    <!--/*$('<span class="Mandatorytooltip alt-text" style="position: absolute;top: 785px;left: 0;color: #e14132;"><span>This is required field</span></span>').insertAfter(nearestAlertIcon);*/-->
                    $('<span class="Mandatorytooltip alt-text" style="position: absolute; left:0; color:#e14132"><span>This is required field</span></span>').insertAfter(nearestAlertIcon);
                   	mandatoryCheckFlag = 1;
                    $(".cq-dialog-floating .Mandatorytooltip.alt-text").css("cssText", "position: absolute !important; left:0 !important; color:#e14132 !important");

                    $(".coral-Dialog--fullscreen .Mandatorytooltip.alt-text").css("cssText", "top: 40px !important; color: #e14132");

                	$(".coral-Dialog--fullscreen .dynamic-list-alt-text").siblings(".mandatory-validator").css("cssText", "right: 24px !important;");
                }        
            });

    
            $( ".dynamic-list-url-field input" ).each(function( index ) { 
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
					$('.dynamic-list-url-field .mandatory-validator.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "top: -28px !important; right: 22px !important;");
                    var nearestAlertIcon = $(currentElement1).next().next('.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
                    $('<span class="Mandatorytooltip" style="position: absolute;top: 38px;left: 0;color: #e14132;"><span>This is required field</span></span>').insertAfter(nearestAlertIcon);
                    mandatoryCheckFlag = 1;

                    $(".coral-Dialog--fullscreen .dynamic-list-url-field .mandatory-validator").css("cssText", "top: -27px !important; right: 24px !important;");
                    $(".coral-Dialog--fullscreen .dynamic-list-url-field .Mandatorytooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");
                }
            });
    
    
            if(mandatoryCheckFlag == 1){
                e.stopPropagation();
                e.stopImmediatePropagation();
                e.preventDefault();
            }
            /***********/
            var $form = $(this).closest("form.foundation-form");
            $('.dynamic-list-url-field .duplicate-validator').remove();
            $('.dynamic-list-url-field .coral3-Tooltip.coral3-Tooltip--error.coral3-Tooltip--arrowDown').remove();
            $('.dynamic-list-url-field span.tooltip').remove();
            if(mandatoryCheckFlag==0 && $('body .dynamic-list-url-field').length !=0){
                $(".coral-Textfield").css({
                    "border-color": "#3c3c3c"
                });
                $(".coral-Button.coral-Button--square.js-coral-pathbrowser-button").css({
                    "border-color": "#3c3c3c"
                });
            }
            var values = $('.dynamic-list-url-field input').map(function() {
                return this.value;
            }).toArray();
            var ids = $('.dynamic-list-url-field input').map(function() {
                return $(this).attr("id");
            }).toArray();
            var hasDups = !values.every(function(v, i) {
                return values.indexOf(v) == i;
            });
            $.each(values, function( index, value ) {
                if(values[index]!=""){
                    if (values.indexOf(value) != index) {
                        $('.dynamic-list-url-field #'+ids[index]).css({
                            "border-color": "#e14132"
                        });
                        $('.dynamic-list-url-field #'+ids[index]).next().children().css({
                            "border-color": "#e14132"
                        });	
                        if(flag == 0){
                            $("#"+ids[index]).focus();
                            flag = 1;
                        }
                        var id = $('.dynamic-list-url-field #'+ids[index]).next();
                        $('<coral-icon class="coral-Icon duplicate-validator coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter(id);
						$('.dynamic-list-url-field .coral-Icon.duplicate-validator.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "top: -28px !important; right: 22px !important;");
                        //$('<coral-tooltip class="coral3-Tooltip coral3-Tooltip--error coral3-Tooltip--arrowDown" aria-hidden="true" variant="error" tabindex="-1" role="tooltip"><coral-tooltip-content>Duplicate entries not allowed</coral-tooltip-content></coral-tooltip>').insertAfter('.dynamic-list-url-field .coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
						$('.dynamic-list-url-field #'+ids[index]).parent().append('<span class="tooltip" style="position: absolute;top: 37px;left: 0;color: #e14132;"><span>Duplicate entries not allowed</span></span>');	
                        //$('<span class="tooltip" style="position: absolute;top: 37px;left: 0;color: #e14132;"><span>Duplicate entries not allowed</span></span>').insertAfter('.dynamic-list-url-field .coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
						$(".coral-Dialog--fullscreen .dynamic-list-url-field .duplicate-validator").css("cssText", "top: -27px !important; right: 24px !important;");
                        $(".coral-Dialog--fullscreen .dynamic-list-url-field .tooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");
                    }
                }
            });

            if (!hasDups && mandatoryCheckFlag==0) {

            }else{
                e.stopPropagation();
                e.stopImmediatePropagation();
                e.preventDefault();            
            }

        }
    });
})(document, Granite.$, Granite.author);