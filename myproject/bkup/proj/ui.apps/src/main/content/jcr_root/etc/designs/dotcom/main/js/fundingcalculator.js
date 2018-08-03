$(document).ready(function(){
	$('.calculator_div input').keyup(function(evt) {	
		this.value = this.value.replace(/[^0-9\.,]/g,'');
	});
	$(".resetCalVal").click(function() {
		$('.calculator_div input').val("");
		$('.financialGap_positive,.financialGap_negative').addClass('hide');
        if($('.calculator_div').prev().hasClass('school_button')) {
			$('.school_annual_exp').removeClass('active');
        }
	});
	$('.calculator_div input').bind('blur', function(){
		if(!$('.calculator_div').hasClass('MonthlyCalc')) {
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
				if($('.annaul_subTotal').val()=='') {
					$('.annaulExpense').val('');
				}
				if($('.scholarship_subTotal').val()=='' && $('.annual_subTotal').val()=='') {
					$('.annualFinancial').val('');
				}
				if($('.annaulExpense').val()=='' || $('.annualFinancial').val()=='') {
					$('.totalFinancialGap').val('');
					$('.financialGap_positive,.financialGap_negative').addClass('hide');
				}
			}	
			if(nullCheck>0)
			{
				subTotalVal=subTotalVal.toString();
				var valSplit = subTotalVal.split('.')
				subTotalVal=valSplit[0];
				if(valSplit[1]!=undefined) {
					if(valSplit[1]>2) {
						valSplit[1] = valSplit[1].substring(0,2);
					}
					if(valSplit[1].length==1) {
						valSplit[1] = valSplit[1]+'0';
					}
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
			if($(".annaul_subTotal").val()!=""){
				$(".annaulExpense").val($(".annaul_subTotal").val());
			}
			if($(".scholarship_subTotal").val()!="" || $(".annual_subTotal").val()!=""){
				var scholarship_subTotal = 0;			
				var annual_subTotal = 0;			
				if($(".scholarship_subTotal").val()!="") {
					var scholarship_subTotalVal = $(".scholarship_subTotal").val().replace(/[^0-9\.]/g,'');
					scholarship_subTotal = parseFloat(scholarship_subTotalVal);
				}
				if($(".annual_subTotal").val()!="") {
					var annual_subTotalVal = $(".annual_subTotal").val().replace(/[^0-9\.]/g,'');
					annual_subTotal = parseFloat(annual_subTotalVal);
				}
				var annualFinancialSub = scholarship_subTotal + annual_subTotal;
				
				annualFinancialSub=annualFinancialSub.toString();
				var valSplitFinancial = annualFinancialSub.split('.')
				annualFinancialSub=valSplitFinancial[0];				
				if(valSplitFinancial[1]!=undefined) {
					if(valSplitFinancial[1]>2) {
						valSplitFinancial[1] = valSplitFinancial[1].substring(0,2);
					}
					if(valSplitFinancial[1].length==1) {
						valSplitFinancial[1] = valSplitFinancial[1]+'0';
					}
				}
				var lastThreeFinancial = annualFinancialSub.substring(annualFinancialSub.length-3);
				var otherNumbersFinancial = annualFinancialSub.substring(0,annualFinancialSub.length-3);
				if(otherNumbersFinancial != '') {
					lastThreeFinancial = ',' + lastThreeFinancial;
				}
				var resFinancial = otherNumbersFinancial.replace(/\B(?=(\d{3})+(?!\d))/g, ",") + lastThreeFinancial;
				if(valSplitFinancial[1]!=undefined) {
					$(".annualFinancial").val(resFinancial+'.'+valSplitFinancial[1]);
				}
				else {
					$(".annualFinancial").val(resFinancial+'.00');
				}		
				//$(".annualFinancial").val(annualFinancialSub);
			}
			if($(".annaulExpense").val()!="" && $(".annualFinancial").val()!=""){
				var annaulExpense = 0;
				var annualFinancial = 0;
				if($(".annaulExpense").val()!="") {
					var annaulExpenseVal = $(".annaulExpense").val().replace(/[^0-9\.]/g,'');
					annaulExpense = parseFloat(annaulExpenseVal);
				}
				if($(".annualFinancial").val()!="") {
					var annualFinancialVal = $(".annualFinancial").val().replace(/[^0-9\.]/g,'');
					annualFinancial = parseFloat(annualFinancialVal);
				}
				var totalFinancialGap = annualFinancial - annaulExpense;
				
				var totalFinancialGapMinus = "";
				var totalFinancialGapMinusSplit = totalFinancialGap.toString();
				totalFinancialGapMinusSplit = totalFinancialGapMinusSplit.split('-');
				if(totalFinancialGapMinusSplit[0]=="") {
					totalFinancialGapMinus = '-';
				}
				totalFinancialGap=totalFinancialGap.toString();			
				var valSplitFinancialGap = totalFinancialGap.split('.')
				totalFinancialGap=valSplitFinancialGap[0];				
				if(valSplitFinancialGap[1]!=undefined) {
					if(valSplitFinancialGap[1]>2) {
						valSplitFinancialGap[1] = valSplitFinancialGap[1].substring(0,2);
					}
					if(valSplitFinancialGap[1].length==1) {
						valSplitFinancialGap[1] = valSplitFinancialGap[1]+'0';
					}
				}
				var lastThreeFinancialGap = totalFinancialGap.substring(totalFinancialGap.length-3);
				var otherNumbersFinancialGap = totalFinancialGap.substring(0,totalFinancialGap.length-3);
				if(otherNumbersFinancialGap != '' && otherNumbersFinancialGap !='-') { 
					lastThreeFinancialGap = ',' + lastThreeFinancialGap;
				}
				var resFinancialGap = otherNumbersFinancialGap.replace(/\B(?=(\d{3})+(?!\d))/g, ",") + lastThreeFinancialGap;
				var resFinancialGapSplit = resFinancialGap.split('-');
				if(resFinancialGapSplit[0]=="") {
					resFinancialGap = resFinancialGapSplit[1];
				}
				if(valSplitFinancialGap[1]!=undefined) {
					$(".totalFinancialGap").val(totalFinancialGapMinus+resFinancialGap+'.'+valSplitFinancialGap[1]);
				}
				else {
					$(".totalFinancialGap").val(totalFinancialGapMinus+resFinancialGap+'.00');
				}
				
				//$(".totalFinancialGap").val(totalFinancialGap);
				var totalFinancialGapResult = totalFinancialGap;
				if($(".totalFinancialGap").val().indexOf('-') > -1) {
					totalFinancialGapResult = $(".totalFinancialGap").val().replace(/[^0-9\.\-]/g,'');
					totalFinancialGapResult = parseFloat(totalFinancialGapResult);
				}

				if(totalFinancialGap>0) {
					//Positive
					$(".financialGap_positive span").html('');
					if(valSplitFinancialGap[1]!=undefined) {
						$(".financialGap_positive span").html(totalFinancialGapMinus+resFinancialGap+'.'+valSplitFinancialGap[1]);
					}
					else {
						$(".financialGap_positive span").html(totalFinancialGapMinus+resFinancialGap+'.00');
					}
					$(".financialGap_positive").removeClass('hide');
					$(".financialGap_negative").addClass('hide');
				}
				else if(totalFinancialGap<0) {
					//Negative
					$(".financialGap_negative span").html('');					
					if(valSplitFinancialGap[1]!=undefined) {
						$(".financialGap_negative span").html(resFinancialGap+'.'+valSplitFinancialGap[1]);
					}
					else {
						$(".financialGap_negative span").html(resFinancialGap+'.00');
					}
					$(".financialGap_negative").removeClass('hide');
					$(".financialGap_positive").addClass('hide');
				}
                else if(totalFinancialGap==0) {
					$(".financialGap_positive").addClass('hide');
                    $(".financialGap_negative").addClass('hide');
                }
			}
		}
	});
	if(window.innerWidth<801){
		$(document).on('click','.calculator_div label',function(e){                        
			e.preventDefault();
        });
    }
    schoolTypeButtonHeight();
	function schoolTypeButtonHeight() {
		var maxHeight = 0;
		$(".school_button .school_annual_exp").each(function(){
			if ($(this).height() > maxHeight) 
			{ 
				maxHeight = $(this).height(); 
			}
		});
		$('.school_button .school_annual_exp').height(maxHeight);
	}
	$(window).resize(function(){
		schoolTypeButtonHeight();
	});
	
	$(document).on('click','.school_annual_exp',function(){
		$('.school_annual_exp').removeClass('active');
        $(this).addClass('active');
		var idVal = $(this).attr('id');
		var idValTotal = 0;
		$(".calculator_div table.savings_contribution input").val('');
		$(".calculator_div table.savings_contribution input[data-"+idVal+"]").each(function(){
			var idAttrVal = $(this).attr('data-'+idVal);
			$(this).val(idAttrVal);
		});
        $(".calculator_div table.savings_contribution input:not([data-"+idVal+"])").each(function(){
			$(this).val('0.00');
		});
        $(".calculator_div table.savings_contribution input").trigger('blur');
	});
});