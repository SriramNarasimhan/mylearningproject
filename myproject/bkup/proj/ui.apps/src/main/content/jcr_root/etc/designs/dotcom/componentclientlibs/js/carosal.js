$(document).ready(function(){
/*************************Carousel script STARTS*************************/

// initiates settings function for author dialog
var settings = function() {
	var step = 50;
	var stepCount = stepValue;
	var slideCount = 0 + slideValue;
	var pauseCount = pauseValue;
	var autodelayCount = autodelayValue;
	var randomStartVariable = randomStartValue;
	var controlsVariable = controlsValue;
	var autoVariable = autoValue;
	var autoHoverVariable = autoHoverValue;

	/* Function for setting the values */
	var settingControls = {
		mode : modeChange,
		speed : stepCount,
		startSlide : slideCount,
		randomStart : randomStartVariable,
		controls : controlsVariable,
		auto : autoVariable,
		pause : pauseCount,
		autoHover : autoHoverVariable,
		autoDelay : autodelayCount,
		adaptiveHeight : true,
		/**
		 * *******************Hover text for pager
		 * icons***********************
		 */
		onSlideAfter : function() {
			$(".bx-pager-link").each(function() 
			{
				var paginationCount = $(this).data("slide-index") + 1;
				$(this).attr("title","Slide "+ paginationCount);
			});
		},
        onSliderLoad: function () {
            $('.bxslider .hero').eq(1).addClass('active-slide')
        },
        onSlideBefore: function (currentSlideNumber, totalSlideQty, currentSlideHtmlObject) {
            $('.active-slide').removeClass('active-slide');
            $('.bxslider .hero').eq(currentSlideHtmlObject + 1).addClass('active-slide')
    	}
	/*** ******************* Hover text for pager icons	 *************************/
	};
	return settingControls;
}

/* Change the settings for the author dialog */
var modeChange = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-mode');
var stepValue = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-speed'); 
/** there is Speed Value and * Speed Step Value */
var slideValue = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-startslide'); 
/** there is Slide Value * and Slide Step Value */
var pauseValue = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-pause');
var autodelayValue = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-autodelay');
var randomStartValue = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-randomstart');
var controlsValue = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-controls');
if (controlsValue == 'true') {
	controlsValue = true;
} else {
	controlsValue = false;
}
if (randomStartValue == 'true') {
	randomStartValue = true;
} else {
	randomStartValue = false;
}
var autoValue = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-auto');
var autoHoverValue = $('.sun-product-carousel .sun-product-carousel-slide-list').attr('data-autohover');
if (autoValue == 'true') {
	autoValue = true;
} else {
	/*
	 * if (navigator.appName == 'Microsoft Internet
	 * Explorer' || !!(navigator.userAgent.match(/Trident/) ||
	 * navigator.userAgent.match(/rv:11/)) || (typeof
	 * $.browser !== "undefined" && $.browser.msie == 1)) {
	 * $('.bxslider').bxSlider(settings()); autoValue =
	 * true; setTimeout(function() { autoValue = false; },
	 * 200); } else { autoValue = false; }
	 */
	autoValue = false;
}
if (autoHoverValue == 'true') {
	autoHoverValue = true;
} else {
	autoHoverValue = false;
}
/* Initiate the slider function */
$("ul.bxslider").append("<div class='bxslider'> </div>");
var carousel_Slides = $(".bxslider div.hero").detach();
$("ul.bxslider").removeClass("bxslider");
$(".bxslider").append(carousel_Slides);

//$('.bxslider').bxSlider(settings());
setTimeout(function(){
	$('.bxslider').one().bxSlider(settings());
	
},500);
/*
 * var suntrustslider = $('.bxslider').bxSlider(settings());
 * suntrustslider.reloadSlider();
 */

/**
 * *******************Hover text for arrow icons left and
 * right***********************
 */
$(".bx-prev").attr("title", "Previous Slide");
$(".bx-prev").attr("aria-label", "Previous Slide")
$(".bx-next").attr("title", "Next Slide");
$(".bx-next").attr("aria-label", "Next Slide");
/**
 * *******************Hover text for arrow icons left and
 * right***********************
 */
 
/**
 * ********************** Carousel script ENDS
 * *************************
 */	
 });