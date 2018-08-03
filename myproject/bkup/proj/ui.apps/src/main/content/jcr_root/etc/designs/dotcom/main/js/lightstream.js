    /**** LightStream Integration Starts******/
    $(window).load(function() {
	var LSRateComponents = $(".lightstream");
	if(LSRateComponents.length >= 1){		
        if (typeof(Storage) !== "undefined") 
        {
            if (!sessionStorage.getItem('lsData') || typeof(sessionStorage.getItem('lsData')) == "undefined" ) 
            {
			//alert("on load"+LSRateComponents);
                //Use JQuery AJAX request to post data to a Sling Servlet
                $.ajax({
                    type: 'GET',
                    url: '/dotcom/lightstreamservice',
                    /*data : { pass your request parameter here, currently we are not passing any data },*/
                    success: function(data) {
                        //alert(data);
                        sessionStorage.setItem('lsData', data);
                    },
                    async: false
                });

            }
        } 
        else
        {
            console.log("You are using a old browser that does not support Web Storage");
        }
	}

        var finalMaxRate = 0;
        var finalMinRate = 0;
        var ratesMinArray = [];
        var ratesMaxArray = [];
        var rateMinIterator;
        var rateMaxIterator;

        obj = JSON.parse(sessionStorage.getItem('lsData'));

       
        $(LSRateComponents).each(function(i, ele) {
		var paramArray= $(ele).attr( "class" );
		var loanType = paramArray.split(" ")[1]; 
		var rateType = paramArray.split(" ")[2]; 


            ratesMaxArray = [];
            ratesMinArray = [];

            if (loanType == "DefaultNoAuto") {
                for (var i = 0; i < obj.length; i++) {
                    if (obj[i].LoanPurpose != "NewAutoPurchase" && obj[i].LoanPurpose != "UsedAutoPurchase" && obj[i].LoanPurpose != "PrivatePartyPurchase" && obj[i].LoanPurpose != "LeaseBuyOut" && obj[i].LoanPurpose != "AutoRefinancing") {
                    	for (var x = 0; x < obj[i].LoanTerms.length; x++) {
                            amtTerm = obj[i].LoanTerms[x].AmountTerms;

                            for (var y = 0; y < amtTerm.length; y++) {
                                if (rateType == 'Min') {
                                    rateMinIterator = JSON.stringify(amtTerm[y].Rates.Min);
                                    ratesMinArray.push(rateMinIterator);

                                }
                                if (rateType == 'Max') {
                                    rateMaxIterator = JSON.stringify(amtTerm[y].Rates.Max);
                                    ratesMaxArray.push(rateMaxIterator);
                                }
                            }

                        }

                    }
                }
            } else if (loanType == "Default") {
                for (var i = 0; i < obj.length; i++) {
                	for (var x = 0; x < obj[i].LoanTerms.length; x++) {
                        amtTerm = obj[i].LoanTerms[x].AmountTerms;

                        for (var y = 0; y < amtTerm.length; y++) {
                            if (rateType == 'Min') {
                                rateMinIterator = JSON.stringify(amtTerm[y].Rates.Min);
                                ratesMinArray.push(rateMinIterator);
                            }
                            if (rateType == 'Max') {
                                rateMaxIterator = JSON.stringify(amtTerm[y].Rates.Max);
                                ratesMaxArray.push(rateMaxIterator);
                            }
                        }

                    }
                }
            } else {
                for (var i = 0; i < obj.length; i++) {
                    if (obj[i].LoanPurpose == loanType) {
                    	for (var x = 0; x < obj[i].LoanTerms.length; x++) {
                            amtTerm = obj[i].LoanTerms[x].AmountTerms;

                            for (var y = 0; y < amtTerm.length; y++) {
                                if (rateType == 'Min') {
                                    rateMinIterator = JSON.stringify(amtTerm[y].Rates.Min);
                                    ratesMinArray.push(rateMinIterator);
                                }
                                if (rateType == 'Max') {
                                    rateMaxIterator = JSON.stringify(amtTerm[y].Rates.Max);
                                    ratesMaxArray.push(rateMaxIterator);
                                }
                            }

                        }
                        break;
                    }
                }
            }

            if (ratesMaxArray.length > 0)
                finalMaxRate = Math.max.apply(Math, ratesMaxArray);
            else
                finalMaxRate = 0

            if (ratesMinArray.length > 0)
                finalMinRate = Math.min.apply(Math, ratesMinArray);
            else
                finalMinRate = 0;

            var ratetoDisplay = finalMaxRate > 0 ? finalMaxRate : finalMinRate;
            ratetoDisplay = (ratetoDisplay * 100).toFixed(2);

            //finalMaxRate = Math.max(...ratesMaxArray);  //Spread operator does not work with IE11

           $(ele).replaceWith(ratetoDisplay + "%");
        });

    });

    /**** LightStream Integration Ends******/