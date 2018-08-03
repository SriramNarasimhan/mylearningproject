try {
console.log("IN MAP JS");
//let directionsLayer;
var directionsLayer;
var curlatitude;
var curlongitude;
var curlatitudes=[];
var curlongitudes=[];
var geolatitude;
var geolongitude;
var geocoord=[];
var currentAddress;
var coords;
var dest;
var cityArr=[];
var stateArr=[];
var autoComp=[];
//General map object
var advisorMapObj = {};
advisorMapObj.mapContainerId = 'AdvisorLocationsMap';
advisorMapObj.narrativeContainerId = 'RouteNarration';
//Set map  zoom level
advisorMapObj.zoomLevel = 5;
//Set center
advisorMapObj.defaultCenter = {};
advisorMapObj.defaultCenter.Lat = 33.65;
advisorMapObj.defaultCenter.Lng = -84.42;
//Save route search data
advisorMapObj.startText = "";
advisorMapObj.startLat = "";
advisorMapObj.startLng = "";
advisorMapObj.endText = "";
advisorMapObj.endLat = "";
advisorMapObj.endLng = "";
advisorMapObj.profileEmailName = "";
//Save map layers
advisorMapObj.layers = [];

advisorMapObj.startMap = function () {

    L.mapquest.key = 'Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850';
    var baseLayer = L.mapquest.tileLayer('map');

    map = L.mapquest.map(advisorMapObj.mapContainerId, {
        center: [advisorMapObj.defaultCenter.Lat, advisorMapObj.defaultCenter.Lng],
        zoom: advisorMapObj.zoomLevel,
        layers: baseLayer
    });
    map.addControl(L.mapquest.control());
    mapLayer = L.control.layers({ 
        'Map': baseLayer,
        'Satellite': L.mapquest.tileLayer('satellite'),
        'Hybrid': L.mapquest.tileLayer('hybrid'),
        'Light': L.mapquest.tileLayer('light'),
        'Dark': L.mapquest.tileLayer('dark')
    }, {}, { position: 'topleft'}).addTo(map);

};
//Set map size using parent width()
advisorMapObj.setMapSize = function () {    
    //The width of the map is 63% and the height 180px
    var $mapContainer = $('#AdvisorLocationsMap')
    var width = $mapContainer.parent().width();
    var height = width * 0.56;
    $mapContainer.width(width);
    $mapContainer.height(height);
};
advisorMapObj.removeAllLayers = function () {
	console.log('Inside clear layer--');
    for (var i = 0; i <= advisorMapObj.layers.length - 1; i++) {
        map.removeLayer(advisorMapObj.layers[i]);
    }

    advisorMapObj.layers = [];
};
advisorMapObj.initAddPinpoint = function () {
	console.log('Inside get Pin Points');
    var $resultSetInput = document.getElementById('hfResultSet');
    console.log('hfResultSet from page: '+$resultSetInput);
    var resultSet = $resultSetInput == null ? '' : $resultSetInput.value;
    console.log('Result: '+resultSet);
    return resultSet;
};
advisorMapObj.createPinpoint = function (poi, pinNumber) {    
    var result = poi.split("$");
    if (result[0] == '') return;
	var pinNo = 'pin_'+pinNumber;
    var icon = L.icon({
        iconUrl: '/content/dam/suntrust/us/en/elements/2017/map/' + (pinNumber <= 6 ? pinNo : 'map-default') + '.png',
        iconSize: [21, 28],
        iconAnchor: [11, 28,],
        popupAnchor: [0, -28],
    });

    var name = '<b style="color:#FF9933;width:200px;display:block">' + result[2] + '</b>';

    marker = L.marker([result[0], result[1]], { icon: icon }).bindPopup(name);

    return marker;
};
//Add Pinpoints to the map
advisorMapObj.addPinpoints = function () {
    try {
        //Clean Map and get Result Set
    	console.log('Inside add Pin Points');
        advisorMapObj.removeAllLayers();

        var resultSet = advisorMapObj.initAddPinpoint();
        if (resultSet == '') {
            map.setView([advisorMapObj.defaultCenter.Lat, advisorMapObj.defaultCenter.Lng], advisorMapObj.zoomLevel);
            return;
        }

        var compositeSet = resultSet.split(";");
        var resultSetLength = compositeSet.length - 1;
        console.log('result length: '+resultSetLength);
        var group = [],
        marker,
        features;        

        for (var i = 1; i <= resultSetLength - 1; i++) {
            // Add POI markers to the group
        	console.log('Location value: '+compositeSet[i]);
            marker = advisorMapObj.createPinpoint(compositeSet[i], i);
            group.push(marker);
            advisorMapObj.layers.push(marker);
        }


        // Add POI markers to the map and zoom to the features
        features = L.featureGroup(group).addTo(map);
        map.fitBounds(features.getBounds());
    } catch (ex) {
        console.log(ex)
    } finally {
    }
};
//This function takes initial request for Get Direction
advisorMapObj.showRoute = function (startLat, startLng, endLat, endLng, startVal, endVal) {
console.log("startLat, startLng, endLat, endLng, startVal, endVal::"+startLat+ startLng+ endLat+ endLng+ startVal+ endVal);
    advisorMapObj.removeAllLayers();
    advisorMapObj.findRoute(startLat, startLng, endLat, endLng, startVal, endVal);

    advisorMapObj.startText = startVal;
    advisorMapObj.startLat = startLat;
    advisorMapObj.startLng = startLng;
    advisorMapObj.endText = endVal;
    advisorMapObj.endLat = endLat;
    advisorMapObj.endLng = endLng;
    $('#divStartAddress').html(advisorMapObj.startText);
    $('#divEndAddressFooter').html(advisorMapObj.endText);
    $('#findUsRoutes .dirCityTitle').html(advisorMapObj.endText);
    $('#advisorMapTabSel').attr('class', 'maptab');
    //Events
    window.dataLayer.events = 'event3';
    //Bootstrap
    /*if (window.dataLayer.boostrapEnabled) {
        Bootstrapper.ensEvent.trigger('STcom_PF_Directions');
    }
    //Tracking
    if (!window.dataLayer.omnitureDisabled) {
        trackingObj.peopleFinder.trackDirections(advisorMapObj.profileEmailName);
    }*/
};
//Find Route and call display narrative
advisorMapObj.findRoute = function (startLat, startLng, endLat, endLng, startVal, endVal) {
    /*var directions = L.routing.directions().on('success:route', function (data) {
        //advisorMapObj.displayNarrative(data);
    });*/

    var directions;
    var fromZip, toZip; 
    console.log(" b4 data11:");


	if($('#people_map_select').is(":visible")) {
        toZip = $('#people_map_select').val();
    } else {
    	var endZip = $("#hfResultSet").val();
    	toZip = endZip.substring(endZip.lastIndexOf("$")+1,endZip.lastIndexOf(";"));
    }

    fromZip = $('#MyLocationText').val();


	//console.log("fromZip:"+fromZip);
    //console.log("toZip:"+toZip);
    //console.log("startVal:"+startVal);
    //console.log("endVal:"+endVal);


	L.mapquest.key = 'Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850';
	directions = L.mapquest.directions();
	directions.route({
	  start: startVal,
      end: endVal,
	  /*options: {
		timeOverage: 25,
		maxRoutes: 3,
	  }*/
	}, directionsCallback);

	function directionsCallback(error, response) {


	  /*var map = L.mapquest.map(advisorMapObj.mapContainerId, {
		center: [advisorMapObj.defaultCenter.Lat, advisorMapObj.defaultCenter.Lng],
		layers: L.mapquest.tileLayer('light'),
		zoom: advisorMapObj.zoomLevel
	  });*/

		if(directionsLayer != null && directionsLayer !=undefined)
			directionsLayer.remove();
            directionsLayer = L.mapquest.directionsLayer({
            directionsResponse: response
        }).addTo(map);

        advisorMapObj.displayNarrative(response);
        //console.log("directionsLayer:"+directionsLayer);
	  return map;
	}
	console.log("directions:"+directions);

    /*advisorMapObj.layers.push(route);

    map.addLayer(route);*/
};
/*This function calculates route narration between two points and puts them on page*/
advisorMapObj.displayNarrative = function (data) {


    if (data.route) {
        var legs = data.route.legs, html = '', i = 0, j = 0, trek, maneuver;
        html += '<table><tbody>';

        if (legs != null && legs.length!=null) {
            for (; i < legs.length; i++) {
                for (j = 0; j < legs[i].maneuvers.length; j++) {
                    maneuver = legs[i].maneuvers[j];
                    html += '<tr>';
                    html += '<td>';

                    if (maneuver.iconUrl) {
                        //This is required for a known issue on MapQuest side
                        maneuver.iconUrl = maneuver.iconUrl.replace('httpss', 'https');
                        html += '<img src="' + maneuver.iconUrl + '">  ';
                    }

                    for (k = 0; k < maneuver.signs.length; k++) {
                        var sign = maneuver.signs[k];
                        if (sign && sign.url) {
                            //This is required for a known issue on MapQuest side
                            sign.url = sign.url.replace('httpss', 'https');
                            html += '<img src="' + sign.url + '">  ';
                        }
                    }

                    html += '</td><td>' + maneuver.narrative + '</td>';
                    html += '</tr>';
                }
            }
        }
        html += '</tbody></table>';
        document.getElementById(advisorMapObj.narrativeContainerId).innerHTML = html;
    }
};
//get directions complete callback
advisorMapObj.getDirectionsSuccess = function (data) {
console.log("in get direction Data: "+data);
    var isValid = true;
    var startInput = $('#MyLocationText');
    var endInput = $('#people_map_select');
	startInput.parent().removeClass('noValid');
	endInput.parent().removeClass('noValid');
    var startInputVal = $('#source').find('#MyLocationText').val() == undefined ? $('#source').find('#people_map_select').val():$('#source').find('#MyLocationText').val();
   // var myLocation = $('#advisorMyLocation');
    //$('#findUsRoutes ').find('.dirTextBoxHolder ').removeClass('noValid');
    //myLocation.removeClass('noValid');
    var resultSet = data.split('&');
	console.log("resultSet length"+resultSet.length);
	if(startInput.val().length < 3){
		isValid = false;
    	startInput.parent().addClass('noValid');
	}
    if (resultSet.length != 4) {
	console.log("not valid 1");
        isValid = false;
        // This validation to be commented after autocomplete is enabled
        if(startInput.val().length < 3)
        	startInput.parent().addClass('noValid');
        if(endInput.val().length < 3)
        	endInput.parent().addClass('noValid');
		return false;
    } else {
        if (resultSet[0] == 0 && resultSet[1] == 0) {
        	console.log("not valid 2");
            isValid = false;
            //startInput.parent().addClass('noValid');
            //myLocation.addClass('noValid');
			advisorMapObj.getLocationCoords();
        }
        if (resultSet[2] == 0 && resultSet[3] == 0) {
        	console.log("not valid 3");
            isValid = false;
            endInput.parent().addClass('noValid');
        }
    }
	console.log("isValid"+isValid);
    if($('#people_map_select').is(":visible")) {
        endInput = $('#destination').find('#people_map_select').val() == undefined ? $('#destination').find('#MyLocationText').val():$('#destination').find('#people_map_select').val();
    } else {
    	var endZip = $("#hfResultSet").val();
    	endInput = endZip.substring(endZip.lastIndexOf("$")+1,endZip.lastIndexOf(";"));
    }
    if (isValid) {
       // $('#GetDirections').addClass('dirResultsView');
        
    	if(/Android/i.test(navigator.userAgent)){
    		//window.open("google.navigation:q="+startInput.val()+","+endInput+"&mode=d" , '_system');
            if ($(window).width() < 767) {           
    			console.log('Android identified: windowsize less than 767');
    			window.open("https://www.google.com/maps/dir/?api=1&origin="+startInputVal+"&destination="+endInput+"&mode=d" , '_system');
    		} else {
    			console.log('Android identified: windowsize greater than 767');
    			advisorMapObj.showRoute(resultSet[0], resultSet[1], resultSet[2], resultSet[3], startInputVal, endInput);
    		} 
    	//else if(/iPhone|iPad|iPod/i.test(navigator.userAgent))
    	} else if(/iPhone/i.test(navigator.userAgent)){
    		//window.open("comgooglemaps://?saddr="+startInput.val()+"&daddr="+endInput+"&directionsmode=transit");
    		window.open("http://maps.apple.com/?saddr="+startInputVal+"&daddr="+endInput+"&directionsmode=transit");
    	} else { 
    		advisorMapObj.showRoute(resultSet[0], resultSet[1], resultSet[2], resultSet[3], startInputVal, endInput);
    	}
    }
};
//Validate Address
advisorMapObj.isValidAddress = function (address) {
    var regexAddressValidation = /^[a-zA-Z0-9\s\-\[\]\,\.\#\/]+$/;
    return (regexAddressValidation.test(address) && (address != ""));
};
advisorMapObj.initGeolocation = function () {
	console.log('initGeolocation called');
    var urlStartLoc = getParameterByName('start');
    var urlDestLoc = getParameterByName('destination');
    //urlStartLoc = decodeURIComponent(urlStartLoc);
    //urlDestLoc = decodeURIComponent(urlDestLoc);
    if((urlStartLoc != "" && urlStartLoc != null && urlStartLoc != undefined && urlStartLoc != "undefined") || (urlLocParam != "" && urlLocParam != null && urlLocParam != undefined && urlLocParam != "undefined")) {
        if ($("#MyLocationText").is(':visible')) {
            $("#MyLocationText").val(urlStartLoc);
            if ($("#people_map_select").is(':visible') && urlDestLoc != "" && urlDestLoc != null && urlDestLoc != undefined && urlDestLoc != "undefined") {
                $('#people_map_select').val(urlDestLoc).change();
            }
            
        }
    } else {
    	//advisorMapObj.getLocationCoords($('.suntrust-input-location-button-advisor'),'onload');
    }
    if($('.locationpage').is(':visible')) {
    	var urlLocParam = getParameterByName('location');
    	console.log("urlLocParam:"+urlLocParam);
    	if(urlLocParam != "" && urlLocParam != null && urlLocParam != undefined && urlLocParam != "undefined") {
    		$("#MyLocationText").val(urlLocParam);
            if (!navigator.userAgent.match(/(iPhone|iPod|iPad|Android|BlackBerry|IEMobile)/)) {
            	setTimeout(function () { $('.suntrust-secondary-button.submit-button').click(); }, 100);
            }
    	} else if(urlStartLoc != "" && urlStartLoc != null && urlStartLoc != undefined && urlStartLoc != "undefined") {
    		setTimeout(function () { $('.suntrust-secondary-button.submit-button').click(); }, 100);
    	}
    }
    if($('.peoplepage').is(':visible')) {
		var destParam = getParameterByName('destination');
		if($(".people_location_details_individual").length<2 && (getParameterByName('destination') == null || getParameterByName('destination') =='') && (urlStartLoc != null && urlStartLoc !='' && urlStartLoc !=undefined)) {
			setTimeout(function () { $('.suntrust-secondary-button.submit-button').click(); }, 100);
		} else if(destParam != null && destParam !='' && destParam !=undefined) {
			setTimeout(function () { $('.suntrust-secondary-button.submit-button').click(); }, 100);
		}
    }
    if(urlDestLoc != "" && urlDestLoc != null && urlDestLoc != undefined && urlDestLoc != "undefined") {
    	$("#MyLocationText").focus();
	if (navigator.userAgent.match(/(iPad|SM-T330NU)/)) {
		$('html, body').animate({ scrollTop: $('#source').offset().top }, 'slow');
		$('.people.suntrust-secondary-button.submit-button').click();
	}
    }
    
    $('.suntrust-input-location-button-advisor').click(function (event) {
    	console.log('Get geo location icon clicked');
    	//var $button = $(this);
        //advisorMapObj.getLocationCoords($button,'');
        advisorMapObj.getLocationCoords($('.suntrust-input-location-button-advisor'),'');
    	/*if($("#MyLocationText").attr("curLocationAdd") !== undefined){
    		var curLocationAdd = $("#MyLocationText").attr("curLocationAdd");
    		$("#MyLocationText").val(curLocationAdd);
    	} else {
    		if (navigator.geolocation) {
    			alert('Geolocation produced and error. Code: 1. Message: User denied Geolocation');
    		} else {
    			alert("Geolocation is not supported by this browser.");
    		}
    	}*/
    	
    	/*if($('.locationpage').is(':visible')) {
            console.log("locationpage");
            if(/Android/i.test(navigator.userAgent)){                		
            	if ($(window).width() >= 767) {           
            		console.log('Anbriod but tablet - load location direction');
            		$('.suntrust-secondary-button.submit-button').click();  			
            	}	
            } else if(/iPhone/i.test(navigator.userAgent) == false){    
            	console.log('Not an iphone or andriod load location direction');
            	$('.suntrust-secondary-button.submit-button').click();
            }
        }*/

    });

    };

advisorMapObj.getLocationCoords = function ($button, onload) {
	if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) { showLocation($button, position) },
			 function (ex) {
				 if(onload != 'onload')alert('Geolocation produced and error. Code: ' + ex.code + '. Message: ' + ex.message);
                 if ($(".location_phone.location_distance.clearfix").is(':visible')) {
                     $(".location_phone.location_distance.clearfix").hide();
                 }
			 });

    } else {
        if(onload != 'onload')alert("Geolocation is not supported by this browser.");
        if ($(".location_phone.location_distance.clearfix").is(':visible')) {
            $(".location_phone.location_distance.clearfix").hide();
        }
    }
};
function getXML(startVal) {
console.log('In XML');
    $.ajax({
        type: "POST",
        url: 'https://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850&outFormat=xml&location='+startVal,
        dataType: "xml",
        async:false,
		//async:true,
        success: function (response) {
            xmlParser(response);
        },
		error: function (error) {
			console.log('Error captured: '+error);
			console.log(error);
		}
    });

};

