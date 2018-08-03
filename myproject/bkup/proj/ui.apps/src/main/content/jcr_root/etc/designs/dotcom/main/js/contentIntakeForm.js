$(document).ready(function(){ 
var isIE = navigator.appName == 'Microsoft Internet Explorer'
								|| !!(navigator.userAgent.match(/Trident/) || navigator.userAgent
										.match(/rv:11/))
								|| (typeof $.browser !== "undefined" && $.browser.msie == 1);
    if(isIE){    
jQuery.fn.putCursorAtEnd = function() {

  return this.each(function() {
    
    // Cache references
    var $el = $(this),
        el = this;

    // Only focus if input isn't already
    if (!$el.is(":focus")) {
     $el.focus();
    }

    // If this function exists... (IE 9+)
    if (el.setSelectionRange) {

      // Double the length because Opera is inconsistent about whether a carriage return is one character or two.
      var len = $el.val().length * 2;
      
      // Timeout seems to be required for Blink
      setTimeout(function() {
        el.setSelectionRange(len, len);
      }, 1);
    
    } else {
      
      // As a fallback, replace the contents with itself
      // Doesn't work in Chrome, but Chrome supports setSelectionRange
      $el.val($el.val());
      
    }

    // Scroll to the bottom, in case we're in a tall textarea
    // (Necessary for Firefox and Chrome)
   // this.scrollTop = 100;

  });

};
}
    $('.contentintakeform select').uniform({
        selectClass : 'sun-select-container',
        selectAutoWidth : false
    }).each(
        function() {
            
            $(this).siblings("span").attr(
                "aria-hidden", true);
        });
//$("select#cmbComplianceSingleUser").not("#uniform-cmbComplianceSingleUser").uniform();
//$("select#cmbLegalSingleUser").not("#uniform-cmbLegalSingleUser").uniform();

$("select#cmbComplianceSingleUser").select2();
$("select#cmbLegalSingleUser").select2();

$( "input.pageNameValid" ).focus(function() {
  $( this ).removeClass("errorBorderClass");
});

$('#button').click(function () {
    $("input[type='file']").trigger('click');
});

$("input[type='file']").change(function () {
    $('#val').text(this.value.replace(/C:\\fakepath\\/i, ''));
    if(isIE){
		$("#val").text(this.files && this.files.length ?
          this.files[0].name : this.value.replace(/^C:\\fakepath\\/i, ''));
    }
	$("#val").append("<span id='closeIcon'>&times;</span>");
	$("#closeIcon").click(function (){
        $("#fileUpolad").val("");
        $('#val').empty();
	});
});
$('.suntrust-radio-button').change(function() {

	var a = $(this).is(":checked");
	if(a = "true"){
		$(this).parent().removeClass("errorBorderClass");
	}
    });

    var wftypetext = $("#wftype").val();
if(wftypetext == 'Expedited') {
	$("#uniform-cmbComplianceSingleUser").parent().parent().hide();
    $("#uniform-cmbLegalSingleUser").parent().parent().hide();
}

var globalTextvalue;

$("#iw_submit_comment_id").on('focus',function() {
    $(this).val(globalTextvalue);
	if(isIE){
		$(this).putCursorAtEnd();
	}
    $( this ).removeClass("errorBorderClass");
});

$("#iw_submit_comment_id").on('blur',function(){
    $( this ).removeClass("errorBorderClass");
	console.log("blur called");	
    var JobTypeSel=$("#Jobtype :selected" ).val();
    var RequesterType=" Requester: " + $("#Name").val() + ";";
    var LRTypeSel=" Legal Reviewer: " + $("#cmbLegalSingleUser :selected" ).text() + ";";
    var CRTypeSel=" Compliance Reviewer: " + $("#cmbComplianceSingleUser :selected" ).text();
    //var initDesc = "ST.com - ";
    var initDesc = "";
    var jd_value = $(this).val();


    globalTextvalue = jd_value;

    if($('#wftype option:selected').val() == "Expedited"){
                    $("#iw_submit_comment_id").val(initDesc + JobTypeSel + " - " + jd_value + RequesterType);
                   // alert("IF Part Value ::  " + jd_value);
    }
   	else if(jd_value != ""){
        if(JobTypeSel != ""){
            $("#iw_submit_comment_id").val(initDesc + JobTypeSel + " - " + jd_value + RequesterType + CRTypeSel + " ; " + LRTypeSel);
        }
        else{
			$("#iw_submit_comment_id").val(initDesc + JobTypeSel + "" + jd_value + RequesterType + CRTypeSel + " ; " + LRTypeSel);
        }
                   // alert("IF Part Value ::  " + jd_value);
    }
    else {     
                    $("#iw_submit_comment_id").val(initDesc + JobTypeSel + "" + jd_value + RequesterType + CRTypeSel + " ; " + LRTypeSel);
                   // alert("else Part Value >>  " + jd_value);
    }
});




var contentformPage = $(".contentintakeform");
    if(contentformPage.is(':visible')){
		$("body").addClass("contentRemovePadding");
    }
var wftypetext = $("#wftype").val();
if(wftypetext === 'Expedited') {
	$("#uniform-cmbComplianceSingleUser").parent().parent().hide();
    $("#uniform-cmbLegalSingleUser").parent().parent().hide();
}   

    $("#wftype").on("change",function(){
		$("#wftype").parent().removeClass("errorBorderClass");
        //$("#submitButtonBottom").removeClass('btnDisable');
    });
    $("#Jobtype").on("change",function(){
		$("#Jobtype").parent().removeClass("errorBorderClass");
    });
    $("#cmbComplianceSingleUser").on("change",function(){
       var legal = $("#select2-cmbComplianceSingleUser-container").text();
    if(legal != "Select One"){
        console.log("1");
        $("#select2-cmbComplianceSingleUser-container").parent().removeClass("errorBorderClass");
    }

    });
    $("#cmbLegalSingleUser").on("change",function(){
  	var compliance = $("#select2-cmbLegalSingleUser-container").text();
    if(compliance != "Select One"){
        $("#select2-cmbLegalSingleUser-container").parent().removeClass("errorBorderClass");
    }
    });

});
var submitButton = true;
var template = 'solutions/standexp_submit_with_email.wft';
var flag=0;
var tname='STcom Approval Workflow';

