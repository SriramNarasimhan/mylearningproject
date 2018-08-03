$(document).ready(function(e){
	var keys;
	var searchIn = "";
	var searchForm = "";
	var searchInputVisibleFlag = $('input.typeahead').length;
	var searchLocInputVisibleFlag = $('input.loc_typeahead').length;
	console.log("searchInputVisibleFlag:"+searchInputVisibleFlag);

	if(searchInputVisibleFlag > 0) {
		getSearchHashValue();	
		
		$.ajax({
			type: "GET",
			url: '/dotcom/search/typeahead',
			cache: false,
			data: {"searchpath":$('input.typeahead').attr("data-searchpath")}, // DAM dictionary file path

			success: function(data, textStatus, jqXHR) {
				keys = data;
				console.log("searchIn1:"+ searchIn);
				$.each($('input.typeahead'), function() {
					if($(this).data("cat")!=null && $(this).data("cat")!='' && 
							$(this).data("cat")!=undefined && $(this).data("cat").length > 0) {
						if($(this).data("cat") == 'locations') {
							setMinTypeahead($(this), filteredKeywords(data,$(this).data("cat")));
						} else {
							if($(this).hasClass('minThree')) {
								setMinTypeahead($(this), filteredKeywords(data,$(this).data("cat")));
							} else {
								setTypeahead($(this), filteredKeywords(data,$(this).data("cat")));
							}
						}
					} else {
						if($(this).hasClass('minThree')) {
							setMinTypeahead($(this), filteredKeywords(data, searchIn));
						} else if(searchIn == 'locations' || searchIn == 'people') {
							setMinTypeahead($(this), filteredKeywords(data, searchIn));
						} else {
							setTypeahead($(this), filteredKeywords(data, searchIn));
						}
					}
				})
			},

			error: function(XMLHttpRequest, textStatus, errorThrown) {
				console.log("error in typeahead response:"+ textStatus);
			}
		});
	}
	
	$(document).on("click",".search_result_categories ul.text-center a",function(){
		
		$('input.typeahead#search-input').typeahead('destroy');
		var tabName = $('.search_result_categories ul.text-center a.active').attr('index');
		console.log(tabName);
		if($('.search_result_categories ul.text-center a.active').attr('index').indexOf('people') > -1 ||
				$('.search_result_categories ul.text-center a.active').attr('index').indexOf('locations') > -1) {
			setMinTypeahead($('input.typeahead#search-input'), filteredKeywords(keys, tabName));
		} else {
			setTypeahead($('input.typeahead#search-input'), filteredKeywords(keys, tabName));
		}
	});
	
	/* Search menu selected on mobile Start */
	$('select.search_select').on('change', function(e) {
		tabName = $(this).val();
		$('input.typeahead#search-input').typeahead('destroy');
		if(tabName =='people' || tabName =='locations') {
			setMinTypeahead($('input.typeahead#search-input'), filteredKeywords(keys, tabName));
		} else {
			setTypeahead($('input.typeahead#search-input'), filteredKeywords(keys, tabName));
		}
	});
	/* Search menu selected on mobile End */
	
	$(document).on("click",".suntrust-get-directions-route-switch",function(){
    	$('input#MyLocationText').typeahead('destroy');
    	setMinTypeahead(("input#MyLocationText"), filteredKeywords(keys, "locations"));
	});

	var filteredKeywords = function(data,cat) {
		getSearchHashValue();
		var keywords = JSON.stringify(data['all_results'][0]).replace(/]|[[]/g, '').replace(/"/g,"").split(',');
		keywords = unique(keywords);
		
        if(searchIn == "all_results" || cat == "all_results") {
			keywords = JSON.stringify(data['all_results'][0]).replace(/]|[[]/g, '').replace(/"/g,"").split(',');
        	keywords = unique(keywords);
        }
		else if(searchIn == "locations" || cat == "locations") {
			keywords = JSON.stringify(data['location_results'][0]).replace(/]|[[]/g, '').replace(/"/g,"").split(',');
            keywords = unique(keywords);
        }
		else if(searchIn == "people" || cat == "people") {
			keywords = JSON.stringify(data['people_results'][0]).replace(/]|[[]/g, '').replace(/"/g,"").split(',');
            keywords = unique(keywords);
        }
        else if(searchIn == "faqs" || cat == "faqs") {
			keywords = JSON.stringify(data['faq_results'][0]).replace(/]|[[]/g, '').replace(/"/g,"").split(',');
            keywords = unique(keywords);
        }
        else if(searchIn == "products" || cat == "products") {
			keywords = JSON.stringify(data['product_results'][0]).replace(/]|[[]/g, '').replace(/"/g,"").split(',');
            keywords = unique(keywords);
        }
        else if(searchIn == "resources" || cat == "resources") {
			keywords = JSON.stringify(data['resource_results'][0]).replace(/]|[[]/g, '').replace(/"/g,"").split(',');
            keywords = unique(keywords);
        }

		return keywords;
	}

    function unique(list) {
        var result = [];
        $.each(list, function(i, e) {
            if ($.inArray(e, result) == -1) result.push(e);
        });
        return result;
    }
	function getSearchHashValue(){
		if(window.location.hash == "#all_results" || window.location.hash == "#locations" || window.location.hash == "#people" ||window.location.hash == "#faqs" || window.location.hash == "#products" || window.location.hash == "#resources" )
			searchIn = window.location.hash;
		else
			searchIn = "#all_results";
		while(searchIn != "" && searchIn.charAt(0) === '#'){
			searchIn = searchIn.substr(1);
		}
		searchIn = searchIn.trim();
		console.log("searchIn:"+ searchIn);
    }

	var setTypeahead = function(elem, keywords) {
		$(elem).typeahead({
			hint: true,
			highlight: true,
			minLength: 1
		}, {
			name: 'keywords',
			source: substringMatcher(keywords),
			limit:$(elem).attr('data-suggestcount'),
			templates: {
				header: '<div class="suggested-name">Suggested Searches</div>'
			}
		});
	}
	var setMinTypeahead = function(elem, keywords) {
		$(elem).typeahead({
			hint: true,
			highlight: true,
			minLength: 3
		}, {
			name: 'keywords',
			source: substringMatcher(keywords),
			limit:$(elem).attr('data-suggestcount'),
			templates: {
				header: '<div class="suggested-name">Suggested Searches</div>'
			}
		});
	}
	var substringMatcher = function(strs) {
		return function findMatches(q, cb) {
			var matches, substringRegex;
			matches = [];
			try {
			substrRegex = new RegExp(q, 'i');
			} catch(e){}
			$.each(strs, function(i, str) {
				if (substrRegex.test(str)) {
					matches.push(str);
				}
			});
			
			cb(matches);
		};
	};
	
});

