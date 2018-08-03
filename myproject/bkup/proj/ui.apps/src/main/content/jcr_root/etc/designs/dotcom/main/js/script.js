$(document)
		.ready(
				function() {
					var globalElement = $('#suntrust-page');
					var isFirefox = navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
					var isIE = navigator.appName == 'Microsoft Internet Explorer'
							|| !!(navigator.userAgent.match(/Trident/) || navigator.userAgent
									.match(/rv:11/))
							|| (typeof $.browser !== "undefined" && $.browser.msie == 1);
					var isSafari = /Safari/.test(navigator.userAgent) && /Apple Computer/.test(navigator.vendor);
					
										
					/******Tab Focus Trigger **********/
					$(document).keypress(function (e) {

		                var code = e.keyCode ? e.keyCode : e.which;
		                if (code == 13) 
		                {
		                	var $focusTrigger = $(':focus');
		                	if($focusTrigger.is('button, input[type=button], input[type=submit],.suntrust-main-links'))
		                    {              
		                      $focusTrigger.click();
		                    }

		                }
                        if(isFirefox){

							var activeElement = $(document.activeElement).attr('href');

                            if(code == 13 && activeElement!=undefined){
                                if(activeElement.is('a')){
								    activeElement.click();
                                }
                            }
                        }

					});
					/******Tab Focus Trigger End **********/
					
					/******Fax number disable on Mobile device Start ********/
					$(document).on('touchstart','.location_fax_details p,.search_result_location_fax_details p',function(){                        
                        return false;
                    });
					/******Fax number disable on Mobile device End ********/
					
					/****** ADA New Window Popup Alert Start ********/
					if($('.newWindowpopup').length==0){
						$("#suntrust-page").append('<div class="newWindowpopup">Opens a new window</div>');
					}
					/****** ADA New Window Popup Alert End ********/
					
					/*Header White Space Browser Level Starts*/
					setTimeout(function(){
						if(window.innerWidth>767){
							if($('header').innerHeight()>70)
							{
								$('body').css('padding-top',$('header').innerHeight()+'px');
							}
						}
						if (isSafari) {						
							if(window.innerWidth>1024){
								if($('header').innerHeight()>70)
								{
									$('body').css('padding-top',$('header').innerHeight()+'px');
								}
							}							
						}
					},800);
					/*Header White Space Browser Level End*/
					
					$(window).resize(function(){
                        if(window.innerWidth>767)
						{
                            if($('header').innerHeight()>70)
							{
								$('body').css('padding-top',$('header').innerHeight()+'px');
							}
                        }
                        else
						{
							$('body').css('padding-top','70px');
						}
                        stickyNavPosStyle();
	                });
					/*Open an account - starts*/
					$(document).on("click","#bbNavList a",function(){
						var prodScrollTop = $( $(this).attr('href') ).offset().top - 189;
						$("#bbNavList a").removeClass('active');
						$(this).addClass('active');
						$("body, html").animate({ scrollTop: prodScrollTop }, 1000);
					});
					var lastScrollTop = 0;
					$(document).on("scroll", onScroll);
					function onScroll(event){
						var scrollPos = $(document).scrollTop() + 280;
						if ($("#bbNavList").is(":visible") == true) {
							$('#bbNavList a').each(function () {
								var currLink = $(this);
								if(currLink.attr("href")!=undefined && currLink.attr("href").indexOf('/') == -1) {
                                    var refElement = $(currLink.attr("href"));
                                    if(currLink.attr("href")!="#") {
                                        if (refElement.offset().top <= scrollPos && refElement.offset().top + refElement.height() > scrollPos) {
                                            $('#bbNavList a').removeClass("active");
                                            currLink.addClass("active");
                                        }
                                        else{
                                            currLink.removeClass("active");
                                        }
                                    }
                                }
							});
						}
						if ($("#bbMobileMenu").is(":visible") == true) {
							var distanceFromTop = $(this).scrollTop();
							var navMobHeight = 0;
							if ($(".suntrust-heroHeadlineHolder").is(":visible") == true) {
								navMobHeight = $('.suntrust-heroHeadlineHolder').innerHeight()+80;
								//$(".suntrust-heroHeadlineHolder").parent().addClass("openAccountHeroFixed");
							}
							else
								navMobHeight = 80;
							/*if (distanceFromTop >= navMobHeight) {
								$('#bbMobileMenu').addClass('fixed');
							} else {
								$('#bbMobileMenu').removeClass('fixed');
							}*/
							if (distanceFromTop > lastScrollTop){
								if(distanceFromTop >= 80) {
									$(".suntrust-heroHeadlineHolder").parent().addClass("openAccountHeroFixed");
									$('#bbMobileMenu').addClass('fixed');
									$(".suntrust-heroHeadlineHolder").parent().css("top","0");
									$('#bbMobileMenu').css('top',$('.suntrust-heroHeadlineHolder').innerHeight()+'px');
								}
							   // downscroll code
							} else {
								if(distanceFromTop >= 80) {
									$(".suntrust-heroHeadlineHolder").parent().css("top","70px");
									$('#bbMobileMenu').css('top',($('.suntrust-heroHeadlineHolder').innerHeight()+70)+'px');
								}
								else {
									$(".suntrust-heroHeadlineHolder").parent().removeClass("openAccountHeroFixed");
									$('#bbMobileMenu').removeClass('fixed');
									$(".suntrust-heroHeadlineHolder").parent().css("top","auto");
									$('#bbMobileMenu').css('top','auto');
								}
							  // upscroll code
							}
							lastScrollTop = distanceFromTop;
						}
					} 
					$(document).on("click","#bbMobileOptionsContainer a",function(){
						var	prodScrollTopMobile;
                        if($(window).scrollTop()==0)
						{
                        	prodScrollTopMobile = $( $(this).attr('href') ).offset().top - 530;
						}
                        else
						{
                        	prodScrollTopMobile = $( $(this).attr('href') ).offset().top - 200;
						}	
						$("#bbMobileOptionsContainer a").removeClass('active');
						$(this).addClass('active');
						$("body, html").animate({ scrollTop: prodScrollTopMobile }, 1000);
						$(this).parents("#bbMobileOptionsContainer").prev("#bbMobileGrayBar").find("#bbCloseBox").toggleClass('bbRotate');
						$(this).parents("#bbMobileOptionsContainer").slideToggle(); 
					});
					$(".newAccount_Col").each(function(){
						$(this).parents('section').find(".openNewAccount_mobileNav .HeaderNavContaner").html($(this).find('.container-fluid .row').html());
						$(this).find('.container-fluid').remove();
					}); 

					var mi=0;
					$(".openaccountcontresp .openNewAccount_mobileNav").each(function(){
						if(mi!=0)
							$(this).remove();
						mi++;
					});
					if ($("#bbStickyNavBox").is(":visible") == true) {
                        stickyNavPosStyle();
                    }
                    function stickyNavPosStyle() {
                        var bodyHeightSticky = 0;
                        var heroHeadingHeightSticky = 0;
                        var sectionHeightSticky = 0;
						if ($("header").is(":visible") == true) {
                        	bodyHeightSticky = $('header').innerHeight();
                        }
                        if ($(".heroheadline").is(":visible") == true) {
                        	heroHeadingHeightSticky = $('.heroheadline').innerHeight();
                        }
                        var sn = 0;
                        $(".openaccountcontresp").each(function(){
                            if(sn==0) {
								sectionHeightSticky = $(this).find(".suntrust-compare-section-heading").innerHeight()+65;
                                var tileWidth = $(this).find(".suntrust-accounts-comparison-list li:first-child").innerWidth();
                                $('li#bbStickyNavBox').attr('style', 'width: '+tileWidth+'px');
                            }
                            sn++;
                        });
                        $("li#bbStickyNavBox").css("top",(bodyHeightSticky+heroHeadingHeightSticky+sectionHeightSticky)+"px");
                    }
					/*Open an account - ends*/					
					
					/** ******* Ellipsis code for hero ******** */

					$("h2.sun-product-carousel-heading,p.sun-product-carousel-products-intro")
							.each(
									function() {
										if ($(this).text().length > 150) {
											$(this).html(
													$(this).html().substring(0,
															150)
															+ '...');
										}

									});

					$("h2.sun-product-carousel-heading,p.sun-product-carousel-products-intro")
							.each(
									function() {
										if ($(this).text().length > 80) {
											$("h2.sun-product-carousel-heading")
													.addClass("adjustFont");
											$(
													"p.sun-product-carousel-products-intro")
													.addClass("adjustFont");
										}

									});

					/** ******* Ellipsis code for hero ******** */									
					
					
					/*Find us with search - starts*/

					$(document).on("click", ".find_us .find_search.suntrust-secondary-button", function(e){
						e.preventDefault();
						var searchTerm = $('.find_us .suntrust_find_input').val().trim();
						var redirectionURL = $(this).attr("href");
						if(searchTerm != "" && redirectionURL != undefined)
						window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm)+"#locations";
					});

					$(document).on('keypress',function(e) {
						if (e.keyCode === 13) {
							if($('.find_us .find_search.suntrust-secondary-button').length > 0) {
								e.preventDefault();
								var searchTerm = $('.find_us .suntrust_find_input').val().trim();
								var redirectionURL = $('.find_us .find_search.suntrust-secondary-button').attr("href");
								if(searchTerm != "" && redirectionURL != undefined)
								window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm)+"#locations";
							}
						}
					});
					
					
					globalElement.on('click','.find_us .find_input a.find_search', function(e) {				        
			            e.preventDefault();
			    		var serachBarURL = $(this).attr('href');			            
			    		var searchText = $(this).closest(".find_input").find(".suntrust_find_input").val().trim();
			    		searchText = encodeURIComponent(searchText).replace(/%20/g, "+");
			    		if(serachBarURL != '')
			    		{
			    			//serachBarURL = serachBarURL + "?searchTerm=" + encodeURIComponent(searchText) + "#locations";
			    			serachBarURL = serachBarURL + "?searchTerm=" + searchText + "#locations";
			    			//this will redirect us in same tab
			    			window.open(serachBarURL, '_self');		    			
			    		}
			    	});			    	
			    	$(document).on('keypress','.find_us .find_input .suntrust_find_input',function(e) {
			    	    if (e.keyCode === 13) {
			        		$(this).blur();
			        		$('.find_us .find_input a.find_search').trigger('click');
			    	    }
			    	});
					 
				    /*Find us with search - ends*/
					    
					/*Search modal Window with search - starts*/
					$(document).on("click", ".suntrust-modal-window .suntrust-nav-search .suntrust-orange-button", function(e){
						e.preventDefault();
						var searchTerm = $('.suntrust-modal-window .suntrust-nav-search #search.suntrust-nav-search-input').val().trim();
						var redirectionURL = $(this).attr("href");
						var searchTabName = typeof ($(this).data('search-tab')) == 'undefined' ? 'all_results' : $(this).data('search-tab');
						var currentUrl = window.location.href.split("#")[0];
						if(searchTerm != "" && redirectionURL != undefined){
							if(redirectionURL.indexOf("?") >=0){
								//var icid = redirectionURL.split("?")[1];
								var newUrl = window.location.protocol+"//"+window.location.hostname+redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+");
								if(currentUrl == newUrl){
									window.location.href = redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
									window.location.reload();
								}else{
									window.location.href = redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
								}
								
							}
							else{
								var newUrl = window.location.protocol+"//"+window.location.hostname+redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+");
								if(currentUrl == newUrl){
									window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
									window.location.reload();
								}else{
									window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
								}
							}
						}
					});
					
					
					$(document).on('keypress',function(e) {
						if (e.keyCode === 13) {
							if($('.suntrust-modal-window .suntrust-nav-search .suntrust-orange-button').length > 0) {
								e.preventDefault();
								var searchTerm = $('.suntrust-modal-window .suntrust-nav-search #search.suntrust-nav-search-input').val().trim();
								var redirectionURL = $('.suntrust-modal-window .suntrust-nav-search .suntrust-orange-button').attr("href");
								var $link = $('.suntrust-modal-window .suntrust-nav-search .suntrust-orange-button');
								var searchTabName = typeof ($link.data('search-tab')) == 'undefined' ? 'all_results' : $link.data('search-tab');
								var currentUrl = window.location.href.split("#")[0];
								if(searchTerm != "" && redirectionURL != undefined){
									if(redirectionURL.indexOf("?") >=0){
										//var icid = redirectionURL.split("?")[1];
										var newUrl = window.location.protocol+"//"+window.location.hostname+redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+");
										if(currentUrl == newUrl){
											window.location.href = redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
											window.location.reload();
										}else{
											window.location.href = redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
										}
									}
									else{
										var newUrl = window.location.protocol+"//"+window.location.hostname+redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+");
										if(currentUrl == newUrl){
											window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
											window.location.reload();
										}else{
											window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
										}
									}
								}
							}
						}
					});
						
					/*Search modal Window with search - ends*/

				    /*Location modal Window with search - starts*/
				    $(document).on("click", ".suntrust-modal-window .suntrust-nav-location .suntrust-orange-button", function(e){
				        e.preventDefault();
						var searchTerm = $('.suntrust-modal-window .suntrust-nav-location #locations.suntrust-nav-location-input').val().trim();
				        var redirectionURL = $(this).attr("href");
				        var searchTabName = typeof ($(this).data('search-tab')) == 'undefined' ? 'all_results' : $(this).data('search-tab');
				        var currentUrl = window.location.href.split("#")[0];
				        if(searchTerm != "" && redirectionURL != undefined){
				        	if(redirectionURL.indexOf("?") >=0){
		                		//var icid = redirectionURL.split("?")[1];
				        		var newUrl = window.location.protocol+"//"+window.location.hostname+redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+");
								if(currentUrl == newUrl){
									window.location.href = redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
									window.location.reload();
								}else{
									window.location.href = redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
								}
		                	}
                            else{
                            	var newUrl = window.location.protocol+"//"+window.location.hostname+redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+");
								if(currentUrl == newUrl){
									window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
									window.location.reload();
								}else{
									window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
								}
                            }
				        }
				    });

				    $(document).on('keypress',function(e) {
					    if (e.keyCode === 13) {
					    	if($('.suntrust-modal-window .suntrust-nav-location .suntrust-orange-button').length > 0) {
					    		e.preventDefault();
				                var searchTerm = $('.suntrust-modal-window .suntrust-nav-location #locations.suntrust-nav-location-input').val().trim();
				                var redirectionURL = $('.suntrust-modal-window .suntrust-nav-location .suntrust-orange-button').attr("href");
				                var $link = $('.suntrust-modal-window .suntrust-nav-location .suntrust-orange-button');
				                var searchTabName = typeof ($link.data('search-tab')) == 'undefined' ? 'all_results' : $link.data('search-tab');
				                var currentUrl = window.location.href.split("#")[0];
				                if(searchTerm != "" && redirectionURL != undefined){
				                	if(redirectionURL.indexOf("?") >=0){
				                		//var icid = redirectionURL.split("?")[1];
						        		var newUrl = window.location.protocol+"//"+window.location.hostname+redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+");
										if(currentUrl == newUrl){
											window.location.href = redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
											window.location.reload();
										}else{
											window.location.href = redirectionURL.split("?")[0]+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
										}
				                	}
		                            else{
		                            	var newUrl = window.location.protocol+"//"+window.location.hostname+redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+");
										if(currentUrl == newUrl){
											window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
											window.location.reload();
										}else{
											window.location.href = redirectionURL+"?searchTerm="+encodeURIComponent(searchTerm).replace(/%20/g, "+")+"#" + searchTabName;
										}
		                            }
				                }
					    	}
					    }
					});
				    /*Location modal Window with search - ends*/

					$('.suntrust-resource-tags-list').each(function(e) {
						$(this).find('span:last').remove();
					});
					
					$('.sun-checkbox-input-container').on('click', function(e){
                        if($(this).hasClass('sun-checkbox-disabled'))
							e.preventDefault();
                        else{
                        	if($('body .search_result_categories').length == 0)
                        		$(this).find("span").toggleClass("sun-checked");
                        }
                    })          
                    
					/*if (isIE) {
						$("html, body").animate({
							scrollTop : 150
						}, 5);
						$('html, body').animate({
							scrollTop : 0
						}, 8);
						var timeOut = setTimeout(function (){
					        window.scrollTo(0, 200);
					        setTimeout(function (){
					            window.scrollTo(0, 0);
					            clearTimeout(timeOut);
					        }, 500);
					    }, 1000);
					}*/

					window.onpageshow = function(event) {
						if (event.persisted) {
							window.location.reload()
						}
					};
					var is_iPad = navigator.userAgent.match(/iPad/i) != null;
					/*if (is_iPad) {
						if (isSafari){
                            if($(".rowcontainerresp .video").is(":visible")){
                                $(".suntrust-wrapperContainer, .suntrust-rowContainer").css({
                                    "overflow" : "visible"
                                });
                            }
                        }
					}*/
					var isSafari = /Safari/.test(navigator.userAgent)
							&& /Apple Computer/.test(navigator.vendor);

					if (isSafari) {
						/*$(".suntrust-primary-button_CTAs").css({
							"padding-top" : "12px",
							"padding-bottom" : "8px !important",
							"min-height" : "48px"
						});
						$(".suntrust-secondary-button_CTAs").css({
							"padding-top" : "12px",
							"padding-bottom" : "8px !important",
							"min-height" : "48px"
						});
						$(".suntrust-tertiary-button_CTAs").css({
							"padding-top" : "12px",
							"padding-bottom" : "8px !important",
							"min-height" : "48px"
						});*/
						$(".suntrust-contactUs .suntrust-supportCall .suntrust-supportNumber").css({"top" : "0"});
						$(".suntrust-contactUs .suntrust-supportMail .suntrust-supportEmail, .suntrust-contactUs .suntrust-supportLiveChat .suntrust-supportChat,.suntrust-contactUs .suntrust-supporthelpCenter .suntrust-supportHelpcenter")
								.css({
									"top" : "0"
								});
						$(".suntrust-mail .suntrust-mailPara span").css({"top" : "0"});
						$(".suntrust-transferInstructions .suntrust-transferPara span")
								.css({
									"margin-top" : "0"
								});
						$(".suntrust-contactUsCallCenter .suntrust-callcenterPara span")
								.css({
									"top" : "0"
								});
						$(".contactUsLstcontSec .suntrust-supportNumber").css({
							"line-height" : "28px"
						});
						$(".suntrust-ContactHeadTxt span").css({
							"vertical-align" : "baseline"
						});
						$(".suntrust-transferInstructions .suntrust-instructions .suntrust-instructionBgwhite")
								.css({
									"padding" : "5px 5px 9px 12px"
								});
						$(".find_input .find_search").css({
							"padding" : "12px 0 15px 0"
						});
						$(".suntrust-promoTop .suntrust-color-border-orange")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTop .suntrust-color-border-green")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTop .suntrust-color-border-yellow")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTop .suntrust-color-border-blue")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTop .suntrust-color-border-skyblue")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTop .suntrust-color-border-pink")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTopImage .suntrust-color-border-orange")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTopImage .suntrust-color-border-green")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTopImage .suntrust-color-border-yellow")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTopImage .suntrust-color-border-blue")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTopImage .suntrust-color-border-skyblue")
								.css({
									"top" : "-10px"
								});
						$(".suntrust-promoTopImage .suntrust-color-border-pink")
								.css({
									"top" : "-10px"
								});
								
						$(".location_tog span")
								.css({
									"margin-top" : "-10px"
								});
						$(".location_tog span.HideLoc")
								.css({
									"margin-top" : "-10px"
								});
						if(window.innerWidth<768) {
                            $("#loadingMask").prev('div').find('section:first-child .container-fluid .search_result_heading').css({"padding":"0 10px"});
                            $("#loadingMask").prev('div').find('section:not(:first-child) .row:first-child').css({"padding":"0 10px"});
                        }
					}

					// Find branch atm script start
					$(".suntrust-directory-horizontal-menu-item a").each(function () {						
						var alphabetHrefValue = $(this).attr('href').trim();						
						if(!$(alphabetHrefValue).length)
						{							
							alphabetHrefValue = alphabetHrefValue.replace("#suntrust-branch-directory-" , "");
							
							$(this).closest(".suntrust-directory-horizontal-menu-item").text(alphabetHrefValue);
						}
						
					});
					
					var userAddress;
					
					function getUserAddress()
					{
						navigator.geolocation.getCurrentPosition(function (position) {
							startLat = position.coords.latitude;
							startLng = position.coords.longitude;
							$.ajax({
								  type: "POST",
								  url: 'https://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850&outFormat=json&location='+startLat+','+startLng,
								  dataType: "json",
								  success: function (data) {
										 $.each(data.results, function() {
										   $.each(this, function(key, val){
											   if(key != null && key != undefined && key=='locations') {
													  var locationsArr = val;
													  street = locationsArr[0].street;
													  city = locationsArr[0].adminArea5;
													  state = locationsArr[0].adminArea3;
													  zip = locationsArr[0].postalCode;
											   }
										   });
										 });
										 var findme = {
											   address: street+','+city+','+state+','+zip,
										 };
										console.log(findme['address']);
										userAddress = findme['address'];
										
										
								  },
								  error: function (xhRequest, ErrorText, thrownError) {
										 ajaxError();
								  }
						   });
						});
						//userAddress = "7225 Peachtree Dunwoody Rd NE,Atlanta,GA,30328-1619";
						return userAddress;
					}
					
					if($('body .search_result_categories').length == 0){
						userAddress = getUserAddress();
					}			
					
					
					setTimeout(function() {
						
						var schListCount = $('.branch_atm_schList').length;
						
						//console.log("schListCount : " + schListCount);
						
						$('.branch_atm_schList').each(function (index) {
							
							//console.log("userAddress value : " + index + " : " +userAddress);
							
							// initially show only 6 items
							if(index > 5)
				        	{
					        	return false;
				        	}
							
							/*else
							{*/
								var id = $(this).attr('id').trim();
						
						        var loadURL = $(this).data('branch-atm-path');
						
						        loadURL = loadURL.trim() + ".search.html?searchString=" + encodeURIComponent(userAddress);
						
						        $(this).load(loadURL);
						        
						        if(index < 5 && index < (schListCount - 1))
						        {
						        	$(this).after(" <div class='atm_schList_divider'></div>");
						        }
						        						        					        
						        if(schListCount > 6)
								{
									$("div.location_result_show_more_results").show();
								}	
					        
							//}
					
						});	
						
					}, 2000);
					
					
					$("div.location_result_show_more_results a").on("click", function() {
						
						var len = $('.search_result_description_details').length;						
						var len1 = $('.branch_atm_schList').length;
						
						var nextIncrement = 5;
						var flag = true;						
						
						if(len === len1)
						{
							flag = false;
							$("div.location_result_show_more_results").remove();	
						}
						
						if(flag)
						{
							$('.branch_atm_schList').each(function (index) {
								
								if(index < len)
					        	{
						        	return true;
					        	}
								
								else if(index > (len + nextIncrement))
					        	{
						        	return false;
					        	}
								
								if(len1 <= (len + nextIncrement + 1))
								{
									$("div.location_result_show_more_results").remove();
								}								
								
								/*else
								{*/
									if(index === len)
							        {
							        	$(this).before(" <div class='atm_schList_divider'></div>");
							        }
									var id = $(this).attr('id').trim();								
							        var loadURL = $(this).data('branch-atm-path');				
							        
							        loadURL = loadURL.trim() + ".search.html?searchString=" + encodeURIComponent(userAddress);	
									
							        $(this).load(loadURL);
							        if(index != (len + nextIncrement) && index < (len1 - 1))
							        {
							        	$(this).after(" <div class='atm_schList_divider'></div>");
							        }
								//}
								
							});
							
						}
						
					});
					
					
					// Find branch atm script end
					
					/* Continue Reading Mobile View Start */
	                var about_length = $('.people_right_about').text().length;
                    //$("p:empty").css("display", "none");
	                continue_reading();       
	                
	                $(document).on("click",".people_right_about .suntrust-secondary-button",function(){
	                                $(this).parent().parent().addClass("people_right_about_reading");
	                });
	                
	                $(window).resize(function(){
                        continue_reading();
                        /*open an Account*/
    						if($('header').innerHeight()>70) {
    							$('body').css('padding-top',$('header').innerHeight()+'px');
    						}
                        /*open an Account*/
	                });
	                
	                function continue_reading() {
                        if(window.innerWidth<768) {
                            if(about_length>500)
                                $(".people_right_about .suntrust-secondary-button").parent().show();
                            else
                                $(".people_right_about .suntrust-secondary-button").parent().hide();
                        }
                        else
                            $(".people_right_about .suntrust-secondary-button").parent().hide();
	                }
	                /* Continue Reading Mobile View End */

					  // Table compare colspan set dynamically Start
					  var tableStyle = '';
					  var tc_count = 0;
					  $('.suntrust_account_find').each(function() {
                          var comparisonWidth = 0;
					      tableStyle = $(this).find('input[name=compareTableType]').val();
					      tc_count = 0;
					      if (tableStyle == 'comparison') {
					          $(this).find('.table_compare').each(function() {
                                  comparisonWidth = $(this).innerWidth();
					              tc_count = $(this).find("tbody > tr:first-child td").length;
					              $(this).find('th.account_bg_grey').each(function() {
					                  $(this).parent().find('td').remove();
					                  for (var i = 0; i < tc_count; i++) {
					                      $(this).parent().append('<td class="account_bg_grey">&nbsp;</td>');
					                  }
					              });


					              $(this).find('th.account_bg_grey').each(function() {
                                      var th_height;
                                      var pad_th_height;
                                    	if($(this).find('div').hasClass('table_heading_content')) {
										th_height=$(this).find('.table_heading_content').innerHeight();
										pad_th_height = th_height + 30;
									}
									else {
										th_height = $(this).innerHeight();
										pad_th_height = th_height + 10;
									}

					                  if (isSafari) {
                                        if (pad_th_height >= 70) {
                                            $(this).parent().css("height", pad_th_height + "px");
                                            $(this).css("height", pad_th_height + "px");
                                        }
                                    }
					                if(is_iPad){
					                	  if (pad_th_height >= 70) {
	                                            $(this).parent().css("height", pad_th_height + "px");
	                                            $(this).css("height", pad_th_height + "px");
	                                  }
	                                }
                                    else {
                                        if (pad_th_height > 50) {
                                            $(this).parent().css("height", pad_th_height + "px");
                                            $(this).css("height", pad_th_height + "px");
                                        }
                                        else {
                                        	$(this).parent().css("height", "auto");
                                            $(this).css("height", "auto");
                                        }
                                    }
					              });


					              var tc_index = tc_count - 1;
					              var tc_more = tc_count - 3;
					              var tc_more_count = 0;
                                  var tableColumnWidth = 0;

                                  if(!$(this).parents().hasClass('suntrust-section3A') && tc_count > 4) {
									  tableColumnWidth = comparisonWidth/5;
                                      $(this).find('td').css('width', tableColumnWidth+'px');
                                      $(this).find('tr td:nth-child(5)').css('width', (tableColumnWidth-2)+'px');
                                      $(this).find('tr td:nth-child(5)').css('border-right', '0');
                                      $(this).find('th').css('width', (tableColumnWidth+1)+'px');
                                      $(this).parent().css('margin-left', tableColumnWidth+'px');
                                  }
                                  else if($(this).parents().hasClass('suntrust-section3A') && tc_count > 3) {
									  tableColumnWidth = comparisonWidth/4;
                                      $(this).find('td').css('width', tableColumnWidth+'px');
                                      $(this).find('th').css('width', (tableColumnWidth+2)+'px');
                                      $(this).parent().css('margin-left', tableColumnWidth+'px');
                                      if(isFirefox) {
                                          $(this).find('tr td:nth-child(4)').css('width', (tableColumnWidth)+'px');
                                          $(this).find('th').css('width', (tableColumnWidth+3)+'px');
                                          $(this).find('tr td:nth-child(4)').css('border-right', '0');
                                      }
                                      else {
                                          $(this).find('tr td:nth-child(4)').css('width', (tableColumnWidth)+'px');
                                          $(this).find('tr td:nth-child(4)').css('border-right', '0');
                                      }
                                  }
                                  else {
								  	  tableColumnWidth = comparisonWidth/(tc_count+1);
                                      $(this).find('td').css('width', tableColumnWidth+'px');
                                      $(this).find('tr td:last-child').css('width', (tableColumnWidth-2)+'px');
                                      $(this).find('th').css('width', tableColumnWidth+'px');
                                  }

								  var thWidth = $(this).find('tr:first-child > th').innerWidth();
                                  $(this).parent().parent().prev().find('div:first-child').css('width', (comparisonWidth - thWidth) + 'px');

                                  if(($(this).parents().hasClass('suntrust-section3A') && tc_count > 3) || !$(this).parents().hasClass('suntrust-section3A') && tc_count > 4) {
                                        if ($(this).parent().parent().prev().hasClass('table_compare_more'))
                                            $(this).parent().parent().prev().show();
                                        $('.table_compare_more .table_compare_left').addClass('disabled');
                                        $(this).removeClass('noViewMore');
                                        if ($(this).parent().hasClass('table_scroller'))
                                            $(this).parent().addClass("table_row_height");
                                  }
								  else if(($(this).parents().hasClass('suntrust-section3A') && $(this).parent().parent().prev().hasClass('table_compare_more') && tc_count <= 3) || (!$(this).parents().hasClass('suntrust-section3A') && $(this).parent().parent().prev().hasClass('table_compare_more') && tc_count <= 4)) {
									    $(this).parent().parent().prev().hide();
                                  }
                                  var lastTrHeight = 0;
                                  if($(this).find("tr:last-child th > div").hasClass("table_heading_content")) {
									lastTrHeight = $(this).find("tr:last-child th div.table_heading_content").innerHeight();
                                    $(this).find("tr:last-child th").css("height",(lastTrHeight+31)+"px");
                                    $(this).find("tr:last-child").css("height",(lastTrHeight+31)+"px");
                                  }
                                  else {
                                    lastTrHeight = $(this).find("tr:last-child").innerHeight();
                                    $(this).find("tr:last-child th").css("height",(lastTrHeight+1)+"px");
                                    $(this).find("tr:last-child").css("height",(lastTrHeight+1)+"px");
                                  }
                                  var firstThHeight = $(this).find("tr:nth-child(1) td:nth-child(2)").innerHeight();
                                  var secondThHeight = $(this).find("tr:nth-child(2) td:nth-child(2)").innerHeight();
                                  $(this).find("tr:nth-child(1) th").css("height",firstThHeight+'px');
                                  $(this).find("tr:nth-child(2) th").css("height",secondThHeight+'px');
					          });
					      } else {
					          var maxtdLength = 0;
					          var staticTableCount = 0;
					          $(this).find('.table_compare').each(function() {
					        	  $(this).addClass('tableComparison_Static'); 
					              $(this).find('tr').each(function() {
					                  if (!$(this).hasClass('account_bg_grey')) {
					                      if ($(this).find('td').length > maxtdLength) {
					                          maxtdLength = $(this).find('td').length;
					                      }
					                  }
					                  if (!$(this).find('th').hasClass('account_bg_grey')) {
                                        	if($(this).find('td:nth-child(3)').text()!="")
												staticTableCount++;
									  }
					              });
					              tc_count = maxtdLength;
					              $(this).find('th.account_bg_grey').each(function() {
					                  $(this).parent().find('td').remove();
					                  for (var i = 0; i < tc_count; i++) {
					                      $(this).parent().append('<td class="account_bg_grey">&nbsp;</td>');
					                  }
					              });

					              if(staticTableCount==0) {
                                      $(this).find('td:nth-child(2)').attr('colspan','2');
                                      $(this).find('td:nth-child(3)').remove();
                                  }
					              
					              $(this).find('th.account_bg_grey').each(function() {
                                      var th_height;
                                      var pad_th_height;
                                    	if($(this).find('div').hasClass('table_heading_content')) {
										th_height=$(this).find('.table_heading_content').innerHeight();
										pad_th_height = th_height + 30;
									}
									else {
										th_height = $(this).innerHeight();
										pad_th_height = th_height + 10;
									}

					                  if (isSafari) {
                                        if (pad_th_height >= 70) {
                                            $(this).parent().css("height", pad_th_height + "px");
                                            $(this).css("height", pad_th_height + "px");
                                        }
                                    }
					                 if(is_iPad){
					                	  if (pad_th_height >= 70) {
	                                            $(this).parent().css("height", pad_th_height + "px");
	                                            $(this).css("height", pad_th_height + "px");
	                                  }
	                                }
                                    else {
                                        if (pad_th_height > 50) {
                                            $(this).parent().css("height", pad_th_height + "px");
                                            $(this).css("height", pad_th_height + "px");
                                        }
                                    }
					              });


					              var tc_index = tc_count - 1;
					              var tc_more = tc_count - 3;
					              var tc_more_count = 0;

					              if (tc_count > 3) {

					                  // $('.table_compare_more
					                  // a').removeClass('disabled');
					                  if ($(this).parent().parent().prev().hasClass('table_compare_more'))
					                      $(this).parent().parent().prev().show();
					                  $('.table_compare_more .table_compare_left').addClass('disabled');
					                  $(this).find('td').css('width', '250px');
					                  if ($(this).parent().hasClass('table_scroller'))
					                      $(this).parent().addClass("table_row_height");
					              } else if ($(this).parent().parent().prev().hasClass('table_compare_more') && tc_count <= 3)
					                  $(this).parent().parent().prev().hide();
					              // Table compare colspan set dynamically End
					          });
					          if (tableStyle == 'static' && window.innerWidth < 768) {

					              $(this).find('.table_compare_mobile').show();
					              $(this).find('.table_compare_mobile td').removeClass('hide');
					              $(this).find('.table_compare_mobile td.account_bg_grey').attr('colspan', tc_count);
					              $(this).find('.table_compare_mobile tr.account_odd_row td').attr('colspan', tc_count);
					              if (tc_count == 1)
					                  $(this).find('.table_compare_mobile tr.account_even_row td').addClass('text-center');
					              else{
					                  $(this).find('.table_compare_mobile tr.account_even_row td').removeClass('text-center');
					                  $(this).find('.table_compare_mobile td').css('width', '50%');
					              }

					          } else if (window.innerWidth < 768 && ($('input[name="compareTableProductCount"]').val() <= 2)) {
					              $(this).find('.product_selection_details').hide();
					              $(this).find('.table_compare_mobile').show();
					              $(this).find('.table_compare_mobile').css('margin-top', '0');
					              $(this).find('.table_compare_mobile td').removeClass('hide');
					              if ($(this).find('input[name="compareTableProductCount"]').val() == 2)
					                  $(this).find('.table_compare_mobile td').css('width', '50%');
					              if ($(this).find('input[name="compareTableProductCount"]').val() == 1) {
					                  $(this).find('.table_compare_mobile td').addClass('text-center');
					              }
					          } else
					              $(this).find('.table_compare_mobile').hide();
					          
					          $(this).find('.table_compare_mobile').each(function() {
                                  staticTableCount = 0;
					              $(this).find('tr.account_even_row').each(function() {
									  if($(this).find('td:last-child').text()!="")
									  	  staticTableCount++;
                                  });
                                  if(staticTableCount==0) {
                                      $(this).find('tr.account_even_row td').css('text-align','center');
                                      $(this).find('tr.account_even_row td:last-child').remove();
                                  }
                               });
					          
					          tableCompare(tableStyle);
					      }

					  });


					  // Table View More Navigation Start
					  $('.table_compare_more a.disabled').click(function(e) {
					      e.preventDefault();
					  });

					  $(".table_compare_left").click(function() {
					      $(this).parents('.table_compare_more').next().find('.table_scroller').animate({
					          scrollLeft: 0
					      });
					  });

					  var tableCompScrollCount=0;
					  $(".table_compare_right").click(function() {
                          var tc_count = $(this).parents('.table_compare_more').next().find("tbody > tr:first-child td").length;
                          var tableColumnWidthPx = $(this).parents('.table_compare_more').next().find('.table_compare tr:first-child > th').css('width');
                          var tableColumnWidth = parseInt(tableColumnWidthPx, 10);
                          $(this).parents('.table_compare_more').next().find('.table_scroller').animate({
                              scrollLeft: "+=" + tableColumnWidth
					      });
                          if(!$(this).parents().hasClass('suntrust-section3A') && tc_count > 4) {
                              if(tableCompScrollCount<=6) {
									  	tableCompScrollCount=6;
                              }
                              $(this).parents('.table_compare_more').next().find('tr td:nth-child('+(tableCompScrollCount-1)+')').css('border-right', '1px solid #d8d8d8');
                              $(this).parents('.table_compare_more').next().find('tr td:nth-child('+tableCompScrollCount+')').css('border-right', '0');
                          }
                          else if($(this).parents().hasClass('suntrust-section3A') && tc_count > 3) {
                              if(tableCompScrollCount<=5)
                                  tableCompScrollCount=5;
                              if(isFirefox) 
                              {
                                  $(this).parents('.table_compare_more').next().find('tr td:nth-child('+(tableCompScrollCount-1)+')').css('border-right', '1px solid #d8d8d8');
                                  $(this).parents('.table_compare_more').next().find('tr td:nth-child('+tableCompScrollCount+')').css('border-right', '0');
                              }
                              else
                              {
                                  $(this).parents('.table_compare_more').next().find('tr td:nth-child('+(tableCompScrollCount-1)+')').css('border-right', '1px solid #d8d8d8');
                                  $(this).parents('.table_compare_more').next().find('tr td:nth-child('+tableCompScrollCount+')').css('border-right', '0');
                              }
                          }
                          else {
                              $(this).parents('.table_compare_more').next().find('tr td:last-child').css('width', (tableColumnWidth+2)+'px');
                          }
                          tableCompScrollCount++;
					  }); 
					  
					  $(function() {
					      var scrollLeftPrev = 0;
					      $('.table_scroller')
					          .scroll(
					              function() {
					                  var $elem = $('.table_scroller');
					                  var newScrollLeft = $elem
					                      .scrollLeft(),
					                      width = $elem
					                      .outerWidth(),
					                      scrollWidth = $elem
					                      .get(0).scrollWidth+1;
					                  if (scrollWidth - newScrollLeft == width)
                                          $(this).parent().prev().find(".table_compare_right").addClass('disabled');
					                  else
                                          $(this).parent().prev().find(".table_compare_right").removeClass('disabled');

					                  if (newScrollLeft === 0)
                                          $(this).parent().prev().find(".table_compare_left").addClass('disabled');
					                  else
                                          $(this).parent().prev().find(".table_compare_left").removeClass('disabled');
					                  scrollLeftPrev = newScrollLeft;
					              });
					  });

					  // Checkbox checked count Start
					  //if (tableStyle == 'comparison') {
					  $('.product_selection_details table.visible-xs').hide();
					  $('.compare_accounts .sun-checkbox-input-container input:checkbox').attr('checked', false);
					  $('.sun-checkbox-input-container input:checkbox').click(function() {
                          tableStyle = $(this).parents('.suntrust_account_find').find('input[name=compareTableType]').val();
					      if (tableStyle == 'comparison') {
					          var numberOfChecked = $(this).parents('.compare_accounts').find('.sun-checkbox-input-container input:checkbox:checked').length;
					          var checkboxIndex;
					          $(this).parents('.compare_accounts').next().next().find('.table_compare_mobile').find('tr.account_even_row td').addClass('hide');
					          $('.sun-checkbox-input-container input:checkbox:checked').each(function() {
					              checkboxIndex = parseInt($(this).attr('index')) + 1;
					              $(this).parents('.compare_accounts').next().next().find('.table_compare_mobile').find('tr td:nth-child(' + checkboxIndex + ')').removeClass('hide');
					          });

					          if (numberOfChecked > 0) {
					              $(this).parents('.compare_accounts').next().next().find('.table_compare_mobile').show();
					          } else
					              $('.table_compare_mobile').hide();
                              if (numberOfChecked >= 2) {
					              $('.sun-checkbox-input-container input:checkbox:not(":checked")').parents('.sun-checkbox-input-container').addClass('sun-checkbox-disabled');
                                  $('.sun-checkbox-input-container input:checkbox:not(":checked")').attr('disabled', true);
					          // $('.product_selection_details
					          // table.visible-xs').show();
                              }
					          else {
					              $('.sun-checkbox-input-container input:checkbox:not(":checked")').parents('.sun-checkbox-input-container').removeClass('sun-checkbox-disabled');
                                  $('.sun-checkbox-input-container input:checkbox:not(":checked")').attr('disabled', false);
					          }
					          // $('.product_selection_details
					          // table.visible-xs').hide();
					          $(this).parents('.compare_accounts').find('.table_compare_mobile td.account_bg_grey').attr('colspan', numberOfChecked);
					          $(this).parents('.compare_accounts').find('.table_compare_mobile tr.account_odd_row td').attr('colspan', numberOfChecked);
					          if (numberOfChecked == 1) {
					              $(this).parents('.compare_accounts').next().next().find('.table_compare_mobile').find('tr.account_even_row td').addClass('text-center');
					              $(this).parents('.compare_accounts').next().next().find('.table_compare_mobile').css('width', '99.99%');
					              $(this).parents('.compare_accounts').next().next().find('.table_compare_mobile').find('tr.account_even_row td').css('width', '100%');
					          } else if (numberOfChecked == 2) {
					              $(this).parents('.compare_accounts').next().next().find('.table_compare_mobile').find('tr.account_even_row td').removeClass('text-center');
					              $(this).parents('.compare_accounts').next().next().find('.table_compare_mobile').find('tr.account_even_row td').css('width', '50%');
					          }
                          } 
					  });
					  // }

					  // Checkbox checked count End
					  // Global header nav script starts//
					  $(window)
					      .resize(
					          function() {
					              // static table
					              $('.suntrust_account_find').each(function() {
					                  tableStyleState = $(this).find('input[name=compareTableType]').val();
					                  var tc_count_state = 0;
					                  var tc_count = 0;
					                  if (tableStyleState == 'static' && window.innerWidth < 768) {
					                      $(this).find(".table_compare_mobile tbody tr").each(function() {
					                          if (!$(this).hasClass('account_bg_grey')) {
					                              if ($(this).find('td').length > tc_count_state) {
					                                  tc_count_state = $(this).find('td').length;
					                              }
					                          }
					                      });
					                      $(this).find('.table_compare_mobile').show();
					                      $(this).find('.table_compare_mobile td').removeClass('hide');
					                      $(this).find('.table_compare_mobile td.account_bg_grey').attr('colspan', tc_count_state);
					                      $(this).find('.table_compare_mobile tr.account_odd_row td').attr('colspan', tc_count_state);

					                      if (tc_count == 1)
					                          $(this).find('.table_compare_mobile tr.account_even_row td').addClass('text-center');
					                      else{
					                    	  $(this).find('.table_compare_mobile tr.account_even_row td').removeClass('text-center');
					                    	  $(this).find('.table_compare_mobile td').css('width', '50%');
					                      }
					                          
					                      tableCompare(tableStyleState);

					                  } else if ((tableStyleState == 'static' || tableStyleState == 'comparison') && window.innerWidth >= 768) {
                                          if(tableStyleState == 'comparison') {
											$(this).find('.table_compare').each(function() {
                                                $(this).find('th').css("width","auto");
                                                $(this).find('td').css("width","auto");
                                                comparisonWidth = $(this).parent().parent().innerWidth();
                                                tc_count = $(this).find("tbody > tr:first-child td").length;

                                                var tableColumnWidth = 0;

                                                if(!$(this).parents().hasClass('suntrust-section3A') && tc_count > 4) {
              									  tableColumnWidth = comparisonWidth/5;
                                                    $(this).find('td').css('width', tableColumnWidth+'px');
                                                    $(this).find('tr td:nth-child(5)').css('width', (tableColumnWidth-2)+'px');
                                                    $(this).find('tr td:nth-child(5)').css('border-right', '0');
                                                    $(this).find('th').css('width', (tableColumnWidth+1)+'px');
                                                    $(this).parent().css('margin-left', tableColumnWidth+'px');
                                                }
                                                else if($(this).parents().hasClass('suntrust-section3A') && tc_count > 3) {
              									  tableColumnWidth = comparisonWidth/4;
                                                    $(this).find('td').css('width', tableColumnWidth+'px');
                                                    $(this).find('th').css('width', (tableColumnWidth+2)+'px');
                                                    $(this).parent().css('margin-left', tableColumnWidth+'px');
                                                    if(isFirefox) {
                                                        $(this).find('tr td:nth-child(4)').css('width', (tableColumnWidth)+'px');
                                                        $(this).find('th').css('width', (tableColumnWidth+3)+'px');
                                                        $(this).find('tr td:nth-child(4)').css('border-right', '0');
                                                    }
                                                    else {
                                                        $(this).find('tr td:nth-child(4)').css('width', (tableColumnWidth)+'px');
                                                        $(this).find('tr td:nth-child(4)').css('border-right', '0');
                                                    }
                                                }
                                                else {
              								  	  tableColumnWidth = comparisonWidth/(tc_count+1);
                                                    $(this).find('td').css('width', tableColumnWidth+'px');
                                                    $(this).find('tr td:last-child').css('width', (tableColumnWidth-2)+'px');
                                                    $(this).find('th').css('width', tableColumnWidth+'px');
                                                }
                
                                                  var thWidth = $(this).find('tr:first-child > th').innerWidth();
                                                  $(this).parent().parent().prev().find('div:first-child').css('width', (comparisonWidth - thWidth) + 'px');
                
                                                  if(($(this).parents().hasClass('suntrust-section3A') && tc_count > 3) || !$(this).parents().hasClass('suntrust-section3A') && tc_count > 4) {
                                                        if ($(this).parent().parent().prev().hasClass('table_compare_more'))
                                                            $(this).parent().parent().prev().show();
                                                        $('.table_compare_more .table_compare_left').addClass('disabled');
                                                        $(this).removeClass('noViewMore');
                                                        if ($(this).parent().hasClass('table_scroller'))
                                                            $(this).parent().addClass("table_row_height");
                                                  }
                                                  else if(($(this).parents().hasClass('suntrust-section3A') && $(this).parent().parent().prev().hasClass('table_compare_more') && tc_count <= 3) || (!$(this).parents().hasClass('suntrust-section3A') && $(this).parent().parent().prev().hasClass('table_compare_more') && tc_count <= 4)) {
                                                        $(this).parent().parent().prev().hide();
                                                  }
                                            });
                                          }
                                          $(this).find('.table_compare_mobile').hide();
                                      }
					                  else if (window.innerWidth < 768 && ($(this).find('input[name="compareTableProductCount"]').val() <= 2)) {
					                      $(this).find('.product_selection_details').hide();
					                      $(this).find('.table_compare_mobile').show();
					                      $(this).find('.table_compare_mobile').css('margin-top', '0');
					                      $(this).find('.table_compare_mobile td').removeClass('hide');
					                      if ($(this).find('input[name="compareTableProductCount"]').val() == 2)
					                          $(this).find('.table_compare_mobile td').css('width', '50%');
					                      if ($(this).find('input[name="compareTableProductCount"]').val() == 1)
					                          $(this).find('.table_compare_mobile td').addClass('text-center');
					                  } 
					              });

                                  
								  // static table ends

					          });
					  

                    // Search List Show Location Start
                    $(document).on("click", ".location_tog", function() {
                        $(this).next().slideToggle();
                        if ($(this).find('strong').text() == "Show Locations") {
                            $(this).find('strong').text('Hide Locations');
                            $(this).find('span').text('-');
                            $("span").removeClass("HideLoc");
                        } else if ($(this).find('strong').text() == "Hide Locations") {
                            $(this).find('strong').text('Show Locations');
                            $(this).find('span').text('+');
                            $("span").addClass("HideLoc");
                        }
                    });
                    // Search List Show Location End


                    /*Header Modal Window Start*/
                	$(document).on('click','[data-suntrust-class="suntrust-nav-search"]',function(){
                		$('.suntrust-modal-window,.suntrust-nav-search').show().focus();						
                		$('.suntrust-nav-location').hide();
                	})
                	$(document).on('click','[data-suntrust-class="suntrust-nav-location"]',function(){
                		$('.suntrust-modal-window,.suntrust-nav-location').show().focus();						
                		$('.suntrust-nav-search').hide();
                	})
                	$(document).on('click','[data-suntrust-class="suntrust-modal-close"]',function(){
                		$(this).parents('.suntrust-modal-window').hide();
                        $(this).parents('.suntrust-modal-window').find('[type="search"]').typeahead('val','');
                        $(this).parents('.suntrust-modal-window').find('[data-sun-class="search-cancel-button"]').removeClass("sun-active");
						if($(this).parents(".suntrust-nav-search").is(".suntrust-nav-search"))
						{
							$('[data-suntrust-class="suntrust-nav-search"]').focus();
						}
						else
						{
							$('[data-suntrust-class="suntrust-nav-location"]').focus();
						}

                	})
                	/*$(document).on('focusout','.suntrust-modal-window .suntrust-orange-button',function(){
	                	$('[data-suntrust-class="suntrust-modal-close"]').trigger('click');
	                });*/
                /* Search text clear */

				searchTxt = $(".suntrust-nav-search,.suntrust-nav-location,.search_text_box_reset");
				searchTxt.find('[data-sun-type="search-cancel-button"]').click(
					function(e) {
						var tmpClass = $(this);
						e.preventDefault(),tmpClass.removeClass("sun-active"),
						tmpClass.prev().find("input").typeahead('val','').focus(),
						$(".suntrust-autocomplete").empty();
					}),
				searchTxt.find('[type="search"]').keyup(
					function() {
						var txtKeyUp = $(this), $currentCancelButton=$(this).parent().next(),
						txtLength = txtKeyUp.val().length;
						txtLength > 0 ? $currentCancelButton.addClass("sun-active"): $currentCancelButton.removeClass("sun-active");
					});

					/*Header Modal Window End*/

					/* Header Global Navigation start here */
					$(document)
					.on(
						"focus hover mouseenter",
						/*
						 * ADA fix give
						 * add "focus
						 * with
						 * mouseenter
						 */
						".suntrust-subMenu li.suntrust-subMenuList",
						function() {
							/*console
									.log("Loading in ie on mouse over");*/
							$(".suntrust-overlayContent").addClass(
								"hide");
							$(".suntrust-topArrow")
								.addClass("hide");
							$(this).find(
									".suntrust-overlayContent")
								.removeClass("hide");
							$(this).find(
									".suntrust-topArrow")
								.removeClass("hide");
							if($(this).find('.col-lg-12 .col-lg-3').length==3){
								   $(this).find('.suntrust-emptydiv .col-lg-3:first-child').css('margin-left','135px');
							}
							else{
								   $(this).find('.suntrust-emptydiv .col-lg-3:first-child').css('margin-left','0px');
							}
							/*if ($(this)
								.find(
									".suntrust-column-width").length == 0) {
								$(this).find(
										".suntrust-overlayContent")
									.addClass("hide");
								$(this).find(
										".suntrust-topArrow")
									.addClass("hide");
							}
							var dropWidth = $(this).find(
									'.suntrust-overlayItem')
								.outerWidth();
							$(this)
								.find(
									'.suntrust-overlayContent')
								.css({
									'width': dropWidth
								});
							var left = $(this).offset().left;
							var lposition;
                            if (dropWidth > 1100) {
                                if (window.innerWidth >= 1450) {
                                    lposition = ((1170-dropWidth)+left)-285;
                                }
                                else{
                                    lposition = ((1170-dropWidth)+left)-115;
                                }	
                                $(this).find(".suntrust-overlayContent").css({'left' : -(lposition)});
                            }
                            else{
                                if (dropWidth < 630) {
                                    if(left>750)
                                    {
                                        lposition = 'auto';
                                    }
                                    else{
                                        lposition = 0;
                                    }
                                }
                                else {
                                	if(left>750)
                					{
                						lposition = 'auto';
                					}
                					else{
                						lposition = (((1170 - dropWidth) + left) / 2)-125;
                					}
                                }
                                $(this).find(".suntrust-overlayContent").css({'left' : -(lposition)});
                            }*/
							$(".suntrust-comboBoxloans").blur();
						});
						$(document).on(
							"mouseleave",
							".suntrust-subMenu li.suntrust-subMenuList",
							function() {
								$(this).children().next(
									".suntrust-overlayContent").addClass(
									"hide");
								$(this).children().next(".suntrust-topArrow")
									.addClass("hide");
							});
					/* ADA FIX IN SCRIPT */
					/* Esc Code */
					$(document).keyup(function(e) {
						if (e.keyCode === 27) {
							$(".suntrust-overlayContent").addClass("hide");
							$(".suntrust-topArrow").addClass("hide");
						}
					});
					/* Esc Code */

					$('.suntrust-overlayContent').on('focus', 'h4', function() {
						$(this).removeClass("active");
						$(this).addClass("active");
						$(this).prev().removeClass("active");
						$(this).next().children().removeClass("listactive");
					}).on(
							'blur',
							'h4',
							function(e) {
								$(this).removeClass("active");
								$(".suntrust-headingText").next().children()
										.removeClass("listactive");
							}).on('keydown', 'h4', function(e) {
						$this = $(this);
						if (e.keyCode == 40) {
							$this.next().focus();
							return false;
						} else if (e.keyCode == 38) {
							$this.prev().focus();
							return false;
						}
					});

					$('.suntrust-overlayContent').on('focus', 'li', function() {

						$(this).removeClass("listactive");
						$(":last-child").removeClass("listactive");
						$(this).prev().removeClass("listactive");
						$(this).next().removeClass("listactive");
						$(this).addClass("listactive");
						$(this).parent().prev().removeClass("active");
						$(this).parent().next().removeClass("active");
					}).on(
							'blur',
							'li',
							function(e) {
								$(this).removeClass("active");
								$(".suntrust-headingText").next().children()
										.removeClass("listactive");
							}).on('keydown', 'li', function(e) {

						$this = $(this);
						if (e.keyCode == 40) {
							$this.next().focus();
							return false;
						} else if (e.keyCode == 38) {
							$this.prev().focus();
							return false;
						}
					});
					var lastchild = $(
							".suntrust-overlayContent .suntrust-column-width")
							.last();
					var lastchildheading = lastchild.children("h4").last();
					var lastchildlist = lastchild.children("ul").children()
							.last();
					if (lastchildheading.siblings().children().size() === 0) {
						lastchildheading.on('focusout', function() {
							$(".suntrust-overlayContent").addClass("hide");
							$(".suntrust-topArrow").addClass("hide");
						});
					} else {
						lastchildlist.on('focusout', function() {
							$(".suntrust-overlayContent").addClass("hide");
							$(".suntrust-topArrow").addClass("hide");
						});
					}
					var firstchildmenu = $(
							".suntrust-subMenuList:first-child a").first();
					firstchildmenu.keydown(function(e) {

						if (e.shiftKey && e.keyCode == 9) {
							$(".suntrust-overlayContent").addClass("hide");
							$(".suntrust-topArrow").addClass("hide");
						}

					});
					var firstchildheading = $(".suntrust-headingText");
					firstchildheading.keydown(function(e) {
						if (e.shiftKey && e.keyCode == 9) {
							$(this).removeClass("active");
						}
					});
					/* ADA FIX IN SCRIPT */

					$("html").on("click", function() {
						$(".suntrust-overlayContent").addClass("hide");
						$(".suntrust-topArrow").addClass("hide");
						
						/**** Video Full screen Mode Ipad Fix Starts****/
						if (is_iPad) {
                            if (isSafari)
							{
                                if($(".rowcontainerresp .video").is(":visible"))
                                {
                                    setTimeout(function(){
										var videomode= $('.s7innercontainer').attr('mode');
										if(videomode=="fullscreen")
										{
											$(".suntrust-wrapperContainer, .suntrust-rowContainer").css({
												"overflow" : "visible"
											});
										}
										 else
										{
											$(".suntrust-wrapperContainer, .suntrust-rowContainer").css({
												"overflow" : "hidden"
											});
										}
										videomode="";
                                    },500);
                                }
                            }
                        }
						/**** Video Full screen Mode Ipad Fix End****/
					});

					$(document)
							.on(
									"click",
									".suntrust-overlayContent .suntrust-overlayItem ul li",
									function() {
										/* alert("L3 item clicked!") */
										$(
												".suntrust-overlayContent .suntrust-overlayItem ul li")
												.removeClass(
														"suntrust-selected");
										$(this).addClass("suntrust-selected");
										$(".suntrust-subMenuList").removeClass(
												"active");
									});

					$(document)
							.on(
									"click",
									".suntrust-overlayContent .suntrust-overlayItem .suntrust-headingText",
									function() {
										$(
												".suntrust-overlayContent .suntrust-overlayItem .suntrust-headingText")
												.removeClass(
														"suntrust-selected");
										$(this).addClass("suntrust-selected");
										$(".suntrust-subMenuList").removeClass(
												"active");
									});

					$(document)
							.on(
									"click",
									".suntrust-subMenu li.suntrust-subMenuList",
									function() {
										window.location = $(this).find(
												"a.suntrust-subMenuanchor")
												.first().attr("href");
										$(".suntrust-subMenu li").removeClass(
												"active");
										$(this).addClass("active");
										$(".suntrust-overlayContent").addClass(
												"hide");
										$(".suntrust-activeList").show();
									});

					$(document)
							.on(
									"click",
									".suntrust-levelHolder .suntrust-levelBodysection .suntrust-levelBodyItems.suntrust-levelOne li",
									function() {
										$(
												".suntrust-levelHolder .suntrust-levelBodysection .suntrust-levelBodyItems.suntrust-levelOne li")
												.removeClass("selected");
										$(this).parent().hide();
										// $(this).parents().find(".suntrust-levelBodysection").children().eq(1).show();
										$(this).addClass("selected");
									});
					$(document).on(
							"click",
							".suntrust-levelTwo .suntrust-viewBack",
							function() {
								$(this).parent().hide();
								$(this).parents().find(
										".suntrust-levelBodysection")
										.children().eq(0).show();
							});
					$(document)
							.on(
									"click",
									".suntrust-levelHolder .suntrust-levelBodysection .suntrust-levelBodyItems.suntrust-levelTwo li",
									function() {
										$(
												".suntrust-levelHolder .suntrust-levelBodysection .suntrust-levelBodyItems.suntrust-levelTwo li")
												.removeClass("selected");
										$(this).parent().hide();
										// $(this).parents().find(".suntrust-levelBodysection").children().eq(2).show();
										$(this).addClass("selected");
									});
					/*
					 * $(document).on("click",".suntrust-levelThree
					 * .suntrust-viewBack",function(){ $(this).parent().hide();
					 * $(this).parents().find(".suntrust-levelBodysection").children().eq(1).show();
					 * });
					 */					

					// Global header nav script Ends//

					//Section Feature Width Set Start
					$(window).resize(function() {
                        $('ul.suntrust-dynamic-width').each(function (){
                            var maxHeight = -1;
                            var count = $(this).find("li").length;
                            var thisvariable = $(this);
                            suntrustdynamicwidth(count,thisvariable,maxHeight);
                        });
                        $('ul.suntrust-feature-list-details.suntrust-four-col-info').each(function (){
                            var thisvariable = $(this);
                            var maxHeight = -1;
                            var count = $(this).find("li").length;
                            setTimeout(function ()
                            {
                                suntrustfourcolwidth(count,thisvariable, maxHeight);
                            }, 400);
                        });

					});
                    $('ul.suntrust-dynamic-width').each(function (){
                        var maxHeight = -1;
                        var count = $(this).find("li").length;
                        var thisvariable = $(this);
                        suntrustdynamicwidth(count,thisvariable,maxHeight);
                    });
                    $('ul.suntrust-feature-list-details.suntrust-four-col-info').each(function (){
                        var thisvariable = $(this);
                        var maxHeight = -1;
                        var count = $(this).find("li").length;
                        setTimeout(function ()
                        {
                            suntrustfourcolwidth(count,thisvariable, maxHeight);
                        }, 400);
                    });
                    //Section Feature Width Set End
					
					/* START -- Script for adding COACH MARK in all the pages. */	
					if($("body").height()<=$(window).height())
                    {
                        $('.scroll-arrow-indicator').hide();
                    }
					var scrollLen = 250; //550
					if ($("#scroll-indicator").length > 0) {
						$(document).on('click',"#scroll-indicator",function (event) {
							event.preventDefault();
							if (($(this).data('isClicked') == 'undefined') || !$(this).data('isClicked')) {
								var link = $(this);
								link.data('isClicked', true);
								//console.log('link = ' + link.data('isClicked'));
							//Actual animation start
							$('html, body').animate({
								scrollTop: '+=' + $(window).height() // Content scroll for both portrait and landscape view
							}, 1000);
							setTimeout(function () {
								link.removeData('isClicked')
							}, 1000);
							setTimeout(function () {
								if (document.body.scrollTop + scrollLen > document.body.scrollHeight) {
									//hide the scroll graphic
									$('#scroll-indicator').hide();
								} else {
									//console.log('top = ' + document.body.scrollTop + ' height = ' + document.body.scrollHeight);
								}                
							}, scrollLen);
								//Actual animation end
						}        
						});
					}
					$(window).scroll(function () {
						if (document.body.scrollTop + scrollLen < document.body.scrollHeight) {
							$('.scroll-arrow-indicator').show();
						}
						// condition to remove the coach mark when it reaches the end of the page
						if((window.innerHeight+window.scrollY)>=document.body.offsetHeight){
							$('.scroll-arrow-indicator').hide();
							//console.log('window='+$(window).height());
							//console.log('scroll height='+($(window).scrollTop()+$(window).height()))
							//console.log('document='+$(document).height());
						}
					});
					/* END -- Script for adding COACH MARK in all the pages. */

					// Waive Fee Script Start
					$('.suntrust-waive-component-vertical')
							.each(
									function() {
										var waive_len = $(this)
												.find(
														'.sun-bodytext-conjunction_item').length;
										if (waive_len == 2) {
											if (window.innerWidth > 600) {
												$(this)
														.find(
																'.sun-bodytext-conjunction_item')
														.each(function() {
															$(this).css({
																'width' : '47%'
															});
														});
											}
										}
									});
					$(window).resize(function() {
						$('.suntrust-waive-component-vertical')
						.each(
								function() {
									var waive_len = $(this)
											.find(
													'.sun-bodytext-conjunction_item').length;
									if (waive_len == 2) {
										if (window.innerWidth > 600) {
											$(this)
													.find(
															'.sun-bodytext-conjunction_item')
													.each(function() {
														$(this).css({
															'width' : '47%'
														});
													});
										}
										else {
											$(this)
												.find(
														'.sun-bodytext-conjunction_item')
												.each(function() {
													$(this).css({
														'width' : '100%'
													});
												});
                                    }
										
									}									
								});
					});
					// Waive Fee Script End

					// Step components Script Start
					$('.suntrust-stepsContainer ul').each(function() {
						var count = $(this).find("li").length;
						var width;
						if (window.innerWidth > 767) {
							if (count >= 2 && count < 4) {
								width = (100 / count);
							} else if (count == 1) {
								width = 100;
							}
							$(this).find("li").css("width", width + "%");
						}
					});
					$(window).resize(function() {
						$('.suntrust-stepsContainer ul').each(function() {
							var count = $(this).find("li").length;
							var width;
							if (window.innerWidth > 767) {
								if (count >= 2 && count < 4) {
									width = (100 / count);
								} else if (count == 1) {
									width = 100;
								}
								$(this).find("li").css("width", width + "%");
							}
							else
							{
								$(this).find("li").removeAttr("style");
							}
						});
					});
					// Step components Script End

					/* Find Us Script start */

					$(".find_search").click(
							function() {

								var $findUsInpElement = $(this).closest(
										".find_input").find(
										".suntrust_find_input");
								var findUsInpVal = $findUsInpElement.val();
								if (findUsInpVal.trim() == "") {
									$findUsInpElement.addClass("no-valid");
								} else {
									$findUsInpElement.removeClass("no-valid");
								}
							})
					/* Find Us Script end */

					/*
					 * Remove suntrust-invoca-hide class script
					 * start
					 */

					setTimeout(function() {
						if (($("span, div").hasClass("suntrust-invoca-hide"))) {
							
							$("span.suntrust-invoca-hide, div.suntrust-invoca-hide").removeClass("suntrust-invoca-hide");
						}
						
					}, 1800);				

					/**
					 * ********************** Featuredcontresp script START
					 * *************************
					 */
					$("div.featuredcontresp").last().addClass("bottomMargin");
					$("div.featuredcontresp").first().addClass("topMargin");
					/**
					 * ********************** Featuredcontresp script ENDS
					 * *************************
					 */

					$("header.navbar-fixed-top").autoHidingNavbar({'showOnBottom': false});
					$('.suntrust-search-form').on("submit", function(e) {
						e.preventDefault();
					});

					/*
					 * $('input[name=onlinecashmanager]') .click( function() {
					 * var inputValue = $(
					 * 'input[name=onlinecashmanager]:checked') .val()
					 * $(".suntrust-remember-me-field") .toggle(inputValue ==
					 * 'onlineB'); });
					 */
					
					/* Dynamic List Load More Function Start */
					$(".component-dynamic-summarylist").each(function() {
						var size_li = $(this).find('li').length;
						var x = parseInt($(this).find('.load-more button').attr("data-visible-items-limit"));
						$(this).find('li:lt(' + x + ')').show();
						if (size_li <= x) {
							$(this).find('.load-more').hide();
						}
					});

					$('.suntrust-load-more').click(function() {
						var x1 = parseInt($(this).attr("data-visible-items-limit"));
						var x = $(this).parents('.component-dynamic-summarylist').find('li:visible').length;
						var size_li = $(this).parents('.component-dynamic-summarylist').find('li').length;
						x = (x + x1 <= size_li) ? x + x1 : size_li;
						$(this).parents('.component-dynamic-summarylist').find('li:lt(' + x + ')').show();
						if (x >= size_li) {
							$(this).parent('.load-more').hide();
						}						
					});
					/* Dynamic List Load More Function End */

					/* Video Transcript Accordion */
					var $videoAccordionshow = $("#showTranscript .suntrust-video-transcript-accordion");
					var $videoAccordionShowThis,$videoAccordionhideThis;
					function videoAccordionshow(){		
						$videoAccordionShowThis.parent('#showTranscript').next("#hideTranscript").show();
						$videoAccordionShowThis.parent('#showTranscript').next("#hideTranscript").find(".suntrust-video-accordion-section").show()
						$videoAccordionShowThis.parent('#showTranscript').next("#hideTranscript").find(".suntrust-video-transcript-accordion").addClass("active");
						$videoAccordionShowThis.parent("#showTranscript").hide();
					}
					function videoAccordionhide(){
						$videoAccordionhideThis.parent('#hideTranscript').prev("#showTranscript").show();
						$videoAccordionhideThis.parent('#hideTranscript').hide();
						$videoAccordionhideThis.parent('#hideTranscript').find(".suntrust-video-accordion-section").hide();
						$videoAccordionShowThis.parent('#hideTranscript').find(".suntrust-video-transcript-accordion").removeClass("active");
					}
					$videoAccordionshow.on('click',function() {
						$videoAccordionShowThis = $(this);
						videoAccordionshow($videoAccordionShowThis);		
					});
					var $videoAccordionhide = $("#hideTranscript .suntrust-video-transcript-accordion");
					$videoAccordionhide.on('click', function() {
						$videoAccordionhideThis = $(this);
						videoAccordionhide($videoAccordionhideThis);
					});

					$videoAccordionshow.on('keypress',function(e) {
						if (e.which == 13) {
							$videoAccordionShowThis = $(this);
							videoAccordionshow($videoAccordionShowThis);
						}
					});
					$videoAccordionhide.on('keypress',function(e) {
						if (e.which == 13) {
							$videoAccordionhideThis = $(this);
							videoAccordionhide($videoAccordionhideThis);
						}
					});
					/* Video Transcript Accordion */

					/* Audio Transcript Accordion */					
					var $audioAccordionShow = $('#showAudioTranscript .suntrust-audio-transcript-accordion');
					var $audioAccordionShowThis,$audioAccordionhideThis;
					$audioAccordionShow.on('click',function()
					{
						$audioAccordionShowThis = $(this);
						audioAccordionshow($audioAccordionShowThis);
										
					});
					
					$audioAccordionShow.on('keypress',function(e) 
					{
						if (e.which == 13) {
							$audioAccordionShowThis = $(this);
							audioAccordionshow($audioAccordionShowThis);
						}
					});
					
					var $audioAccordionhide = $('#hideAudioTranscript .suntrust-audio-transcript-accordion');
					$audioAccordionhide.on('click',function() {
						$audioAccordionhideThis = $(this);
						audioAccordionhide($audioAccordionhideThis);
					});
					
					$audioAccordionhide.on('keypress',function(e)
					{
						if (e.which == 13) {
							$audioAccordionhideThis = $(this);
							audioAccordionhide($audioAccordionhideThis);
						}
					});
					function audioAccordionshow(){
						$audioAccordionShowThis.parent('#showAudioTranscript').next("#hideAudioTranscript").show();
						$audioAccordionShowThis.parent('#showAudioTranscript').next(
								"#hideAudioTranscript").find(".suntrust-audio-accordion-section")
								.show();
						$audioAccordionShowThis.parent('#showAudioTranscript').next(
								"#hideAudioTranscript").find(".suntrust-audio-transcript-accordion")
								.addClass("active");
						$audioAccordionShowThis.parent("#showAudioTranscript").hide();
					}
					function audioAccordionhide(){
						$audioAccordionhideThis.parent('#hideAudioTranscript').prev("#showAudioTranscript").show();
						$audioAccordionhideThis.parent('#hideAudioTranscript').prev("#showAudioTranscript").find(".suntrust-audio-accordion-section")
								.show();
						$audioAccordionhideThis.parent('#hideAudioTranscript').hide();
					}
				/* Audio Transcript Accordion */					

					/** Contact us script * */
					if ($(".contactus").is(':visible')) {
						var mailheadingtext = $(".suntrust-mailPara img").attr(
								"aria-label");
						if (mailheadingtext != null
								&& mailheadingtext != undefined) {
							mailheadingtext = mailheadingtext.toLowerCase();
						}
						if (mailheadingtext === "mail") {
							$(".suntrust-mailQuestions").next().addClass(
									"suntrust-mailAddress");
						} else {
							$(".suntrust-mailQuestions").next().removeClass(
									"suntrust-mailAddress").addClass(
									"suntrust-mailWithout");
						}
					}
					/** Contact us script * */
					
				/***Start of the sunScriptfun Function***/	
				function sunScriptfun() {
				
					/*Open Left Side of the User Menu Start*/
					$(document).on('click',
							'[data-suntrust-class="suntrust-menu-trigger"]',
							function() {
								animateUserMenuOut();
								$(".suntrust-levelHolder").show().animate({
									left : "0px"
								});
								setTimeout(function() {
									$('body').addClass(noScrollbars);
								}, 350);
							});
					//Close
					$(document).on("click", ".suntrust-onclose", function() {
						$(".suntrust-levelHolder").animate({
							left : "-320px"
						});
						$('body').removeClass(noScrollbars);
					});
					/*Open Left Side of the User Menu End*/
					
					$('[data-suntrust-class="header"] select, .suntrust-main-content select, .suntrust-select,.suntrust-bank-segment ')
							.uniform({
								selectClass : 'sun-select-container',
								selectAutoWidth : false
							}).each(
									function() {
										$(this).siblings("span").attr(
												"aria-hidden", true);
									});
				
					/** Script for sign on OLB/OCM starts * */
					function serviceContent() {

						var option = $('select.suntrust-bank-segment').val();

						if (option == "signonblade-OLB") {
							$(
									'#suntrust-login-form .suntrust-login-onlinebanking-form,.online-banking')
									.removeClass('hidden');
							$(
									'#suntrust-login-form .suntrust-login-onlinecashmanager-form,.online-cash-manager')
									.addClass('hidden');
						} else if (option == "signonblade-OCM") {
							$(
									'#suntrust-login-form .suntrust-login-onlinecashmanager-form,.online-cash-manager')
									.removeClass('hidden');
							$(
									'#suntrust-login-form .suntrust-login-onlinebanking-form,.online-banking')
									.addClass('hidden');
						}
					}
					serviceContent();
					$('select.suntrust-bank-segment').change(function() {
						serviceContent();
					});

					/* Hero Signon starts */
					var $sunMainHero = $('div[data-suntrust-class="suntrust-main"]');
					if ($sunMainHero.length > 0) {
						var $sunPageHero = $('#suntrust-page');
						var $loginFormHero = $('#suntrust-login-form-herosignon');
						var hasBannerHero = false;
						// Detect IE8
						var ie8;
						if ($('html').hasClass('ie8')
								|| $('body').hasClass('ie8')) {
							ie8 = true;
						} else {
							ie8 = false;
						}

						// select
						$.uniform.update('#segment');

						// Sign on cookies
						var signOnCookiesHero = {

							olbCookie : get_cookieNoUnescape('OLBRMdata'),
							olbMaskedValue : '',
							olbEncryptedValue : '',
							olbValid : false,
							optionCookie : getCookie('HeroSignOnOption'),
							optionValid : false,
							olbIsEncrypted : function() {
								if (signOnCookiesHero.olbCookie != null
										&& signOnCookiesHero.olbCookie != ''
										&& signOnCookiesHero.olbEncryptedValue != ''
										&& signOnCookiesHero.olbMaskedValue != '') {
									return true;
								}
								return false;
							}
						};

						// Sign On
						var $bankSegmentSelectHero = $loginFormHero
								.find('input.suntrust-bank-segment-herosignon-radio');

						if ($bankSegmentSelectHero.length > 0) {
							if (signOnCookiesHero.optionCookie == null
									|| signOnCookiesHero.optionCookie == '') {
								console.log("1");
								$bankSegmentSelectHero[0].checked = true;
							} else {
								for (i = 0; i < $bankSegmentSelectHero.length; i++) {
									if ($bankSegmentSelectHero[i].id == signOnCookiesHero.optionCookie) {
										console.log("2");
										$bankSegmentSelectHero[i].checked = true;
										break;
									} else {
										console.log("3");
										$bankSegmentSelectHero[0].checked = true;
									}
								}
							}

							// Set the selected option
							if (signOnCookiesHero.optionCookie != null
									&& signOnCookiesHero.optionCookie != '') {
								var appId = signOnCookiesHero.optionCookie;
								var split_appid = appId.split('-');

								var $formHero = $loginFormHero.find('#SignOn-'
										+ appId);
								var $otherServicesHero = $('.suntrust-more-services-wrapper[id=OtherServices-signonblade-'
										+ split_appid[1] + ']');
								var $mobileAppsHero = $('.suntrust-app-banner-wrapper[id=MobileApps-'
										+ appId + ']');

								if ($formHero.length > 0) {
									$loginFormHero.find(
											'div.suntrust-signon-login-form')
											.addClass('hidden-hero-signon');
									$formHero.removeClass('hidden-hero-signon');

									$('div.suntrust-more-services-wrapper')
											.addClass('hidden-hero-signon');
									$otherServicesHero
											.removeClass('hidden-hero-signon');

									$('div.suntrust-app-banner-wrapper')
											.addClass('hidden-hero-signon');
									$mobileAppsHero
											.removeClass('hidden-hero-signon');

									$bankSegmentSelectHero.val(appId);
								}
							}
							$.uniform.update($bankSegmentSelectHero);
							// Set the OLB form values based on olbCookie
							if (signOnCookiesHero.olbCookie != null
									&& signOnCookiesHero.olbCookie != '') {
								var cookieValuesHero = signOnCookiesHero.olbCookie
										.replace('maskedValue=', '').replace(
												'encryptedValue=', '').split(
												'&');
								if (cookieValuesHero.length = 2) {
									signOnCookiesHero.olbMaskedValue = cookieValuesHero[0];
									signOnCookiesHero.olbEncryptedValue = cookieValuesHero[1];
									if (signOnCookiesHero.olbIsEncrypted()) {
										signOnCookiesHero.olbValid = true;
										// Only forms of OLB
										var formsHero = $(".signOnLoginForm[data-apptype='OLB']");
										var currentFormHero = $(".signOnLoginForm[data-apptype='OLB'][data-appid='"
												+ signOnCookiesHero.optionCookie
												+ "']");
										var textboxHero = formsHero
												.find('.suntrust-login-user-input');
										if (signOnCookiesHero.optionValid
												&& signOnCookiesHero.optionCookie == 'OLB') {
											$(function() {
												var passFieldHero = currentFormHero
														.find('.suntrust-login-password-herosignon');
												passFieldHero.siblings('label')
														.hide();
											});
										}
										textboxHero
												.val(signOnCookiesHero.olbMaskedValue);
										textboxHero
												.siblings(
														'.suntrust-login-hidden-user-id')
												.val(
														signOnCookiesHero.olbEncryptedValue);
										textboxHero
												.siblings(
														'.suntrust-login-input-is-encrypted')
												.val('true');
										textboxHero.siblings('label').hide();
										var checkboxHero = formsHero
												.find('.suntrust-login-checkbox');
										if (checkboxHero.length > 0) {
											checkboxHero.attr("checked", true);
											$.uniform.update(checkboxHero);
										}
									}
								}
							}
							// Input click event selects text
							$loginFormHero.find('.suntrust-login-input').click(
									function() {
										$(this).select();
									});

							// Select
							$bankSegmentSelectHero
									.change(function() {
										var appId = this.id;
										var split_appid = appId.split('-');
										var $formHero = $loginFormHero
												.find('#SignOn-' + appId);
										var $otherServicesHero = $('.suntrust-more-services-wrapper[id=OtherServices-signonblade-'
												+ split_appid[1] + ']');
										var $mobileAppsHero = $('.suntrust-app-banner-wrapper[id=MobileApps-'
												+ appId + ']');

										if ($formHero.length > 0) {
											$loginFormHero
													.find(
															'div.suntrust-signon-login-form')
													.addClass(
															'hidden-hero-signon');
											$formHero
													.removeClass('hidden-hero-signon');

											$(
													'div.suntrust-more-services-wrapper')
													.addClass(
															'hidden-hero-signon');
											$otherServicesHero
													.removeClass('hidden-hero-signon');

											$('div.suntrust-app-banner-wrapper')
													.addClass(
															'hidden-hero-signon');
											$mobileAppsHero
													.removeClass('hidden-hero-signon');
										}
										if (ie8) {
											// Fix webfont icon issues on IE8
											var head = document
													.getElementsByTagName('head')[0], style = document
													.createElement('style');
											style.type = 'text/css';
											style.styleSheet.cssText = ':before,:after{content:none !important';
											head.appendChild(style);
											setTimeout(function() {
												head.removeChild(style);
											}, 0);
										}
									});

							var $bankSegmentSubmitButtonsHero = $loginFormHero
									.find('.suntrust-login-button-herosignon');
							// Submit
							$bankSegmentSubmitButtonsHero
									.click(function(event) {
										event.preventDefault();
										$formHero = $(this).closest(
												'form.signOnLoginForm');
										var valid = true;
										$formHero.find('.suntrust-login-input')
												.removeClass('sun-error');
										$formHero
												.find('.suntrust-login-input')
												.each(
														function() {
															var input = $(this);
															if ($.trim(input
																	.val()).length == 0
																	|| (input
																			.is('.suntrust-login-user-input') && $
																			.trim(input
																					.val()) == input
																			.attr('placeholder'))) {
																input
																		.addClass('sun-error');
																valid = false;
															}
														});
										if (valid) {
											var cookieConfig = {
												expire : 30, // 30days
												secure : window.location.protocol == "https:" ? true
														: false,
												domain : null,
												path : "/"
											};
											var appId = $formHero.data('appid');
											var isEncryptEnabled = $formHero.data('encrypt');
											if(isEncryptEnabled != undefined && 
													isEncryptEnabled != null && isEncryptEnabled != '') {
												isEncryptEnabled = isEncryptEnabled.toString();
											}
											var appType = $formHero
													.data('apptype');
											var gateway = $formHero
													.data('gateway');
											setCookie('HeroSignOnOption',
													appId, cookieConfig.expire,
													cookieConfig.path,
													cookieConfig.domain,
													cookieConfig.secure);

											// Encrypt password if gateway is
											// 11g and there is a public key
											var password = $formHero
													.find('.suntrust-login-password-herosignon');
											var userId = $formHero
													.find('.suntrust-login-user-input');
											var isUserIdEnc = $formHero
													.find('.suntrust-login-input-is-encrypted');
											
											var hiddenPublicKey = password
													.siblings('.hiddenPublicKey');
											if (gateway == '11g'
													&& hiddenPublicKey.length > 0) {
												var publicKey = KEYUTIL
														.getRSAKeyFromPublicPKCS8PEM(hiddenPublicKey
																.val());
												var encryptedPass = publicKey
														.encrypt(password.val());
												encryptedPass = hex2b64(encryptedPass);

												password.siblings(
														'.hiddenPassword').val(
														encryptedPass);
												/*US60890-Oauth to address vulnerability scan - OLB changes in the login flow-start*/
												if(isUserIdEnc.val() == 'true') {
													if(isEncryptEnabled == 'true') {
                                                    	if(userId.val()!=signOnCookiesHero.olbMaskedValue) {
															var encryptedUserId = publicKey
															.encrypt(userId.val());
															encryptedUserId = hex2b64(encryptedUserId);
															userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																encryptedUserId);
	                                                    } else {
	                                                        userId.siblings(
	                                                            '.suntrust-login-hidden-user-id').val(
    	                                                        signOnCookiesHero.olbEncryptedValue);
	                                                    }
													} else {
														if(userId.val()!=signOnCookiesHero.olbMaskedValue) {
															userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																userId.val());
	                                                        userId.siblings(
															'.suntrust-login-input-is-encrypted').val('false');
                                                    	} else {
                                                        	userId.siblings(
                                                            	'.suntrust-login-hidden-user-id').val(
                                                            	signOnCookiesHero.olbEncryptedValue);
                                                    	}
													}
												} else if(isUserIdEnc.val() == 'false') {
													if(userId.val()!=signOnCookiesHero.olbMaskedValue) {
														userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																userId.val());
													} else {
														var encryptedUserId = publicKey
														.encrypt(userId.val());
														encryptedUserId = hex2b64(encryptedUserId);
														userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																encryptedUserId);
													}
												}
												/*US60890-Oauth to address vulnerability scan - OLB changes in the login flow-end*/
											} else {
												password.siblings(
														'.hiddenPassword').val(
														password.val());
											}

											var textboxHero = $formHero
													.find('.suntrust-login-user-input');
											// dont have encrypted value in
											// cookie or masked user id has
											// changed
											if (isUserIdEnc.val() == 'true' && (signOnCookiesHero.olbCookie != null
													&& signOnCookiesHero.olbCookie != '') && textboxHero
                                               .val() == signOnCookiesHero.olbMaskedValue) {
												$('form[name="herosignon-OLB"] input[name="rmUsernameSet"]').val("true");
											}
											if(appType != 'OLB') {
												textboxHero
														.siblings(
																'.suntrust-login-hidden-user-id')
														.val(textboxHero.val());
												textboxHero
														.siblings(
																'.suntrust-login-input-is-encrypted')
														.val('false');
											}
											// If checkbox exists and is
											// unchecked, delete the cookie
											var rememberCheck = $formHero
													.find('.suntrust-login-checkbox');
											if (rememberCheck.length > 0
													&& appType == 'OLB') {
												var rememberHidden = rememberCheck
														.closest(
																'.suntrust-remember-me-field')
														.find(
																'.suntrust-login-checkbox-hidden');
												if (rememberCheck
														.is(':checked')) { // for
													// olb
													// remove
													// cookie
													rememberHidden.val('true');
												} else {
													rememberHidden.val('false');
													delete_cookie('OLBRMdata');
												}
											}
											$formHero.submit();
										}
									});

							$(document).on('keypress','#SignOn-herosignon-OLB .suntrust-login-input, #SignOn-herosignon-OCM .suntrust-login-input',function(e){
								if(e.which==13)
								{
									$bankSegmentSubmitButtonsHero.trigger('click');
								}
							});
						}
					}
					/* Hero Signon ends */

					/* Init scripts - uniform starts */
					var suntrustPage = {
						init : function(settings) {
							suntrustPage.config = {
								isIE8 : $('body').hasClass('ie8'),
								isLtIE10 : $('body').hasClass('lt-ie10'),
								throttleTime : 250,
								calcFormDataSet : {
									dataElements : [ 'PropertyPrice',
											'PropertyDownPayment',
											'CurrentValueOfHome',
											'AmountToRefinance' ],
									PropertyPrice : {
										inputDefault : 0,
										min : 0,
										max : 5000000,
										decPoints : 2
									},
									PropertyDownPayment : {
										inputDefault : 0,
										min : 0,
										max : 10000000,
										decPoints : 2
									},
									CurrentValueOfHome : {
										inputDefault : 0,
										min : 0,
										max : 10000000,
										decPoints : 2
									},
									AmountToRefinance : {
										inputDefault : 0,
										min : 0,
										max : 100000000,
										decPoints : 2
									}
								}
							};
							// Reload icons for IE8
							if (suntrustPage.config.isIE8) {
								suntrustPage.reloadIcons();
							}
							suntrustPage.initSkipNavigation();
							var header = $('#container-header');
							if (header.length > 0
									&& header.hasClass('sun-header')) {
								suntrustPage.initHeader();
							}
							suntrustPage.initSearch();
							suntrustPage.initUniform();
							suntrustPage.initDropdownCTA();
							suntrustPage.initVideo();
							suntrustPage.initFAQ();
							suntrustPage.initSlider();
							// US:12222
							var width = $(window).width();
							suntrustPage.initGeolocation();
							suntrustPage.initContactUs();

							// Functionality for Load More Button on Summary
							// List/Search Results
							suntrustPage.initLoadMore();
							// Display App CTA functionality
							// Set device detection variable
							if (typeof deviceDetection !== 'undefined') {
								$(deviceDetection.selector).removeClass(
										'sun-global-app-element');
							}

							/*
							 * //Tooltip functionality
							 * $('.sun-tooltip').tooltipster(); //placeholder
							 * $('input, textarea').placeholder(); //Contact box
							 * $('[data-sun-class="mortgage-contact-options-tabs"]').tabs();
							 * //Init the autocomplete functionality
							 * setInputAutocomplete($('.sun-autocomplete-input'));
							 */
							
							// Suntrust External Forms
							$('div.suntrust-embed-form').each(function() {
								suntrustPage.initExternalForm($(this));
							});
							// Calc Form Promo
							$('form.sun-right-rail-promo-calculator-form')
									.each(
											function() {
												suntrustPage
														.initCalculatorPromo($(this));
											});
							// Validation Form
							$('form.suntrust-validation-form').each(function() {
								suntrustPage.initValForm($(this));
							});
							// People Finder Promo
							$('form.suntrust-people-finder-promotion')
									.each(
											function() {
												suntrustPage
														.initPeopleFinderPromo($(this));
											});
							// LOFinderForm Promo
							$('form.sun-right-rail-advisor-promo')
									.each(
											function() {
												suntrustPage
														.initLOFinderPromo($(this));
											});
							// Comparison Chart
							suntrustPage.initComparisonChart();
							// Popup Window
							$('a[data-popup-window]').click(
									function(event) {
										event.preventDefault();
										var link = $(this);
										suntrustPage.openWindow(link
												.attr('href'), link
												.data('popup-window'));
									});
							// Accordion
							suntrustPage.initAccordion();
						},
						reloadIcons : function() {
							// Fix webfont icon issues on IE8
							var head = document.getElementsByTagName('head')[0], style = document
									.createElement('style');
							style.type = 'text/css';
							style.styleSheet.cssText = ':before,:after{content:none !important';
							head.appendChild(style);
							setTimeout(function() {
								head.removeChild(style);
							}, 0);
						},
						initSkipNavigation : function() {
							// Skip navigation
							var skipNavigation = $('#skip-nav');
							skipNavigation.find('.skip-nav-link').focus(
									function() {
										$('#skip-nav').addClass('sun-active');
									});
							skipNavigation.find('.skip-nav-link').blur(
									function() {
										$('#skip-nav')
												.removeClass('sun-active');
									});
						},
						initHeader : function() {
							// Header Actions
							$(
									'[data-sun-class="search-and-mobile-menu-container-toggle"]')
									.on(
											'click',
											$(document),
											function() {
												var $this = $(this), $target = $('[data-sun-class="search-and-mobile-menu-container"]');

												$this.toggleClass('sun-active');
												$target
														.toggleClass('sun-active');
											});

							$(
									'[data-sun-class="sun-header-search-form-toggle"]')
									.on(
											'click',
											$(document),
											function() {
												var $target = $('[data-sun-class="sun-header-search-form"]');
												$target
														.toggleClass('sun-active');
												if (suntrustPage.config.isIE8) {
													suntrustPage.reloadIcons();
												}
											});
							// Toggle Overlay
							$('[data-sun-toggle-overlay-target]')
									.on(
											'click',
											$(document),
											function() {
												var $this = $(this), $target = $this
														.data('sun-toggle-overlay-target'), $toggleOverlay = $('[data-sun-class="toggle-overlay"]');

												$toggleOverlay.addClass(
														'sun-active').data(
														'sun-target', $target);
											});
							$('[data-sun-class="toggle-overlay"]')
									.on(
											'click',
											$(document),
											function() {
												var $this = $(this), $target = $('[data-sun-class="'
														+ $this
																.data('sun-target')
														+ '"]');

												$this.removeClass('sun-active');
												$target
														.removeClass('sun-active');
												if (suntrustPage.config.isIE8) {
													suntrustPage.reloadIcons();
												}
												$(
														'.sun-header-search-cancel-button')
														.removeClass(
																'sun-active');
											});
						},
						initSearch : function() {
							var $currentCancelButton;

							$('[data-sun-type="search-cancel-button"]').on(
									'click', $(document), function(e) {
										var $this = $(this);

										e.preventDefault();
										$this.removeClass('sun-active');
										$this.prev('input').val('').focus();
										$('.suntrust-autocomplete').empty();
									});

							$('[type="search"]')
									.on(
											{
												keyup : function() {
													var $this = $(this), charLength = $this
															.val().length;

													if (charLength > 0) {
														$currentCancelButton
																.addClass('sun-active');
													} else {
														$currentCancelButton
																.removeClass('sun-active');
													}
												},
												focus : function() {
													var $this = $(this);

													$currentCancelButton = $this
															.next('[data-sun-class="search-cancel-button"]');
													$this
															.closest(
																	'.suntrust-header-search-container')
															.addClass(
																	'search-active');
												},
												blur : function() {
													var $this = $(this);

													if ($(this).val().length > 0) {
														return false;
													} else {
														$this
																.closest(
																		'.suntrust-header-search-container')
																.removeClass(
																		'search-active');
													}
												}
											}, $(document));

							if (document.activeElement.attributes.type) {
								if (document.activeElement.attributes.type.value === "search") {
									var $this = $(document.activeElement);

									$currentCancelButton = $('[data-sun-class="search-cancel-button"]');
								}
							}
						},
						initValForm : function(form) {
							form
									.submit(function(event) {
										var valid = true;
										form.find('input').removeClass(
												'no-valid');
										form
												.find('.suntrust-form-required')
												.each(
														function() {
															var input = $(this);
															if ($.trim(input
																	.val()).length == 0) {
																input
																		.addClass('no-valid');
																valid = false;
															}
														});
										if (!valid) {
											event.preventDefault();
										}
									});
						},
						initUniform : function() {
							// Uniform
							$('label.sun-radio-label [type="radio"]').uniform({
								radioClass : 'sun-radio-input-container',
								checkedClass : 'sun-checked'
							});

							$('label.sun-checkbox-label [type="checkbox"]')
									.uniform(
											{
												checkboxClass : 'sun-checkbox-input-container',
												checkedClass : 'sun-checked'
											});

							$('[data-suntrust-class="suntrust-checkbox"]')
									.uniform(
											{
												checkboxClass : 'sun-checkbox-input-container',
												checkedClass : 'sun-checked',
												focusClass : 'sun-focused'
											});

							$('[data-suntrust-class="suntrust-checkbox-alt"]')
									.uniform(
											{
												checkboxClass : 'sun-checkbox-input-container-alt',
												checkedClass : 'sun-checked',
												focusClass : 'sun-focused'
											});

							$(
									'[data-suntrust-class="suntrust-checkbox-tertiary"]')
									.uniform(
											{
												checkboxClass : 'sun-checkbox-input-container-tertiary',
												checkedClass : 'sun-checked'
											});
							$(
									'[data-suntrust-class="suntrust-checkbox-toggle"]')
									.uniform(
											{
												checkboxClass : 'suntrust-checkbox-toggle-container',
												checkedClass : 'sun-checked'
											});
							$('select').uniform({
								selectClass : 'sun-select-container',
								selectAutoWidth : false
							}).each(
									function() {
										$(this).siblings("span").attr(
												"aria-hidden", true);
									});
						},
						initExternalForm : function($form) {

							function initFormCheckFields() {
								var $radioButtons = $form
										.find("[type='radio']");
								$radioButtons.uniform({
									radioClass : 'sun-radio-input-container',
									checkedClass : 'sun-checked',
									focusClass : 'sun-focused'
								});
								var $checkBoxes = $form
										.find("[type='checkbox']");

								$checkBoxes
										.uniform({
											checkboxClass : 'sun-checkbox-input-container',
											checkedClass : 'sun-checked',
											focusClass : 'sun-focused'
										});
							}
							initFormCheckFields();
							if ($form.hasClass('suntrust-salesforce-form')) {
								$(".sun-checkbox-input-container").prev("br")
										.css({
											"display" : "block"
										});
								$(".sun-checkbox-input-container").next("br")
										.css({
											"display" : "block",
											"margin-top" : "5px",
											"margin-bottom" : "5px"
										});
							} else if ($form.hasClass('suntrust-eloqua-form')) {
								$form.find(".sun-checkbox-input-container")
										.closest("p").addClass(
												"suntrust-eloqua-checkbox-row");
							}

						},
						initContactUs : function() {
							// Check for the existence of the loan officer
							// cookie and if present
							// update the contact information phone number with
							// the Loan Officer's
							// phone number
							$('div.component-contactus')
									.each(
											function() {
												var $component = $(this);
												if (Cookies
														.getJSON('LoanOfficer') != null) {
													var loanOfficer = Cookies
															.getJSON('LoanOfficer');
													var phoneNumber = loanOfficer.phone;
													$component
															.find(
																	'a.sun-contact-options-list-item-link.lo-data-persist')
															.each(
																	function() {
																		$(this)
																				.attr(
																						'href',
																						'tel:'
																								+ phoneNumber);
																		$(this)
																				.find(
																						'span')
																				.html(
																						phoneNumber);
																	});

													$component
															.find(
																	'div.sun-contact-options-list-item-text.lo-data-persist')
															.each(
																	function() {
																		$(this)
																				.html(
																						phoneNumber);
																	});
												}
											});
						},
						initDropdownCTA : function() {
							// Lookup Form option select
							$(
									'select.suntrust-lookup-form-selector,select.suntrust-select-account,select.sun-body-select-cta')
									.change(
											function(event) {
												var $option = $(
														'option:selected', this), url = $option
														.val(), overlayClass = $option
														.data('overlay');
												if (url != '') {
													if (overlayClass != ''
															&& overlayClass != undefined) {
														overlayClass = overlayClass
																.replace(
																		'overlay--',
																		'');
														var width = 980;
														switch (overlayClass) {
														case 'small':
															width = 470;
															break;
														case 'medium':
															width = 788;
															break;
														default:
															break;
														}
														$.fancybox({
															href : url,
															maxWidth : width,
															width : width,
															closeClick : false,
															type : 'iframe'
														})
													} else if ($option
															.is('[data-popup-window-params]')) {
														suntrustPage
																.openWindow(
																		url,
																		$option
																				.data('popup-window-params'));
													} else {
														if ($option
																.data('target') == '_blank') {
															var win = window
																	.open(url,
																			'_blank');
															win.focus();
														} else {
															window.location.href = url;
														}
													}
												}
											});
						},
						initVideo : function() {
							// Video Overlay
							$('a[data-overlay-video]')
									.click(
											function(event) {
												event.preventDefault();
												var $link = $(this);
												$
														.ajax({
															type : "POST",
															url : '/Mortgage/GetOverlayVideo',
															data : 'id='
																	+ $link
																			.data('overlay-video'),
															success : function(
																	data) {
																var $data = $(data);

																// Added for
																// VideoOverlay
																// research:
																// US13362
																// US13018
																var width = $(
																		window)
																		.width();
																if (width <= 1400) {
																	$(
																			'#overlayvideo')
																			.html(
																					data)
																			.modalWindow(
																					{
																						size : 'small',
																						$trigger : $link
																					});
																} else {
																	$data
																			.modalWindow({
																				size : 'small',
																				$trigger : $link
																			});
																}
																// End
																speedbumpCheck($data);
																$(
																		'.suntrust-transcript-button')
																		.click(
																				function(
																						event) { // Modified
																									// for
																									// Video
																									// overlay
																									// :
																									// US13018
																					event
																							.preventDefault();
																					var $button = $(this);
																					$button
																							.hide();
																					$button
																							.siblings(
																									'.suntrust-transcript')
																							.show();
																					suntrustPage
																							.centerOverlay();
																				});
															},
															error : function(
																	xhRequest,
																	ErrorText,
																	thrownError) {
																ajaxError();
															}
														});
											});
							// Video Component
							$(".video-container")
									.each(
											function(index, el) {
												var container = $(this);
												if (container.children('table').length > 0) {
													var image = container
															.attr('data-image');
													var imageCopy = '';
													if (image != undefined
															&& image != '') {
														imageCopy = "<img src='"
																+ container
																		.attr('data-image')
																+ "' alt='Alternate image content: Unable to play video due to browser player constraints not meet' /><br/>";
													}
													container
															.html("<span>"
																	+ imageCopy
																	+ "In order to view this video content you need a browser that supports flash  9.0.124 or html5.</span>");
												}
											});
						},
						centerOverlay : function() {
							var $modalWindow = $('.suntrust-modal-container');
							var centeredHeight = $modalWindow.outerHeight()
									/ -2;
							$modalWindow.css({
								'margin-top' : centeredHeight
							});
						},
						initFAQ : function() {
														
						},
						initSlider : function() {
							// Slider
							$('ul.bxslider')
									.each(
											function() {
												var slider = $(this);
												slider
														.bxSlider({
															speed : slider
																	.data('speed'),
															nextText : '<span>Next</span>',
															prevText : '<span>Prev</span>',
															auto : slider
																	.data('lapse') > 0,
															pause : slider
																	.data('lapse'),
															autoHover : true,
															onSliderLoad : function() {
																$(".suntrust-carousel .suntrust-carouselcomponent").css("visibility","visible");
																slider
																		.find(
																				'a')
																		.attr(
																				"tabindex",
																				"-1");
															},
															onSlideAfter : function(
																	$slideElement) {
																if (typeof (LimelightPlayerUtil) != "undefined") {
																	var $parent = $slideElement
																			.find('.video-container');
																	$parent
																			.css(
																					'width',
																					'99.99%');
																	setTimeout(
																			function() {
																				$parent
																						.css(
																								'width',
																								'100%');
																			},
																			0);
																}
															}
														});
											});
						},
						initCalculatorPromo : function(form) {
							form
									.find(
											'.sun-right-rail-promo-calculator-selector')
									.change(
											function() {
												var value = $(this).val();
												if (value == 'newhomebuyer') {
													form
															.addClass('newhomebuyer-active');
												} else {
													form
															.removeClass('newhomebuyer-active');
												}
											});
							form
									.submit(function() {
										var dataElements = suntrustPage.config.calcFormDataSet.dataElements;
										for (var i = 0, l = dataElements.length; i < l; i++) {
											var dataElement = dataElements[i], inputDefault = suntrustPage.config.calcFormDataSet[dataElements[i]].inputDefault, min = suntrustPage.config.calcFormDataSet[dataElements[i]].min, max = suntrustPage.config.calcFormDataSet[dataElements[i]].max, decPoints = suntrustPage.config.calcFormDataSet[dataElements[i]].decPoints;
											var input = form
													.find('input[name="'
															+ dataElement
															+ '"]');
											var inputValue = input.val();
											inputValue = inputValue.replace(
													/[^0-9\.]+/g, '');
											inputValue = parseFloat(inputValue);

											if (!inputValue) {
												input.val(inputDefault
														.toFixed(decPoints));
											} else if (inputValue < min) {
												input.val(min
														.toFixed(decPoints));
											} else if (inputValue > max) {
												input.val(max
														.toFixed(decPoints));
											} else {
												input.val(inputValue
														.toFixed(decPoints));
											}
										}
										if (form
												.hasClass('newhomebuyer-active')) {
											form
													.attr('action',
															'https://lfgs.suntrust.com/solutions/suntrust/goal/newhomebuyer');
										} else {
											form
													.attr('action',
															'https://lfgs.suntrust.com/solutions/suntrust/goal/refinance');
										}
									});
						},
						initGeolocation : function() {
							globalElement.on('click','.suntrust-input-location-button',function(event) {
												var $button = $(this);
												if (navigator.geolocation) {
													navigator.geolocation.getCurrentPosition(function(position) {
																		suntrustPage.showLocation($button,position)
																	},
																	function(ex) {
																		alert('Geolocation produced and error. Code: '
																				+ ex.code
																				+ '. Message: '
																				+ ex.message);
																	});

												} else {
													alert("Geolocation is not supported by this browser.");
												}
											});
						},
						showLocation : function (button, position) {
				               var latitude = position.coords.latitude;
				               var longitude = position.coords.longitude;
				               var $input = button.siblings('span').find('input:nth-child(2)');
				               var address, street, city, state, zip;
				               $.ajax({
				                      type: "POST",
				                      url: 'https://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850&outFormat=json&location='+latitude+','+longitude,
				                      dataType: "json",
				                      success: function (data) {
				                             //console.log("data"+data);
				                             $.each(data.results, function() {
				                               $.each(this, function(key, val){
				                                   if(key != null && key != undefined && key=='locations') {
				                                          var locationsArr = val;
				                                          street = locationsArr[0].street;
				                                          city = locationsArr[0].adminArea5;
				                                          state = locationsArr[0].adminArea3;
				                                          zip = locationsArr[0].postalCode;
				                                   }

				                               });
				                             });
				                             var findme = {
				                                   address: street+','+city+','+state+','+zip,
				                                   zip: zip,
				                                   city: city,
				                                   state: state,
				                             };
				                            //console.log("findme"+JSON.stringify(findme));
				                            data = JSON.parse(JSON.stringify(findme));
				                            //console.log(findme['address']);	
				                            suntrustPage.updateLocationFields(data, $input);	
				                            if($('.suntrust-input-location-button-map').length > 0) {
				                            	updateLocationFields(data, $('.suntrust-input-location-button-map').siblings('input:first'));
				                            //if($('.suntrust-input-location-button').length > 0) {
				                                //updateLocationFields(data, $('.suntrust-input-location-button').siblings('input:first'));
				                            }
				                      },
				                      error: function (xhRequest, ErrorText, thrownError) {
				                             ajaxError();
				                      }
				               });
				               },
						updateLocationFields : function(data, $input) {
							if (data.address != '') {
								$input.typeahead('val',data.address);
								$input.focus();
								if ($input.data('linked-id')) {
									var $linkedInput = $('#'
											+ $input.data('linked-id'));
									if ($linkedInput.length > 0) {
										$linkedInput.val(data.address);
									}
								}
							}
							if (data.zip != '' || data.city != ''
									|| data.state != '') {
								// ZIP
								var $zipField = $input
										.siblings('input.suntrust-input-location-zip');
								if (data.zip != '' && $zipField.length > 0) {
									$zipField.val(data.zip);
								}
								// City
								var $cityField = $input
										.siblings('input.suntrust-input-location-city');
								if (data.city != '' && $cityField.length > 0) {
									$cityField.val(data.city);
								}
								// City
								var $stateField = $input
										.siblings('input.suntrust-input-location-state');
								if (data.state != '' && $stateField.length > 0) {
									$stateField.val(data.state);
								}
								// Clear location fields on text change
								var $locationFields = $input
										.siblings('input.suntrust-input-location-field');
								if ($locationFields.length > 0) {
									$input
											.keydown(function(event) {
												// Allow: tab, escape, and enter
												if (event.keyCode == 9
														|| event.keyCode == 27
														|| event.keyCode == 13
														||
														// Allow: Ctrl+A
														(event.keyCode == 65 && event.ctrlKey === true)
														||
														// Allow: home, end,
														// left, right
														(event.keyCode >= 35 && event.keyCode <= 39)) {
													// let it happen, don't do
													// anything
													return;
												} else {
													$locationFields.val('');
												}
											});
								}
							}
						},
						initPeopleFinderPromo : function(form) {
							// Validate location fields
							function valLocationFields($input) {
								var $zipField = $input
										.siblings('input.suntrust-input-location-zip');
								var $cityField = $input
										.siblings('input.suntrust-input-location-city');
								var $stateField = $input
										.siblings('input.suntrust-input-location-state');
								return ($zipField.length > 0 && $
										.trim($zipField.val()).length != 0)
										|| ($cityField.length > 0 && $
												.trim($cityField.val()).length != 0)
										|| ($stateField.length > 0 && $
												.trim($stateField.val()).length != 0);
							}
							// Redirect form based on target
							function peopleFinderRedirect(isValid) {
								var $input = form.find(
										'input.suntrust-form-required')
										.removeClass('no-valid');
								if (isValid || valLocationFields($input)) {
									var url = form.attr('action')
											+ '#'
											+ form.find(
													"input[value!=''],select")
													.serialize();
									if (form.data('target') == "_blank") {
										var win = window.open(url, '_blank');
										win.focus();
									} else {
										window.location.href = url;
									}
								} else {
									$input.addClass('no-valid');
								}
							}
							// Promo form submit
							form.submit(function(event) {
								event.preventDefault();
								// Required field validation
								var $input = form.find(
										'input.suntrust-form-required')
										.removeClass('no-valid');
								if ($.trim($input.val()).length == 0) {
									$input.addClass('no-valid');
								} else {
									$.ajax({
										type : "POST",
										url : '/FindUs/GetAddress',
										data : 'location=' + $input.val(),
										beforeSend : function() {
											showLoadingMask();
										},
										success : function(data) {
											suntrustPage.updateLocationFields(
													data, $input);
										},
										error : function(xhRequest, ErrorText,
												thrownError) {
											ajaxError();
											$input.addClass('no-valid');
										},
										complete : function() {
											peopleFinderRedirect(false);
											hideLoadingMask();
										}

									});
								}

							});
						},
						initLOFinderPromo : function(form) {
							// LO FInder promo
							suntrustPage
									.setZipCodeField(form
											.find('input.sun-right-rail-promo-zip-code-field'));
							// advisor search form submit
							form
									.submit(function(event) {
										event.preventDefault();
										var isValid = true, fromInputs = $(this)
												.find('.sun-val-form-input'), zipCodeInput = $(
												this)
												.find(
														'.sun-right-rail-promo-zip-code-field');
										$(this).find('.sun-error').removeClass(
												"sun-error");
										if (fromInputs.filter(function() {
											return this.value != "";
										}).length == 0) {
											isValid = false;
											fromInputs.closest('.sun-field')
													.addClass('sun-error');
										} else if (zipCodeInput.val().length > 0
												&& zipCodeInput.val().length < 5) {
											isValid = false;
											zipCodeInput.closest('.sun-field')
													.addClass('sun-error');
										}
										if (isValid) {
											var url = $(this).attr('action')
													+ '#' + $(this).serialize();
											if ($(this).data('target') == "_blank") {
												var win = window.open(url,
														'_blank');
												win.focus();
											} else {
												window.location.href = url;
											}

										}
									});
						},
						setZipCodeField : function(field) {
							// Zip input keydown to avoid letter or symbols
							field
									.keydown(function(event) {
										// Allow: backspace, delete, tab,
										// escape, and enter
										if (event.keyCode == 46
												|| event.keyCode == 8
												|| event.keyCode == 9
												|| event.keyCode == 27
												|| event.keyCode == 13
												||
												// Allow: Ctrl+A
												(event.keyCode == 65 && event.ctrlKey === true)
												||
												// Allow: home, end, left, right
												(event.keyCode >= 35 && event.keyCode <= 39)) {
											// let it happen, don't do anything
											return;
										} else {
											// Ensure that it is a number and
											// stop the keypress
											if (event.shiftKey
													|| (event.keyCode < 48 || event.keyCode > 57)
													&& (event.keyCode < 96 || event.keyCode > 105)) {
												event.preventDefault();
											} else {
												if (isTextSelected($(this)[0])) {
													$(this).val('');
												}
												if ($(this).val().length == 5) {
													event.preventDefault();
												}
											}
										}
									});
							// Zip input click event selects text
							field.click(function() {
								$(this).select();
							});
						},
						initComparisonChart : function() {
							$('div.component-comparisonchart')
									.each(
											function() {
												var $comparisonChart = $(this), $chartproductList = $comparisonChart
														.find('ul[data-suntrust-class="compare-account-list"]');
												// height fix
												var setCompareHeights = function() {
													var $compareAcctItems = $chartproductList
															.find('.suntrust-accounts-comparison-list-item-wrapper'), $compareAcctHeaders = $compareAcctItems
															.find('.suntrust-compare-header'), largestHeight = 0, headerLargestHeight = 0;

													$compareAcctItems.css(
															'height', 'auto');
													// Headers
													$compareAcctHeaders.css(
															'height', 'auto');
													$compareAcctHeaders
															.each(function() {
																var $itemHeader = $(this);
																if ($itemHeader
																		.height() > headerLargestHeight) {
																	headerLargestHeight = $itemHeader
																			.height();
																}
															});
													$compareAcctHeaders.css(
															'height',
															headerLargestHeight
																	+ 'px');
													// Items
													$compareAcctItems
															.each(function() {
																var $item = $(this);
																if ($item
																		.height() > largestHeight) {
																	largestHeight = $item
																			.height();
																}
															});
													$compareAcctItems.css(
															'height',
															largestHeight
																	+ 'px');
												};
												setCompareHeights();
												$(window)
														.resize(
																_
																		.throttle(
																				setCompareHeights,
																				suntrustPage.throttleTime));

												// compare functionality
												var $compareButton = $comparisonChart
														.find('[data-suntrust-class="compare-button"]'), $compareForm = $comparisonChart
														.find('.sun-comparison-chart-form');
												var totalChecked = 0;// US14067
																		// Changes
																		// for
																		// deactivating
																		// compare
																		// button
												$compareForm
														.each(function() {
															var compareForm = $(this);
															var compareNotification = compareForm
																	.find('[data-suntrust-class="compare-button"] .suntrust-accessible-text');
															var compareMessage = compareNotification
																	.text();
															var compareCheckboxes = compareForm
																	.find('.suntrust-compare-checkbox');

															function countChecked() {
																var count = compareCheckboxes
																		.filter(':checked').length;
																totalChecked = count;
																if (count >= 2) {
																	compareNotification
																			.text('Continue selecting accounts to compare or Compare Now');
																	$compareButton
																			.removeClass(
																					'suntrust-disabled')
																			.removeAttr(
																					'disabled');
																} else {
																	if (compareNotification
																			.text() !== compareMessage) {
																		compareNotification
																				.text(compareMessage);
																		$compareButton
																				.addClass(
																						'suntrust-disabled')
																				.attr(
																						'disabled');
																	}
																}
															}
															compareCheckboxes
																	.on(
																			'change',
																			countChecked);
														});

												$compareForm
														.submit(function(event) {
															var $form = $(this);
															event
																	.preventDefault();
															// US14067 Start
															// Changes for
															// deactivating
															// compare button
															if (totalChecked < 2) {
																return false;
															}
															// End Changes for
															// deactivating
															// compare button
															$
																	.ajax({
																		type : "POST",
																		url : $form
																				.attr('action'),
																		data : $form
																				.serialize(),
																		success : function(
																				data) {
																			var $data = $(data);
																			$data
																					.find(
																							'h2.suntrust-modal-header_overlay-title')
																					.append(
																							$form
																									.data('overlay-title'));
																			$data
																					.modalWindow({
																						$trigger : $form
																								.find('button.suntrust-compare')
																					});
																			// Title
																			var $suntrustCompareTitleRow = $data
																					.find('.suntrust-compare-title-row');

																			if ($suntrustCompareTitleRow.length > 0) {
																				$suntrustCompareTitleRow
																						.each(function() {
																							var $suntrustCompareTitle = $(
																									this)
																									.find(
																											'.suntrust-compare-title'), $suntrustCompareTitleHeight = $suntrustCompareTitle
																									.innerHeight() - 1; // -1
																														// for
																														// border
																							$(
																									this)
																									.height(
																											$suntrustCompareTitleHeight);
																						});
																			}
																			// Rows
																			var $tableRow = $data
																					.find('[data-suntrust-class="compare-table-row"]');

																			$tableRow
																					.each(function() {
																						var rowHeight = $(
																								this)
																								.outerHeight()
																								+ 'px', $firstColumn = $(
																								this)
																								.find(
																										$('[data-suntrust-class="first-column"]'));

																						$firstColumn
																								.css(
																										'height',
																										rowHeight);
																					});
																			speedbumpCheck($data);
																		},
																		error : function(
																				xhRequest,
																				ErrorText,
																				thrownError) {
																			ajaxError();
																		}
																	});
														});
											});
						},
						initLoadMore : function() {
							$('ul.suntrust-loadmore-list')
									.each(
											function() {
												var $list = $(this), page = 1;
												$list
														.find(
																'button.suntrust-load-more')
														.click(
																function(event) {
																	page++;
																	var pageSize = $list
																			.data('page-size'), visibleLimit = page
																			* pageSize, $listItems = $list
																			.find('.suntrust-article-list-item'), $button = $(this);
																	event
																			.preventDefault();
																	$listItems
																			.filter(
																					':lt('
																							+ visibleLimit
																							+ ')')
																			.show();
																	if (visibleLimit + 1 >= $listItems.length) {
																		$button
																				.hide();
																	}
																});
											});

						},
						initAccordion : function() {
							$('section[data-suntrust-class="accordion"]')
									.each(
											function() {
												var $accordion = $(this), $accordionBtns = $accordion
														.find('[data-suntrust-class="accordion-trigger"]');
												$accordionBtns
														.each(function() {
															$(this)
																	.click(
																			function() {
																				var $accordionBtn = $(this), currIndex = $accordionBtn
																						.index('.suntrust-accordion-trigger'), targetSection = $accordionBtn
																						.next('section[data-suntrust-class="accordion-section"]'), doScroll = ($(
																						window)
																						.width() <= 768 && targetSection
																						.hasClass('suntrust-closed-section'));
																				$accordion
																						.find(
																								'section[data-suntrust-class="accordion-section"]')
																						.addClass(
																								'suntrust-closed-section');
																				// Only
																				// open
																				// open
																				// at a
																				// time,
																				// clicking
																				// same
																				// on
																				// thats
																				// open
																				// will
																				// close
																				// it

																				if ($accordionBtn
																						.hasClass('suntrust-open')) {
																					$accordionBtns
																							.removeClass('suntrust-open');
																				} else {
																					$accordionBtns
																							.removeClass('suntrust-open');
																					$accordionBtn
																							.addClass('suntrust-open');
																					targetSection
																							.removeClass(
																									'suntrust-closed-section')
																							.attr(
																									{
																										'tabIndex' : '-1'
																									})
																							.focus();
																					targetSection[0]
																							.focus();
																				}
																				if (doScroll) {
																					var btnTop = $accordionBtn
																							.offset().top, scrollTo = $(
																							window)
																							.scrollTop() >= btnTop ? btnTop - 80
																							: btnTop;
																					$(
																							'html, body')
																							.stop()
																							.animate(
																									{
																										scrollTop : scrollTo,
																										easing : 'ease'
																									},
																									400);
																				}
																			});

															$(this)
																	.focus(
																			function() {
																				$(
																						this)
																						.toggleClass(
																								"suntrust-accordion-trigger-focus",
																								true);
																			});

															$(this)
																	.blur(
																			function() {
																				$(
																						this)
																						.toggleClass(
																								"suntrust-accordion-trigger-focus",
																								false);
																			});
														});
											});
						},
						openWindow : function(url, params) {
							var name = suntrustPage
									.getParameter(params, 'name');
							/*
							 * Generic function for opening a new window // url =
							 * the url that is gonna be displayed in the new
							 * window // params = the confguration of the window
							 * (name, width, height, showLocation, showMenubar,
							 * isResizable, hasScrollbars, showStatus,
							 * showTitlebar, showToolbar)
							 */
							if ($.browser.msie) {
								name = '_blank';
							}
							if ($.browser.webkit
									|| ($.browser.msie && !suntrustPage.config.isIE8)) {
								params = suntrustPage.updateParameter(params,
										'status', '0');
							}
							var left = ((window.screen.width - suntrustPage
									.getParameter(params, 'width')) / 2);
							var top = ((window.screen.height - suntrustPage
									.getParameter(params, 'height')) / 2);
							params += ",top=" + top + ",left=" + left;
							window.open(url, name, params, false);
						},
						updateParameter : function(params, name, value) {
							// params = list of Comma-separated parameters
							// name = Name of the parameter to update
							// value = new value
							// Returs the same list with a specific parameter
							// updated
							if (params.indexOf(name) >= 0) {
								var regex = new RegExp('(,)?' + name
										+ '=[_\-a-z0-9]*');
								var newParameter = name + '=' + value;
								if (params.indexOf(name) != 0) {
									newParameter = ',' + newParameter;
								}
								return params.replace(regex, newParameter);
							} else {
								return '';
							}
						},
						getParameter : function(params, name) {
							// params = list of Comma-separated parameters
							// name = Name of the parameter to get
							// Returs a specific parameter if exists
							if (params.indexOf(name) >= 0) {
								var regex = new RegExp(name + '=[_\-a-z0-9]*');
								return params.match(regex)[0].replace(name
										+ '=', '');
							} else {
								return '';
							}
						},
						// Added for US18204
						getRateByLoanType : function(loanType, rateType) {

							var rate = 0;

							if (loanType == undefined || rateType == undefined) {

								return rate;
							}

							$(strLightStreamJSON).each(function(i, ele) {

								if (ele.LoanPurpose == loanType) {

									if (rateType == 'Max') {
										rate = ele.MaxRate;
									} else if (rateType == "Min") {
										rate = ele.MinRate;
									}

									rate = (parseFloat(rate) * 100).toFixed(2);
									return rate;
								}
							}

							);
							return rate;
						}
					};
					$(function() {
						suntrustPage.init();
					});					
					/* Init scripts - uniform ends */

					/* Sign on blade starts */
					var $sunMain = $('[data-suntrust-class="suntrust-main"]');
					// if ($sunMain.length > 0) {
						var $sunPage = $('[data-suntrust-class="suntrust-page"]'), $sunBankMenuTrigger = $('[data-suntrust-class="suntrust-menu-trigger"]'), $sunUserMenuTrigger = $('[data-suntrust-class="suntrust-user-trigger"]'), $sunMenuClose = $('[data-suntrust-class="suntrust-menu-close"]'), $changeBankAccount = $('[data-suntrust-class="change-bank-account"]'), $submenuBack = $('[data-suntrust-class="suntrust-submenu-back"]'), $suntrustSubmenus = $('.suntrust-submenu'), $suntrustSubmenuLTRTriggers = $('[data-suntrust-ltr-triggers] li'), $suntrustSubmenuRTLTriggers = $('[data-suntrust-rtl-triggers] li'), $suntrustBankingMenuLinks = $('[data-suntrust-class="submenu-triggers"] li'), $bankingMenu = $('[data-suntrust-class="banking-menu"]'), $userMenu = $('[data-suntrust-class="user-menu"]'), $header = $('[data-suntrust-class="header"]'), hoverClass = 'suntrust-hover', activeClass = 'active', $bankingLinksContainer = $('[data-suntrust-class="banking-links-container"]'), noScrollbars = 'no-scrollbars';

						// Browser sniff to see if and old android version is
						// used
						/*
						 * var ua = navigator.userAgent; var androidversion =
						 * parseFloat(ua.slice(ua .indexOf("Android") + 8)); if
						 * (androidversion < 3) {
						 * $('html').addClass('old-android'); }
						 */

						// Detect if its a touch device
						var isTouch;
						if ($('html').hasClass('touch')) {
							isTouch = true;
						} else {
							isTouch = false;
						}

						// Detect IE > 9 for Scrollbar issue on submenus
						var ua = window.navigator.userAgent, ie = (ua
								.indexOf('MSIE') !== -1)
								|| (ua.indexOf('Trident') !== -1), ieGt9 = ie
								&& !$('html').hasClass('lt-ie10')
								&& !$('body').hasClass('lt-ie10');

						if (ieGt9) {
							$('html').addClass('ie10');
						}

						// Detect IE8
						var ie8;
						if ($('html').hasClass('ie8')
								|| $('body').hasClass('ie8')) {
							ie8 = true;
						} else {
							ie8 = false;
						}

						$('[data-suntrust-class="submenu-triggers"]').hover(
								function() {
									if ($(window).width() > 768 && !ie8) {
										$sunPage.addClass(hoverClass);
									}

								},
								function() {
									if (!ie8) {
										$sunPage.removeClass(hoverClass);
									}

									if (ie8) {
										$sunPage.removeClass(hoverClass);
										$suntrustSubmenus
												.removeClass(activeClass);
									}
								});

						$('.suntrust-banking-overview-link').hover(function() {
							if (ie8) {
								$suntrustSubmenus.removeClass(activeClass);
							}
						});

						// Separate hover JS for IE8
						$suntrustBankingMenuLinks.hover(function() {
							if (ie8) {
								$bankingMenu.addClass(noScrollbars);
								$sunPage.addClass(hoverClass);
								$(this).prev('li').find('.suntrust-submenu')
										.removeClass(activeClass);
								$(this).next('li').find('.suntrust-submenu')
										.removeClass(activeClass);
								$(this).find('.suntrust-submenu').addClass(
										activeClass);
							}
						}, function() {
							$bankingMenu.removeClass(noScrollbars);
						});

						// Reset selected option when clicking "Back / Forward"
						// browser buttons
						$('.suntrust-segment-switch, .suntrust-bank-segment')
								.val(0);

						// Code to switch main banking links visible by bank
						// segment selected
						$('[data-suntrust-account-type]')
								.change(
										function() {
											var e = document
													.getElementById("segment"), selectedSegment = e.options[e.selectedIndex].index, accessibleSelectedSegment = $(
													this).val();

											$bankingLinksContainer
													.addClass('suntrust-hidden');
											$bankingLinksContainer.eq(
													selectedSegment)
													.removeClass(
															'suntrust-hidden');
										});

						function removeSubmenuLinkStyle() {
							$suntrustSubmenuLTRTriggers
									.removeClass('touch-active');
							$suntrustSubmenuRTLTriggers
									.removeClass('touch-active');
							$sunPage.removeClass('submenu-rtl-active active');
							$suntrustSubmenus.removeClass('active');

							/*
							 * if (isTouch) { $header.removeClass('no-shadow'); }
							 */
						}

						var bankNavOpen = false, userNavOpen = false, menuAnimTime = 500, menuAnimEase = 'swing';

						function animateBankMenuIn() {
							if (($('#divsuntrustmobileview').css("display") == 'block')) {
								if (!bankNavOpen) {
									$bankingMenu.show();
									$bankingMenu.animate({
										left : '0px'
									}, menuAnimTime, menuAnimEase);
									bankNavOpen = true;
									$('body').addClass(noScrollbars);									
								} else {
									animateBankMenuOut();
								}
							} else {
								if (!bankNavOpen) {
									$bankingMenu.show();
									$bankingMenu.animate({
										top : '78px'
									}, menuAnimTime, menuAnimEase);
									bankNavOpen = true;
									$('body').addClass(noScrollbars);
								} else {
									animateBankMenuOut();
								}
							}
						}

						function animateBankMenuOut() {
							if (($('#divsuntrustmobileview').css("display") == 'block')) {
								if (bankNavOpen) {
									$bankingMenu.animate({
										left : '-320px'
									}, menuAnimTime, menuAnimEase, function() {
										bankNavOpen = false;
										$bankingMenu.hide();
										$('body').removeClass(noScrollbars);
									});
								}
							} else {
								if (bankNavOpen) {
									$bankingMenu.animate({
										top : '-720px'
									}, menuAnimTime, menuAnimEase, function() {
										bankNavOpen = false;
										$bankingMenu.hide();
										$('body').removeClass(noScrollbars);
									});
									$(".suntrust-levelHolder").animate({
										left : "-320px"
									});
									$('body').removeClass(noScrollbars);
								}
							}

						}

						function animateUserMenuIn() {
							if (!userNavOpen) {
								$userMenu.show();
								$userMenu.animate({
									right : '0'
								}, menuAnimTime, menuAnimEase);
								userNavOpen = true;
								$('body').addClass(noScrollbars);								
							}
						}
						function animateUserMenuOut() {
							if (userNavOpen) {
								$userMenu.animate({
									right : '-320px'
								}, menuAnimTime, menuAnimEase, function() {
									userNavOpen = false;
									$userMenu.hide();
									$('body').removeClass(noScrollbars);
									$("html,body").removeClass("ios-cursor");
								});
							}
						}
						// Open left side of global nav
						$sunBankMenuTrigger
								.click(function() {
									$sunPage
											.removeClass('suntrust-hover suntrust-user-menu-active');
									removeSubmenuLinkStyle();

									// incase you go from one menu directly to
									// the other to keep the scrollbars away
									setTimeout(function() {
										$('body').addClass(noScrollbars);
									}, 350);

									$sunPage
											.addClass('suntrust-bank-menu-active active');
									// Defect# 164563 -- Fix for Secondary Menu
									// slide out
									if (bankNavOpen) {
										// Defect id: 164522 -- Removing the css
										// class conditionally to keep
										// HeroComponent visible in mobile views
										$sunPage
												.removeClass('suntrust-bank-menu-active active');
										animateBankMenuOut();
									} else {
										animateBankMenuIn();
									}
									if (userNavOpen) {
										animateUserMenuOut();
									}

									$('.banking-menu-accessibility-trigger')
											.focus();
									$(this)
											.addClass(
													'banking-button-triggered');

									/*
									 * if (isTouch) {
									 * $header.addClass('no-shadow'); }
									 */
								});

						// Open right side of global nav
						$sunUserMenuTrigger
								.click(function() {

									$(".suntrust-levelHolder").animate({
										left : '-320px'
									});
									if(!$(".suntrust-header-signon-block").is(':empty'))
									{
										var isOperaMini = Object.prototype.toString
												.call(window.operamini) === '[object OperaMini]';
										var isInputSupported = 'placeholder' in document
												.createElement('input')
												&& !isOperaMini;
										var isTextareaSupported = 'placeholder' in document
												.createElement('textarea')
												&& !isOperaMini;
	
										if (isInputSupported && isTextareaSupported) {
											$sunPage
													.removeClass('suntrust-hover suntrust-bank-menu-active');
											removeSubmenuLinkStyle();
	
											animateUserMenuIn();
											if (bankNavOpen) {
												animateBankMenuOut();
											}
	
											// incase you go from one menu directly
											// to the other to keep the scrollbars
											// away
											setTimeout(function() {
												$('body').addClass(noScrollbars);
												/***** Ios 11 fix on text field start***/
												if (isSafari) {
													if(window.innerWidth<768){
														$("html,body").addClass("ios-cursor");
													}
												}
												/***** Ios 11 fix on text field end***/
											}, 350);
						
											$sunPage
													.addClass('suntrust-user-menu-active active');
											$('.user-menu-accessibility-trigger')
													.focus();
											$(this).addClass(
													'user-button-triggered');
										} else {
											var noJSurl = this.attributes["data-nojs-url"].value;
											window.location = noJSurl;
										}
									}
								});

						// Close global nav by clicking inthe main content area
						// $sunMain.click(function() {
						$(document)
								.on(
										'click',
										function(event) {
											// console.log($(event.target).parents().is('header'));
											if (!$(event.target).parents().is(
													'header') && !$(event.target).parents().is('.suntrust-hero-signon-desktop-black') && !$(event.target).is('.newWindowpopup')) {
												event.stopPropagation();
												if ($sunPage.hasClass(activeClass)) {
													$sunPage.removeClass('active suntrust-bank-menu-active suntrust-user-menu-active submenu-rtl-active suntrust-hover');
												}

												removeSubmenuLinkStyle();
												if ($sunUserMenuTrigger
														.hasClass('user-button-triggered')) {
													$sunUserMenuTrigger
															.focus()
															.removeClass(
																	'user-button-triggered');
												}

												if ($sunBankMenuTrigger
														.hasClass('banking-button-triggered')) {
													$sunBankMenuTrigger
															.focus()
															.removeClass(
																	'banking-button-triggered');
												}

												if (bankNavOpen) {
													animateBankMenuOut();
												} 
												if (userNavOpen) {
													animateUserMenuOut();
												}
											}
										});

						// Close the global nav by clicking the "X" in the menus
						$sunMenuClose
								.click(function() {
									$sunPage
											.removeClass('active suntrust-user-menu-active suntrust-bank-menu-active suntrust-hover');
									removeSubmenuLinkStyle();
									$(this).parents(
											'[data-suntrust-hidden-navs]')
											.prev().focus();

									if (bankNavOpen) {
										animateBankMenuOut();
									}

									if (userNavOpen) {
										animateUserMenuOut();
									}
								});
						//Close Header Sign on on Tabbing
						$('.suntrust-menu-close.signon-end').focusout(function(){
							removeSubmenuLinkStyle();
							animateUserMenuOut();
						})
						
						// On mobile and Tablet Portrait View, go back from
						// submenu to main menu
						$submenuBack.click(function(e) {
							e.stopPropagation();
							$sunPage.removeClass('submenu-rtl-active');
							$suntrustSubmenus.removeClass(activeClass);
							$userMenu.removeClass(noScrollbars);
							$bankingMenu.removeClass(noScrollbars);
						});

						// Remove Menu Classes on resizes or orientation changes
						function removeMenuClasses() {
							$sunPage
									.removeClass('active suntrust-user-menu-active suntrust-bank-menu-active submenu-rtl-active suntrust-hover');
							$suntrustSubmenus.removeClass(activeClass);
							removeSubmenuLinkStyle();

							if (bankNavOpen) {
								animateBankMenuOut();
							} else if (userNavOpen) {
								animateUserMenuOut();
							}
						}

						// Only happens on Touch Devices
						/*
						 * function closeMenuOnOrientationChange() { switch
						 * (window.orientation) { case -90: case 90:
						 * removeMenuClasses(); break; default:
						 * removeMenuClasses(); break; } }
						 */

						// Only happens on NON-touch devices
						/*
						 * function closeMenuOnResize() { if (!isTouch && !ie8) {
						 * removeMenuClasses(); } };
						 */

						/*
						 * if (!ie8) { if (window.addEventListener) {
						 * window.addEventListener('orientationchange',
						 * closeMenuOnOrientationChange); } }
						 */

						/*
						 * var throttledResize = _ .throttle(closeMenuOnResize,
						 * 250); $(window).resize(throttledResize);
						 */

						// Code for hiding and showing submenus on the left side
						// of global nav
						$suntrustSubmenuLTRTriggers.each(function() {
							$(this).children('.suntrust-main-links').click(
									function() {

										$(this).next('.suntrust-submenu')
												.addClass(activeClass);
										$(this).find('a').first().focus();
										$bankingMenu.scrollTop(0);
										$bankingMenu.addClass(noScrollbars);

										if ($(window).width() > 768) {
											$sunPage.addClass(hoverClass);
										}

										/*
										 * if ($(window).width() > 768 &&
										 * isTouch) { $( '.suntrust-submenu')
										 * .removeClass( activeClass); $(this)
										 * .next( '.suntrust-submenu')
										 * .addClass( activeClass); $sunPage
										 * .addClass(hoverClass);
										 * $suntrustSubmenuLTRTriggers
										 * .removeClass('touch-active'); $(this)
										 * .addClass( 'touch-active'); }
										 */
										return false;
									});
							/*
							 * if (!ie8 && !isTouch) { $(this) .children(
							 * '.suntrust-main-links') .hover( function() { if
							 * ($(window) .width() > 768) { $suntrustSubmenus
							 * .removeClass(activeClass); } }); }
							 */
						});

						// Code for hiding and showing submenus on the right
						// side of global nav
						$suntrustSubmenuRTLTriggers
								.each(function() {
									$(this)
											.children('.suntrust-main-links')
											.click(
													function() {

														$sunPage
																.addClass('submenu-rtl-active');
														$(this)
																.next(
																		'.suntrust-submenu')
																.addClass(
																		activeClass);
														$(this).find('a')
																.first()
																.focus();
														$userMenu.scrollTop(0);
														$userMenu
																.addClass(noScrollbars);

														if ($(window).width() > 768) {
															$sunPage
																	.addClass(hoverClass);
														}

														/*
														 * if ($(window).width() >
														 * 768 && isTouch) { $(
														 * '.suntrust-submenu')
														 * .removeClass(
														 * activeClass); $(this)
														 * .next(
														 * '.suntrust-submenu')
														 * .addClass(
														 * activeClass);
														 * $sunPage
														 * .addClass(hoverClass);
														 * $suntrustSubmenuRTLTriggers
														 * .removeClass('touch-active');
														 * $(this) .addClass(
														 * 'touch-active'); }
														 */
														return false;
													});

									/*
									 * if (!ie8 && !isTouch) { $(this)
									 * .children( '.suntrust-main-links')
									 * .hover( function() { if ($(window)
									 * .width() > 768) { $suntrustSubmenus
									 * .removeClass(activeClass); } }); }
									 */
								});
								
						//Commented unwanted code
						// For hiding and showing header on scroll
						/*var scrollTimer, scrollDuration = 250, lastScrollStop = 0, windowAfterScrollPX;

						function toggleHeader() {
							if ($bankingMenu.is(':visible')
									|| $userMenu.is(':visible')) {
								return;
							}

							var windowYPos = window.pageYOffset, st = $(this)
									.scrollTop();

							if (st > lastScrollStop) {
								// Downscroll code
								windowAfterScrollPX = $(window).scrollTop();

								if (windowYPos > 100) {
									$header.addClass('invis');
								}

							} else {
								// Upscroll code
								if (windowYPos < windowAfterScrollPX - 50) {
									setTimeout(function() {
										$header.removeClass('invis');
									}, 500);
								}
							}

							if (st > lastScrollStop && lastScrollStop == 0) {
								$header.removeClass('invis');
							}

							lastScrollStop = st;

							clearTimeout(scrollTimer);
						}
						toggleHeader();
						$(window).scroll(_.throttle(toggleHeader, 500));*/
						//Commented unwanted code

						// login statechange demo code
						$(
								'[data-suntrust-login-button],[data-suntrust-logout-button]')
								.click(
										function() {
											$(
													'[data-suntrust-logged-in], [data-suntrust-login-form]')
													.toggleClass(
															'suntrust-state-hidden');
											$(
													'[data-suntrust-class="suntrust-page"]')
													.toggleClass(
															'suntrust-header-signed-in');
											$(
													'.suntrust-my-settings-text, .suntrust-default-sign-on-text')
													.toggle();
										});

						$('[data-suntrust-login-button]')
								.click(
										function() {
											$(
													'[data-suntrust-class="suntrust-user-menu-state"]')
													.html(
															'You have successfully signed in. The Sign On menu has been changed to My Settings menu.');
										});

						$('[data-suntrust-logout-button]')
								.click(
										function() {
											$(
													'[data-suntrust-class="suntrust-user-menu-state"]')
													.html(
															'You have successfully signed off. The My Settings menu has been changed to the Sign On menu.');
										});

						$('[data-suntrust-class="show-authenticated"]')
								.click(
										function(e) {
											e.preventDefault();
											$(
													'[data-suntrust-class="authenticated-dotcom"]')
													.hide();
											$(
													'[data-suntrust-class="authenticated-olb"]')
													.show();
											$(
													'[data-suntrust-class="suntrust-page"]')
													.toggleClass(
															'suntrust-header-signed-in');
											$(
													'.suntrust-my-settings-text, .suntrust-default-sign-on-text')
													.toggle();
										});

						var placeholder = function(id) {
							var self = $(id);
							var placeholder = self.attr('placeholder');
							self.val(placeholder);
							self.focus(function() {
								if (self.val() === placeholder) {
									self.val('');
								}
							});
							self.focusout(function() {
								if (self.val().trim().length === 0) {
									self.val(placeholder);
								}
							});
						};

						/*
						 * if (!Modernizr.input.placeholder) {
						 * placeholder('#desktop-global-search');
						 * placeholder('#mobile-global-search');
						 * placeholder('#user-id'); placeholder('#password'); }
						 */

						$('#mobile-global-search').focus(
								function() {
									if (isTouch) {
										$('[data-suntrust-class="nav-safety"]')
												.show();
										$('.suntrust-header-alt').addClass(
												'no-shadow');
									}
								});

						$('#mobile-global-search').blur(
								function() {
									if (isTouch) {
										$('[data-suntrust-class="nav-safety"]')
												.hide();
										$('.suntrust-header-alt').removeClass(
												'no-shadow');
									}
								});

						// Search Cancel Button JS for GlobalNav Only
						var $sunHeader = $('header.suntrust-header-alt');
						if ($sunHeader.length > 0) {
							$sunHeader.find('select').uniform({
								selectClass : 'sun-select-container',
								selectAutoWidth : false
							});
							$sunHeader
									.find(
											'[data-suntrust-class="suntrust-checkbox"]')
									.uniform(
											{
												checkboxClass : 'sun-checkbox-input-container',
												checkedClass : 'sun-checked'
											});
							$sunHeader.find(
									'[data-sun-type="search-cancel-button"]')
									.click(function(e) {
										var $this = $(this);

										e.preventDefault();
										$this.removeClass('sun-active');
										$this.prev('input').val('').focus();
										$('.suntrust-autocomplete').empty();
									});

							$sunHeader.find('[type="search"]').keyup(
									function() {
										var $this = $(this), charLength = $this
												.val().length;

										if (charLength > 0) {
											$currentCancelButton
													.addClass('sun-active');
										} else {
											$currentCancelButton
													.removeClass('sun-active');
										}
									});
							$sunHeader
									.find('[type="search"]')
									.focus(
											function() {
												var $this = $(this);

												$currentCancelButton = $this
														.next('[data-sun-class="search-cancel-button"]');
												$this
														.closest(
																'.suntrust-header-search-container')
														.addClass(
																'search-active');
											});
							$sunHeader
									.find('[type="search"]')
									.blur(
											function() {
												var $this = $(this);

												if ($(this).val().length > 0) {
													return false;
												} else {
													$this
															.closest(
																	'.suntrust-header-search-container')
															.removeClass(
																	'search-active');
												}
											});
						}

					// }
					/* Sign on blade ends */

					/* Global nav starts */
					var $sunMainBlade = $('div[data-suntrust-class="suntrust-page"]');
					if ($sunMainBlade.length > 0) {
						var $sunPage = $('#suntrust-page');
						var $sunHeader = $('header.suntrust-header-alt');
						var $loginForm = $('#suntrust-login-form');
						var hasBanner = false;
						// Detect IE8
						var ie8;
						if ($('html').hasClass('ie8')
								|| $('body').hasClass('ie8')) {
							ie8 = true;
						} else {
							ie8 = false;
						}
						// Search
						$('.suntrust-search-input').focus(function() {
							$(this).select();
						});

						if ($sunHeader.length > 0) {
							// App banner
							if (typeof deviceDetection !== 'undefined'
									&& typeof (suntrustPage) == 'undefined') {
								$sunHeader.find(deviceDetection.selector)
										.removeClass('sun-global-app-element');
							}
							var $bannerContainers = $sunHeader
									.find('.suntrust-menu-alert-top-container');
							$bannerContainers
									.each(function() {
										var $bannerContainer = $(this);
										if ($bannerContainer.children().not(
												'.sun-global-app-element').length == 0) {
											$bannerContainer.hide();
										}
									});
							// placeholder
							if ($sunHeader.length > 0) {
								// $sunHeader.find('input,
								// textarea').placeholder();
							}
							if ($loginForm.length > 0) {
								$sunHeader.find('.suntrust-user-icon-button')
										.removeClass('hidden-element');
							}
						}
						// select
						$.uniform.update('#segment');
						
						// Sign On
						var $bankSegmentSelect = $loginForm
								.find('select.suntrust-bank-segment');
						if ($bankSegmentSelect.length > 0) {
							// Sign on cookies
							var signOnCookies = {
								olbCookie : get_cookieNoUnescape('OLBRMdata'),
								olbMaskedValue : '',
								olbEncryptedValue : '',
								olbValid : false,
								optionCookie : getCookie('SignOnOption'),
								optionValid : false,
								olbIsEncrypted : function() {
									if (signOnCookies.olbCookie != null
											&& signOnCookies.olbCookie != ''
											&& signOnCookies.olbEncryptedValue != ''
											&& signOnCookies.olbMaskedValue != '') {
										return true;
									}
									return false;
								}
							};
							// Set the selected option
							if (signOnCookies.optionCookie != null
									&& signOnCookies.optionCookie != '') {
								var appId = signOnCookies.optionCookie;

								var $form = $loginForm.find('#SignOn-' + appId);
								var $otherServices = $('.suntrust-more-services-wrapper[id=OtherServices-'
										+ appId + ']');
								var $mobileApps = $('.suntrust-app-banner-wrapper[id=MobileApps-'
										+ appId + ']');

								if ($form.length > 0) {
									$loginForm.find(
											'div.suntrust-signon-login-form')
											.addClass('hidden');
									$form.removeClass('hidden');

									$('div.suntrust-more-services-wrapper')
											.addClass('hidden');
									$otherServices.removeClass('hidden');

									$('div.suntrust-app-banner-wrapper')
											.addClass('hidden');
									$mobileApps.removeClass('hidden');

									$bankSegmentSelect.val(appId);
								}
							}
							$.uniform.update($bankSegmentSelect);
							// Set the OLB form values based on olbCookie
							if (signOnCookies.olbCookie != null
									&& signOnCookies.olbCookie != '') {
								var cookieValues = signOnCookies.olbCookie
										.replace('maskedValue=', '').replace(
												'encryptedValue=', '').split(
												'&');
								if (cookieValues.length = 2) {
									signOnCookies.olbMaskedValue = cookieValues[0];
									signOnCookies.olbEncryptedValue = cookieValues[1];
									if (signOnCookies.olbIsEncrypted()) {
										signOnCookies.olbValid = true;
										// Only forms of OLB
										var forms = $(".signOnLoginForm[data-apptype='OLB']");
										var currentForm = $(".signOnLoginForm[data-apptype='OLB'][data-appid='"
												+ signOnCookies.optionCookie
												+ "']");
										var textbox = forms
												.find('.suntrust-login-user-input');
										if (signOnCookies.optionValid
												&& signOnCookies.optionCookie == 'OLB') {
											$(function() {
												var passField = currentForm
														.find('.suntrust-login-password');
												passField.siblings('label')
														.hide();
											});
										}
										textbox
												.val(signOnCookies.olbMaskedValue);
										textbox
												.siblings(
														'.suntrust-login-hidden-user-id')
												.val(
														signOnCookies.olbEncryptedValue);
										textbox
												.siblings(
														'.suntrust-login-input-is-encrypted')
												.val('true');
										textbox.siblings('label').hide();
										var checkbox = forms
												.find('.suntrust-login-checkbox');
										if (checkbox.length > 0) {
											checkbox.attr("checked", true);
											$.uniform.update(checkbox);

										}
									}
								}
							}
							// Input click event selects text
							$loginForm.find('.suntrust-login-input').click(
									function() {
										$(this).select();
									});
							// Select
							$bankSegmentSelect
									.change(function() {
										var appId = this.value;
										var $form = $loginForm.find('#SignOn-'
												+ appId);
										var $otherServices = $('.suntrust-more-services-wrapper[id=OtherServices-'
												+ appId + ']');
										var $mobileApps = $('.suntrust-app-banner-wrapper[id=MobileApps-'
												+ appId + ']');

										if ($form.length > 0) {
											$loginForm
													.find(
															'div.suntrust-signon-login-form')
													.addClass('hidden');
											$form.removeClass('hidden');

											$(
													'div.suntrust-more-services-wrapper')
													.addClass('hidden');
											$otherServices
													.removeClass('hidden');

											$('div.suntrust-app-banner-wrapper')
													.addClass('hidden');
											$mobileApps.removeClass('hidden');
										}
										if (ie8) {
											// Fix webfont icon issues on IE8
											var head = document
													.getElementsByTagName('head')[0], style = document
													.createElement('style');
											style.type = 'text/css';
											style.styleSheet.cssText = ':before,:after{content:none !important';
											head.appendChild(style);
											setTimeout(function() {
												head.removeChild(style);
											}, 0);
										}
									});
							var $bankSegmentSubmitButtons = $loginForm
									.find('.suntrust-login-button');
							// Submit
							$bankSegmentSubmitButtons
									.click(function(event) {
										event.preventDefault();
										$form = $(this).closest(
												'form.signOnLoginForm');
										var valid = true;
										$form.find('.suntrust-login-input')
												.removeClass('sun-error');
										$form
												.find('.suntrust-login-input')
												.each(
														function() {
															var input = $(this);
															if ($.trim(input
																	.val()).length == 0
																	|| (input
																			.is('.suntrust-login-user-input') && $
																			.trim(input
																					.val()) == input
																			.attr('placeholder'))) {
																input
																		.addClass('sun-error');
																valid = false;
															}
														});
										if (valid) {
											var cookieConfig = {
												expire : 30, // 30days
												secure : window.location.protocol == "https:" ? true
														: false,
												domain : window.location.host,
												path : "/"
											};
											var appId = $form.data('appid');
											var isEncryptEnabled = $form.data('encrypt');
											if(isEncryptEnabled != undefined && 
													isEncryptEnabled != null && isEncryptEnabled != '') {
												isEncryptEnabled = isEncryptEnabled.toString();
											}
											var appType = $form.data('apptype');
											var gateway = $form.data('gateway');
											setCookie('SignOnOption', appId,
													cookieConfig.expire,
													cookieConfig.path,
													cookieConfig.domain,
													cookieConfig.secure);

											// Encrypt password if gateway is
											// 11g and there is a public key
											var password = $form
													.find('.suntrust-login-password');
											var userId = $form
													.find('.suntrust-login-user-input');
											var isUserIdEnc = $form
													.find('.suntrust-login-input-is-encrypted');
											var hiddenPublicKey = password
													.siblings('.hiddenPublicKey');
											if (gateway == '11g'
													&& hiddenPublicKey.length > 0) {
												var publicKey = KEYUTIL
														.getRSAKeyFromPublicPKCS8PEM(hiddenPublicKey
																.val());
												var encrypted = publicKey
														.encrypt(password.val());
												encrypted = hex2b64(encrypted);

												password.siblings(
														'.hiddenPassword').val(
														encrypted);
												/*US60890-Oauth to address vulnerability scan - OLB changes in the login flow-start*/
												if(isUserIdEnc.val() == 'true') {
													if(isEncryptEnabled == 'true') {
														if(userId.val()!=signOnCookies.olbMaskedValue) {
															var encryptedUserId = publicKey
															.encrypt(userId.val());
															encryptedUserId = hex2b64(encryptedUserId);
															userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																encryptedUserId);
														} else {
															userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																	signOnCookies.olbEncryptedValue);
														}
													} else {
														if(userId.val()!=signOnCookies.olbMaskedValue) {
															userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																userId.val());
															userId.siblings(
															'.suntrust-login-input-is-encrypted').val('false');
                                                    	} else {
                                                        	userId.siblings(
                                                            	'.suntrust-login-hidden-user-id').val(
                                                            	signOnCookies.olbEncryptedValue);
                                                    	}
													}
												} else if(isUserIdEnc.val() == 'false') {
													if(userId.val()!=signOnCookies.olbMaskedValue) {
														userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																userId.val());
													} else {
														var encryptedUserId = publicKey
														.encrypt(userId.val());
														encryptedUserId = hex2b64(encryptedUserId);
														userId.siblings(
																'.suntrust-login-hidden-user-id').val(
																encryptedUserId);
													}
												}
												/*US60890-Oauth to address vulnerability scan - OLB changes in the login flow-end*/
											} else {
												password.siblings(
														'.hiddenPassword').val(
														password.val());
											}

											var textbox = $form
													.find('.suntrust-login-user-input');
											// dont have encrypted value in
											// cookie or masked user id has
											// changed
											if (isUserIdEnc.val() == 'true' && (signOnCookies.olbCookie != null
													&& signOnCookies.olbCookie != '') && textbox
                                               .val() == signOnCookies.olbMaskedValue) {
												$('form[name="loginForm-signonblade-OLB"] input[name="rmUsernameSet"]').val("true");
											}
											if(appType != 'OLB') {
												textbox
														.siblings(
																'.suntrust-login-hidden-user-id')
														.val(textbox.val());
												textbox
														.siblings(
																'.suntrust-login-input-is-encrypted')
														.val('false');
											}
											// If checkbox exists and is
											// unchecked, delete the cookie
											var rememberCheck = $form
													.find('.suntrust-login-checkbox');
											if (rememberCheck.length > 0
													&& appType == 'OLB') {
												var rememberHidden = rememberCheck
														.closest(
																'.suntrust-remember-me-field')
														.find(
																'.suntrust-login-checkbox-hidden');
												if (rememberCheck
														.is(':checked')) { // for
													// olb
													// remove
													// cookie
													rememberHidden.val('true');
												} else {
													rememberHidden.val('false');
													delete_cookie('OLBRMdata');
												}
											}
											$form.submit();
										}
									});
									$(document).on('keypress','#SignOn-signonblade-OCM .suntrust-login-input, #SignOn-signonblade-OLB .suntrust-login-input',function(e){
										if(e.which==13)
										{
											$bankSegmentSubmitButtons.trigger('click');
										}
									});
						}
					}
					/* Global nav ends */			
					
				}
				sunScriptfun();
				/***End of the sunScriptfun Function***/

					/* Tab Set Start */
					$('.sun-feature-tabs_tabset').responsiveTabs();
					/* Tab Set End*/

					/* Accordion Script for Page On Load functionality Start */
					$(".suntrust-accordion-trigger").each(function() {
						var expand = $(this).attr("data-expand");
						if (expand == "true") {
							$(this).addClass("suntrust-open");
							$(this).next().show();
						}
					});
					/* Accordion Script for Page On Load functionality End */

					/*Combo Box Anchor Navigation Start*/
					$(document).on("change",".suntrust-select.suntrust-comboBoxloans",function(){                        
                        if($(this).val()!="")
                        {
							window.location = $(this).val();
                            var common_url = window.location.href;
                            var common_anchor_fields = common_url.split('#');
                            var common_anchor_target_id = common_anchor_fields[common_anchor_fields.length - 1];
                            var topoff = $("body").find('[data-anchor="'+common_anchor_target_id+'"]').offset().top;
                            $('html, body').animate({scrollTop : topoff},'slow');
                        }
					});
					/*Combo Box Anchor Navigation End*/

					/*Anchor Navigation Start*/
					
					/*Anchor Functionality On Page Load Start*/
					var anchor_url,anchor_fields,anchor_target_id;
					try{
						anchor_url = window.location.href;
						anchor_fields = anchor_url.split('#');
						anchor_target_id = anchor_fields[anchor_fields.length - 1];
						if($("[data-anchor='" + anchor_target_id + "']").parent().hasClass('tabset-nav-list'))
						{
							tabAnchoring(anchor_target_id);
						}
						else if($("[data-anchor='" + anchor_target_id + "']").parent().hasClass("suntrust-mortgagetablewrapper"))
						{
							mortgageAnchoring(anchor_target_id);
						}
						else if($("[data-anchor='" + anchor_target_id + "']").parents("ul.faq").hasClass("faq") && !$("[data-anchor='" + anchor_target_id + "']").parents("ul.suntrust-faqs-list").hasClass("suntrust-faqs-list"))
						{
							accordionAnchoring(anchor_target_id);
						}
						else{
							faqAnchoring(anchor_target_id);
						}
					}
					catch(e){
					}
					/*Anchor Functionality On Page Load End*/
					
					/*Anchor Functionality within the Page Start*/
					$(document).on('click','a[href^="#"]',function(){
						var anchor = $(this).attr('href').split("#");
						anchor_target_id = anchor[1];
						if($("[data-anchor='" + anchor_target_id + "']").parent().hasClass('tabset-nav-list'))
						{
							tabAnchoring(anchor_target_id);
						}
						else if($("[data-anchor='" + anchor_target_id + "']").parent().hasClass("suntrust-mortgagetablewrapper"))
						{
							mortgageAnchoring(anchor_target_id);
						}
						else if($("[data-anchor='" + anchor_target_id + "']").parents("ul.faq").hasClass("faq") && !$("[data-anchor='" + anchor_target_id + "']").parents("ul.suntrust-faqs-list").hasClass("suntrust-faqs-list"))
						{
							accordionAnchoring(anchor_target_id);
						}
						else{
							faqAnchoring(anchor_target_id);
						}
					});
					/*Anchor Functionality within the Page End*/
					
					function tabAnchoring()
					{
						try{
							$("li[data-anchor='" + anchor_target_id + "']>a").click();
							var aTag = $("li[data-anchor='" + anchor_target_id + "']").offset().top-150;
							$('html,body').stop().animate({scrollTop: aTag},'slow');
						}catch(e){
						}
					}

					function accordionAnchoring()
					{
						/*if ($("li[data-anchor='" + anchor_target_id+ "'] > div").hasClass("suntrust-open")) {
							$('html,body').animate({scrollTop : $("li[data-anchor='"+ anchor_target_id + "'] > div").offset().top}, 'slow');
						} else {
							$('html,body').animate({scrollTop : $("li[data-anchor='" + anchor_target_id	+ "'] > div").offset().top}, 'slow');
							$("li[data-anchor='" + anchor_target_id + "'] > div").addClass("suntrust-open");
							$("li[data-anchor='" + anchor_target_id + "'] > ul").slideToggle();
						}*/
						$("li[data-anchor='" + anchor_target_id+ "'] > div").click();
                        $('html,body').animate({scrollTop : $("li[data-anchor='" + anchor_target_id	+ "'] > div").offset().top}, 'slow');
					}

					function faqAnchoring()
					{
						$('a[data-anchor="'+anchor_target_id+'"]').each(function(){
							$(this).addClass('sun-active');
							$(this).next().addClass('sun-active');
							if($(this).parents().hasClass('suntrust-faqs-list')) {
								$(this).parents('.suntrust-faqs-list').prev().addClass('suntrust-open');
								$(this).parents('.suntrust-faqs-list').show();
							}
							$('html, body').animate({scrollTop : $(this).offset().top},'slow');
						});
					}

					function mortgageAnchoring()
					{
						var mortgagetop = $('[data-anchor="'+anchor_target_id+'"]').offset().top;
						$('html, body').animate({scrollTop : mortgagetop},'slow');
						$('html, body').animate({scrollTop : mortgagetop},'slow');

					}
					/*Anchor Navigation End*/
					
                    setTimeout(function(){
					    var tableStyleTimeout = '';
						var tdMax=0;
                        var thDivHeight=0;
                        var thPadHeight=0;

						$('.suntrust_account_find').each(function() {
							tableStyleTimeout = $(this).find('input[name=compareTableType]').val();
							if (tableStyleTimeout == 'comparison' || tableStyleTimeout == 'static') {
								$(this).find('table.table_compare tr').each(function(){
									if(!$(this).find('th').hasClass('account_bg_grey')) {
										tdMax=0;
										thDivHeight=0;
										thPadHeight=0;
										$(this).find('th').wrapInner("<div class='table_heading_content'></div>");
										$(this).find('td').each(function(){
											tdMax = Math.max($(this).innerHeight(),tdMax);
										});
										thDivHeight = $(this).find('th .table_heading_content').innerHeight();
										thPadHeight = thDivHeight + 30;
										if(thPadHeight > tdMax)
											$(this).css("height", thPadHeight+"px");
									}
								});
								$(this).find('th.account_bg_grey').each(function() {
									var th_height=0;
									var pad_th_height=0;
									if($(this).find('div').hasClass('table_heading_content')) {
										th_height=$(this).find('.table_heading_content').innerHeight();
										pad_th_height = th_height + 30;
									}
									else {
										th_height = $(this).innerHeight();
										pad_th_height = th_height + 10;
									}
									if(is_iPad){
					                	  if (pad_th_height >= 70) {
	                                            $(this).parent().css("height", pad_th_height + "px");
	                                            $(this).css("height", pad_th_height + "px");
	                                  }
	                                }
                                    if (isSafari) {
                                        if (pad_th_height > 70) {
                                            $(this).parent().css("height", pad_th_height + "px");
											$(this).css("height", pad_th_height + "px");
                                        }
                                        else {
											$(this).parent().css("height", "auto");
                                            $(this).css("height", "auto");
                                        }
                                    }
                                    else {
                                        if (pad_th_height > 50) {
                                            $(this).parent().css("height", pad_th_height + "px");
                                            $(this).css("height", pad_th_height + "px");
                                        }
                                    }
								});
                                if (tableStyleTimeout == 'comparison') {
                                    var lastTrHeight = 0;
                                    if($(this).find("table.table_compare tr:last-child th > div").hasClass("table_heading_content")) {
                                        lastTrHeight = $(this).find("tr:last-child th div.table_heading_content").innerHeight();
                                        if(lastTrHeight==0)
                                        	lastTrHeight=20;
                                        $(this).find("tr:last-child").css("height",(lastTrHeight+30)+"px");
                                        if(isIE)
                                            $(this).find("tr:last-child th").css("height",(lastTrHeight+30)+"px");
	                                    else
	                                        $(this).find("tr:last-child th").css("height",(lastTrHeight+31)+"px");                                        
                                    }
                                    else {
                                        lastTrHeight = $(this).find("table.table_compare tr:last-child").innerHeight();
                                        $(this).find("tr:last-child th").css("height",(lastTrHeight+1)+"px");
                                        $(this).find("tr:last-child").css("height",lastTrHeight+"px");
                                    }
                                    if(isFirefox) {
                                        $(this).find("table.table_compare").css("margin-left","1px");
                                    }
                                }
								/*if (isSafari) {
								 * 
										$('.account_bg_grey').css('padding-top', '10px');
										$(this).parent().css("height", "auto");
								}*/
							}
						});
					},600);                 

					if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
						// fix for hero code..
						if(!$(".suntrust-hero-signon-mobile-block").is(":visible")){
							$(".sun-product-carousel.signon_enabled").css("display","block");
						}
						else if($(".suntrust-hero-signon-mobile-block").is(":visible")){
							$(".sun-product-carousel.signon_enabled").css("display","none !important");
						}
					}
					
                    //Article modal window script starts//
                    var modalWindow;
                    $(document).off('click', '[data-suntrust-class="suntrust-content-modalWindow"]').on('click', '[data-suntrust-class="suntrust-content-modalWindow"]', function() {
                        modalWindow = $(this);
                        modalpopup(modalWindow);
                    });
                    /* Enter key */
                    $(document).on('keypress', '[data-suntrust-class="suntrust-content-modalWindow"]', function(e) {
                        modalWindow = $(this);
                        if (e.keyCode === 13) {
                            modalpopup(modalWindow);
                        }
                    });
                    
                    $(document).on('keypress','[data-suntrust-class="close-modal"]',function(e) {
                        if (e.keyCode === 13) {
                            $(".suntrust-modal").removeClass("active");
                            $('[data-suntrust-class="suntrust-modal-inner"]').empty();
                        }
                    });

                    function modalpopup() {
                        $('[data-suntrust-class="suntrust-modal-inner"]').empty();
                        var modal_url = modalWindow.data('anchor');
			            var modal_size = modalWindow.data('modal');
						
                        $.ajax({
                            url: modal_url,
                            dataType: 'text',
                            cache: false,
                            success: function(data) {
								$('div.sun-faqs-component-list').find('[data-sun-class="faqs-page-list-item-header"]').off('click');
                                var modal_page = $(data).filter("#suntrust-page");
                                $('[data-suntrust-class="suntrust-modal-inner"]').html(modal_page);
								$('[data-suntrust-class="example-modal"]').modalWindow();

                                if(modal_size == '_smallmodal'){

                                    $('[data-suntrust-class="example-modal"]').removeClass("suntrust-medium-window");
                                    $('[data-suntrust-class="example-modal"]').removeClass("suntrust-large-window");
                                    $('[data-suntrust-class="example-modal"]').addClass("suntrust-small-window");
                                }
                                if(modal_size == '_mediummodal'){

                                    $('[data-suntrust-class="example-modal"]').removeClass("suntrust-small-window");
                                    $('[data-suntrust-class="example-modal"]').removeClass("suntrust-large-window");
                                    $('[data-suntrust-class="example-modal"]').addClass("suntrust-medium-window");
                                }
                                if(modal_size == '_largemodal'){

                                    $('[data-suntrust-class="example-modal"]').removeClass("suntrust-small-window");
                                    $('[data-suntrust-class="example-modal"]').removeClass("suntrust-medium-window");
                                    $('[data-suntrust-class="example-modal"]').addClass("suntrust-large-window");
                                }
                                                               
                                 
                                 /*Fix for scroll disappears after small window */
 								var modalInnerheight = $(".suntrust-modal .suntrust-modal-inner #suntrust-page").innerHeight();
 								if(modalInnerheight < 575){
 									$(".suntrust-modal-container").addClass("suntrust-modal-container-newHeight");
 								}
 								else{
 									$(".suntrust-modal-container").removeClass("suntrust-modal-container-newHeight");
 								}
 								
 								/*Fix for scroll disappears after small window */ 
 							
 								var windowouterheight = $(window).outerHeight();
                                var modalouterheight = $(".suntrust-modal-container").outerHeight();                                                                                         
                                var outputouterheight = windowouterheight - modalouterheight;                       
                                var innermargin = outputouterheight/2;
                                $(".suntrust-modal-container").css({
                                    'margin-top': innermargin
                                });
                                
                                var query_modalVideo = $('.suntrust-modal-inner .video');
                                var isVisible_video = query_modalVideo.is(':visible');
                                
                                if (isVisible_video  === true) {
									$(".suntrust-modal-container").addClass("suntrust-modal-container-new");
                                    $(".suntrust-modal-container").removeClass("suntrust-modal-container-newHeight");
                                }
                                else{
									$(".suntrust-modal-container").removeClass("suntrust-modal-container-new");
                                    /*$(".suntrust-modal-container").addClass("suntrust-modal-container-newHeight");*/
                                }
                                 /*var query_modal = $('.suntrust-modal-inner .audiopodcast');
                             	var query_modalVideo = $('.suntrust-modal-inner .video');

                             // check if element is Visible
                                var isVisible_modal = query_modal.is(':visible');
                                
                                var isVisible_video = query_modalVideo.is(':visible');

                                if (isVisible_modal || isVisible_video  === true) {

                                // element is Visible
                                $(".suntrust-modal-container").removeClass("suntrust-modal-container-new");

                                }
                                else {

                                // element is Hidden

                                $(".suntrust-modal-container").addClass("suntrust-modal-container-new");

                                }*/
								
								/*Fix for videomodal comes add this script*/      
								/*
                                 var query_modalVideo = $('.suntrust-modal-inner .video');
								var isVisible_video = query_modalVideo.is(':visible');
								var modalWinHeight = $(".suntrust-modal-container").height();
								var modalWinHeight50p = modalWinHeight/2;
								if (isVisible_video  === true) {
									$(".suntrust-modal-container").css("margin-top", -modalWinHeight50p + "px");
								}
								*/
								/*Fix for videomodal comes add this script*/
								$(".suntrust-modal .suntrust-modal-container .suntrust-close").focus();                         
                            }
						});

                    }
                    /* Enter key */