function xmlParser(response) {
console.log('Success parse XML');
	//var xml = $.parseXML(response);
	var xml = response;
	console.log("xml"+xml);
	var locationLength = $(xml).find("locations location").length;
	console.log("locationLength:"+locationLength);
	$(xml).find("locations location").each(function (i) {
		curlatitude = $(this).find('displayLatLng latLng lat').text();
		console.log("curlatitude:"+curlatitude);
		//curlatitudes.push(curlatitude);
		curlongitude = $(this).find('displayLatLng latLng lng').text();
		console.log("curlongitude:"+curlongitude);
		//curlongitudes.push(curlongitude);
		console.log("iter:"+i);
		
		/*if(i ==locationLength-1) {
		console.log("iter:"+i);
			geocoord.join('&'+curlatitude+'&'+curlongitude);
		}
		else{*/
        geocoord.push(curlatitude);
        geocoord.push(curlongitude);
        coords=geocoord.join('&');
    	console.log("coords: "+coords);
    	return false; // returning false as only location is required and mapquest 1st address is always the closest match.
			//}
		//geocoord.replaceWith(',','');
		//console.log("geocoord:"+geocoord);
	});
	//coords=geocoord.join('&');
	//console.log("coords: "+coords);
}

advisorMapObj.getZipFromAddress = function (addr) {
    var comma = addr.lastIndexOf(',');

    // remove everything before the dash
    var zip = addr.substring(comma+1);
    return zip;
};

