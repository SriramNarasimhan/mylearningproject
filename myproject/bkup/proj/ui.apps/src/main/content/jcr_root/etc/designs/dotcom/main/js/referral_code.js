$(document).ready(
function() {	
	var domainCheck = "referrer_link=SCSTELWEB";
	var defaultCode = "SCSTELWEB";
	var finalCode = "";

    var referenceCode = getQueryStringParams("referrer_link");
    if (referenceCode != undefined) {
        if (typeof(Storage) !== "undefined") {
			console.log("referrer_link method called successfully::"+referenceCode);
            sessionStorage.setItem('refCode', referenceCode);            
        }
    }    	
	$("a").click(function(e){		
        var hyperLink = $(this).attr('href');		
        if (hyperLink != undefined && hyperLink.indexOf(domainCheck) != -1) {
            if (typeof(Storage) !== "undefined") {				
                if (sessionStorage.getItem('refCode') != null) {
                    console.log("got the reference code successfully");
                    finalCode = sessionStorage.getItem('refCode');
                } else {          
					console.log("setting the default code");
                    finalCode = defaultCode;
                }
            } else {
                console.log("check if the browser is compatible for session storage IE6/IE7 won't support");
                finalCode = defaultCode;
            }
			var updatedURL = hyperLink.replace(/(referrer_link=)[^\&]+/, '$1' + finalCode);            
			$(this).attr('href', updatedURL);
        }		
    });
}); 

function getQueryStringParams(sParam){
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {            
            return sParameterName[1];
        }
    }
}