if($('body').find('.rowContainer-RemoveTop-Border')) {
	$('.rowContainer-RemoveTop-Border').parents('.rowContFullWidthBGborderTop').css("border","0px");
}

                    //Article modal window script ends//
    //feature content Learn more link modification script//
	$(".suntrust-feature-links").each(function(){
	if($(this).is(":visible")){
	    $(this).parents(".featuredcontent").addClass("suntrust-SLfeature");
	}
	});

/* Student Lending Section Feature rte height set Start */

	if($('#modecheck').val() == undefined) {
    	SLSectionFeature();

        $(window).resize(function() {
            SLSectionFeature();
        });
	}

    function SLSectionFeature() {
        if ($(window).innerWidth() >= 768) {
            var maxHeight = 0;
            var maxHeightTitle = 0;
            $(".suntrust-SLfeature .suntrust-feature-header").each(function(){
               if ($(this).innerHeight() > maxHeightTitle) 
               { 
                   maxHeightTitle = $(this).innerHeight(); 
               }
            });
            $(".suntrust-SLfeature .rteelement").each(function(){
               if ($(this).innerHeight() > maxHeight) 
               { 
                   maxHeight = $(this).innerHeight(); 
               }
            });
            var totalMaxHeight = maxHeight;
            $(".suntrust-SLfeature .rteelement").each(function(){
               maxHeight = totalMaxHeight;
               var titleHeight = $(this).parents('.suntrust-user-path-container').find('.suntrust-feature-header').height();
               if(maxHeightTitle > titleHeight) {
                    maxHeight = maxHeight + (maxHeightTitle-titleHeight);
               }
               $(this).height(maxHeight);
            });
        }
        else{
            $(".suntrust-SLfeature .rteelement").css("height","auto");
        }
    }