//Ajax Call Get Geolocation
advisorMapObj.getGeolocation = function (startVal, endVal) {


    //startVal = advisorMapObj.getZipFromAddress(startVal).trim();
    //endVal = advisorMapObj.getZipFromAddress(endVal).trim();
	console.log("b4 getXml:"+startVal+"	::"+endVal);

	//if(startVal.indexOf('.')>0)
	 /*setTimeout(function(){ 
		getXML(startVal);
    }, 2000);*/
	getXML(startVal);
    getXML(endVal);
	//advisorMapObj.getDirectionsSuccess("start=30346&end=121+PERIMETER+CENTER%2C+Atlanta%2C+GA%2C+30346");
	advisorMapObj.getDirectionsSuccess(coords);
    coords=[];
    geocoord=[];
};



$(document).on('keypress','#MyLocationText',function(e) {
    if (e.keyCode === 13) {
    	if($('.people.suntrust-secondary-button.submit-button').length > 0) {
    		$('.people.suntrust-secondary-button.submit-button').trigger('click');
    	}
    	if($('.location.suntrust-secondary-button.submit-button').length > 0) {
    		$('.location.suntrust-secondary-button.submit-button').trigger('click');
    	}
    }
});
$(document).on('keypress','.addressDirection',function(e) {
    if (e.keyCode === 13) {
		$('.addressDirection').click();
    }
});
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function showLocation(button, position) {
	var latitude = position.coords.latitude;
	var longitude = position.coords.longitude;
	var $input = button.siblings('span').find('input:nth-child(2)');
	var address, street, city, state, zip;
	$.ajax({
		type: "POST",
		url: 'https://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850&outFormat=json&location='+latitude+','+longitude,
		dataType: "json",
		success: function (data) {
			console.log("data"+data);
			//console.log("data.results1"+JSON.stringify(data.results));

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
			console.log("findme"+JSON.stringify(findme));
			data = JSON.parse(JSON.stringify(findme));
			updateLocationFields(data, $input);
			/*if($('.suntrust-input-location-button-map').length > 0) {
				updateLocationFields(data, $('.suntrust-input-location-button-map').siblings('input:first'));
            }*/


		},
		error: function (xhRequest, ErrorText, thrownError) {
			ajaxError();
		}
	});
	}

	function updateLocationFields(data, $input) {
	console.log("updateLocationFields data.address"+data.address);
	currentAddress = data.address;
	if (data.address != '') {
		$input.typeahead('val',data.address);
    	//Saving address as attribute in input field to show in button click as there a delay with current process 
    	//$input.attr('curLocationAdd', data.address);

        if($('.locationpage').is(':visible')) {
            console.log("locationpage");
            if(/Android/i.test(navigator.userAgent)){                		
            	if ($(window).width() >= 767) {           
            		console.log('Anbriod but tablet - load location direction');
            		$('.suntrust-secondary-button.submit-button').click();  			
            	}	
            } else if(/iPhone/i.test(navigator.userAgent) == false){    
            	console.log('Not an iphone or andriod load location direction');
            	$('.suntrust-secondary-button.submit-button').click();
            }
        }

		if ($input.data('linked-id')) {
			var $linkedInput = $('#' + $input.data('linked-id'));
			if ($linkedInput.length > 0) {
				$linkedInput.val(data.address);
			}
		}
	}
	/*if (data.zip != '' || data.city != '' || data.state != '') {
		//ZIP
		var $zipField = $input.siblings('input.suntrust-input-location-zip');
		if (data.zip != '' && $zipField.length > 0) {
			$zipField.val(data.zip);
		}
		//City
		var $cityField = $input.siblings('input.suntrust-input-location-city');
		if (data.city != '' && $cityField.length > 0) {
			$cityField.val(data.city);
		}
		//City
		var $stateField = $input.siblings('input.suntrust-input-location-state');
		if (data.state != '' && $stateField.length > 0) {
			$stateField.val(data.state);
		}
		//Clear location fields on text change
		var $locationFields = $input.siblings('input.suntrust-input-location-field');
		if ($locationFields.length > 0) {
			$input.keydown(function (event) {
				// Allow: tab, escape, and enter
				if (event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 ||
				// Allow: Ctrl+A
			(event.keyCode == 65 && event.ctrlKey === true) ||
				// Allow: home, end, left, right
			(event.keyCode >= 35 && event.keyCode <= 39)) {
					// let it happen, don't do anything
					return;
				}
				else {
					$locationFields.val('');
				}
			});
		}
	}*/
	}



	function locationGetDirection(startLoc){
    	console.log("Branch location get direction clicked");
		geocoord = [];
		var myLocation = $('.location_map_input');
        var input = myLocation.find('input');
        var locationVal = input.typeahead('val');
        if(startLoc)
        	locationVal = startLoc;
		//var $resultSetInput = document.getElementById('MyLocationText');
        console.log("startlocationVal:"+locationVal);
		if(locationVal == null || locationVal == undefined) {
			console.log("startlocationVal not defined");
		}
        myLocation.removeClass('noValid');
        if (!advisorMapObj.isValidAddress(locationVal)) {
            myLocation.addClass('noValid');
            $('#MyLocationText').focus();
        }
        else {
            //var endVal = dest;
        	var resultSet = advisorMapObj.initAddPinpoint();
			//var $branchAddress = document.getElementById('people_map_select');
        	var compositeSet = resultSet.split(";");
        	var result = compositeSet[1].split("$");
			var endVal = result[2];
            console.log("endlocationVal:"+endVal);
            //$('#txtRouteStartAddress').val(locationVal);
            //$('#txtRouteEndAddress').val(endVal);
            advisorMapObj.getGeolocation(locationVal, endVal);
        }
    }
	