function formSubmit(button)
{

	var failure = function(err) {
        alert("Unable to retrive data "+err);
   };
   
   var requiredFieldCheck = true;

    var publishMonth= $('#PublishMonth').val();
	var publishDay= $('#PublishDay').val();
	var publishYear= $('#PublishYear').val();
	var publishDateText = "";

	if(publishMonth != "0" && publishDay != "00")
	{
		publishDateText = publishMonth + "/" + publishDay + "/" + publishYear;
	}
	
	var expirationMonth= $('#ExpirationMonth').val();
	var expirationDay= $('#ExpirationDay').val();
	var expirationYear= $('#ExpirationYear').val();
	var expirationDateText = "";
	
	if(expirationMonth != "0" && expirationDay != "00")
	{
		expirationDateText = expirationMonth + "/" + expirationDay + "/" + expirationYear;
	}
	
	var selectedCompilanceReviewerName = $('#cmbComplianceSingleUser option:selected').text();
	var selectedLegalReviewerName = $('#cmbLegalSingleUser option:selected').text();
	
	selectedCompilanceReviewerName = (selectedCompilanceReviewerName === 'Select One') ? "N/A" : selectedCompilanceReviewerName;
	selectedLegalReviewerName = (selectedLegalReviewerName === 'Select One') ? "Select One": selectedLegalReviewerName;


	if($("#wftype").val()=="NA"){
        $("#wftype").parent().addClass("errorBorderClass");
        requiredFieldCheck = false;
    }
    if($("#Jobtype").val()==""){
        $("#Jobtype").parent().addClass("errorBorderClass");
        requiredFieldCheck = false;
    }
    
    
    
    var textAreaElement = $("textarea.iw-base-text-field-data");	
    var text = $("textarea.iw-base-text-field-data").val();
    if(text == ""){
        textAreaElement.addClass("errorBorderClass");
        requiredFieldCheck = false;
    }
    
    var pageName = $(".pageNameValid").val();
    var pagenameElement = $(".pageNameValid");
    
    if(pageName == ""){
        pagenameElement.addClass("errorBorderClass");
        requiredFieldCheck = false;
    }

    if($("#wftype").val()!="Expedited"){
        if($("#cmbComplianceSingleUser").val()=="N/A"){
            //$("#cmbComplianceSingleUser").parent().next().children().find(".select2-selection--single").addClass("errorBorderClass");
            $("#select2-cmbComplianceSingleUser-container").parent().addClass('errorBorderClass');
            requiredFieldCheck = false;
        }
        /*if($("#cmbLegalSingleUser").val()=="Select One"){
            $("#cmbLegalSingleUser").parent().next().children().find(".select2-selection--single").addClass("errorBorderClass");
            requiredFieldCheck = false;
        }*/
    }

    if (submitButton) {
	if(tname == "STcom Approval Workflow")
	{

        if(requiredFieldCheck) {

            console.log('ajax call');
             // Get form
                var form = $('#fileUploadForm')[0];
        
                // Create an FormData object
                var data = new FormData(form);
                var jobDescription = $("#iw_submit_comment_id").val();
                var contentIntakePageName = $("#contentIntakePageName").val();
                var publishComments = $("#PublishComments").val();
                var existingUrls = $("#existing-url").val();
                jobDescription = encodeURIComponent(jobDescription);
                contentIntakePageName = encodeURIComponent(contentIntakePageName);
                publishComments = encodeURIComponent(publishComments);
                existingUrls = encodeURIComponent(existingUrls);
                data.append("encodeJobDescription", jobDescription);
                data.append("encodedContentIntakePageName",contentIntakePageName);
                data.append("encodedPublishComments", publishComments);
                data.append("encodedExistingUrls", existingUrls);
                data.append("publishDateText", publishDateText);
                data.append("expirationDateText", expirationDateText);
                
                data.append("selectedCompilanceReviewerName", selectedCompilanceReviewerName);
                data.append("selectedLegalReviewerName", selectedLegalReviewerName);
          
          
            //Use JQuery AJAX request to post data to a Sling Servlet
            $.ajax({
                 type: 'POST',    
                 enctype: 'multipart/form-data',
                 url:'/dotcom/content-intake-search',
                 data: data,
                 processData: false,
                 contentType: false,
                 cache: false,
                 
                 success: function(msg){ 
                    var json = jQuery.parseJSON(msg);
                    var uniqueID = json.uniqueID;
                    var formSubmisionStatus = json.formSubmisionStatus;
                    var assetUploadStatus = json.assetUploadStatus;
                    var uniqueIDPageStatus = json.uniqueIDPageStatus;
                    //alert("Form submission request (" + uniqueID + ") status : " + formSubmisionStatus);
                    $('#formSubmissionStatus').text("Form submission request (" + uniqueID + ") status : " + formSubmisionStatus + "." + assetUploadStatus + uniqueIDPageStatus);
                    $("#submitButtonBottom").addClass('btnDisable');
                    	$("#submitButtonBottom").click(function(event) {
                    	  event.preventDefault();
                    	});
                     if(assetUploadStatus != "" || uniqueIDPageStatus != "")
                    {
                        //$("#submitButtonBottom").prop("disabled", false);
                    	 $("#submitButtonBottom").removeClass('btnDisable');
                    }
                     $("#formSubmissionStatus").show();
                     $(".mandatoryCheck").hide();
                 }
             });

        }
        else {
            $(".mandatoryCheck").show();
            $("#wftype").focus();
            $("#formSubmissionStatus").hide();
        }
	}
	else
	{
		alert("sucess");
		submitButton = false;
        button.disabled = true;
        document.getElementById("submitButtonBottom").disabled = true;
		document.forms.iwwft_instantiator.submit();
	}
    }
    return false;
}