/* Student Lending Section Feature rte height set End */


//feature content modification script//
                    
					/*Search form validation starts*/
					$(document).on('click','#Search, #PeopleSearch, #ProdSearch, #FaqSearch, #allresultsSearch',function(){
						var thisbtn = $(this);
						searchFormValidation(thisbtn);
					});
					$(document).on('keypress','#Search, #PeopleSearch, #ProdSearch, #FaqSearch, #allresultsSearch',function(e) {
						if (e.keyCode === 13) {
							var thisbtn = $(this);
							searchFormValidation(thisbtn);
						}
					})
					
					function searchFormValidation(thisbtn){
						$formSearch = thisbtn.closest('form.form-wealth-advisor');
						$formSearch.find('input[name="searchTerm"]').removeClass('sun-error');
						var zip = $formSearch.find('input[name="searchTerm"]').val();
						//var cir = $formSearch.find('#CircumstanceId').val();
						//var radius = $formSearch.find('#Radius').val();
						if(!(/[a-zA-Z0-9]/.test(zip)))
						{
							$formSearch.find('input[name="searchTerm"]').addClass('sun-error').focus();
							//$formSearch.find('.sun-select-container').addClass('sun-error');
							event.preventDefault();
						}
						else
						{
							$formSearch.submit();
						}
					}
					$('.zip-radius-search input').focus(function(){
						$(this).parents('.suntrust-wrapperContainer, .suntrust-rowContainer').css('overflow','visible');
					}).blur(function(){
						$(this).parents('.suntrust-wrapperContainer, .suntrust-rowContainer').css('overflow','hidden');
					})
					/*Search form validation ends*/
					/* Open a new account height set code Start */
					newAccountOpen();
					$(window).resize(function(){
						newAccountOpen();
					});
					function newAccountOpen() {
						$(".newAccount_Col").each(function() {
							var n = $(this),
								f = n.find('ul[data-suntrust-class="compare-account-list"]'),
							
								u = function() {
									var n = f.find(".suntrust-accounts-comparison-list-item-wrapper"),
										r = n.find(".suntrust-compare-header"),
										i = 0,
										t = 0;
									n.css("height", "auto"), 
									r.css("height", "auto"), 
									r.each(function() {
										var n = $(this);
										n.height() > t && (t = n.height());
									}), 
									r.css("height", t + "px"), 
									n.each(function() {
										var n = $(this);
										n.height() > i && (i = n.height());
									}), 
									n.css("height", i + "px");
								};	
							u();
						});
					}
					/* Open a new account height set code End */
					$(document).off("click","#bbCloseBox");
                    $(document).on("click","#bbCloseBox",function(){
						$(this).toggleClass('bbRotate');
						$(this).parent().next().slideToggle();						
					});
					var stickyDivLength=$('.openaccountcontresp').length;
					$(document).scroll(function() {
						$('li').each(function(){
                            if($(this).attr('id')=="bbStickyNavBox") 
							{ 
								if($(window).scrollTop() >= ($('#suntrust-page .openaccountcontresp:last').offset().top + 150)) { 
									$('#bbStickyNavBox').hide();
								}
								else {
									$('#bbStickyNavBox').show();
								}
							}
						});
					});				
					/*SpeeBumpCheck Function call*/
					speedBumpCheck()
					/*SpeeBumpCheck Function call*/
					
					/******** Desktop Hero Sign on Start ****/
					var checkmodevalue,checkmode;
					checkmodevalue = $("#modecheck").val();
					if(checkmodevalue=="edit")
                    {
                        checkmode="?wcmmode=disabled";
                    }
                    else{
                        checkmode="?";
                    }

					if($('.suntrust-hero-signon-desktop-block').is(":visible"))
					{						
						var desktop_signonMMode,desktop_config;
						if($("#maintenancemode").val()=="true")						
						{
							if(checkmodevalue=="edit")
                            {
                            	desktop_signonMMode ="&maintenance-mode-override=true";
                            }else
                            {
                                desktop_signonMMode ="maintenance-mode-override=true";
                            }
							desktop_config=$("#signonpageconfig").val()+".hero_desktop.html"+checkmode+desktop_signonMMode;
						}
						else
						{
							desktop_config=$("#signonpageconfig").val()+".hero_desktop.html"+checkmode;
						}
							//alert("signonMMode1 Desktop======"+desktop_config);
						$.ajax({
								url : desktop_config,
								dataType :'text',
								cache : false, 
								success : function(data) {											
									var hhtml = $(data).find('#desktopherosignon').html();
									$('.suntrust-hero-signon-desktop-block').html(hhtml);
									sunScriptfun();
									speedBumpCheck($('.suntrust-hero-signon-desktop-block'));
									allowParameter($('.suntrust-hero-signon-desktop-block'));
								}
						})					   
					}
					/******** Desktop Hero Sign on End ****/
					
					/******** Mobile Hero Sign on Start ****/
					if($('.suntrust-hero-signon-mobile-block').is(":visible"))
					{						
						var mobile_signonMMode,mobile_config;
						
						if($("#maintenancemode").val()=="true")						
						{
                            if(checkmodevalue=="edit")
                            {
                            	mobile_signonMMode ="&maintenance-mode-override=true";
                            }else
                            {
                                mobile_signonMMode ="maintenance-mode-override=true";
                            }
							mobile_config=$("#signonpageconfig").val()+".hero_mobile.html"+checkmode+mobile_signonMMode;
						}
						else
						{
							mobile_config=$("#signonpageconfig").val()+".hero_mobile.html"+checkmode;
						}
							//alert("signonMMode1 Desktop======"+mobile_config);
						$.ajax({
								url : mobile_config,
								dataType :'text',
								cache : false, 
								success : function(data) {											
									var hhtml = $(data).find('#mobileherosignon').html();
									$('.suntrust-hero-signon-mobile-block').html(hhtml);
									sunScriptfun();
									speedBumpCheck($('.suntrust-hero-signon-mobile-block'));
									allowParameter($('.suntrust-hero-signon-mobile-block'));
								}
						})					   
					}
					/******** Mobile Hero Sign on End ****/
					/******** Global Sign on Start ****/			
					var global_signonMMode,global_config;
					
					if($("#globalmaintenancemode").val()=="true")						
					{
						global_signonMMode ="&maintenance-mode-override=true";
                        if(checkmodevalue=="edit")
                        {
                            global_signonMMode ="&maintenance-mode-override=true";
                        }else
                        {
                            global_signonMMode ="maintenance-mode-override=true";
                        }
						global_config=$("#globalsignonconfig").val()+".signon.html"+checkmode+global_signonMMode;
					}
					else
					{
						global_config=$("#globalsignonconfig").val()+".signon.html"+checkmode;
					}
						//alert("signonMMode1 Desktop======"+mobile_config);
					if($("#globalsignonconfig").val() !="" && $("#globalsignonconfig").val() !=undefined)
					{
					$.ajax({
							url : global_config,
							dataType :'text',
							cache : false, 
							success : function(data) {											
								var hhtml = $(data).find('#globalSignon').html();
								$('.suntrust-header-signon-block').html(hhtml);
								sunScriptfun();
								speedBumpCheck($('.suntrust-header-signon-block'));
								allowParameter($('.suntrust-header-signon-block'));
							}
					})	
					}
					
					/********  Global Sign on End ****/
					
					
					/********Moved from signon to here for pmd inline script ****/
					var bzCookieValueRC = 'personal-banking';
			        var bzDefaultLOB = '';
			        var bzCookieMapping = '';
			        var cookieDuration = '';
			        var bzCookieName = '';			
										
			        /*allow param's call*/					
					allowParameter();
					/*allow param's*/	
				});

