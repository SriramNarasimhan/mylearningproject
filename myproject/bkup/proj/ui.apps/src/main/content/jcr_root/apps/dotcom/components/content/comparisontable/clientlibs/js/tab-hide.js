(function (document, $, ns) {
"use strict";
    $(document).on("dialog-ready", function() {
                // hide fields on load based on dropdown option selection
        showHideTabs();
         $("#ComparisonTableType").on('selected.select', function(event){
            showHideTabs();
        });
        function showHideTabs(){
				var tableType=$('#ComparisonTableType .coral-Select-select :selected').val();
        		console.log(tableType);
            if(tableType=='comparison'){
                $("#compTableProductDetails").parent('.coral-Form-fieldwrapper').css( "display", "block");
               $("input[name='./rowanswer3']").parent('.coral-Form-fieldwrapper').css( "display", "block");
                 $("input[name='./rowanswer4']").parent('.coral-Form-fieldwrapper').css( "display", "block");
                 $("input[name='./rowanswer5']").parent('.coral-Form-fieldwrapper').css( "display", "block");
                 $("input[name='./rowanswer6']").parent('.coral-Form-fieldwrapper').css( "display", "block");
                 $("input[name='./rowanswer7']").parent('.coral-Form-fieldwrapper').css( "display", "block");
                 $("input[name='./rowanswer8']").parent('.coral-Form-fieldwrapper').css( "display", "block");
            }
            if(tableType=='static'){
                $("#compTableProductDetails").parent('.coral-Form-fieldwrapper').css( "display", "none");
                $("input[name='./rowanswer3']").parent('.coral-Form-fieldwrapper').css( "display", "none");
                 $("input[name='./rowanswer4']").parent('.coral-Form-fieldwrapper').css( "display", "none");
                 $("input[name='./rowanswer5']").parent('.coral-Form-fieldwrapper').css( "display", "none");
                 $("input[name='./rowanswer6']").parent('.coral-Form-fieldwrapper').css( "display", "none");
                 $("input[name='./rowanswer7']").parent('.coral-Form-fieldwrapper').css( "display", "none");
                 $("input[name='./rowanswer8']").parent('.coral-Form-fieldwrapper').css( "display", "none");

            }
        }

         var $multifield1 = $("#compTableSection1 div.coral-Multifield"),$multifield2 = $("#compTableSection2 div.coral-Multifield"),
             $multifield3 = $("#compTableSection3 div.coral-Multifield"),$multifield4 = $("#compTableSection4 div.coral-Multifield"),
             $multifield5 = $("#compTableSection5 div.coral-Multifield"),$multifield6 = $("#compTableSection6 div.coral-Multifield");


         $multifield1.on("click", ".js-coral-Multifield-add", function(e) {
            showHideTabs();
        });
         $multifield2.on("click", ".js-coral-Multifield-add", function(e) {
            showHideTabs();
        });
         $multifield3.on("click", ".js-coral-Multifield-add", function(e) {
            showHideTabs();
        });
         $multifield4.on("click", ".js-coral-Multifield-add", function(e) {
            showHideTabs();
        });
         $multifield5.on("click", ".js-coral-Multifield-add", function(e) {
            showHideTabs();
        });
         $multifield6.on("click", ".js-coral-Multifield-add", function(e) {
            showHideTabs();
        });

    }); 
})(document, Granite.$, Granite.author);