function checkText(obj)
{
	if(obj.value.length >499)
	{
		alert("The Max Length Exceeded,Acceptable Length 499 Characters");
		obj.value="";
	}
}

function addSTComm(txtArea){
	if(txtArea.value != ""){
	var JobType = "";
	var RequesterType = "";
	var LCType = "";
	var LegalType = "";
	var CompType = "";
	var textAreaElement = $("textarea.iw-base-text-field-data");
	textAreaElement.removeClass("errorBorderClass");
	if(document.getElementById("wftype").value == "Expedited" || document.getElementById("wftype").value == "Standard")
	{
		JobType = document.getElementById("Jobtype").value;
		if(JobType == 0)
		{JobType = "";}
		RequesterType = " Requester: " + document.getElementById("Name").value + "; ";
	}
	
	if(document.getElementById("wftype").value == "Standard")
	{
		/* LCType = document.getElementById("Category");
		if(LCType.selectedIndex != "0")
		{ */
		LCType = LCType.options[LCType.selectedIndex].text;
		LCType = "L&C Group: " + LCType + "; " ;
		}else{
		LCType = "L&C Group: Not specified; ";
		}
		if(document.getElementById("SkipLegComp").value != "Yes")
		{
		LegalType = document.getElementById("cmbLegalSingleUser");	
		if(LegalType.selectedIndex != "0")
		{
		LegalType = LegalType.options[LegalType.selectedIndex].text;
		LegalType = "Legal: " + LegalType + "; ";
		}else{
		LegalType = "Legal: Not specified; ";
		}
		
		CompType = document.getElementById("cmbComplianceSingleUser");
		if(CompType.selectedIndex != "0")
		{
		CompType = CompType.options[CompType.selectedIndex].text;
		CompType = "Compliance: " + CompType + "; ";
		}else{
		CompType = "Compliance: Not specified; ";
		}
		
		}
				
	}
	
	var initDesc = "ST.com - ";

	if(txtArea.value != "" && JobType != ""){
	txtArea.value = initDesc + JobType + " - " + txtArea.value + RequesterType + LCType + LegalType + CompType;
	}
	else{
	txtArea.value = initDesc + JobType + "" + txtArea.value + RequesterType + LCType + LegalType + CompType;
	}
	//}
}

