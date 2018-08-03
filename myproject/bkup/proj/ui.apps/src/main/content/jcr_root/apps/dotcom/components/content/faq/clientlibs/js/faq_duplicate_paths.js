(function($, $document) {
    "use strict";
 
    $document.on("dialog-ready", function() {

    });

    $(document).on("click", ".cq-dialog-submit", function (e) {
        var $multifield = $("#faqpaths div.coral-Multifield");
        var flag=0;

       /* $multifield.find('.faqpathvalue input').each(function() {

            var pathValue=$(this).val();

		});*/

          var faqpathvalues = $multifield.find('.faqpathvalue input').map(function() {
                return this.value;
            }).toArray();
            var faqids = $multifield.find('.faqpathvalue input').map(function() {
                return $(this).attr("id");
            }).toArray();
            var hasDups = !faqpathvalues.every(function(v, i) {
                return faqpathvalues.indexOf(v) == i;
            });


          /*  $multifield.find('.faqpathvalue input').each(function() {

            	var pathValue=$(this).val();
              var numOccurences = $.grep(faqpathvalues, function (elem) {
                    return elem === pathValue;
               }).length;

                if(numOccurences>1){
                    $(this).addClass('is-invalid');
                     $('<coral-icon class="coral-Icon duplicate-validator coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter($(this));
						$('.faqpathvalue .coral-Icon.duplicate-validator.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "top: -28px !important; right: 22px !important;");
                        //$('<coral-tooltip class="coral3-Tooltip coral3-Tooltip--error coral3-Tooltip--arrowDown" aria-hidden="true" variant="error" tabindex="-1" role="tooltip"><coral-tooltip-content>Duplicate entries not allowed</coral-tooltip-content></coral-tooltip>').insertAfter('.dynamic-list-url-field .coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
						$(this).parent().append('<span class="tooltip" style="position: absolute;top: 37px;left: 0;color: #e14132;"><span>Duplicate entries not allowed</span></span>');	
                        //$('<span class="tooltip" style="position: absolute;top: 37px;left: 0;color: #e14132;"><span>Duplicate entries not allowed</span></span>').insertAfter('.dynamic-list-url-field .coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
						$(".coral-Dialog--fullscreen .faqpathvalue .duplicate-validator").css("cssText", "top: -27px !important; right: 24px !important;");
                        $(".coral-Dialog--fullscreen .faqpathvalue .tooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");

                }

			});*/

           $.each(faqpathvalues, function( index, value ) {
                if(faqpathvalues[index]!=""){
                    if (faqpathvalues.indexOf(value) != index) {
                        $('.faqpathvalue #'+faqids[index]).css({
                            "border-color": "#e14132"
                        });
                        $('.faqpathvalue #'+faqids[index]).next().children().css({
                            "border-color": "#e14132"
                        });	
                        if(flag == 0){
                            $("#"+faqids[index]).focus();
                            flag = 1;
                        }
                        var id = $('.faqpathvalue #'+faqids[index]);
                        $('<coral-icon class="coral-Icon duplicate-validator coral-Form-fielderror coral-Icon--sizeS coral-Icon--alert" icon="alert" size="S" role="img" aria-label="alert"></coral-icon>').insertAfter(id);
						$('.faqpathvalue.coral-Icon.duplicate-validator.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert').css("cssText", "top: -28px !important; right: 22px !important;");
                        //$('<coral-tooltip class="coral3-Tooltip coral3-Tooltip--error coral3-Tooltip--arrowDown" aria-hidden="true" variant="error" tabindex="-1" role="tooltip"><coral-tooltip-content>Duplicate entries not allowed</coral-tooltip-content></coral-tooltip>').insertAfter('.dynamic-list-url-field .coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
						$('.faqpathvalue #'+faqids[index]).parent().append('<span class="tooltip" style="position: absolute;top: 37px;left: 0;color: #e14132;"><span>Duplicate entries not allowed</span></span>');	
                        //$('<span class="tooltip" style="position: absolute;top: 37px;left: 0;color: #e14132;"><span>Duplicate entries not allowed</span></span>').insertAfter('.dynamic-list-url-field .coral-Icon.coral-Form-fielderror.coral-Icon--sizeS.coral-Icon--alert');
						//$(".coral-Dialog--fullscreen .faqpathvalue .duplicate-validator").css("cssText", "top: -27px !important; right: 24px !important;");
                        $(".coral-Dialog--fullscreen .faqpathvalue .tooltip").css("cssText", "top: 37px !important; left: 0 !important; position: absolute; color: #e14132");
                    }
                }
            });

			 if(hasDups){
                e.stopPropagation();
                e.stopImmediatePropagation();
                e.preventDefault();              
        	}
    });
})($, $(document));