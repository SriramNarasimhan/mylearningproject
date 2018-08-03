$(document).ready(function () {
	var isIE = navigator.appName == 'Microsoft Internet Explorer' || !!(navigator.userAgent.match(/Trident/) || navigator.userAgent.match(/rv:11/));
	var isFirefox = navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
	var isSafari = /Safari/.test(navigator.userAgent) && /Apple Computer/.test(navigator.vendor);
	var is_iPad = navigator.userAgent.match(/iPad/i) != null;
	
	if($('.component-decisiontree').length!=0)
	{
		var decisionTree = $('#DecisionTreeComponent');
		var decisiontreeCtaAjax = $('#suntrust-page');
		var carouselType = $('#isPageCarousel').val();		
		
		//Clearing and Setting Navigation History
		var navigationHistory =[];
		localStorage.setItem("navigationHistory",JSON.stringify(navigationHistory));
		
		function dfsbgSet()
		{
			//Setting Background Image
			$('.component-decisiontree ').find('style').remove();
			var retina_image='',mobile_image='';
			if($("#retinaImage").val())
			{
				retina_image = '@media (-webkit-min-device-pixel-ratio: 2), (min-resolution: 192dpi) {.suntrust-dfs-carousel {background-image: url('+$("#retinaImage").val()+');}}';
			}
			if($("#mobileImage").val())
			{
				mobile_image ='@media (max-width:767px) {.suntrust-dfs-carousel {background-image: url('+$("#mobileImage").val()+');}}';
			}
			var bgstyle = '<style type="text/css">.suntrust-dfs-carousel {background-image: url('+$("#desktopImage").val()+');}'+retina_image + mobile_image+'</style>';
			$('.component-decisiontree').prepend(bgstyle);
			
			//Setting Font Theme
			var font_theme=$("#font-theme").val();
            if(font_theme==""){
                $('.component-decisiontree').removeClass('suntrust-dfs-dark-theme');
                }
            else
            {
				$('.component-decisiontree').addClass('suntrust-dfs-dark-theme');
            }
			
			$('#navigationHistory').data('navigationHistory',navigationHistory);        
			var dfstab_length = $(".suntrust-product-position-list-alt li").length;
			if(window.innerWidth<=600)
			{
				var ptab = $('.suntrust-product-position-list-alt').height() + 20;
				decisionTree.css({'padding-top':ptab+'px'});
			}
			else
			{
				if(dfstab_length<4)
				{
					$(".suntrust-product-position-list-alt li").css({'width':'210px'});
				}
			}
			
			//For Slide Alignment
			$('[data-isrte="false"]').removeClass();

			//Setting Navigation for Next button
			if(!$('.suntrust-product-position-list-alt li.sun-active').is(":last-child"))
			{
				next_ajaxurl =$('.suntrust-product-position-list-alt li.sun-active').next('li').find('a').data('ajaxurl');				
				next_href = $('.suntrust-product-position-list-alt li.sun-active').next('li').find('a').attr('href');
				$('.sun-product-carousel-next-arrow').attr('href',next_href).data('ajaxurl',next_ajaxurl);
				if($('#isPageCarousel').val() == "true")
				{
					next_pageurl =$('.suntrust-product-position-list-alt li.sun-active').next('li').find('a').data('pageajaxurl');
					$('.sun-product-carousel-next-arrow').data('pageajaxurl',next_pageurl);
				}
			}
			else
			{
				$('.sun-product-carousel-next-arrow').hide();
			}
			
			//Setting Navigation for Previous button
			/*navigationHistory = localStorage.getItem("navigationHistory");
			navigationHistory = JSON.parse(navigationHistory);*/
			if(navigationHistory.length!=0)
			{
				var arrlength = navigationHistory.length;
				href_value = navigationHistory[arrlength-1].href;
				ajax_value = navigationHistory[arrlength-1].ajaxurl;
				$('.sun-product-carousel-prev-arrow').attr('href',href_value);
				$('.sun-product-carousel-prev-arrow').data('ajaxurl',ajax_value);				
				if($('#isPageCarousel').val() == "true")
				{
					prev_pageurl = ajax_value.replace('/slidequestinfo','');
					$('.sun-product-carousel-prev-arrow').data('pageajaxurl',prev_pageurl);
				}
			}
			else
			{
				if(!$('.suntrust-product-position-list-alt li.sun-active').is(":first-child") )
				{
					prev_ajaxurl = $('.suntrust-product-position-list-alt li.sun-active').prev('li').find('a').data('ajaxurl');
					prev_href = $('.suntrust-product-position-list-alt li.sun-active').prev('li').find('a').attr('href');
					$('.sun-product-carousel-prev-arrow').attr('href',prev_href).data('ajaxurl',prev_ajaxurl);
					if($('#isPageCarousel').val() == "true")
					{
						prev_pageurl = $('.suntrust-product-position-list-alt li.sun-active').prev('li').find('a').data('pageajaxurl');
						$('.sun-product-carousel-prev-arrow').data('pageajaxurl',prev_pageurl);
					}
				}
				else
				{				
					$('.sun-product-carousel-prev-arrow').hide();
				}
			}
			
		}
		dfsbgSet();
		
		$( window ).resize(function() {
			var dfstab_length = $(".suntrust-product-position-list-alt li").length;
			if(window.innerWidth<=600)
			{
				var ptab = $('.suntrust-product-position-list-alt').height() + 20;
				decisionTree.css({'padding-top':ptab+'px'});
			}
			else
			{
				if(dfstab_length<4)
				{
					$(".suntrust-product-position-list-alt li").css({'width':'210px'});
				}
			}
		})
		
		function setDecisionTreeCookie(seoName) {
			var cookieConfig = {
				expire: 1,
				name: 'DecisionTree',
				value: seoName,
				secure: window.location.protocol == "https:" ? true : false,
				domain: window.location.host,
				path: "/"
			};
			setCookie(cookieConfig.name, cookieConfig.value, cookieConfig.expire, cookieConfig.path, cookieConfig.domain, cookieConfig.secure);
		}
		
		/*Handling the Navigation History*/
		function setNavigationhistory(direction){
			navigationHistory = $('#navigationHistory').data('navigationHistory');
			if(direction =="Steps" || direction == "onload")
			{			
				navigationHistory =[];
			}
			else if(direction =="Previous")
			{
				navigationHistory.pop();
			}     
			else
			{
				history_url = $('#currentSlidePath').val();
				history_ajxurl = $('#currentSlideAjaxUrl').val();
				navigationHistory.push ({'href':history_url,'ajaxurl':history_ajxurl});
				/*if(navigationHistory.length==0)
				{
					navigationHistory[0] ={'href':history_url,'ajaxurl':history_ajxurl};
				}
				else{
					navigationHistory[navigationHistory.length] ={'href':history_url,'ajaxurl':history_ajxurl};
				}*/

			}
			localStorage.setItem("navigationHistory", JSON.stringify(navigationHistory));
		}
		/*Handling the Navigation History End*/
		
		function getDecisionTreeSlide(pageurl,ajxUrl, direction) {
			if(ajxUrl!=undefined)
			{
				var dfs_path = window.location.pathname.split('.');
				var dfs_cookie =dfs_path[0]+"#"+ajxUrl;
				setDecisionTreeCookie(dfs_cookie);
				if(carouselType == "true")
				{
					AjaxSubmit(pageurl, $('.dfsheader').next(),direction);
					//console.log("page carousel")
				}
				else {
					AjaxSubmit(ajxUrl, $('#DecisionTreeSlide'),direction);
				}
				
			}
		}
		

		/*Checking cookie on pageload*/
		var dtCookie = getCookie('DecisionTree');
		var authormode = $('#environemnt').val();
		if(authormode !="author")
		{
			if (dtCookie != "") {
				var dtCookieValues = dtCookie.split('#');
				var dfs_cookie_path = dtCookieValues[0];
				var dfs_cookie_ajax = dtCookieValues[1];
				var location_path = window.location.pathname.split('.');
				var tmp_name = dtCookieValues[1].split('/_jcr_content/');
				var dfs_slide_name = tmp_name[0].split('/');

				if (dfs_cookie_path == location_path[0] && dfs_slide_name[dfs_slide_name.length-1] != $('#currentSlide').val()) {
					if(carouselType == "true")
					{
						dfs_cookie_ajax = dtCookieValues[1].replace('/slidequestinfo','');
						AjaxSubmit(dfs_cookie_ajax, $('.dfsheader').next(),"onload");
					}
					else {						
						AjaxSubmit(dfs_cookie_ajax, $('#DecisionTreeSlide'),"onload");
					}
				}
			}
			else {
				var cookie_values = window.location.pathname.split('.');
				var cookie_value = cookie_values[0]+"#"+$('#currentSlide').val();
				setDecisionTreeCookie(cookie_value);
			}
		}
		/*Checking cookie on pageload End*/

		
		//Prev Next Click
		decisiontreeCtaAjax.on('click', '.sun-decisiontree-slide-link', function (event) {
			event.preventDefault();
			var ajaxurl = $(this).data('ajaxurl');		
			var direction = $(this).data('direction');
			var pageurl = $(this).data('pageajaxurl');
			/*navigationHistory = localStorage.getItem("navigationHistory");        
			navigationHistory = JSON.parse(navigationHistory);*/
			getDecisionTreeSlide(pageurl,ajaxurl, $(this).data('direction'));		

		});

		
		//Rebinding the component Events
		function rebindingEvents(target){
			/*Uniform function for select dropdown Start*/
			$('select').uniform({
				selectClass : 'sun-select-container',
				selectAutoWidth : false
			}).each(
				function() {
					$(this).siblings("span").attr("aria-hidden", true);
				});
			/*Uniform function for select dropdown End*/
			
			/*Combo Box Anchor Navigation Start*/
            $(target).on("change",".suntrust-select.suntrust-comboBoxloans",function(){                        
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
			
			/* Tab Set Start */
				$(target).find('.sun-feature-tabs_tabset').responsiveTabs();
			/* Tab Set End*/
			
			//Section Feature Width Set Start
			$(target).find('ul.suntrust-dynamic-width').each(function (){
				var maxHeight = -1;
				var count = $(this).find("li").length;
				var thisvariable = $(this);
				suntrustdynamicwidth(count,thisvariable,maxHeight);
			});
			$(target).find('ul.suntrust-feature-list-details.suntrust-four-col-info').each(function (){
				var thisvariable = $(this);
				var maxHeight = -1;
				var count = $(this).find("li").length;
				setTimeout(function ()
				{
					suntrustfourcolwidth(count,thisvariable, maxHeight);
				}, 400);
			});
			//Section Feature Width Set End
			
			//feature content Learn more link Start
			$(target).find(".suntrust-feature-links").each(function(){
				if($(this).is(":visible")){
					$(this).parents(".featuredcontent").addClass("suntrust-SLfeature");
				}
			});
			//feature content Learn more link End
			
			/* Accordion Script for Page On Load functionality Start */
			$(target).find(".suntrust-accordion-trigger").each(function() {
				var expand = $(this).attr("data-expand");
				if (expand == "true") {
					$(this).addClass("suntrust-open");
					$(this).next().show();
				}
			});
			/* Accordion Script for Page On Load functionality End */
			
			/*FAQ Component End*/
            $(target).find('div.sun-faqs-component-list').find('[data-sun-class="faqs-page-list-item-header"]').click(function(event) 
            {
                event.preventDefault();
                var $this = $(this), $parent = $this
                .closest('[data-sun-class="faqs-page-list-item"]'), $target = $parent
                .find('[data-sun-class="faqs-page-list-item-detail"]');
                
                $this.toggleClass('sun-active');
                $target.toggleClass('sun-active');
                $this.focus();
            });			
            /*FAQ Component End*/
			
			/* Dynamic List Load More Function Start */
            $(target).find(".component-dynamic-summarylist").each(function() {
                var size_li = $(this).find('li').length;
                var x = parseInt($(this).find('.load-more button').attr("data-visible-items-limit"));
                $(this).find('li:lt(' + x + ')').show();
                if (size_li <= x) {
                    $(this).find('.load-more').hide();
                }
            });

            $(target).find('.suntrust-load-more').click(function() {
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
			
			// Step components Script Start
			$(target).find('.suntrust-stepsContainer ul').each(function() {
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
			// Step components Script End
			
			/*--------------Table Comparision Component Script Start--------------*/
			// Table compare colspan set dynamically Start
			$('[type="checkbox"]').uniform(
			{
				checkboxClass : 'sun-checkbox-input-container',
				checkedClass : 'sun-checked'
			});
			
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
							if ($(this).parent().parent().prev().hasClass('table_compare_more')) {
								$(this).parent().parent().prev().show();
							}
							$('.table_compare_more .table_compare_left').addClass('disabled');
							$(this).removeClass('noViewMore');
							if ($(this).parent().hasClass('table_scroller')) {
								$(this).parent().addClass("table_row_height");
							}
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
				} 
				else {
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
								{
									staticTableCount++;
								}
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
							if ($(this).parent().parent().prev().hasClass('table_compare_more')) {
							  $(this).parent().parent().prev().show();
							}
							$('.table_compare_more .table_compare_left').addClass('disabled');
							$(this).find('td').css('width', '250px');
							if ($(this).parent().hasClass('table_scroller')) {
								$(this).parent().addClass("table_row_height");
							}
						} 
						else if ($(this).parent().parent().prev().hasClass('table_compare_more') && tc_count <= 3)
						{
							$(this).parent().parent().prev().hide();
						}
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
						if ($(this).find('input[name="compareTableProductCount"]').val() == 2) {
							$(this).find('.table_compare_mobile td').css('width', '50%');
						}
						if ($(this).find('input[name="compareTableProductCount"]').val() == 1) {
						  $(this).find('.table_compare_mobile td').addClass('text-center');
						}
					} else {
						$(this).find('.table_compare_mobile').hide();
					}
				  
					$(this).find('.table_compare_mobile').each(function() {
						staticTableCount = 0;
						$(this).find('tr.account_even_row').each(function() {
							if($(this).find('td:last-child').text()!="") {
								staticTableCount++;
							}
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


			var scrollLeftPrev = 0;
			$('.table_scroller').scroll(function() {
				var $elem = $('.table_scroller');
				var newScrollLeft = $elem.scrollLeft(),width = $elem.outerWidth(),scrollWidth = $elem.get(0).scrollWidth+1;
				if (scrollWidth - newScrollLeft == width) {
					$(this).parent().prev().find(".table_compare_right").addClass('disabled');
				}
				else {
					$(this).parent().prev().find(".table_compare_right").removeClass('disabled');
				}

				if (newScrollLeft === 0) {
					$(this).parent().prev().find(".table_compare_left").addClass('disabled');
				}
				else {
					$(this).parent().prev().find(".table_compare_left").removeClass('disabled');
				}
				scrollLeftPrev = newScrollLeft;
			});
			// Table View More Navigation Start

			// Checkbox checked count Start

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
					}
					else {
						$('.sun-checkbox-input-container input:checkbox:not(":checked")').parents('.sun-checkbox-input-container').removeClass('sun-checkbox-disabled');
						$('.sun-checkbox-input-container input:checkbox:not(":checked")').attr('disabled', false);
					}	  
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

			// Checkbox checked count End

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
								if(thPadHeight > tdMax) {
									$(this).css("height", thPadHeight+"px");
								}
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
								if(lastTrHeight==0) {
									lastTrHeight=20;
								}
								$(this).find("tr:last-child").css("height",(lastTrHeight+30)+"px");
								if(isIE) {
									$(this).find("tr:last-child th").css("height",(lastTrHeight+30)+"px");
								}
								else {
									$(this).find("tr:last-child th").css("height",(lastTrHeight+31)+"px");                                        
								}
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
					}
				});
			},600);

			/*--------------Table Comparision Component Script End--------------*/
		}
		
		// Generic function for AJAX functionality via XMLHttpRequest.

		//Generic error callback for AjaxSubmit
		function dfsajaxError() {
			var content = "An error occurred. Please try again.";

			if (typeof $.fancybox == 'function') {
				$.fancybox({ 'content': '<p style="margin:40px 50px;font-size:15px;">'+content+'</p>' });
			} else {
				//alert(content);
			}
		}

		//Fires a POST Ajax Call 
		// args:
		// {String} url: Content source URL
		// {object} target: dom element used as a container for the resulting data  
		// {function} successFunction:  success callback function, 
		//                   Note: data append is default behavior, do not add it to this function  
		// {function} completedFunction:  completed callback function
		// {function} errorFunction:  error callback function
		// {Boolean} avoidMask: Boolean. Determines if a mask must be shown whike loading

		function AjaxSubmit(url, target, direction, successFunction, completeFunction, errorFunction, avoidMask) 
		{			
			$.ajax({
				url: url,		
				cahe: false,
				beforeSend: function () {
					if (!avoidMask) {
					showLoadingMask();
					}
				},
				success: function (data) {
					setNavigationhistory(direction);
					if($('#isPageCarousel').val() == "true")
					{
						var dfshtml = $(data);
					}
					else
					{
						var dfshtml = $(data).find('#DecisionTreeSlide').html();
					}						
					target.empty().append(dfshtml);										
					dfsbgSet();
					speedBumpCheck(target);
					allowParameter(target);
					if($('#isPageCarousel').val() == "true"){
						rebindingEvents(target);										
					}
					if (successFunction) {
						successFunction();
					}
				},
				error: function (xhRequest, ErrorText, thrownError) {
					if (errorFunction) {
						errorFunction();
					}
					else {
						dfsajaxError();
					}
				},
				complete: function () {
					if (!avoidMask) {
						hideLoadingMask();
					}
					if (completeFunction) {
						completeFunction();
					}
				}
			});


		}

		// Show the loading mask
		//a white layer with trasparency to stand out a loading event
		//You can find the styling in Areas/ResourceCenter/Css/Site.css  #loadingMask{ }
		function showLoadingMask() {
			var maskHeight = $(document).height();
			var maskWidth = $(document).width();
			var mask = $('#loadingMask');
			if (mask.length == 0) {
				$('body').append('<div id="loadingMask"></div>');
				mask = $('#loadingMask');
			}
			//mask.css({ 'width': maskWidth, 'height': maskHeight });
			mask.fadeTo("fast", 0.5);
		}
		//Hide the loading mask
		function hideLoadingMask() {
			$('#loadingMask').fadeOut(500);
		}
	}
});