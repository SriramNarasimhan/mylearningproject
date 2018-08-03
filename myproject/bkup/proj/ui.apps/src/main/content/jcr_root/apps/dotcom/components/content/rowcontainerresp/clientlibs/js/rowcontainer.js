(function(document, $, ns) {
    "use strict";
    $(document).on("dialog-ready", function() {
        var wrapperCheck = $('#WrapperMode').prop('checked');
        // hide fields on load based on dropdown option selection
        showHideTabs();
        $("#RowContainerLayout,#RowContainerTheme").on('selected.select', function(event) {
            showHideTabs();
        });

        function showHideTabs() {
            var layoutType = $('#RowContainerLayout .coral-Select-select :selected').val();
            var themeType = $('#RowContainerTheme .coral-Select-select :selected').val();
            hideOptions();
            if (layoutType == 'rightrail' && themeType == 'white') {
                $("#TopRightRailBorder").parents('.coral-Form-fieldwrapper').css("display", "block");
            }
            if (layoutType == 'rightrail' && themeType == 'grey') {
            	$("#RemoveTopRightRailBorder").parents('.coral-Form-fieldwrapper').css("display", "block");
            }
            if (layoutType == 'leftrail' && themeType == 'white') {
            	$("#TopLeftRailBorder").parents('.coral-Form-fieldwrapper').css("display", "block");
            }
            if (layoutType == 'leftrail' && themeType == 'grey') {
                $("#RemoveTopLeftRailBorder").parents('.coral-Form-fieldwrapper').css("display", "block");
            }
            if (layoutType == 'fullwidthrightrail') {
                $("#FullWidthTopRightRailBorder").parents('.coral-Form-fieldwrapper').css("display", "block");
            }
            if (layoutType == 'fullwidthleftrail') {
            	$("#FullWidthTopLeftRailBorder").parents('.coral-Form-fieldwrapper').css("display", "block");
            }
            if (layoutType == 'fullwidthrightrail' || layoutType == 'fullwidthleftrail') {
                $('#WrapperMode').prop('checked', false);
            	$("#WrapperMode").parents('.coral-Form-fieldwrapper').css("display", "none");
            }
            if (layoutType != 'fullwidthrightrail' && layoutType != 'fullwidthleftrail') {
				$("#WrapperMode").parents('.coral-Form-fieldwrapper').css("display", "block");
                $('#WrapperMode').prop('checked', wrapperCheck);
            }
        }

        function hideOptions() {
        	$("#TopLeftRailBorder").parents('.coral-Form-fieldwrapper').css("display", "none");
            $("#RemoveTopLeftRailBorder").parents('.coral-Form-fieldwrapper').css("display", "none");

            $("#TopRightRailBorder").parents('.coral-Form-fieldwrapper').css("display", "none");
            $("#RemoveTopRightRailBorder").parents('.coral-Form-fieldwrapper').css("display", "none");

            $("#FullWidthTopLeftRailBorder").parents('.coral-Form-fieldwrapper').css("display", "none");
            $("#FullWidthTopRightRailBorder").parents('.coral-Form-fieldwrapper').css("display", "none");
        }
    });
})(document, Granite.$, Granite.author);