function removeSTComm(txtArea){
    $(".textAreasize").removeClass("errorBorderClass");
    if(txtArea.value != ""){
	var JobType = "";
	if(document.getElementById("wftype").value == "Expedited" || document.getElementById("wftype").value == "Standard")
	{
		JobType = document.getElementById("Jobtype").value;
		if(JobType == 0)
		{JobType = "";}
	}	
    var jType = txtArea.value.split("-");
    var jTypeLength = jType.length-1;
        console.log(jTypeLength);
    var JobTypeSel=$("#Jobtype :selected" ).val().split("-");

    if(JobTypeSel.length>1) {
        if(jType.length>3) {
        	var jTypeSpace = jType[jTypeLength].split(" ");            
	        var hitRequest = 0;
	        var finalTextarea = "";
	        for(var i=0;i<jTypeSpace.length-1;i++) {
	            if(i>0 && hitRequest==0 && jTypeSpace[i]!="Requester:" && jTypeSpace[i]!="") {
	                finalTextarea += jTypeSpace[i]+" ";
	            }
	            else if(i>0) {
					hitRequest++;
	            }
	        }
	        txtArea.value = finalTextarea;

        }
        else
		{
            txtArea.value="";
		}
    }
    else {        
		if(jType.length==2 ) {
            var jTypeSpace = jType[jTypeLength].split(" ");

			var jTypeTextVal = jType[jTypeLength].split("Requester");

			var hitRequest = 0;
            var finalTextarea = "";
            var jTypeTextValConcat = "";
            for(var j=0;j<jType.length-1;j++) {
                if(j>1) {
                    if(j==2) {
						jType[j]=jType[j].trim()+' ';
                    }
                    jTypeTextValConcat += jType[j]+'-';
                }
            }
            for(var i=0;i<jTypeSpace.length-1;i++) {
                if(i>0 && hitRequest==0 && jTypeSpace[i]!="Requester:" && jTypeSpace[i]!="") {
                    finalTextarea += jTypeSpace[i]+" ";
                }
                else if(i>0) {
					hitRequest++;
                }
            }
            txtArea.value = jTypeTextValConcat+' '+jTypeTextVal[0].trim()+' ';
        }
		else if(jType.length > 2 ) {
			var jTypeTextVal = jType[jTypeLength].split("Requester");
            var jTypeTextValConcat = "";
            for(var j=0;j<jType.length-1;j++) {
                if(j>1) {
                    if(j==2) {
						jType[j]=jType[j].trim()+' ';
                    }
                    jTypeTextValConcat += jType[j]+'-';
                }
            }
            txtArea.value = jTypeTextValConcat+' '+jType[jTypeLength-1].trim()+' - '+jTypeTextVal[0].trim()+' ';
        }
        else
		{			
			txtArea.value="";
		}
    }

	/*txtArea.value = txtArea.value.substring(jType[0].length + jType[1].length + 3, txtArea.value.length);
        console.log("removeSTComm - "+txtArea.value);
	txtArea.value = txtArea.value.substr(0, txtArea.value.indexOf("Requester")); 
        console.log("removeSTComm - "+txtArea.value);
        console.log("removeSTComm - "+txtArea.value.indexOf("Requester"));*/

	}
}

