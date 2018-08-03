$(document).ready(function(){
	$('.MonthlyCalc input').keyup(function(evt) {	
		this.value = this.value.replace(/[^0-9\.,]/g,'');
	});
	$(".MonthlyCalc .resetMonCalVal").click(function() {
		$('.MonthlyCalc input').val("");
		$('.financialGap_message').html("");		
	});
	$('.MonthlyCalc input').bind('blur', function(){
		var subTotalVal = 0;
		var nullCheck = 0;
		var dataCheck = 0;
		subTotalVal = parseFloat(subTotalVal);
		$($(this).parents("table").find('input')).each(function(){
			if($(this).val()!="" && !$(this).attr('class')) {
                if($(this).val().match(/\d+/g)!=null) {
                    var commaRemove = $(this).val().replace(/[^0-9\.]/g,'');
                    subTotalVal += parseFloat(commaRemove);
                    nullCheck++;
                }
                else {
                    dataCheck++;
                }
            }
            else {
                dataCheck++;
            }
		});
        if(dataCheck>0) {
			$(this).parents("table").find("tr:last input").val('');
            if($('.monthlyExpense').val()=='') {
				$('.monthlyExpenseSubTotal').val('');
            }
            if($('.monthlyIncome').val()=='') {
				$('.monthlyIncomeSubTotal').val('');
            }
            if($('.monthlyExpenseSubTotal').val()=='' || $('.monthlyIncomeSubTotal').val()=='') {
				$('.netMonthlyIncome').val('');
            }
        }
		
		if(nullCheck > 0)
		{
			subTotalVal=subTotalVal.toString();
			var valSplit = subTotalVal.split('.')
			subTotalVal=valSplit[0];
			if(valSplit[1]>2) {
				valSplit[1] = valSplit[1].substring(0,2);
			}
			var lastThree = subTotalVal.substring(subTotalVal.length-3);
			var otherNumbers = subTotalVal.substring(0,subTotalVal.length-3);
			if(otherNumbers != '') {
				lastThree = ',' + lastThree;
			}
			var res = otherNumbers.replace(/\B(?=(\d{3})+(?!\d))/g, ",") + lastThree;
			if(valSplit[1]!=undefined) {
				$(this).parents("table").find("tr:last input").val(res+'.'+valSplit[1]);
			}
			else {
				$(this).parents("table").find("tr:last input").val(res+'.00');
			}
		}
		
		//$(this).parents("table").find("tr:last input").val(subTotalVal);
		if($(".monthlyExpense").val()!=""){
			$(".monthlyExpenseSubTotal").val($(".monthlyExpense").val());
		}
		if($(".monthlyIncome").val()!=""){
			var monthlyIncome = 0;			
			if($(".monthlyIncome").val()!="") {
				var monthlyIncomeVal = $(".monthlyIncome").val().replace(/[^0-9\.]/g,'');
				monthlyIncome = parseFloat(monthlyIncomeVal);
			}
			var monthlyIncomeSubTotalSub = monthlyIncome;
			
			monthlyIncomeSubTotalSub=monthlyIncomeSubTotalSub.toString();
			var valSplitFinancial = monthlyIncomeSubTotalSub.split('.')
			monthlyIncomeSubTotalSub=valSplitFinancial[0];
			if(valSplitFinancial[1]>2) {
				valSplitFinancial[1] = valSplitFinancial[1].substring(0,2);
			}
			var lastThreeFinancial = monthlyIncomeSubTotalSub.substring(monthlyIncomeSubTotalSub.length-3);
			var otherNumbersFinancial = monthlyIncomeSubTotalSub.substring(0,monthlyIncomeSubTotalSub.length-3);
			if(otherNumbersFinancial != '') {
				lastThreeFinancial = ',' + lastThreeFinancial;
			}
			var resFinancial = otherNumbersFinancial.replace(/\B(?=(\d{3})+(?!\d))/g, ",") + lastThreeFinancial;
			if(valSplitFinancial[1]!=undefined) {
				$(".monthlyIncomeSubTotal").val(resFinancial+'.'+valSplitFinancial[1]);
			}
			else {
				$(".monthlyIncomeSubTotal").val(resFinancial+'.00');
			}		
			//$(".monthlyIncomeSubTotal").val(monthlyIncomeSubTotalSub);
		}
		if($(".monthlyExpenseSubTotal").val()!="" && $(".monthlyIncomeSubTotal").val()!=""){
			var monthlyExpenseSubTotal = 0;
			var monthlyIncomeSubTotal = 0;
			if($(".monthlyExpenseSubTotal").val()!="") {
				var monthlyExpenseSubTotalVal = $(".monthlyExpenseSubTotal").val().replace(/[^0-9\.]/g,'');
				monthlyExpenseSubTotal = parseFloat(monthlyExpenseSubTotalVal);
			}
			if($(".monthlyIncomeSubTotal").val()!="") {
				var monthlyIncomeSubTotalVal = $(".monthlyIncomeSubTotal").val().replace(/[^0-9\.]/g,'');
				monthlyIncomeSubTotal = parseFloat(monthlyIncomeSubTotalVal);
			}
			var netMonthlyIncome = monthlyIncomeSubTotal - monthlyExpenseSubTotal;
			
			var netMonthlyIncomeMinus = "";
			var netMonthlyIncomeMinusSplit = netMonthlyIncome.toString();
			netMonthlyIncomeMinusSplit = netMonthlyIncomeMinusSplit.split('-');
			if(netMonthlyIncomeMinusSplit[0]=="") {
				netMonthlyIncomeMinus = '-';
			}
			netMonthlyIncome=netMonthlyIncome.toString();			
			var valSplitFinancialGap = netMonthlyIncome.split('.')
			netMonthlyIncome=valSplitFinancialGap[0];
			if(valSplitFinancialGap[1]>2) {
				valSplitFinancialGap[1] = valSplitFinancialGap[1].substring(0,2);
			}
			var lastThreeFinancialGap = netMonthlyIncome.substring(netMonthlyIncome.length-3);
			var otherNumbersFinancialGap = netMonthlyIncome.substring(0,netMonthlyIncome.length-3);
			if(otherNumbersFinancialGap != '' && otherNumbersFinancialGap !='-' ) {
				lastThreeFinancialGap = ',' + lastThreeFinancialGap;
			}
			var resFinancialGap = otherNumbersFinancialGap.replace(/\B(?=(\d{3})+(?!\d))/g, ",") + lastThreeFinancialGap;
			var resFinancialGapSplit = resFinancialGap.split('-');
			if(resFinancialGapSplit[0]=="") {
				resFinancialGap = resFinancialGapSplit[1];
			}
			if(valSplitFinancialGap[1]!=undefined) {
				$(".netMonthlyIncome").val(netMonthlyIncomeMinus+resFinancialGap+'.'+valSplitFinancialGap[1]);
			}
			else {
				$(".netMonthlyIncome").val(netMonthlyIncomeMinus+resFinancialGap+'.00');
			}
			
			//$(".netMonthlyIncome").val(netMonthlyIncome);
			/*$(".financialGap_message").empty();
			var netMonthlyIncomeResult = netMonthlyIncome;
			if($(".netMonthlyIncome").val().indexOf('-') > -1) {
				netMonthlyIncomeResult = $(".netMonthlyIncome").val().replace(/[^0-9\.\-]/g,'');
				netMonthlyIncomeResult = parseFloat(netMonthlyIncomeResult);
			}
			if(netMonthlyIncome>0) {
				//Positive
				$(".financialGap_message").append('<div class="financialGap_positive">There is a $<span>'+netMonthlyIncomeResult+'.00</span> difference between the cost of your college attendance and the money you already have.</div>');
			}
			else {
				//Negative
				$(".financialGap_message").append('<div class="financialGap_negative">Good job! You have $<span>'+netMonthlyIncomeResult.toFixed(2)+'</span> more in savings and financial aid than the cost of your college attendence.</div>');
			}*/
		}
	});
});