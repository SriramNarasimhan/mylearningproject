$(document).ready(function(e){
	
if($(".search_result_categories").length !=0 ){
function getParameterByName(name, url) {
	if (!url) url = window.location.href;
	name = name.replace(/[\[\]]/g, "\\$&");
	var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
		results = regex.exec(url);
	if (!results) return null;
	if (!results[2]) return '';
	return decodeURIComponent(results[2].replace(/\+/g, " "));
}

$(document).on('click','.get-direction-link-mobile',function(e){
	e.preventDefault();
	var getDirectionLink = $(this).attr("data-mobile-link");
	if(getDirectionLink != ""){
		if(/Android/i.test(navigator.userAgent)){
			window.open(getDirectionLink, '_system');
		}else{
			window.open(getDirectionLink);
		}
	}	
});
var locationsTabFirstTimeClickedFlag = true;
var peopleTabFirstTimeClickedFlag = true;
var faqsTabFirstTimeClickedFlag = true;
var productsTabFirstTimeClickedFlag = true;
var allTabFirstTimeClickedFlag = true;
var resourcesTabFirstTimeClickedFlag = true;
var searchButtonClickInLocationTab = false;
var searchButtonClickInPeopleTab = false;
var searchButtonClickInAllTab = false;

var valueFromServicesCheckBoxGlobal=[];
var valueFromSpecialityCheckBoxGlobal=[];
var LocationResultCount = 0;
var PeopleResultCount = 0;

var noResultTextForLocationFilter = $('.no-result-text-for-location-filter').val();
var noResultTextForPeopleFilter = $('.no-result-text-for-people-filter').val();
var noResultTextForResourcesFilter = $('.no-result-text-for-resources-filter').val();
var selectbox = $("select.search_select option:selected").val();
var selectboxLowerCase;
var selectboxSpace;
var locationSearchMobileRadiusChangedFlag = false;
var peopleSearchMobileRadiusChangedFlag = false;
	
var startLat=undefined;
var startLng=undefined
var userAddress;
var geoLocationErrorCode;
var geoLocationErrorMessage;
var loc_result_object;
var people_result_object;
var product_result_object;
var faq_result_object;
var all_result_object;
var resources_result_object;
var product_result_object
var mapQuestResultCount = 0; 
var searchString;
var refinedRadiusResult=[];
var searchTerm = getParameterByName("searchTerm");
	if(searchTerm != null && searchTerm != "null" && searchTerm != "" && searchTerm != undefined){
		searchTerm = encodeURIComponent(searchTerm);
		validateSearchTerm(searchTerm);
		$('.location-search .suntrust_search').val(decodeURIComponent(searchTerm.trim()));
		$('#loadingMask').show();
	}
	var locationRadius = getParameterByName("locationRadius");
	var locationServices = getParameterByName("locationServices");
	var peopleRadius = getParameterByName("peopleRadius");
	var peoplespeciality = getParameterByName("specialty");

	var searchIn = "";
	if(window.location.hash == "#all_results" || window.location.hash == "#locations" || window.location.hash == "#people" ||window.location.hash == "#faqs" || window.location.hash == "#products" || window.location.hash == "#resources" )
		searchIn = window.location.hash;
	else
		searchIn = "#all_results";
	while(searchIn != "" && searchIn.charAt(0) === '#'){
		searchIn = searchIn.substr(1);
	}
	searchIn = searchIn.trim();
var filteredLocationArray=[];
var filteredAllResultsArray=[];
var filteredResourcesArray=[];
var filteredPeopleArray=[];
var locationShowMoreCounter = 0;
var allCategoryShowMoreCounter = 0;
var peopleShowMoreCounter = 0;
var resourcesShowMoreCounter = 0;
	
var loc_result_object_full_response;
var people_result_object_full_response;
var MqAndAEMResults = [];
var MqOnlyResults=[];
var AemOnlyResults=[];
var MqAndAEMResultsPeople = [];
var MqOnlyResultsPeople=[];
var AemOnlyResultsPeople=[];
var numberOfResultToBeDisplayed = parseInt($('.location_result_show_more_results a').attr('data-search-visible-items-limit'));
var numberOfResultToBeDisplayedPeople = parseInt($('.people_result_show_more_results a').attr('data-search-visible-items-limit'));
var numberOfResultToBeDisplayedResources = parseInt($('.resources_result_show_more_results a').attr('data-search-visible-items-limit'));
var allResultsNumberOfResultToBeDisplayed = parseInt($('.all_result_show_more_results a').attr('data-search-visible-items-limit'));

var specialityListCount;
// Hide Tab navigation bar during page load
$('.search_result_categories').addClass("hide");
//hide filter location results in mobile
$('.search_result_categories .visible-xs .mobile-filter-button').addClass("hide");
//hide zero results during page load
$('.location-search .search_result_count').addClass("hide");
//hide show more buttons during page load
$('.location_result_show_more_results').addClass("hide");
$('.search_result_show_more_results').addClass("hide");
$('.faq_result_show_more_results').addClass("hide");
$('.all_result_show_more_results').addClass("hide");
$('.people_result_show_more_results').addClass("hide");
	
function locationSearchTrigger(){
		if(searchIn != ""){
			
			$("ul.search_select a").removeClass("active");
			$("ul.search_select a[index='"+searchIn+"']").addClass('active');
			//$("select.search_select option[value='"+searchIn.toLowerCase()+"']").attr("selected", true);
			var SelectedTab = $("select.search_select option[value='"+searchIn.toLowerCase()+"']").text();
			$("select.search_select option[value="+searchIn.toLowerCase()+"]").parent().prev().html(SelectedTab);
			if(searchIn == "locations"){
				//$("select.search_select option").removeAttr("selected");
				//$('.search_result_categories').removeClass("hide");
				searchTerm = searchTerm.trim();
				if(locationRadius!=null&&locationRadius!=undefined&&locationRadius!='')
				locationRadius = locationRadius.toString().trim();
				else
				locationRadius = 10;
				locationRadius = parseInt(locationRadius);
				
				var strRadiusIndex;
				if(locationRadius == 05 || locationRadius == 5) strRadiusIndex = 0;
				if(locationRadius == 10) strRadiusIndex = 1;
				if(locationRadius == 20) strRadiusIndex = 2;
				if(locationRadius == 50) strRadiusIndex = 3;
				$('.locations_filter_desktop .search_detail_radius_select select option:eq('+strRadiusIndex+')').attr("selected",true);
				$('.locations_filter_desktop .search_detail_radius_select span').text(locationRadius+" Miles");
				$('.locations_filter_mobile .mobile-radius-filter .search_detail_radius_select select option:eq('+strRadiusIndex+')').attr("selected",true);
				$('.locations_filter_mobile .mobile-radius-filter .search_detail_radius_select span').text(locationRadius+" Miles");
				
				if(locationServices!=null&&locationServices!=undefined&&locationServices!='')
				locationServices = locationServices.trim();
				searchString = searchTerm;
				
				$('.location-search .suntrust_search').val(decodeURIComponent(searchString));
				
				if(locationsTabFirstTimeClickedFlag){
					if(searchTerm != null && searchTerm != "null" && locationRadius != null && locationServices != null){
						locationSearchHandler(locationRadius,"newSearch",locationServices);				
					}else if(searchTerm != null && searchTerm != "null" && locationRadius == null && locationServices != null){
						locationSearchHandler(10,"newSearch",locationServices);				
					} else if(searchTerm != null && locationRadius != null && locationServices == null){
						locationSearchHandler(locationRadius,"newSearch");
					} else if(searchTerm != null && locationRadius == null && locationServices == null){
						locationSearchHandler(10,"newSearch");
					}
				}
				locationsTabFirstTimeClickedFlag = false;
			} 
		}
	}
	function peopleSearchTrigger(){
		if(searchIn != ""){
			
			$("ul.search_select a").removeClass("active");
			$("ul.search_select a[index='"+searchIn+"']").addClass('active');

			var SelectedTab = $("select.search_select option[value='"+searchIn.toLowerCase()+"']").text();
			$("select.search_select option[value="+searchIn.toLowerCase()+"]").parent().prev().html(SelectedTab);
			if(searchIn == "people"){
				//$("select.search_select option").removeAttr("selected");
				//$('.search_result_categories').removeClass("hide");
				if(searchTerm!=null&&searchTerm!=undefined&&searchTerm!='')
				searchTerm = searchTerm.trim();
				if(peopleRadius!=null&&peopleRadius!=undefined&&peopleRadius!='')
				peopleRadius = peopleRadius.toString();
				else
				peopleRadius = 10;
				peopleRadius = parseInt(peopleRadius);
				
				var aboutMe=[];
				var strRadiusIndex;
				if(peopleRadius == 05 || peopleRadius == 5) strRadiusIndex = 0;
				if(peopleRadius == 10) strRadiusIndex = 1;
				if(peopleRadius == 20) strRadiusIndex = 2;
				if(peopleRadius == 50) strRadiusIndex = 3;
				$('.people_filter_desktop .search_detail_radius_select select option:eq('+strRadiusIndex+')').attr("selected",true);
				$('.people_filter_desktop .search_detail_radius_select span').text(peopleRadius+" Miles");
				$('.people_filter_mobile .mobile-radius-filter .search_detail_radius_select select option:eq('+strRadiusIndex+')').attr("selected",true);
				$('.people_filter_mobile .mobile-radius-filter .search_detail_radius_select span').text(peopleRadius+" Miles");
				
				if(peoplespeciality!=null&&peoplespeciality!=undefined&&peoplespeciality!='') {
					peoplespeciality = peoplespeciality.trim();
					
					if(peoplespeciality.indexOf("+") > -1) {
						peoplespeciality = peoplespeciality.replace(/\+/g, ' ');
					}
					if(peoplespeciality.indexOf("%2B") > -1) {
						peoplespeciality = peoplespeciality.replace(/\%2B/g, ' ');
					}
					
					function searchStringInArray (str, strArray) {
					    for (var j=0; j<strArray.length; j++) {
					        if (strArray[j].match(str)) return j;
					    }
					    return -1;
					}
					
					var specialityListSearchForm;
					var specialityListHiddenField;
					var specialCnt = 0;
					var specialCntUniq = 0;
					
					$(".advisorConfigs input").each(function(){
						var $iptthis=$(this);
						if($iptthis.attr("class") == (peoplespeciality)) { // dropdown value from search form
							specialityListSearchForm = $(this).attr("class");
						} else if(peoplespeciality.indexOf(" ") > -1) { // hidden field value from search form
							
							peoplespeciality.split(" ").forEach(function(item) {
								if(item!=null&&item!=undefined&&item!=''&&item!='all'){
									if($iptthis.hasClass(item)) {
										aboutMe[specialCnt] = "advisor-specialty:"+item;
										++specialCnt;
									}
								}
							});
						} else {
							if(peoplespeciality!=null&&peoplespeciality!=undefined&&peoplespeciality!=''&&peoplespeciality!='all'){
								aboutMe[specialCntUniq] = "advisor-specialty:"+peoplespeciality;
								++specialCntUniq;
							}
							//specialityListHiddenField = peoplespeciality;
						}
					});
					
					var specialCnt = 0;
					if(specialityListSearchForm!=null&&specialityListSearchForm!=undefined&&specialityListSearchForm!=''){
						specialityListSearchForm.split(" ").forEach(function(item) {
							if(item!=null&&item!=undefined&&item!=''&&item!='all'){
								aboutMe[specialCnt] = "advisor-specialty:"+item;
								++specialCnt;
							}
						});
					}
					
					$.unique(aboutMe);
					
					/*if(specialityListHiddenField!=null&&specialityListHiddenField!=undefined&&specialityListHiddenField!=''){
						specialityListHiddenField.split(" ").forEach(function(item) {
							if(item!=null&&item!=undefined&&item!=''){
								aboutMe[specialCnt] = "advisor-specialty:"+item;
								++specialCnt;
							}
						});
					}*/
				}
				
				searchString = searchTerm;
				if(searchString!=null&&searchString!=undefined&&searchString!=''&&searchString!="null")
				$('.location-search .suntrust_search').val(decodeURIComponent(searchString));
				
				if(peopleTabFirstTimeClickedFlag){
					if(searchTerm != null && searchTerm != "null" && peopleRadius != null && aboutMe != null){
						peopleSearchHandler(peopleRadius,"newSearch",aboutMe);				
					}else if(searchTerm != null && searchTerm != "null" && peopleRadius == null && aboutMe != null){
						peopleSearchHandler(10,"newSearch",aboutMe);				
					} else if(searchTerm != null && peopleRadius != null && aboutMe == null){
						peopleSearchHandler(peopleRadius,"newSearch");
					} else if(searchTerm != null && peopleRadius == null && aboutMe == null){
						peopleSearchHandler(10,"newSearch");
					}
					peopleTabFirstTimeClickedFlag = false;
				}
			}
		}
	}
	function productSearchTrigger(){
		if(searchIn != ""){
			if(searchTerm != null && searchTerm != "null"){
				//$('.search_result_categories').removeClass("hide");
				
				$("ul.search_select a").removeClass("active");
				$("ul.search_select a[index='"+searchIn+"']").addClass('active');
				//$("select.search_select option[value='"+searchIn.toLowerCase()+"']").attr("selected", true);
				var SelectedTab = $("select.search_select option[value='"+searchIn.toLowerCase()+"']").text();
				$("select.search_select option[value="+searchIn.toLowerCase()+"]").parent().prev().html(SelectedTab);
				if(searchIn == "products"){
					//$("select.search_select option").removeAttr("selected");
					searchString = searchTerm;
					$('.location-search .suntrust_search').val(decodeURIComponent(searchString));
					productsTabFirstTimeClickedFlag = true;
					if(productsTabFirstTimeClickedFlag){
						productSearchHandler();				
						productsTabFirstTimeClickedFlag = false;
					}
				}
			}
		}
	}
	function resourcesSearchTrigger(){
		if(searchIn != ""){
			if(searchTerm != null && searchTerm != "null"){
				//$('.search_result_categories').removeClass("hide");
				
				$("ul.search_select a").removeClass("active");
				$("ul.search_select a[index='"+searchIn+"']").addClass('active');
				//$("select.search_select option[value='"+searchIn.toLowerCase()+"']").attr("selected", true);
				var SelectedTab = $("select.search_select option[value='"+searchIn.toLowerCase()+"']").text();
				$("select.search_select option[value="+searchIn.toLowerCase()+"]").parent().prev().html(SelectedTab);
				if(searchIn == "resources"){
					//$("select.search_select option").removeAttr("selected");
					searchString = searchTerm;
					$('.location-search .suntrust_search').val(decodeURIComponent(searchString));
					resourcesTabFirstTimeClickedFlag = true;
					if(resourcesTabFirstTimeClickedFlag){
						resourcesSearchHandler();				
						resourcesTabFirstTimeClickedFlag = false;
					}
				}
			}
		}
	}
	function faqSearchTrigger(){
		if(searchIn != ""){
			if(searchTerm != null && searchTerm != "null"){
				//$('.search_result_categories').removeClass("hide");
				
				$("ul.search_select a").removeClass("active");
				$("ul.search_select a[index='"+searchIn+"']").addClass('active');
				//$("select.search_select option[value='"+searchIn.toLowerCase()+"']").attr("selected", true);
				var SelectedTab = $("select.search_select option[value='"+searchIn.toLowerCase()+"']").text();
				$("select.search_select option[value="+searchIn.toLowerCase()+"]").parent().prev().html(SelectedTab);
				if(searchIn == "faqs"){
					//$("select.search_select option").removeAttr("selected");
					searchString = searchTerm;
					$('.location-search .suntrust_search').val(decodeURIComponent(searchString));
					faqsTabFirstTimeClickedFlag = true;
					if(faqsTabFirstTimeClickedFlag){
						faqSearchHandler();				
						faqsTabFirstTimeClickedFlag = false;
					}
				}
			}
		}
	}
	function allCategorySearchTrigger(){
		if(searchIn != ""){
			if(searchTerm != null && searchTerm != "null"){
				//$('.search_result_categories').removeClass("hide");
				
				$("ul.search_select a").removeClass("active");
				$("ul.search_select a[index='"+searchIn+"']").addClass('active');
				//$("select.search_select option[value='"+searchIn.toLowerCase()+"']").attr("selected", true);
				var SelectedTab = $("select.search_select option[value='"+searchIn.toLowerCase()+"']").text();
				$("select.search_select option[value="+searchIn.toLowerCase()+"]").parent().prev().html(SelectedTab);
				if(searchIn == "all_results"){
					//$("select.search_select option").removeAttr("selected");
					searchString = searchTerm;
					$('.location-search .suntrust_search').val(decodeURIComponent(searchString));
					allTabFirstTimeClickedFlag = true;
					if(allTabFirstTimeClickedFlag){
						allCategorySearchHandler();				
						allTabFirstTimeClickedFlag = false;
					}
				}
			}
		}
	}
	Array.prototype.min = function() {
	  return Math.min.apply(null, this);
	};
	function validateSearchTerm(searchString){
		var searchedTerm = searchString;
		$.ajax({
			  type: "POST",
			  url: 'https://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850&outFormat=json&maxResults=1&location='+searchedTerm,
			  dataType: "json",
			  async: false,
			  success: function (data) {
				if(data != undefined && data.results != undefined && data.results[0] != undefined && data.results[0].locations != undefined){				
					for(var i = 0; i < data.results[0].locations.length; i++){
						if(data.results[0].locations[i].adminArea1 != undefined && data.results[0].locations[i].adminArea1 == "US" && data.results[0].locations[i].latLng != undefined && data.results[0].locations[i].latLng.lat != undefined && data.results[0].locations[i].latLng.lng != undefined){
							startLat = data.results[0].locations[i].latLng.lat;
							startLng = data.results[0].locations[i].latLng.lng;
							break;
						}
					}
				}
			  },
			  error: function (xhRequest, ErrorText, thrownError) {
					 console.log("Error during getCurrentPosition");
			  }
	   });
	}
	function triggerAllSearch(){
		locationSearchTrigger();
		peopleSearchTrigger();
		productSearchTrigger();
		resourcesSearchTrigger();
		faqSearchTrigger();
		allCategorySearchTrigger();
	}
	if($('.location-search').length != 0){
		triggerAllSearch();
	}
	function showLocation(button, position) {
		startLat = position.coords.latitude;
		startLng = position.coords.longitude;
		   var $input = button.siblings('span').find('input:nth-child(2)');
		   var address, street, city, state, zip;
		   $.ajax({
				  type: "POST",
				  url: 'https://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850&outFormat=json&location='+startLat+','+startLng,
				  dataType: "json",
				  async: false,
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
							   zip: zip,
							   city: city,
							   state: state,
						 };
						data = JSON.parse(JSON.stringify(findme));
						userAddress = findme['address'];
						updateLocationFields(data, $input);	
						 if($('.suntrust-input-location-button-map').length > 0) {
							updateLocationFields(data, $('.suntrust-input-location-button-map').siblings('input:first'));
						}
				  },
				  error: function (xhRequest, ErrorText, thrownError) {
						console.log("Error during showLocation");
				  }
		   });
	}
	function updateLocationFields(data, $input) {
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
		if (data.zip != '' || data.city != ''|| data.state != '') {
			// ZIP
			var $zipField = $input.siblings('input.suntrust-input-location-zip');
			if (data.zip != '' && $zipField.length > 0) {
				$zipField.val(data.zip);
			}
			// City
			var $cityField = $input.siblings('input.suntrust-input-location-city');
			if (data.city != '' && $cityField.length > 0) {
				$cityField.val(data.city);
			}
			// City
			var $stateField = $input.siblings('input.suntrust-input-location-state');
			if (data.state != '' && $stateField.length > 0) {
				$stateField.val(data.state);
			}
			// Clear location fields on text change
			var $locationFields = $input.siblings('input.suntrust-input-location-field');
			if ($locationFields.length > 0) {
				$input.keydown(function(event) {
					if (event.keyCode == 9|| event.keyCode == 27|| event.keyCode == 13||(event.keyCode == 65 && event.ctrlKey === true)||(event.keyCode >= 35 && event.keyCode <= 39)) {
						return;
					} else {
						$locationFields.val('');
					}
				});
			}
		}
	}

/*Get user current location - ends*/
/*component parsys validation - starts*/
		if($('body .search_result_filters.result_filters_other').length !=0){
			$('.search_result_filters_products .parsys_validation_error_products').remove();
			$('.search_result_filters_all_results .parsys_validation_error_all_results').remove();
			$('.search_result_filters_faqs .parsys_validation_error_faqs').remove();
			var numberOfPromosAuthoredInProductsTab = $('.search_result_filters_products').children("div").length;
            var numberOfPromosAuthoredInFAQsTab = $('.search_result_filters_faqs').children("div").length;
            var numberOfPromosAuthoredInAllResultsTab = $('.search_result_filters_all_results').children("div").length;
			var numberOfPromosAllowedInProductsTab = parseInt($('.number-of-promo-components-in-products').val());
            var numberOfPromosAllowedInFAQsTab = parseInt($('.number-of-promo-components-in-faqs').val());
            var numberOfPromosAllowedInAllResultsTab = parseInt($('.number-of-promo-components-in-all-results').val()); 
            if(numberOfPromosAuthoredInProductsTab >=(numberOfPromosAllowedInProductsTab+1) ){
				$('.search_result_filters_products .newpar').hide();
                $('.search_result_filters_products').append("<p class='parsys_validation_error_products'>Reached maximum number of components for products tab.</p>")
            }else {
				$('.search_result_filters_products .newpar').show();
                $('.search_result_filters_products .parsys_validation_error_products').remove();
            }
            if(numberOfPromosAuthoredInFAQsTab >=(numberOfPromosAllowedInFAQsTab+1) ){
				$('.search_result_filters_faqs .newpar').hide();
                $('.search_result_filters_faqs').append("<p class='parsys_validation_error_faqs'>Reached maximum number of components for faqs tab.</p>")
            }else {
				$('.search_result_filters_faqs .newpar').show();
                $('.search_result_filters_faqs .parsys_validation_error_faqs').remove();
            }
            if(numberOfPromosAuthoredInAllResultsTab >=(numberOfPromosAllowedInAllResultsTab+1) ){
				$('.search_result_filters_all_results .newpar').hide();
                $('.search_result_filters_all_results').append("<p class='parsys_validation_error_all_results'>Reached maximum number of components for all results tab.</p>")
            }else {
				$('.search_result_filters_all_results .newpar').show();
                $('.search_result_filters_all_results .parsys_validation_error_all_results').remove();
            }
        }
/*component parsys validation - ends*/
	/* All Results Tab FAQ show more/less Start */
    $(document).on('click','.show_more',function(){
		if($(this).text()=="Show More")
		{
			$(this).text("View Less");
			$(this).prev('.faq_result_data').find('.faq_shoeMoreLess_Content').removeClass('faq_showMore');
		}
		else
		{
			$(this).text("Show More");
			$(this).prev('.faq_result_data').find('.faq_shoeMoreLess_Content').addClass('faq_showMore');
		}
	});
    /* All Results Tab FAQ show more/less End */
	function lazyLoadSearch() {
	    var section3A = $('.locations_filter_container').height();
	    var off_section3A = $('.locations_filter_container').offset().top;
	    var lazyheight = off_section3A + $(window).scrollTop();
		var visibleMqResultsCount = $('.location_mq_results').children().length;
		var visibleAemResultsCount = $('.location_aem_results').children().length;
		var visibleResultsCount = parseInt(visibleMqResultsCount) + parseInt(visibleAemResultsCount);
		var actualResultsCount = filteredLocationArray.length;
	    if( (lazyheight >= (section3A-200)) && (visibleResultsCount < actualResultsCount)){
	    	$("div.search_result_description_details:visible:last").css("border-bottom","1px solid #d5d5d5");		
			numberOfResultToBeDisplayed = numberOfResultToBeDisplayed+parseInt($('.location_result_show_more_results a').attr('data-search-visible-items-limit'));
			locationShowMoreCounter = locationShowMoreCounter + parseInt($('.location_result_show_more_results a').attr('data-search-visible-items-limit'));
			resultCreator(filteredLocationArray,"singleDimension","refineResult",numberOfResultToBeDisplayed,locationShowMoreCounter);			
			
			if(numberOfResultToBeDisplayed < actualResultsCount){
				resultCreator(filteredLocationArray,"singleDimension","refineResult",numberOfResultToBeDisplayed,locationShowMoreCounter);
			}
			else{
				resultCreator(filteredLocationArray,"singleDimension","refineResult",actualResultsCount,locationShowMoreCounter);
			}
			var showMoreItemCount = parseInt($('.location_result_show_more_results a').attr("data-search-visible-items-limit"));
			$("div.search_result_description_details:visible:last").css("border-bottom","0px");
	    }
	}
	function lazyLoadSearchResources() {
		var section3A = $('.resources_filter_container').height();
		var off_section3A = $('.resources_filter_container').offset().top;
		var lazyheight = off_section3A + $(window).scrollTop();
		var visibleResultsCount = parseInt($('.resources_filter_container .search_result_content').children().length);
		var actualResultsCount = filteredResourcesArray.length;
		if( (lazyheight >= (section3A-200)) && (visibleResultsCount < actualResultsCount)){
			$("div.search_result_content_data:visible:last").css("border-bottom","1px solid #d5d5d5");		
			numberOfResultToBeDisplayedResources = numberOfResultToBeDisplayedResources+parseInt($('.resources_result_show_more_results a').attr('data-search-visible-items-limit'));
			resourcesShowMoreCounter = resourcesShowMoreCounter + parseInt($('.resources_result_show_more_results a').attr('data-search-visible-items-limit'));	
			
			if(numberOfResultToBeDisplayedResources < actualResultsCount){
				resourcesResultCreator(filteredResourcesArray,"refineResult",numberOfResultToBeDisplayedResources,resourcesShowMoreCounter);
			}
			else{
				resourcesResultCreator(filteredResourcesArray,"refineResult",actualResultsCount,resourcesShowMoreCounter);
			}
			$("div.search_result_content_data:visible:last").css("border-bottom","0px");
		}
	}
	function lazyLoadAllCategorySearch() {
	    var section3A = $('.all_results_filter_container').height();
	    var off_section3A = $('.all_results_filter_container').offset().top;
	    var lazyheight = off_section3A + $(window).scrollTop();
		var visibleResultsCount = $('.all_results_filter_container').children().length;
		var actualResultsCount = filteredAllResultsArray.length;
	    if( (lazyheight >= (section3A-300)) && (visibleResultsCount < actualResultsCount)){
	    	$("div.search_result_content:last").css("border-bottom","1px solid #d5d5d5");		
			allResultsNumberOfResultToBeDisplayed = allResultsNumberOfResultToBeDisplayed+parseInt($('.all_result_show_more_results a').attr('data-search-visible-items-limit'));
			allCategoryShowMoreCounter = allCategoryShowMoreCounter + parseInt($('.all_result_show_more_results a').attr('data-search-visible-items-limit'));
			if(allResultsNumberOfResultToBeDisplayed < actualResultsCount){
				allCategoryResultCreator(filteredAllResultsArray,allResultsNumberOfResultToBeDisplayed,allCategoryShowMoreCounter);
				irrelaventDivsRemover();
				closedHoursCreator();
			}
			else{
				allCategoryResultCreator(filteredAllResultsArray,actualResultsCount,allCategoryShowMoreCounter);	
				irrelaventDivsRemover();
				closedHoursCreator();
			}
			//$("div.search_result_content:visible:last").css("border-bottom","0px");
	    }
	}

	function lazyLoadPeopleSearch() {
	    var section3A = $('.people_filter_container').height();
	    var off_section3A = $('.people_filter_container').offset().top;
	    var lazyheight = off_section3A + $(window).scrollTop();
		var visibleMqResultsCount = $('.people_mq_results').children().length;
		var visibleAemResultsCount = $('.people_aem_results').children().length;
		var visibleResultsCount = parseInt(visibleMqResultsCount) + parseInt(visibleAemResultsCount);
		var actualResultsCount = filteredPeopleArray.length;
	    if( (lazyheight >= (section3A-200)) && (visibleResultsCount < actualResultsCount)){
	    	$("div.search_result_description_details:visible:last").css("border-bottom","1px solid #d5d5d5");
			peopleShowMoreCounter = peopleShowMoreCounter + parseInt($('.people_result_show_more_results a').attr('data-search-visible-items-limit'));
			numberOfResultToBeDisplayedPeople = numberOfResultToBeDisplayedPeople+parseInt($('.people_result_show_more_results a').attr('data-search-visible-items-limit'));
			if(numberOfResultToBeDisplayedPeople < actualResultsCount){
				resultCreatorPeople(filteredPeopleArray,"singleDimension","refineResult",numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
			}
			else{
				resultCreatorPeople(filteredPeopleArray,"singleDimension","refineResult",actualResultsCount,peopleShowMoreCounter);
			}
			var showMoreItemCount = parseInt($('.people_result_show_more_results a').attr("data-search-visible-items-limit"));
			$("div.search_result_description_details:visible:last").css("border-bottom","0px");
	    }
	}

	$(window).scroll(function () {
		var tabSelectedMobile = $("select.search_select").parent().find("span").text();
		$("select.search_select option").each(function(e){
			if($(this).text() == tabSelectedMobile)
				selectboxLowerCase = $(this).val();
		});
		if(window.innerWidth<768) {
			if(selectboxLowerCase != undefined || selectboxLowerCase != null || selectboxLowerCase != ""){
				if(selectboxLowerCase == "locations")
					lazyLoadSearch();
				else if(selectboxLowerCase == "all_results")
					lazyLoadAllCategorySearch();
				else if(selectboxLowerCase == "people")
					lazyLoadPeopleSearch();
				else if(selectboxLowerCase == "resources")	
					lazyLoadSearchResources();
			}
		}
	});


	/*Location Search Starts*/

	//searchResultCat();
	

	function searchResultCat() {
		if(window.innerWidth<768) {
			selectboxSpace = $("select.search_select option:selected").val();
			selectbox = $("select.search_select option:selected").val();
			if(selectbox != undefined)
				selectboxLowerCase = selectbox.toLowerCase();
		}
		else {
			selectboxSpace = $("ul.search_select a.active").attr("index");
			selectbox = $("ul.search_select a.active").attr("index");
			if(selectbox != undefined)
				selectboxLowerCase = selectbox.toLowerCase();
		}
		if(selectboxLowerCase!="products" && selectboxLowerCase!="all_results" && selectboxLowerCase!="faqs") {
			if($('.search_result_filters_nonProducts').hasClass('hide'))
				$('.search_result_filters_nonProducts').removeClass('hide');
			if(!$('.search_result_filters_products').parent().hasClass('author_hide')) {
				$('.search_result_filters_products').parent().addClass('author_hide');
				$('.search_result_filters_products').addClass('hide');
				$('.search_result_filters_products').next().addClass('hide');
			}
			$("."+selectboxLowerCase+"_result_count").removeClass('hide');
			$("a[index='"+selectboxLowerCase+"_filter']").removeClass('hide');
			$("."+selectboxLowerCase+"_filter_mobile").removeClass('hide');
			$("."+selectboxLowerCase+"_filter_desktop").removeClass('hide');
			$("."+selectboxLowerCase+"_filter_container").removeClass('hide');
			$('.search_result_filters').removeClass('hide');
		}
		else if(selectboxLowerCase=="products" || selectboxLowerCase=="all_results" || selectboxLowerCase=="faqs")	{
			$('.search_result_filters_nonProducts').addClass('hide');
			$("."+selectboxLowerCase+"_result_count").removeClass('author_hide');
			$('.search_result_filters_products').parent().removeClass('author_hide');
			$('.search_result_filters_'+selectboxLowerCase+'').removeClass('hide');
			if(selectboxLowerCase!="faqs")
				$('.search_result_filters_products').find('.search_result_filters_'+selectboxLowerCase+'').addClass('hide');
			$("."+selectboxLowerCase+"_filter_container").removeClass('hide');
			$('.search_result_filters_nonProducts').addClass('hide');
		}
	}

	function searchResultCatHide() {
		$("."+selectboxLowerCase+"_result_count").addClass('hide');
		//$("select.search_select option[value='"+selectboxSpace+"']").attr("selected", false);
		if(selectboxLowerCase!='all_results' && selectboxLowerCase!='products' && selectboxLowerCase!="faqs")
			$("a[index='"+selectboxLowerCase+"_filter']").addClass('hide');
		$("."+selectboxLowerCase+"_filter_mobile").addClass('hide');
		$("."+selectboxLowerCase+"_filter_desktop").addClass('hide');
		$("."+selectboxLowerCase+"_filter_container").addClass('hide');
		$(".search_result_filters_"+selectboxLowerCase+"").addClass('hide');
	}

	function searchResultCatHideResize() {
		$("."+selectboxLowerCase+"_result_count").addClass('hide');
		//$("select.search_select option[value='"+selectboxSpace+"']").attr("selected", false);
		if(selectboxLowerCase!='all_results' && selectboxLowerCase!='products' && selectboxLowerCase!="faqs")
			$("a[index='"+selectboxLowerCase+"_filter']").addClass('hide');
		$("."+selectboxLowerCase+"_filter_mobile").addClass('hide');
		$("."+selectboxLowerCase+"_filter_desktop").addClass('hide');
		$("."+selectboxLowerCase+"_filter_container").addClass('hide');
		$(".search_result_filters_"+selectboxLowerCase+"").addClass('hide');
	}


	function allCategorySearchHelper(){
		$('.search_result_count').addClass("hide");
		var all_result_count = filteredAllResultsArray.length;
		if(all_result_count == 0){
			$('.all_results_no_result.zero_result_found').removeClass("hide");
			$('.search_result_filters_all_results').addClass("hide");
		}else if(all_result_count > 0){
			$('.all_results_filter_container').removeClass("hide");
			$('.result_filters_other').removeClass("author_hide");
			$('.search_result_filters_all_results').removeClass("author_hide");
			$('.search_result_filters_all_results').removeClass("hide");

		}
		var visibleLi = $('.all_results_filter_container div.search_result_content:visible').length;
		var totalLi = filteredAllResultsArray.length;
		if (visibleLi < totalLi) {
			$('.all_result_show_more_results').removeClass("hide");
		}else{
			$('.all_result_show_more_results').addClass("hide");
		}
		$('#loadingMask').fadeOut(100);
	}

	function faqSearchHelper(){
		$('.faqs_result_count').removeClass("hide");
		var faq_result_count = parseInt($('.faqs_result_count').text());
		if(faq_result_count == 0){
			$('.faqs_no_result.zero_result_found').removeClass("hide");
			$('.search_result_filters_faqs').addClass("hide");
		}else if(faq_result_count > 0){
			$('.faqs_filter_container').removeClass("hide");
			$('.result_filters_other').removeClass("author_hide");
			$('.search_result_filters_faqs').removeClass("author_hide");
			$('.search_result_filters_faqs').removeClass("hide");

		}
		var visibleLi = $('.faqs_filter_container div.sun-faqs-page-list-item:visible').length;
		var totalLi = $('.faqs_filter_container div.sun-faqs-page-list-item').length;
		if (visibleLi < totalLi) {
			$('.faq_result_show_more_results').removeClass("hide");
		}else{
			$('.faq_result_show_more_results').addClass("hide");
		}
		$('#loadingMask').fadeOut(100);
	}
	function productSearchHelper(){
		$('.products_result_count').removeClass("hide");
		var products_result_count = parseInt($('.products_result_count').text());
		if(products_result_count == 0){
			$('.products_no_result.zero_result_found').removeClass("hide");
			$('.search_result_filters_products').addClass("hide");
		}else if(products_result_count > 0){
			$('.products_filter_container').removeClass("hide");
			$('.result_filters_other').removeClass("author_hide");
			$('.search_result_filters_products').removeClass("author_hide");
			$('.search_result_filters_products').removeClass("hide");

		}
		var visibleLi = $('.products_filter_container div.search_result_content_data:visible').length;
		var totalLi = $('.products_filter_container div.search_result_content_data').length;
		if (visibleLi < totalLi) {
			$('.search_result_show_more_results').removeClass("hide");
		}else{
			$('.search_result_show_more_results').addClass("hide");
		}
		$('#loadingMask').fadeOut(100);
	}
	function locationSearchHelper(){
		$('.locations_result_count').removeClass("hide");
		var locations_result_count = parseInt($('.locations_result_count').text());
		var filterNoResultVisibleFlag = $('.locations_filter_container .filter-no-result.zero_result_found').is(":visible");
		if(!filterNoResultVisibleFlag){
			if(locations_result_count == 0){
				$('.locations_no_result.zero_result_found').removeClass("hide");
				$('.location.mobile-filter-button').addClass("hide");
				$('.search_result_filters_nonProducts').addClass('hide');
				$('.result_filters_other').addClass('author_hide');
			}else if(locations_result_count > 0){
				$('.location.mobile-filter-button').removeClass("hide");
				$('.locations_filter_container').removeClass("hide");
				$('.search_result_filters_nonProducts').removeClass('hide');
				$('.result_filters_other').removeClass('author_hide');
			}
			var visibleMqResultsCount = $('.location_mq_results').children().length;
			var visibleAemResultsCount = $('.location_aem_results').children().length;
			var visibleResultsCount = parseInt(visibleMqResultsCount) + parseInt(visibleAemResultsCount);
			var actualResultsCount = filteredLocationArray.length;
			var showMoreItemCount = parseInt($('.location_result_show_more_results a').attr("data-search-visible-items-limit"));
			if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
				$('.location_result_show_more_results').addClass("hide");
			}else{
				$('.location_result_show_more_results').removeClass("hide");
			}
		}
		$('#loadingMask').fadeOut(100);
	}
	
	function resourcesSearchHelper(){
		$('.resources_result_count').removeClass("hide");
		var resources_result_count = parseInt($('.resources_result_count').text());
		var filterNoResultVisibleFlag = $('.resources_filter_container .filter-no-result.zero_result_found').is(":visible");
		if(!filterNoResultVisibleFlag){
			if(resources_result_count == 0){
				$('.resources_no_result.zero_result_found').removeClass("hide");
				$('.resources.mobile-filter-button').addClass("hide");
				$('.search_result_filters_nonProducts').addClass('hide');
				$('.result_filters_other').addClass('author_hide');
			}else if(resources_result_count > 0){
				$('.resources.mobile-filter-button').removeClass("hide");
				$('.resources_filter_container').removeClass("hide");
				$('.search_result_filters_nonProducts').removeClass('hide');
				$('.result_filters_other').removeClass('author_hide');
			}
			var visibleResultsCount = parseInt($('.resources_filter_container .search_result_content').children().length);
			var actualResultsCount = filteredResourcesArray.length;
			var showMoreItemCount = parseInt($('.resources_result_show_more_results a').attr("data-search-visible-items-limit"));
			if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
				$('.resources_result_show_more_results').addClass("hide");
			}else{
				$('.resources_result_show_more_results').removeClass("hide");
			}
		}
		$('#loadingMask').fadeOut(100);
	}
	function peopleSearchHelper(){
		$('.people_result_count').removeClass("hide");
		var people_result_count = parseInt($('.people_result_count').text());
		var filterNoResultVisibleFlag = $('.people_filter_container .filter-no-result.zero_result_found').is(":visible");
		if(!filterNoResultVisibleFlag){
			if(people_result_count == 0){
				$('.people_no_result.zero_result_found').removeClass("hide");
				$('.people.mobile-filter-button').addClass("hide");
				$('.search_result_filters_nonProducts').addClass('hide');
				$('.result_filters_other').addClass('author_hide');
			}else if(people_result_count > 0){
				$('.people.mobile-filter-button').removeClass("hide");
				$('.people_filter_container').removeClass("hide");
				$('.search_result_filters_nonProducts').removeClass('hide');
				$('.result_filters_other').removeClass('author_hide');
			}
			var visibleMqResultsCount = $('.people_mq_results').children().length;
			var visibleAemResultsCount = $('.people_aem_results').children().length;
			var visibleResultsCount = parseInt(visibleMqResultsCount) + parseInt(visibleAemResultsCount);
			var actualResultsCount = filteredPeopleArray.length;
			var showMoreItemCount = parseInt($('.people_result_show_more_results a').attr("data-search-visible-items-limit"));
			if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
				$('.people_result_show_more_results').addClass("hide");
			}else{
				$('.people_result_show_more_results').removeClass("hide");
			}
		}
		$('#loadingMask').fadeOut(100);
	}
	/* Search menu active class add Start */
	$(document).on("click",".search_result_categories ul.text-center a",function(){
		$('#loadingMask').show();
		$("ul.search_select a").removeClass("active");
		$(this).addClass("active");
		searchResultCatHide();
		searchResultCat();
		$('.people_filter_mobile').addClass("hide");
		$('.resources_filter_mobile').addClass("hide");
		$('.locations_filter_mobile').addClass("hide");
		var validationPassedFlag = $('.search_text_error_message').is(":visible");
		$("select.search_select option[value="+selectbox+"]").attr("selected", true);
		$("select.search_select option[value="+selectbox+"]").parent().prev().html(selectbox);
		/*integration changes - start*/
		if(selectboxLowerCase!="products" && selectboxLowerCase!="all_results" && selectboxLowerCase!="faqs") {
			if($('.search_result_filters_nonProducts').hasClass('hide'))
				$('.search_result_filters_nonProducts').removeClass('hide');
			if(!$('.search_result_filters_products').parent().hasClass('author_hide')) {
				$('.search_result_filters_products').parent().addClass('author_hide');
				$('.search_result_filters_products').addClass('hide');
				$('.search_result_filters_products').next().addClass('hide');
			}
			$("."+selectboxLowerCase+"_result_count").removeClass('hide');
			if(selectboxLowerCase!='all_results' && selectboxLowerCase!='products' && selectboxLowerCase!="faqs")
				$("a[index='"+selectboxLowerCase+"_filter']").removeClass('hide');
			$("."+selectboxLowerCase+"_filter_mobile").removeClass('hide');
			$("."+selectboxLowerCase+"_filter_desktop").removeClass('hide');
			$("."+selectboxLowerCase+"_filter_container").removeClass('hide');
			$('.search_result_filters').removeClass('hide');
		}
		else if(selectboxLowerCase=="products" || selectboxLowerCase=="all_results" || selectboxLowerCase=="faqs")	{
			$('.search_result_filters_nonProducts').addClass('hide');
			$("."+selectboxLowerCase+"_result_count").removeClass('hide');
			$('.search_result_filters_products').parent().removeClass('author_hide');
			$('.search_result_filters_'+selectboxLowerCase+'').removeClass('hide');
			$('.search_result_filters_'+selectboxLowerCase+'').removeClass('author_hide');
			if(selectboxLowerCase!="faqs")
				$('.search_result_filters_products').find('.search_result_filters_'+selectboxLowerCase+'').addClass('hide');
			$("."+selectboxLowerCase+"_filter_container").removeClass('hide');
			$('.search_result_filters_nonProducts').addClass('hide');
		}

		$('.location_result_show_more_results').addClass("hide");
		$('.people_result_show_more_results').addClass("hide");
		$('.faq_result_show_more_results').addClass("hide");
		$('.resources_result_show_more_results').addClass("hide");
		$('.all_result_show_more_results').addClass("hide");
		$('.search_result_show_more_results').addClass("hide");

		$('.all_results_filter_container').addClass("hide");
		$('.all_results_no_result.zero_result_found').addClass("hide");

		$('.faqs_filter_container').addClass("hide");
		$('.faqs_result_count').addClass("hide");
		$('.faqs_no_result.zero_result_found').addClass("hide");

		$('.products_no_result.zero_result_found').addClass("hide");
		$('.products_filter_container').addClass("hide");
		$('.products_result_count').addClass("hide");

		$('.locations_no_result.zero_result_found').addClass("hide");
		$('.locations_filter_container').addClass("hide");
		$('.locations_result_count').addClass("hide");


		$('.resources_no_result.zero_result_found').addClass("hide");
		$('.resources_filter_container').addClass("hide");
		$('.resources_result_count').addClass("hide");

		$('.people_no_result.zero_result_found').addClass("hide");
		$('.people_filter_container').addClass("hide");
		$('.people_result_count').addClass("hide");



		$('.search_result_count').removeClass("hide");



		if(selectboxLowerCase == "faqs"){
			window.location.hash = selectboxLowerCase;
			if(faqsTabFirstTimeClickedFlag && !validationPassedFlag){
				faqsTabFirstTimeClickedFlag = false;
				faqSearchHandler();
			}
			else{
				faqSearchHelper();
			}
		}
		if(selectboxLowerCase == "products"){
			window.location.hash = selectboxLowerCase;
			if(productsTabFirstTimeClickedFlag && !validationPassedFlag){
				productsTabFirstTimeClickedFlag = false;
				productSearchHandler();
			}
			else{
				productSearchHelper();
			}
		}
		if(selectboxLowerCase == "all_results"){
			$('.search_result_count').addClass("hide");
			window.location.hash = selectboxLowerCase;
			if(allTabFirstTimeClickedFlag && !validationPassedFlag){
				allTabFirstTimeClickedFlag = false;
				allCategorySearchHandler();
			}
			else{
				allCategorySearchHelper();
			}
		}
		if(selectboxLowerCase == "locations"){
			window.location.hash = selectboxLowerCase;
			if(locationsTabFirstTimeClickedFlag && !validationPassedFlag){
				locationsTabFirstTimeClickedFlag = false;
				locationSearchHandler("10","newSearch");
			}else{
				var filterNoResultIdentifier = $('.location-filter-no-result-identifier').val();
				if(filterNoResultIdentifier == "NoResultsDuringFilter"){
					$('.locations_filter_container').removeClass("hide");
					$('.locations_result_count').removeClass("hide");
				}
				var filterNoResultVisibleFlag = $('.locations_filter_container .filter-no-result.zero_result_found').is(":visible");
				if(!filterNoResultVisibleFlag)
					locationSearchHelper();
			}
		}
		if(selectboxLowerCase == "resources"){
			window.location.hash = selectboxLowerCase;
			if(resourcesTabFirstTimeClickedFlag && !validationPassedFlag){
				resourcesTabFirstTimeClickedFlag = false;
				resourcesSearchHandler();
			}else{
				var filterNoResultIdentifier = $('.resources-filter-no-result-identifier').val();
				if(filterNoResultIdentifier == "NoResultsDuringFilter"){
					$('.resources_filter_container').removeClass("hide");
					$('.resources_result_count').removeClass("hide");
				}
				var filterNoResultVisibleFlag = $('.resources_filter_container .filter-no-result.zero_result_found').is(":visible");
				if(!filterNoResultVisibleFlag)
					resourcesSearchHelper();
			}
		}
		if(selectboxLowerCase == "people"){
			window.location.hash = selectboxLowerCase;
			if(peopleTabFirstTimeClickedFlag && !validationPassedFlag){
				peopleTabFirstTimeClickedFlag = false;
				peopleSearchHandler("10","newSearch");
			}else{
				var filterNoResultIdentifier = $('.people-filter-no-result-identifier').val();
				if(filterNoResultIdentifier == "NoResultsDuringFilter"){
					$('.people_filter_container').removeClass("hide");
					$('.people_result_count').removeClass("hide");
				}
				var filterNoResultVisibleFlag = $('.people_filter_container .filter-no-result.zero_result_found').is(":visible");
				if(!filterNoResultVisibleFlag)
					peopleSearchHelper();
			}
		}
		$('.search_result_categories').removeClass("hide");
		$('#loadingMask').fadeOut(100);
		/*integration changes - end*/

	});
	/* Search menu active class add End */
	/* Search menu selected on mobile Start */
	$('select.search_select').on('change', function(e) {
		$('#loadingMask').show();
		searchResultCatHide();
		selectboxSpace = $(this).val();
		$('.people_filter_mobile').addClass("hide");
		$('.resources_filter_mobile').addClass("hide");
		$('.locations_filter_mobile').addClass("hide");
		var validationPassedFlag = $('.search_text_error_message').is(":visible");
		selectbox = selectboxSpace.replace(" ", "_");
		selectboxLowerCase = selectbox.toLowerCase();
		$("ul.search_select a").removeClass('active');
		$("ul.search_select a").each(function() {
			if($(this).attr("index")==selectboxSpace)
				$(this).addClass('active');
		});
		$("."+selectboxLowerCase+"_result_count").removeClass('hide');if(selectboxLowerCase!='all_results' && selectboxLowerCase!='products' && selectboxLowerCase!="faqs")
			$("a[index='"+selectboxLowerCase+"_filter']").removeClass('hide');
		$("."+selectboxLowerCase+"_filter_mobile").removeClass('hide');
		$("."+selectboxLowerCase+"_filter_desktop").removeClass('hide');
		$("."+selectboxLowerCase+"_filter_container").removeClass('hide');

		$('.all_results_no_result.zero_result_found').addClass("hide");
		$('.all_results_filter_container').addClass("hide");
		$('.search_result_filters_all_results').addClass("hide");

		$('.locations_result_count').addClass("hide");
		$('.locations_no_result.zero_result_found').addClass("hide");
		$('.locations_filter_container').addClass("hide");
		$('.location.mobile-filter-button').addClass("hide");
		$('.resources_result_count').addClass("hide");
		$('.resources_no_result.zero_result_found').addClass("hide");
		$('.resources_filter_container').addClass("hide");
		$('.resources.mobile-filter-button').addClass("hide");

		$('.people_result_count').addClass("hide");
		$('.people_no_result.zero_result_found').addClass("hide");
		$('.people_filter_container').addClass("hide");
		$('.people.mobile-filter-button').addClass("hide");

		$('.faqs_result_count').addClass("hide");
		$('.faqs_no_result.zero_result_found').addClass("hide");
		$('.faqs_filter_container').addClass("hide");
		$('.search_result_filters_faqs').addClass("hide");

		$('.products_result_count').addClass("hide");
		$('.products_no_result.zero_result_found').addClass("hide");
		$('.products_filter_container').addClass("hide");
		$('.search_result_filters_products').addClass("hide");

		$('.search_result_count').removeClass("hide");
		if(selectboxLowerCase=="faqs") {
			window.location.hash = selectboxLowerCase;
			if(faqsTabFirstTimeClickedFlag && !validationPassedFlag){
				faqsTabFirstTimeClickedFlag = false;
				faqSearchHandler();
			}
			if(!faqsTabFirstTimeClickedFlag){
				$('#loadingMask').fadeOut(100);
			}
			$('.search_result_filters_products').parent().removeClass('author_hide');
			$('.search_result_filters_'+selectboxLowerCase+'').removeClass('hide');
			$('.faqs_result_count').removeClass("hide");
			var faq_result_count = parseInt($('.faqs_result_count').text());
			if(faq_result_count == 0){
				$('.faqs_no_result.zero_result_found').removeClass("hide");
				$('.faqs_filter_container').addClass("hide");
				$('.result_filters_other').addClass("author_hide");
				$('.search_result_filters_faqs').addClass("author_hide");
				$('.search_result_filters_faqs').addClass("hide");
			}else if(faq_result_count > 0){
				$('.faqs_filter_container').removeClass("hide");
				$('.faqs_no_result.zero_result_found').addClass("hide");
				$('.search_result_filters_faqs').removeClass("author_hide");
				$('.search_result_filters_faqs').removeClass("hide");
				$('.result_filters_other').removeClass("author_hide");
			}
			
		}

		if(selectboxLowerCase=="products") {
			window.location.hash = selectboxLowerCase;
			if(productsTabFirstTimeClickedFlag && !validationPassedFlag){
				productsTabFirstTimeClickedFlag = false;
				productSearchHandler();
			}
			if(!productsTabFirstTimeClickedFlag){
				$('#loadingMask').fadeOut(100);
			}
			$('.search_result_filters_products').parent().removeClass('author_hide');
			$('.search_result_filters_'+selectboxLowerCase+'').removeClass('hide');
			$('.products_result_count').removeClass("hide");
			var products_result_count = parseInt($('.products_result_count').text());
			if(products_result_count == 0){
				$('.products_no_result.zero_result_found').removeClass("hide");
				$('.products_filter_container').addClass("hide");
				$('.result_filters_other').addClass("author_hide");
				$('.search_result_filters_products').addClass("author_hide");
			}else if(products_result_count > 0){
				$('.products_filter_container').removeClass("hide");
				$('.products_no_result.zero_result_found').addClass("hide");
				$('.search_result_filters_products').removeClass("author_hide");
				$('.result_filters_other').removeClass("author_hide");
			}
			
		}

		if(selectboxLowerCase=="all_results") {
			window.location.hash = selectboxLowerCase;
			if(allTabFirstTimeClickedFlag && !validationPassedFlag){
				allTabFirstTimeClickedFlag = false;
				allCategorySearchHandler();
			}
			if(!allTabFirstTimeClickedFlag){
				$('#loadingMask').fadeOut(100);
			}
			$('.search_result_filters_all_results').parent().removeClass('author_hide');
			$('.search_result_filters_'+selectboxLowerCase+'').removeClass('hide');
			$('.search_result_count').addClass("hide");
			var all_result_count = parseInt(filteredAllResultsArray.length);
			if(all_result_count == 0){
				$('.all_results_no_result.zero_result_found').removeClass("hide");
				$('.all_results_filter_container').addClass("hide");
				$('.result_filters_other').addClass("author_hide");
				$('.search_result_filters_all_results').addClass("author_hide");
				$('.search_result_filters_all_results').addClass("hide");
			}else if(all_result_count > 0){
				$('.all_results_filter_container').removeClass("hide");
				$('.all_results_no_result.zero_result_found').addClass("hide");
				$('.search_result_filters_all_results').removeClass("author_hide");
				$('.search_result_filters_all_results').removeClass("hide");
				$('.result_filters_other').removeClass("author_hide");
			}
			
		}
		if(selectboxLowerCase=="locations") {
			window.location.hash = selectboxLowerCase;
			if(locationsTabFirstTimeClickedFlag && !validationPassedFlag){
				locationsTabFirstTimeClickedFlag = false;
				locationSearchHandler("10","newSearch");
			}
			if(!locationsTabFirstTimeClickedFlag){
				$('#loadingMask').fadeOut(100);
			}
			var filterNoResultIdentifier = $('.location-filter-no-result-identifier').val();
			if(filterNoResultIdentifier == "NoResultsDuringFilter"){
				$('.locations_filter_container').removeClass("hide");
				$('.locations_result_count').removeClass("hide");
			}
			var filterNoResultVisibleFlag = $('.locations_filter_container .filter-no-result.zero_result_found').is(":visible");
			if(!filterNoResultVisibleFlag){
				$('.locations_result_count').removeClass("hide");
				var locations_result_count = parseInt($('.locations_result_count').text());
				if(locations_result_count == 0){
					$('.location.mobile-filter-button').addClass("hide");
					$('.locations_no_result.zero_result_found').removeClass("hide");
					$('.search_result_filters_nonProducts').addClass('hide');
					$('.result_filters_other').addClass('author_hide');
					$('.locations_filter_container').addClass("hide");
				}else if(locations_result_count > 0){
					$('.location.mobile-filter-button').removeClass("hide");
					$('.locations_filter_container').removeClass("hide");
					$('.search_result_filters_nonProducts').removeClass('hide');
					$('.result_filters_other').removeClass('author_hide');
					$('.locations_no_result.zero_result_found').addClass("hide");
				}
			}
			
		}
		
		if(selectboxLowerCase=="resources") {
			window.location.hash = selectboxLowerCase;
			if(resourcesTabFirstTimeClickedFlag && !validationPassedFlag){
				resourcesTabFirstTimeClickedFlag = false;
				resourcesSearchHandler();
			}
			if(!resourcesTabFirstTimeClickedFlag){
				$('#loadingMask').fadeOut(100);
			}
			var filterNoResultIdentifier = $('.resources-filter-no-result-identifier').val();
			if(filterNoResultIdentifier == "NoResultsDuringFilter"){
				$('.resources_filter_container').removeClass("hide");
				$('.resources_result_count').removeClass("hide");
			}
			var filterNoResultVisibleFlag = $('.resources_filter_container .filter-no-result.zero_result_found').is(":visible");
			if(!filterNoResultVisibleFlag){
				$('.resources_result_count').removeClass("hide");
				var resources_result_count = parseInt($('.resources_result_count').text());
				if(resources_result_count == 0){
					$('.resources.mobile-filter-button').addClass("hide");
					$('.resources_no_result.zero_result_found').removeClass("hide");
					$('.search_result_filters_nonProducts').addClass('hide');
					$('.result_filters_other').addClass('author_hide');
					$('.resources_filter_container').addClass("hide");
				}else if(resources_result_count > 0){
					$('.resources.mobile-filter-button').removeClass("hide");
					$('.resources_filter_container').removeClass("hide");
					$('.search_result_filters_nonProducts').removeClass('hide');
					$('.result_filters_other').removeClass('author_hide');
					$('.resources_no_result.zero_result_found').addClass("hide");
				}
			}			
		}
		if(selectboxLowerCase=="people") {
			window.location.hash = selectboxLowerCase;
			if(peopleTabFirstTimeClickedFlag && !validationPassedFlag){
				peopleTabFirstTimeClickedFlag = false;
				peopleSearchHandler("10","newSearch");
			}
			if(!peopleTabFirstTimeClickedFlag){
				$('#loadingMask').fadeOut(100);
			}
			var filterNoResultIdentifier = $('.people-filter-no-result-identifier').val();
			if(filterNoResultIdentifier == "NoResultsDuringFilter"){
				$('.people_filter_container').removeClass("hide");
				$('.people_result_count').removeClass("hide");
			}
			var filterNoResultVisibleFlag = $('.people_filter_container .filter-no-result.zero_result_found').is(":visible");
			if(!filterNoResultVisibleFlag){
				$('.people_result_count').removeClass("hide");
				var people_result_count = parseInt($('.people_result_count').text());
				if(people_result_count == 0){
					$('.people.mobile-filter-button').addClass("hide");
					$('.people_no_result.zero_result_found').removeClass("hide");
					$('.search_result_filters_nonProducts').addClass('hide');
					$('.result_filters_other').addClass('author_hide');
					$('.people_filter_container').addClass("hide");
				}else if(people_result_count > 0){
					$('.people.mobile-filter-button').removeClass("hide");
					$('.people_filter_container').removeClass("hide");
					$('.search_result_filters_nonProducts').removeClass('hide');
					$('.result_filters_other').removeClass('author_hide');
					$('.people_no_result.zero_result_found').addClass("hide");
				}
			}
			
		}
	});
	/* Search menu selected on mobile End */

	/* Sect box JS Start*/
	/*$('#search_mobile_tab_navigation, #location-radius-mobile,#people-radius-mobile,#location-radius-desktop,#people-radius-desktop').uniform({
        selectClass: 'sun-select-container',
        selectAutoWidth: false
    }).each(function() {
        $(this).siblings("span").attr("aria-hidden", true);
    });*/
	/* Sect box JS End*/
	/* Search Clear Button Enable Start */
	searchTxt = $('.search_text_box_reset');
	searchTxt.find('[data-sun-type="search-cancel-button"]').click(function(e)
	{
		var tmpClass = $(this);
		e.preventDefault(),
		tmpClass.removeClass("sun-active"),
		tmpClass.prev("input").val("").focus(),
		$(".suntrust-autocomplete").empty()
	}),
	searchTxt.find('[type="search"]').keyup(function() {
		var txtKeyUp = $(this), txtLength = txtKeyUp.val().length;
		txtLength > 0 ? $currentCancelButton.addClass("sun-active"): $currentCancelButton.removeClass("sun-active")
	}),
	searchTxt.find('[type="search"]').focus(function()
	{
		var n = $(this);
		$currentCancelButton = n.next('[data-sun-type="search-cancel-button"]');
	});
	/*  Search Clear Button Enable End */
	/* Location hide show for more than 1 location in the list Start  */
	function showHideLocationToggleForAllResults(){
		$('.search_result_description_details').each(function(){
			var count=0;
			$(this).find('.location_tog .location_show_hide > .clearfix').each(function(){
				count++;
			});
			if(count>1) {
				$(this).find('.location_tog > a').show();
			}
			else if(count<=1) {
				$(this).find('.location_tog > a').hide();
			}
		});
	}
	/* Location hide show for more than 1 location in the list End  */
	/* Hide Show location toggle Start */
	$(document).on("click",".location_toggle",function(){
		$(this).next().slideToggle();
		if($(this).find('strong').text()==" Show Locations") {
			$(this).find('strong').text(' Hide Locations');
			$(this).find('span').text('-');
		}
		else if($(this).find('strong').text()==" Hide Locations") {
			$(this).find('strong').text(' Show Locations');
			$(this).find('span').text('+');
		}
	});
	/*$(window).resize(function() {
		searchResultCatHideResize();
		searchResultCat();
	});*/
	/* Search FAQ tab start*/
	$(document).on('click','.integrated-search .sun-faqs-page-list-item-header',function(){
		$(this).toggleClass("sun-active");
		$(this).next().toggleClass("sun-active");
	});
	/* Search FAQ tab end*/
	/* Hide Show location toggle End */
	/* Suntrust Custom Checkbox Start */
	$(document).on('click', '.integrated-search .sun-checkbox-input-container', function(){
		$(this).find("span").toggleClass("sun-checked");
	});
	/* Suntrust Custom Checkbox End */

	/*Mobile Filter Search Start*/
	$(document).on("click",".search_result_categories .visible-xs a",function(){
		$(".mobile_filter_search").animate({left: "0px"});
	});
	$(document).on("click",".search_result_categories .location.mobile-filter-button",function(){
		locationSearchMobileRadiusChangedFlag = false;
	});
	$(document).on("click",".search_result_categories .people.mobile-filter-button",function(){
		peopleSearchMobileRadiusChangedFlag = false;
	});

	$(document).on('click','[data-suntrust-class="suntrust-menu-close"]',function(){
		$(this).parents('.mobile_filter_search').animate({left: "-320px"});
	});
	/*Mobile Filter Search End*/
	/*Search By People Filter Search Search*/
	$(document).on('click','.filter_title > a',function(){
		$(".filter_show_hide").not($(this).parent().next()).slideUp();
		$(".filter_title > a span:first-child").not($(this).find('span:first-child')).text('+');
		$('.state_city_selectall_clear').not($(this).next()).hide();
		$(this).parent().next().slideToggle();
		if($(this).find('span:first-child').text()=='-')
			$(this).find('span:first-child').text('+');
		else if($(this).find('span:first-child').text()=='+')
			$(this).find('span:first-child').text('-');
		if($(this).attr('area-filter'))
			$(this).next().toggle();
	});
	/*Search By People Filter Search End*/



	$('.search_text_box_reset .suntrust-input-location-button').click(function(event) {
		event.preventDefault();
		event.stopImmediatePropagation();
		var $button = $(this);
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(position) {
				showLocation($button,position)
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


    function updateDistance(startLat, startLng, endLat, endLng){
        var start = L.latLng(startLat,startLng);
        var end = L.latLng(endLat, endLng);
        var distance = start.distanceTo(end).toFixed(0);
        if (!distance) {
            distance = 0.00;
        } else {
            distance = (Math.round(distance * 0.000621371 * 100) / 100);
        }
        return distance;
    }

    /*Find us with search - starts*/
    /*$(document).on("click", ".find_us .find_search.suntrust-secondary-button", function(e){
        e.preventDefault();
		var searchTerm = $('.find_us .suntrust_find_input').val();
        var redirectionURL = $(this).attr("href");
        window.location.href = redirectionURL+"?searchString="+searchTerm+"&searchIn=locations";
    });*/
    /*Find us with search - ends*/

    /*Location modal Window with search - starts*/
    /*$(document).on("click", ".suntrust-modal-window .suntrust-nav-location .suntrust-orange-button", function(e){
        e.preventDefault();
		var searchTerm = $('.suntrust-modal-window .suntrust-nav-location .suntrust-login-input.suntrust-nav-location-input').val();
        var redirectionURL = $(this).attr("href");
        window.location.href = redirectionURL+"?searchString="+searchTerm+"&searchIn=locations";
    });*/
    /*Location modal Window with search - ends*/




    //Initially Select All checkbox - starts
    function selectAllFilters(){
        $('.locations_filter_desktop .search_detail_city input, .locations_filter_mobile  .mobile-city input').each(function(e){
            $(this).prop('checked', true);
            $(this).parent().addClass("sun-checked");
			if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
				$(this).parents('.sun-checkbox-input-field').next().removeClass('hide');
			}
        });


        $('.locations_filter_desktop .search_detail_state input, .locations_filter_mobile .mobile-state input').each(function(e){
            $(this).prop('checked', true);
            $(this).parent().addClass("sun-checked");
			if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
				$(this).parents('.sun-checkbox-input-field').next().removeClass('hide');
			}
        });


        $('.locations_filter_desktop .search_detail_services_level0 input, .locations_filter_mobile .mobile-services input').each(function(e){
            $(this).prop('checked', true);
            $(this).parent().addClass("sun-checked");
			if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
				$(this).parents('.sun-checkbox-input-field').next().removeClass('hide');
				$(this).parents('.sun-checkbox-input-field').next().show();
			}
            valueFromServicesCheckBoxGlobal.push($(this).val());			
        });

        valueFromServicesCheckBoxGlobal = $.unique(valueFromServicesCheckBoxGlobal);
    }
    //Initially Select All checkbox - ends
	function selectAllFiltersResources(){
		$('.resources_filter_desktop .resource-type input, .resources_filter_mobile  .resource-type input').each(function(e){
			$(this).prop('checked', true);
			$(this).parent().addClass("sun-checked");

			if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
				$(this).parents('.sun-checkbox-input-field').next().show();
			}
		});


		$('.resources_filter_desktop .article-type input, .resources_filter_mobile .article-type input').each(function(e){
			$(this).prop('checked', true);
			$(this).parent().addClass("sun-checked");
		});
	}
	function selectAllFiltersPeople(){
        $('.people_filter_desktop .search_detail_city input, .people_filter_mobile .mobile-city input').each(function(e){
            $(this).prop('checked', true);
            $(this).parent().addClass("sun-checked");
        });


        $('.people_filter_desktop .search_detail_state input, .people_filter_mobile .mobile-state input').each(function(e){
            $(this).prop('checked', true);
            $(this).parent().addClass("sun-checked");
        });


        $('.people_filter_desktop .search_detail_speciality_level input, .people_filter_mobile .mobile-speciality input').each(function(e){
            $(this).prop('checked', true);
            $(this).parent().addClass("sun-checked");
        });

    }

	//Show More Results - Starts

	function allCategorySearchShowMore(){
		$('.all_result_show_more_results').removeClass("hide");
		var visibleResultsCount = parseInt($('.all_results_filter_container').children().length);
		var actualResultsCount = filteredAllResultsArray.length;
		var showMoreItemCount = parseInt($('.all_result_show_more_results a').attr("data-search-visible-items-limit"));
		if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
			$('.all_result_show_more_results').addClass("hide");
		}else{
			$('.all_result_show_more_results').removeClass("hide");
		}
		//$("div.search_result_content:visible:last").css("border-bottom","0px");
	}
    function locationSearchShowMore(){
		$('.location_result_show_more_results').removeClass("hide");
		var visibleMqResultsCount = $('.location_mq_results').children().length;
		var visibleAemResultsCount = $('.location_aem_results').children().length;
		var visibleResultsCount = parseInt(visibleMqResultsCount) + parseInt(visibleAemResultsCount);
		var actualResultsCount = filteredLocationArray.length;
		var showMoreItemCount = parseInt($('.location_result_show_more_results a').attr("data-search-visible-items-limit"));
		if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
			$('.location_result_show_more_results').addClass("hide");
		}else{
			$('.location_result_show_more_results').removeClass("hide");
		}
		$("div.search_result_description_details:visible:last").css("border-bottom","0px");

	}
	function resourcesSearchShowMore(){
		$('.resources_result_show_more_results').removeClass("hide");
		var visibleResourcesResultsCount = $('.resources_filter_container .search_result_content').children().length;
		var visibleResultsCount = parseInt(visibleResourcesResultsCount);
		var actualResultsCount = filteredResourcesArray.length;
		var showMoreItemCount = parseInt($('.resources_result_show_more_results a').attr("data-search-visible-items-limit"));
		if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
			$('.resources_result_show_more_results').addClass("hide");
		}else{
			$('.resources_result_show_more_results').removeClass("hide");
		}
		$("div.search_result_content_data:visible:last").css("border-bottom","0px");
	}
	function peopleSearchShowMore(){
		$('.people_result_show_more_results').removeClass("hide");
		var visibleMqResultsCount = $('.people_mq_results').children().length;
		var visibleAemResultsCount = $('.people_aem_results').children().length;
		var visibleResultsCount = parseInt(visibleMqResultsCount) + parseInt(visibleAemResultsCount);
		var actualResultsCount = filteredPeopleArray.length;
		var showMoreItemCount = parseInt($('.people_result_show_more_results a').attr("data-search-visible-items-limit"));
		if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
			$('.people_result_show_more_results').addClass("hide");
		}else{
			$('.people_result_show_more_results').removeClass("hide");
		}
		$("div.search_result_description_details:visible:last").css("border-bottom","0px");
    }

    function productSearchShowMore(){
    	selectboxSpace = $("ul.search_select a.active").attr("index");
		selectbox = $("ul.search_select a.active").attr("index");
		if(selectbox != undefined)
			selectboxLowerCase = selectbox.toLowerCase();
        $(".search_result_description .search-results").each(function() {
            var size_li = $(this).find('div.search_result_content_data').length;
            var x = parseInt($('.search_result_show_more_results a').attr("data-search-visible-items-limit"));
            $(this).find('div.search_result_content_data:lt(' + x + ')').show();
			if(selectboxLowerCase=="products") {
				if (size_li <= x) {
					$(this).parents('.search_result_description').find('.search_result_show_more_results').addClass("hide");
				}else{
					$(this).parents('.search_result_description').find('.search_result_show_more_results').removeClass("hide");
				}
			}
            $("div.search_result_content_data:visible:last").css("border-bottom","0px");
        });
    }

	function faqSearchShowMore(){
		selectboxSpace = $("ul.search_select a.active").attr("index");
		selectbox = $("ul.search_select a.active").attr("index");
		if(selectbox != undefined)
			selectboxLowerCase = selectbox.toLowerCase();
        $(".search_result_description .search-results").each(function() {
        	var size_li = $(this).find('div.sun-faqs-page-list-item').length;
            var x = parseInt($('.faq_result_show_more_results a').attr("data-search-visible-items-limit"));
            $(this).find('div.sun-faqs-page-list-item:lt(' + x + ')').show();
			if(selectboxLowerCase=="faqs") {
				if (size_li <= x) {
					$(this).parents('.search_result_description').find('.faq_result_show_more_results').addClass("hide");
				}else{
					$(this).parents('.search_result_description').find('.faq_result_show_more_results').removeClass("hide");
				}
			}
            $("div.sun-faqs-page-list-item:visible:last").css("border-bottom","0px");
        });
    }
    $(document).on('click','.location_result_show_more_results',function() {
        $("div.search_result_description_details:visible:last").css("border-bottom","1px solid #cfcfcf");

var actualResultsCount = filteredLocationArray.length;
		locationShowMoreCounter = locationShowMoreCounter + parseInt($('.location_result_show_more_results a').attr('data-search-visible-items-limit'));
		numberOfResultToBeDisplayed = numberOfResultToBeDisplayed+parseInt($('.location_result_show_more_results a').attr('data-search-visible-items-limit'));
		if(numberOfResultToBeDisplayed < actualResultsCount){
			resultCreator(filteredLocationArray,"singleDimension","refineResult",numberOfResultToBeDisplayed,locationShowMoreCounter);
		}else{
			resultCreator(filteredLocationArray,"singleDimension","refineResult",actualResultsCount,locationShowMoreCounter);
		}
		var visibleMqResultsCount = $('.location_mq_results').children().length;
		var visibleAemResultsCount = $('.location_aem_results').children().length;
		var visibleResultsCount = parseInt(visibleMqResultsCount) + parseInt(visibleAemResultsCount);
		var showMoreItemCount = parseInt($('.location_result_show_more_results a').attr("data-search-visible-items-limit"));
		if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
			$('.location_result_show_more_results').addClass("hide");
		}else{
			$('.location_result_show_more_results').removeClass("hide");
		}
		$("div.search_result_description_details:visible:last").css("border-bottom","0px");
    });
	$(document).on('click','.resources_result_show_more_results',function() {
		$("div.search_result_content_data:visible:last").css("border-bottom","1px solid #cfcfcf");

		var actualResultsCount = filteredResourcesArray.length;
		resourcesShowMoreCounter = resourcesShowMoreCounter + parseInt($('.resources_result_show_more_results a').attr('data-search-visible-items-limit'));
		numberOfResultToBeDisplayedResources = numberOfResultToBeDisplayedResources+parseInt($('.resources_result_show_more_results a').attr('data-search-visible-items-limit'));
		if(numberOfResultToBeDisplayedResources < actualResultsCount){
			resourcesResultCreator(filteredResourcesArray,"refineResult",numberOfResultToBeDisplayedResources,resourcesShowMoreCounter);
		}else{
			resourcesResultCreator(filteredResourcesArray,"refineResult",actualResultsCount,resourcesShowMoreCounter);
		}
		var visibleResourcesResultsCount = $('.resources_filter_container .search_result_content').children().length;
		var visibleResultsCount = parseInt(visibleResourcesResultsCount);
		var showMoreItemCount = parseInt($('.resources_result_show_more_results a').attr("data-search-visible-items-limit"));
		if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
			$('.resources_result_show_more_results').addClass("hide");
		}else{
			$('.resources_result_show_more_results').removeClass("hide");
		}
		$("div.search_result_content_data:visible:last").css("border-bottom","0px");
	});
	$(document).on('click','.people_result_show_more_results',function() {
        $("div.search_result_description_details:visible:last").css("border-bottom","1px solid #cfcfcf");

		var actualResultsCount = filteredPeopleArray.length;
		peopleShowMoreCounter = peopleShowMoreCounter + parseInt($('.people_result_show_more_results a').attr('data-search-visible-items-limit'));
		numberOfResultToBeDisplayedPeople = numberOfResultToBeDisplayedPeople+parseInt($('.people_result_show_more_results a').attr('data-search-visible-items-limit'));
		if(numberOfResultToBeDisplayedPeople < actualResultsCount){
			resultCreatorPeople(filteredPeopleArray,"singleDimension","refineResult",numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
		}else{
			resultCreatorPeople(filteredPeopleArray,"singleDimension","refineResult",actualResultsCount,peopleShowMoreCounter);
		}


		var visibleMqResultsCount = $('.people_mq_results').children().length;
		var visibleAemResultsCount = $('.people_aem_results').children().length;
		var visibleResultsCount = parseInt(visibleMqResultsCount) + parseInt(visibleAemResultsCount);

		var showMoreItemCount = parseInt($('.people_result_show_more_results a').attr("data-search-visible-items-limit"));
		if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
			$('.people_result_show_more_results').addClass("hide");
		}else{
			$('.people_result_show_more_results').removeClass("hide");
		}
		$("div.search_result_description_details:visible:last").css("border-bottom","0px");
    });

    $(document).on('click','.search_result_show_more_results',function() {
        $("div.search_result_content_data:visible:last").css("border-bottom","1px solid #cfcfcf");
        var x1 = parseInt($(this).find('a').attr("data-search-visible-items-limit"));
        var x = $(this).parent().find('.search-results div.search_result_content_data:visible').length;
        var size_li = $(this).parent().find('.search-results div.search_result_content_data').length;
        x = (x + x1 <= size_li) ? x + x1 : size_li;
        $(this).parent().find('.search-results div.search_result_content_data:lt(' + x + ')').show();
        if (x >= size_li) {
            $(this).hide();
        }
        $("div.search_result_content_data:visible:last").css("border-bottom","0px");
    });
    $(document).on('click','.all_result_show_more_results',function() {
		allCategoryShowMoreCounter = allCategoryShowMoreCounter + parseInt($('.all_result_show_more_results a').attr('data-search-visible-items-limit'));
        allResultsNumberOfResultToBeDisplayed = allResultsNumberOfResultToBeDisplayed+parseInt($('.all_result_show_more_results a').attr('data-search-visible-items-limit'));
		var actualResultsCount = filteredAllResultsArray.length;
		if(allResultsNumberOfResultToBeDisplayed < actualResultsCount){
			allCategoryResultCreator(filteredAllResultsArray,allResultsNumberOfResultToBeDisplayed,allCategoryShowMoreCounter);
			irrelaventDivsRemover();
			closedHoursCreator();
		}
		else{
			allCategoryResultCreator(filteredAllResultsArray,actualResultsCount,allCategoryShowMoreCounter);
			irrelaventDivsRemover();
			closedHoursCreator();
		}
		var visibleResultsCount = $('.all_results_filter_container').children().length;

		var showMoreItemCount = parseInt($('.all_result_show_more_results a').attr("data-search-visible-items-limit"));
		if(visibleResultsCount == actualResultsCount || actualResultsCount <= showMoreItemCount){
			$('.all_result_show_more_results').addClass("hide");
		}else{
			$('.all_result_show_more_results').removeClass("hide");
		}
		//$("div.search_result_content:visible:last").css("border-bottom","0px");
    });
    $(document).on('click','.faq_result_show_more_results',function() {
        $("div.sun-faqs-page-list-item:visible:last").css("border-bottom","1px solid #cfcfcf");
        var x1 = parseInt($(this).find('a').attr("data-search-visible-items-limit"));
        var x = $(this).parent().find('.search-results div.sun-faqs-page-list-item:visible').length;
        var size_li = $(this).parent().find('.search-results div.sun-faqs-page-list-item').length;
        x = (x + x1 <= size_li) ? x + x1 : size_li;
        $(this).parent().find('.search-results div.sun-faqs-page-list-item:lt(' + x + ')').show();
        if (x >= size_li) {
            $(this).hide();
        }
        $("div.sun-faqs-page-list-item:visible:last").css("border-bottom","0px");
    });
    
    //Show More Results - Ends
    function duplicateResultRemover(refinedDuplicateServicesArray){
        var servicesArray = [];
        var uniqueStatesArray = [];
        var refinedUniqueServicesResult=[];
        for (var j=0;j<refinedDuplicateServicesArray.length;j++){
            for (var p=0;p<refinedDuplicateServicesArray[j].length;p++){
                servicesArray.push(refinedDuplicateServicesArray[j][p].loc_detail_page);                    
            }
        }
        $.each(servicesArray, function(i, el){
            if($.inArray(el, refinedUniqueServicesResult) === -1) refinedUniqueServicesResult.push(el);
        });
        return refinedUniqueServicesResult;
    }
    function closedHoursCreator(){
        $('.search_result_location_branch_hours').each(function(e){
            if($(this).find("p").text() == "Closed"){
                $(this).find("p").css("color","#c94c06");
                var pageUrl = $(this).parent().siblings().find(' .search_result_description_detail_addr h5 a').attr('href');
                $("<p>(<a href='"+pageUrl+"'>more hours</a>)</p>").insertAfter($(this).find("p"));
            }
        });

        $('.search_result_location_drive_thru_hours').each(function(e){
            if($(this).find("p").text() == "Closed"){
                $(this).find("p").css("color","#c94c06");
                var pageUrl = $(this).parent().siblings().find(' .search_result_description_detail_addr h5 a').attr('href');
                $("<p>(<a href='"+pageUrl+"'>more hours</a>)</p>").insertAfter($(this).find("p"));
            }
        });
        
        $('.search_result_location_teller_connect_hours').each(function(e){
            if($(this).find("p").text() == "Closed"){
            	//$(this).find("p").text("Closed Now");
                $(this).find("p").css("color","#c94c06");
                var pageUrl = $(this).parent().siblings().find(' .search_result_description_detail_addr h5 a').attr('href');
                $("<p>(<a href='"+pageUrl+"'>more hours</a>)</p>").insertAfter($(this).find("p"));
            }
        });
    }

    function getDirectionLinkFormaterForUserLocationNotAllowed(){
        $('.search-results .search_result_location_direction').each(function(){
			var getDirectionLink = $(this).find('.search_result_location_direction_details a').attr('href');
			if(getDirectionLink.indexOf("undefined")>0){
				$(this).find('.search_result_location_direction_details a').attr('href',getDirectionLink.split("?")[0]);				
			}
		});
    }
    