/*FAQ Component Start*/
/*$(document).on('click', '.sun-faqs-page-list-item-header', function() {
	$(this).toggleClass('sun-active');
	$(this).next('.sun-faqs-page-list-item-detail').toggle();
})*/
$('div.sun-faqs-component-list').find('[data-sun-class="faqs-page-list-item-header"]').click(function(event) 
{
	event.preventDefault();
	var $this = $(this), $parent = $this
			.closest('[data-sun-class="faqs-page-list-item"]'), $target = $parent
			.find('[data-sun-class="faqs-page-list-item-detail"]');

	$this.toggleClass('sun-active');
	$target.toggleClass('sun-active');
	$this.focus();
});			
/*FAQ Component Start*/

/* Accordion out of ready Script Start */


/* Accordion Functionality start */
$(document).on('click', '.faq .suntrust-faq-title', function() {

	/* Dialog checkbox values */
	var collapse = $(this).parents('.faq').attr('data-collapse');
	//var expand = $('.suntrust-faq-title').attr('data-expand');
	if (collapse == 'true') {
		collapse = true
	} else {
		collapse = false
	}
	/* Dialog checkbox values */

	if ($(this).hasClass('suntrust-open')) {
		$(this).removeClass('suntrust-open');
		$(this).next('ul.suntrust-faqs-list').hide();
	} else {
		if (collapse && !$(this).parents().hasClass('contactUs_accordion')) {
			$('.accordion > div > ul.faq .suntrust-faq-title').removeClass('suntrust-open');
			$('.accordion > div > ul.faq ul.suntrust-faqs-list').hide();
			$(this).addClass('suntrust-open');
			$(this).next('ul.suntrust-faqs-list').toggle();
		} 
        else if (collapse && $(this).parents().hasClass('contactUs_accordion')) {
			$('.contactUs_accordion .suntrust-faq-title').removeClass('suntrust-open');
			$('.contactUs_accordion ul.suntrust-faqs-list').hide();
			$(this).addClass('suntrust-open');
			$(this).next('ul.suntrust-faqs-list').toggle();
		} else {
			$(this).addClass('suntrust-open');
			$(this).next('ul.suntrust-faqs-list').toggle();
		}
		var btnTop = $(this).parent('li').offset().top,
		scrollTo = $(window).scrollTop() >= btnTop ? btnTop - 100 : btnTop;
		$('html, body').stop().animate({
			scrollTop: scrollTo,
			easing: 'slow'
		}, 400);
	}
})
$(document).on('keypress', '.faq .suntrust-faq-title', function(e) {
	if (e.which == 13) {
		/* Dialog checkbox values */
		var collapse = $(this).parents('.faq').attr('data-collapse');
		var expand = $('.suntrust-faq-title').attr('data-expand');
		if (collapse == 'true') {
			collapse = true
		} else {
			collapse = false
		}
		/* Dialog checkbox values */

		if ($(this).hasClass('suntrust-open')) {
			$(this).removeClass('suntrust-open');
			$(this).next('ul.suntrust-faqs-list').hide();
		} else {
			if (collapse && !$(this).parents().hasClass('contactUs_accordion')) {
				$('.accordion > div > ul.faq .suntrust-faq-title').removeClass('suntrust-open');
				$('.accordion > div > ul.faq ul.suntrust-faqs-list').hide();
				$(this).addClass('suntrust-open');
				$(this).next('ul.suntrust-faqs-list').toggle();
			} 
	        else if (collapse && $(this).parents().hasClass('contactUs_accordion')) {
				$('.contactUs_accordion .suntrust-faq-title').removeClass('suntrust-open');
				$('.contactUs_accordion ul.suntrust-faqs-list').hide();
				$(this).addClass('suntrust-open');
				$(this).next('ul.suntrust-faqs-list').toggle();
			} else {
				$(this).addClass('suntrust-open');
				$(this).next('ul.suntrust-faqs-list').toggle();
			}
			var btnTop = $(this).parent('li').offset().top,
			scrollTo = $(window).scrollTop() >= btnTop ? btnTop - 100 : btnTop;
			$('html, body').stop().animate({
				scrollTop: scrollTo,
				easing: 'slow'
			}, 400);
		}
	}
})
$(document).on('click', '.suntrust-faqs-list-item-header', function() {
	$(this).toggleClass('suntrust-active');
	$(this).next('.suntrust-faqs-list-item-detail').toggle();
})
/* Accordion Functionality End */
/* Accordion out of ready Script End */

