
$(document).ready(function() {
		$('#successMessage').hide();
		var max_fields      = 10; //maximum input boxes allowed
		var wrapper         = $(".input_fields_wrap"); //Fields wrapper
		var add_button      = $(".add_field_button"); //Add button ID		
		var x = 1; //initlal text box count
		$(add_button).click(function(e){ //on add input button click
			e.preventDefault();
			if(x < max_fields){ //max input box allowed
				x++; //text box increment
				$(wrapper).append('<div style="display:flex;flex-direction: row;align-items: center;"><input class="iw-base-text-field-data inputtextField" type="text" name="input'+x+'" size="60"/><input type="button" class="remove_field iw-base-actionlist-link suntrust-tertiary-button_CTAs" value="Remove"/></div>'); //add input box
			}
		});		
		$(wrapper).on("click",".remove_field", function(e){ //user click on remove text
			e.preventDefault(); $(this).parent('div').remove(); x--;
		})
		
		$("#submitForm").click(function(e){
			e.preventDefault();
			var example = {};						
			$("div.input_fields_wrap input.inputtextField").each( function( index, element ){								
				example[$(this).attr("name")] = $(this).val();				
			});
			
			/*$.each(example, function(key, value) {
				alert("key::"+key);
				alert("value::"+value);				
			});*/
			
			var param = JSON.stringify(example);						
			$.ajax({
			   type: 'POST',	
			   url: '/dotcom/awscacheclear',
			   data: {"data" : param}, 
			   success: function(response) {	
					if(response == "Success"){
						$('#successMessage').show();
						$('#successMessage').css('display','block');
						$('.remove_field').css('pointer-events','none');										
						$('#awscacheclear input').attr('readonly', 'readonly');
						$('#submitForm').prop('disabled', true);
						$('.add_field_button').prop('disabled', true); 
					}else{
						$('#successMessage').text("");
						$('#successMessage').text("Issue with AWS Cache Clearing. Contact System Amdminstrator.");
					}
			   }
			});
		});		
	});