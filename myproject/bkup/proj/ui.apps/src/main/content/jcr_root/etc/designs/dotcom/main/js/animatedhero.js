var suntrustFABPage = {
			init: function (settings) {
					//debugger;
			
			//Reload icons for IE8
			
			//suntrustPage.initSearch();
			//suntrustPage.initUniform();
			//suntrustPage.initDropdownCTA();
			//suntrustPage.initVideo();
		   // suntrustPage.initFAQ();
			//suntrustPage.initSlider(); 
			//US:12222
			var width = $(window).width();   
			if (width > 767) {
				//Start-US: 11234            
				if (typeof IsAnimation != 'undefined') {
					if (width < 1025) //checks if the viewport is openend in tablet.
					{
						$('.InfoGraphicbxsliderAnimation').css("display", "none");
						$('.InfoGraphicbxslider').css("display", "block");
						$('.InfoGraphicbxsliderMobile').css("display", "none");
						suntrustFABPage.InitInfoGraphicSlider();
					}
					else {
						if (IsAnimation == 'FALSE')//if Animation disabled
						{
							$('.InfoGraphicbxsliderAnimation').css("display", "none");
							$('.InfoGraphicbxslider').css("display", "block");
							$('.InfoGraphicbxsliderMobile').css("display", "none");

							suntrustFABPage.InitInfoGraphicSlider();
						}

						else {//if Animation enabled
							$('.InfoGraphicbxsliderAnimation').css("display", "block");
							$('.InfoGraphicbxslider').css("display", "none");
							$('.InfoGraphicbxsliderMobile').css("display", "none");
							CallInitialAnimation();
							suntrustFABPage.InitInfoGraphicSliderAnimation();
							setCTAFontsize();
						}
					}
				}
				//End-US:11234
			}
			else {
				suntrustFABPage.InitInfoGraphicSliderMobile();
			}                         
			},
				  InitInfoGraphicSlider: function () {   
				//Slider
				$('ul.InfoGraphicbxslider').each(function () {
					var slider = $(this);
					slider.bxSlider({
						speed: slider.data('speed'),
						nextText: '<span>Next</span>',
						prevText: '<span>Prev</span>',
						auto: slider.data('lapse') > 0,
						pause: slider.data('lapse'),
						keyboardEnabled: true,
						pager: false,
						autoHover: true,
						onSliderLoad: function () {
							
							 
							slider.find('a').attr("tabindex", "-1"); 
							slider.find('.sun-product-carousel-infographic-CTA-text0 a').first().attr("tabindex", "0");

							if (slider.getCurrentSlide() == 0)
								$('.bx-prev').css("display", "none");
							else
								$('.bx-prev').css("display", "block");
							 
						},
						onSlideAfter: function ($slideElement) {
							if (typeof (LimelightPlayerUtil) != "undefined") {
								var $parent = $slideElement.find('.video-container');
								$parent.css('width', '99.99%');
								setTimeout(function () {
									$parent.css('width', '100%');
								}, 0);

								//To take focus off the arrow keys after sliding

								//$('.component-infographiccarousel a.bx-next').blur();

								//$('.component-infographiccarousel a.bx-prev').blur();
							}                     
							slider.find('a').attr("tabindex", "-1");
							if (slider.getCurrentSlide() == 0)
								slider.find('.sun-product-carousel-infographic-CTA-text' + slider.getCurrentSlide() + ' a').first().attr("tabindex", "0");
							 //added for TAb focus issue
							else if (slider.getCurrentSlide() + 1 == slider.getSlideCount())
							{
								slider.find('.sun-product-carousel-infographic-CTA-text' + slider.getCurrentSlide() + ' a').attr("tabindex", "0");
								slider.find('.bx-clone  .sun-product-carousel-infographic-CTA-text' + slider.getCurrentSlide() + ' a').attr("tabindex", "-1");
							}
								//added for TAb focus issue
							else
								slider.find('.sun-product-carousel-infographic-CTA-text' + slider.getCurrentSlide() + ' a').attr("tabindex", "0");

							if (slider.getCurrentSlide() == 0)
								$('.bx-prev').css("display", "none");
							else
								$('.bx-prev').css("display", "block");
						}
					});

					//Start-<US:10089:Implement:FAB-Analytics_Event_Tagging>
					//Analytics Implementation for FAB
					$('a.bx-next').click(function (e) {
						var currentSlideIndex = 0;
						if (slider.getCurrentSlide() != 0)
							currentSlideIndex = slider.getCurrentSlide() - 1;
						else
							currentSlideIndex = slider.getSlideCount() - 1;

						var slidename = $("#SlideIndex" + currentSlideIndex).val();                    
						$("#SlideClicked").val(slidename);

					});
					//*****Analytics Implementation for FAB*****///  
					//End-<US:10089:Implement:FAB-Analytics_Event_Tagging>
				});
			//US:12222
				$(window).resize(function () {

					width = $(window).width();
					////US: 11234
					if ($(window).width() < 768 || ($(window).width() > 1024 && IsAnimation == 'TRUE')) {
						window.location = window.location.href;
					}

				});

		},
		//US:12222                                                                                                                                                                      
		InitInfoGraphicSliderMobile: function () {
			//MobileSlider

			$('ul.InfoGraphicbxsliderMobile').each(function () {
				var mobileSlider = $(this);            
				mobileSlider.bxSlider({
					speed: mobileSlider.data('speed'),
					nextText: '<span>Next</span>',
					prevText: '<span>Prev</span>',
					auto: mobileSlider.data('lapse') > 0,
					pause: mobileSlider.data('lapse'),
					keyboardEnabled: true,
					pager: false,
					autoHover: true,
					onSliderLoad: function () {

						if (mobileSlider.getCurrentSlide() == 0)
							$('.bx-prev').css("display", "none");
						else
							$('.bx-prev').css("display", "block");

					},
					onSlideAfter: function ($slideElement) {

						if (typeof (LimelightPlayerUtil) != "undefined") {
							var $parent = $slideElement.find('.video-container');
							$parent.css('width', '99.99%');
							setTimeout(function () {
								$parent.css('width', '100%');
							}, 0);
						}

						if (mobileSlider.getCurrentSlide() == 0)
							$('.bx-prev').css("display", "none");
						else
							$('.bx-prev').css("display", "block");
					}
				});

				//Analytics Implementation for FAB

				$('a.bx-next').click(function (e) {
					var currentMobileSlideIndex = 0;
					if (mobileSlider.getCurrentSlide() != 0)
						currentMobileSlideIndex = mobileSlider.getCurrentSlide() - 1;
					else
						currentMobileSlideIndex = mobileSlider.getSlideCount() - 1;

					var mobileslidename = $("#MobileSlideIndex" + currentMobileSlideIndex).val();
					//Need to use the same hidden field as used for desktop-infographic
					$("#SlideClicked").val(mobileslidename);

				});
				//*****Analytics Implementation for FAB*****///

			});
			$(window).resize(function () {
				if (($(window).width() > 767) ) {
						window.location = window.location.href;
					}           
			});

		},
		//Start-US: 11234
		InitInfoGraphicSliderAnimation : function () { 
			// Set looping variable of off if Firefox
			if (uAgent.indexOf('Firefox') > -1) {
				turnLoopOn = false;
				lastCaretOff = true;
			} else {
				turnLoopOn = true;
				lastCaretOff = false;
			}
			 
			var sldr = $('.InfoGraphicbxsliderAnimation');
			sldr.bxSlider({
			slideWidth: 800, 
			speed: sldr.data('speed'),
			nextText: '<span>Next</span>',
			prevText: '<span>Prev</span>',
			auto: sldr.data('lapse') > 0,
			pause: sldr.data('lapse'),
			keyboardEnabled: true,
			pager: false,
			autoHover: true,
			preloadImages :'all',
			onSliderLoad: function () {
				
				if (sldr.getCurrentSlide() == 0) {
					$('.bx-prev').css("display", "none");

					//set the tabindex for anchor tags inside the divs for subsequent animation slides as well
					$('#stFabBBcta a').attr("tabindex", "-1");
					$('#stFabGScta a').attr("tabindex", "-1");
					$('#stFabMTcta a').attr("tabindex", "-1");
					$('#stFabTBcta a').attr("tabindex", "-1");
					$('#stFabFAQcta a').attr("tabindex", "-1");
					$('#stFabUFcta a').attr("tabindex", "-1");
					$('#stFabGcta a').attr("tabindex", "-1");
				}
			else
				$('.bx-prev').css("display", "block");
		},
		onSlideAfter: function () {
				setTimeout(setCTAFontsize, 5);
			var cameFromSlide = sldr.getCurrentSlide();
			if (cameFromSlide == 1) {
				if (gsFired == false) {
				setTimeout(function () {
					gettingStartedAnimation();
				}, 1000);
				gsFired = true;
				}
				//set the correspdoning div to 0
				$('#stFabGScta a').attr("tabindex", "0");           
				//set the other slidde divs to -1
				$('#stFabMTcta a').attr("tabindex", "-1");
				$('#stFabBBcta a').attr("tabindex", "-1");
				$('#stFabTBcta a').attr("tabindex", "-1");
				$('#stFabFAQcta a').attr("tabindex", "-1");
				$('#stFabUFcta a').attr("tabindex", "-1");
				$('#stFabGcta a').attr("tabindex", "-1");
			} else if (cameFromSlide == 2) {
				if (bbFired == false) {
				setTimeout(function () {               
					bankingBasicsAnimation();
				}, 1000);
				bbFired = true;
				}
			   
				//set the correspdoning div to 0
				$('#stFabBBcta a').attr("tabindex", "0");
				//set the other slidde divs to -1
				$('#stFabMTcta a').attr("tabindex", "-1");
				$('#stFabGScta a').attr("tabindex", "-1");
				$('#stFabTBcta a').attr("tabindex", "-1");
				$('#stFabFAQcta a').attr("tabindex", "-1");
				$('#stFabUFcta a').attr("tabindex", "-1");
				$('#stFabGcta a').attr("tabindex", "-1");
			} else if (cameFromSlide == 3) {
				if (mtFired == false) {
					setTimeout(function () {
						makingTransactionsAnimation();
					}, 1000);
					mtFired = true;
				}

				//set the correspdoning div to 0
				$('#stFabMTcta a').attr("tabindex", "0");
				//set the other slidde divs to -1
				$('#stFabGScta a').attr("tabindex", "-1");
				$('#stFabBBcta a').attr("tabindex", "-1");
				$('#stFabTBcta a').attr("tabindex", "-1");
				$('#stFabFAQcta a').attr("tabindex", "-1");
				$('#stFabUFcta a').attr("tabindex", "-1");
				$('#stFabGcta a').attr("tabindex", "-1");
			} else if (cameFromSlide == 4) {
				// need to swap the bg image or the second MT slide here
				if (tbFired == false) {
					setTimeout(function () {
						trackBalancesAnimation();
					}, 1000);
					tbFired = true;
				}

				//set the correspdoning div to 0
				$('#stFabTBcta a').attr("tabindex", "0");
				//set the other slidde divs to -1
				$('#stFabMTcta a').attr("tabindex", "-1");
				$('#stFabGScta a').attr("tabindex", "-1");
				$('#stFabBBcta a').attr("tabindex", "-1");
				$('#stFabFAQcta a').attr("tabindex", "-1");
				$('#stFabUFcta a').attr("tabindex", "-1");
				$('#stFabGcta a').attr("tabindex", "-1");

			} else if (cameFromSlide == 5) {
				if (ufFired == false) {
					setTimeout(function () {
						understandingFeesAnimation();
					}, 1000);
					ufFired = true;
				}
				//set the correspdoning div to 0
				$('#stFabUFcta a').attr("tabindex", "0");
				//set the other slidde divs to -1
				$('#stFabMTcta a').attr("tabindex", "-1");
				$('#stFabGScta a').attr("tabindex", "-1");
				$('#stFabBBcta a').attr("tabindex", "-1");
				$('#stFabTBcta a').attr("tabindex", "-1");
				$('#stFabFAQcta a').attr("tabindex", "-1");
				$('#stFabGcta a').attr("tabindex", "-1");
			} else if (cameFromSlide == 6) {
				if (gFired == false) {
					setTimeout(function () {
						glossaryAnimation();
					}, 200);
					gFired = true;
				}

				//set the correspdoning div to 0
				$('#stFabGcta a').attr("tabindex", "0");
				//set the other slidde divs to -1
				$('#stFabMTcta a').attr("tabindex", "-1");
				$('#stFabGScta a').attr("tabindex", "-1");
				$('#stFabBBcta a').attr("tabindex", "-1");
				$('#stFabTBcta a').attr("tabindex", "-1");
				$('#stFabFAQcta a').attr("tabindex", "-1");
				$('#stFabUFcta a').attr("tabindex", "-1");

			} else if (cameFromSlide == 7) {
				// need to swap the bg image or the second MT slide here
				if (faqFired == false) {
					//setTimeout(function () {
						faqAnimation(); 
				   // }, 200);
					faqFired = true;
				}

				//set the correspdoning div to 0
				$('#stFabFAQcta a').attr("tabindex", "0");
				//added for TAb focus issue
				$('.bx-clone #stFabFAQcta a').attr("tabindex", "-1");
				//added for TAb focus issue
				//set the other slidde divs to -1
				$('#stFabMTcta a').attr("tabindex", "-1");
				$('#stFabGScta a').attr("tabindex", "-1");
				$('#stFabBBcta a').attr("tabindex", "-1");
				$('#stFabTBcta a').attr("tabindex", "-1");
				$('#stFabUFcta a').attr("tabindex", "-1");
				$('#stFabGcta a').attr("tabindex", "-1");
			} else if (cameFromSlide == 0) {
					//set the tabindex for anchor tags inside the divs for subsequent animation slides as well
					$('#stFabBBcta a').attr("tabindex", "-1");
					$('#stFabGScta a').attr("tabindex", "-1");
					$('#stFabMTcta a').attr("tabindex", "-1");
					$('#stFabTBcta a').attr("tabindex", "-1");
					$('#stFabFAQcta a').attr("tabindex", "-1");
					$('#stFabUFcta a').attr("tabindex", "-1");
					$('#stFabGcta a').attr("tabindex", "-1");
			}
			if (sldr.getCurrentSlide() == 0)
				$('.bx-prev').css("display", "none");
			else
				$('.bx-prev').css("display", "block"); 
		},
		maxSlides: 2, // maximum slides displayed
		minSlides: 2, // minimum slides displayed
		moveSlides: 2,
		infiniteLoop: turnLoopOn,
		hideControlOnEnd: lastCaretOff
			  //  ,useCSS:false// prevents the slider from looping back to the welcome slide
		//hideControlOnEnd: true // hides the controls at either end of the carousel
				
			});
			
	$('a.bx-next').click(function (e) {
		var currentAnimationSlideIndex = 0;
		if (sldr.getCurrentSlide() != 0)
			currentAnimationSlideIndex = sldr.getCurrentSlide() - 1;
		else
			currentAnimationSlideIndex = sldr.getSlideCount()/2 - 1;

		var animationslidename = $("#AnimationSlideIndex" + currentAnimationSlideIndex).val();
		
		//Need to use the same hidden field as used for desktop-infographic
		$("#SlideClicked").val(animationslidename);

	});
	$(window).bind('resize', stopGap());
			
	$(window).resize(function () {

		width = $(window).width();
		if ($(window).width() < 1025) {
			window.location = window.location.href;
		}
		setTimeout(setCTAFontsize, 5);
		var zoomNew = document.documentElement.clientWidth / window.innerWidth;
		if (zoom != zoomNew) {
			// zoom has changed
			// adjust your fixed element
			zoom = zoomNew;
		}

	});
	}
									};
	$(function () {
					suntrustFABPage.init();
	});
					function setCTAFontsize() { 
					var fontsize = $('#stFabGScta').width() * 0.125; // 12.5% of container width 
					$('.cta .cta-text').css('font-size', fontsize); 
					}; 
					var zoom = document.documentElement.clientWidth / window.innerWidth; 
					
					function stopGap() { 
					setTimeout(setCTAFontsize, 5); 
					} 
					
					var uAgent = navigator.userAgent, 
					turnLoopOn = true, 
					lastCaretOff = false; 
					var windowSize, 
					bbFired = false; 
					var gsFired = false; 
					var mtFired = false; 
					var tbFired = false; 
					var faqFired = false; 
					var ufFired = false; 
					var gFired = false; 
					var IsAnimation = 'TRUE'; 