// Cookie handling Start
function getCookie(w) {
	var cName = "", pCookies = new Array();
	pCookies = document.cookie.split('; ');
	for (var bb = 0; bb < pCookies.length; bb++) {
		var nmeVal;
		nmeVal = new Array();
		nmeVal = pCookies[bb].split('=');
		if (nmeVal[0] == w) {
			cName = unescape(nmeVal[1]);
		}
	}
	return cName;
}
function get_cookieNoUnescape(cookie_name) {
	var results = document.cookie.match('(^|;) ?' + cookie_name
			+ '=([^;]*)(;|$)');
	if (results)
		return results[2];
	else
		return null;
}
function delete_cookie(name) {
	document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
};
function printCookies(w) {
	var cStr = "", pCookies = new Array();
	pCookies = document.cookie.split('; ');
	for (var bb = 0; bb < pCookies.length; bb++) {
		var nmeVal = new Array();
		nmeVal = pCookies[bb].split('=');
		if (nmeVal[0]) {
			cStr += nmeVal[0] + '=' + unescape(nmeVal[1]) + '; ';
		}
	}
	return cStr;
}
function setCookie(name, value, expires, path, domain, secure) {
	document.cookie = name + "=" + escape(value)
			+ (expires ? "; expires=" + setExpiration(expires) : "")
			+ (path ? "; path=" + path : "")
			+ (domain ? "; domain=" + domain : "")
			+ (secure ? "; secure;" : "");
}
function setCookieNoEscape(name, value, expires, path, domain, secure) {
	document.cookie = name + "=" + value
			+ (expires ? "; expires=" + setExpiration(expires) : "")
			+ (path ? "; path=" + path : "")
			+ (domain ? "; domain=" + domain : "")
			+ (secure ? "; secure;" : "");
}
function setExpiration(cookieLife) {
	var today = new Date();
	var expr = new Date(today.getTime() + cookieLife * 24 * 60 * 60 * 1000);
	return expr.toGMTString();
}
// Cookie handling End