function checkForPublish(obj)
{
	if(obj.value.length >500)
	{
		alert("The Max Length Exceeded,Acceptable Length 500 Words");
		obj.value="";
	}
}

function showText(obj)
{
	var selelem="";
	if(obj.value == "Expedited")
	{
		//document.getElementById('Category').disabled=true;
		document.getElementById('cmbLegalSingleUser').disabled=true;
        
        //document.getElementById('g23').parentNode.parentNode.style.display = 'none';
        //document.getElementById('Category').parentNode.parentNode.style.display = 'none';
        document.getElementById('cmbLegalSingleUser').parentNode.parentNode.style.display = 'none';
        document.getElementById('cmbComplianceSingleUser').parentNode.parentNode.style.display = 'none';
       	$("#uniform-cmbComplianceSingleUser").parent().parent().hide();
    	$("#uniform-cmbLegalSingleUser").parent().parent().hide();
		//document.getElementById('g23').disabled=true;
		//document.getElementById('g12').disabled=true;
        //document.getElementById('g23').checked=true;
        
		document.getElementById('cmbComplianceSingleUser').disabled=true;
		document.getElementById('wftype').classList.remove("errorBorderClass");
		$(".ComplianceReviewcheck").removeClass("errorBorderClass");
		$(".changeTypeCheck").removeClass("errorBorderClass");
		//document.getElementById('texting').style.display=block;
		//document.getElementById('g11').checked=true;
		document.getElementById('po11').checked=true;
		/*selelem = document.getElementById('RequestType');
		selelem.remove(11);
		selelem.remove(10);
		selelem.remove(9);
		selelem.remove(8);
		selelem.remove(7);
		selelem.remove(6);
		selelem.remove(5);
		selelem.remove(4);
		selelem.remove(3);
		selelem.remove(2);
		selelem.remove(1);
		selelem.options[1]=new Option('Rates','Rates');
		selelem.options[2]=new Option('Compliance Issues','ComplianceIssues');
		selelem.options[3]=new Option('Fraud Alerts','FraudAlerts');
		selelem.options[4]=new Option('Legal Issues','LegalIssues');
		selelem.options[5]=new Option('Resource Center Metadata Tag','ResourceCenter');
		selelem.options[6]=new Option('Advisor','Advisor');		 
		selelem.options[7]=new Option('Vanity URL','VanityURL');
		selelem.options[8]=new Option('Domain Request','DomainRequest');
		selelem.options[9]=new Option('T&T Image Update','TandTImageUpdate');
		selelem.options[10]=new Option('Tracking Code Changes','TrackingCodeChanges');
		selelem.options[11]=new Option('Immediate Technology Fix','ImmediateTechnologyFix');*/
		
	}
	else
	{
    if(obj.value == "Standard")
	{
        //document.getElementById('g23').parentNode.parentNode.style.display = '';
               // document.getElementById('Category').parentNode.parentNode.style.display = '';
        document.getElementById('cmbLegalSingleUser').parentNode.parentNode.style.display = '';
        document.getElementById('cmbComplianceSingleUser').parentNode.parentNode.style.display = '';
        $("#uniform-cmbComplianceSingleUser").parent().parent().show();
    	$("#uniform-cmbLegalSingleUser").parent().parent().show();
		//document.getElementById('Category').disabled=false;
		document.getElementById('cmbLegalSingleUser').disabled=false;
		document.getElementById('cmbComplianceSingleUser').disabled=false;
       // document.getElementById('g23').disabled=false;
		//document.getElementById('g12').disabled=false;
		//document.getElementById('g12').checked=true;
		//document.getElementById('g11').checked=true;
		document.getElementById('po11').checked=true;
		document.getElementById('wftype').classList.remove("errorBorderClass");
		$(".ComplianceReviewcheck").removeClass("errorBorderClass");
		$(".changeTypeCheck").removeClass("errorBorderClass");
		if(obj.value == "Standard")
		{
		/*selelem = document.getElementById('RequestType');
		selelem.remove(6);
		selelem.remove(5);
		selelem.remove(4);
		selelem.remove(3);
		selelem.remove(2);
		selelem.remove(1);
		selelem.options[1]=new Option('Earnings Release','EarningsRelease');
		selelem.options[2]=new Option('Investor Relations','InvestorRelations');
		selelem.options[3]=new Option('Link Management','LinkManagement');
		selelem.options[4]=new Option('Meta-data Changes/SEO Changes','MetadataChanges/SEOChanges');
		selelem.options[5]=new Option('Multimedia Management','MultimediaManagement');
		selelem.options[6]=new Option('Page Management','PageManagement');
		selelem.options[7]=new Option('Press Releases','PressReleases');
		selelem.options[8]=new Option('Speed Bumps','SpeedBumps');
		selelem.options[9]=new Option('Standard Copy Changes','StandardCopyChanges');
		selelem.options[10]=new Option('Resource Center','ResourceCenter');
		selelem.options[11]=new Option('Advisor','Advisor');	
		selelem.options[12]=new Option('Vanity URL','VanityURL');
		selelem.options[13]=new Option('Domain Request','DomainRequest');
		selelem.options[14]=new Option('T&T Image Update','TandTImageUpdate');
		selelem.options[15]=new Option('Tracking Code Changes','TrackingCodeChanges');
		selelem.options[16]=new Option('Immediate Technology Fix','ImmediateTechnologyFix');*/
		
		}
    }
	}
}
function removeText(objt)
{
	alert(objt.value);
}

