var AJAX_ERROR_MESSAGE = "Rates are unavailable now. Please try again in a few minutes.";
var ERROR_CODE_001 = "001";
var ERROR_CODE_003 = "003";
var ERROR_CODE_004 = "004";
var ERROR_CODE_005 = "005";

var errMsg = $('.findrate-error-message-othUnavail').text();				
errMsg = (errMsg.trim() != '') ? errMsg : AJAX_ERROR_MESSAGE;


/* CD Rates Zip code Validation Start */
$("#zip_search").keypress(function(e) {
	if(e.keyCode === 13){
		$('.zip_search_submit').trigger("click");
	}
});
$(document).on("click",".zip_search_submit",function(){

	$('.rate-suntrust-input').removeClass("sun-error");
	$('.findrate-error-message-invalid').hide();
	$('.findrate-error-message-unavail').hide();
	$('.findrate-error-message-othUnavail').hide();
	var findrate_zip = $('#zip_search').val();
	var isValidZip = /(^\d{5}$)|(^\d{5}-\d{4}$)/.test(findrate_zip);
	if(isValidZip) {
		$('.rate-suntrust-input').removeClass("sun-error");
		$('.findrate-error-message-invalid').hide();
		//var $row = jQuery(this).closest('tr');
		var $row = $(".cdrate_zip_desktopTable tr:first");
		var $columns = $row.find('th');
		var col0 = '';
		var col1 = '';
		var col2 = '';
		jQuery.each($columns, function(i, item) {
			if(i === 0){
				col0 = item.innerHTML;
			} else if (i === 1){
				col1 = item.innerHTML;
			} else if (i === 2){
				col2 = item.innerHTML;
			}
		});

		console.log('Column names: '+col0 + col1 + col2);
		
        var localHostName = 'https://'+location.host;
        if(location.hostname == "localhost"){
			localHostName = 'http://'+location.host;
        }
        
        var reqURL = '/dotcom/nac-rest-api-consumer';
        var data = {'ZipCode': findrate_zip , 'channel' : 'NAC'};
        
        console.log('Request URL: '+reqURL);
		
		$.ajax({
			type: 'GET',
            url: reqURL,
            data: data,
		    async: false,
		    dataType: 'json',
            success: function(data) {
            	populateCDRatesTable(data,col0,col1,col2);
            },
    		error: function (error) {
    			$('.rate-suntrust-input').addClass("sun-error");
				$('.findrate-error-message-othUnavail').text(errMsg);
				$('.findrate-error-message-othUnavail').show();				
				$('.cdrate_zip_border .cdrate_savings_promotional').hide();
    		}
        });
	} else{		
		$('.rate-suntrust-input').addClass("sun-error");
		$('.findrate-error-message-invalid').show();
		$('.cdrate_zip_border .cdrate_savings_promotional').hide();
	}
		
});

function populateCDRatesTable(data,col0,col1,col2){
	
	$("tr.cdrates_desktop_row").remove();
    $('.cdrates_mobile_table').remove();
	var status = "";
    var row = "";
	var table = "";
	
	if(data.TermAndRate == null)
	{
		$('.rate-suntrust-input').addClass("sun-error");
		
		if(data.ErrorContent != null)
		{
			var rateAPIErrorMsg = data.ErrorContent.ErrorMsg;
			var errorCode = data.ErrorContent.ErrorCode;
			
			if(errorCode == ERROR_CODE_005)
			{		
				$('.findrate-error-message-othUnavail').text(errMsg);
				$('.findrate-error-message-othUnavail').show();
			}
			else if(errorCode != ERROR_CODE_003 && errorCode != ERROR_CODE_004)
			{
				if(errorCode == ERROR_CODE_001)
				{
					$('.findrate-error-message-invalid').show();
				}
				else
				{
					$('.findrate-error-message-unavail').text(rateAPIErrorMsg);
					$('.findrate-error-message-unavail').show();
				}
			}	
			
			else
			{
				$('.findrate-error-message-unavail').show();
			}
			
			$('.cdrate_zip_border .cdrate_savings_promotional').hide();
		}
		
	}
	
	else
	{
		var row = "";
	    var table = "";
		var items = data.TermAndRate.map(function (item , index) {
		row = row + '<tr class="cdrates_desktop_row"><td>' + item.Term.Description  + '</td><td>' + (item.Rate.Interest * 100).toFixed(2) + "%" + '</td><td>' +  (item.Rate.APY*100).toFixed(2) + "%" + '</td></tr>';
		table = table + '<table class="cdrates_mobile_table">' + '<tr><th>' + col0 + '</th><td>'+item.Term.Description+'</td></tr>' + '<tr><th>' + col1 + '</th><td>'+(item.Rate.Interest * 100).toFixed(2) + "%"+'</td></tr>' + '<tr><th>' + col2 + '</th><td>'+(item.Rate.APY*100).toFixed(2) + "%"+'</td></tr>' + '</table>';
	});

		$('.cdrate_zip_desktopTable').append(row);
		$('.cdrate_zip_mobileTable').append(table);

		$('.cdrate_zip_border .cdrate_savings_promotional').show();
		$('html, body').animate({
			   scrollTop: $(".cdrates").offset().top-120
		}, 200);
	}
	
}


