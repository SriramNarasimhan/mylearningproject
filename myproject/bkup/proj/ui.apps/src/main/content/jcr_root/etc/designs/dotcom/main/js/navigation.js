$(document).ready(function(){
	//Global header nav script starts//
	$(document).on("mouseenter",".suntrust-subMenu li.suntrust-subMenuList",function(){	
		$(".suntrust-overlayContent").addClass("hide");
		$(".suntrust-topArrow").addClass("hide");
		$(this).children().next(".suntrust-overlayContent").removeClass("hide");
		$(this).children().next(".suntrust-topArrow").removeClass("hide");
		if($(this).children().next().children().children(".suntrust-column-width").length == 0){
			$(this).children().next(".suntrust-overlayContent").addClass("hide");
			$(this).children().next(".suntrust-topArrow").addClass("hide");
		}
		var dropWidth = $(this).find('.suntrust-overlayItem').outerWidth();
		$(this).find('.suntrust-overlayContent').css({'width':dropWidth});
		var left = $(this).offset().left-90;
		if(dropWidth > 630)
		{
			$(this).children().next(".suntrust-overlayContent").css({'left':-left});
		}
	});

	//suntrust-subMenuanchor active
	
	$(document).on("mousee nter",".suntrust-subMenu li.suntrust-subMenuList.active",function(){
		$(".suntrust-overlayContent").addClass("hide");
		$(".suntrust-topArrow").addClass("hide");
		$(this).children().next(".suntrust-overlayContent").removeClass("hide");
		$(this).children().next(".suntrust-topArrow").removeClass("hide");
		$(this).children().next(".suntrust-topArrow").addClass("suntrust-topArrow-active");
		if($(this).children().next().children().children(".suntrust-column-width").length == 0){
			$(this).children().next(".suntrust-overlayContent").addClass("hide");
			$(this).children().next(".suntrust-topArrow").addClass("hide");
		}
		var dropWidth = $(this).find('.suntrust-overlayItem').outerWidth();
		$(this).find('.suntrust-overlayContent').css({'width':dropWidth});
		var left = $(this).offset().left-90;
		if(dropWidth > 630)
		{
			$(this).children().next(".suntrust-overlayContent").css({'left':-left});
		}
	});

	$(document).on("mouseleave",".suntrust-subMenu li.suntrust-subMenuList",function(){
		$(this).children().next(".suntrust-overlayContent").addClass("hide");
		$(this).children().next(".suntrust-topArrow").addClass("hide");
	});
	$(document).on("mouseleave",".suntrust-subMenu li.suntrust-subMenuList.active",function(){
		$(this).children().next(".suntrust-overlayContent").addClass("hide");
		$(this).children().next(".suntrust-topArrow").addClass("hide");
	});

	$("html").on("click",function(){
		$(".suntrust-overlayContent").addClass("hide");
		$(".suntrust-topArrow").addClass("hide");
	});

	$(document).on("click",".suntrust-overlayContent .suntrust-overlayItem ul li",function(){
		/* alert("L3 item clicked!") */
		$(".suntrust-overlayContent .suntrust-overlayItem ul li").removeClass("suntrust-selected");
		$(this).addClass("suntrust-selected");
		$(".suntrust-subMenuList").removeClass("active");
	});

	$(document).on("click",".suntrust-overlayContent .suntrust-overlayItem .suntrust-headingText",function(){
		$(".suntrust-overlayContent .suntrust-overlayItem .suntrust-headingText").removeClass("suntrust-selected");
		$(this).addClass("suntrust-selected");
		$(".suntrust-subMenuList").removeClass("active");
	});	

	$(document).on("click",".suntrust-subMenu li.suntrust-subMenuList",function(){
		$(".suntrust-subMenu li").removeClass("active");
		$(this).addClass("active");
		$(".suntrust-overlayContent").addClass("hide");
		$(".suntrust-activeList").show();		
	});

	$(document).on("click",".suntrust-levelHolder .suntrust-levelBodysection .suntrust-levelBodyItems.suntrust-levelOne li",function(){
		$(".suntrust-levelHolder .suntrust-levelBodysection .suntrust-levelBodyItems.suntrust-levelOne li").removeClass("selected");
		$(this).parent().hide();
		//$(this).parents().find(".suntrust-levelBodysection").children().eq(1).show();
		$(this).addClass("selected");
	});
	$(document).on("click",".suntrust-levelTwo .suntrust-viewBack",function(){
		$(this).parent().hide();
		$(this).parents().find(".suntrust-levelBodysection").children().eq(0).show();
	});
	$(document).on("click",".suntrust-levelHolder .suntrust-levelBodysection .suntrust-levelBodyItems.suntrust-levelTwo li",function(){
		$(".suntrust-levelHolder .suntrust-levelBodysection .suntrust-levelBodyItems.suntrust-levelTwo li").removeClass("selected");
		$(this).parent().hide();
		//$(this).parents().find(".suntrust-levelBodysection").children().eq(2).show();
		$(this).addClass("selected");
	});
	/*$(document).on("click",".suntrust-levelThree .suntrust-viewBack",function(){
		$(this).parent().hide();
		$(this).parents().find(".suntrust-levelBodysection").children().eq(1).show();
	});*/

	$(document).on("click",".suntrust-header-icon-button-container",function(){
		$(".suntrust-levelHolder").animate({left: "0px"});
	});
	$(document).on("click",".suntrust-onclose",function(){
		$(".suntrust-levelHolder").animate({left: "-320px"});
	});

	//Global header nav script Ends//

});