$('.locations_filter_mobile .mobile-radius-filter #location-radius-mobile').on("change",function(e){
	locationSearchMobileRadiusChangedFlag = true;
});
$('.people_filter_mobile .mobile-radius-filter #people-radius-mobile').on("change",function(e){
	peopleSearchMobileRadiusChangedFlag = true;
});
function duplicateResultDivRemover (){
    var seen = {};
    $('.locations_filter_container .search_result_description .search-results .address.search_result_description_detail_addr').each(function() {
        var txt = $(this).find('a').attr('href');
        if (seen[txt])
            $(this).parent().parent().remove();
        else
            seen[txt] = true;
    });
}
function irrelaventDivsRemover(){
	$('.locations_filter_container .search_result_location_branch_hours, .all_results_filter_container .search_result_location_branch_hours').each(function(e){
		if($(this).find('p').text() == "AlwaysClosed"){
			$(this).remove();
		}
	});
	$('.locations_filter_container .search_result_location_drive_thru_hours, .all_results_filter_container .search_result_location_drive_thru_hours').each(function(e){
		if($(this).find('p').text() == "AlwaysClosed"){
			$(this).remove();
		}
	});
	$('.locations_filter_container .search_result_location_teller_connect_hours, .all_results_filter_container .search_result_location_teller_connect_hours').each(function(e){
		if($(this).find('p').text() == "AlwaysClosed"){
			$(this).remove();
		}
	});
	$('.locations_filter_container .search_result_location_phone, .people_filter_container .search_result_location_phone, .all_results_filter_container .search_result_location_phone').each(function(e){
		if($(this).find('a').text() == "" || $(this).find('a').text() == undefined || $(this).find('a').text() == "undefined"){
			$(this).remove();
		}
	});
	$('.locations_filter_container .search_result_location_fax, .all_results_filter_container .search_result_location_fax').each(function(e){
		if($(this).find('p').text() == "" || $(this).find('p').text() == undefined || $(this).find('p').text() == "undefined"){
			$(this).remove();
		}
	});
	$('.locations_filter_desktop .search_detail_state_city_select input').each(function(e){
		if($(this).val()=="" || $(this).val()== "undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){
		if($(this).val()=="" || $(this).val()== "undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	$('.locations_filter_mobile .mobile-city input').each(function(e){
		if($(this).val()=="" || $(this).val()== "undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	$('.locations_filter_mobile .mobile-state input').each(function(e){
		if($(this).val()=="" || $(this).val()== "undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	$('.locations_filter_mobile .mobile-services input').each(function(e){
		if($(this).val()=="" || $(this).val()== "undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	
	$('.people_filter_desktop .search_detail_state_city_select input').each(function(e){
		if($(this).val()=="" || $(this).val()== "undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	$('.people_filter_desktop .search_detail_speciality_level input').each(function(e){
		if($(this).val()=="" || $(this).val()=="undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	$('.people_filter_mobile .mobile-city input').each(function(e){
		if($(this).val()=="" || $(this).val()=="undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	$('.people_filter_mobile .mobile-state input').each(function(e){
		if($(this).val()==""|| $(this).val()=="undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	$('.people_filter_mobile .mobile-speciality input').each(function(e){
		if($(this).val()=="" || $(this).val()=="undefined" || $(this).val()== undefined)
			$(this).parent().parent().parent().remove();
	});
	
}

function resultCreator(resultToBeDisplayed,arrayType,eventType, numberOfResultToBeDisplayed, locationShowMoreCounter){
	var mqResultToBeDisplayed = [];
	var aemResultToBeDisplayed = [];
	var responseStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
	if(resultToBeDisplayed.length < numberOfResultToBeDisplayed)
		numberOfResultToBeDisplayed = resultToBeDisplayed.length;
	for(var i = 0; i < resultToBeDisplayed.length; i++ ){
		if(resultToBeDisplayed[i].loc_result_type == "AEM"){
			mqResultToBeDisplayed.push(resultToBeDisplayed[i]);
		} else if(resultToBeDisplayed[i].loc_result_type == "MQ"){
			aemResultToBeDisplayed.push(resultToBeDisplayed[i]);
		}
	}		
	if(resultToBeDisplayed.length > 0){
		if(startLat != undefined && startLng != undefined){
			mqResultToBeDisplayed.sort(function (a, b) {
				if (a.loc_miles_away < b.loc_miles_away) {
					return -1;
				}
				else if (a.loc_miles_away > b.loc_miles_away) {
					return 1;
				}
				return 0;
			});
			aemResultToBeDisplayed.sort(function (a, b) {
				if (a.loc_miles_away < b.loc_miles_away) {
					return -1;
				}
				else if (a.loc_miles_away > b.loc_miles_away) {
					return 1;
				}
				return 0;
			});
		}else{
			mqResultToBeDisplayed.sort(function (a, b) {
				if (a.loc_locationname < b.loc_locationname) {
					return -1;
				}
				else if (a.loc_locationname > b.loc_locationname) {
					return 1;
				}
				return 0;
			});
			aemResultToBeDisplayed.sort(function (a, b) {
				if (a.loc_locationname < b.loc_locationname) {
					return -1;
				}
				else if (a.loc_locationname > b.loc_locationname) {
					return 1;
				}
				return 0;
			});
		}
		for(var i = 0; i < mqResultToBeDisplayed.length; i++ ){
			aemResultToBeDisplayed.push(mqResultToBeDisplayed[i]);
		}
		if(startLat != undefined && startLng != undefined){
			if(arrayType == "singleDimension"){ 				
				for(var i = locationShowMoreCounter; i < numberOfResultToBeDisplayed; i++ ){
					var destination = aemResultToBeDisplayed[i].loc_address+", "+aemResultToBeDisplayed[i].loc_city+", "+aemResultToBeDisplayed[i].loc_state_tag.toUpperCase()+", "+aemResultToBeDisplayed[i].loc_zipcode;
						if ($(window).width() < 767) {  
							if(/Android/i.test(navigator.userAgent)){
								var getDirectionUrl = "https://www.google.com/maps/dir/?api=1&origin="+searchString+"&destination="+destination+"&mode=d";
							}else if(/iPhone/i.test(navigator.userAgent)){
								var getDirectionUrl = "http://maps.apple.com?saddr="+searchString+"&daddr="+destination+"&directionsmode=transit";
							}else{
								var getDirectionUrl = aemResultToBeDisplayed[i].loc_detail_page+"?start="+searchString;
							}
						}
					if(aemResultToBeDisplayed[i].loc_result_type == "AEM"){
						$('.search_result_description .search-results .locations_filter_container .location_aem_results').append("<div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+aemResultToBeDisplayed[i].loc_detail_page+"?location="+searchString+"'>"+aemResultToBeDisplayed[i].loc_locationname+"</a></strong></h5><p>"+aemResultToBeDisplayed[i].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+aemResultToBeDisplayed[i].loc_city+", </span><span class='state'>"+aemResultToBeDisplayed[i].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+aemResultToBeDisplayed[i].loc_zipcode+"</span></p></div><div class='search_result_description_detail_addr'><h5><strong>Miles</strong></h5><div class='addressDistance' latitude='"+aemResultToBeDisplayed[i].loc_latitude+"' longitude='"+aemResultToBeDisplayed[i].loc_longitude+"'><div class='miles-away'>"+aemResultToBeDisplayed[i].loc_miles_away+"</div></div></div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+aemResultToBeDisplayed[i].loc_phone+"'>"+aemResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+aemResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img  src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' class='get-direction-link-mobile' data-mobile-link='"+getDirectionUrl+"' href='"+getDirectionUrl+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+aemResultToBeDisplayed[i].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+aemResultToBeDisplayed[i].loc_phone+"'>"+aemResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+aemResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+aemResultToBeDisplayed[i].loc_detail_page+"?start="+searchString+"'>Get Directions</a></p></div></div></div></div>");
						
					}else if(aemResultToBeDisplayed[i].loc_result_type == "MQ"){
						$('.search_result_description .search-results .locations_filter_container .location_mq_results').append("<div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+aemResultToBeDisplayed[i].loc_detail_page+"?location="+searchString+"'>"+aemResultToBeDisplayed[i].loc_locationname+"</a></strong></h5><p>"+aemResultToBeDisplayed[i].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+aemResultToBeDisplayed[i].loc_city+", </span><span class='state'>"+aemResultToBeDisplayed[i].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+aemResultToBeDisplayed[i].loc_zipcode+"</span></p></div><div class='search_result_description_detail_addr'><h5><strong>Miles</strong></h5><div class='addressDistance' latitude='"+aemResultToBeDisplayed[i].loc_latitude+"' longitude='"+aemResultToBeDisplayed[i].loc_longitude+"'><div class='miles-away'>"+aemResultToBeDisplayed[i].loc_miles_away+"</div></div></div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+aemResultToBeDisplayed[i].loc_phone+"'>"+aemResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+aemResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' class='get-direction-link-mobile' data-mobile-link='"+getDirectionUrl+"' href='"+getDirectionUrl+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+aemResultToBeDisplayed[i].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+aemResultToBeDisplayed[i].loc_phone+"'>"+aemResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+aemResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+aemResultToBeDisplayed[i].loc_detail_page+"?start="+searchString+"'>Get Directions</a></p></div></div></div></div>");
					}
				}
			}/*else if(arrayType == "associative"){
				for(var i = locationShowMoreCounter; i < resultToBeDisplayed.length; i++ ){
					for(var j = 0; j < resultToBeDisplayed[i].length; j++ ){
						if(resultToBeDisplayed[i][j].loc_result_type == "AEM")
							$('.search_result_description .search-results .locations_filter_container .location_aem_results').append("<div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+mqResultToBeDisplayed[i][j].loc_detail_page+"?location="+searchString+"'>"+mqResultToBeDisplayed[i][j].loc_locationname+"</a></strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+mqResultToBeDisplayed[i][j].loc_city+", </span><span class='state'>"+mqResultToBeDisplayed[i][j].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+mqResultToBeDisplayed[i][j].loc_zipcode+"</span></p></div><div class='search_result_description_detail_addr'><h5><strong>Miles</strong></h5><div class='addressDistance' latitude='"+mqResultToBeDisplayed[i][j].loc_latitude+"' longitude='"+mqResultToBeDisplayed[i][j].loc_longitude+"'><div class='miles-away'>"+mqResultToBeDisplayed[i].loc_miles_away+"</div></div></div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+mqResultToBeDisplayed[i][j].loc_phone+"'>"+mqResultToBeDisplayed[i][j].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+mqResultToBeDisplayed[i][j].loc_detail_page+"?start="+searchString+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+mqResultToBeDisplayed[i][j].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+mqResultToBeDisplayed[i][j].loc_phone+"'>"+mqResultToBeDisplayed[i][j].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+mqResultToBeDisplayed[i][j].loc_detail_page+"?start="+searchString+"'>Get Directions</a></p></div></div></div></div>");
						else if(resultToBeDisplayed[i][j].loc_result_type == "MQ")
							$('.search_result_description .search-results .locations_filter_container .location_mq_results').append("<div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+mqResultToBeDisplayed[i].loc_detail_page+"?location="+searchString+"'>"+mqResultToBeDisplayed[i].loc_locationname+"</a></strong></h5><p>"+mqResultToBeDisplayed[i].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+mqResultToBeDisplayed[i].loc_city+", </span><span class='state'>"+mqResultToBeDisplayed[i].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+mqResultToBeDisplayed[i].loc_zipcode+"</span></p></div><div class='search_result_description_detail_addr'><h5><strong>Miles</strong></h5><div class='addressDistance' latitude='"+mqResultToBeDisplayed[i].loc_latitude+"' longitude='"+mqResultToBeDisplayed[i].loc_longitude+"'><div class='miles-away'>"+mqResultToBeDisplayed[i].loc_miles_away+"</div></div></div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+mqResultToBeDisplayed[i].loc_phone+"'>"+mqResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+mqResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+mqResultToBeDisplayed[i].loc_detail_page+"?start="+searchString+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+mqResultToBeDisplayed[i].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+mqResultToBeDisplayed[i].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+mqResultToBeDisplayed[i].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+mqResultToBeDisplayed[i].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+mqResultToBeDisplayed[i].loc_phone+"'>"+mqResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+mqResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+mqResultToBeDisplayed[i].loc_detail_page+"?start="+searchString+"'>Get Directions</a></p></div></div></div></div>");
					}
				}
			}*/
		}else{
			if(arrayType == "singleDimension"){ 				
				for(var i = locationShowMoreCounter; i < numberOfResultToBeDisplayed; i++ ){
					if(aemResultToBeDisplayed[i].loc_result_type == "AEM")
						$('.search_result_description .search-results .locations_filter_container .location_aem_results').append("<div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+aemResultToBeDisplayed[i].loc_detail_page+"'>"+aemResultToBeDisplayed[i].loc_locationname+"</a></strong></h5><p>"+aemResultToBeDisplayed[i].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+aemResultToBeDisplayed[i].loc_city+", </span><span class='state'>"+aemResultToBeDisplayed[i].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+aemResultToBeDisplayed[i].loc_zipcode+"</span></p></div><div class='search_result_description_detail_addr'><div class='addressDistance' latitude='"+aemResultToBeDisplayed[i].loc_latitude+"' longitude='"+aemResultToBeDisplayed[i].loc_longitude+"'></div></div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+aemResultToBeDisplayed[i].loc_phone+"'>"+aemResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+aemResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+aemResultToBeDisplayed[i].loc_detail_page+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+aemResultToBeDisplayed[i].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+aemResultToBeDisplayed[i].loc_phone+"'>"+aemResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+aemResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+aemResultToBeDisplayed[i].loc_detail_page+"'>Get Directions</a></p></div></div></div></div>");
					else if(aemResultToBeDisplayed[i].loc_result_type == "MQ")
						$('.search_result_description .search-results .locations_filter_container .location_mq_results').append("<div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+aemResultToBeDisplayed[i].loc_detail_page+"'>"+aemResultToBeDisplayed[i].loc_locationname+"</a></strong></h5><p>"+aemResultToBeDisplayed[i].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+aemResultToBeDisplayed[i].loc_city+", </span><span class='state'>"+aemResultToBeDisplayed[i].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+aemResultToBeDisplayed[i].loc_zipcode+"</span></p></div><div class='search_result_description_detail_addr'><div class='addressDistance' latitude='"+aemResultToBeDisplayed[i].loc_latitude+"' longitude='"+aemResultToBeDisplayed[i].loc_longitude+"'></div></div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+aemResultToBeDisplayed[i].loc_phone+"'>"+aemResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+aemResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+aemResultToBeDisplayed[i].loc_detail_page+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+aemResultToBeDisplayed[i].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+aemResultToBeDisplayed[i].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+aemResultToBeDisplayed[i].loc_phone+"'>"+aemResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+aemResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+aemResultToBeDisplayed[i].loc_detail_page+"'>Get Directions</a></p></div></div></div></div>");
				}
			}/*else if(arrayType == "associative"){
				for(var i = locationShowMoreCounter; i < resultToBeDisplayed.length; i++ ){
					for(var j = 0; j < resultToBeDisplayed[i].length; j++ ){
						if(resultToBeDisplayed[i][j].loc_result_type == "AEM")
							$('.search_result_description .search-results .locations_filter_container .location_aem_results').append("<div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+mqResultToBeDisplayed[i][j].loc_detail_page+"'>"+mqResultToBeDisplayed[i][j].loc_locationname+"</a></strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+mqResultToBeDisplayed[i][j].loc_city+", </span><span class='state'>"+mqResultToBeDisplayed[i][j].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+mqResultToBeDisplayed[i][j].loc_zipcode+"</span></p></div><div class='search_result_description_detail_addr'><div class='addressDistance' latitude='"+mqResultToBeDisplayed[i][j].loc_latitude+"' longitude='"+mqResultToBeDisplayed[i][j].loc_longitude+"'></div></div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+mqResultToBeDisplayed[i][j].loc_phone+"'>"+mqResultToBeDisplayed[i][j].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+mqResultToBeDisplayed[i][j].loc_detail_page+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+mqResultToBeDisplayed[i][j].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+mqResultToBeDisplayed[i][j].loc_phone+"'>"+mqResultToBeDisplayed[i][j].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+mqResultToBeDisplayed[i][j].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+mqResultToBeDisplayed[i][j].loc_detail_page+"'>Get Directions</a></p></div></div></div></div>");
						else if(resultToBeDisplayed[i][j].loc_result_type == "MQ")
							$('.search_result_description .search-results .locations_filter_container .location_mq_results').append("<div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+mqResultToBeDisplayed[i].loc_detail_page+"'>"+mqResultToBeDisplayed[i].loc_locationname+"</a></strong></h5><p>"+mqResultToBeDisplayed[i].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+mqResultToBeDisplayed[i].loc_city+", </span><span class='state'>"+mqResultToBeDisplayed[i].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+mqResultToBeDisplayed[i].loc_zipcode+"</span></p></div><div class='search_result_description_detail_addr'><div class='addressDistance' latitude='"+mqResultToBeDisplayed[i].loc_latitude+"' longitude='"+mqResultToBeDisplayed[i].loc_longitude+"'></div></div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+mqResultToBeDisplayed[i].loc_phone+"'>"+mqResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+mqResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+mqResultToBeDisplayed[i].loc_detail_page+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+mqResultToBeDisplayed[i].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+mqResultToBeDisplayed[i].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+mqResultToBeDisplayed[i].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+mqResultToBeDisplayed[i].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+mqResultToBeDisplayed[i].loc_phone+"'>"+mqResultToBeDisplayed[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+mqResultToBeDisplayed[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+mqResultToBeDisplayed[i].loc_detail_page+"'>Get Directions</a></p></div></div></div></div>");
					}
				}
			}*/
		}
	}
    var endTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
    irrelaventDivsRemover();
    duplicateResultDivRemover();
    var mqNumberOfResults = $('.search_result_description .search-results .locations_filter_container .location_mq_results .search_result_description_details').length;
    var aemNumberOfResults = $('.search_result_description .search-results .locations_filter_container .location_aem_results .search_result_description_details').length;
    var resultLength = mqNumberOfResults + aemNumberOfResults;
	$('.location-filter-no-result-identifier').remove();
    if(resultLength != 0){
        if(startLat != undefined && startLng != undefined){
        }else if(startLat == undefined && startLng == undefined){
			getDirectionLinkFormaterForUserLocationNotAllowed();
        }
		resultCreatorShowLocationResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		if(activeTabLowecase=="locations") {
			$('.faqs_result_count').addClass("hide");
			$('.products_result_count').addClass("hide");
			$('.'+activeTabLowecase+'_result_count').removeClass('hide');
			$('.search_result_filters_nonProducts').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_desktop').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_mobile').removeClass('hide');
		}
        $('.search_result_description .search-results .locations_filter_container .location_mq_results').show();
        $('.search_result_description .search-results .locations_filter_container .location_aem_results').show();
        closedHoursCreator();
        if(window.innerWidth>=768) {
            locationSearchShowMore();
        }
    }
    else{
    	if(eventType == "newSearch" && selectboxLowerCase=="locations"){
			resultCreatorShowNoResult();
			resultCreatorShowLocationResults();	
			var activeTab = $("ul.search_select a.active").attr("index");
			var activeTabNoSpace = activeTab.replace(" ", "_");
			var activeTabLowecase = activeTabNoSpace.toLowerCase();
			if(activeTabLowecase="locations") {
				$('.faqs_result_count').addClass("hide");
				$('.products_result_count').addClass("hide");
				$('.'+activeTabLowecase+'_no_result').removeClass('hide');
				$('.'+activeTabLowecase+'_result_count').removeClass('hide');
			}
    	}
    	else if(eventType == "refineResult"){
    		$('.search_result_description .search-results .locations_filter_container').append("<div class='filter-no-result zero_result_found'> <div class='zero_result_text'>"+noResultTextForLocationFilter+"</div></div>");
			$('.search_result_description .search-results .locations_filter_container .filter-no-result').append('<input class="location-filter-no-result-identifier" type="hidden" value="NoResultsDuringFilter">');
			locationSearchShowMore();
		}
    }
	$('.location-search .search_result_count').removeClass("hide");
        var mqNumberOfResults = $('.search_result_description .search-results .locations_filter_container .location_mq_results .search_result_description_details').length;
        var aemNumberOfResults = $('.search_result_description .search-results .locations_filter_container .location_aem_results .search_result_description_details').length;
		var numberOfResults = resultToBeDisplayed.length;
        LocationResultCount = numberOfResults;
        $('.search_result_count .locations_result_count').text(numberOfResults);
		if(eventType == "newSearch" )
			locationSearchHelper();
    $('#loadingMask').fadeOut(100);
	var responseEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
}

function resultCreatorPeople(resultToBeDisplayedPeople,arrayType,eventType, numberOfResultToBeDisplayedPeople,peopleShowMoreCounter){
	var mqResultToBeDisplayed = [];
	var aemResultToBeDisplayed = [];
	var responseStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
	if(resultToBeDisplayedPeople.length < numberOfResultToBeDisplayedPeople)
		numberOfResultToBeDisplayedPeople = resultToBeDisplayedPeople.length;
	for(var i = 0; i < resultToBeDisplayedPeople.length; i++ ){
		if(resultToBeDisplayedPeople[i].people_result_type == "AEM"){
			mqResultToBeDisplayed.push(resultToBeDisplayedPeople[i]);
		} else if(resultToBeDisplayedPeople[i].people_result_type == "MQ"){
			aemResultToBeDisplayed.push(resultToBeDisplayedPeople[i]);
		}
	}		
	if(resultToBeDisplayedPeople.length > 0){
		if(startLat != undefined && startLng != undefined){
			mqResultToBeDisplayed.sort(function (a, b) {
				if (a.people_miles_away < b.people_miles_away) {
					return -1;
				}
				else if (a.people_miles_away > b.people_miles_away) {
					return 1;
				}
				return 0;
			});
			aemResultToBeDisplayed.sort(function (a, b) {
				if (a.people_miles_away < b.people_miles_away) {
					return -1;
				}
				else if (a.people_miles_away > b.people_miles_away) {
					return 1;
				}
				return 0;
			});
		}else{
			mqResultToBeDisplayed.sort(function (a, b) {
				if (a.advisor_firstname < b.advisor_firstname) {
					return -1;
				}
				else if (a.advisor_firstname > b.advisor_firstname) {
					return 1;
				}
				return 0;
			});
			aemResultToBeDisplayed.sort(function (a, b) {
				if (a.advisor_firstname < b.advisor_firstname) {
					return -1;
				}
				else if (a.advisor_firstname > b.advisor_firstname) {
					return 1;
				}
				return 0;
			});
		}
		for(var i = 0; i < mqResultToBeDisplayed.length; i++ ){
			aemResultToBeDisplayed.push(mqResultToBeDisplayed[i]);
		}
		if(startLat != undefined && startLng != undefined){
			if(arrayType == "singleDimension"){
				for(var i = peopleShowMoreCounter; i < numberOfResultToBeDisplayedPeople; i++ ){
					if(aemResultToBeDisplayed[i].people_result_type == "AEM")
						resultUpdate(aemResultToBeDisplayed[i].advisor_profilePage, "AEM");
					else if(aemResultToBeDisplayed[i].people_result_type == "MQ")
						resultUpdate(aemResultToBeDisplayed[i].advisor_profilePage, "MQ");		
				}
			}else if(arrayType == "associative"){
				for(var i = peopleShowMoreCounter; i < resultToBeDisplayedPeople.length; i++ ){
					for(var j = 0; j < resultToBeDisplayedPeople[i].length; j++ ){
						if(resultToBeDisplayedPeople[i][j].people_result_type == "AEM")
							resultUpdate(resultToBeDisplayedPeople[i][j].advisor_profilePage, "AEM");
						else if(resultToBeDisplayedPeople[i][j].people_result_type == "MQ")
							resultUpdate(resultToBeDisplayedPeople[i][j].advisor_profilePage, "MQ");
					}
				}
			}
		}else{
			if(arrayType == "singleDimension"){ 				
				for(var i = peopleShowMoreCounter; i < numberOfResultToBeDisplayedPeople; i++ ){
					if(aemResultToBeDisplayed[i].people_result_type == "AEM")
						resultUpdate(aemResultToBeDisplayed[i].advisor_profilePage, "AEM");
					else if(aemResultToBeDisplayed[i].people_result_type == "MQ")
						resultUpdate(aemResultToBeDisplayed[i].advisor_profilePage, "MQ"); 			
				}
			}else if(arrayType == "associative"){
				for(var i = peopleShowMoreCounter; i < resultToBeDisplayedPeople.length; i++ ){
					for(var j = 0; j < resultToBeDisplayedPeople[i].length; j++ ){
						if(resultToBeDisplayedPeople[i][j].people_result_type == "AEM")
							resultUpdate(resultToBeDisplayedPeople[i][j].advisor_profilePage, "AEM");
						else if(resultToBeDisplayedPeople[i][j].people_result_type == "MQ")
							resultUpdate(resultToBeDisplayedPeople[i][j].advisor_profilePage, "MQ");
					}
				}
			}
		}
	}
	
	
    var endTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
    irrelaventDivsRemover();
    var mqNumberOfResults = $('.search_result_description .search-results .people_filter_container .people_mq_results .search_result_description_details').length;
    var aemNumberOfResults = $('.search_result_description .search-results .people_filter_container .people_aem_results .search_result_description_details').length;
    var resultLength = mqNumberOfResults + aemNumberOfResults;
	$('.people-filter-no-result-identifier').remove();
    if(resultLength != 0){
        if(startLat != undefined && startLng != undefined){
        }else if(startLat == undefined && startLng == undefined){
			getDirectionLinkFormaterForUserLocationNotAllowed();
        }
		resultCreatorShowLocationResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		if(activeTabLowecase=="locations") {
			$('.faqs_result_count').addClass("hide");
			$('.products_result_count').addClass("hide");
			$('.'+activeTabLowecase+'_result_count').removeClass('hide');
			$('.search_result_filters_nonProducts').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_desktop').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_mobile').removeClass('hide');
		}
        $('.search_result_description .search-results .people_filter_container .people_mq_results').show();
        $('.search_result_description .search-results .people_filter_container .people_aem_results').show();
        closedHoursCreator();
        if(window.innerWidth>=768) {
            peopleSearchShowMore();
        }
    }
    else{
    	if(eventType == "newSearch" && selectboxLowerCase=="locations"){
			resultCreatorShowNoResult();
			resultCreatorShowLocationResults();	
			var activeTab = $("ul.search_select a.active").attr("index");
			var activeTabNoSpace = activeTab.replace(" ", "_");
			var activeTabLowecase = activeTabNoSpace.toLowerCase();
			if(activeTabLowecase="locations") {
				$('.faqs_result_count').addClass("hide");
				$('.products_result_count').addClass("hide");
				$('.'+activeTabLowecase+'_no_result').removeClass('hide');
				$('.'+activeTabLowecase+'_result_count').removeClass('hide');
			}
    	}
    	else if(eventType == "refineResult") {
    		$('.search_result_description .search-results .people_filter_container').append("<div class='filter-no-result zero_result_found'> <div class='zero_result_text'>"+noResultTextForPeopleFilter+"</div></div>");
			$('.search_result_description .search-results .people_filter_container .filter-no-result').append('<input class="people-filter-no-result-identifier" type="hidden" value="NoResultsDuringFilter">');
			peopleSearchShowMore();
		}
    }
	$('.location-search .search_result_count').removeClass("hide");
        var mqNumberOfResults = $('.search_result_description .search-results .people_filter_container .people_mq_results .search_result_description_details').length;
        var aemNumberOfResults = $('.search_result_description .search-results .people_filter_container .people_aem_results .search_result_description_details').length;
		var numberOfResults = resultToBeDisplayedPeople.length;
        PeopleResultCount = numberOfResults;
        $('.search_result_count .people_result_count').text(numberOfResults);
		if(eventType == "newSearch" )
			peopleSearchHelper();
    $('#loadingMask').fadeOut(100);
	var responseEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
}

function resultUpdate(filePath, resultType) {	
	filtered_jsonObject = $.grep(MqAndAEMResultsPeople, function(v) {
		return v.advisor_profilePage === filePath;
	});
	if(filtered_jsonObject !="" && filtered_jsonObject != undefined){
		var $clonedResult = $("#hiddenTemplate").clone();
		if (startLat != undefined && startLng != undefined) {
			$clonedResult.find(".profilePage").attr("href",filtered_jsonObject[0].advisor_profilePage+"?start="+searchString);
		}else{
			$clonedResult.find(".profilePage").attr("href",filtered_jsonObject[0].advisor_profilePage);
		}
		if(filtered_jsonObject[0].advisor_pictureFilePath != undefined && filtered_jsonObject[0].advisor_pictureFilePath != "")
			$clonedResult.find(".profileImage").attr("src",filtered_jsonObject[0].advisor_pictureFilePath);		
		else
			$clonedResult.find(".search_result_description_detail_img").remove();	
		$clonedResult.find(".profilePage").attr("title",filtered_jsonObject[0].advisor_firstname+' '+filtered_jsonObject[0].advisor_lastname+', '+filtered_jsonObject[0].advisor_title1);
		var advDesignationSpl = '';
		var advDesignationCodes = filtered_jsonObject[0].advisor_designationcodes;
		if(advDesignationCodes != '' && advDesignationCodes != undefined && advDesignationCodes != null)
			advDesignationSpl = ', '+advDesignationCodes;
		$clonedResult.find(".advisorName").html(filtered_jsonObject[0].advisor_firstname+' '+filtered_jsonObject[0].advisor_lastname+advDesignationSpl+', '+filtered_jsonObject[0].advisor_title1);
		$clonedResult.find(".advisorTitle").html(filtered_jsonObject[0].advisor_title2);

		$clonedResult.find(".telNo").attr("href",'tel:'+filtered_jsonObject[0].advisor_phone).html(filtered_jsonObject[0].advisor_phone);
		$clonedResult.find(".mailId").attr("href",'mailto:'+filtered_jsonObject[0].advisor_emailaddress).html(filtered_jsonObject[0].advisor_emailaddress);

		$clonedResult.find(".contactFormId").attr("href",'/dotcom/external?clickedUrl='+filtered_jsonObject[0].advisor_onlineContactFormUrl);

		filtered_jsonObject[0].advisor_addressListArray.addressListArray.sort(function (a, b) {
			if (a.people_eachloc_miles_away < b.people_eachloc_miles_away) {
				return -1;
			}
			else if (a.people_eachloc_miles_away > b.people_eachloc_miles_away) {
				return 1;
			}
			return 0;
		});
		
		$.each(filtered_jsonObject[0].advisor_addressListArray.addressListArray, function(i, data){
			var addresshtml='<div class="clearfix"><div class="location_show_hide_addr">';
			addresshtml+='<p>'+data.adv_address+'</p>';
			addresshtml+='<p><span class="adv-city">'+data.adv_city+'</span>, '+data.adv_state+'\n'+data.adv_zipcode+'</p>';
			addresshtml+='<div class="addressDistance" latitude="'+data.adv_latitude+'" longitude="'+data.adv_longitude+'" />';
			if(data.people_eachloc_miles_away!=undefined) {
				addresshtml+=data.people_eachloc_miles_away+' Miles</div>';
			} else {
				addresshtml+='</div>';
			}
			
			var destination="";
			if(data.adv_address != "" && data.adv_address != undefined)
				destination = data.adv_address;
			if(data.adv_city != "" && data.adv_city != undefined){						
				if(destination != "")
					destination = destination +" "+ data.adv_city;
				else
					destination = data.adv_city;
			}
			if(data.adv_state != "" && data.adv_state != undefined){						
				if(destination != "")
					destination = destination +", "+ data.adv_state;
				else
					destination = data.adv_state;
			}
			if(data.adv_zipcode != "" && data.adv_zipcode != undefined){						
				if(destination != "")
					destination = destination +" "+ data.adv_zipcode;
				else
					destination = data.adv_zipcode;
			}
			
			if (startLat != undefined && startLng != undefined) {
				/*addresshtml+='<div class="loction_show_hide_direction clearfix"><div class="loction_show_hide_direction_icon"><img src="/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png"></div><div class="loction_show_hide_direction_details"><p><a target="_blank" href="'+filtered_jsonObject[0].advisor_profilePage;*/
								
				if ($(window).width() < 767) {  
					if(/Android/i.test(navigator.userAgent)){
						addresshtml+='<div class="loction_show_hide_direction clearfix"><div class="loction_show_hide_direction_icon"><img src="/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png"></div><div class="loction_show_hide_direction_details"><p><a target="_blank" class="get-direction-link-mobile" data-mobile-link="https://www.google.com/maps/dir/?api=1&origin='+searchString+'&destination='+destination+'&mode=d"  href="https://www.google.com/maps/dir/?api=1';
						addresshtml+='&origin='+searchString+'&destination='+destination+'&mode=d">';
						addresshtml+='Get Directions</a>';
						addresshtml+='</p></div></div></div>';
					}else if(/iPhone/i.test(navigator.userAgent)){
						addresshtml+='<div class="loction_show_hide_direction clearfix"><div class="loction_show_hide_direction_icon"><img src="/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png"></div><div class="loction_show_hide_direction_details"><p><a target="_blank" class="get-direction-link-mobile" data-mobile-link="http://maps.apple.com?saddr='+searchString+'&daddr='+destination+'&mode=d" href="http://maps.apple.com';
						addresshtml+='?saddr='+searchString+'&daddr='+destination+'&directionsmode=transit">';
						addresshtml+='Get Directions</a>';
						addresshtml+='</p></div></div></div>';
					}else{
						var getDirectionUrl = filtered_jsonObject[0].advisor_profilePage+'?start='+searchString+'&daddr='+destination;
					}
				} else {
					addresshtml+='<div class="loction_show_hide_direction clearfix"><div class="loction_show_hide_direction_icon"><img src="/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png"></div><div class="loction_show_hide_direction_details"><p><a target="_blank" href="'+filtered_jsonObject[0].advisor_profilePage;
					addresshtml+='?start='+searchString+'&destination='+destination+'">';
					addresshtml+='Get Directions</a>';
					addresshtml+='</p></div></div></div>';
				}	
			}
			$clonedResult.find(".locationResults").append(addresshtml);
		});
					
		$clonedResult.removeAttr("id").addClass("search_result_description_details clearfix").show();
		if(filtered_jsonObject[0].people_result_type == "AEM")
			$("#searchResult .people_aem_results").append($clonedResult);
		else if(filtered_jsonObject[0].people_result_type == "MQ")
			$("#searchResult .people_mq_results").append($clonedResult);

	}
	peopleAddressToggle();

}

function peopleAddressToggle() {
	$('.search_result_description_details').each(function() {
		var count = 0;
		$(this).find('.peopleSearch_togSec .location_show_hide > .clearfix').each(function() {
		count++;
		});
		if (count > 1) {
		$(this).find('.peopleSearch_togSec > a').show();
		} else if (count <= 1) {
		$(this).find('.peopleSearch_togSec > a').hide();
		$(this).find('.peopleSearch_togSec > .location_show_hide').show();
		}
	});
}
function allResultAddressToggle() {
	$('.search_result_description_details').each(function() {
		var count = 0;
		$(this).find('.location_tog .location_show_hide > .clearfix').each(function() {
		count++;
		});
		if (count > 1) {
		$(this).find('.location_tog > a').show();
		} else if (count <= 1) {
		$(this).find('.location_tog > a').hide();
		$(this).find('.location_tog > .location_show_hide').show();
		}
	});
}

function updateQueryStringParameter(uri, key, value) {
  var re = new RegExp("([?&])" + key + "=.*?(&|#|$)", "i");
  if( value === undefined ) {
  	if (uri.match(re)) {
		return uri.replace(re, '$1$2');
	} else {
		return uri;
	}
  } else {
  	if (uri.match(re)) {
  		return uri.replace(re, '$1' + key + "=" + value + '$2');
	} else {
    var hash =  '';
    if( uri.indexOf('#') !== -1 ){
        hash = uri.replace(/.*#/, '#');
        uri = uri.replace(/#.*/, '');
    }
    var separator = uri.indexOf('?') !== -1 ? "&" : "?";    
    return uri + separator + key + "=" + value + hash;
  }
  }  
}
function removeQueryStringParameter(key, url) {
    if (!url) url = window.location.href;
    var hashParts = url.split('#');
    var regex = new RegExp("([?&])" + key + "=.*?(&|#|$)", "i");
    if (hashParts[0].match(regex)) {
        //REMOVE KEY AND VALUE
        url = hashParts[0].replace(regex, '$1');
        //REMOVE TRAILING ? OR &
        url = url.replace(/([?&])$/, '');
        //ADD HASH
        if (typeof hashParts[1] !== 'undefined' && hashParts[1] !== null)
            url += '#' + hashParts[1];
    }
    return url;
}
$('.location-search .suntrust-orange-button.search-button').on('click', function(e){
	locationShowMoreCounter = 0;
	allCategoryShowMoreCounter = 0;	
	peopleShowMoreCounter = 0;	
	resourcesShowMoreCounter = 0;
	numberOfResultToBeDisplayed = parseInt($('.location_result_show_more_results a').attr("data-search-visible-items-limit"));
	numberOfResultToBeDisplayedPeople = parseInt($('.people_result_show_more_results a').attr("data-search-visible-items-limit"));
	allResultsNumberOfResultToBeDisplayed = parseInt($('.all_result_show_more_results a').attr('data-search-visible-items-limit'));
	NumberOfResultToBeDisplayedResources = parseInt($('.resources_show_more_results a').attr('data-search-visible-items-limit'));
	locationsTabFirstTimeClickedFlag = true;
	peopleTabFirstTimeClickedFlag = true;
	faqsTabFirstTimeClickedFlag = true;
	productsTabFirstTimeClickedFlag=true;
	allTabFirstTimeClickedFlag=true;
	resourcesTabFirstTimeClickedFlag=true;
	searchString = $('.search_text_box input#search-input').val().trim();
	if(/[a-zA-Z0-9]/.test(searchString)){  
		$('.search_text_error_message').addClass('hide');
	}
	else {
		$('.search_text_error_message').removeClass('hide');
	}
	searchString = encodeURIComponent(searchString).replace(/%20/g, "+");
	var validationPassedFlag = $('.search_text_error_message').is(":visible");
    e.stopImmediatePropagation();
	//show Tab navigation bar when search button clicked
	if(searchString != "" && !validationPassedFlag){
		$('.locations_filter_desktop .search_detail_radius_select select option:eq(1)').attr("selected",true);
		$('.locations_filter_desktop .search_detail_radius_select span').text("10 Miles");
		$('.locations_filter_mobile .mobile-radius-filter .search_detail_radius_select select option:eq(1)').attr("selected",true);
		$('.locations_filter_mobile .mobile-radius-filter .search_detail_radius_select span').text("10 Miles");
		$('.people_filter_desktop .search_detail_radius_select select option:eq(1)').attr("selected",true);
		$('.people_filter_desktop .search_detail_radius_select span').text("10 Miles");
		$('.people_filter_mobile .mobile-radius-filter .search_detail_radius_select select option:eq(1)').attr("selected",true);
		$('.people_filter_mobile .mobile-radius-filter .search_detail_radius_select span').text("10 Miles");		
		var selectbox = $("ul.search_select a.active").attr("index"); 
				if(selectbox != undefined)
					var selectboxLowerCase = selectbox.toLowerCase();
		if(selectboxLowerCase == "locations" && locationsTabFirstTimeClickedFlag){
			var oldLocationName = getParameterByName("searchTerm");
			if(oldLocationName != null && oldLocationName != undefined && oldLocationName != ""){
				var newUrl = window.location.href;
				if(newUrl.indexOf("locationRadius")>=0){
					newUrl = removeQueryStringParameter("locationRadius",newUrl);
				}
				if(newUrl.indexOf("locationServices")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("productTag")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("peopleRadius")>=0){
					newUrl = removeQueryStringParameter("peopleRadius",newUrl);
				}
				if(newUrl.indexOf("specialty")>=0){
					newUrl = removeQueryStringParameter("specialty",newUrl);
				}
				newUrl = newUrl.replace("searchTerm="+encodeURIComponent(oldLocationName).replace(/%20/g, "+"), "searchTerm="+searchString);
				if(window.location.href == newUrl)
					window.location.reload();
				else
					window.location.href = newUrl;		
			}else{
				window.location.hash = selectboxLowerCase;
				window.location.href = updateQueryStringParameter(window.location.href,"searchTerm",searchString.replace(/%20/g, "+"));				
			}
		}
		if(selectboxLowerCase == "resources" && resourcesTabFirstTimeClickedFlag){
			var oldLocationName = getParameterByName("searchTerm");
			if(oldLocationName != null && oldLocationName != undefined && oldLocationName != ""){
				var newUrl = window.location.href;
				if(newUrl.indexOf("locationRadius")>=0){
					newUrl = removeQueryStringParameter("locationRadius",newUrl);
				}
				if(newUrl.indexOf("locationServices")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("productTag")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("peopleRadius")>=0){
					newUrl = removeQueryStringParameter("peopleRadius",newUrl);
				}
				if(newUrl.indexOf("specialty")>=0){
					newUrl = removeQueryStringParameter("specialty",newUrl);
				}
				newUrl = newUrl.replace("searchTerm="+encodeURIComponent(oldLocationName).replace(/%20/g, "+"), "searchTerm="+searchString);
				if(window.location.href == newUrl)
					window.location.reload();
				else
					window.location.href = newUrl;		
			}else{
				window.location.hash = selectboxLowerCase;
				window.location.href = updateQueryStringParameter(window.location.href,"searchTerm",searchString.replace(/%20/g, "+"));				
			}
		}
		if(selectboxLowerCase == "all_results" && allTabFirstTimeClickedFlag){
			var oldLocationName = getParameterByName("searchTerm");
			if(oldLocationName != null && oldLocationName != undefined && oldLocationName != ""){
				var newUrl = window.location.href;
				if(newUrl.indexOf("locationRadius")>=0){
					newUrl = removeQueryStringParameter("locationRadius",newUrl);
				}
				if(newUrl.indexOf("locationServices")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("productTag")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("peopleRadius")>=0){
					newUrl = removeQueryStringParameter("peopleRadius",newUrl);
				}
				if(newUrl.indexOf("specialty")>=0){
					newUrl = removeQueryStringParameter("specialty",newUrl);
				}
				newUrl = newUrl.replace("searchTerm="+encodeURIComponent(oldLocationName).replace(/%20/g, "+"), "searchTerm="+searchString);
				if(window.location.href == newUrl)
					window.location.reload();
				else
					window.location.href = newUrl;
			}else{
				window.location.hash = selectboxLowerCase;
				window.location.href = updateQueryStringParameter(window.location.href,"searchTerm",searchString.replace(/%20/g, "+"));
			}
		}
		if(selectboxLowerCase == "people" && peopleTabFirstTimeClickedFlag){
			var oldPeopleName = getParameterByName("searchTerm");
			if(oldPeopleName != null && oldPeopleName != undefined && oldPeopleName != ""){
				var newUrl = window.location.href;
				if(newUrl.indexOf("peopleRadius")>=0){
					newUrl = removeQueryStringParameter("peopleRadius",newUrl);
				}
				if(newUrl.indexOf("specialty")>=0){
					newUrl = removeQueryStringParameter("specialty",newUrl);
				}
				if(newUrl.indexOf("productTag")>=0){
					newUrl = removeQueryStringParameter("specialty",newUrl);
				}
				if(newUrl.indexOf("locationRadius")>=0){
					newUrl = removeQueryStringParameter("locationRadius",newUrl);
				}
				if(newUrl.indexOf("locationServices")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				newUrl = newUrl.replace("searchTerm="+encodeURIComponent(oldPeopleName).replace(/%20/g, "+"), "searchTerm="+searchString);
				if(window.location.href == newUrl)
					window.location.reload();
				else
					window.location.href = newUrl;
			}else{
				window.location.hash = selectboxLowerCase;
				window.location.href = updateQueryStringParameter(window.location.href,"searchTerm",searchString.replace(/%20/g, "+"));
			}
		}
		if(selectboxLowerCase == "faqs" && faqsTabFirstTimeClickedFlag){
			var oldProductName = getParameterByName("searchTerm");
			if(oldProductName != null && oldProductName != undefined && oldProductName != ""){
				var newUrl = window.location.href;
				if(newUrl.indexOf("locationRadius")>=0){
					newUrl = removeQueryStringParameter("locationRadius",newUrl);
				}
				if(newUrl.indexOf("locationServices")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("productTag")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("peopleRadius")>=0){
					newUrl = removeQueryStringParameter("peopleRadius",newUrl);
				}
				if(newUrl.indexOf("specialty")>=0){
					newUrl = removeQueryStringParameter("specialty",newUrl);
				}
				newUrl = newUrl.replace("searchTerm="+encodeURIComponent(oldProductName).replace(/%20/g, "+"), "searchTerm="+searchString);
				if(window.location.href == newUrl)
					window.location.reload();
				else
					window.location.href = newUrl;
			}else{
				window.location.hash = selectboxLowerCase;
				window.location.href = updateQueryStringParameter(window.location.href,"searchTerm",searchString.replace(/%20/g, "+"));
			}
		}
		if(selectboxLowerCase == "products" && productsTabFirstTimeClickedFlag){
			var oldProductName = getParameterByName("searchTerm");
			if(oldProductName != null && oldProductName != undefined && oldProductName != ""){
				var newUrl = window.location.href;
				if(newUrl.indexOf("locationRadius")>=0){
					newUrl = removeQueryStringParameter("locationRadius",newUrl);
				}
				if(newUrl.indexOf("locationServices")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("productTag")>=0){
					newUrl = removeQueryStringParameter("locationServices",newUrl);
				}
				if(newUrl.indexOf("peopleRadius")>=0){
					newUrl = removeQueryStringParameter("peopleRadius",newUrl);
				}
				if(newUrl.indexOf("specialty")>=0){
					newUrl = removeQueryStringParameter("specialty",newUrl);
				}
				newUrl = newUrl.replace("searchTerm="+encodeURIComponent(oldProductName).replace(/%20/g, "+"), "searchTerm="+searchString);
				if(window.location.href == newUrl)
					window.location.reload();
				else
					window.location.href = newUrl;
			}else{
				window.location.hash = selectboxLowerCase;
				window.location.href = updateQueryStringParameter(window.location.href,"searchTerm",searchString.replace(/%20/g, "+"));
			}
		}
		
		
	}else{
		$('.search_result_categories').addClass("hide");
		$('.faqs_filter_container').addClass("hide");
		$('.locations_filter_container').addClass("hide");
		$('.resources_filter_container').addClass("hide");
		$('.products_filter_container').addClass("hide");
		$('.all_results_filter_container').addClass("hide");
		$('.people_filter_container').addClass("hide");
		$('.search_result_filters.result_filters_other .search_result_filters_faqs').addClass("hide");
		$('.search_result_filters.result_filters_other .search_result_filters_products').addClass("hide");
		$('.search_result_filters.result_filters_other .search_result_filters_all_results').addClass("hide");
		$('.locations_no_result.zero_result_found').addClass("hide");
		$('.resources_no_result.zero_result_found').addClass("hide");
		$('.faqs_no_result.zero_result_found').addClass("hide");
		$('.products_no_result.zero_result_found').addClass("hide");
		$('.all_results_no_result.zero_result_found').addClass("hide");
		$('.people_no_result.zero_result_found').addClass("hide");
		$('.search_result_filters_nonProducts.search_result_filters').addClass("hide");
		$('.search_result_count.text-center').addClass("hide");
		$('.location_result_show_more_results').addClass("hide");
		$('.resources_result_show_more_results').addClass("hide");
		$('.people_result_show_more_results').addClass("hide");
		$('.search_result_show_more_results').addClass("hide");
		$('.faq_result_show_more_results').addClass("hide");
		$('.all_result_show_more_results').addClass("hide");
		
	}
    return false;
});


	$(document).on('keypress','#search-input',function(e) {
	    if (e.keyCode === 13) {
    		$(this).blur();
    		$('.location-search .suntrust-orange-button.search-button').trigger('click');
	    }
	});
	
	
	function selectCategoriesDropdownInMobile() {
		if(searchIn != ""){
			var SelectedTab = $("select.search_select option[value='"+searchIn.toLowerCase()+"']").text();
			$("select.search_select option[value='"+searchIn.toLowerCase()+"']").attr("selected",true);
			$("select.search_select option[value="+searchIn.toLowerCase()+"]").parent().prev().html(SelectedTab);
		}
	}
	setTimeout(selectCategoriesDropdownInMobile, 300);

	var iOS = !!navigator.platform && /iPhone/.test(navigator.platform);
	if(iOS){
		$("#search_mobile_tab_navigation option").attr("selected",true);
	}

	
	
	function removeDuplicate(arr, prop) {
		var new_arr = [];
		var lookup = {};
		for (var i in arr) {
			if(arr[i][prop] !=undefined)
			lookup[arr[i][prop]] = arr[i];
		}
		for (i in lookup) {
			new_arr.push(lookup[i]);
		}
		return new_arr;
	}
	
    
    function refineFilter(selectedRadius,eventType){
		var startTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
		$('#loadingMask').show();
		numberOfResultToBeDisplayed = parseInt($('.location_result_show_more_results a').attr("data-search-visible-items-limit"));
		locationShowMoreCounter = 0;
        var valueFromCityCheckBox=[];
        var valueFromStateCheckBox=[];
        var valueFromServicesCheckBox=[];
        var refinedResult=[];
        var refinedStateResult=[];
        var refinedCityResult=[];
        var refinedServicesResult=[];
        var refinedDuplicateServicesResult=[];
        var refinedUniqueUrlList=[];
        var checkedCityFlag = false;
        var checkedStateFlag = false;
        var checkedServiceFlag = false;
		var unCheckedCityFlag = false;
        var unCheckedStateFlag = false;
        var unCheckedServiceFlag = false;
		var stateFlag = false;
		var cityFlag = false;
		var serviceFlag = false
		var resultTypeSingleDimentionCount = 0;
        var radius = selectedRadius;
		var refinedResultSingleDimension=[];
        $('.search_result_description .search-results .locations_filter_container .filter-no-result').remove();
        $('.search_result_description .search-results .locations_filter_container .location_mq_results').children().remove();
        $('.search_result_description .search-results .locations_filter_container .location_aem_results').children().remove();
    	$('.location-search .search_result_count').addClass("hide");
        if(window.innerWidth<768) {
            $('.locations_filter_mobile .mobile-city input').each(function(e){
                if($(this).is(':checked')){
                    checkedCityFlag = true;
                    valueFromCityCheckBox.push($(this).val());
                }
				if(!($(this).is(':checked'))){
                    unCheckedCityFlag = true;
                }
            });
            $('.locations_filter_mobile .mobile-state input').each(function(e){
                if($(this).is(':checked')){
                    checkedStateFlag = true;
                    valueFromStateCheckBox.push($(this).val());
                }
				if(!($(this).is(':checked'))){
                    unCheckedStateFlag = true;
                }
            });
            $('.locations_filter_mobile .mobile-services input').each(function(e){
                if($(this).is(':checked')){
                    checkedServiceFlag = true;
                    valueFromServicesCheckBox.push($(this).val());
                }
				if(!($(this).is(':checked'))){
                    unCheckedServiceFlag = true;
                }
            });
        }
		if(window.innerWidth>=768) {
            $('.locations_filter_desktop .search_detail_city input').each(function(e){
                if($(this).is(':checked')){
                    checkedCityFlag = true;
                    valueFromCityCheckBox.push($(this).val());
                }
				if(!($(this).is(':checked'))){
                    unCheckedCityFlag = true;
                }
            });
            $('.locations_filter_desktop .search_detail_state input').each(function(e){
                if($(this).is(':checked')){
                    checkedStateFlag = true;
                    valueFromStateCheckBox.push($(this).val());
                }
				if(!($(this).is(':checked'))){
                    unCheckedStateFlag = true;
                }
            });
            $('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){
                if($(this).is(':checked')){
                    checkedServiceFlag = true;
                    valueFromServicesCheckBox.push($(this).val());
                }
				if(!($(this).is(':checked'))){
                    unCheckedServiceFlag = true;
                }
            });
			
			setTimeout(function() {
    						passSearchValuesToAnalyticEngine("desktopView");
    						}, 1000);
        }
		
		valueFromServicesCheckBoxGlobal = valueFromServicesCheckBox;
		
		
		
        if(radius != "Select"){
            /* //services only checked
            if(checkedServiceFlag == true && checkedStateFlag == false && checkedCityFlag == false){
                for (var j=0;j<MqAndAEMResults.length;j++){
                      for (var k=0;k<valueFromServicesCheckBox.length;k++){
                          refinedDuplicateServicesResult.push($(MqAndAEMResults[j]).filter(function (i,n){if(n.loc_services_tag.toString().indexOf(valueFromServicesCheckBox[k])>=0) return true;else return false;})); 
                      }
                }
                resultCreator(refinedDuplicateServicesResult,"associative",eventType);
            }
            //Cities only checked
            if(checkedCityFlag == true && checkedStateFlag == false && checkedServiceFlag == false){  
                for (var j=0;j<MqAndAEMResults.length;j++){
                    for (var k=0;k<valueFromCityCheckBox.length;k++){
                        refinedCityResult.push($(MqAndAEMResults[j]).filter(function (i,n){return n.loc_city === valueFromCityCheckBox[k]})); 
                    }
                }
                resultCreator(refinedCityResult,"associative",eventType);
            }
            //States only checked
            if(checkedCityFlag == false && checkedStateFlag == true && checkedServiceFlag == false){
                for (var j=0;j<MqAndAEMResults.length;j++){
                        for (var k=0;k<valueFromStateCheckBox.length;k++){
                            refinedStateResult.push($(MqAndAEMResults[j]).filter(function (i,n){return n.loc_state_tag === valueFromStateCheckBox[k]})); 
                        }
                }
                resultCreator(refinedStateResult,"associative",eventType);
            }
            //Cities & States only checked	
            if(checkedCityFlag == true && checkedStateFlag == true && checkedServiceFlag == false){
                for (var j=0;j<MqAndAEMResults.length;j++){
                        for (var k=0;k<valueFromStateCheckBox.length;k++){
                            refinedStateResult.push($(MqAndAEMResults[j]).filter(function (i,n){return n.loc_state_tag === valueFromStateCheckBox[k]})); 
                        }
                }
                for (var j=0;j<refinedStateResult.length;j++){
                    for (var p=0;p<refinedStateResult[j].length;p++){
                        for (var k=0;k<valueFromCityCheckBox.length;k++){
                            refinedResult.push($(refinedStateResult[j][p]).filter(function (i,n){return n.loc_city === valueFromCityCheckBox[k]}));
                        }
                    }
                }
                resultCreator(refinedResult,"associative",eventType);
            }
            //States & Services only checked
            if(checkedCityFlag == false && checkedStateFlag == true && checkedServiceFlag == true){
                for (var j=0;j<MqAndAEMResults.length;j++){
                        for (var k=0;k<valueFromStateCheckBox.length;k++){
                            refinedStateResult.push($(MqAndAEMResults[j]).filter(function (i,n){return n.loc_state_tag === valueFromStateCheckBox[k]})); 
                        }
                }
                for (var j=0;j<refinedStateResult.length;j++){
                    for (var p=0;p<refinedStateResult[j].length;p++){
                        for (var k=0;k<valueFromServicesCheckBox.length;k++){
                            refinedResult.push($(refinedStateResult[j][p]).filter(function (i,n){if(n.loc_services_tag.toString().indexOf(valueFromServicesCheckBox[k])>=0) return true;else return false;}));           
                        }
                    }
                }
                resultCreator(refinedResult,"associative",eventType);            
            }
            //Cities & Services only checked
            if(checkedCityFlag == true && checkedStateFlag == false && checkedServiceFlag == true){
                for (var j=0;j<MqAndAEMResults.length;j++){
                        for (var k=0;k<valueFromCityCheckBox.length;k++){
                            refinedCityResult.push($(MqAndAEMResults[j]).filter(function (i,n){return n.loc_city === valueFromCityCheckBox[k]})); 
                        }
                }
                for (var j=0;j<refinedCityResult.length;j++){
                    for (var p=0;p<refinedCityResult[j].length;p++){
                        for (var k=0;k<valueFromServicesCheckBox.length;k++){
                            refinedResult.push($(refinedCityResult[j][p]).filter(function (i,n){if(n.loc_services_tag.toString().indexOf(valueFromServicesCheckBox[k])>=0) return true;else return false;}));           
                        }
                    }
                }
                resultCreator(refinedResult,"associative",eventType);
            } */
            //None checked
            if(checkedStateFlag == false && checkedCityFlag == false && checkedServiceFlag == false){
				filteredLocationArray = MqAndAEMResults;
				filteredLocationArray = removeDuplicate(filteredLocationArray,"loc_detail_page");	
            	resultCreator(filteredLocationArray,"singleDimension",eventType,numberOfResultToBeDisplayed,locationShowMoreCounter);
            }else if(checkedCityFlag == false || checkedStateFlag == false || checkedServiceFlag == false){
            	var noMatchingResults=[];
				filteredLocationArray = noMatchingResults;
            	resultCreator(filteredLocationArray,"singleDimension",eventType,numberOfResultToBeDisplayed,locationShowMoreCounter);
            }
            //States, Cities & Services checked
            if(checkedCityFlag == true && checkedStateFlag == true && checkedServiceFlag == true){
				if(unCheckedStateFlag == true){
					stateFlag = true;
					for (var j=0;j<MqAndAEMResults.length;j++){
						for (var k=0;k<valueFromStateCheckBox.length;k++){
							refinedStateResult.push($(MqAndAEMResults[j]).filter(function (i,n){return n.loc_state_tag === valueFromStateCheckBox[k]})); 
						}
					}
				}else if(unCheckedStateFlag == false){
					refinedStateResult = MqAndAEMResults;
					resultTypeSingleDimentionCount = resultTypeSingleDimentionCount+1;
				}
				if(unCheckedCityFlag == true){					
					if(stateFlag == true){
						cityFlag = true;
						for (var j=0;j<refinedStateResult.length;j++){
							for (var p=0;p<refinedStateResult[j].length;p++){
								for (var k=0;k<valueFromCityCheckBox.length;k++){
									refinedCityResult.push($(refinedStateResult[j][p]).filter(function (i,n){return n.loc_city === valueFromCityCheckBox[k]}));
								}
							}
						}
					}else if(stateFlag == false){
						for (var j=0;j<refinedStateResult.length;j++){
							for (var k=0;k<valueFromCityCheckBox.length;k++){
								refinedCityResult.push($(refinedStateResult[j]).filter(function (i,n){return n.loc_city === valueFromCityCheckBox[k]})); 
							}
						}
					}
				}else{
					refinedCityResult = refinedStateResult;
					resultTypeSingleDimentionCount = resultTypeSingleDimentionCount+1;
				}
				if(unCheckedServiceFlag == true){					
					if(cityFlag == true){
						serviceFlag = true;
						for (var j=0;j<refinedCityResult.length;j++){
							for (var p=0;p<refinedCityResult[j].length;p++){
								for (var k=0;k<valueFromServicesCheckBox.length;k++){
									if((valueFromServicesCheckBox.indexOf("drive-thru-banking") < 0) && (valueFromServicesCheckBox.indexOf("weekend-hours") < 0) && (valueFromServicesCheckBox.indexOf("instore-branch") < 0)){
										refinedResult.push($(refinedCityResult[j][p]).filter(function (i,n){if(n.loc_services_tag.toString().indexOf(valueFromServicesCheckBox[k])>=0) return true;else return false;}));
									}else{
										if(valueFromServicesCheckBox[k] != "branch"){
											refinedResult.push($(refinedCityResult[j][p]).filter(function (i,n){if(n.loc_services_tag.toString().indexOf(valueFromServicesCheckBox[k])>=0) return true;else return false;}));
										}
									}
								}	
							}
						}
					}else if(cityFlag == false){
						for (var j=0;j<refinedCityResult.length;j++){
							for (var k=0;k<valueFromServicesCheckBox.length;k++){
								if((valueFromServicesCheckBox.indexOf("drive-thru-banking") < 0) && (valueFromServicesCheckBox.indexOf("weekend-hours") < 0) && (valueFromServicesCheckBox.indexOf("instore-branch") < 0)){
									refinedResult.push($(refinedCityResult[j]).filter(function (i,n){
										if(n.loc_services_tag.toString().indexOf(valueFromServicesCheckBox[k])>=0) 
											return true;
										else 
										return false;
									}));
								}else{
									if(valueFromServicesCheckBox[k] != "branch"){
										refinedResult.push($(refinedCityResult[j]).filter(function (i,n){
											if(n.loc_services_tag.toString().indexOf(valueFromServicesCheckBox[k])>=0) 
												return true;
											else 
												return false;
										}));
									}
								}								
							}
						}
					}
				}else{
					refinedResult = refinedCityResult;
					resultTypeSingleDimentionCount = resultTypeSingleDimentionCount+1;
				}
				if(resultTypeSingleDimentionCount == 3){
					filteredLocationArray = refinedResult;
					filteredLocationArray = removeDuplicate(filteredLocationArray,"loc_detail_page");
					resultCreator(filteredLocationArray,"singleDimension",eventType,numberOfResultToBeDisplayed,locationShowMoreCounter);
				}	
				else{
					for(var i=0; i<refinedResult.length;i++){	
			    	    for(var j=0; j<refinedResult[i].length;j++){
							refinedResultSingleDimension.push(refinedResult[i][j]);
						}
					}
					filteredLocationArray = refinedResultSingleDimension;
					filteredLocationArray = removeDuplicate(filteredLocationArray,"loc_detail_page");					
					resultCreator(filteredLocationArray,"singleDimension",eventType,numberOfResultToBeDisplayed,locationShowMoreCounter);
				}
            }
            
    
        }       
    }

	function refinePeopleFilter(selectedRadius,eventType){
		var startTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
		$('#loadingMask').show();
		numberOfResultToBeDisplayedPeople = parseInt($('.people_result_show_more_results a').attr("data-search-visible-items-limit"));
		peopleShowMoreCounter = 0;
        var valueFromCityCheckBox=[];
        var valueFromStateCheckBox=[];
        var valueFromSpecialityCheckBox=[];
        var refinedResult=[];
        var refinedStateResult=[];
        var refinedCityResult=[];
        var refinedSpecialityResult=[];
        var refinedDuplicateSpecialityResult=[];
        var refinedUniqueUrlList=[];
        var checkedCityFlag = false;
        var checkedStateFlag = false;
        var checkedSpecialityFlag = false;
		var unCheckedCityFlag = false;
        var unCheckedStateFlag = false;
        var unCheckedSpecialityFlag = false;
		var stateFlag = false;
		var cityFlag = false;
		var specialityFlag = false
		var resultTypeSingleDimentionCount = 0;
        var radius = selectedRadius;
		var refinedResultSingleDimension=[];
        $('.search_result_description .search-results .people_filter_container .filter-no-result').remove();
        $('.search_result_description .search-results .people_filter_container .people_mq_results').children().remove();
        $('.search_result_description .search-results .people_filter_container .people_aem_results').children().remove();
    	$('.location-search .search_result_count').addClass("hide");
        if(window.innerWidth<768) {
            $('.people_filter_mobile .mobile-city input').each(function(e){
                if($(this).is(':checked')){
                    checkedCityFlag = true;
					if($(this).val().indexOf(',')>0){
						var splArr = $(this).val().split(',');
						for(var i=0;i<splArr.length;i++) {
							valueFromCityCheckBox.push(splArr[i]);
						}
					} else {
						valueFromCityCheckBox.push($(this).val());
					}
                }
				if(!($(this).is(':checked'))){
                    unCheckedCityFlag = true;
                }
            });
			valueFromCityCheckBox = $.unique(valueFromCityCheckBox);
			
            $('.people_filter_mobile .mobile-state input').each(function(e){
                if($(this).is(':checked')){
                    checkedStateFlag = true;
                    valueFromStateCheckBox.push($(this).val());
                }
				if(!($(this).is(':checked'))){
                    unCheckedStateFlag = true;
                }
            });
            $('.people_filter_mobile .mobile-speciality input').each(function(e){
                if($(this).is(':checked')){
                    checkedSpecialityFlag = true;
                    if($(this).val().indexOf(',')>0){
						var splArr = $(this).val().split(',');
						for(var i=0;i<splArr.length;i++) {
							valueFromSpecialityCheckBox.push(splArr[i]);
						}
					} else {
						valueFromSpecialityCheckBox.push($(this).val());
					}
                }
				if(!($(this).is(':checked'))){
                    unCheckedSpecialityFlag = true;
                }
            });
			valueFromSpecialityCheckBox = $.unique(valueFromSpecialityCheckBox);
        }
		if(window.innerWidth>=768) {
            $('.people_filter_desktop .search_detail_city input').each(function(e){
                if($(this).is(':checked')){
                    checkedCityFlag = true;
                    if($(this).val().indexOf(',')>0){
						var splArr = $(this).val().split(',');
						for(var i=0;i<splArr.length;i++) {
							valueFromCityCheckBox.push(splArr[i]);
						}
					} else {
						valueFromCityCheckBox.push($(this).val());
					}
                }
				if(!($(this).is(':checked'))){
                    unCheckedCityFlag = true;
                }
            });
			valueFromCityCheckBox = $.unique(valueFromCityCheckBox);
			
            $('.people_filter_desktop .search_detail_state input').each(function(e){
                if($(this).is(':checked')){
                    checkedStateFlag = true;
                    valueFromStateCheckBox.push($(this).val());
                }
				if(!($(this).is(':checked'))){
                    unCheckedStateFlag = true;
                }
            });
            $('.people_filter_desktop .search_detail_speciality_level input').each(function(e){
                if($(this).is(':checked')){
                    checkedSpecialityFlag = true;
                    if($(this).val().indexOf(',')>0){
						var splArr = $(this).val().split(',');
						for(var i=0;i<splArr.length;i++) {
							valueFromSpecialityCheckBox.push(splArr[i]);
						}
					} else {
						valueFromSpecialityCheckBox.push($(this).val());
					}
                }
				if(!($(this).is(':checked'))){
                    unCheckedSpecialityFlag = true;
                }
            });
			valueFromSpecialityCheckBox = $.unique(valueFromSpecialityCheckBox);
			
			setTimeout(function() {
    						passSearchValuesToAnalyticEngine("desktopView");
    						}, 1000);
        }
		
		valueFromSpecialityCheckBoxGlobal = valueFromSpecialityCheckBox;
		
		
		
        if(radius != "Select"){
			//None checked
            if(checkedStateFlag == false && checkedCityFlag == false && checkedSpecialityFlag == false){
				filteredPeopleArray = MqAndAEMResultsPeople;
				filteredPeopleArray = removeDuplicate(filteredPeopleArray,"advisor_profilePage");	
            	resultCreatorPeople(filteredPeopleArray,"singleDimension",eventType,numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);           
            }else if(checkedCityFlag == false || checkedStateFlag == false || checkedSpecialityFlag == false){
            	var noMatchingResults=[];
				filteredPeopleArray = noMatchingResults;
            	resultCreatorPeople(filteredPeopleArray,"singleDimension",eventType,numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
            }
            //States, Cities & Services checked
            if(checkedCityFlag == true && checkedStateFlag == true && checkedSpecialityFlag == true){
				if(unCheckedStateFlag == true){
					stateFlag = true;
					/*for (var j=0;j<MqAndAEMResultsPeople.length;j++){
						for (var k=0;k<valueFromStateCheckBox.length;k++){
							refinedStateResult.push($(MqAndAEMResultsPeople[j]).filter(function (i,n){return n.advisor_state === valueFromStateCheckBox[k]})); 
						}
					}*/
					for (var ka=0;ka<valueFromStateCheckBox.length;ka++){
						refinedStateResult = refinedStateResult.concat($.grep(MqAndAEMResultsPeople, function(v) {
							if(v.advisor_state!=null&&v.advisor_state!=undefined)
							return v.advisor_state.toUpperCase() === valueFromStateCheckBox[ka];
						}));
					}
				}else if(unCheckedStateFlag == false){
					refinedStateResult = MqAndAEMResultsPeople;
					resultTypeSingleDimentionCount = resultTypeSingleDimentionCount+1;
				}
				if(unCheckedCityFlag == true){					
					if(stateFlag == true){
						cityFlag = true;
						/*for (var j=0;j<refinedStateResult.length;j++){
							for (var p=0;p<refinedStateResult[j].length;p++){
								for (var k=0;k<valueFromCityCheckBox.length;k++){
									refinedCityResult.push($(refinedStateResult[j][p]).filter(function (i,n){return n.advisor_city === valueFromCityCheckBox[k]}));
								}
							}
						}*/
						for (var ka=0;ka<valueFromCityCheckBox.length;ka++){
							refinedCityResult = refinedCityResult.concat($.grep(refinedStateResult, function(v) {
								if(v.advisor_city!=null&&v.advisor_city!=undefined)
								return (v.advisor_city.toUpperCase()).indexOf(valueFromCityCheckBox[ka]) > -1;
							}));
						}
					}else if(stateFlag == false){
						/*for (var j=0;j<refinedStateResult.length;j++){
							for (var k=0;k<valueFromCityCheckBox.length;k++){
								refinedCityResult.push($(refinedStateResult[j]).filter(function (i,n){return n.advisor_city === valueFromCityCheckBox[k]})); 
							}
						}*/
						for (var ka=0;ka<valueFromCityCheckBox.length;ka++){
							refinedCityResult = refinedCityResult.concat($.grep(refinedStateResult, function(v) {
								if(v.advisor_city!=null&&v.advisor_city!=undefined)
								return (v.advisor_city.toUpperCase()).indexOf(valueFromCityCheckBox[ka]) > -1;
							}));
						}
					}
				}else{
					refinedCityResult = refinedStateResult;
					resultTypeSingleDimentionCount = resultTypeSingleDimentionCount+1;
				}
				if(unCheckedSpecialityFlag == true){					
					if(cityFlag == true){
						specialityFlag = true;
						/*for (var j=0;j<refinedCityResult.length;j++){
							for (var p=0;p<refinedCityResult[j].length;p++){
								for (var k=0;k<valueFromSpecialityCheckBox.length;k++){		
									refinedResult.push($(refinedCityResult[j][p]).filter(function (i,n){if(n.advisor_specialty.toString().toUpperCase().indexOf(valueFromSpecialityCheckBox[k].toLowerCase())>=0) return true;else return false;}));
								}	
							}
						}*/
						for (var ka=0;ka<valueFromSpecialityCheckBox.length;ka++){
							refinedResult = refinedResult.concat($.grep(refinedCityResult, function(v) {
								if(v.advisor_specialty!=null&&v.advisor_specialty!=undefined)
								return (v.advisor_specialty.toUpperCase()).indexOf(valueFromSpecialityCheckBox[ka]) > -1;
							}));
						}
					}else if(cityFlag == false){
						/*for (var j=0;j<refinedCityResult.length;j++){
							for (var k=0;k<valueFromSpecialityCheckBox.length;k++){
								refinedResult.push($(refinedCityResult[j]).filter(function (i,n){
									if(n.advisor_specialty.toString().toUpperCase().indexOf(valueFromSpecialityCheckBox[k])>=0) 
										return true;
									else 
									return false;
								}));
							}
						}*/
						for (var ka=0;ka<valueFromSpecialityCheckBox.length;ka++){
							refinedResult = refinedResult.concat($.grep(refinedCityResult, function(v) {
								if(v.advisor_specialty!=null&&v.advisor_specialty!=undefined)
								return (v.advisor_specialty.toUpperCase()).indexOf(valueFromSpecialityCheckBox[ka]) > -1;
							}));
						}
					}
				}else{
					refinedResult = refinedCityResult;
					resultTypeSingleDimentionCount = resultTypeSingleDimentionCount+1;
				}
				
					filteredPeopleArray = refinedResult;
					filteredPeopleArray = removeDuplicate(filteredPeopleArray,"advisor_profilePage");					
					resultCreatorPeople(filteredPeopleArray,"singleDimension",eventType,numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
            }
            
    
        }       
    }
	
$(document).on("change",".locations_filter_desktop .search_detail_city input, .locations_filter_desktop .search_detail_state input, .locations_filter_desktop .search_detail_services_level0 input, .state_city_selectall_clear a", function(e) {
    var eventType="refineResult";
	//$(this).parent().toggleClass("sun-checked");
    if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
        if(!$(this).is(":checked")) {
            $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').hide();
            $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').addClass('hide');
            $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').find('.sun-checkbox-input-field span').removeClass('sun-checked');
            $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').find('.sun-checkbox-input-field input').attr('checked',false);
        }
        else {
	         $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').removeClass('hide');
         	 $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').show();
	    }
	}

    var selecteRadius = $('.locations_filter_desktop .search_detail_location_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refineFilter(selecteRadius,eventType);
});

$(document).on("change",".locations_filter_mobile input", function(e) {
   // $(this).parent().toggleClass("sun-checked");
    if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
    	if($(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').is(':visible')) {
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').addClass('hide');
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').hide();
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').find('.sun-checkbox-input-field span').removeClass('sun-checked');
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').find('.sun-checkbox-input-field input').attr('checked',false);
		}
		else {
		     $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').removeClass('hide');
		     $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').show();
		}
	}
});

$(document).on("click",".locations_filter_desktop .location_state .select_all", function(e) {
	var eventType="refineResult";
    $('.locations_filter_desktop .search_detail_state .search_detail_state_city_select .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);
    });	
    var selecteRadius = $('.locations_filter_desktop .search_detail_location_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refineFilter(selecteRadius,eventType);
});

$(document).on("click",".locations_filter_desktop .location_state .select_none", function(e) {
	var eventType="refineResult";
    $('.locations_filter_desktop .search_detail_state .search_detail_state_city_select .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
    var selecteRadius = $('.locations_filter_desktop .search_detail_location_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refineFilter(selecteRadius,eventType);
});


$(document).on("click",".locations_filter_desktop .location_city .select_none", function(e) {
	var eventType="refineResult";
	$('.locations_filter_desktop .search_detail_city .search_detail_state_city_select .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
    var selecteRadius = $('.locations_filter_desktop .search_detail_location_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refineFilter(selecteRadius,eventType);
});


$(document).on("click",".locations_filter_desktop .location_city .select_all", function(e) {
	var eventType="refineResult";
    $('.locations_filter_desktop .search_detail_city .search_detail_state_city_select .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);       
    });	
    var selecteRadius = $('.locations_filter_desktop .search_detail_location_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refineFilter(selecteRadius,eventType);

});

$(document).on("click",".locations_filter_mobile .location_state .select_all", function(e) {

    $('.locations_filter_mobile .mobile-state .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);
    });	
});

$(document).on("click",".locations_filter_mobile .location_state .select_none", function(e) {

    $('.locations_filter_mobile .mobile-state .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
});


$(document).on("click",".locations_filter_mobile .location_city .select_none", function(e) {

	$('.locations_filter_mobile .mobile-city .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
});


$(document).on("click",".locations_filter_mobile .location_city .select_all", function(e) {

    $('.locations_filter_mobile .mobile-city .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);       
    });	

});

/*People search click handlers - start*/
$(document).on("change",".people_filter_desktop .search_detail_city input, .people_filter_desktop .search_detail_state input, .people_filter_desktop .search_detail_speciality_level input, .state_city_selectall_clear a", function(e) {
    var eventType="refineResult";
	//$(this).parent().toggleClass("sun-checked");
    if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_speciality_level')) {
        if(!$(this).parent().hasClass('sun-checked')) {
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_speciality_level').addClass('hide');
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_speciality_level').find('.sun-checkbox-input-field span').removeClass('sun-checked');
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_speciality_level').find('.sun-checkbox-input-field input').attr('checked',false);
		}
		else {
		     $(this).parents('.sun-checkbox-input-field').next('.search_detail_speciality_level').removeClass('hide');
		}
	}

    var selecteRadius = $('.people_filter_desktop .search_detail_people_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refinePeopleFilter(selecteRadius,eventType);
	
	
});

$(document).on("change",".people_filter_mobile input", function(e) {
    //$(this).parent().toggleClass("sun-checked");
    if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_speciality_level')) {
        if(!$(this).parent().hasClass('sun-checked')) {
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_speciality_level').addClass('hide');
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_speciality_level').find('.sun-checkbox-input-field span').removeClass('sun-checked');
			$(this).parents('.sun-checkbox-input-field').next('.search_detail_speciality_level').find('.sun-checkbox-input-field input').attr('checked',false);
		}
		else {
		     $(this).parents('.sun-checkbox-input-field').next('.search_detail_speciality_level').removeClass('hide');
		}
	}
});

$(document).on("click",".people_filter_desktop .people_speciality .select_all", function(e) {
	var eventType="refineResult";
    $('.people_filter_desktop .search_detail_speciality .search_detail_speciality_level .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);
    });	
    var selecteRadius = $('.people_filter_desktop .search_detail_people_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refinePeopleFilter(selecteRadius,eventType);
});

$(document).on("click",".people_filter_desktop .people_speciality .select_none", function(e) {
	var eventType="refineResult";
    $('.people_filter_desktop .search_detail_speciality .search_detail_speciality_level .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
    var selecteRadius = $('.people_filter_desktop .search_detail_people_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refinePeopleFilter(selecteRadius,eventType);
});

$(document).on("click",".people_filter_desktop .people_state .select_all", function(e) {
	var eventType="refineResult";
    $('.people_filter_desktop .search_detail_state .search_detail_state_city_select .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);
    });	
    var selecteRadius = $('.people_filter_desktop .search_detail_people_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refinePeopleFilter(selecteRadius,eventType);
});

$(document).on("click",".people_filter_desktop .people_state .select_none", function(e) {
	var eventType="refineResult";
    $('.people_filter_desktop .search_detail_state .search_detail_state_city_select .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
    var selecteRadius = $('.people_filter_desktop .search_detail_people_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refinePeopleFilter(selecteRadius,eventType);
});


$(document).on("click",".people_filter_desktop .people_city .select_none", function(e) {
	var eventType="refineResult";
	$('.people_filter_desktop .search_detail_city .search_detail_state_city_select .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
    var selecteRadius = $('.people_filter_desktop .search_detail_people_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refinePeopleFilter(selecteRadius,eventType);
});


$(document).on("click",".people_filter_desktop .people_city .select_all", function(e) {
	var eventType="refineResult";
    $('.people_filter_desktop .search_detail_city .search_detail_state_city_select .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);       
    });	
    var selecteRadius = $('.people_filter_desktop .search_detail_people_radius .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
    refinePeopleFilter(selecteRadius,eventType);

});

$(document).on("click",".people_filter_mobile .people_speciality .select_all", function(e) {

    $('.people_filter_mobile .mobile-speciality .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);
    });	
});

$(document).on("click",".people_filter_mobile .people_speciality .select_none", function(e) {

    $('.people_filter_mobile .mobile-speciality .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
});

$(document).on("click",".people_filter_mobile .people_state .select_all", function(e) {

    $('.people_filter_mobile .mobile-state .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);
    });	
});

$(document).on("click",".people_filter_mobile .people_state .select_none", function(e) {

    $('.people_filter_mobile .mobile-state .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
});


$(document).on("click",".people_filter_mobile .people_city .select_none", function(e) {

	$('.people_filter_mobile .mobile-city .sun-checkbox-input-container').each(function(e){
		$(this).find('span').removeClass("sun-checked");
        $(this).find('input').prop('checked', false);
    });	
});


$(document).on("click",".people_filter_mobile .people_city .select_all", function(e) {

    $('.people_filter_mobile .mobile-city .sun-checkbox-input-container').each(function(e){
		$(this).find('span').addClass("sun-checked");
        $(this).find('input').prop('checked', true);       
    });	

});
/*People search click handlers - end*/

function selectedFilterFinder(selector){
    var selectedFiltersArray=[];
	$(selector).find('input').each(function(e){
         if(!$(this).is(':checked')){
             selectedFiltersArray.push($(this).val());
         }
     });
    return selectedFiltersArray;
}
function selectedPeopleFilterFinder(selector){
    var selectedFiltersArray=[];
	$(selector).find('input').each(function(e){
         if(!$(this).is(':checked')){
             selectedFiltersArray.push($(this).attr('name'));
         }
     });
    return selectedFiltersArray;
}

function filterOptionFinder(selector){
    var filtersOptionsArray=[];
	$(selector).find('input').each(function(e){
        filtersOptionsArray.push($(this).val());
    });
    return filtersOptionsArray;
}

function retainFilterValues(selector, selectedValuesArray){	
    $(selector).find('input').each(function(index){
		$(this).prop('checked', true);
		$(this).parent().addClass("sun-checked");
        for(var i=0; i<selectedValuesArray.length; i++){
            if($(this).val() == selectedValuesArray[i]){
				$(this).prop('checked', false);
            	$(this).parent().removeClass("sun-checked");
            }
        }
        if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
            if(!$(this).is(":checked")) {
                $(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').hide();
            }
            else {
            	$(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').show();
            }
        }
    });
}
function retainPeopleFilterValues(selector, selectedValuesArray){	
    $(selector).find('input').each(function(index){
		$(this).prop('checked', true);
		$(this).parent().addClass("sun-checked");
        for(var i=0; i<selectedValuesArray.length; i++){
            if($(this).attr('name') == selectedValuesArray[i]){
				$(this).prop('checked', false);
				$(this).parent().removeClass("sun-checked");
            }
        }
    });
}

function resultCreatorShowResourcesResults() {
	var activeTab = $("ul.search_select a.active").attr("index");
	var activeTabNoSpace = activeTab.replace(" ", "_");
	var activeTabLowecase = activeTabNoSpace.toLowerCase();
	$('.search_result_filters_nonProducts').addClass('hide');
	$('.search_result_filters_nonProducts > div').addClass('hide');
	$('.result_filters_other').addClass('author_hide');
	$('.result_filters_other > div').addClass('author_hide');
	//$('.mobile_filter_search > div:not(first-child)').addClass('hide');
	$('.resources_no_result.zero_result_found').addClass('hide');
	if(activeTabLowecase!="products" && activeTabLowecase!="all_results" && activeTabLowecase!="faqs") {
		$('.search_result_filters_nonProducts').removeClass('hide');
		$('.search_result_filters_nonProducts').show();
		$('.'+activeTabLowecase+'_filter_desktop').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_mobile').removeClass('hide');
	}
	else {
		$('.search_result_filters_nonProducts').addClass('hide');
		if(!$('.faqs_no_result.zero_result_found').is(":visible")){
			$('.result_filters_other').removeClass('author_hide');
			$('.faqs_filter_container').removeClass('hide');
			$('.search_result_filters_faqs').removeClass('author_hide');
		}		
	}
}
function resultCreatorShowLocationResults() {
	var activeTab = $("ul.search_select a.active").attr("index");
	var activeTabNoSpace = activeTab.replace(" ", "_");
	var activeTabLowecase = activeTabNoSpace.toLowerCase();
	$('.search_result_filters_nonProducts').addClass('hide');
	$('.search_result_filters_nonProducts > div').addClass('hide');
	$('.result_filters_other').addClass('author_hide');
	$('.result_filters_other > div').addClass('author_hide');
	//$('.mobile_filter_search > div:not(first-child)').addClass('hide');
	$('.locations_no_result.zero_result_found').addClass('hide');
	if(activeTabLowecase!="products" && activeTabLowecase!="all_results" && activeTabLowecase!="faqs") {
		$('.search_result_filters_nonProducts').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_desktop').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_mobile').removeClass('hide');
	}
	else {
		$('.search_result_filters_nonProducts').addClass('hide');
		if(!$('.faqs_no_result.zero_result_found').is(":visible")){
			$('.result_filters_other').removeClass('author_hide');
			$('.faqs_filter_container').removeClass('hide');
			$('.search_result_filters_faqs').removeClass('author_hide');
		}		
	}
}
function resultCreatorShowAllResults() {
	var activeTab = $("ul.search_select a.active").attr("index");
	var activeTabNoSpace = activeTab.replace(" ", "_");
	var activeTabLowecase = activeTabNoSpace.toLowerCase();
	$('.search_result_filters_nonProducts').addClass('hide');
	$('.search_result_filters_nonProducts > div').addClass('hide');
	$('.result_filters_other').addClass('author_hide');
	$('.result_filters_other > div').addClass('author_hide');
	$('.faqs_no_result.zero_result_found').addClass('hide');
	$('.products_no_result.zero_result_found').addClass('hide');
	$('.locations_no_result.zero_result_found').addClass('hide');
	$('.all_results_no_result.zero_result_found').addClass("hide");
	if(activeTabLowecase!="products" && activeTabLowecase!="all_results" && activeTabLowecase!="faqs") {
		$('.search_result_filters_nonProducts').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_desktop').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_mobile').removeClass('hide');
	}
	else {
		$('.search_result_filters_nonProducts').addClass('hide');
		$('.result_filters_other').removeClass('author_hide');
		$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
		$('.search_result_filters_'+activeTabLowecase).removeClass('hide');
		$('.search_result_filters_'+activeTabLowecase).removeClass("author_hide");
	}
}
function resultCreatorShowPeopleResults() {
	$('.locations_result_count').addClass("hide");
	var activeTab = $("ul.search_select a.active").attr("index");
	var activeTabNoSpace = activeTab.replace(" ", "_");
	var activeTabLowecase = activeTabNoSpace.toLowerCase();
	$('.search_result_filters_nonProducts').addClass('hide');
	$('.search_result_filters_nonProducts > div').addClass('hide');
	$('.result_filters_other').addClass('author_hide');
	$('.result_filters_other > div').addClass('author_hide');
	//$('.mobile_filter_search > div:not(first-child)').addClass('hide');
	$('.people_no_result.zero_result_found').addClass('hide');
	if(activeTabLowecase!="products" && activeTabLowecase!="all_results" && activeTabLowecase!="faqs") {
		$('.search_result_filters_nonProducts').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_desktop').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_mobile').removeClass('hide');
	}
	else {
		$('.search_result_filters_nonProducts').addClass('hide');
		if(!$('.faqs_no_result.zero_result_found').is(":visible")){
			$('.result_filters_other').removeClass('author_hide');
			$('.faqs_filter_container').removeClass('hide');
			$('.search_result_filters_faqs').removeClass('author_hide');
		}		
	}
}
function resultCreatorShowFaqResults() {
	var activeTab = $("ul.search_select a.active").attr("index");
	var activeTabNoSpace = activeTab.replace(" ", "_");
	var activeTabLowecase = activeTabNoSpace.toLowerCase();
	$('.search_result_filters_nonProducts').addClass('hide');
	$('.search_result_filters_nonProducts > div').addClass('hide');
	$('.result_filters_other').addClass('author_hide');
	$('.result_filters_other > div').addClass('author_hide');
	//$('.mobile_filter_search > div:not(first-child)').addClass('hide');
	$('.faqs_no_result.zero_result_found').addClass('hide');
	$('.products_no_result.zero_result_found').addClass('hide');

	if(activeTabLowecase!="products" && activeTabLowecase!="all_results" && activeTabLowecase!="faqs") {
		$('.search_result_filters_nonProducts').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_desktop').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
		$('.'+activeTabLowecase+'_filter_mobile').removeClass('hide');
	}
	else {
		$('.search_result_filters_nonProducts').addClass('hide');
		$('.result_filters_other').removeClass('author_hide');
		$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
		$('.search_result_filters_'+activeTabLowecase).removeClass('hide');
		$('.search_result_filters_'+activeTabLowecase).removeClass("author_hide");
	}
}
function resultCreatorShowNoResult() {
	var activeTab = $("ul.search_select a.active").attr("index");
	var activeTabNoSpace = activeTab.replace(" ", "_");
	var activeTabLowecase = activeTabNoSpace.toLowerCase();
	$('.search_result_filters_nonProducts').addClass('hide');
	$('.search_result_filters_nonProducts > div').addClass('hide');
	$('.result_filters_other').addClass('author_hide');
	$('.result_filters_other > div').addClass('author_hide');
	//$('.mobile_filter_search > div:not(first-child)').addClass('hide');
	//$('.zero_result_found').addClass('hide');
	$('.faqs_filter_container').addClass('hide');
	$('.all_results_filter_container').addClass('hide');
	$('.'+activeTabLowecase+'_no_result').removeClass('hide');
	$('.result_filters_other').addClass("author_hide");
	$('.search_result_filters_'+activeTabLowecase).addClass("author_hide");
}

function faqResultCreator(){
	
	$('.locations_result_count').addClass("hide");
	$('.search_result_filters_nonProducts').addClass("hide");
	if(faq_result_object.length > 0){
		$('.faqs_no_result').addClass("hide");
		//$('.faqs_filter_container').removeClass("hide");
		for(var i=0;i<faq_result_object.length;i++){
			if(faq_result_object[i].faq_question != undefined && faq_result_object[i].faq_answer != undefined)
				$('.search-results .faqs_filter_container .sun-faqs-page-details .sun-faqs-page-list.sun-faqs-page-list-alt').append("<div data-sun-class='faqs-page-list-item' class='sun-faqs-page-list-item'><a class='sun-faqs-page-list-item-header' data-sun-class='faqs-page-list-item-header' href='javascript:void(0);'>"+faq_result_object[i].faq_question+"</a><div class='sun-faqs-page-list-item-detail' data-sun-class='faqs-page-list-item-detail'><p>"+faq_result_object[i].faq_answer+"</p></div></div>");
			else if(faq_result_object[i].faq_question == undefined && faq_result_object[i].faq_answer != undefined)
				$('.search-results .faqs_filter_container .sun-faqs-page-details .sun-faqs-page-list.sun-faqs-page-list-alt').append("<div data-sun-class='faqs-page-list-item' class='sun-faqs-page-list-item'><a class='sun-faqs-page-list-item-header' data-sun-class='faqs-page-list-item-header' href='javascript:void(0);'></a><div class='sun-faqs-page-list-item-detail' data-sun-class='faqs-page-list-item-detail'><p>"+faq_result_object[i].faq_answer+"</p></div></div>");
			else if(faq_result_object[i].faq_question != undefined && faq_result_object[i].faq_answer == undefined)
				$('.search-results .faqs_filter_container .sun-faqs-page-details .sun-faqs-page-list.sun-faqs-page-list-alt').append("<div data-sun-class='faqs-page-list-item' class='sun-faqs-page-list-item'><a class='sun-faqs-page-list-item-header' data-sun-class='faqs-page-list-item-header' href='javascript:void(0);'>"+faq_result_object[i].faq_question+"</a><div class='sun-faqs-page-list-item-detail' data-sun-class='faqs-page-list-item-detail'><p></p></div></div>");
			else if(faq_result_object[i].faq_question == undefined && faq_result_object[i].faq_answer == undefined)
				$('.search-results .faqs_filter_container .sun-faqs-page-details .sun-faqs-page-list.sun-faqs-page-list-alt').append("<div data-sun-class='faqs-page-list-item' class='sun-faqs-page-list-item'><a class='sun-faqs-page-list-item-header' data-sun-class='faqs-page-list-item-header' href='javascript:void(0);'></a><div class='sun-faqs-page-list-item-detail' data-sun-class='faqs-page-list-item-detail'><p></p></div></div>");
		}
		$('.faq_shoeMoreLess_Content [href^="/content/suntrust/dotcom/us/en"], .sun-faqs-page-list-item-detail [href^="/content/suntrust/dotcom/us/en"]').each(function(e){
			var formattedVal = $(this).attr('href').replace("/content/suntrust/dotcom/us/en",""); 
			formattedVal = formattedVal.replace(".html",""); 
			$(this).attr("href", formattedVal);
		});
		speedBumpCheck($('.sun-faqs-page-list-item-detail'));
		speedBumpCheck($('.faq_shoeMoreLess_Content'));
		resultCreatorShowFaqResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		if(activeTabLowecase=="faqs") {
			$('.search_result_filters_nonProducts').addClass('hide');
			$('.result_filters_other').removeClass('author_hide');
			$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
			$('.search_result_filters_'+activeTabLowecase).removeClass('author_hide');	
		}
	}
	else
	{
		resultCreatorShowNoResult();
		resultCreatorShowFaqResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		$('.'+activeTabLowecase+'_no_result').removeClass('hide');
		if(activeTabLowecase=="faqs") {
			$('.result_filters_other').addClass('author_hide');
			$('.'+activeTabLowecase+'_filter_container').addClass('hide');
			$('.search_result_filters_'+activeTabLowecase).addClass('author_hide');	
		}
	}
	var faqResultCount = $(".search-results .faqs_filter_container .sun-faqs-page-details .sun-faqs-page-list.sun-faqs-page-list-alt").children().length;
	$('.search_result_count .faqs_result_count').text(faqResultCount);	
	$('.search_result_count .faqs_result_count').removeClass("hide");
	$('.location-search .search_result_count').removeClass("hide");
	$('#loadingMask').fadeOut(100);
	$("div.sun-faqs-page-list-item").hide();
	$(".search-results .faqs_filter_container .sun-faqs-page-details .sun-faqs-page-list.sun-faqs-page-list-alt").children().show();
	if(window.innerWidth>=768) {
		$(".search-results .faqs_filter_container .sun-faqs-page-details .sun-faqs-page-list.sun-faqs-page-list-alt").children().hide();
		faqSearchShowMore();
	}
	faqSearchHelper();
	
}

function resourcesResultCreator(resultToBeDisplayed, eventType, numberOfResultToBeDisplayed, resourcesShowMoreCounter){
	var responseStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
	if(resultToBeDisplayed.length < numberOfResultToBeDisplayed)
		numberOfResultToBeDisplayed = resultToBeDisplayed.length;		
	if(resultToBeDisplayed.length > 0){				
		for(var i = resourcesShowMoreCounter; i < numberOfResultToBeDisplayed; i++ ){
			if(resultToBeDisplayed[i].resource_thumbnail_image != undefined && resultToBeDisplayed[i].resource_thumbnail_image != "")
				$('.search_result_description .search-results .resources_filter_container .search_result_content').append("<div class='search_result_content_data clearfix'><div class='search_result_content_img'><a title='"+resultToBeDisplayed[i].resource_title+"' href='"+resultToBeDisplayed[i].resource_page+"'><img src='"+resultToBeDisplayed[i].resource_thumbnail_image+"'/></a></div><div class='search_result_content_img_data'><a title='"+resultToBeDisplayed[i].resource_title+"' href='"+resultToBeDisplayed[i].resource_page+"'>"+resultToBeDisplayed[i].resource_title+"</a><p>"+resultToBeDisplayed[i].resource_desc+"</p></div></div>");
			else
				$('.search_result_description .search-results .resources_filter_container .search_result_content').append("<div class='search_result_content_data clearfix'><div class='search_result_content_img_data'><a title='"+resultToBeDisplayed[i].resource_title+"' href='"+resultToBeDisplayed[i].resource_page+"'>"+resultToBeDisplayed[i].resource_title+"</a><p>"+resultToBeDisplayed[i].resource_desc+"</p></div></div>");
		}
	}
	var endTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
	var resultLength = $('.search_result_description .search-results .resources_filter_container .search_result_content').children().length;
	$('.resources-filter-no-result-identifier').remove();
	if(resultLength != 0){
		resultCreatorShowResourcesResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		if(activeTabLowecase=="resources") {
			$('.faqs_result_count').addClass("hide");
			$('.products_result_count').addClass("hide");
			$('.'+activeTabLowecase+'_result_count').removeClass('hide');
			$('.search_result_filters_nonProducts').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_desktop').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
			$('.'+activeTabLowecase+'_filter_mobile').removeClass('hide');
		}
		$('.search_result_description .search-results .resources_filter_container .search_result_content').children().show();
		if(window.innerWidth>=768) {
			resourcesSearchShowMore();
		}
	}
	else{
		if(eventType == "newSearch" && selectboxLowerCase=="resources"){
			resultCreatorShowNoResult();
			resultCreatorShowResourcesResults();	
			var activeTab = $("ul.search_select a.active").attr("index");
			var activeTabNoSpace = activeTab.replace(" ", "_");
			var activeTabLowecase = activeTabNoSpace.toLowerCase();
			if(activeTabLowecase="resources") {
				$('.faqs_result_count').addClass("hide");
				$('.products_result_count').addClass("hide");
				$('.'+activeTabLowecase+'_no_result').removeClass('hide');
				$('.'+activeTabLowecase+'_result_count').removeClass('hide');
			}
		}
		else if(eventType == "refineResult"){
			$('.search_result_description .search-results .resources_filter_container').append("<div class='filter-no-result zero_result_found'> <div class='zero_result_text'>"+noResultTextForResourcesFilter+"</div></div>");
			$('.search_result_description .search-results .resources_filter_container .filter-no-result').append('<input class="resources-filter-no-result-identifier" type="hidden" value="NoResultsDuringFilter">');
			resourcesSearchShowMore();
		}
	}
	$('.location-search .search_result_count').removeClass("hide");
		var numberOfResults = resultToBeDisplayed.length;
		resourcesResultCount = numberOfResults;
		$('.search_result_count .resources_result_count').text(numberOfResults);
		if(eventType == "newSearch" )
			resourcesSearchHelper();
	$('#loadingMask').fadeOut(100);
	var responseEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
}
function allCategoryResultCreator(filteredAllResultsArray,allResultsNumberOfResultToBeDisplayed,allCategoryShowMoreCounter){
	$('.faqs_result_count').addClass("hide");
	$('.locations_result_count').addClass("hide");
	$('.products_result_count').addClass("hide");	
	$('.search_result_filters_nonProducts').addClass("hide");
	if(filteredAllResultsArray.length > 0){
		for(var i=allCategoryShowMoreCounter;i<allResultsNumberOfResultToBeDisplayed;i++){
			if(filteredAllResultsArray[i].result_type == "faq_result"){
				if(filteredAllResultsArray[i].faq_question != undefined && filteredAllResultsArray[i].faq_answer != undefined){
					$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>FAQs</span></div><div class='search_result_content_data'><div class='faq_result_data'><a href='javascript:void(0);'>"+filteredAllResultsArray[i].faq_question+"</a><div class='faq_shoeMoreLess_Content'><p>"+filteredAllResultsArray[i].faq_answer+"</p></div></div><a href='javascript:void(0);' class='show_more hide'>Show More</a></div></div>");
				}else if(filteredAllResultsArray[i].faq_question == undefined && filteredAllResultsArray[i].faq_answer != undefined){
					$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>FAQs</span></div><div class='search_result_content_data'><div class='faq_result_data'><a href='javascript:void(0);'></a><div class='faq_shoeMoreLess_Content'><p>"+filteredAllResultsArray[i].faq_answer+"</p></div></div><a href='javascript:void(0);' class='show_more hide'>Show More</a></div></div>");
				}else if(filteredAllResultsArray[i].faq_question != undefined && filteredAllResultsArray[i].faq_answer == undefined){
					$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>FAQs</span></div><div class='search_result_content_data'><div class='faq_result_data'><a href='javascript:void(0);'>"+filteredAllResultsArray[i].faq_question+"</a><div class='faq_shoeMoreLess_Content'><p></p></div></div><a href='javascript:void(0);' class='show_more hide'>Show More</a></div></div>");
				}else if(filteredAllResultsArray[i].faq_question == undefined && filteredAllResultsArray[i].faq_answer == undefined){
					$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>FAQs</span></div><div class='search_result_content_data'><div class='faq_result_data'><a href='javascript:void(0);'></a><div class='faq_shoeMoreLess_Content'><p></p></div></div><a href='javascript:void(0);' class='show_more hide'>Show More</a></div></div>");
				}	
				$('.faq_shoeMoreLess_Content [href^="/content/suntrust/dotcom/us/en"], .sun-faqs-page-list-item-detail [href^="/content/suntrust/dotcom/us/en"]').each(function(e){
					var formattedVal = $(this).attr('href').replace("/content/suntrust/dotcom/us/en",""); 
					formattedVal = formattedVal.replace(".html",""); 
					$(this).attr("href", formattedVal);
				});
				speedBumpCheck($('.sun-faqs-page-list-item-detail'));
				speedBumpCheck($('.faq_shoeMoreLess_Content'));
			}else if(filteredAllResultsArray[i].result_type == "product_result"){
				$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>Products</span></div><div class='search_result_content_data'><a href='"+filteredAllResultsArray[i].product_page+"'>"+filteredAllResultsArray[i].product_title+"</a><p>"+filteredAllResultsArray[i].product_desc+"</p></div></div>");
			}else if(filteredAllResultsArray[i].result_type == "people_result"){
				var locationDiv="";
				var designationCodes = filteredAllResultsArray[i].advisor_designationcodes;
				if(designationCodes != "" && designationCodes != null && designationCodes != undefined){
					designationCodes = designationCodes+", ";
				}else{
					designationCodes = "";
				}
				
				for(var j=0;j<filteredAllResultsArray[i].advisor_addressListArray.addressListArray.length; j++){
				if (startLat != undefined && startLng != undefined) {
					var endLat = filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_latitude;
					var endLng = filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_longitude;
					var milesAway = updateDistance(startLat, startLng, endLat, endLng);
					var milesAwayDiv = "<p>"+milesAway+" Miles</p>";
					var destination="";
					var advisorPageUrl = filteredAllResultsArray[i].advisor_profilePage + "?start="+searchString;
					if(filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_address != "" && filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_address != undefined)
						destination = filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_address;
					if(filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_city != "" && filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_city != undefined){						
						if(destination != "")
							destination = destination +" "+ filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_city;
						else
							destination = filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_city;
					}
					if(filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_state != "" && filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_state != undefined){						
						if(destination != "")
							destination = destination +", "+ filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_state;
						else
							destination = filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_state;
					}
					if(filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_zipcode != "" && filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_zipcode != undefined){						
						if(destination != "")
							destination = destination +" "+ filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_zipcode;
						else
							destination = filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_zipcode;
					}
					if ($(window).width() < 767) {  
						if(/Android/i.test(navigator.userAgent)){
							var getDirectionUrl = "https://www.google.com/maps/dir/?api=1&origin="+searchString+"&destination="+destination+"&mode=d";
						}else if(/iPhone/i.test(navigator.userAgent)){
							var getDirectionUrl = "http://maps.apple.com?saddr="+searchString+"&daddr="+destination+"&directionsmode=transit";
						}else{
							var getDirectionUrl = filteredAllResultsArray[i].advisor_profilePage+"?start="+searchString+"&destination="+destination;
						}
					} else {
						var getDirectionUrl = filteredAllResultsArray[i].advisor_profilePage+"?start="+searchString+"&destination="+destination;
					}					
					/*if(destination != "")
						var getDirectionUrl = filteredAllResultsArray[i].advisor_profilePage+"?start="+searchString+"&destination="+destination;
					else
						var getDirectionUrl = filteredAllResultsArray[i].advisor_profilePage+"?start="+searchString;*/

						
					var getDirectionDiv = "<div class='loction_show_hide_direction clearfix'><div class='loction_show_hide_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png'></div><div class='loction_show_hide_direction_details'><p><a target='_blank' class='get-direction-link-mobile' data-mobile-link='"+getDirectionUrl+"' href='"+getDirectionUrl+"'>Get Directions</a></p></div></div>"
				}else{
					var milesAwayDiv = "";
					var getDirectionDiv = "";
					var advisorPageUrl = filteredAllResultsArray[i].advisor_profilePage;
				}
					locationDiv = locationDiv + "<div class='clearfix'><div class='location_show_hide_addr'><a href='#'><strong>"+filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_location_name+"</strong></a><p>"+filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_address+"</p><p>"+filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_city+", "+filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_state+" "+filteredAllResultsArray[i].advisor_addressListArray.addressListArray[j].adv_zipcode+"</p>"+milesAwayDiv+"</div>"+getDirectionDiv+"</div>";
				}
				
				var adv_Title2 = "";
				if(filteredAllResultsArray[i].advisor_title2 != undefined){
					adv_Title2 = "<p>"+filteredAllResultsArray[i].advisor_title2+"</p>";
				}
				if(filteredAllResultsArray[i].advisor_pictureFilePath != undefined && filteredAllResultsArray[i].advisor_pictureFilePath != ""){
					var profileImageDiv = "<div class='search_result_description_detail_img clearfix'><a title='"+filteredAllResultsArray[i].advisor_title+", "+designationCodes + filteredAllResultsArray[i].advisor_title1+"'href='"+advisorPageUrl+"'><img src='"+filteredAllResultsArray[i].advisor_pictureFilePath+"'></a></div>"
				}else{
					var profileImageDiv = "";
				}
				$('.all_results_filter_container').append("<div class='search_result_content allPeopleresult'><div class='search_result_content_cat'><span>People</span></div><div class='search_result_description_details clearfix'>"+profileImageDiv+"<div class='search_result_details_individual_details'><div><a href='"+advisorPageUrl+"'><strong>"+filteredAllResultsArray[i].advisor_title+", "+designationCodes + filteredAllResultsArray[i].advisor_title1+"</strong></a>"+adv_Title2+"</div><div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_phone.png'></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+filteredAllResultsArray[i].advisor_phone+"'>"+filteredAllResultsArray[i].advisor_phone+"</a></p></div></div><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_email.png'></div><div class='search_result_location_phone_details'><h5><strong>Email</strong></h5><p><a href='mailto:"+filteredAllResultsArray[i].advisor_emailaddress+"'>"+filteredAllResultsArray[i].advisor_emailaddress+"</a></p></div></div><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_contact.png'></div><div class='search_result_location_phone_details'><h5><strong>Contact</strong></h5><p><a target='_blank' href='/dotcom/external?clickedUrl="+filteredAllResultsArray[i].advisor_onlineContactFormUrl+"'>Online Contact Form</a></p></div></div></div><div class='location_tog'><a href='javascript:void(0);' class='location_toggle'><span>+</span><strong> Show Locations</strong></a><div class='location_show_hide' style='display:none'>"+locationDiv+"</div></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_phone.png'></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+filteredAllResultsArray[i].advisor_phone+"'>"+filteredAllResultsArray[i].advisor_phone+"</a></p></div></div><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_email.png'></div><div class='search_result_location_phone_details'><h5><strong>Email</strong></h5><p><a href='mailto:"+filteredAllResultsArray[i].advisor_emailaddress+"'>"+filteredAllResultsArray[i].advisor_emailaddress+"</a></p></div></div><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_contact.png'></div><div class='search_result_location_phone_details'><h5><strong>Contact</strong></h5><p><a target='_blank' href='/dotcom/external?clickedUrl="+filteredAllResultsArray[i].advisor_onlineContactFormUrl+"'>Online Contact Form</a></p></div></div></div></div></div>");
			}else if(filteredAllResultsArray[i].result_type == "location_result"){
				if (startLat != undefined && startLng != undefined) {
					var endLat = filteredAllResultsArray[i].loc_latitude;
					var endLng = filteredAllResultsArray[i].loc_longitude;
					var milesAway = updateDistance(startLat, startLng, endLat, endLng);
					var milesAwayDiv = "<div class='search_result_description_detail_addr'><h5><strong>Miles</strong></h5><div class=addressDistance' latitude='"+endLat+"' longitude='endLng'><div class='miles-away'>"+milesAway+"</div></div></div>";
					if ($(window).width() < 767) {  
						if(/Android/i.test(navigator.userAgent)){
							var getDirectionUrl = "https://www.google.com/maps/dir/?api=1&origin="+searchString+"&destination="+filteredAllResultsArray[i].loc_address+", "+filteredAllResultsArray[i].loc_city+", "+filteredAllResultsArray[i].loc_state_tag.toUpperCase()+", "+filteredAllResultsArray[i].loc_zipcode+"&mode=d";
						}else if(/iPhone/i.test(navigator.userAgent)){
							var getDirectionUrl = "http://maps.apple.com?saddr="+searchString+"&daddr="+filteredAllResultsArray[i].loc_address+", "+filteredAllResultsArray[i].loc_city+", "+filteredAllResultsArray[i].loc_state_tag.toUpperCase()+", "+filteredAllResultsArray[i].loc_zipcode+"&directionsmode=transit";
						}else{
							var getDirectionUrl = filteredAllResultsArray[i].loc_detail_page+"?start="+searchString;
						}
					} else {
						var getDirectionUrl = filteredAllResultsArray[i].loc_detail_page+"?start="+searchString;
					}					
					var locationUrl = filteredAllResultsArray[i].loc_detail_page+"?location="+searchString;
				}else{
					var milesAwayDiv = "";
					var getDirectionUrl = filteredAllResultsArray[i].loc_detail_page;
					var locationUrl = filteredAllResultsArray[i].loc_detail_page;
				}
				$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>Locations</span></div><div class='search_result_content_data'><div class='search_result_description_details clearfix'><div class='search_result_location_name'><div class='address search_result_description_detail_addr'><h5><strong><a target='_self' href='"+locationUrl+"'>"+filteredAllResultsArray[i].loc_locationname+"</a></strong></h5><p>"+filteredAllResultsArray[i].loc_address+"</p><p class='city-state-zipcode'><span class='city'>"+filteredAllResultsArray[i].loc_city+", </span><span class='state'>"+filteredAllResultsArray[i].loc_state_tag.toUpperCase()+" </span><span class='zipcode'> "+filteredAllResultsArray[i].loc_zipcode+"</span></p></div>"+milesAwayDiv+"<div class='search_result_location_contact visible-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+filteredAllResultsArray[i].loc_phone+"'>"+filteredAllResultsArray[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+filteredAllResultsArray[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' class='get-direction-link-mobile' data-mobile-link='"+getDirectionUrl+"' href='"+getDirectionUrl+"'>Get Directions</a></p></div></div></div><div class='search_result_description_detail_service'><h5><strong>Services</strong></h5><div>"+filteredAllResultsArray[i].loc_services+"</div></div></div><div class='search_result_location_working_hours'><div class='search_result_location_branch_hours'><h5><strong>Branch Hours</strong></h5><p>"+filteredAllResultsArray[i].loc_branch_hours+"</p></div><div class='search_result_location_drive_thru_hours'><h5><strong>Drive-Thru Hours</strong></h5><p>"+filteredAllResultsArray[i].loc_drive_in_hours+"</p></div><div class='search_result_location_teller_connect_hours'><h5><strong>Teller Connect Hours</strong></h5><p>"+filteredAllResultsArray[i].loc_teller_hours+"</p></div></div><div class='search_result_location_contact hidden-xs'><div class='search_result_location_phone clearfix'><div class='search_result_location_phone_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_phone_details'><h5><strong>Phone</strong></h5><p><a href='tel:"+filteredAllResultsArray[i].loc_phone+"'>"+filteredAllResultsArray[i].loc_phone+"</a></p></div></div><div class='search_result_location_fax clearfix'><div class='search_result_location_fax_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_cell.png' /></div><div class='search_result_location_fax_details'><h5><strong>Fax</strong></h5><p>"+filteredAllResultsArray[i].loc_fax+"</p></div></div><div class='search_result_location_direction clearfix'><div class='search_result_location_direction_icon'><img   src='/content/dam/suntrust/us/en/elements/2017/graphic-elements/icon_location.png' /></div><div class='search_result_location_direction_details'><p><a target='_blank' href='"+getDirectionUrl+"'>Get Directions</a></p></div></div></div></div></div></div>");
			}else if(filteredAllResultsArray[i].result_type == "resource_result"){
				if(filteredAllResultsArray[i].resource_thumbnail_image != undefined && filteredAllResultsArray[i].resource_thumbnail_image != "")
					$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>Resources</span></div><div class='search_result_content_data clearfix'><div class='search_result_content_img'><a title='"+filteredAllResultsArray[i].resource_title+"' href='"+filteredAllResultsArray[i].resource_page+"'><img src='"+filteredAllResultsArray[i].resource_thumbnail_image+"' /></a></div><div class='search_result_content_img_data'><a title='"+filteredAllResultsArray[i].resource_title+"' href='"+filteredAllResultsArray[i].resource_page+"'>"+filteredAllResultsArray[i].resource_title+"</a><p>"+filteredAllResultsArray[i].resource_desc+"</p> </div></div></div>");
				else
					$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>Resources</span></div><div class='search_result_content_data clearfix'><div class='search_result_content_img_data'><a title='"+filteredAllResultsArray[i].resource_title+"' href='"+filteredAllResultsArray[i].resource_page+"'>"+filteredAllResultsArray[i].resource_title+"</a><p>"+filteredAllResultsArray[i].resource_desc+"</p> </div></div></div>");
			}else if(filteredAllResultsArray[i].result_type == "document_result"){
				if(filteredAllResultsArray[i].hasOwnProperty('doc_description')){
					$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>Document</span></div><div class='search_result_content_data'><a href='"+filteredAllResultsArray[i].doc_path+"'>"+filteredAllResultsArray[i].doc_title+"</a><div class='faq_result_data'><div class='faq_shoeMoreLess_Content'><p>"+filteredAllResultsArray[i].doc_description+"</p></div></div><a href='javascript:void(0);' class='show_more hide'>Show More</a></div></div>");
					}else{
						$('.all_results_filter_container').append("<div class='search_result_content'><div class='search_result_content_cat'><span>Document</span></div><div class='search_result_content_data'><a href='"+filteredAllResultsArray[i].doc_path+"'>"+filteredAllResultsArray[i].doc_title+"</a></div></div>");
					}
			}
		}
		
		showHideLocationToggleForAllResults();
		resultCreatorShowAllResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		
		if(activeTabLowecase=="all_results") {
			$('.search_result_filters_nonProducts').addClass('hide');
			$('.result_filters_other').removeClass('author_hide');
			$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
			$('.search_result_filters_'+activeTabLowecase).removeClass('author_hide');
		}
		allResultAddressToggle();
	}
	else
	{
		resultCreatorShowNoResult();
		resultCreatorShowAllResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		$('.'+activeTabLowecase+'_no_result').removeClass('hide');
		if(activeTabLowecase=="all_results") {
			$('.result_filters_other').addClass('author_hide');
			$('.'+activeTabLowecase+'_filter_container').addClass('hide');
			$('.search_result_filters_'+activeTabLowecase).addClass('author_hide');	
		}
	}

	$('.all_results_filter_container .faq_result_data .faq_shoeMoreLess_Content').each(function() {
		var faq_showMore_Height = $(this).innerHeight();
        if(faq_showMore_Height>40) {
			if($(this).parent().next('.show_more').html()!='View Less') {
				$(this).parent().next('.show_more').removeClass('hide');
				$(this).addClass('faq_showMore');	
			}			
		}
        else {		
			if(faq_showMore_Height==40 && $(this).parent().next('.show_more').html()!='View Less') {}
			else {
				$(this).parent().next('.show_more').addClass('hide');
				$(this).removeClass('faq_showMore');
			}
		}
    });

	$('#loadingMask').fadeOut(100);
	if(window.innerWidth>=768) {
		allCategorySearchShowMore();
	}
	allCategorySearchHelper();

}

function productResultCreator(){
	$('.faqs_result_count').addClass("hide");
	$('.locations_result_count').addClass("hide");
	$('.search_result_filters_nonProducts').addClass("hide");
	if(product_result_object.length > 0){
		$('.products_no_result').addClass("hide");
		//$('.products_filter_container').removeClass("hide");
		for(var i=0;i<product_result_object.length;i++){
			$('.search-results .products_filter_container .search_result_content').append("<div class='search_result_content_data'><a href='"+product_result_object[i].product_page+"'>"+product_result_object[i].product_title+"</a><p>"+product_result_object[i].product_desc+"</p></div>");
		}
		resultCreatorShowFaqResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		
		if(activeTabLowecase=="products") {
			$('.search_result_filters_nonProducts').addClass('hide');
			$('.result_filters_other').removeClass('author_hide');
			$('.'+activeTabLowecase+'_filter_container').removeClass('hide');
			$('.search_result_filters_'+activeTabLowecase).removeClass('author_hide');
		}
	}
	else
	{
		resultCreatorShowNoResult();
		resultCreatorShowFaqResults();
		var activeTab = $("ul.search_select a.active").attr("index");
		var activeTabNoSpace = activeTab.replace(" ", "_");
		var activeTabLowecase = activeTabNoSpace.toLowerCase();
		$('.'+activeTabLowecase+'_no_result').removeClass('hide');
		if(activeTabLowecase=="products") {
			$('.result_filters_other').addClass('author_hide');
			$('.'+activeTabLowecase+'_filter_container').addClass('hide');
			$('.search_result_filters_'+activeTabLowecase).addClass('author_hide');	
		}
	}
	$('.search_result_count .products_result_count').text(product_result_object.length);	
	$('.search_result_count .products_result_count').removeClass("hide");
	$('.location-search .search_result_count').removeClass("hide");
	$('#loadingMask').fadeOut(100);
	//$("div.search_result_content_data").hide();
	$(".search-results .products_filter_container .search_result_content").children().show();
	if(window.innerWidth>=768) {
		$(".search-results .products_filter_container .search_result_content").children().hide();
		productSearchShowMore();
	}
	productSearchHelper();
}

function allCategorySearchHandler(){
	$('.all_results_no_result.zero_result_found').addClass("hide");
	$('.search_result_count').addClass("hide");
	var currentDay = new Date().getDay();
	$('.all_results_filter_container').children().remove();	
	if(searchString == ""){
        $('.all_results_filter_container').addClass("hide");
    }
    if(searchString != ""){
		$.ajax({
			type : "GET",
			url : '/dotcom/search',
			async: false,
			data : {"searchString": searchString, "currentDay":currentDay,"searchingFor":"allResults"},
			success : function(response, textStatus, jqXHR) {
				all_result_object = response['all_results'];
				$('.number-of-results-in-all-category-search').val(all_result_object.length);
				filteredAllResultsArray = all_result_object;
				$('.search_result_categories').removeClass("hide");
				if(allResultsNumberOfResultToBeDisplayed < filteredAllResultsArray.length){
					allCategoryResultCreator(filteredAllResultsArray,allResultsNumberOfResultToBeDisplayed,allCategoryShowMoreCounter);
					irrelaventDivsRemover();
					closedHoursCreator();
					
				}
				else{
					allCategoryResultCreator(filteredAllResultsArray,filteredAllResultsArray.length,allCategoryShowMoreCounter);
					irrelaventDivsRemover();
					closedHoursCreator();
				}
				
				// Start Analytics call
				//passSearchValuesToAnalyticEngine("desktopView");
				// End Analytics call
					
			},
			error: function (xhRequest, ErrorText, thrownError) {
				console.log("Error occured");
				$('#loadingMask').fadeOut(100);
			}
		   });
	}

}

function faqSearchHandler(){
	var currentDay = new Date().getDay();
	$('.faqs_filter_container .sun-faqs-page-list.sun-faqs-page-list-alt').children().remove();	
	if(searchString == ""){
        $('.faqs_filter_container .sun-faqs-page-list.sun-faqs-page-list-alt').hide();
    }
    if(searchString != ""){
		$.ajax({
			type : "GET",
			url : '/dotcom/search',
			async: false,
			data : {"searchString": searchString, "currentDay":currentDay,"searchingFor":"Faqs"},
			success : function(response, textStatus, jqXHR) {
				$('.search_result_categories').removeClass("hide");
				loc_result_object = response['location_results'];
				people_result_object = response['people_results'];
				faq_result_object = response['faq_results'];
				faqResultCreator();
				
			},
			error: function (xhRequest, ErrorText, thrownError) {
				console.log("Error occured");
				$('#loadingMask').fadeOut(100);
			}
		   });
	}
}

function productSearchHandler(){
	var currentDay = new Date().getDay();
	$('.products_filter_container .search_result_content').children().remove();	
	if(searchString == ""){
        $('.products_filter_container .search_result_content').hide();
    }
	var productTag=getParameterByName("productTag");
    if(searchString != ""){
		$.ajax({
			type : "GET",
			url : '/dotcom/search',
			async: false,
			data : {"searchString": searchString, "currentDay":currentDay, "productTag":productTag, "searchingFor":"Products"},
			success : function(response, textStatus, jqXHR) {
				$('.search_result_categories').removeClass("hide");
				loc_result_object = response['location_results'];
				people_result_object = response['people_results'];
				faq_result_object = response['faq_results'];
				product_result_object = response['product_results'];
				productResultCreator();
				
			},
			error: function (xhRequest, ErrorText, thrownError) {
				console.log("Error occured");
				$('#loadingMask').fadeOut(100);
			}
		   });
	}
}



function refineFilterResources(eventType) {
	$('#loadingMask').show();
	numberOfResultToBeDisplayedResources = parseInt($('.resources_result_show_more_results a').attr("data-search-visible-items-limit"));
	resourcesShowMoreCounter = 0;
	var valueFromResourcesTypeCheckBox = [];
	var valueFromContentTypeCheckBox = [];
	var refinedContentTypeResult = [];
	var refinedResourcesResult = [];
	var checkedResourcesFlag = false;
	var checkedContentTypeFlag = false;
	var uncheckedResourcesFlag = false;
	var uncheckedContentTypeFlag = false;
	var contentTypeFlag = false;
	var resourcesFlag = false;
	var resultTypeSingleDimentionCountResources = 0;
	var refinedResultSingleDimensionResources = [];
	$('.search_result_description .search-results .resources_filter_container .filter-no-result').remove();
	$('.search_result_description .search-results .resources_filter_container .search_result_content').children().remove();
	$('.location-search .search_result_count').addClass("hide");
	if (window.innerWidth < 768) {
		$('.resources_filter_mobile .resource-type input').each(function(e) {
			if ($(this).is(':checked')) {
				checkedResourcesFlag = true;
				if(!$(this).hasClass("dontAdd")){
					valueFromResourcesTypeCheckBox.push($(this).val());
				}
			}
			if (!($(this).is(':checked'))) {
				uncheckedResourcesFlag = true;
			}
		});
		$('.resources_filter_mobile .article-type input').each(function(e) {
			if ($(this).is(':checked')) {
				checkedContentTypeFlag = true;
				valueFromContentTypeCheckBox.push($(this).val());
			}
			if (!($(this).is(':checked'))) {
				uncheckedContentTypeFlag = true;
			}
		});
	}
	if (window.innerWidth >= 768) {
		$('.resources_filter_desktop .resource-type input').each(function(e) {
			if ($(this).is(':checked')) {
				checkedResourcesFlag = true;
				if(!$(this).hasClass("dontAdd")){
					valueFromResourcesTypeCheckBox.push($(this).val());
				}
			}
			if (!($(this).is(':checked'))) {
				uncheckedResourcesFlag = true;
			}
		});
		$('.resources_filter_desktop .article-type input').each(function(e) {
			if ($(this).is(':checked')) {
				checkedContentTypeFlag = true;
				valueFromContentTypeCheckBox.push($(this).val());
			}
			if (!($(this).is(':checked'))) {
				uncheckedContentTypeFlag = true;
			}
		});	
	}
	// None checked
	if (checkedContentTypeFlag == false && checkedResourcesFlag == false) {
		filteredResourcesArray = resources_result_object;
		filteredResourcesArray = removeDuplicate(filteredResourcesArray,"resource_page");
		resourcesResultCreator(filteredResourcesArray, eventType,numberOfResultToBeDisplayedResources, resourcesShowMoreCounter);
	}else if (checkedResourcesFlag == false || checkedContentTypeFlag == false) {
		var noMatchingResults = [];
		filteredResourcesArray = noMatchingResults;
		resourcesResultCreator(filteredResourcesArray, eventType, numberOfResultToBeDisplayedResources, resourcesShowMoreCounter);
	}
	// Resources, Content Type checked
	if (checkedResourcesFlag == true && checkedContentTypeFlag == true) {
		if (uncheckedContentTypeFlag == true) {
			contentTypeFlag = true;
			for (var j = 0; j < resources_result_object.length; j++) {
				for (var k = 0; k < valueFromContentTypeCheckBox.length; k++) {
					refinedContentTypeResult
							.push($(resources_result_object[j])
									.filter(
											function(i, n) {
												return n.resource_content_type === valueFromContentTypeCheckBox[k]
											}));
				}
			}
		} else if (uncheckedContentTypeFlag == false) {
			refinedContentTypeResult = resources_result_object;
			resultTypeSingleDimentionCountResources = resultTypeSingleDimentionCountResources + 1;
		}
		if (uncheckedResourcesFlag == true) {
			if (contentTypeFlag == true) {
				resourcesFlag = true;
				for (var j = 0; j < refinedContentTypeResult.length; j++) {
					for (var p = 0; p < refinedContentTypeResult[j].length; p++) {
						for (var k = 0; k < valueFromResourcesTypeCheckBox.length; k++) {
							refinedResourcesResult
									.push($(refinedContentTypeResult[j][p])
											.filter(
													function(i, n) {
														if (n.resource_primary_tag
																.toString()
																.indexOf(
																		valueFromResourcesTypeCheckBox[k]) >= 0
																|| n.resource_secondary_tag
																		.toString()
																		.indexOf(
																				valueFromResourcesTypeCheckBox[k]) >= 0)
															return true;
														else
															return false;
													}));
						}
					}
				}
			} else if (contentTypeFlag == false) {
				for (var j = 0; j < refinedContentTypeResult.length; j++) {
					for (var k = 0; k < valueFromResourcesTypeCheckBox.length; k++) {
						refinedResourcesResult
								.push($(refinedContentTypeResult[j])
										.filter(
												function(i, n) {
													if (n.resource_primary_tag
															.toString()
															.indexOf(
																	valueFromResourcesTypeCheckBox[k]) >= 0
															|| n.resource_secondary_tag
																	.toString()
																	.indexOf(
																			valueFromResourcesTypeCheckBox[k]) >= 0)
														return true;
													else
														return false;
												}));
					}
				}
			}
		} else {
			refinedResourcesResult = refinedContentTypeResult;
			resultTypeSingleDimentionCountResources = resultTypeSingleDimentionCountResources + 1;
		}
		if (resultTypeSingleDimentionCountResources == 2) {
			filteredResourcesArray = refinedResourcesResult;
			filteredResourcesArray = removeDuplicate(filteredResourcesArray,"resource_page");
			resourcesResultCreator(filteredResourcesArray, eventType,numberOfResultToBeDisplayedResources,resourcesShowMoreCounter);
		} else {
			for (var i = 0; i < refinedResourcesResult.length; i++) {
				for (var j = 0; j < refinedResourcesResult[i].length; j++) {
					refinedResultSingleDimensionResources.push(refinedResourcesResult[i][j]);
				}
			}
			filteredResourcesArray = refinedResultSingleDimensionResources;
			filteredResourcesArray = removeDuplicate(filteredResourcesArray,"resource_page");
			resourcesResultCreator(filteredResourcesArray, eventType,numberOfResultToBeDisplayedResources,resourcesShowMoreCounter);
		}
	}
	
}

$(document).on("change",".resources_filter_desktop .resource-type input, .resources_filter_desktop .article-type input", function(e) {
	var eventType="refineResult";
	var classLevel = $(this).parents().hasClass("search_detail_services_level1");
	var checkCondition=false;
	var resourceFilterFlag=true;
	var numberOfLevel1Tags = $(this).parents(".search_detail_services_level1").find("input").length;
    var uncheckedFieldCounter=0;
    if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
		if($(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').is(':visible')) {
			$(this).parents('.sun-checkbox-input-field').next().find('.sun-checkbox-input-field span').removeClass('sun-checked');
			$(this).parents('.sun-checkbox-input-field').next().find('.sun-checkbox-input-field span input').prop("checked",false);
			$(this).parents('.sun-checkbox-input-field').next().slideToggle();
		}
		else {
			$(this).parents('.sun-checkbox-input-field').next().slideToggle();
			$(this).parents('.sun-checkbox-input-field').next().find('.sun-checkbox-input-field span').addClass('sun-checked');
			$(this).parents('.sun-checkbox-input-field').next().find('.sun-checkbox-input-field span input').prop("checked",true);
		}
	}
	if(classLevel){

        $(this).parents(".search_detail_services_level1").find("input").each(function(){
			checkCondition = $(this).is(":checked");
			if(checkCondition == false){
				$(this).parents(".search_detail_services_level1").prev().find("input").addClass("dontAdd");
				resourceFilterFlag=false;
                uncheckedFieldCounter = uncheckedFieldCounter+1;
			}
			
		});
		if(uncheckedFieldCounter == numberOfLevel1Tags)
        	resourceFilterFlag = true;
		if(resourceFilterFlag){
			$(this).parents(".search_detail_services_level1").find("input").each(function(){
				$(this).parents(".search_detail_services_level1").prev().find("input").removeClass("dontAdd");
			});		
		}
	}
	
	refineFilterResources(eventType);
});

function resourcesFilterCreator(filteredResourcesArray){
$('.resources_filter_desktop .search_filter_result_cat.resource-type .search_detail_services_level0').children().remove();
$('.resources_filter_mobile .filter_tog.resource-type .filter_show_hide').children().remove();
$('.resources_filter_desktop .search_filter_result_cat.article-type .article-type-level-1').children().remove();
$('.resources_filter_mobile .filter_tog.article-type .filter_show_hide').children().remove();
var resourcesLevel1PrimaryTags=[];
var resourcesLevel2PrimaryTags=[];
var resourcesContentType=[];
	for(var i = 0; i < filteredResourcesArray.length; i++ ){
		var primaryTag = filteredResourcesArray[i].resource_primary_tag;
		var secondaryTag = filteredResourcesArray[i].resource_secondary_tag;
		var contentType = filteredResourcesArray[i].resource_content_type;
		if(primaryTag != undefined && primaryTag != "" && primaryTag != null){
			if(primaryTag.indexOf('/')>0){
				var primaryTagSplited = primaryTag.split('/');
				if(resourcesLevel1PrimaryTags.indexOf(primaryTagSplited[0])==-1)
					resourcesLevel1PrimaryTags.push(primaryTagSplited[0]);
				if(resourcesLevel2PrimaryTags.indexOf(primaryTagSplited[0]+"/"+primaryTagSplited[1])==-1)
					resourcesLevel2PrimaryTags.push(primaryTagSplited[0]+"/"+primaryTagSplited[1]);	
			}else{
				if(resourcesLevel1PrimaryTags.indexOf(primaryTag)==-1)
					resourcesLevel1PrimaryTags.push(primaryTag);
			}
		}
		if(secondaryTag != undefined && secondaryTag != "" && secondaryTag != null){
			if(secondaryTag.indexOf(",") != -1){
				var secondaryTagSplited = [];
				secondaryTagSplited = secondaryTag.split(",");
				for(var k=0; k<secondaryTagSplited.length;k++){
					if(secondaryTagSplited[k].indexOf('/')>0){
						var secondaryTagSplitedTemp = secondaryTagSplited[k].split('/');
						if(resourcesLevel1PrimaryTags.indexOf(secondaryTagSplitedTemp[0])==-1)
							resourcesLevel1PrimaryTags.push(secondaryTagSplitedTemp[0]);
						if(resourcesLevel2PrimaryTags.indexOf(secondaryTagSplitedTemp[0]+"/"+secondaryTagSplitedTemp[1])==-1)
							resourcesLevel2PrimaryTags.push(secondaryTagSplitedTemp[0]+"/"+secondaryTagSplitedTemp[1]);	
					}else{
						if(resourcesLevel1PrimaryTags.indexOf(secondaryTagSplited[k])==-1)
							resourcesLevel1PrimaryTags.push(secondaryTagSplited[k]);
					}
				}
			}else if(secondaryTag.indexOf('/')>0){
				var secondaryTagSplitedTemp = secondaryTag.split('/');
				if(resourcesLevel1PrimaryTags.indexOf(secondaryTagSplitedTemp[0])==-1)
					resourcesLevel1PrimaryTags.push(secondaryTagSplitedTemp[0]);
				if(resourcesLevel2PrimaryTags.indexOf(secondaryTagSplitedTemp[0]+"/"+secondaryTagSplitedTemp[1])==-1)	
					resourcesLevel2PrimaryTags.push(secondaryTagSplitedTemp[0]+"/"+secondaryTagSplitedTemp[1]);	
			}else{
				if(resourcesLevel1PrimaryTags.indexOf(secondaryTag)==-1)
					resourcesLevel1PrimaryTags.push(secondaryTag);
			}
		}
		if(contentType != undefined && contentType != "" && contentType != null){
			if(resourcesContentType.indexOf(contentType)==-1)
				resourcesContentType.push(contentType);
		}
	}
	
	for(var i = 0; i < resourcesLevel1PrimaryTags.length; i++ ){
		$('.resources_filter_desktop .search_filter_result_cat.resource-type .search_detail_services_level0').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' class='suntrust-checkbox' value='"+resourcesLevel1PrimaryTags[i]+"' name='"+resourcesLevel1PrimaryTags[i]+"' /></span></div><label class='suntrust-checkbox-label'>"+resourcesLevel1PrimaryTags[i]+" <span class='resource-child-filter-count'></span></label></div>");
		$('.resources_filter_mobile .filter_tog.resource-type .filter_show_hide').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' class='suntrust-checkbox' value='"+resourcesLevel1PrimaryTags[i]+"' name='"+resourcesLevel1PrimaryTags[i]+"' /></span></div><label class='suntrust-checkbox-label'>"+resourcesLevel1PrimaryTags[i]+" <span class='resource-child-filter-count'></span></label></div>");
	}
	for(var j = 0; j < resourcesLevel2PrimaryTags.length; j++ ){
		//desktop implementaion
		$('.resources_filter_desktop .search_filter_result_cat.resource-type input').each(function (e){
			if(resourcesLevel2PrimaryTags[j].indexOf($(this).val()) != -1){
				if($(this).parents('.sun-checkbox-input-field').next().attr("class") != undefined && $(this).parents('.sun-checkbox-input-field').next().attr("class").indexOf("search_detail_services_level1") != -1){
					var resourcesLevel2PrimaryTagsTemp = resourcesLevel2PrimaryTags[j].split("/")[1];
					$(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' class='suntrust-checkbox' value='"+resourcesLevel2PrimaryTags[j]+"' name='"+resourcesLevel2PrimaryTagsTemp+"' /></span></div><label class='suntrust-checkbox-label'>"+resourcesLevel2PrimaryTagsTemp+" </label></div>");
				}else{
					if(!$(this).parents().hasClass('search_detail_services_level1')){
						var resourcesLevel2PrimaryTagsTemp = resourcesLevel2PrimaryTags[j].split("/")[1];
						$(this).parents('.sun-checkbox-input-field').after("<div class='search_detail_services_level1' ><div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' class='suntrust-checkbox' value='"+resourcesLevel2PrimaryTags[j]+"' name='"+resourcesLevel2PrimaryTagsTemp+"'/></span></div><label class='suntrust-checkbox-label'>"+resourcesLevel2PrimaryTagsTemp+"</label></div></div>");
					}
				}
			}
		});
		//mobile implementaion
		$('.resources_filter_mobile .filter_tog.resource-type .filter_show_hide input').each(function (e){
			if(resourcesLevel2PrimaryTags[j].indexOf($(this).val()) != -1){
				if($(this).parents('.sun-checkbox-input-field').next().attr("class") != undefined && $(this).parents('.sun-checkbox-input-field').next().attr("class").indexOf("search_detail_services_level1") != -1){
					var resourcesLevel2PrimaryTagsTemp = resourcesLevel2PrimaryTags[j].split("/")[1];
					$(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' class='suntrust-checkbox' value='"+resourcesLevel2PrimaryTags[j]+"' name='"+resourcesLevel2PrimaryTagsTemp+"' /></span></div><label class='suntrust-checkbox-label'>"+resourcesLevel2PrimaryTagsTemp+" </label></div>");
				}else{
					if(!$(this).parents().hasClass('search_detail_services_level1')){
						var resourcesLevel2PrimaryTagsTemp = resourcesLevel2PrimaryTags[j].split("/")[1];
						$(this).parents('.sun-checkbox-input-field').after("<div class='search_detail_services_level1' ><div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' class='suntrust-checkbox' value='"+resourcesLevel2PrimaryTags[j]+"' name='"+resourcesLevel2PrimaryTagsTemp+"'/></span></div><label class='suntrust-checkbox-label'>"+resourcesLevel2PrimaryTagsTemp+"</label></div></div>");
					}
				}
			}
		});
	}
	//desktop implementation
	$('.resources_filter_desktop .search_filter_result_cat.resource-type .search_detail_services_level0 .sun-checkbox-input-field').each(function(e){
		if($(this).next().attr("class") != undefined && $(this).next().attr("class").indexOf("search_detail_services_level1")!=-1){
		 var childFilterCount = $(this).next().children().length;
			$(this).find('.resource-child-filter-count').text("("+childFilterCount+")");
		}else{
			$(this).find('.resource-child-filter-count').remove();
		}
	});
	//mobile implementation
	$('.resources_filter_mobile .filter_tog.resource-type .filter_show_hide .sun-checkbox-input-field').each(function(e){
		if($(this).next().attr("class") != undefined && $(this).next().attr("class").indexOf("search_detail_services_level1")!=-1){
		 var childFilterCount = $(this).next().children().length;
			$(this).find('.resource-child-filter-count').text("("+childFilterCount+")");
		}else{
			$(this).find('.resource-child-filter-count').remove();
		}
	});
	var number_of_resources_tags = $('.resources_filter_desktop .search_filter_result_cat.resource-type input').length;
	var number_of_resources_tags_mobile = $('.resources_filter_mobile .filter_tog.resource-type .filter_show_hide input').length;
	$('.resources_filter_desktop .search_filter_result_cat.resource-type h4 strong span').text(number_of_resources_tags);
	$('.resources_filter_mobile .filter_tog.resource-type span.resources-count').text(number_of_resources_tags_mobile);
	for(var i = 0; i < resourcesContentType.length; i++ ){
		$('.resources_filter_desktop .search_filter_result_cat.article-type .article-type-level-1').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' class='suntrust-checkbox' value='"+resourcesContentType[i]+"' name='"+resourcesContentType[i]+"' /></span></div><label class='suntrust-checkbox-label'>"+resourcesContentType[i]+"</label></div>");
		$('.resources_filter_mobile .filter_tog.article-type .filter_show_hide').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' class='suntrust-checkbox' value='"+resourcesContentType[i]+"' name='"+resourcesContentType[i]+"' /></span></div><label class='suntrust-checkbox-label'>"+resourcesContentType[i]+"</label></div>");
	}
	var number_of_content_tags = $('.resources_filter_desktop .search_filter_result_cat.article-type .article-type-level-1 input').length;
	var number_of_content_tags_mobile = $('.resources_filter_mobile .filter_tog.article-type .filter_show_hide input').length;
	$('.resources_filter_desktop .search_filter_result_cat.article-type h4 strong span').text(number_of_content_tags);
	$('.resources_filter_mobile .filter_tog.article-type span.article-count').text(number_of_content_tags_mobile);
}
function resourcesSearchHandler(){
	var currentDay = new Date().getDay();
	$('.resources_filter_container .search_result_content').children().remove();	
	if(searchString == ""){
		$('.resources_filter_container .search_result_content').hide();
	}
	var resourcesTag=getParameterByName("resourcesTag");
	if(searchString != ""){
		$.ajax({
			type : "GET",
			url : '/dotcom/search',
			data : {"searchString": searchString, "currentDay":currentDay, "resourcesTag":resourcesTag, "searchingFor":"Resource"},
			success : function(response, textStatus, jqXHR) {
				$('.search_result_categories').removeClass("hide");
				resources_result_object = response['resource_results'];
				filteredResourcesArray = resources_result_object;
				resourcesFilterCreator(filteredResourcesArray);
				selectAllFiltersResources();
				resourcesResultCreator(resources_result_object, "newSearch", numberOfResultToBeDisplayedResources, resourcesShowMoreCounter);	
			},
			error: function (xhRequest, ErrorText, thrownError) {
				console.log("Error occured");
				$('#loadingMask').fadeOut(100);
			}
		   });
	}
}
function locationSearchHandler(selecteRadiusParam, eventType, selectedLocationService){
	if(!searchButtonClickInLocationTab){
		var tempLocationServices = getParameterByName("locationServices");
		if(tempLocationServices != null && tempLocationServices != "" && tempLocationServices != undefined && tempLocationServices != "undefined")
			selectedLocationService = tempLocationServices;
	}
	
	locationShowMoreCounter = 0;
	numberOfResultToBeDisplayed = parseInt($('.location_result_show_more_results a').attr('data-search-visible-items-limit'));
	var servicesFilterSelectedValues=[];
	var stateFilterSelectedValues=[];
	var cityFilterSelectedValues=[];
	var desktopServicesFilterSelectedValues=[];
	var desktopStateFilterSelectedValues=[];
	var desktopCityFilterSelectedValues=[];
    servicesFilterSelectedValues = selectedFilterFinder('.locations_filter_mobile .mobile-services');
    stateFilterSelectedValues = selectedFilterFinder('.locations_filter_mobile .mobile-state');
    cityFilterSelectedValues = selectedFilterFinder('.locations_filter_mobile .mobile-city');
    desktopCityFilterSelectedValues = selectedFilterFinder('.locations_filter_desktop .search_detail_city');
    desktopStateFilterSelectedValues = selectedFilterFinder('.locations_filter_desktop .search_detail_state');
    desktopServicesFilterSelectedValues = selectedFilterFinder('.locations_filter_desktop .search_detail_services');
    
	
    if(eventType =="refineResult"){
    	loc_result_object_full_response="";
    }else if(eventType =="newSearch"){
    	loc_result_object_full_response="";
    	loc_result_object="";
    }

	MqAndAEMResults = [];
	MqOnlyResults=[];
	AemOnlyResults=[];
    	$('.locations_no_result').addClass("hide");
	$('.faqs_no_result.zero_result_found').addClass("hide");
    	$('.locations_filter_desktop .search_result_filters.col-lg-4.col-md-4.col-sm-4.hidden-xs').hide(); 
	$('.location_result_show_more_results').addClass("hide");	
	$('.search_result_show_more_results').addClass("hide");
	$('.location-search .search_result_count').addClass("hide");
	$('.search_result_description .search-results .locations_filter_container .filter-no-result').remove();
	$('.search_result_description .search-results .locations_filter_container .location_mq_results').children().remove();
	$('.search_result_description .search-results .locations_filter_container .location_aem_results').children().remove();
	$('.locations_filter_desktop').hide();
	$('.locations_filter_mobile').addClass("hide");
	$('.locations_filter_container').addClass("hide");
    
    	$('.locations_filter_desktop .search_by_filter.search_detail_services .search_detail_services_level0').children().remove();
	$('.locations_filter_desktop .search_detail_state .search_detail_state_city_select').children().remove();
	$('.locations_filter_desktop .search_detail_city .search_detail_state_city_select').children().remove();
    	$('.locations_filter_mobile .filter_tog .mobile-services').children().remove();
	$('.locations_filter_mobile .filter_tog .mobile-state').children().remove();
    	$('.locations_filter_mobile .filter_tog .mobile-city').children().remove();
	$('.locations_filter_desktop .search_by_filter.search_detail_location_radius').show();
	$('.locations_filter_mobile .mobile-radius-filter').show();
	var currentDay = new Date().getDay();
	var mapQuestResultKey = [];
	var mqPostalCodesArray=[];
	var mqPostalCodesString="";
	refinedRadiusResult=[];
	var checkedServiceFlag = false;
	var checkedStateFlag = false;
	var checkedCityFlag = false;
	var selectedRadius = selecteRadiusParam;
	var loc_result_length = loc_result_object.length;
	var flag;
	if(searchString == ""){
		$('.locations_filter_desktop').hide();
		$('.locations_filter_mobile').addClass("hide");
		$('.search_result_description .search-results .locations_filter_container').addClass("hide");
	}
	if(searchString != ""){
		$('#loadingMask').show().focus();
		var mapQuestURL = 'https://www.mapquestapi.com/search/v2/radius?origin='+searchString+'&radius='+selectedRadius+'&maxMatches=500&ambiguities=ignore&hostedData=mqap.32547_SunTrust_Branch_Loc&outFormat=json&key=Gmjtd|lu6zn1ua2d,70=o5-l0850';
		$.ajax({
				type : "GET",
				url : mapQuestURL,
				async: false,
				success : function(responseMapQuest, textStatus, jqXHR) {
					if(responseMapQuest.info.statusCode != '500'){
						mapQuestResultCount = parseInt(responseMapQuest['resultsCount']);
						if(mapQuestResultCount != 0 && responseMapQuest['searchResults'] != undefined){
							for(var i=0; i<responseMapQuest['searchResults'].length; i++){
								var latitude = responseMapQuest['searchResults'][i].fields.lat;
								var longitude = responseMapQuest['searchResults'][i].fields.lng;
								mqPostalCodesArray[i]=responseMapQuest['searchResults'][i].fields.postal;
								if(latitude != "" && latitude != "null" && latitude != null && longitude != "null" && longitude != null && longitude != "")								
									mapQuestResultKey[i]=latitude.toString()+longitude.toString();
								else
									mapQuestResultKey[i]=0;
							}
							mapQuestResultKey = $.unique(mapQuestResultKey);
							mqPostalCodesArray = $.unique(mqPostalCodesArray); 
							for(var i=0;i<mqPostalCodesArray.length;i++){
								if(mqPostalCodesString.length == 0)
									mqPostalCodesString = mqPostalCodesArray[i];
								else
									mqPostalCodesString = mqPostalCodesString + "," +mqPostalCodesArray[i];
							}
						}
						
						if(loc_result_object_full_response.length == 0){
							mqPostalCodesString = "mqPostalCodeData"+mqPostalCodesString;
							$.ajax({
								type : "GET",
								url : '/dotcom/search',
								async: false,
								data : {"searchString": mqPostalCodesString, "currentDay":currentDay, "LocationService":selectedLocationService, "searchingFor":"Locations"},
								success : function(response, textStatus, jqXHR) {
									loc_result_object_full_response = response['location_results'];
									var loc_result_full_response_length = loc_result_object_full_response.length;
									if(startLat != undefined && startLng != undefined){
										for(var i=0; i<loc_result_full_response_length; i++){
											var endLat = loc_result_object_full_response[i].loc_latitude;
											var endLng = loc_result_object_full_response[i].loc_longitude;
											var milesAway = updateDistance(startLat, startLng, endLat, endLng);
											loc_result_object_full_response[i].loc_miles_away = milesAway;			
										}
									}
									if(loc_result_object.length == 0){								
										$.ajax({
											type : "GET",
											url : '/dotcom/search',
											async: false,
											data : {"searchString": searchString, "currentDay":currentDay, "LocationService":selectedLocationService, "searchingFor":"Locations"},
											success : function(response, textStatus, jqXHR) {
												var startTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
												loc_result_object = response['location_results'];
												var loc_result_length = loc_result_object.length;
												if(startLat != undefined && startLng != undefined){
													for(var i=0; i<loc_result_length; i++){
														var endLat = loc_result_object[i].loc_latitude;
														var endLng = loc_result_object[i].loc_longitude;
														var milesAway = updateDistance(startLat, startLng, endLat, endLng);
														loc_result_object[i].loc_miles_away = milesAway;
													}
												}
				    	                        if(mapQuestResultCount != 0 && loc_result_object_full_response.length > 0){
				    	                        	for (var k=0;k<mapQuestResultKey.length;k++){
				    	    	                        refinedRadiusResult.push($(loc_result_object_full_response).filter(function (i,n){return n.loc_latitude_longitude === mapQuestResultKey[k]}));           
				    	    	                    }
				    	                        	var MQaemKey = [];
					    	                        for(var i=0; i<refinedRadiusResult.length;i++){	
					    	                        	for(var j=0; j<refinedRadiusResult[i].length;j++)
						    	                        	MQaemKey.push(refinedRadiusResult[i][j].loc_detail_page);
					    	                        }
					    	                        
					    	                        for(var i=0;i<loc_result_object.length;i++){
					    	                        	for(var j=0;j<MQaemKey.length;j++){
					    	                        		if(MQaemKey[j] != loc_result_object[i].loc_detail_page){
					    	                        			flag="notPresent";
					    	                        		}else{
					    	                        			flag = "present";
					    	                        			break;
					    	                        		}
					    	                        	}
					    	                        	if(flag=="notPresent"){
					    	                        		loc_result_object[i].loc_result_type = 'AEM';
					    	                        		AemOnlyResults.push(loc_result_object[i]);
					    	                        	}
					    	                        }
					    	                        
					    	                        for(var i=0; i<refinedRadiusResult.length;i++){	
					    	                        	for(var j=0; j<refinedRadiusResult[i].length;j++){
					    	                        		refinedRadiusResult[i][j].loc_result_type = 'MQ';
					    	                        		MqAndAEMResults.push(refinedRadiusResult[i][j]);
					    	                        		MqOnlyResults.push(refinedRadiusResult[i][j]);
					    	                        	}
					    	                        }
					    	                        for(var i=0; i<AemOnlyResults.length;i++){	
					    	                        	MqAndAEMResults.push(AemOnlyResults[i]);           
				    	    	                    }
				    	                        } else if(mapQuestResultCount == 0 || loc_result_object_full_response.length == 0){
													for(var i=0; i<loc_result_object.length;i++){	
					    	                        	loc_result_object[i].loc_result_type = 'AEM';
					    	                        	AemOnlyResults.push(loc_result_object[i]);           
				    	    	                    }
				    	                        	MqAndAEMResults = AemOnlyResults;           
				    	                        }
				    	                        
				    	                        /**/
				    	                        /*Populate Service Filter Options - Starts*/
				    	                    	var servicesArray = [];
				    	                    	var finalServicesArray = [];
				    	                    	var uniqueServicesArray = [];
				    	                    	var duplicateServicesArray = [];
				    	                    	var servicesTagArray = [];
				    	                    	var finalServicesTagArray = [];
				    	                    	var uniqueServicesTagArray = [];
				    	                    	var duplicateServicesTagArray = [];
												var statesArray = [];
				    	                    	var uniqueStatesArray = [];
												var cityArray = [];
				    	                    	var uniqueCityArray = [];
												var filterStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
				    	                    	for(var i = 0; i < MqAndAEMResults.length; i++ ){
				    	                    		servicesArray[i]=MqAndAEMResults[i].loc_services;
													servicesTagArray[i]=MqAndAEMResults[i].loc_services_tag;
				    	                    		for(var k = 0; k < servicesArray[i].length; k++ ){
														if(uniqueServicesArray.indexOf(servicesArray[i][k].trim())==-1)
															uniqueServicesArray.push(servicesArray[i][k].trim());
				    	                    		}
				    	                    		for(var j = 0; j < servicesTagArray[i].length; j++ ){
				    	                    			if(uniqueServicesTagArray.indexOf(servicesTagArray[i][j])==-1)
															uniqueServicesTagArray.push(servicesTagArray[i][j]);
				    	                    		}
													
													/**/
													
													statesArray[i]=MqAndAEMResults[i].loc_state+":"+MqAndAEMResults[i].loc_state_tag;
													$.each(statesArray, function(i, el){
														if($.inArray(el, uniqueStatesArray) === -1) uniqueStatesArray.push(el);
													});
													
													/**/
													
													cityArray[i]=MqAndAEMResults[i].loc_city;
			    	                    			$.each(cityArray, function(i, el){
			    	                    				if($.inArray(el, uniqueCityArray) === -1) uniqueCityArray.push(el);
			    	                    			});
				    	                    	}
												var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
																								
				    	                    	uniqueServicesArray.sort();
				    	                    	uniqueServicesTagArray.sort();
												uniqueStatesArray.sort();
												uniqueCityArray.sort();
												var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
				    	                    	for(var servicesCount = 0; servicesCount < uniqueServicesArray.length; servicesCount++){
				    	                    		$('.locations_filter_desktop .search_by_filter.search_detail_services .search_detail_services_level0').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueServicesTagArray[servicesCount]+"' class='suntrust-checkbox' value='"+uniqueServicesTagArray[servicesCount]+"' name='"+uniqueServicesTagArray[servicesCount]+"' aria-label='"+uniqueServicesTagArray[servicesCount]+"' /></span></div><label for='"+uniqueServicesTagArray[servicesCount]+"'class='suntrust-checkbox-label'>"+uniqueServicesArray[servicesCount]+"</label></div>");
				    	                    		$('.mobile_filter_search .locations_filter_mobile .mobile-services').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueServicesTagArray[servicesCount]+"' class='suntrust-checkbox' value='"+uniqueServicesTagArray[servicesCount]+"' name='"+uniqueServicesTagArray[servicesCount]+"' aria-label='"+uniqueServicesTagArray[servicesCount]+"' /></span></div><label for='"+uniqueServicesTagArray[servicesCount]+"'class='suntrust-checkbox-label'>"+uniqueServicesArray[servicesCount]+"</label></div>");
				    	                    	}
				    	                    	
				    	                    	$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){
				    	                    		if(($(this).val() == "branch")){
				    	                    			$(this).parent().parent().parent().after("<div class='desktop search_detail_services_level1'></div>")
				    	                    		}
				    	                    	});
				    	                    	$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){                  
				    	                    		if(($(this).val() == "drive-thru-banking") || ($(this).val() == "weekend-hours") || ($(this).val() == "instore-branch")){
				    	                    			var divData = $(this).parent().parent().parent().detach();
				    	                    			$(".locations_filter_desktop .desktop.search_detail_services_level1").append(divData);
				    	                    		}
				    	                    	});
				    	                    	$('.locations_filter_mobile .mobile-services-tog input').each(function(e){
				    	                    		if(($(this).val() == "branch")){
				    	                    			$(this).parent().parent().parent().after("<div class='mobile search_detail_services_level1'></div>")
				    	                    		}
				    	                    	});
				    	                    	$('.locations_filter_mobile .mobile-services-tog input').each(function(e){                  
				    	                    		if(($(this).val() == "drive-thru-banking") || ($(this).val() == "weekend-hours") || ($(this).val() == "instore-branch")){
				    	                    			var divData = $(this).parent().parent().parent().detach();
				    	                    			$(".locations_filter_mobile .mobile.search_detail_services_level1").append(divData);
				    	                    		}
				    	                    	});
				    	                    	if($(".locations_filter_mobile .search_detail_services_level1").children().length == 0){
				    	                    		$(".locations_filter_mobile .search_detail_services_level1").remove();
				    	                    	}
				    	                    	if($(".locations_filter_desktop .search_detail_services_level1").children().length == 0){
				    	                    		$(".locations_filter_desktop .search_detail_services_level1").remove();
				    	                    	}
				    	                    	/*Populate Service Filter Options - Ends*/
				    	                    	
				    	                    	/*Populate State Filter Options - Starts*/
				    	                    	
				    	                    	
				    	                    	for(var statesCount = 0; statesCount < uniqueStatesArray.length; statesCount++){
				    	                    		var stateName = uniqueStatesArray[statesCount].substring(0,uniqueStatesArray[statesCount].indexOf(":"));
				    	                    		var stateValue= uniqueStatesArray[statesCount].substring(uniqueStatesArray[statesCount].indexOf(":")+1,uniqueStatesArray[statesCount].length);
				    	                    		$('.locations_filter_desktop .search_detail_state .search_detail_state_city_select').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+stateName+"'class='suntrust-checkbox' value='"+stateValue+"' name='"+stateName+"' aria-label='"+stateName+"' /></span></div><label for='"+stateName+"'class='suntrust-checkbox-label'>"+stateName+"</label></div>");
				    	                    		$('.locations_filter_mobile .mobile-state').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input id='"+stateName+"'type='checkbox' class='suntrust-checkbox' value='"+stateValue+"' name='"+stateName+"' aria-label='"+stateName+"' /></span></div><label for='"+stateName+"'class='suntrust-checkbox-label'>"+stateName+"</label></div>");
				    	                    	}
				    	                    	
				    	                    	
				    	                    	
				    	                    	//retainFilterValues('.locations_filter_desktop .search_detail_state', desktopStateFilterSelectedValues);
				    	                    	/*Populate State Filter Options - Ends*/
				    	                    	
				    	                    	/*Populate City Filter Options - Starts*/
				    	                    	
				    	                    	
				    	                    	
				    	                    	for(var cityCount = 0; cityCount < uniqueCityArray.length; cityCount++){
				    	                    		$('.locations_filter_desktop .search_detail_city .search_detail_state_city_select').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueCityArray[cityCount]+"'class='suntrust-checkbox' value='"+uniqueCityArray[cityCount]+"' name='"+uniqueCityArray[cityCount]+"' aria-label='"+uniqueCityArray[cityCount]+"' /></span></div><label for='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox-label'>"+uniqueCityArray[cityCount]+"</label></div>");
				    	                    		$('.locations_filter_mobile .mobile-city').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox' value='"+uniqueCityArray[cityCount]+"' name='"+uniqueCityArray[cityCount]+"' aria-label='"+uniqueCityArray[cityCount]+"' /></span></div><label for='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox-label'>"+uniqueCityArray[cityCount]+"</label></div>");
				    	                    		}
				    	                    	irrelaventDivsRemover();
												var numberOfStates = $('.locations_filter_desktop .search_detail_state .search_detail_state_city_select').children().length;
				    	                    	var numberOfCities = $('.locations_filter_desktop .search_detail_city .search_detail_state_city_select').children().length;
				    	                    	$('.locations_filter_desktop .search_detail_city h4 span').text(numberOfCities);
				    	                    	$('.locations_filter_desktop .city-count').text(numberOfCities);
				    	                    	$('.locations_filter_mobile .city-count').text(numberOfCities);
												$('.locations_filter_desktop .search_detail_state h4 span').text(numberOfStates);
				    	                    	$('.locations_filter_mobile .state-count').text(numberOfStates);
				    	                    	//retainFilterValues('.locations_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
				    	                    	if(window.innerWidth<768) {
													if(eventType == "newSearch"){
														selectAllFilters();	
													}else if(eventType == "refineResult"){
														//if(!locationSearchMobileRadiusChangedFlag){
															retainFilterValues('.locations_filter_mobile .mobile-services', servicesFilterSelectedValues);
															retainFilterValues('.locations_filter_mobile .mobile-state', stateFilterSelectedValues);
															retainFilterValues('.locations_filter_mobile .mobile-city', cityFilterSelectedValues);
														/*}else{
															selectAllFilters();	
														}*/
													}	
												}else{
				    	                    		//selectAllFilters();
				    	                    		if(eventType == "newSearch"){
														selectAllFilters();	
													}else if(eventType == "refineResult"){
														retainFilterValues('.locations_filter_desktop .search_detail_services',desktopServicesFilterSelectedValues);
														retainFilterValues('.locations_filter_desktop .search_detail_state', desktopStateFilterSelectedValues);
														retainFilterValues('.locations_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
													}
				    	                    	}
				    	                    	/*Populate City Filter Options - Ends*/
				    	                    	
				    	                    	$('.search_result_filters.col-lg-4.col-md-4.col-sm-4.hidden-xs').show();
				    	                    	//$('.location_result_show_more_results').removeClass("hide");
												var serviceFilterCount = $('.locations_filter_desktop .search_detail_services_level0').children().length; 
												var stateFilterCount = $('.search_detail_state .search_detail_state_city_select').children().length;
												var cityFilterCount = $('.search_detail_city .search_detail_state_city_select').children().length;
												if(serviceFilterCount !=0 || stateFilterCount != 0 || cityFilterCount !=0 ){
													$('.locations_filter_desktop').show();
													$('.locations_filter_mobile').removeClass("hide");
												}
				    	                    	$('.locations_filter_container').removeClass("hide");
												var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
												if(window.innerWidth<768) {
													$('.locations_filter_mobile .mobile-city input').each(function(e){
														if(!($(this).is(':checked'))){
															checkedCityFlag = true;
														}
													});
													$('.locations_filter_mobile .mobile-state input').each(function(e){
														if(!($(this).is(':checked'))){
															checkedStateFlag = true;
														}
													});
													$('.locations_filter_mobile .mobile-services input').each(function(e){
														if(!($(this).is(':checked'))){
															checkedServiceFlag = true;
														}
													});
												}
												if(window.innerWidth>=768) {
													$('.locations_filter_desktop .search_detail_city input').each(function(e){
														if(!($(this).is(':checked'))){
															checkedCityFlag = true;
														}
													});
													$('.locations_filter_desktop .search_detail_state input').each(function(e){
														if(!($(this).is(':checked'))){
															checkedStateFlag = true;
														}
													});
													$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){
														if(!($(this).is(':checked'))){
															checkedServiceFlag = true;
														}
													});
												}									
				    	                    	if(eventType == "newSearch"){
													filteredLocationArray = MqAndAEMResults;
													resultCreator(filteredLocationArray,"singleDimension","newSearch",numberOfResultToBeDisplayed,locationShowMoreCounter);
												}else if((eventType == "refineResult") &&(checkedCityFlag == true || checkedStateFlag == true || checkedServiceFlag == true )){
													refineFilter(selectedRadius,eventType);
												}else if((eventType == "refineResult") &&(checkedCityFlag == false && checkedStateFlag == false && checkedServiceFlag == false )){
													filteredLocationArray = MqAndAEMResults;
													resultCreator(filteredLocationArray,"singleDimension","refineResult",numberOfResultToBeDisplayed,locationShowMoreCounter);
												}
				    	                    	var responseStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
				    	                    	/**/
												
						                    },
						    				error: function(XMLHttpRequest, textStatus, errorThrown) {
						    					console.log("Error occured");
						    					$('#loadingMask').fadeOut(100);
						    				}
						                 });
			                        }else{
			                        	var loc_result_length = loc_result_object.length;
		    	                        if(mapQuestResultCount != 0 && loc_result_object_full_response.length > 0){
		    	                        	for (var k=0;k<mapQuestResultKey.length;k++){
		    	    	                        refinedRadiusResult.push($(loc_result_object_full_response).filter(function (i,n){return n.loc_latitude_longitude === mapQuestResultKey[k]}));           
		    	    	                    }
		    	                        	var MQaemKey = [];
			    	                        for(var i=0; i<refinedRadiusResult.length;i++){	
			    	                        	for(var j=0; j<refinedRadiusResult[i].length;j++)
				    	                        	MQaemKey.push(refinedRadiusResult[i][j].loc_detail_page);
			    	                        }
			    	                        
			    	                        for(var i=0;i<loc_result_object.length;i++){
			    	                        	for(var j=0;j<MQaemKey.length;j++){
			    	                        		if(MQaemKey[j] != loc_result_object[i].loc_detail_page){
			    	                        			flag="notPresent";
			    	                        		}else{
			    	                        			flag = "present";
			    	                        			break;
			    	                        		}
			    	                        	}
			    	                        	if(flag=="notPresent"){
			    	                        		loc_result_object[i].loc_result_type = 'AEM';
			    	                        		AemOnlyResults.push(loc_result_object[i]);
			    	                        	}
			    	                        }
			    	                        
			    	                        for(var i=0; i<refinedRadiusResult.length;i++){	
			    	                        	for(var j=0; j<refinedRadiusResult[i].length;j++){
			    	                        		refinedRadiusResult[i][j].loc_result_type = 'MQ';
			    	                        		MqAndAEMResults.push(refinedRadiusResult[i][j]);
			    	                        		MqOnlyResults.push(refinedRadiusResult[i][j]);
			    	                        	}
			    	                        }
			    	                        for(var i=0; i<AemOnlyResults.length;i++){	
			    	                        	MqAndAEMResults.push(AemOnlyResults[i]);           
		    	    	                    }
		    	                        } else if(mapQuestResultCount == 0 || loc_result_object_full_response.length == 0){
											for(var i=0; i<loc_result_object.length;i++){	
												loc_result_object[i].loc_result_type = 'AEM';
												AemOnlyResults.push(loc_result_object[i]);           
											}
											MqAndAEMResults = AemOnlyResults;           
										}
		    	                        
		    	                        /*Populate Service Filter Options - Starts*/
				    	                    	var servicesArray = [];
				    	                    	var finalServicesArray = [];
				    	                    	var uniqueServicesArray = [];
				    	                    	var duplicateServicesArray = [];
				    	                    	var servicesTagArray = [];
				    	                    	var finalServicesTagArray = [];
				    	                    	var uniqueServicesTagArray = [];
				    	                    	var duplicateServicesTagArray = [];
												var statesArray = [];
				    	                    	var uniqueStatesArray = [];
												var cityArray = [];
				    	                    	var uniqueCityArray = [];
												var filterStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
				    	                    	for(var i = 0; i < MqAndAEMResults.length; i++ ){
				    	                    		servicesArray[i]=MqAndAEMResults[i].loc_services;
													servicesTagArray[i]=MqAndAEMResults[i].loc_services_tag;
				    	                    		for(var k = 0; k < servicesArray[i].length; k++ ){
														if(uniqueServicesArray.indexOf(servicesArray[i][k].trim())==-1)
															uniqueServicesArray.push(servicesArray[i][k].trim());
				    	                    		}
				    	                    		for(var j = 0; j < servicesTagArray[i].length; j++ ){
				    	                    			if(uniqueServicesTagArray.indexOf(servicesTagArray[i][j])==-1)
															uniqueServicesTagArray.push(servicesTagArray[i][j]);
				    	                    		}
													
													/**/
													
													statesArray[i]=MqAndAEMResults[i].loc_state+":"+MqAndAEMResults[i].loc_state_tag;
													$.each(statesArray, function(i, el){
														if($.inArray(el, uniqueStatesArray) === -1) uniqueStatesArray.push(el);
													});
													
													/**/
													
													cityArray[i]=MqAndAEMResults[i].loc_city;
			    	                    			$.each(cityArray, function(i, el){
			    	                    				if($.inArray(el, uniqueCityArray) === -1) uniqueCityArray.push(el);
			    	                    			});
				    	                    	}
												var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
																								
				    	                    	uniqueServicesArray.sort();
				    	                    	uniqueServicesTagArray.sort();
												uniqueStatesArray.sort();
												uniqueCityArray.sort();
												var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
				    	                    	for(var servicesCount = 0; servicesCount < uniqueServicesArray.length; servicesCount++){
				    	                    		$('.locations_filter_desktop .search_by_filter.search_detail_services .search_detail_services_level0').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueServicesTagArray[servicesCount]+"' class='suntrust-checkbox' value='"+uniqueServicesTagArray[servicesCount]+"' name='"+uniqueServicesTagArray[servicesCount]+"' aria-label='"+uniqueServicesTagArray[servicesCount]+"' /></span></div><label for='"+uniqueServicesTagArray[servicesCount]+"'class='suntrust-checkbox-label'>"+uniqueServicesArray[servicesCount]+"</label></div>");
				    	                    		$('.mobile_filter_search .locations_filter_mobile .mobile-services').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueServicesTagArray[servicesCount]+"' class='suntrust-checkbox' value='"+uniqueServicesTagArray[servicesCount]+"' name='"+uniqueServicesTagArray[servicesCount]+"' aria-label='"+uniqueServicesTagArray[servicesCount]+"' /></span></div><label for='"+uniqueServicesTagArray[servicesCount]+"'class='suntrust-checkbox-label'>"+uniqueServicesArray[servicesCount]+"</label></div>");
				    	                    	}
				    	                    	
				    	                    	$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){
				    	                    		if(($(this).val() == "branch")){
				    	                    			$(this).parent().parent().parent().after("<div class='desktop search_detail_services_level1'></div>")
				    	                    		}
				    	                    	});
				    	                    	$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){                  
				    	                    		if(($(this).val() == "drive-thru-banking") || ($(this).val() == "weekend-hours") || ($(this).val() == "instore-branch")){
				    	                    			var divData = $(this).parent().parent().parent().detach();
				    	                    			$(".locations_filter_desktop .desktop.search_detail_services_level1").append(divData);
				    	                    		}
				    	                    	});
				    	                    	$('.locations_filter_mobile .mobile-services-tog input').each(function(e){
				    	                    		if(($(this).val() == "branch")){
				    	                    			$(this).parent().parent().parent().after("<div class='mobile search_detail_services_level1'></div>")
				    	                    		}
				    	                    	});
				    	                    	$('.locations_filter_mobile .mobile-services-tog input').each(function(e){                  
				    	                    		if(($(this).val() == "drive-thru-banking") || ($(this).val() == "weekend-hours") || ($(this).val() == "instore-branch")){
				    	                    			var divData = $(this).parent().parent().parent().detach();
				    	                    			$(".locations_filter_mobile .mobile.search_detail_services_level1").append(divData);
				    	                    		}
				    	                    	});
				    	                    	if($(".locations_filter_mobile .search_detail_services_level1").children().length == 0){
				    	                    		$(".locations_filter_mobile .search_detail_services_level1").remove();
				    	                    	}
				    	                    	if($(".locations_filter_desktop .search_detail_services_level1").children().length == 0){
				    	                    		$(".locations_filter_desktop .search_detail_services_level1").remove();
				    	                    	}
				    	                    	/*Populate Service Filter Options - Ends*/
				    	                    	
				    	                    	/*Populate State Filter Options - Starts*/
				    	                    	
				    	                    	
				    	                    	for(var statesCount = 0; statesCount < uniqueStatesArray.length; statesCount++){
				    	                    		var stateName = uniqueStatesArray[statesCount].substring(0,uniqueStatesArray[statesCount].indexOf(":"));
				    	                    		var stateValue= uniqueStatesArray[statesCount].substring(uniqueStatesArray[statesCount].indexOf(":")+1,uniqueStatesArray[statesCount].length);
				    	                    		$('.locations_filter_desktop .search_detail_state .search_detail_state_city_select').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+stateName+"'class='suntrust-checkbox' value='"+stateValue+"' name='"+stateName+"' aria-label='"+stateName+"' /></span></div><label for='"+stateName+"'class='suntrust-checkbox-label'>"+stateName+"</label></div>");
				    	                    		$('.locations_filter_mobile .mobile-state').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input id='"+stateName+"'type='checkbox' class='suntrust-checkbox' value='"+stateValue+"' name='"+stateName+"' aria-label='"+stateName+"' /></span></div><label for='"+stateName+"'class='suntrust-checkbox-label'>"+stateName+"</label></div>");
				    	                    	}
				    	                    	
				    	                    	
				    	                    	
				    	                    	//retainFilterValues('.locations_filter_desktop .search_detail_state', desktopStateFilterSelectedValues);
				    	                    	/*Populate State Filter Options - Ends*/
				    	                    	
				    	                    	/*Populate City Filter Options - Starts*/
				    	                    	
				    	                    	
				    	                    	
				    	                    	for(var cityCount = 0; cityCount < uniqueCityArray.length; cityCount++){
				    	                    		$('.locations_filter_desktop .search_detail_city .search_detail_state_city_select').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueCityArray[cityCount]+"'class='suntrust-checkbox' value='"+uniqueCityArray[cityCount]+"' name='"+uniqueCityArray[cityCount]+"' aria-label='"+uniqueCityArray[cityCount]+"' /></span></div><label for='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox-label'>"+uniqueCityArray[cityCount]+"</label></div>");
				    	                    		$('.locations_filter_mobile .mobile-city').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox' value='"+uniqueCityArray[cityCount]+"' name='"+uniqueCityArray[cityCount]+"' aria-label='"+uniqueCityArray[cityCount]+"' /></span></div><label for='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox-label'>"+uniqueCityArray[cityCount]+"</label></div>");
				    	                    		}
				    	                    	irrelaventDivsRemover();
												var numberOfStates = $('.locations_filter_desktop .search_detail_state .search_detail_state_city_select').children().length;
				    	                    	var numberOfCities = $('.locations_filter_desktop .search_detail_city .search_detail_state_city_select').children().length;
				    	                    	$('.locations_filter_desktop .search_detail_city h4 span').text(numberOfCities);
				    	                    	$('.locations_filter_desktop .city-count').text(numberOfCities);
				    	                    	$('.locations_filter_mobile .city-count').text(numberOfCities);
												$('.locations_filter_desktop .search_detail_state h4 span').text(numberOfStates);
				    	                    	$('.locations_filter_mobile .state-count').text(numberOfStates);
				    	                    	//retainFilterValues('.locations_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
				    	                    	if(window.innerWidth<768) {
													if(eventType == "newSearch"){
														selectAllFilters();	
													}else if(eventType == "refineResult"){
														//if(!locationSearchMobileRadiusChangedFlag){
															retainFilterValues('.locations_filter_mobile .mobile-services', servicesFilterSelectedValues);
															retainFilterValues('.locations_filter_mobile .mobile-state', stateFilterSelectedValues);
															retainFilterValues('.locations_filter_mobile .mobile-city', cityFilterSelectedValues);
														/*}else{
															selectAllFilters();	
														}*/
													}	
												}else{
				    	                    		//selectAllFilters();
				    	                    		if(eventType == "newSearch"){
														selectAllFilters();	
													}else if(eventType == "refineResult"){
														retainFilterValues('.locations_filter_desktop .search_detail_services',desktopServicesFilterSelectedValues);
														retainFilterValues('.locations_filter_desktop .search_detail_state', desktopStateFilterSelectedValues);
														retainFilterValues('.locations_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
													}
				    	                    	}
				    	                    	/*Populate City Filter Options - Ends*/
				    	                    	
				    	                    	$('.search_result_filters.col-lg-4.col-md-4.col-sm-4.hidden-xs').show();
				    	                    	//$('.location_result_show_more_results').removeClass("hide");
												var serviceFilterCount = $('.locations_filter_desktop .search_detail_services_level0').children().length; 
												var stateFilterCount = $('.search_detail_state .search_detail_state_city_select').children().length;
												var cityFilterCount = $('.search_detail_city .search_detail_state_city_select').children().length;
												if(serviceFilterCount !=0 || stateFilterCount != 0 || cityFilterCount !=0 ){
													$('.locations_filter_desktop').show();
													$('.locations_filter_mobile').removeClass("hide");
												}
				    	                    	$('.locations_filter_container').removeClass("hide");
												var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
										if(window.innerWidth<768) {
											$('.locations_filter_mobile .mobile-city input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedCityFlag = true;
												}
											});
											$('.locations_filter_mobile .mobile-state input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedStateFlag = true;
												}
											});
											$('.locations_filter_mobile .mobile-services input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedServiceFlag = true;
												}
											});
										}
										if(window.innerWidth>=768) {
											$('.locations_filter_desktop .search_detail_city input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedCityFlag = true;
												}
											});
											$('.locations_filter_desktop .search_detail_state input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedStateFlag = true;
												}
											});
											$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedServiceFlag = true;
												}
											});
										}									
		    	                    	if(eventType == "newSearch"){
											filteredLocationArray = MqAndAEMResults;
											resultCreator(filteredLocationArray,"singleDimension","newSearch",numberOfResultToBeDisplayed,locationShowMoreCounter);
										}else if((eventType == "refineResult") &&(checkedCityFlag == true || checkedStateFlag == true || checkedServiceFlag == true )){
											refineFilter(selectedRadius,eventType);
										}else if((eventType == "refineResult") &&(checkedCityFlag == false && checkedStateFlag == false && checkedServiceFlag == false )){
											filteredLocationArray = MqAndAEMResults;
											resultCreator(filteredLocationArray,"singleDimension","refineResult",numberOfResultToBeDisplayed,locationShowMoreCounter);
										}
			                        }
			                        
			        				
									
				                },
								error: function(XMLHttpRequest, textStatus, errorThrown) {
									console.log("Error occured");
									$('#loadingMask').fadeOut(100);
								}
				            });
		                }else{
		                	var loc_result_length = loc_result_object.length;
		                	if(mapQuestResultCount != 0 && loc_result_object_full_response.length > 0){
	                        	for (var k=0;k<mapQuestResultKey.length;k++){
	    	                        refinedRadiusResult.push($(loc_result_object_full_response).filter(function (i,n){return n.loc_latitude_longitude === mapQuestResultKey[k]}));           
	    	                    }
	                        	var MQaemKey = [];
		                        for(var i=0; i<refinedRadiusResult.length;i++){	
		                        	for(var j=0; j<refinedRadiusResult[i].length;j++)
	    	                        	MQaemKey.push(refinedRadiusResult[i][j].loc_detail_page);
		                        }
		                        
		                        for(var i=0;i<loc_result_object.length;i++){
		                        	for(var j=0;j<MQaemKey.length;j++){
		                        		if(MQaemKey[j] != loc_result_object[i].loc_detail_page){
		                        			flag="notPresent";
		                        		}else{
		                        			flag = "present";
		                        			break;
		                        		}
		                        	}
		                        	if(flag=="notPresent"){
		                        		loc_result_object[i].loc_result_type = 'AEM';
		                        		AemOnlyResults.push(loc_result_object[i]);
		                        	}
		                        }
		                        
		                        for(var i=0; i<refinedRadiusResult.length;i++){	
		                        	for(var j=0; j<refinedRadiusResult[i].length;j++){
		                        		refinedRadiusResult[i][j].loc_result_type = 'MQ';
		                        		MqAndAEMResults.push(refinedRadiusResult[i][j]);
		                        		MqOnlyResults.push(refinedRadiusResult[i][j]);
		                        	}
		                        }
		                        for(var i=0; i<AemOnlyResults.length;i++){	
		                        	MqAndAEMResults.push(AemOnlyResults[i]);           
	    	                    }
		                        
	                        } else if(mapQuestResultCount == 0 || loc_result_object_full_response.length == 0){
								for(var i=0; i<loc_result_object.length;i++){	
									loc_result_object[i].loc_result_type = 'AEM';
									AemOnlyResults.push(loc_result_object[i]);           
								}
								MqAndAEMResults = AemOnlyResults;           
							}
	                        /**/
	                        /*Populate Service Filter Options - Starts*/
							var servicesArray = [];
							var finalServicesArray = [];
							var uniqueServicesArray = [];
							var duplicateServicesArray = [];
							var servicesTagArray = [];
							var finalServicesTagArray = [];
							var uniqueServicesTagArray = [];
							var duplicateServicesTagArray = [];
							var statesArray = [];
							var uniqueStatesArray = [];
							var cityArray = [];
							var uniqueCityArray = [];
							var filterStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
							for(var i = 0; i < MqAndAEMResults.length; i++ ){
								servicesArray[i]=MqAndAEMResults[i].loc_services;
								servicesTagArray[i]=MqAndAEMResults[i].loc_services_tag;
								for(var k = 0; k < servicesArray[i].length; k++ ){
									if(uniqueServicesArray.indexOf(servicesArray[i][k].trim())==-1)
										uniqueServicesArray.push(servicesArray[i][k].trim());
								}
								for(var j = 0; j < servicesTagArray[i].length; j++ ){
									if(uniqueServicesTagArray.indexOf(servicesTagArray[i][j])==-1)
										uniqueServicesTagArray.push(servicesTagArray[i][j]);
								}
								
								/**/
								
								statesArray[i]=MqAndAEMResults[i].loc_state+":"+MqAndAEMResults[i].loc_state_tag;
								$.each(statesArray, function(i, el){
									if($.inArray(el, uniqueStatesArray) === -1) uniqueStatesArray.push(el);
								});
								
								/**/
								
								cityArray[i]=MqAndAEMResults[i].loc_city;
								$.each(cityArray, function(i, el){
									if($.inArray(el, uniqueCityArray) === -1) uniqueCityArray.push(el);
								});
							}
							var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
																			
							uniqueServicesArray.sort();
							uniqueServicesTagArray.sort();
							uniqueStatesArray.sort();
							uniqueCityArray.sort();
							var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
							for(var servicesCount = 0; servicesCount < uniqueServicesArray.length; servicesCount++){
								$('.locations_filter_desktop .search_by_filter.search_detail_services .search_detail_services_level0').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueServicesTagArray[servicesCount]+"' class='suntrust-checkbox' value='"+uniqueServicesTagArray[servicesCount]+"' name='"+uniqueServicesTagArray[servicesCount]+"' aria-label='"+uniqueServicesTagArray[servicesCount]+"' /></span></div><label for='"+uniqueServicesTagArray[servicesCount]+"'class='suntrust-checkbox-label'>"+uniqueServicesArray[servicesCount]+"</label></div>");
								$('.mobile_filter_search .locations_filter_mobile .mobile-services').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueServicesTagArray[servicesCount]+"' class='suntrust-checkbox' value='"+uniqueServicesTagArray[servicesCount]+"' name='"+uniqueServicesTagArray[servicesCount]+"' aria-label='"+uniqueServicesTagArray[servicesCount]+"' /></span></div><label for='"+uniqueServicesTagArray[servicesCount]+"'class='suntrust-checkbox-label'>"+uniqueServicesArray[servicesCount]+"</label></div>");
							}
							
							$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){
								if(($(this).val() == "branch")){
									$(this).parent().parent().parent().after("<div class='desktop search_detail_services_level1'></div>")
								}
							});
							$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){                  
								if(($(this).val() == "drive-thru-banking") || ($(this).val() == "weekend-hours") || ($(this).val() == "instore-branch")){
									var divData = $(this).parent().parent().parent().detach();
									$(".locations_filter_desktop .desktop.search_detail_services_level1").append(divData);
								}
							});
							$('.locations_filter_mobile .mobile-services-tog input').each(function(e){
								if(($(this).val() == "branch")){
									$(this).parent().parent().parent().after("<div class='mobile search_detail_services_level1'></div>")
								}
							});
							$('.locations_filter_mobile .mobile-services-tog input').each(function(e){                  
								if(($(this).val() == "drive-thru-banking") || ($(this).val() == "weekend-hours") || ($(this).val() == "instore-branch")){
									var divData = $(this).parent().parent().parent().detach();
									$(".locations_filter_mobile .mobile.search_detail_services_level1").append(divData);
								}
							});
							if($(".locations_filter_mobile .search_detail_services_level1").children().length == 0){
								$(".locations_filter_mobile .search_detail_services_level1").remove();
							}
							if($(".locations_filter_desktop .search_detail_services_level1").children().length == 0){
								$(".locations_filter_desktop .search_detail_services_level1").remove();
							}
							/*Populate Service Filter Options - Ends*/
							
							/*Populate State Filter Options - Starts*/
							
							
							for(var statesCount = 0; statesCount < uniqueStatesArray.length; statesCount++){
								var stateName = uniqueStatesArray[statesCount].substring(0,uniqueStatesArray[statesCount].indexOf(":"));
								var stateValue= uniqueStatesArray[statesCount].substring(uniqueStatesArray[statesCount].indexOf(":")+1,uniqueStatesArray[statesCount].length);
								$('.locations_filter_desktop .search_detail_state .search_detail_state_city_select').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+stateName+"'class='suntrust-checkbox' value='"+stateValue+"' name='"+stateName+"' aria-label='"+stateName+"' /></span></div><label for='"+stateName+"'class='suntrust-checkbox-label'>"+stateName+"</label></div>");
								$('.locations_filter_mobile .mobile-state').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input id='"+stateName+"'type='checkbox' class='suntrust-checkbox' value='"+stateValue+"' name='"+stateName+"' aria-label='"+stateName+"' /></span></div><label for='"+stateName+"'class='suntrust-checkbox-label'>"+stateName+"</label></div>");
							}
							
							
							
							//retainFilterValues('.locations_filter_desktop .search_detail_state', desktopStateFilterSelectedValues);
							/*Populate State Filter Options - Ends*/
							
							/*Populate City Filter Options - Starts*/
							
							
							
							for(var cityCount = 0; cityCount < uniqueCityArray.length; cityCount++){
								$('.locations_filter_desktop .search_detail_city .search_detail_state_city_select').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueCityArray[cityCount]+"'class='suntrust-checkbox' value='"+uniqueCityArray[cityCount]+"' name='"+uniqueCityArray[cityCount]+"' aria-label='"+uniqueCityArray[cityCount]+"' /></span></div><label for='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox-label'>"+uniqueCityArray[cityCount]+"</label></div>");
								$('.locations_filter_mobile .mobile-city').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox' value='"+uniqueCityArray[cityCount]+"' name='"+uniqueCityArray[cityCount]+"' aria-label='"+uniqueCityArray[cityCount]+"' /></span></div><label for='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox-label'>"+uniqueCityArray[cityCount]+"</label></div>");
								}
							irrelaventDivsRemover();
							var numberOfStates = $('.locations_filter_desktop .search_detail_state .search_detail_state_city_select').children().length;
							var numberOfCities = $('.locations_filter_desktop .search_detail_city .search_detail_state_city_select').children().length;
							$('.locations_filter_desktop .search_detail_city h4 span').text(numberOfCities);
							$('.locations_filter_desktop .city-count').text(numberOfCities);
							$('.locations_filter_mobile .city-count').text(numberOfCities);
							$('.locations_filter_desktop .search_detail_state h4 span').text(numberOfStates);
							$('.locations_filter_mobile .state-count').text(numberOfStates);
							//retainFilterValues('.locations_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
							if(window.innerWidth<768) {
								if(eventType == "newSearch"){
									selectAllFilters();	
								}else if(eventType == "refineResult"){
									//if(!locationSearchMobileRadiusChangedFlag){
										retainFilterValues('.locations_filter_mobile .mobile-services', servicesFilterSelectedValues);
										retainFilterValues('.locations_filter_mobile .mobile-state', stateFilterSelectedValues);
										retainFilterValues('.locations_filter_mobile .mobile-city', cityFilterSelectedValues);
									/*}else{
										selectAllFilters();	
									}*/
								}	
							}else{
								//selectAllFilters();
								if(eventType == "newSearch"){
									selectAllFilters();	
								}else if(eventType == "refineResult"){
									retainFilterValues('.locations_filter_desktop .search_detail_services',desktopServicesFilterSelectedValues);
									retainFilterValues('.locations_filter_desktop .search_detail_state', desktopStateFilterSelectedValues);
									retainFilterValues('.locations_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
								}
							}
							/*Populate City Filter Options - Ends*/
							
							$('.search_result_filters.col-lg-4.col-md-4.col-sm-4.hidden-xs').show();
							//$('.location_result_show_more_results').removeClass("hide");
							var serviceFilterCount = $('.locations_filter_desktop .search_detail_services_level0').children().length; 
							var stateFilterCount = $('.search_detail_state .search_detail_state_city_select').children().length;
							var cityFilterCount = $('.search_detail_city .search_detail_state_city_select').children().length;
							if(serviceFilterCount !=0 || stateFilterCount != 0 || cityFilterCount !=0 ){
								$('.locations_filter_desktop').show();
								$('.locations_filter_mobile').removeClass("hide");
							}
							$('.locations_filter_container').removeClass("hide");
							var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
							if(window.innerWidth<768) {
								$('.locations_filter_mobile .mobile-city input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedCityFlag = true;
									}
								});
								$('.locations_filter_mobile .mobile-state input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedStateFlag = true;
									}
								});
								$('.locations_filter_mobile .mobile-services input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedServiceFlag = true;
									}
								});
							}
							if(window.innerWidth>=768) {
								$('.locations_filter_desktop .search_detail_city input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedCityFlag = true;
									}
								});
								$('.locations_filter_desktop .search_detail_state input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedStateFlag = true;
									}
								});
								$('.locations_filter_desktop .search_detail_services_level0 input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedServiceFlag = true;
									}
								});
							}									
	                    	if(eventType == "newSearch"){
								filteredLocationArray = MqAndAEMResults;
								resultCreator(filteredLocationArray,"singleDimension","newSearch",numberOfResultToBeDisplayed,locationShowMoreCounter);
							}else if((eventType == "refineResult") &&(checkedCityFlag == true || checkedStateFlag == true || checkedServiceFlag == true )){
								refineFilter(selectedRadius,eventType);
							}else if((eventType == "refineResult") &&(checkedCityFlag == false && checkedStateFlag == false && checkedServiceFlag == false )){
								filteredLocationArray = MqAndAEMResults;
								resultCreator(filteredLocationArray,"singleDimension","refineResult",numberOfResultToBeDisplayed,locationShowMoreCounter);
							}
		                }
					}else {
						$('#loadingMask').fadeOut(100);
						$('.locations_no_result.zero_result_found').removeClass("hide");
						$('.search_result_count').removeClass("hide");
						$('.locations_result_count').text("0");
						$('.locations_result_count').removeClass("hide");
					}
					
					// Start Analytics call
    				//passSearchValuesToAnalyticEngine("desktopView");
    				// End Analytics call
    				
				$('.search_result_categories').removeClass("hide");		
				},
	  			error: function(XMLHttpRequest, textStatus, errorThrown) {
	  				console.log("Error occured");
	  				$('#loadingMask').fadeOut(100);
	  			}
		});
    } 
}