/* Speed Bump start */
function speedBumpCheck(targetElement) {
	var speedBumpCheck = targetElement ? targetElement : $(document);
	var servletUrl='/dotcom/external?clickedUrl=';
	var domainUrl="www.suntrust.com";
	var hyperLink;
	speedBumpCheck.find("a[href^='https://']").each(function(e) {
		hyperLink=$(this).attr('href');
		if (hyperLink.toLocaleLowerCase().indexOf(domainUrl) == -1) {
			$(this).attr('href',servletUrl+hyperLink);
			$(this).attr("target","_blank");
		}                              
	});
	speedBumpCheck.find("a[href^='http://']").each(function(e) {           
		hyperLink=$(this).attr('href');
		if (hyperLink.toLocaleLowerCase().indexOf(domainUrl) == -1) {
			$(this).attr('href',servletUrl+hyperLink); 
			$(this).attr("target","_blank");
		}                                                                                                              
	});
	speedBumpCheck.find("a[href^='www.']").each(function(e) {
		hyperLink=$(this).attr('href');
		if (hyperLink.toLocaleLowerCase().indexOf(domainUrl) == -1) {
			$(this).attr('href',servletUrl+hyperLink);
			$(this).attr("target","_blank");
		}                                                                                                              
	});
	
	/*Adding Span tag in anchor tag for ADA Start*/
	if($('.newWindowAlertSwitch').val()=="true")
	{
		var spanElement = "<span class='suntrust-accessible-text'>Opens a new window</span>";
		speedBumpCheck.find('a[target="_blank"]').each(function(){
			if(!$(this).hasClass('langLink'))
			{
				$(this).addClass("newWindow").append(spanElement);
			}
		});

		speedBumpCheck.find( ".newWindow" ).on( "mouseenter", function() {
			var popup_top = $(this).offset().top + $(this).innerHeight();
			var popup_left = $(this).offset().left;
			if($(this).hasClass('suntrust-rtl-links')){
				popup_left += 64;
			}
			$('.newWindowpopup').css({'top':popup_top,'left':popup_left,'display':'block'});
		}).on( "mouseleave", function() {
			$('.newWindowpopup').css({'display':'none'});
		});

		speedBumpCheck.find(".newWindow").focus(function(){
			var popup_top = $(this).offset().top + $(this).innerHeight();
			var popup_left = $(this).offset().left;
			if($(this).hasClass('suntrust-rtl-links')){
				popup_left += 64;
			}
			$('.newWindowpopup').css({'top':popup_top,'left':popup_left,'display':'block'});
		}).blur(function() {
			$('.newWindowpopup').css({'display':'none'});
		});
	}
	/*Adding Span tag in anchor tag for ADA End*/
	
}
/* Speed Bump end */

