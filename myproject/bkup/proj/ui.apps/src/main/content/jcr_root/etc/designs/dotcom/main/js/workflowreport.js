$(document).ready(function() {
    
	function getParameterByName(name, url) {
	    if (!url) url = window.location.href;
	    name = name.replace(/[\[\]]/g, "\\$&");
	    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
	        results = regex.exec(url);
	    if (!results) return null;
	    if (!results[2]) return '';
	    return decodeURIComponent(results[2].replace(/\+/g, " "));
	}
	  $('#startDatetimepicker').datetimepicker({format: 'MM/DD/YYYY'});
	  $('#endDatetimepicker').datetimepicker({
	         useCurrent: false,
	         format: 'MM/DD/YYYY'
	  });
	  $("#startDatetimepicker").on("dp.change", function (e) {
	         $('#endDatetimepicker').data("DateTimePicker").minDate(e.date);
	  });
	  $("#endDatetimepicker").on("dp.change", function (e) {
	         $('#startDatetimepicker').data("DateTimePicker").maxDate(e.date);
	  });
	  
	  $(".workFlowData_Form div[data-mandatory='true']").each(function(){
	         $(this).find('label').append(" <span>*</span>");
	  });
	  
	  //$(document).on('click','.workFlowDataForm_submit',function(){
	  $(".workFlowDataForm_submit").click(function(e){
	         e.preventDefault();
	         var wfErrorCount = 0;
	         $(".workFlowData_Form .form-group").each(function(){
	               if($(this).find('.input-group input').val()=="") {
	                      $(this).find('input').addClass('error');
	                      $(this).find('label').addClass('error');
	                      wfErrorCount++;
	               }
	               else if($(this).find('.input-group input').val()!="" && $(this).find('input').val()!=undefined) {
	                      $(this).find('input').removeClass('error');
	                      $(this).find('label').removeClass('error');
	               }
	         });
	         
	         if(wfErrorCount!=0) {
	               $(".workFlowData_errorMessage").show();
	         }
	         else {
               var dates = {};                                        
               $(".workFlowData_Form input.form-control").each( function( index, element ){                                                       
                      dates[$(this).attr("name")] = $(this).val();                         
               });

			   console.log("getParameterByName('startdate')"+getParameterByName("startdate"));
			   console.log("getParameterByName('enddate')"+getParameterByName("enddate"));
			   dates['startdate'] = getParameterByName("startdate");
			   dates['enddate'] = getParameterByName("enddate");
			
			   var param = JSON.stringify(dates); 
			   console.log("params:"+param);
               $.ajax({
                  type: 'GET',      
                  url: '/dotcom/workflowreport',
                  data: {"data" : param, "processId" : "111"}, 
                  success: function(response) {  
                     if(response.indexOf("Success") > -1) {                                                          
                            $('.workFlowData_Form').hide();
                            $('.workFlowData_Form').css('display','none');
                            var reportPath = response.split(/\|/)[1];
                            $('#successMessage').show();
                            $('#successMessage').html("<p>Report saved in DAM location. Please look in this path - <a href='/assetdetails.html"+reportPath+"'>Open Workflow Report</a></p>");
                     } else {
                            $('#successMessage').show();
                            $('#successMessage').text("");
                            $('#successMessage').text("There was some problem with report generation.");
                     }
                     move(widthVal);
                  }
               });
               
    	        function move(widthVal) {
    	            var elem = document.getElementById("myBar"); 
    	            var width = 1;
    	            var id = setInterval(frame, 10);
    	            function frame() {
    	                if (width >= 100) {
    	                    clearInterval(id);
    	                } else {
    	                    width = widthVal; 
    	                    elem.style.width = width + '%'; 
    	                }
    	            }
    	        }
            	        
			    $(".workFlowData_errorMessage").hide();
			    $('.workFlowData_Form input[type=text]').val('');
             }
                          
      });
          
});