function addLocation(valu)
{
	document.getElementById('loc').disabled=false;
}

function remLocation(valu)
{
	document.getElementById('loc').disabled=true;
}

function  addFileSel(val)
{
	var ni = document.getElementById('bf');
	var fileno=ni.getElementsByTagName('input');
	var nio= document.getElementById('bd');
	if(fileno.length<=4)
	{
	var newdiv = document.createElement('input');
	var newtr= document.createElement('tr');
	var newtd= document.createElement('td');
	newdiv.setAttribute('type',"file");
	newdiv.setAttribute('label',"file Selection");
	newdiv.setAttribute('id',"fileSelection");
	newdiv.setAttribute('size',"57");
	newdiv.setAttribute('onchange',"getUpload()");
	ni.appendChild(newdiv);
	newtr.appendChild(newtd);
	ni.appendChild(newtr);
	var newdivo= document.createElement('textarea');
	var newtr1= document.createElement('tr');
	var newtd1= document.createElement('td');
	newdivo.setAttribute('rows',"3");
	newdivo.setAttribute('cols',"30");
	nio.appendChild(newdivo);
	newtr1.appendChild(newtd1);	
	nio.appendChild(newtr1);
	
	}
	else
	{
		alert("Maximum number of allowed file selection is only 5"); 
	}

}