//allow param's function definition Start
function allowParameter(targetElement){
	function getParameterByNameAllow(name) {
		url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
			results = regex.exec(url);
		if (!results) return null;
		if (!results[2]) return '';
		return results[2].replace(/\+/g, " ");
	}
		
	var typeValue = getParameterByNameAllow("type");
	var accounTypeValue = getParameterByNameAllow("accounttype");
	var accountNumberValue = getParameterByNameAllow("accountnumber");
	
	var urlPattern="suntrust.com/UI/login"; 
	var domObj = targetElement ? targetElement : $(document);	
	if(typeValue != null && typeValue != undefined && typeValue != "" && accounTypeValue != null && accounTypeValue != undefined && accounTypeValue != "" && accountNumberValue != null && accountNumberValue != undefined && accountNumberValue != ""){
	domObj.find('a[href*="suntrust.com/UI/login"]').each(function(){
		var hrefAttributeValue = $(this).attr("href");
		if(hrefAttributeValue.indexOf(urlPattern) != -1){
			$(this).attr("href","https://onlinebanking.suntrust.com/UI/enrollment?type="+typeValue+"&accounttype="+accounTypeValue+"&accountnumber="+accountNumberValue);                     
		}		
	});
	}
}
//allow param's function definition End

//Section Feature function definition Start
function suntrustdynamicwidth(count,thisvariable,maxHeight){
	if (thisvariable.parents('.suntrust-section3A').is('.suntrust-section3A'))
	{
		if (count < 9)
		{
			var width = 100;
			if (window.innerWidth > 940)
			{
				if (count >= 5)
				{
					width = (100 / 5);
				}
				else
				{
					width = (100 / count);
				}
			}
			else if (window.innerWidth <= 940 && window.innerWidth >= 601)
			{
				width = (100 / 3);
			}
			else if (window.innerWidth <= 600)
			{
				width = 100;
			}
			thisvariable.find("li").css("width",width + "%");

			if (window.innerWidth > 639)
			{
				thisvariable.find("li").each(function (){
					maxHeight = maxHeight > $(this).height() ? maxHeight : $(this).height();
				});
				thisvariable.find("li").each(function ()
				{
					$(this).height(maxHeight);
				});
			}
			else
			{
				thisvariable.find("li").each(function ()
				{
					$(this).height('auto');
				});
			}
		}
	}
	else
	{
		if (count > 2 & count < 9)
		{
			var width = 100;
			if (window.innerWidth > 940)
			{
				width = (100 / count);
			}
			else if (window.innerWidth <= 940 && window.innerWidth >= 601)
			{
				width = (100 / 3);
			}
			else if (window.innerWidth <= 600)
			{
				width = 100;
			}
			thisvariable.find("li").css("width",width + "%");
			if (window.innerWidth > 639)
			{
				thisvariable.find("li").each(function (){
					maxHeight = maxHeight > $(this).height() ? maxHeight : $(this).height();
				});
				thisvariable.find("li").each(function ()
				{
					$(this).height(maxHeight);
				});
			}
			else
			{
				thisvariable.find("li").each(function (){
					$(this).height('auto');
				});
			}
		}
	}
}
function suntrustfourcolwidth(count,thisvariable,maxHeight){
	if (count < 9)
	{
		var width = 100;
		if (window.innerWidth > 800)
		{
			if (thisvariable.parents('.suntrust-section3A').is('.suntrust-section3A'))
			{
				if (count >= 2)
				{
					width = (100 / 2);
				}
				else
				{
					width = 100;
				}
			}
			else
			{
				if (count >= 4)
				{
					width = (100 / 4);
				}
				else
				{
					width = (100 / count);
				}
			}
		}
		else if (window.innerWidth <= 768)
		{
			width = 100;
			$(this).find("li").each(function ()
			{
				$(this).css('height', 'auto !important');
			});
		}
		thisvariable.find("li").css("width", width + "%");
		if (window.innerWidth > 800)
		{
			thisvariable.find("li").each(function (){
				maxHeight = maxHeight > $(this).height() ? maxHeight : $(this).height();
			});
			thisvariable.find("li").each(function ()
			{
				$(this).height(maxHeight);
			});
		}
		else
		{
			thisvariable.find("li").each(function (){
				$(this).height('auto');
			});
		}
	}
}
//Section Feature function definition End

