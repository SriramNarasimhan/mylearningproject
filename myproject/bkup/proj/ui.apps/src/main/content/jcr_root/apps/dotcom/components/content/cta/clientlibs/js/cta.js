(function (document, $, ns) {
 "use strict";
   $(document).on("click", ".cq-dialog-layouttoggle", function(e){
	  if($('body .cta-url-multifield').is(":visible")){
			var submitFlag = 0;
			$('.Mandatorytooltip').remove();
			$('.mandatory-validator').remove();
			$(".coral-Textfield").css({
						"border-color": "#d0d0d0"
				});
			$(".cq-dialog-floating .Mandatorytooltip.cta-anchor-title-multifield").css({
				"position": "absolute",
				"left":"0",
				"color":"#e14132"
			}); 
			$(".coral-Dialog--fullscreen .Mandatorytooltip.cta-anchor-title-multifield").css({
				"top":"70px",
				"color":"#e14132"
			});
			
			$(".coral-Dialog--fullscreen .cta-anchor-title-multifield .mandatory-validator").css("cssText", "top: -27px !important; right: 24px !important;");
			$(".coral-Dialog--fullscreen .cta-anchor-title-multifield").siblings(".mandatory-validator").css("cssText", "right: 24px !important;");
			$(".coral-Dialog--fullscreen .cta-anchor-title-multifield .Mandatorytooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");

			$( ".cta-url-multifield input" ).each(function( index ) { 
				var currentElement1 = $(this).parent().parent().parent().siblings().find(".cta-anchor-title-multifield");
				if($( this ).val() != "" && $(currentElement1).val() == "" && submitFlag == 0){
					$(".cq-dialog-submit").trigger( "click" );
					submitFlag = 1;
				}
			});
			
			
		} else if($('body .cta-url-simplefield').is(":visible")){
			var submitFlag = 0;
			$('.Mandatorytooltip').remove();
			$('.mandatory-validator').remove();
			$(".coral-Textfield").css({
						"border-color": "#d0d0d0"
				});
			$(".cq-dialog-floating .Mandatorytooltip.cta-anchor-title-simplefield").css({
				"position": "absolute",
				"left":"0",
				"color":"#e14132"
			}); 
			$(".coral-Dialog--fullscreen .Mandatorytooltip.cta-anchor-title-simplefield").css({
				"top":"70px",
				"color":"#e14132"
			});
			
			$(".coral-Dialog--fullscreen .cta-anchor-title-simplefield .mandatory-validator").css("cssText", "top: -27px !important; right: 24px !important;");
			$(".coral-Dialog--fullscreen .cta-anchor-title-simplefield").siblings(".mandatory-validator").css("cssText", "right: 24px !important;");
			$(".coral-Dialog--fullscreen .cta-anchor-title-simplefield .Mandatorytooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");
			
			var anchorTitleField = $(".cta-anchor-title-simplefield");
			var urlTitleField = $(".cta-url-simplefield input");
			if($(urlTitleField).val() != ""  && $(anchorTitleField).val() == ""){
				$(".cq-dialog-submit").trigger( "click" );
				submitFlag = 1;
			}
		}
	});

	$(document).on("click", ".cq-dialog-submit", function (e) {
		if($('body .cta-url-multifield').is(":visible")){
			var mandatoryCheckFlag=0;
			var anchorTitleFocusFlag=0;
			if($('.coral-Dialog--fullscreen').length == 0){
				$(".coral-Textfield").css({
						"border-color": "#d0d0d0"
				});
			} else{
				$(".coral-Textfield").css({
						"border-color": "#d0d0d0"
				});
			}
			$('.Mandatorytooltip').remove();
			$('.mandatory-validator').remove();
			
			$( ".cta-url-multifield input" ).each(function( index ) { 
				var currentElement1 = $(this).parent().parent().parent().siblings().find(".cta-anchor-title-multifield");
				if($( this ).val() != ""  && $(currentElement1).val() == ""){
					mandatoryCheckFlag = 1;
					currentElement1.css({
						"border-color": "#e14132"
					});
					if(anchorTitleFocusFlag == 0){
						$(currentElement1).focus();
						anchorTitleFocusFlag=1;
					}
					$('<coral-icon class="mandatory-validator coral-Icon coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter(currentElement1);
					$(currentElement1).next('.mandatory-validator.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "right: 22px !important;");
					var nearestAlertIcon = $(currentElement1).next('.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
					$('<span class="Mandatorytooltip alt-text" style="left:0; color:#e14132"><span>This is required field</span></span>').insertAfter(nearestAlertIcon);
					$(".cq-dialog-floating .Mandatorytooltip.alt-text").css("cssText", "left:0 !important; color:#e14132 !important");

					$(".coral-Dialog--fullscreen .Mandatorytooltip.alt-text").css("cssText", "top: 40px !important; color: #e14132");

					$(".coral-Dialog--fullscreen .cta-anchor-title-multifield").siblings(".mandatory-validator").css("cssText", "right: 24px !important;");
				}
			});
	
	
			
			$('.cta-anchor-title-multifield .coral3-Tooltip.coral3-Tooltip--error.coral3-Tooltip--arrowDown').remove();
			$('.cta-anchor-title-multifield span.tooltip').remove();
			
			if(mandatoryCheckFlag==0 && $('body .cta-anchor-title-multifield').length !=0){
				$(".coral-Textfield").css({
					"border-color": "#3c3c3c"
				});
			}
			

			if (mandatoryCheckFlag==0) {

			}else{
				e.stopPropagation();
				e.stopImmediatePropagation();
				e.preventDefault();            
			}
		}
		
		else if($('body .cta-url-simplefield').is(":visible")){
			var mandatoryCheckFlag=0;
			var anchorTitleFocusFlag=0;
			$(".coral-Textfield").css({
					"border-color": "#d0d0d0"
			});
			$('.Mandatorytooltip').remove();
			$('.mandatory-validator').remove();
			
			 
			var anchorTitleField = $(".cta-anchor-title-simplefield");
			var urlTitleField = $(".cta-url-simplefield input");
			if($(urlTitleField).val() != ""  && $(anchorTitleField).val() == ""){
				mandatoryCheckFlag = 1;
				anchorTitleField.css({
					"border-color": "#e14132"
				});
				if(anchorTitleFocusFlag == 0){
					$(anchorTitleField).focus();
					anchorTitleFocusFlag=1;
				}
				$('<coral-icon class="mandatory-validator coral-Icon coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter(anchorTitleField);
				$(anchorTitleField).next('.mandatory-validator.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "right: 22px !important;");
				var nearestAlertIcon = $(anchorTitleField).next('.coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
				$('<span class="Mandatorytooltip alt-text" style="left:0; color:#e14132"><span>This is required field</span></span>').insertAfter(nearestAlertIcon);
				$(".cq-dialog-floating .Mandatorytooltip.alt-text").css("cssText", "left:0 !important; color:#e14132 !important");

				$(".coral-Dialog--fullscreen .Mandatorytooltip.alt-text").css("cssText", "top: 40px !important; color: #e14132");

				$(".coral-Dialog--fullscreen .cta-anchor-title-simplefield").siblings(".mandatory-validator").css("cssText", "right: 24px !important;");
			}
	
	
		
			$('.cta-anchor-title-simplefield .coral3-Tooltip.coral3-Tooltip--error.coral3-Tooltip--arrowDown').remove();
			$('.cta-anchor-title-simplefield span.tooltip').remove();
			
			if(mandatoryCheckFlag==0 && $('body .cta-anchor-title-simplefield').length !=0){
				$(".coral-Textfield").css({
					"border-color": "#3c3c3c"
				});
			}
			

			if (mandatoryCheckFlag==0) {

			}else{
				e.stopPropagation();
				e.stopImmediatePropagation();
				e.preventDefault();            
			}
		}
	});
})(document, Granite.$, Granite.author);