/* CD Rates Zip code Validation End */

/* Equity line of Rates Zip code Start */
$("#er_zip_search").keypress(function(e) {
	if(e.keyCode === 13){
		$('.er_zip_search_submit').trigger("click");
	}
});
$(document).on("click",".er_zip_search_submit",function(){
	$('.rate-suntrust-input').removeClass("sun-error");
	$('.findrate-error-message-invalid').hide();
	$('.findrate-error-message-unavail').hide();
	var findrate_zip = $('#er_zip_search').val();
	
	var isValidZip = /(^\d{5}$)|(^\d{5}-\d{4}$)/.test(findrate_zip);
	//console.log(isValidZip);
	if(isValidZip) {
		$('.rate-suntrust-input').removeClass("sun-error");
		$('.findrate-error-message-invalid').hide();
		//var myCDratesObject = [{"LoanPurpose":"HomeImprovement","TR":[{"TD":"58 Month"},{"TD":"1.98%"},{"TD":"2.00%"}],"TR":[{"TD":"59 Month"},{"TD":"1.98%"},{"TD":"2.00%"}]}];
		//var myeqratesObject = 	[{status:'success', tablerow:[['name0','id0']]}];

		var localHostName = 'https://'+location.host;
        if(location.hostname == "localhost"){
			localHostName = 'http://'+location.host;
        }
        
        var cdratePageURL = $('#cdratePageURL').val();
        var reqExtension = $('#reqExtension').val();
        console.log('cdratePageURL: '+cdratePageURL);
        console.log('reqExtension: '+reqExtension);

        var reqURL = localHostName+cdratePageURL+'.equityrates.'+findrate_zip+'.'+reqExtension;
        console.log('Request URL: '+reqURL);
        
		$.ajax({
            type: 'GET',
            //url: localHostName+'/content/suntrust/dotcom/us/configuration/rates.equityrates.'+findrate_zip+'.json',
            url: reqURL,
            async: false,
            success: function(data) {
            	populateEQRatesTable(data);
            },
    		error: function (error) {
    			console.log('Error captured: '+error.responseText);
    		}
        });
		
	} else {
		$('.rate-suntrust-input').addClass("sun-error");
		$('.findrate-error-message-invalid').show();
		$('.rateDetailsTable').hide();
	}

});

function populateEQRatesTable(myeqratesObject){
	var myString = JSON.stringify(myeqratesObject);
	myString = '['+myString+']';
	console.log("json: "+myString);
	
    var resultsaprlabel = $('#resultsaprlabel').val();
    var resultsTermsLabel = $('#resultsTermsLabel').val();
    
	var servletResponse = JSON.parse(myString);
	console.log("json parsed: "+servletResponse.length);
	var status = "";
	for (var i = 0; i < servletResponse.length; i++) {
		status = servletResponse[i].status;
        console.log('Response status: '+status);
		if (status == "success") {
			$("tr.eqrates_row").remove();
			var cellTable='';
			$.each(servletResponse[i].tablerow, function(i, d) {
			   var row='<tr class="eqrates_row">';
			   var cellRow='';
			   $.each(d, function(j, e) {
				if(j === 0){
				  row+='<td><p class="equity-line-rate-title">'+resultsaprlabel+'</p><p><strong>As low as '+e+'% APR</strong></p></td>';
				  cellRow+='<tr class="eqrates_row"><td><p class="equity-line-rate-title">'+resultsaprlabel+'</p><p><strong>As low as '+e+'% APR</strong></p></td></tr>';
				} else {
					row+='<td><p class="equity-line-rate-title">'+resultsTermsLabel+'</p><div>'+e+'</div></td>';
					cellRow+='<tr class="eqrates_row"><td><p class="equity-line-rate-title">'+resultsTermsLabel+'</p><div>'+e+'</div></td></tr>';
				}
			   });
			   row+='</tr>';
			   
			   $('#equity-line-rate-table-desktop').append(row);
			   $('#equity-line-rate-table-mobile').append(cellRow);
			   //$('.table.equity-line-rate-table').append(row);
			   //$('.table.equity-line-rate-table.mobile').append(row);
			});
		} else {
			$('.rate-suntrust-input').addClass("sun-error");
			$('.findrate-error-message-unavail').show();
			$('.rateDetailsTable,.redirection').hide();
		}
	}
	if(status == "success"){
		$('.rateDetailsTable').show();
		$('html, body').animate({
		    scrollTop: $(".equityrates").offset().top-120
		}, 200);
	}
}
/* Equity line of Rates Zip code Validation End */