function validateDate(dropDown)
{
	 var dt = new Date();
	  var date = dt.getDate();
	  var mon1 = dt.getMonth();
          var mon = mon1+1;
	  var year = dt.getFullYear();
	  var ind1 = document.getElementById("ExpirationMonth").options[document.getElementById("ExpirationMonth").selectedIndex].value;
		var ind = ind1;
          	
		
          var indDate1 = document.getElementById("ExpirationDay").options[document.getElementById("ExpirationDay").selectedIndex].value;
	  var indDate = indDate1;
          var test = date;
          var indYear1 = document.getElementById("ExpirationYear").options[document.getElementById("ExpirationYear").selectedIndex].value;
          var indYear = indYear1-2010;
          var testYear = year-2010;


if(ind1==2 &&  indDate1 >=30)
{
alert("Date selected is not a valid date.");
document.getElementById("ExpirationMonth").selectedIndex = mon1+1;
document.getElementById("ExpirationDay").selectedIndex = date;
document.getElementById("ExpirationYear").selectedIndex= testYear;
document.getElementById("ExpirationYear").selectedIndex= testYear
}


if(ind1==4 &&  indDate1 >30 || ind1==6 &&  indDate1 >30 || ind1==9 &&  indDate1 >30 || ind1==11 &&  indDate1 >30)
{
alert("Month Date selected is not a valid date.");
document.getElementById("ExpirationMonth").selectedIndex = mon1+1;
document.getElementById("ExpirationDay").selectedIndex = date;
document.getElementById("ExpirationYear").selectedIndex= testYear;
document.getElementById("ExpirationYear").selectedIndex= testYear
}



if(indYear <= testYear)
		{
			if(indYear == testYear)
			{
				if(ind <= mon)
				{
					if(ind == mon)
					{
						if(indDate != 0)
						{
						if(indDate < test)
						{
							
							document.getElementById("ExpirationMonth").selectedIndex = mon1+1;
							document.getElementById("ExpirationDay").selectedIndex = date;
							document.getElementById("ExpirationYear").selectedIndex= testYear;
							
							alert("Please select a valid future planned publication date.");
						}
						}
					}
					else
					{
						document.getElementById("ExpirationMonth").selectedIndex = mon1+1;
						document.getElementById("ExpirationDay").selectedIndex = date;
						document.getElementById("ExpirationYear").selectedIndex= testYear;
						
						alert("Please select a valid future planned publication date.");
					}
				}
			}
			else
			{
				document.getElementById("ExpirationMonth").selectedIndex = mon1+1;
				document.getElementById("ExpirationDay").selectedIndex = date;
				document.getElementById("ExpirationYear").selectedIndex= testYear;
				
				alert("Please select a valid future planned publication date.");
			}
		}

		}

