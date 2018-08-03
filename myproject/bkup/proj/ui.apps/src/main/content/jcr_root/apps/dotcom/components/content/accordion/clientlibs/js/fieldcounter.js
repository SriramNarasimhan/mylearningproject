var checkedCount;
    $(document).on("click", ".coral-Multifield .coral-Form-fieldset .innerCheck.coral-Checkbox-input", function (e) {
		checkedCount=0;
        var outerCheckbox = $(".coral-Checkbox.coral-Form-field .outerCheck.coral-Checkbox-input").is(":checked");

        if(outerCheckbox==true){

        $(".coral-Multifield .coral-Form-fieldset .innerCheck.coral-Checkbox-input").each(function () {
            if($(this).prop("checked")) 
                checkedCount++; 
        });

        if(checkedCount > 1) {
            $(this).prop("checked", false);
            $(window).adaptTo("foundation-ui").alert("Invalid Input", "Please choose 1 checkbox to be active at a time");
        }
        }
    });
    $(document).on("click", ".coral-Checkbox.coral-Form-field .outerCheck.coral-Checkbox-input", function (e) {
        if($(".coral-Checkbox.coral-Form-field .outerCheck.coral-Checkbox-input").is(":checked")==true) {
        checkedCount=0;
		$(".coral-Multifield .coral-Form-fieldset .innerCheck.coral-Checkbox-input").each(function () {
            if($(this).prop("checked")) 
                checkedCount++; 
        });

        if(checkedCount > 1) {
            $(".coral-Multifield .coral-Form-fieldset .innerCheck.coral-Checkbox-input").prop("checked", false);
        }
        }
    });