function peopleSearchHandler(selecteRadiusParam, eventType, selectedPeopleSpeciality){
	var specialityFilterSelectedValues=[];
    var stateFilterSelectedValues=[];
	var cityFilterSelectedValues=[];
	var desktopSpecialityFilterSelectedValues=[];
    var desktopStateFilterSelectedValues=[];
	var desktopCityFilterSelectedValues=[];
    specialityFilterSelectedValues = selectedPeopleFilterFinder('.people_filter_mobile .mobile-speciality');
    stateFilterSelectedValues = selectedPeopleFilterFinder('.people_filter_mobile .mobile-state');
    cityFilterSelectedValues = selectedPeopleFilterFinder('.people_filter_mobile .mobile-city');
    desktopCityFilterSelectedValues = selectedPeopleFilterFinder('.people_filter_desktop .search_detail_city');
    desktopStateFilterSelectedValues = selectedPeopleFilterFinder('.people_filter_desktop .search_detail_state');
    desktopSpecialityFilterSelectedValues = selectedPeopleFilterFinder('.people_filter_desktop .search_detail_speciality');
    
    peopleShowMoreCounter = 0;
	numberOfResultToBeDisplayedPeople = parseInt($('.people_result_show_more_results a').attr('data-search-visible-items-limit'));
    if(eventType =="refineResult"){
    	people_result_object_full_response="";
    }else if(eventType =="newSearch"){
    	people_result_object_full_response="";
		people_result_object="";
    }
	
	MqAndAEMResultsPeople = [];
	MqOnlyResultsPeople=[];
	AemOnlyResultsPeople=[];
    $('.people_no_result').addClass("hide");
	$('.faqs_no_result.zero_result_found').addClass("hide");
	$('.locations_no_result.zero_result_found').addClass("hide");
    $('.people_filter_desktop .search_result_filters.col-lg-4.col-md-4.col-sm-4.hidden-xs').hide(); 
	$('.people_result_show_more_results').addClass("hide");	
	$('.search_result_show_more_results').addClass("hide");
	$('.location-search .search_result_count').addClass("hide");
	$('.search_result_description .search-results .people_filter_container .filter-no-result').remove();
	$('.search_result_description .search-results .people_filter_container .people_mq_results').children().remove();
	$('.search_result_description .search-results .people_filter_container .people_aem_results').children().remove();
	$('.people_filter_desktop').hide();
    $('.people_filter_mobile').addClass("hide");
    $('.people_filter_container').addClass("hide");
    
    $('.people_filter_desktop .search_by_filter.search_detail_speciality .search_detail_speciality_level').children().remove();
	$('.people_filter_desktop .search_detail_state .search_detail_state_city_select').children().remove();
	$('.people_filter_desktop .search_detail_city .search_detail_state_city_select').children().remove();
    $('.people_filter_mobile .filter_tog .mobile-speciality').children().remove();
	$('.people_filter_mobile .filter_tog .mobile-state').children().remove();
    $('.people_filter_mobile .filter_tog .mobile-city').children().remove();
	$('.people_filter_desktop .search_by_filter.search_detail_people_radius').show();
	$('.people_filter_mobile .mobile-radius-filter').show();
    var currentDay = new Date().getDay();
    var mapQuestResultKey = [];
	var mqPostalCodesArray=[];
	var mqPostalCodesString="";
    refinedRadiusResult=[];
	var checkedSpecialityFlag = false;
	var checkedStateFlag = false;
	var checkedCityFlag = false;
    var selectedRadius = selecteRadiusParam;
    var people_result_length = people_result_object.length;
    var flag;
	var tempMilesAway=[];
    if(searchString == ""){
		$('.people_filter_desktop').hide();
        $('.people_filter_mobile').addClass("hide");
        $('.search_result_description .search-results .people_filter_container').addClass("hide");
    }
    if(searchString != ""){
		$('#loadingMask').show().focus();
        var mapQuestURL = 'https://www.mapquestapi.com/search/v2/radius?origin='+searchString+'&radius='+selectedRadius+'&maxMatches=500&ambiguities=ignore&hostedData=mqap.32547_SunTrust_Branch_Loc&outFormat=json&key=Gmjtd|lu6zn1ua2d,70=o5-l0850';
		$.ajax({
				type : "GET",
				url : mapQuestURL,
				async: false,
				dataType : 'json',
				crossDomain : true,
				success : function(responseMapQuest, textStatus, jqXHR) {
					if(responseMapQuest.info.statusCode != '500'){
						mapQuestResultCount = parseInt(responseMapQuest['resultsCount']);
		                if(mapQuestResultCount != 0 && responseMapQuest['searchResults'] != undefined){
		                    for(var i=0; i<responseMapQuest['searchResults'].length; i++){
		            			var latitude = responseMapQuest['searchResults'][i].fields.lat;
								var longitude = responseMapQuest['searchResults'][i].fields.lng;
								mqPostalCodesArray[i]=responseMapQuest['searchResults'][i].fields.postal;							
		            			if(latitude != "" && latitude != "null" && latitude != null && longitude != "null" && longitude != null && longitude != "")								
									mapQuestResultKey[i]=latitude.toString()+longitude.toString();
								else
									mapQuestResultKey[i]=0;
		        			}
		                    mapQuestResultKey = $.unique(mapQuestResultKey);
		                    mqPostalCodesArray = $.unique(mqPostalCodesArray); 
		                    for(var i=0;i<mqPostalCodesArray.length;i++){
		                    	if(mqPostalCodesString.length == 0)
		                    		mqPostalCodesString = mqPostalCodesArray[i];
		                    	else
		                    		mqPostalCodesString = mqPostalCodesString + "," +mqPostalCodesArray[i];
			                }
		            	}
		                
		                if(people_result_object_full_response.length == 0){
	                        mqPostalCodesString = "mqPostalCodeData"+mqPostalCodesString;
			                $.ajax({
			                    type : "GET",
			                    url : '/dotcom/search',
			                    async: false,
								data : {"searchString": mqPostalCodesString, "currentDay":new Date().getDay(), "aboutMe":selectedPeopleSpeciality, "searchingFor":"People"},
			                    success : function(response, textStatus, jqXHR) {
			                        people_result_object_full_response = response['people_results'];
			                        var people_result_full_response_length = people_result_object_full_response.length;
									if(startLat != undefined && startLng != undefined){
										for(var i=0; i<people_result_full_response_length; i++){
											tempMilesAway=[];
											for(var j=0; j<people_result_object_full_response[i].advisor_addressListArray.addressListArray.length; j++){												
												var endLat = people_result_object_full_response[i].advisor_addressListArray.addressListArray[j].adv_latitude;
												var endLng = people_result_object_full_response[i].advisor_addressListArray.addressListArray[j].adv_longitude;
												var milesAway = updateDistance(startLat, startLng, endLat, endLng);
												people_result_object_full_response[i].advisor_addressListArray.addressListArray[j].people_eachloc_miles_away = milesAway;
												tempMilesAway.push(milesAway);
											}
											people_result_object_full_response[i].people_miles_away = tempMilesAway.min();
										}
										
									}
									
									
			                        if(people_result_object.length == 0){
				                        $.ajax({
				    	                    type : "GET",
				    	                    url : '/dotcom/search',
				    	                    async: false,
											data : {
												"searchString" : searchString,
												"currentDay" : new Date().getDay(), 
												"aboutMe":selectedPeopleSpeciality, 
												"searchingFor":"People"
											},
				    	                    success : function(response, textStatus, jqXHR) {
												var startTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
				    	                        people_result_object = response['people_results'];
				    	                        var people_result_length = people_result_object.length;
												if(startLat != undefined && startLng != undefined){
													for(var i=0; i<people_result_length; i++){
														tempMilesAway=[];
														for(var j=0; j<people_result_object[i].advisor_addressListArray.addressListArray.length; j++){												
															var endLat = people_result_object[i].advisor_addressListArray.addressListArray[j].adv_latitude;
															var endLng = people_result_object[i].advisor_addressListArray.addressListArray[j].adv_longitude;
															var milesAway = updateDistance(startLat, startLng, endLat, endLng);
															people_result_object[i].advisor_addressListArray.addressListArray[j].people_eachloc_miles_away = milesAway;
															tempMilesAway.push(milesAway);
														}
														people_result_object[i].people_miles_away = tempMilesAway.min();
													}
												}
	
												populatePeopleFilters(eventType, specialityFilterSelectedValues, stateFilterSelectedValues, cityFilterSelectedValues, desktopCityFilterSelectedValues, desktopStateFilterSelectedValues, desktopSpecialityFilterSelectedValues);
												
				    	                    	//retainFilterValues('.people_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
				    	                    	
				    	                    	if(window.innerWidth<768) {
											$('.people_filter_mobile .mobile-city input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedCityFlag = true;
												}
											});
											$('.people_filter_mobile .mobile-state input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedStateFlag = true;
												}
											});
											$('.people_filter_mobile .mobile-speciality input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedSpecialityFlag = true;
												}
											});
										}
										if(window.innerWidth>=768) {
											$('.people_filter_desktop .search_detail_city input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedCityFlag = true;
												}
											});
											$('.people_filter_desktop .search_detail_state input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedStateFlag = true;
												}
											});
											$('.people_filter_desktop .search_detail_speciality_level input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedSpecialityFlag = true;
												}
											});
										}									
		    	                    	if(eventType == "newSearch"){
											filteredPeopleArray = MqAndAEMResultsPeople;
											resultCreatorPeople(filteredPeopleArray,"singleDimension","newSearch",numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
										}else if((eventType == "refineResult") &&(checkedCityFlag == true || checkedStateFlag == true || checkedSpecialityFlag == true )){
											refinePeopleFilter(selectedRadius,eventType);
										}else if((eventType == "refineResult") &&(checkedCityFlag == false && checkedStateFlag == false && checkedSpecialityFlag == false )){
											filteredPeopleArray = MqAndAEMResultsPeople;
											resultCreatorPeople(filteredPeopleArray,"singleDimension","refineResult",numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
										}
				    	                    	var responseStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
				    	                    	/**/
												
						                    },
						    				error: function(XMLHttpRequest, textStatus, errorThrown) {
						    					console.log("Error occured");
						    					$('#loadingMask').fadeOut(100);
						    				}
						                 });
			                        }else{
			                        	var people_result_length = people_result_object.length;
		    	                        populatePeopleFilters(eventType, specialityFilterSelectedValues, stateFilterSelectedValues, cityFilterSelectedValues, desktopCityFilterSelectedValues, desktopStateFilterSelectedValues, desktopSpecialityFilterSelectedValues);
										
										//retainFilterValues('.people_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
										
										if(window.innerWidth<768) {
											$('.people_filter_mobile .mobile-city input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedCityFlag = true;
												}
											});
											$('.people_filter_mobile .mobile-state input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedStateFlag = true;
												}
											});
											$('.people_filter_mobile .mobile-speciality input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedSpecialityFlag = true;
												}
											});
										}
										if(window.innerWidth>=768) {
											$('.people_filter_desktop .search_detail_city input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedCityFlag = true;
												}
											});
											$('.people_filter_desktop .search_detail_state input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedStateFlag = true;
												}
											});
											$('.people_filter_desktop .search_detail_speciality_level input').each(function(e){
												if(!($(this).is(':checked'))){
													checkedSpecialityFlag = true;
												}
											});
										}									
		    	                    	if(eventType == "newSearch"){
											filteredPeopleArray = MqAndAEMResultsPeople;
											resultCreatorPeople(filteredPeopleArray,"singleDimension","newSearch",numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
										}else if((eventType == "refineResult") &&(checkedCityFlag == true || checkedStateFlag == true || checkedSpecialityFlag == true )){
											refinePeopleFilter(selectedRadius,eventType);
										}else if((eventType == "refineResult") &&(checkedCityFlag == false && checkedStateFlag == false && checkedSpecialityFlag == false )){
											filteredPeopleArray = MqAndAEMResultsPeople;
											resultCreatorPeople(filteredPeopleArray,"singleDimension","refineResult",numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
										}
			                        }
									
				                },
								error: function(XMLHttpRequest, textStatus, errorThrown) {
									console.log("Error occured");
									$('#loadingMask').fadeOut(100);
								}
				            });
		                }else{
		                	var people_result_length = people_result_object.length;
		                	populatePeopleFilters(eventType, specialityFilterSelectedValues, stateFilterSelectedValues, cityFilterSelectedValues, desktopCityFilterSelectedValues, desktopStateFilterSelectedValues, desktopSpecialityFilterSelectedValues);
	                    	if(window.innerWidth<768) {
								$('.people_filter_mobile .mobile-city input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedCityFlag = true;
									}
								});
								$('.people_filter_mobile .mobile-state input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedStateFlag = true;
									}
								});
								$('.people_filter_mobile .mobile-speciality input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedSpecialityFlag = true;
									}
								});
							}
							if(window.innerWidth>=768) {
								$('.people_filter_desktop .search_detail_city input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedCityFlag = true;
									}
								});
								$('.people_filter_desktop .search_detail_state input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedStateFlag = true;
									}
								});
								$('.people_filter_desktop .search_detail_speciality_level input').each(function(e){
									if(!($(this).is(':checked'))){
										checkedSpecialityFlag = true;
									}
								});
							}									
							if(eventType == "newSearch"){
								filteredPeopleArray = MqAndAEMResultsPeople;
								resultCreatorPeople(filteredPeopleArray,"singleDimension","newSearch",numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
							}else if((eventType == "refineResult") &&(checkedCityFlag == true || checkedStateFlag == true || checkedSpecialityFlag == true )){
								refinePeopleFilter(selectedRadius,eventType);
							}else if((eventType == "refineResult") &&(checkedCityFlag == false && checkedStateFlag == false && checkedSpecialityFlag == false )){
								filteredPeopleArray = MqAndAEMResultsPeople;
								resultCreatorPeople(filteredPeopleArray,"singleDimension","refineResult",numberOfResultToBeDisplayedPeople,peopleShowMoreCounter);
							}
		                }
					}else {
						$('#loadingMask').fadeOut(100);
						$('.people_no_result.zero_result_found').removeClass("hide");
						$('.search_result_count').removeClass("hide");
						$('.people_result_count').text("0");
						$('.people_result_count').removeClass("hide");
					}
					
    				
					$('.search_result_categories').removeClass("hide");
					
				},
	  			error: function(XMLHttpRequest, textStatus, errorThrown) {
	  				console.log("Error occured");
	  				$('#loadingMask').fadeOut(100);
	  			}
		});
    }
}