//Document Ready
$(document).ready(function () {
    

	advisorMapObj.initGeolocation();
    advisorMapObj.setMapSize();
    if(/Android/i.test(navigator.userAgent)){                		
		if ($(window).width() < 767) {           
			console.log('Android identified: windowsize less than 767');
			$('.people_location_map').css("display", "none");    			
		} else {
			console.log('Android identified: windowsize greater than 767');
			$('.people_location_map').css("display", "block");	
			advisorMapObj.startMap();
			if($('.peoplepage').is(':visible')) {
				advisorMapObj.addPinpoints();   
			}								
		}    	
	} else if(/iPhone/i.test(navigator.userAgent)){    
		console.log('iPhone identified');
		$('.people_location_map').css("display", "none");    		
	}else{
		console.log('windows');
		$('.people_location_map').css("display", "block");	
		advisorMapObj.startMap();
		if($('.peoplepage').is(':visible')) {
			advisorMapObj.addPinpoints();   
		}
	} 
    //setTimeout(function () { advisorMapObj.setMapSize(); }, 1);
    $(window).resize(function () {
        advisorMapObj.setMapSize();
    });


    
    var startLat;
	var startLng;
    navigator.geolocation.getCurrentPosition(function (position) {
		startLat = position.coords.latitude;
		startLng = position.coords.longitude;
		console.log('Current GEO location lat :'+startLat+' long:'+ startLng);
		var addressSpanClass = $('.addressDistance');
		    $(addressSpanClass).each(function (i, ele) {
		    var endLat = $(ele).data('latitude');
		    var endLng = $(ele).data('longitude');
		    console.log('End location lat :'+endLat+' long:'+ endLng);
		    var distance = updateDistance(startLat, startLng, endLat, endLng);
		    $(".location_phone.location_distance.clearfix").show();
		    $(ele).find('span').html(distance).append(((distance == 1) ? ' Mile' : ' Miles'));
		    });
	});

	
    function updateDistance(startLat, startLng, endLat, endLng){
        var start = L.latLng(startLat,startLng); 
        var end = L.latLng(endLat, endLng); 
        console.log('start: '+start);
        console.log('end: '+end);
        var distance = start.distanceTo(end).toFixed(0); 
        
        if (!distance) { 
            distance = 0.00; 
        } else { 
            //Convert from meters to miles and the round 
            distance = (Math.round(distance * 0.000621371 * 100) / 100); 
        } 
        console.log('distance: '+distance);

        return distance;
        //$('.addressDistance').find('span').html(distance);

        //$('#FindUsLocationInformation .suntrust-branch-distance').text(distance + ((distance == 1) ? ' mile' : ' miles'));
    }

    $(document).on("click",".suntrust-get-directions-route-switch",function(){
		$("#destination").removeClass("noValid");
		$("#source").removeClass("noValid");
		$("#MyLocationText").parent().removeClass("noValid");
		$('#destination').find('#uniform-people_map_select').removeClass('noValid');
		$("#source").find('#uniform-people_map_select').removeClass('noValid');
		var swapPrev;
        var swapNext;
        if($(this).prev().children().children().children().hasClass('suntrust-select')==true) {
            swapPrev = $(this).prev().children().children().children('select.suntrust-select');
            $(this).prev().children().remove();
        }
        else {
            swapPrev = $(this).prev().children(); 
            $(this).prev().children().remove();
        } 
        if($(this).next().children().children().children().hasClass('suntrust-select')==true) {
            swapNext = $(this).next().children().children().children('select.suntrust-select');
            $(this).next().children().remove();
        }
        else {
            swapNext = $(this).next().children();
            $(this).next().children().remove();
        }
        if(swapNext.hasClass('suntrust-select')) {
            $(this).prev('div#source').html('<div></div>');
            $(this).prev('div#source').find('div').append(swapNext);                                                                                                                              
        }
        else {
            $(this).prev('div#source').html(swapNext);         
        }
        
        if(swapPrev.hasClass('suntrust-select')) {
            $(this).next('div#destination').html('<div></div>');
            $(this).next('div#destination').find('div').append(swapPrev);                                                     
        }
        else {                                     
            $(this).next('div#destination').html(swapPrev);                
        }
        
        $('.suntrust-select').uniform({
            selectClass: 'sun-select-container',
            selectAutoWidth: false
        }).each(function() {
            $(this).siblings("span").attr("aria-hidden", true);
        });
		
		/* Search Close Button Enable Start */
        $("#MyLocationText").keyup(function(){            
            if ($(this).val().length > 0){
              $(".input_search_reset_map").show();
            }
            else {
              $(".input_search_reset_map").hide();
            }
        });
    	/* Search Close Button Enable End */
		
        /* Search Clear Text on click of close icon Start */
        $('.input_search_reset_map').on('click', function(){
			var tmpClass = $(this);
			e.preventDefault();
			tmpClass.prev().find("input").typeahead('val','').focus(),
			$(".input_search_reset_map").hide();
        });
        /* Search Clear Text on click of close icon Start */
    
        //Get direction trigger click on change
        $('.suntrust-input-location-button-advisor').click(function (event) {
            advisorMapObj.getLocationCoords($('.suntrust-input-location-button-advisor'),'');
        });
        //Get direction trigger click on change

	});
   
   // $(spn[data-distance="miles"]).click(function (event) {
    $('.addressDirection').click(function (event) {
    	console.log("Get direction clicked");
    	var endLat=$(this).attr('data-latitude');
    	var endLng=$(this).attr('data-longitude');
		console.log('endLat:'+endLat+' endLng:'+endLng);
		var startLat;
		var startLng;
		navigator.geolocation.getCurrentPosition(function (position) {
			console.log('Current GEO location: '+position);
			startLat = position.coords.latitude;
			startLng = position.coords.longitude;
			console.log('Current GEO location lat :'+startLat+' long:'+ startLng);
			console.log('Calling show route');
			advisorMapObj.showRoute(startLat, startLng, endLat, endLng, "","");
			console.log('show routec completed');
		});
		$("#MyLocationText").focus();
        var addrConstructed = $($(this).closest('.people_location_details_individual').find('.location_addr').children(0)[0]).text()+' '+$($(this).closest('.people_location_details_individual').find('.location_addr').children(0)[1]).text();
        //addrConstructed = addrConstructed.replace(/,/g , ', ');//.replace(/\s+/g,'').replace(/\./g,'. '); 
        console.log("addrConstructed"+addrConstructed);

        $('#people_map_select').val(addrConstructed).change();
        $('.suntrust-secondary-button.submit-button').click();
    });

    //Get directions icon click for people
    $('.people.suntrust-secondary-button.submit-button').click(function (event) {
		$(".noValidRemove").removeClass("noValidRemove");
		geocoord = [];
	    var myLocation = $('#source');
	    var startlocation = $('#MyLocationText');
	    var endlocation = $('#people_map_select');
	    var destSelect = $('#destination');
	    var endVal = "";
        var locationVal = startlocation.typeahead('val');
		//var $resultSetInput = document.getElementById('MyLocationText');
        console.log("locationVal:"+locationVal);
		if(locationVal == null || locationVal == undefined) {
			
		}
		startlocation.parent().parent().removeClass('noValid');
		if (!advisorMapObj.isValidAddress(locationVal)) {
			startlocation.parent().parent().addClass('noValid');
            startlocation.focus();
        }
        destSelect.find('#uniform-people_map_select').removeClass('noValid');
        myLocation.find('#uniform-people_map_select').removeClass('noValid');
        if (!advisorMapObj.isValidAddress(endlocation.val())) {
            endlocation.parent().addClass('noValid');
            return false;
        } else {
        	endlocation.parent().removeClass('noValid');
        }
        
		if($("#people_map_select").val()!="" && $("#people_map_select").val()!=null) {
			if(dest == undefined || dest == null)
				//var endVal = $('#people_map_select').val();
				endVal = endlocation.val();
			else
				//var endVal = dest;
				endVal = dest;
		}
		console.log("endVal:"+endVal);
		/*if (endVal != '' && endVal != undefined && !advisorMapObj.isValidAddress(endVal)) {
			destSelect.addClass('noValid');
			//input.focus();
			return false;
		}*/
		//$('#txtRouteStartAddress').val(locationVal);
		//$('#txtRouteEndAddress').val(endVal);
		advisorMapObj.getGeolocation(locationVal, endVal);
        
    });
	
   //Get directions icon click in location page
    $('.location.suntrust-secondary-button.submit-button').click(function (event) {
        var input = $('.people_location_map_direction').find('input');
    	locationGetDirection(input.typeahead('val'));
    });
	
    //Back Button click
    $('.findUsBackButton').click(function (event) {
        $('#GetDirections').removeClass('dirResultsView');
        advisorMapObj.addPinpoints();
    });


    //Edit Route - reverse button
    $('.location_map_reverse_trip').click(function (event) {
        //$('#reverseLink').click(function (event) {
        var startInput = $('#MyLocationText');
        var endInput;

        if($('#people_map_select').is(":visible")) {
            endInput = $('#people_map_select').val();
        } else {
            var endZip = $("#hfResultSet").val();
            endInput = endZip.substring(endZip.lastIndexOf("$")+1,endZip.lastIndexOf(";"));
        }

        $('#MyLocationText').typeahead('val',advisorMapObj.endText);
        $('#people_map_select').val(advisorMapObj.startText);

        var myLocation = $('.location_map_input');
        var input = myLocation.find('input');
        myLocation.removeClass('noValid');
        if (!advisorMapObj.isValidAddress(input.typeahead('val'))) {
            myLocation.addClass('noValid');
            $('#MyLocationText').focus();
        }
        else {

            advisorMapObj.showRoute(advisorMapObj.endLat, advisorMapObj.endLng, advisorMapObj.startLat, advisorMapObj.startLng, advisorMapObj.endText, advisorMapObj.startText);
        }
    });

    $('#people_map_select').change(function(){
    	console.log($(this).val());
        dest = $(this).val();
        console.log("dest:"+dest);
    });

    //Edit Route - get direction button
    $('#btnGetDirection').click(function (event) {
        console.log("clicked");
        var startInput = $('#txtRouteStartAddress');
        //var endInput = $('#txtRouteEndAddress');
        var startVal = startInput.val();
        var endVal = dest;
        $('#findUsRoutes ').find('.dirTextBoxHolder ').removeClass('noValid');
        var isValid = true;
        if (!advisorMapObj.isValidAddress(startVal) || startVal == 'Enter Starting Address') {
            isValid = false;
            startInput.parent().addClass('noValid');
        }
        if (!advisorMapObj.isValidAddress(endVal) || endVal == 'Enter End Address') {
            isValid = false;
            endInput.parent().addClass('noValid');
        }
        event.preventDefault();
        if (isValid) {
            var myLocationInput = $('#advisorMyLocation').find('input');
            myLocationInput.val(startVal);
            advisorMapObj.getGeolocation(startVal, endVal);
        }
    });
    //Tabs
    $('#mapContainerTabs .tab').click(function (event) {
        var tabClass = $(this).attr('class').replace(' ', '');
        if ($('#GetDirections').hasClass('initialView') && (tabClass == 'listViewtab' || tabClass == 'editRoutetab')) {
            return;
        }
        $('#advisorMapTabSel').attr('class', tabClass);
    });
    $(".submit-button").mouseout(function(){
    	$(this).css("background-color", "#003b71");
	});
     $(".submit-button").mouseenter(function(){
    	$(this).css("background-color", "#021E30");
	}); 

    //setInputAutocomplete($('.sun-autocomplete-input'));



	/*$("#MyLocationText").autocomplete({
        source: function (request, response) {
            jQuery.get("https://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6zn1ua2d%2C70%3Do5-l0850&outFormat=json", {
                location: request.term
            }, function (data) {
                console.log("success auto"+data);
                var locationObj; 
                $.each(data.results, function() {
                    $.each(this, function(key, val){
                        if(key != null && key != undefined && key=='locations') {
                            var locationsArr = val;
                            console.log("data.results3"+JSON.stringify(locationsArr));
                            locationObj = [];
                            $.each(this, function(key, locationsArr){
                                
                                street = this.street;
                                city = this.adminArea5;
                                state = this.adminArea3;
                                zip = this.postalCode;
                                console.log("street"+street);
                                console.log("city"+city);
                                console.log("state"+state);
                                console.log("zip"+zip);
                                
                                if(city!='' && city !=null && state!='' && state !=null && zip!='' && zip !=null) {
                                    
                                    locationObj.push(city+','+state+','+zip);
                                    
                                } else if(city!='' && city !=null && (state=='' || state ==null) && zip!='' && zip !=null) {
                                    
                                    locationObj.push(city+','+zip);
                                    
                                } else if(state!='' && state !=null && (city=='' || city ==null) && zip!='' && zip !=null) {
                                    
                                    locationObj.push(state+','+zip);
                                    
                                } else if(city!='' && city !=null && (zip=='' || zip ==null) && state!='' && state !=null) {

                                    locationObj.push(city+','+state);
                                    
                                }
                            });

                            locationObj.join(',');
                        }

                    });

                });
                //{"address":"Vishnuvardhana Road, Mysuru, Karnataka, 570001","zip":"570001","city":"Mysuru","state":"Karnataka"}
                //var dataJson = {"address":"'+'"+street, Mysuru, Karnataka, 570001","zip":"570001","city":"Mysuru","state":"Karnataka"}
                //console.log("autoComp join"+autoComp.join(','));
                var findme = JSON.stringify(locationObj);
                
                console.log("json string :: "+findme);
                var jsonResult = JSON.parse(findme);
                console.log("jsonResult"+JSON.stringify(jsonResult));
                autoComp=[];
                response(jsonResult);
            });
        },
        minLength: 3
    });*/

	$("#people_map_select").parent().addClass("noValidRemove");
    $("#people_map_select").parent().removeClass("noValid");
	
});
} catch(e){}
