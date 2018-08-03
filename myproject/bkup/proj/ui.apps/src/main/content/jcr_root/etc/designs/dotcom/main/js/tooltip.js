$(document).ready(function(){
	toolTipTabIndex();
	$(window).resize(function(){
        toolTipTabIndex();
    });
    function toolTipTabIndex() {
        if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
			$(".dcm-tooltip,.dcm-tooltip-right,.dcm-tooltip-left,.dcm-tooltip-top-small,.dcm-tooltip-top-large").removeAttr("tabindex");
        }
        else {
            $(".dcm-tooltip,.dcm-tooltip-right,.dcm-tooltip-left,.dcm-tooltip-top-small,.dcm-tooltip-top-large").attr("tabindex","0");
        }
	}
});