function populatePeopleFilters(eventType, specialityFilterSelectedValues, stateFilterSelectedValues, cityFilterSelectedValues, desktopCityFilterSelectedValues, desktopStateFilterSelectedValues, desktopSpecialityFilterSelectedValues) {
	if(mapQuestResultCount != 0 && people_result_object_full_response.length > 0){
		refinedRadiusResult = people_result_object_full_response;
		var MQaemKey = [];
		for(var i=0; i<refinedRadiusResult.length;i++){	
			MQaemKey.push(refinedRadiusResult[i].advisor_profilePage);
		}
		
		for(var i=0;i<people_result_object.length;i++){
			for(var j=0;j<MQaemKey.length;j++){
				if(MQaemKey[j] != people_result_object[i].advisor_profilePage){
					flag="notPresent";
				}else{
					flag = "present";
					break;
				}
			}
			if(flag=="notPresent"){
				people_result_object[i].people_result_type = 'AEM';
				AemOnlyResultsPeople.push(people_result_object[i]);
			}
		}
		
		for(var i=0;i<refinedRadiusResult.length;i++){
			refinedRadiusResult[i].people_result_type = 'MQ';
			MqAndAEMResultsPeople.push(refinedRadiusResult[i]);
		}
		
		for(var i=0; i<AemOnlyResultsPeople.length;i++){	
			MqAndAEMResultsPeople.push(AemOnlyResultsPeople[i]);           
		}
		
	}else if(mapQuestResultCount == 0 || people_result_object_full_response.length == 0){
		for(var i=0; i<people_result_object.length;i++){	
			people_result_object[i].people_result_type = 'AEM';
			AemOnlyResultsPeople.push(people_result_object[i]);           
		}
		MqAndAEMResultsPeople = AemOnlyResultsPeople;           
	}


	/*Populate Speciality Filter Options - Starts*/
	var specialityArray = [];
	var finalSpecialityArray = [];
	var uniqueSpecialityArray = [];
	var duplicateSpecialityArray = [];
	var specialityTagArray = [];
	var finalSpecialityTagArray = [];
	//var uniqueSpecialityTagArray = [];
	var duplicateSpecialityTagArray = [];
	var statesArray = [];
	var uniqueStatesArray = [];
	var cityArray = [];
	var uniqueCityArray = [];
	var cityListArray = [];
	var filterStartTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
	for(var i = 0; i < MqAndAEMResultsPeople.length; i++ ){
		
		
		
		specialityArray[i]=MqAndAEMResultsPeople[i].advisor_specialty;
		for(var k = 0; k < specialityArray[i].length; k++ ){
			if(uniqueSpecialityArray.indexOf(specialityArray[i].toUpperCase())==-1)
				uniqueSpecialityArray.push(specialityArray[i].toUpperCase());
		}
		
		statesArray[i]= MqAndAEMResultsPeople[i].advisor_state;
		if(statesArray[i]!=''&&statesArray[i]!=null&&statesArray[i]!=undefined)
		statesArray[i] = statesArray[i].toUpperCase();
		$.each(statesArray, function(i, el){
			if($.inArray(el, uniqueStatesArray) === -1) uniqueStatesArray.push(el);
		});
		
		/**/
		
		cityArray[i]=MqAndAEMResultsPeople[i].advisor_city;
	
	}
	
	for (i = 0; i < cityArray.length; i++) {
		if(cityArray[i]!=''&&cityArray[i]!=null&&cityArray[i]!=undefined){
			cityArray[i] = cityArray[i].toUpperCase();

			if (cityArray[i].indexOf(",") >= 0) {
				cityArray[i].split(",").forEach(function(item) {
					cityListArray[i] = item;
				});
			} else {
				cityListArray[i] = cityArray[i];
			}
		}
	}
		
	$.each(cityListArray, function(i, el){
		if($.inArray(el, uniqueCityArray) === -1) uniqueCityArray.push(el);
	});
	
	var aboutMe = [];
	var specialities = [];
	for (i = 0; i < uniqueSpecialityArray.length; i++) {
		var uniqueAdvSpl=[];
		var aboutmeCount = 0;
		var advSplchk = uniqueSpecialityArray[i];

		switch (advSplchk) {
			case 'MEDICAL':
				$('.medical').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'MEDICAL & LEGAL':
				$('.medical-and-legal').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'LEGAL':
				$('.legal').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'SPORTS AND ENTERTAINMENT GROUP':
				$('.sports-and-entertainment-group').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'BUSINESS WEALTH PARTNER':
				$('.business-wealth-partner').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'RETIREMENT':
				$('.retirement').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'INTERNATIONAL':
				$('.international').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'WEALTH':
				$('.wealth').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'MORTGAGE':
				$('.mortgage').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
			case 'MORTGAGE1':
				$('.mortgage1').each(function(){ uniqueAdvSpl.push($(this).attr('value'));});
			break;
		}
		var uniqueAdvSpl = uniqueAdvSpl.filter(function(itm, i, uniqueAdvSpl) {
			return i == uniqueAdvSpl.indexOf(itm);
		});

		
		if(uniqueAdvSpl != undefined && uniqueAdvSpl != null && uniqueAdvSpl != '') {				
			for(var specialityIndex = 0; specialityIndex < uniqueAdvSpl.length; specialityIndex++){
				aboutMe.push({"name": uniqueAdvSpl[specialityIndex], "value": uniqueSpecialityArray[i]});
			}
		}
	}
	
	aboutMe.forEach(function(value) {
	  var existing = specialities.filter(function(v, i) {
		return v.name == value.name;
	  });
	  if (existing.length) {
		var existingIndex = specialities.indexOf(existing[0]);
		specialities[existingIndex].value = specialities[existingIndex].value.concat(value.value);
	  } else {
		if (typeof value.value == 'string')
		  value.value = [value.value];
		specialities.push(value);
	  }
	});
		
	var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
													
	specialities.sort();
	uniqueStatesArray.sort();
	uniqueCityArray.sort();
	if(specialities!=null&&specialities!=undefined&&specialities!='') {
		specialityListCount = specialities.length;
		$("#specialityListCount").html(specialityListCount);
		$("#specialityListCountM").html(specialityListCount);
	}
	var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
	for(var specialityCount = 0; specialityCount < specialities.length; specialityCount++){
		$('.people_filter_desktop .search_by_filter.search_detail_speciality .search_detail_speciality_level').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span class='sun-checked'><input type='checkbox' class='suntrust-checkbox' value='"+specialities[specialityCount].value+"' name='"+specialities[specialityCount].name+"' title='"+specialities[specialityCount].name+"' data-search-filter='" + specialities[specialityCount].value + "' /></span></div><label for='" + specialities[specialityCount].name + "'class='suntrust-checkbox-label'>"+specialities[specialityCount].name+"</label></div>");
		$('.mobile_filter_search .people_filter_mobile .mobile-speciality').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span class='sun-checked'><input type='checkbox' class='suntrust-checkbox' value='"+specialities[specialityCount].value+"' name='"+specialities[specialityCount].name+"' title='"+specialities[specialityCount].name+"' data-search-filter='" + specialities[specialityCount].value + "' /></span></div><label for='" + specialities[specialityCount].name + "'class='suntrust-checkbox-label'>"+specialities[specialityCount].name+"</label></div>");
	}
	
	


	/*Populate Speciality Filter Options - Ends*/

	/*Populate State Filter Options - Starts*/


	for(var statesCount = 0; statesCount < uniqueStatesArray.length; statesCount++){
		var stateValue= uniqueStatesArray[statesCount];
		$('.people_filter_desktop .search_detail_state .search_detail_state_city_select').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+stateValue+"'class='suntrust-checkbox' value='"+stateValue+"' name='"+stateValue+"' aria-label='"+stateValue+"' /></span></div><label for='"+stateValue+"'class='suntrust-checkbox-label'>"+stateValue+"</label></div>");
		$('.people_filter_mobile .mobile-state').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input id='"+stateValue+"'type='checkbox' class='suntrust-checkbox' value='"+stateValue+"' name='"+stateValue+"' aria-label='"+stateValue+"' /></span></div><label for='"+stateValue+"'class='suntrust-checkbox-label'>"+stateValue+"</label></div>");
	}
	
	

	//retainFilterValues('.people_filter_desktop .search_detail_state', desktopStateFilterSelectedValues);
	/*Populate State Filter Options - Ends*/

	/*Populate City Filter Options - Starts*/



	for(var cityCount = 0; cityCount < uniqueCityArray.length; cityCount++){
		$('.people_filter_desktop .search_detail_city .search_detail_state_city_select').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueCityArray[cityCount]+"'class='suntrust-checkbox' value='"+uniqueCityArray[cityCount]+"' name='"+uniqueCityArray[cityCount]+"' aria-label='"+uniqueCityArray[cityCount]+"' /></span></div><label for='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox-label'>"+uniqueCityArray[cityCount]+"</label></div>");
		$('.people_filter_mobile .mobile-city').append("<div class='sun-checkbox-input-field'><div class='sun-checkbox-input-container'><span><input type='checkbox' id='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox' value='"+uniqueCityArray[cityCount]+"' name='"+uniqueCityArray[cityCount]+"' aria-label='"+uniqueCityArray[cityCount]+"' /></span></div><label for='"+uniqueCityArray[cityCount]+"' class='suntrust-checkbox-label'>"+uniqueCityArray[cityCount]+"</label></div>");
		}
	irrelaventDivsRemover();
	var numberOfStates = $('.people_filter_desktop .search_detail_state .search_detail_state_city_select').children().length;
	var numberOfSpecialities = $('.people_filter_desktop .search_by_filter.search_detail_speciality .search_detail_speciality_level').children().length;
	var numberOfCities = $('.people_filter_desktop .search_detail_city .search_detail_state_city_select').children().length;
	$('.people_filter_desktop .search_detail_city h4 span').text(numberOfCities);
	$('.people_filter_desktop .city-count').text(numberOfCities);
	$('.people_filter_mobile .city-count').text(numberOfCities);
	$('.people_filter_desktop .search_detail_state h4 span').text(numberOfStates);
	$('.people_filter_mobile .state-count').text(numberOfStates);
	$("#specialityListCount").html(numberOfSpecialities);
	$("#specialityListCountM").html(numberOfSpecialities);
	
	if(window.innerWidth<768) {
		if(eventType == "newSearch"){
			selectAllFiltersPeople();	
		}else if(eventType == "refineResult"){
			//if(!peopleSearchMobileRadiusChangedFlag){
				retainPeopleFilterValues('.people_filter_mobile .mobile-speciality', specialityFilterSelectedValues);
				retainPeopleFilterValues('.people_filter_mobile .mobile-state', stateFilterSelectedValues);
				retainPeopleFilterValues('.people_filter_mobile .mobile-city', cityFilterSelectedValues);
			/*}else{
				selectAllFiltersPeople();	
			}*/
		}	
	}else{
		//selectAllFiltersPeople();
		if(eventType == "newSearch"){
			selectAllFiltersPeople();	
		}else if(eventType == "refineResult"){
			retainPeopleFilterValues('.people_filter_desktop .search_detail_speciality',desktopSpecialityFilterSelectedValues);
			retainPeopleFilterValues('.people_filter_desktop .search_detail_state', desktopStateFilterSelectedValues);
			retainPeopleFilterValues('.people_filter_desktop .search_detail_city', desktopCityFilterSelectedValues);
		}
	}
	/*Populate City Filter Options - Ends*/
	
	$('.search_result_filters.col-lg-4.col-md-4.col-sm-4.hidden-xs').show();
	$('.search_result_filters.col-lg-4.col-md-4.col-sm-4.hidden-xs').removeClass("hide");
	//$('.people_result_show_more_results').removeClass("hide");
	var specialityFilterCount = $('.people_filter_desktop .search_detail_speciality_level').children().length; 
	var stateFilterCount = $('.search_detail_state .search_detail_state_city_select').children().length;
	var cityFilterCount = $('.search_detail_city .search_detail_state_city_select').children().length;
	if(specialityFilterCount !=0 || stateFilterCount != 0 || cityFilterCount !=0 ){
		$('.people_filter_desktop').show();
		$('.people_filter_mobile').removeClass("hide");
	}
	$('.people_filter_container').removeClass("hide");
	var filterEndTime = new Date().getHours()+":"+new Date().getMinutes()+":"+ new Date().getSeconds();
}