/*****/
function validateDates(dropDown)
{
	 var dt_2 = new Date();
	  var date_2 = dt_2.getDate();
	  var mon2 = dt_2.getMonth();
          var mon_1 = mon2+1;
	  var year_2 = dt_2.getFullYear();
	  var ind2 = document.getElementById("PublishMonth").options[document.getElementById("PublishMonth").selectedIndex].value;
		var ind_1 = ind2;
          	
		
          var indDate1_1 = document.getElementById("PublishDay").options[document.getElementById("PublishDay").selectedIndex].value;
	  var indDate_2 = indDate1_1;
          var test_1 = date_2;
          var indYear1_1 = document.getElementById("PublishYear").options[document.getElementById("PublishYear").selectedIndex].value;
          var indYear_2 = indYear1_1-2010;
          var testYear_1 = year_2-2010;


if(ind2==2 &&  indDate1_1 >=30)
{
alert("Date selected is not a valid date.");
document.getElementById("PublishMonth").selectedIndex = mon2+1;
document.getElementById("PublishDay").selectedIndex = date_2;
document.getElementById("ExpirationYear").selectedIndex= testYear_1;
document.getElementById("PublishYear").selectedIndex= testYear_1;
}


if(ind2==4 &&  indDate1_1 >30 || ind2==6 &&  indDate1_1 >30 || ind2==9 &&  indDate1_1 >30 || ind2==11 &&  indDate1_1 >30)
{
alert("Month Date selected is not a valid date.");
document.getElementById("PublishMonth").selectedIndex = mon2+1;
document.getElementById("PublishDay").selectedIndex = date_2;
document.getElementById("PublishYear").selectedIndex= testYear_1;
document.getElementById("PublishYear").selectedIndex= testYear_1;
}



if(indYear_2 <= testYear_1)
		{
			if(indYear_2 == testYear_1)
			{
				if(ind_1 <= mon_1)
				{
					if(ind_1 == mon_1)
					{
						if(indDate_2 != 0)
						{
						if(indDate_2 < test_1)
						{
							
							document.getElementById("PublishMonth").selectedIndex = mon2+1;
							document.getElementById("PublishDay").selectedIndex = date_2;
							document.getElementById("PublishYear").selectedIndex= testYear_1;
							
							alert("Please select a valid future planned publication date.");
						}
						}
					}
					else
					{
						document.getElementById("PublishMonth").selectedIndex = mon2+1;
						document.getElementById("PublishDay").selectedIndex = date_2;
						document.getElementById("PublishYear").selectedIndex= testYear_1;
						
						alert("Please select a valid future planned publication date.");
					}
				}
			}
			else
			{
				document.getElementById("PublishMonth").selectedIndex = mon2+1;
				document.getElementById("PublishDay").selectedIndex = date_2;
				document.getElementById("PublishYear").selectedIndex= testYear_1;
				
				alert("Please select a valid future planned publication date.");
			}
		}

		}

	function loadUserData(legUserDp,strSource){ 
	try{
		//document.getElementById('Category').classList.remove("errorBorderClass");
		//$("#Category").removeClass("errorBorderClass");
			var strSelectedValue = legUserDp.options[legUserDp.selectedIndex].value;			 
			var selectDropdown;
			var blankDesc;
			var strType;
			blankDesc= "Select a Legal & Compliance Group";	
			var innerDpOptBlak= "";
			
				if(strSource == "LG"){
					selectDropdown = document.iwwft_instantiator.cmbLegalSingleUser;				
					strType = "-legal";
					innerDpOptBlak = "Select a Legal Reviewer"
				}else{
					//CG
					selectDropdown = document.iwwft_instantiator.cmbComplianceSingleUser;			 
					strType = "-compliance";
					innerDpOptBlak = "Select a Compliance Reviewer"
				}
				
			if(strSelectedValue != ""){			
				if (window.DOMParser)
				{
					parser=new DOMParser();
					xmlDoc=parser.parseFromString(formDtaUserXML,"text/xml");
				}
				else // Internet Explorer
				{
					xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
					xmlDoc.async=false;
					xmlDoc.loadXML(formDtaUserXML); 
				}
				
				userXmlList = xmlDoc.getElementsByTagName("USER");
				var cont = 0;
				var prevdate = "";
				var userList = "";
				
				for(cont = 0; cont < userXmlList.length; cont++){
				if(userXmlList[cont].getElementsByTagName("USRGROUP")[0].childNodes.length > 0){
						var userGroups = userXmlList[cont].getElementsByTagName("USRGROUP")[0].childNodes[0].nodeValue;
						var innerGCont = 0;
						var vGroups	= userGroups.split(',');
						if(vGroups.length > 0){
							for(innerGCont=0;innerGCont<vGroups.length;innerGCont++){
								if(vGroups[innerGCont] == (strSelectedValue+strType)){
								if(userList == ""){
									userList = ""+ userXmlList[cont].getElementsByTagName("DISPNAME")[0].childNodes[0].nodeValue + "," + userXmlList[cont].getElementsByTagName("NAME")[0].childNodes[0].nodeValue+"";
									}else{
									userList = userList + "|" +userXmlList[cont].getElementsByTagName("DISPNAME")[0].childNodes[0].nodeValue + "," + userXmlList[cont].getElementsByTagName("NAME")[0].childNodes[0].nodeValue+"";
									}
									break;
								}
							}
						}
					}
				}
				
				
				if(userList != ""){
					removeDropOptions(selectDropdown);
					var vDUserFilter = userList.split('|');
					var contU =0;
					var opt = document.createElement("option");
							opt.text = innerDpOptBlak;
							opt.value = "NA";
							opt.selected = true;
							selectDropdown.options.add(opt);
							for(contU=0;contU<vDUserFilter.length;contU++){
								var opt = document.createElement("option");
								opt.text = vDUserFilter[contU].split(',')[0];
								opt.value = vDUserFilter[contU].split(',')[1];
								selectDropdown.options.add(opt);
							}
							
							if(strSource == "LG"){
								loadUserData(legUserDp,"CG");				
							}				
				}else{
					removeDropOptions(selectDropdown);
					var opt = document.createElement("option");
					opt.text = blankDesc;
					opt.value = "NA";
					opt.selected = true;
					selectDropdown.options.add(opt);
					alert("No users found on the "+ legUserDp.options[legUserDp.selectedIndex].text +" group.");
						if(strSource == "LG"){
							loadUserData(legUserDp,"CG");				
						}
				}
				
				
			}else{
				removeDropOptions(selectDropdown);
				var opt = document.createElement("option");
				opt.text = blankDesc;
				opt.value = "NA";
				opt.selected = true;
				selectDropdown.options.add(opt);
					if(strSource == "LG"){
						loadUserData(legUserDp,"CG");				
					}
			}
		}catch(e){
			alert("The user information could not be loaded.");
		}
	}
	function removeDropOptions(selectbox)
	{
		var i;
		for(i=selectbox.options.length-1;i>=0;i--)
		{
			selectbox.remove(i);
		}
	}