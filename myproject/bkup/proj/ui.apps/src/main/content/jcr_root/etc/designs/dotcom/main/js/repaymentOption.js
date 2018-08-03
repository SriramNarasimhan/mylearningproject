$(document).ready(function(){ 
    var environment="Author";
     var hostName = window.location.host;
     // check hostname is publish instance hostname or author hostname
    /*if(hostName=="stcom-mnt1.suntrust.com" || hostName=="stcom-dev1.suntrust.com" || hostName=="stcom-itca.suntrust.com"|| hostName=="stcom-prdr.suntrust.com"|| hostName=="stcom-prod.suntrust.com" || hostName=="www.suntrust.com") {
        environment="Publish";
    }*/
    /****** Repayment option script starts ******/
    if($(".suntrust-repaymentOption").is(":visible") || $("span[class='rpxcustomtag']").is(":visible") || $(".suntrust-repaymentOption").length>0 || $("span[class='rpxcustomtag']").length>0){
        function repaymentOption(){
            var paymentstableservlet = $(document).find('input[name=paymentstableservlet]').val();
            if(paymentstableservlet=='' || paymentstableservlet==null || paymentstableservlet==undefined) {
            	paymentstableservlet = $("input.rpx-canonical-url").val();
            }
            
            var rpxjsonpath = $(document).find('input[name=rpxjsonpath]').val();
        	if(rpxjsonpath!='' && rpxjsonpath!=null && rpxjsonpath!=undefined) {
        		paymentstableservlet = rpxjsonpath;
        	}
            $.ajax({
                url: paymentstableservlet,
                dataType: 'json',
                success: function(data) {
                   
                    $('#rptable tbody').empty();
                    $("#rpTableMobile tbody").empty();
                    var optionValues = $("#rateType").val()+'_'+$("#term").val()+'_'+$("#repaymentOption").val();
                    var productType = $(document).find('input[name=productType]').val();			
                    var column2heading = $(document).find('input[name=column2heading]').val();
                    var column3heading = $(document).find('input[name=column3heading]').val();
                    var row1label = $(document).find('input[name=row1label]').val();
                    var row2label = $(document).find('input[name=row2label]').val();
                    var row3label = $(document).find('input[name=row3label]').val();
                    var row4label = $(document).find('input[name=row4label]').val();
                    var row5label = $(document).find('input[name=row5label]').val();
                    var row6label = $(document).find('input[name=row6label]').val();
                    var row7label = $(document).find('input[name=row7label]').val();
                    var row8label = $(document).find('input[name=row8label]').val();
                    if(data.hasOwnProperty(productType)){
                        if(data[productType].hasOwnProperty(optionValues)){
                            var row = $('<tr><th class="headnonBg"></th><th>'+column2heading+'</th><th>'+column3heading+'</th></tr><tr><td>'+row1label+'</td><td>$'+ data[productType][optionValues].loan_amount[0]+ '</td><td>$' +data[productType][optionValues].loan_amount[1]+ '</td></tr><tr><td>'+row2label+'</td><td>' + data[productType][optionValues].current_interest_rate[0]+ '%</td><td>' +data[productType][optionValues].current_interest_rate[1]+ '%</td></tr><tr><td>'+row3label+'</td><td>' + data[productType][optionValues].apr[0]+ '%</td><td>' +data[productType][optionValues].apr[1]+ '%</td></tr><tr><td>'+row4label+'</td><td>$' + data[productType][optionValues].monthly_payment_while_in_school[0]+ '</td><td>$' +data[productType][optionValues].monthly_payment_while_in_school[1]+ '</td></tr><tr><td>'+row5label+'</td><td>$' + data[productType][optionValues].monthly_payment_during_payment[0]+ '</td><td>$' +data[productType][optionValues].monthly_payment_during_payment[1]+ '</td></tr><tr><td>'+row6label+'</td><td>' + data[productType][optionValues].deferment_period[0]+ '</td><td>' +data[productType][optionValues].deferment_period[1]+ '</td></tr><tr><td>'+row7label+'</td><td>' + data[productType][optionValues].repayment_period[0]+ '</td><td>' +data[productType][optionValues].repayment_period[1]+ '</td></tr><tr><td>'+row8label+'</td><td>$' + data[productType][optionValues].total_repayment_amount[0]+ '</td><td>$' +data[productType][optionValues].total_repayment_amount[1]+ '</td></tr>');
                            $('#rptable tbody').append(row);
                            $('#rptable').removeClass('hide');
                            $('.noResultsFound').addClass('hide');
                            
                            var rowMobile = $('<tr><th>'+row1label+'</th><td><span>'+column2heading+': $</span>'+ data[productType][optionValues].loan_amount[0]+'</td><td><span>'+column3heading+': $</span>'+ data[productType][optionValues].loan_amount[1]+'</td></tr><tr><th>'+row2label+'</th><td><span>'+column2heading+': </span>'+ data[productType][optionValues].current_interest_rate[0]+'%</td><td><span>'+column3heading+': </span>'+ data[productType][optionValues].current_interest_rate[1]+'%</td></tr><tr><th>'+row3label+'</th><td><span>'+column2heading+': </span>'+ data[productType][optionValues].apr[0]+'%</td><td><span>'+column3heading+': </span>'+ data[productType][optionValues].apr[1]+'%</td></tr><tr><th>'+row4label+'</th><td><span>'+column2heading+': $</span>'+ data[productType][optionValues].monthly_payment_while_in_school[0]+'</td><td><span>'+column3heading+': $</span>'+ data[productType][optionValues].monthly_payment_while_in_school[1]+'</td></tr><tr><th>'+row5label+'</th><td><span>'+column2heading+': $</span>'+ data[productType][optionValues].monthly_payment_during_payment[0]+'</td><td><span>'+column3heading+': $</span>'+ data[productType][optionValues].monthly_payment_during_payment[1]+'</td></tr><tr><th>'+row6label+'</th><td><span>'+column2heading+': </span>'+ data[productType][optionValues].deferment_period[0]+'</td><td><span>'+column3heading+': </span>'+ data[productType][optionValues].deferment_period[1]+'</td></tr><tr><th>'+row7label+'</th><td><span>'+column2heading+': </span>'+ data[productType][optionValues].repayment_period[0]+'</td><td><span>'+column3heading+': </span>'+ data[productType][optionValues].repayment_period[1]+'</td></tr><tr><th>'+row8label+'</th><td><span>'+column2heading+': $</span>'+ data[productType][optionValues].total_repayment_amount[0]+'</td><td><span>'+column3heading+': $</span>'+ data[productType][optionValues].total_repayment_amount[1]+'</td></tr>');
                            $('#rpTableMobile tbody').append(rowMobile);
                            $('#rpTableMobile').removeClass('hide');
                        }else{
                            
                            $('#rptable').addClass('hide');
                            $('#rpTableMobile').addClass('hide');
                            $('.noResultsFound').removeClass('hide');
                        }
                    }else{
                        $('#rptable').addClass('hide');
                        $('#rpTableMobile').addClass('hide');
                        $('.noResultsFound').removeClass('hide');
                        
                    }
                    $("#rptable tr").each(function(){
                        $(this).find("td").each(function(){
                            $(this).html(formatNumber($(this).text()));		
                        });
                    });
                    
                    $("#rpTableMobile tr td").each(function(){
                        $(this).html(formatNumberMobile($(this).text()));			
                    });
                    
                    $(".repaymentOption-table-desktop .repaymentOption-table tr").each(function(index){
                        var tabcount = index - 1;
                        
                        var a = $(this).find("td:nth-child(2)").text();
                        
                        var b = $(this).find("td:nth-child(3)").text();
                        
                        if(a!="" && b!="" && a == b){
                            var findcount = $(".repayment-mobiletable table tr").eq(tabcount);
                            findcount.find("td:nth-child(3)").hide();
                            findcount.find("td:nth-child(2) span").hide();
                        }
                    });

                    $(".rpxcustomtag").each(function(){
                        var loantypeattr = $(this).data('product-code');
                        var variablecodeattr = $(this).data('rate-type');
                        var maxpaytermattr = $(this).data('term');
                        var paycodeattr = $(this).data('repay-option');
                        var fieldnameattr = $(this).data('field-name');
                        var highlowattr = $(this).data('high-or-low');
                        
                        var coordinatedAttr = variablecodeattr+'_'+maxpaytermattr+'_'+paycodeattr;
                        
                        if(loantypeattr != undefined && variablecodeattr != undefined && maxpaytermattr != undefined && paycodeattr != undefined && fieldnameattr != undefined &&highlowattr != undefined && coordinatedAttr != undefined){
                            if(data.hasOwnProperty(loantypeattr)){
                                if(data[loantypeattr].hasOwnProperty(coordinatedAttr)){
                                    if(data[loantypeattr][coordinatedAttr].hasOwnProperty(fieldnameattr)) {
                                        $(this).text(data[loantypeattr][coordinatedAttr][fieldnameattr][highlowattr]);
                                    }
                                }
                            }
                        } else if(loantypeattr != undefined && variablecodeattr != undefined && maxpaytermattr == undefined && paycodeattr == undefined && fieldnameattr != undefined &&highlowattr != undefined && coordinatedAttr != undefined){
                            var newprop;
                            if(variablecodeattr.toLowerCase().indexOf("libor")>-1) {
                                newprop = fieldnameattr+"_variable_range";
                            }
                            if(variablecodeattr.toLowerCase().indexOf("fixed")>-1) {
                                newprop = fieldnameattr+"_fixed_range";
                            }
							if(data.hasOwnProperty(loantypeattr) && data[loantypeattr].commons.hasOwnProperty(newprop)){
                                $(this).text(data[loantypeattr].commons[newprop][highlowattr]);
                            }
                        } else if(loantypeattr == undefined && variablecodeattr != undefined && maxpaytermattr == undefined && paycodeattr == undefined && fieldnameattr != undefined &&highlowattr != undefined && coordinatedAttr != undefined){
                            var newprop;
                            if(variablecodeattr.toLowerCase().indexOf("libor")>-1) {
                                newprop = fieldnameattr+"_variable_range_all";
                            }
                            if(variablecodeattr.toLowerCase().indexOf("fixed")>-1) {
                                newprop = fieldnameattr+"_fixed_range_all";
                            }

                            for (var key in data) {
                                var productTypes = data[key];
                                for(var loans in productTypes) {
                                    if(productTypes[loans].hasOwnProperty(newprop)){
                                        $(this).text(productTypes[loans][newprop][highlowattr]);
                                    }
                                }
                            }
                        } else if(loantypeattr == undefined) {
                        	if(fieldnameattr=="rate_effective_date_time" || fieldnameattr=="libor_date") {
	                        	var effDate = data["SUNTFAO1"].commons[fieldnameattr] != undefined ? data["SUNTFAO1"].commons[fieldnameattr] : data["STGBL1"].commons[fieldnameattr] != undefined ? data["STGBL1"].commons[fieldnameattr] : data["STUFPSL1"].commons[fieldnameattr] !=undefined ? data["STUFPSL1"].commons[fieldnameattr] : "";
	                        	$(this).text(effDate);
                        	}
                    	}
                        else{
                            if(data.hasOwnProperty(loantypeattr) && data[loantypeattr].commons.hasOwnProperty(fieldnameattr)){
                                $(this).text(data[loantypeattr].commons[fieldnameattr]);
                            }
                        }
                    });
                }
                
            });
        }
        
        repaymentOption();
    }
    function formatNumberMobile(formatNumberMobile){
        var isNumberCheck;
        var columnTitle=formatNumberMobile.split(":");
        formatNumberMobile=columnTitle[1];
        if(formatNumberMobile.indexOf("$")!=-1){
            isNumberCheck=formatNumberMobile.substring(2,formatNumberMobile.length);
        }	
        if(formatNumberMobile.indexOf("%")!=-1){		
            isNumberCheck=formatNumberMobile.substring(0,formatNumberMobile.length-1);				
        }		
        if($.isNumeric(parseFloat(isNumberCheck)) && formatNumberMobile.length>5){		
            if(formatNumberMobile.indexOf(".")==-1){				
                var secondPart = formatNumberMobile.substring(formatNumberMobile.length-3);
                var firstPart = formatNumberMobile.substring(0,formatNumberMobile.length-3);			
                formatNumberMobile="<span>"+columnTitle[0]+":</span>"+firstPart+","+secondPart;			
            }else{				
                var numberWithNodecimals=formatNumberMobile.split(".");
                x=numberWithNodecimals[0];
                if(x.length>5){
                    var secondPart = x.substring(x.length-3);
                    var firstPart = x.substring(0,x.length-3);				
                    formatNumberMobile="<span>"+columnTitle[0]+":</span>"+firstPart+","+secondPart+"."+numberWithNodecimals[1];	
                }else{
                    formatNumberMobile="<span>"+columnTitle[0]+":</span>"+formatNumberMobile;	
                }			
            }
        }
        return formatNumberMobile;
    }
    function formatNumber(formatNumber){
        var isNumberCheck;
        if(formatNumber.indexOf("$")!=-1){
            isNumberCheck=formatNumber.substring(1,formatNumber.length);
        }
        if($.isNumeric(parseFloat(isNumberCheck)) && formatNumber.length>5){
            if(formatNumber.indexOf(".")==-1){
                var secondPart = formatNumber.substring(formatNumber.length-3);
                var firstPart = formatNumber.substring(0,formatNumber.length-3);
                formatNumber=firstPart+","+secondPart;
            }else{
                var numberWithNodecimals=formatNumber.split(".");
                x=numberWithNodecimals[0];
                if(x.length>5){
                    var secondPart = x.substring(x.length-3);
                    var firstPart = x.substring(0,x.length-3);
                    formatNumber=firstPart+","+secondPart+"."+numberWithNodecimals[1];
                }
            }
        }
        return formatNumber;
    }
    
    $("#rateType,#term,#repaymentOption").on("change",function(){
        repaymentOption();
    });
    
    if(window.innerWidth<801){
        $(document).on('click','.suntrust-repaymentOption .field label',function(e){                        
            e.preventDefault();
        });
    }
});
/****** Repayment option script ends ******/