$(document).on('click', '.locations_filter_mobile .mobile_filter_submit_reset .submit', function(e){
    e.preventDefault();
    var selecteRadius = $('.locations_filter_mobile .filter_tog.mobile-radius-filter .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
	locationSearchHandler(selecteRadius,"refineResult");
    $(this).parents('.mobile_filter_search').animate({left: "-320px"});
	
	setTimeout(function() {
    						passSearchValuesToAnalyticEngine("mobileView");
    						}, 1000);
});

$(document).on('click', '.locations_filter_mobile .mobile_filter_submit_reset .reset', function(e){
    e.preventDefault();
    selectAllFilters();
    $('.locations_filter_mobile .mobile-radius-filter .search_detail_radius_select span').text("10 Miles");
});

$(document).on('click', '.resources_filter_mobile .suntrust-promo-cta-button.suntrust_apply', function(e){
	e.preventDefault();  
	refineFilterResources("refineResult");
	$(this).parents('.mobile_filter_search').animate({left: "-320px"});
});
$(document).on('click', '.resource_filter_checkTypes .filter_show_hide input', function(e){
	var classLevel = $(this).parents().hasClass("search_detail_services_level1");
	var checkCondition=false;
	var resourceFilterFlag=true;
	var numberOfLevel1Tags = $(this).parents(".search_detail_services_level1").find("input").length;
    var uncheckedFieldCounter=0;
	if($(this).parents('.sun-checkbox-input-field').next().hasClass('search_detail_services_level1')) {
		if($(this).parents('.sun-checkbox-input-field').next('.search_detail_services_level1').is(':visible')) {
			$(this).parents('.sun-checkbox-input-field').next().find('.sun-checkbox-input-field span').removeClass('sun-checked');
			$(this).parents('.sun-checkbox-input-field').next().find('.sun-checkbox-input-field span input').prop("checked",false);
			$(this).parents('.sun-checkbox-input-field').next().slideToggle();
		}
		else {
			$(this).parents('.sun-checkbox-input-field').next().slideToggle();
			$(this).parents('.sun-checkbox-input-field').next().find('.sun-checkbox-input-field span').addClass('sun-checked');
			$(this).parents('.sun-checkbox-input-field').next().find('.sun-checkbox-input-field span input').prop("checked",true);
		}
	}
	if(classLevel){
		$(this).parents(".search_detail_services_level1").find("input").each(function(){
			checkCondition = $(this).is(":checked");
			if(checkCondition == false){
				$(this).parents(".search_detail_services_level1").prev().find("input").addClass("dontAdd");
				resourceFilterFlag=false;
				uncheckedFieldCounter = uncheckedFieldCounter+1;
			}
			
		});
		if(uncheckedFieldCounter == numberOfLevel1Tags)
        	resourceFilterFlag = true;
		if(resourceFilterFlag){
			$(this).parents(".search_detail_services_level1").find("input").each(function(){
				$(this).parents(".search_detail_services_level1").prev().find("input").removeClass("dontAdd");
			});		
		}
	}
	
	
});
$(document).on('click', '.resources_filter_mobile .suntrust-promo-cta-button.suntrust_reset', function(e){
	e.preventDefault();
	selectAllFiltersResources(); 
});

$('.locations_filter_desktop .search_detail_radius_select select').on("change", function(e){
    var selectedRadius = $(this).val();
    locationSearchHandler(selectedRadius,"refineResult");
});

/*Location Search Ends */

$(document).on('click', '.people_filter_mobile .mobile_filter_submit_reset .submit', function(e){
    e.preventDefault();
    var selecteRadius = $('.people_filter_mobile .filter_tog.mobile-radius-filter .search_detail_radius_select span').text();
    selecteRadius = selecteRadius.split("Miles");
	selecteRadius = selecteRadius[0].trim();	
	peopleSearchHandler(selecteRadius,"refineResult");
    $(this).parents('.mobile_filter_search').animate({left: "-320px"});
    
    setTimeout(function() {
		passSearchValuesToAnalyticEngine("mobileView");
		}, 1000);
	
});

$(document).on('click', '.people_filter_mobile .mobile_filter_submit_reset .reset', function(e){
    e.preventDefault();
    selectAllFiltersPeople();
    $('.people_filter_mobile .mobile-radius-filter .search_detail_radius_select span').text("10 Miles");
    
});


$('.people_filter_desktop .search_detail_radius_select select').on("change", function(e){
    var selectedRadius = $(this).val();
    peopleSearchHandler(selectedRadius,"refineResult");
});
 
//START ANALYTICS TRACKING CODE

setTimeout(function() {
	passSearchValuesToAnalyticEngine("desktopView");
}, 7000);

function passSearchValuesToAnalyticEngine(view)
{
	var currentPageURL = window.location.href;
	var tabName = currentPageURL.split("#")[1];	
	tabName = (tabName === undefined) ? 'all_results' : tabName;
	
	if (true) {
		switch (tabName) {
		case 'all_results':
			passAllResultsSearchValuesToAnalyticEngine(tabName);
		break;
		
		case 'locations':
			passLocationSearchValuesToAnalyticEngine(tabName,view);
		break;
		
		case 'people':
			passPeopleSearchValuesToAnalyticEngine(tabName,view);
		break;
		
		case 'products':
			passProductsSearchValuesToAnalyticEngine(tabName);
		break;
		
		case 'faqs':
			passFaqsSearchValuesToAnalyticEngine(tabName);
		break;
		
		case 'resources':
			passResourcesSearchValuesToAnalyticEngine(tabName);
		break;
		}
	
	}
}


$(".search_result_categories a.analytics-placeholder-allsch,a.analytics-placeholder-locsch,a.analytics-placeholder-peoplesch,a.analytics-placeholder-prodsch,a.analytics-placeholder-faqsch,a.analytics-placeholder-resourcesch").on("click", function() {
	var tabName = $(this).attr("index");
	var searchInput = $("#search .suntrust_search").val();
	if(true && tabName != null)
	{
		switch (tabName) {
			case 'all_results':
				window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
				passAllResultsSearchValuesToAnalyticEngine(tabName);
			break;
			
			case 'people':
				window.dataLayer.pageSet = "STcom_AEM_PF_Search";
				setTimeout(function() {
					passPeopleSearchValuesToAnalyticEngine(tabName,"desktopView");
					}, 1000);
			break;
			
			case 'locations':
				window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
				setTimeout(function() {
					passLocationSearchValuesToAnalyticEngine(tabName,"desktopView");
					}, 1000);
			break;
			
			case 'products':
				window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
				passProductsSearchValuesToAnalyticEngine(tabName);
			break;
			
			case 'faqs':
				window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
				passFaqsSearchValuesToAnalyticEngine(tabName);
			break;
			
			case 'resources':
				window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
				passResourcesSearchValuesToAnalyticEngine(tabName);
			break;
		
		}
	}
	
});




$("select#search_mobile_tab_navigation").on("change", function() {
	  var tabName = $(this).val();
	  if (true) 
	  {
		  switch (tabName) 
		  {
				case 'all_results':
					window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
					passAllResultsSearchValuesToAnalyticEngine(tabName);
				break;
				
				case 'locations':
					window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
					setTimeout(function() {
						passLocationSearchValuesToAnalyticEngine(tabName,"mobileView");
						}, 1000);
				break;
				
				case 'people':
					window.dataLayer.pageSet = "STcom_AEM_PF_Search";
					setTimeout(function() {
						passPeopleSearchValuesToAnalyticEngine(tabName,"mobileView");
						}, 1000);
				break;
				
				case 'products':
					window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
					passProductsSearchValuesToAnalyticEngine(tabName);
				break;
				
				case 'faqs':
					window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
					passFaqsSearchValuesToAnalyticEngine(tabName);
				break;
				
				case 'resources':
					window.dataLayer.pageSet = "STcom_AEM_FindUs_Search";
					passResourcesSearchValuesToAnalyticEngine(tabName);
				break;
			}
	  
	  }
	
});


// All Results analytics tracking
function passAllResultsSearchValuesToAnalyticEngine(tabName)
{
	var allResultCount = $('.number-of-results-in-all-category-search').val();
	window.dataLayer.pageName = "STcom|Search|AllResults";
	var searchInput = $("#search .suntrust_search").val();
	
	window.dataLayer.eVar17 = null;
	window.dataLayer.eVar15 = null;
	window.dataLayer.prop2 = null;
	window.dataLayer.prop4 = null;
	window.dataLayer.eVar4 = null;
	window.dataLayer.prop27 = null;
	
	window.dataLayer.prop1 = searchInput;
	
	if(allResultCount != 0)
	{	
		window.dataLayer.prop2 = allResultCount;
		
	}
	else
	{
		window.dataLayer.prop2 = "Zero";
	}
	  
	setTimeout(function() {
		Bootstrapper.ensEvent.trigger('STcom_AEM_FindUs_Search');
	}, 1000);
}



//Location analytics tracking
function passLocationSearchValuesToAnalyticEngine(tabName,view)
{
	var serviceListArray = [];
	var commaSeperatedServiceList = "";
	var searchInput = $("#search .suntrust_search").val();
	LocationResultCount = $(".locations_result_count").text();
	
	var n = view.localeCompare("mobileView");
	if(n != 0)
	{
		$('.search_detail_services_level0 .sun-checkbox-input-container .sun-checked').each(function () {
			var service = $(this).find("input[type='checkbox']").val();
			serviceListArray.push(service);
		});
	}
	else
	{
		$('.mobile-services .sun-checkbox-input-container .sun-checked').each(function () {
			var service = $(this).find("input[type='checkbox']").val();
			serviceListArray.push(service);
		});
	}
	
	serviceListArray = $.unique(serviceListArray);
	
	
	$.each(serviceListArray , function (index, value){
		if(value !="")
		{
			if(commaSeperatedServiceList == "")
			{
				commaSeperatedServiceList = value;	
			}
			else
			{
				commaSeperatedServiceList = commaSeperatedServiceList + ";" + value;
			}
		}
	});
	
	if(true)
	{
		
		if(LocationResultCount != 0)
		{	
			window.dataLayer.prop2 = LocationResultCount;
			window.dataLayer.eVar17 = null;
			window.dataLayer.eVar15 = null;
			window.dataLayer.eVar15 = searchInput;
			
		}
		else
		{
			window.dataLayer.prop2 = "Zero";
			window.dataLayer.eVar15 = null;
			window.dataLayer.eVar17 = null;
			window.dataLayer.eVar17 = searchInput;
		}
		
		window.dataLayer.pageName = "STcom|Search|LocationResults";
		window.dataLayer.prop1 = searchInput;	
		window.dataLayer.prop27 = commaSeperatedServiceList;
		
		setTimeout(function() {
			Bootstrapper.ensEvent.trigger('STcom_AEM_FindUs_Search');
		}, 1000);
	}

}


//People analytics tracking
function passPeopleSearchValuesToAnalyticEngine(tabName,view)
{
	
	var searchResultCount = $(".people_result_count").text();
	var CityListArray = [];
	var StateListArray = [];
	var AdvisorSpecialtyListArray = [];

	var commaSeperatedCityList = "";
	var commaSeperatedStateList = "";
	var commaSeperatedAdvisorSpecialtyList = "";
	var text = "";
	
	var n = view.localeCompare("mobileView");
	if (n != 0) {
		// City
		$('#cityList .sun-checkbox-input-field .sun-checkbox-input-container .sun-checked').each(function() {
		var cityName = $(this).find("input[type='checkbox']").val();
		CityListArray.push(cityName);
		});

		// State
		$('#stateList .sun-checkbox-input-field .sun-checkbox-input-container .sun-checked').each(function() {
		var StateName = $(this).find("input[type='checkbox']").val();
		StateListArray.push(StateName);
		});

		// About me
		$('.search_detail_speciality_level .sun-checkbox-input-field .sun-checkbox-input-container .sun-checked').each(function() {
		var advisorSpecialtyName = $(this).find("input[type='checkbox']").val();
		AdvisorSpecialtyListArray.push(advisorSpecialtyName);
		});
	}

	else {
		// City
		$('.mobile-city .sun-checkbox-input-field .sun-checkbox-input-container .sun-checked').each(function() {
		var cityName = $(this).find("input[type='checkbox']").val();
		CityListArray.push(cityName);
		});

		// State
		$('.mobile-state .sun-checkbox-input-field .sun-checkbox-input-container .sun-checked').each(function() {
		var StateName = $(this).find("input[type='checkbox']").val();
		StateListArray.push(StateName);
		});

		// About me
		$('.mobile-speciality .sun-checkbox-input-field .sun-checkbox-input-container .sun-checked').each(function() {
		var advisorSpecialtyName = $(this).find("input[type='checkbox']").val();
		AdvisorSpecialtyListArray.push(advisorSpecialtyName);
		});

	}

	CityListArray = $.unique(CityListArray);
	StateListArray = $.unique(StateListArray);
	AdvisorSpecialtyListArray = $.unique(AdvisorSpecialtyListArray);

	$.each(CityListArray, function(index, value) {
		if (value != "") {
		if (commaSeperatedCityList == "") {
			commaSeperatedCityList = value;
		} else {
			commaSeperatedCityList = commaSeperatedCityList + "," + value;
		}
		}
	});

	text = commaSeperatedCityList;
	$.each(StateListArray, function(index, value) {
		if (value != "") {
		if (commaSeperatedStateList == "") {
			commaSeperatedStateList = value;
		} else {
			commaSeperatedStateList = commaSeperatedStateList + "," + value;
		}
		}
	});

	if (text != "") {
		var commaSeperatedStateText = (commaSeperatedStateList != "") ? "," + commaSeperatedStateList : "";
		text = text + commaSeperatedStateText;
	} else {
		text = commaSeperatedStateList;
	}

	$.each(AdvisorSpecialtyListArray, function(index, value) {
		if (value != "") {
			if (commaSeperatedAdvisorSpecialtyList == "") {
				commaSeperatedAdvisorSpecialtyList = value;
			} else {
				commaSeperatedAdvisorSpecialtyList = commaSeperatedAdvisorSpecialtyList + "," + value;
			}
		}
	});

	if (text != "") {
		var commaSeperatedAdvisorSpecialtyText = (commaSeperatedAdvisorSpecialtyList != "") ? "|" + commaSeperatedAdvisorSpecialtyList : "";
		text = text + commaSeperatedAdvisorSpecialtyText;
	} else {
		text = commaSeperatedAdvisorSpecialtyList;
	}

	var searchTextKeyword = $("#search .suntrust_search").val();

	var semiColonSeperatedAdvisorSpecialtyList = commaSeperatedAdvisorSpecialtyList.replace(/,/g, ";");

	if (true) {
		window.dataLayer.prop1 = searchTextKeyword;
		window.dataLayer.pageName = "STcom|Search|PeopleResults";

		window.dataLayer.prop2 = null;
		window.dataLayer.prop4 = null;
		window.dataLayer.eVar4 = null;
		window.dataLayer.eVar15 = null;
		window.dataLayer.eVar17 = null;
		window.dataLayer.prop27 = null;

		if (searchResultCount > 0) {
		window.dataLayer.prop2 = searchResultCount;
		window.dataLayer.prop4 = "PeopleFinder|PeopleFound";
		window.dataLayer.eVar4 = "PeopleFinder|PeopleFound";
		window.dataLayer.eVar15 = "peoplefinder|" + text;
		window.dataLayer.prop27 = semiColonSeperatedAdvisorSpecialtyList;
		} else {
		window.dataLayer.prop2 = "Zero";
		window.dataLayer.prop4 = "PeopleFinder|NoPeopleFound";
		window.dataLayer.eVar4 = "PeopleFinder|NoPeopleFound";
		
		window.dataLayer.prop27 = semiColonSeperatedAdvisorSpecialtyList;
		window.dataLayer.eVar17 = "peoplefinder|" + text;

		}
		Bootstrapper.ensEvent.trigger('STcom_AEM_PF_Search');
	}
}


//Products analytics tracking
function passProductsSearchValuesToAnalyticEngine(tabName)
{
	var productResultCount = $(".products_result_count").text();
	window.dataLayer.pageName = "STcom|Search|ProductResults";
	var searchInput = $("#search .suntrust_search").val();
	
	
	window.dataLayer.eVar17 = null;
	window.dataLayer.eVar15 = null;
	window.dataLayer.prop2 = null;
	window.dataLayer.prop4 = null;
	window.dataLayer.eVar4 = null;
	window.dataLayer.prop27 = null;
	
	window.dataLayer.prop1 = searchInput;
	
	if(productResultCount != 0)
	{	
		window.dataLayer.prop2 = productResultCount;
		
	}
	else
	{
		window.dataLayer.prop2 = "Zero";
	}
	  
	setTimeout(function() {
		Bootstrapper.ensEvent.trigger('STcom_AEM_FindUs_Search');
	}, 1000);
}


//Faqs analytics tracking
function passFaqsSearchValuesToAnalyticEngine(tabName)
{
	var faqResultCount = $(".faqs_result_count").text();
	window.dataLayer.pageName = "STcom|Search|FAQResults";
	var searchInput = $("#search .suntrust_search").val();
	
	window.dataLayer.eVar17 = null;
	window.dataLayer.eVar15 = null;
	window.dataLayer.prop2 = null;
	window.dataLayer.prop4 = null;
	window.dataLayer.eVar4 = null;
	window.dataLayer.prop27 = null;
	
	window.dataLayer.prop1 = searchInput;
	
	if(faqResultCount != 0)
	{	
		window.dataLayer.prop2 = faqResultCount;
		
	}
	else
	{
		window.dataLayer.prop2 = "Zero";
	}
	  
	setTimeout(function() {
		Bootstrapper.ensEvent.trigger('STcom_AEM_FindUs_Search');
	}, 1000);
}


//Resources analytics tracking

function passResourcesSearchValuesToAnalyticEngine(tabName)
{
	console.log(tabName + " tab clicked.");
	var resourcesResultCount = $(".resources_result_count").text();
	window.dataLayer.pageName = "STcom|Search|ResoucesResults";
	var searchInput = $("#search .suntrust_search").val();
	
	console.log("resourcesResultCount : " + resourcesResultCount);
	console.log("searchInput : " + searchInput);
	
	window.dataLayer.eVar17 = null;
	window.dataLayer.eVar15 = null;
	window.dataLayer.prop2 = null;
	window.dataLayer.prop4 = null;
	window.dataLayer.eVar4 = null;
	window.dataLayer.prop27 = null;
	
	window.dataLayer.prop1 = searchInput;
	
	if(resourcesResultCount != 0)
	{	
		window.dataLayer.prop2 = resourcesResultCount;
		
	}
	else
	{
		window.dataLayer.prop2 = "Zero";
	}
	  
	setTimeout(function() {
		Bootstrapper.ensEvent.trigger('STcom_AEM_FindUs_Search');
	}, 1000);
}
}
});