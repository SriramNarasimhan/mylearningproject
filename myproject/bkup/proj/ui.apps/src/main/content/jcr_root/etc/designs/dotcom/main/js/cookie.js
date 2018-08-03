$(document).ready(
function() {

	// set the LoanOfficer persistent cookie values when the profile detail page is loaded.			
	var $peopleProfileData = $('#people-profile-data').find('.lo-data-persist');

	if ($peopleProfileData.length) {
		
		var $link = $peopleProfileData,
        $phone = typeof ($link.data('lo-phone')) == 'undefined' ? '' : $link.data('lo-phone'),
        $loId = typeof ($link.data('lo-id')) == 'undefined' ? '' : $link.data('lo-id'),
        $nmls = typeof ($link.data('lo-nmls')) == 'undefined' ? '' : $link.data('lo-nmls'),
		$email = typeof ($link.data('lo-email')) == 'undefined' ? '' : $link.data('lo-email'),
        $mortgageApplynowURL = typeof ($link.data('lo-mortgageappurl')) == 'undefined' ? '' : $link.data('lo-mortgageappurl');

		// Remove cookie value "LoanOfficer"
		Cookies.remove('LoanOfficer');
	
		// Add cookie value "LoanOfficer"
		Cookies.set('LoanOfficer', {
			lo : $loId,
			phone : $phone,
			nmls : $nmls,
			email : $email,
			mortgageApplynowURL : $mortgageApplynowURL
		});
		
		var loanOfficer = Cookies.getJSON('LoanOfficer');
        if (typeof (loanOfficer) != 'undefined') {
            console.log("LOID : " + loanOfficer.lo);
            console.log("NMLSID : " + loanOfficer.nmls);
            console.log("EMAIL : " + loanOfficer.email);
			console.log("PHONE : " + loanOfficer.phone);
            console.log("mortgageApplynowURL : " + loanOfficer.mortgageApplynowURL);
        } 
		
	}		
	// add loid and nmls cookie value as a querystring to all urls which has css class'lo-data-persist-enabled' in anchortag.
	$('.lo-data-persist-enabled').each(function () {
		var $link = $(this),
			tmpHref = $link.attr('href'),
			loanOfficer = Cookies.getJSON('LoanOfficer');                	
		if (tmpHref != '' && tmpHref != '#' && tmpHref != undefined && tmpHref.indexOf('tel:') < 0) {                                        
			if (typeof (loanOfficer) != 'undefined') {						
				//var loDataQueryString = 'lo=' + loanOfficer.lo + '&nmls=' + loanOfficer.nmls;	  
				//US49726 Adding the email & removing nmls & lo id	
				var loDataQueryString = 'referrerId='+loanOfficer.email;
				if(tmpHref.indexOf('/dotcom/external?clickedUrl=') != -1){	 					
					var params = tmpHref.split('/dotcom/external?clickedUrl=')[1];					
					if(params.indexOf('?') != -1){
						$link.attr('href', tmpHref+'&'+loDataQueryString);						
					}else{
						$link.attr('href', tmpHref+'?'+loDataQueryString);
					}					
				}else{																	
					$link.attr('href', fullURL(tmpHref,loDataQueryString));	
				}								
			}
		}		
		if (tmpHref != undefined && tmpHref.indexOf('tel:') >= 0) 
		{						
			if (typeof (loanOfficer) != 'undefined') {
				console.log("loPhoneNumber is"+ loanOfficer.phone);	
				$link.attr('href', "tel:" + loanOfficer.phone); 
				$link.text(loanOfficer.phone);				
			}
		}
	});	

	$('div#lo-persist-contactus').each(function () {							
		var persist_contactus = $(this).text().trim();	
		var liveengage_contactus = $(this).prev('div#lo-persist-contactus-liveengage').text().trim(); 
		var loanOfficer = Cookies.getJSON('LoanOfficer');									
		if (typeof (loanOfficer) != 'undefined' && persist_contactus === "true" && liveengage_contactus != "None") {										
			var selectId = liveengage_contactus; 
			$('li#'+selectId).remove();						
			console.log("Set1 loan_persist_contactus set to true chatNow is removed");
		}									
	});	
	
	$('div#lo-persist-supportus').each(function () {
		var persist_supportus = $(this).text().trim();	
		var liveengage_supportus = $(this).prev('div#lo-persist-supportus-liveengage').text().trim();
		var loanOfficer = Cookies.getJSON('LoanOfficer');									
		if (typeof (loanOfficer) != 'undefined' && persist_supportus === "true" && liveengage_supportus != "None") {					
			var selectId = liveengage_supportus; 
			$('li#'+selectId).remove();											
			console.log("Set2 loan_persist_supportus set true chatNow is removed");
		}									
	});
});

function fullURL(tmpHref,loDataQueryString) {
	var parts=tmpHref.split('#');
	parts[0]=parts[0]+(( parts[0].indexOf('?') !== -1) ? '&' : '?')+loDataQueryString;
	return parts.join('#');
}

$(window).load(
function() {if($('ul.chatnow-listStyle').text().trim() == ''){
	if($('.chatnow').length == 1){
		$('ul.chatnow-listStyle').parent().parent('div.chatnow').css('display','none')
	}
}});