//Table Comparision function definition Start
function tableCompare(tableStyle) {
	if (window.innerWidth < 768) {
		if (tableStyle == 'static') {
			$(this).parent('.suntrust_account_find').find('.table_compare_mobile').show();
			var static_tdCount = $(this).parent('.suntrust_account_find').find('.table_compare_mobile').find('tr:nth-child(2) td').length;
			if (static_tdCount == 1) {
				$(this).parent('.suntrust_account_find').find('.table_compare_mobile').find('td').addClass('text-center');
			} else {
				$(this).parent('.suntrust_account_find').find('.table_compare_mobile').find('td').removeClass('text-center');
			}
		}
	} else {
		$('.table_compare_mobile').hide();	  
	}
}
//Table Comparision function definition End

// Object to manipulate the hash
var HashSearch = new function() {
	var params;

	this.set = function(key, value) {
		params[key] = value;
		this.push();
	};

	this.remove = function(key, value) {
		delete params[key];
		this.push();
	};

	this.get = function(key, value) {
		return params[key];
	};

	this.keyExists = function(key) {
		return params.hasOwnProperty(key);
	};

	this.push = function() {
		var hashBuilder = [], key, value;

		for (key in params)
			if (params.hasOwnProperty(key)) {
				key = escape(key), value = escape(params[key]); // escape(undefined)
				// ==
				// "undefined"
				hashBuilder.push(key
						+ ((value !== "undefined") ? '=' + value : ""));
			}

		window.location.hash = hashBuilder.join("&");
	};

	(this.load = function() {
		params = {};
		var hashStr = window.location.hash, hashArray, keyVal;
		hashStr = hashStr.substring(1, hashStr.length);
		hashArray = hashStr.split('&');

		for (var i = 0; i < hashArray.length; i++) {
			keyVal = hashArray[i].split('=');
			params[unescape(keyVal[0])] = (typeof keyVal[1] != "undefined") ? (unescape(keyVal[1]))
					.replace(/\+/g, ' ')
					: keyVal[1];
		}

	})();

};


/*
 * Start - To supress the CTA anchor links when the Hero background image have
 * links
 */
$('.sun-product-carousel-slide-wrapper a, .suntrust-login-form-hero-signon input, div.suntrust-hero-signon-desktop-black, div.suntrust-hero-mobile-signon').click(function(e) {
	e.stopPropagation();
});
/* End */

/*
 * FlashDetect.JS_RELEASE = "1.0.4"
 */
var FlashDetect = new function() {
	var self = this;
	self.installed = false;
	self.raw = "";
	self.major = -1;
	self.minor = -1;
	self.revision = -1;
	self.revisionStr = "";
	var activeXDetectRules = [ {
		"name" : "ShockwaveFlash.ShockwaveFlash.7",
		"version" : function(obj) {
			return getActiveXVersion(obj);
		}
	}, {
		"name" : "ShockwaveFlash.ShockwaveFlash.6",
		"version" : function(obj) {
			var version = "6,0,21";
			try {
				obj.AllowScriptAccess = "always";
				version = getActiveXVersion(obj);
			} catch (err) {
			}
			return version;
		}
	}, {
		"name" : "ShockwaveFlash.ShockwaveFlash",
		"version" : function(obj) {
			return getActiveXVersion(obj);
		}
	} ];
	var getActiveXVersion = function(activeXObj) {
		var version = -1;
		try {
			version = activeXObj.GetVariable("$version");
		} catch (err) {
		}
		return version;
	};
	var getActiveXObject = function(name) {
		var obj = -1;
		try {
			obj = new ActiveXObject(name);
		} catch (err) {
			obj = {
				activeXError : true
			};
		}
		return obj;
	};
	var parseActiveXVersion = function(str) {
		var versionArray = str.split(",");
		return {
			"raw" : str,
			"major" : parseInt(versionArray[0].split(" ")[1], 10),
			"minor" : parseInt(versionArray[1], 10),
			"revision" : parseInt(versionArray[2], 10),
			"revisionStr" : versionArray[2]
		};
	};
	var parseStandardVersion = function(str) {
		var descParts = str.split(/ +/);
		var majorMinor = descParts[2].split(/\./);
		var revisionStr = descParts[3];
		return {
			"raw" : str,
			"major" : parseInt(majorMinor[0], 10),
			"minor" : parseInt(majorMinor[1], 10),
			"revisionStr" : revisionStr,
			"revision" : parseRevisionStrToInt(revisionStr)
		};
	};
	var parseRevisionStrToInt = function(str) {
		return parseInt(str.replace(/[a-zA-Z]/g, ""), 10) || self.revision;
	};
	self.majorAtLeast = function(version) {
		return self.major >= version;
	};
	self.minorAtLeast = function(version) {
		return self.minor >= version;
	};
	self.revisionAtLeast = function(version) {
		return self.revision >= version;
	};
	self.versionAtLeast = function(major) {
		var properties = [ self.major, self.minor, self.revision ];
		var len = Math.min(properties.length, arguments.length);
		for (i = 0; i < len; i++) {
			if (properties[i] >= arguments[i]) {
				if (i + 1 < len && properties[i] == arguments[i]) {
					continue;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	};
	self.FlashDetect = function() {
		if (navigator.plugins && navigator.plugins.length > 0) {
			var type = 'application/x-shockwave-flash';
			var mimeTypes = navigator.mimeTypes;
			if (mimeTypes && mimeTypes[type] && mimeTypes[type].enabledPlugin
					&& mimeTypes[type].enabledPlugin.description) {
				var version = mimeTypes[type].enabledPlugin.description;
				var versionObj = parseStandardVersion(version);
				self.raw = versionObj.raw;
				self.major = versionObj.major;
				self.minor = versionObj.minor;
				self.revisionStr = versionObj.revisionStr;
				self.revision = versionObj.revision;
				self.installed = true;
			}
		} else if (navigator.appVersion.indexOf("Mac") == -1
				&& window.execScript) {
			var version = -1;
			for (var i = 0; i < activeXDetectRules.length && version == -1; i++) {
				var obj = getActiveXObject(activeXDetectRules[i].name);
				if (!obj.activeXError) {
					self.installed = true;
					version = activeXDetectRules[i].version(obj);
					if (version != -1) {
						var versionObj = parseActiveXVersion(version);
						self.raw = versionObj.raw;
						self.major = versionObj.major;
						self.minor = versionObj.minor;
						self.revision = versionObj.revision;
						self.revisionStr = versionObj.revisionStr;
					}
				}
			}
		}
	}();
};
FlashDetect.JS_RELEASE = "1.0.4";
/** Script for sign on OLB/OCM ends * */

//Search List Show Location Start
$(document).on("click",".peopleSearch_tog",function(){
	$(this).next().slideToggle();
	if($(this).find('strong').text()=="Show Locations") {
		$(this).find('strong').text('Hide Locations');
		$(this).find('span').text('-');
	}
	else if($(this).find('strong').text()=="Hide Locations") {
		$(this).find('strong').text('Show Locations');
		$(this).find('span').text('+');
	}
});
//Search List Show Location End	
/*Mobile Sign On Touch screen Script starts*/
	/*$(function() {
		var $hero = $('.sun-product-carousel.signon_enabled');
		var $signon = $('.suntrust-hero-mobile-signon'); 
		if (!Modernizr.touch) {   
		$hero.css('display', 'block');
		$signon.css('display', 'none'); 
		}
		else {
		$hero.css('display', 'none');
		$signon.css('display', 'block'); 
		}
	});*/
/*Mobile Sign On Touch screen Script Ends*/
//Generic error callback for AjaxSubmit
function ajaxError() {
	console.log("An error occurred. Please try again.");
}