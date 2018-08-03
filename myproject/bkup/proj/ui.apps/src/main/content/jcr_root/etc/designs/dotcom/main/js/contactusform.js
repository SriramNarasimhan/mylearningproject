$(document)
		.ready(
				function() {
                    $.getJSON("/etc/acs-commons/lists/usstates/_jcr_content.list.json", function(json){
                            $('#contact_state').empty();
                            $('#contact_state').append($('<option>').text("Select").attr('value',"Select"));
                        	$('#contact_state').parent().find('span').html('Select');
                            $.each(json, function(i, obj){
                                    $('#contact_state').append($('<option>').text(obj.text).attr('value', obj.value));
                            });
                    });
			        
			        $('.contactUsForm_FieldDiv select').uniform({
			            selectClass: 'sun-select-container',
			            selectAutoWidth: false
			        }).each(function() {
			            $(this).siblings("span").attr("aria-hidden", true);
			        });
			        
					$(".contactUs_Form div[data-mandatory='true']").each(function(){
                        if($(this).find('.contactUsForm_Label').hasClass('contactUsForm_MultipleLabel')) {
                            $(this).find('.contactUsForm_Label.contactUsForm_MultipleLabel span').append(" <span>*</span>");
                        }
                        else {
                        	$(this).find('label').append(" <span>*</span>");
                        }
                    });

                    $(".contactUs_Form div[data-mandatory]").each(function(){
                        if($(this).find('label').html()=="") {
                            $(this).hide();
                        }
                    });


                    $('.contactUsForm_FieldMultipleDiv').each(function(){
						if($(!this).parents('ul').hasClass('suntrust-faqs-list')) { 
							var fieldMultipleDivHide = 0;
							$(this).find('div[data-mandatory]').each(function(){
								if(!$(this).is(':visible')) {
									fieldMultipleDivHide++;
								}
							});
							if(fieldMultipleDivHide==2) {
								$(this).hide();
							}
						}
                    });

                    /* City without number start */
                    $("#contact_city").keypress(function(event){
                        if($(this).parent().parent().attr('data-mandatory')=='true') {
                            var inputValue = event.which;
                            // allow letters and whitespaces only.
                            if(!(inputValue >= 65 && inputValue <= 120) && (inputValue != 32 && inputValue != 0)) { 
                                event.preventDefault(); 
                            }
                        }
                    });
                    /* City without number end */
					$(".cep").keyup(function(){
                        if($(this).val().length==5) {
							$('.phone_us').focus();
                        }
                    });

                    $(".phone_us").keyup(function(){
                        if($(this).val().length==13) {
							$('#contact_loginID').focus();
                        }
                    });
			        $(document).on('click','.contactUs_submit',function(){
			    		var errorCount = 0;
                        $("#selquestion").val($("#contactUs_typeQuestion").val()); 
                        function validateEmail(email) {
                            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                            return re.test(email);
                        }
                        $(".contactUs_Form div[data-mandatory='true']").each(function(){
                            if($(this).find('input').val()=="") {
                                $(this).find('input').addClass('error');
                                $(this).find('label').addClass('error');
                                errorCount++;
                            }
                            else if($(this).find('input').val()!="" && $(this).find('input').val()!=undefined) {
                                $(this).find('input').removeClass('error');
                                $(this).find('label').removeClass('error');
                            }
                            if($(this).find('textarea').val()=="") {
                                $(this).find('textarea').addClass('error');
                                $(this).find('label').addClass('error');
                                errorCount++;
                            }
                            else if($(this).find('textarea').val()!="" && $(this).find('textarea').val()!=undefined) {
                                $(this).find('textarea').removeClass('error');
                                $(this).find('label').removeClass('error');
                            }
							if($(this).find('select').val()=="Select") {
                                $(this).find('select').parent().addClass('error');
                                $(this).find('label').addClass('error');
                                errorCount++;
                            }
                            else if($(this).find('select').val()!="Select" && $(this).find('select').val()!=undefined) {
                                $(this).find('select').parent().removeClass('error');
                                $(this).find('label').removeClass('error');
                            }
                        });
                        if($(".contact_email").parents('div[data-mandatory]').is(':visible') && $(".contact_email").parent().parent().attr('data-mandatory')=='true') {
                            var email = $(".contact_email").val();
                            if(validateEmail(email)) {
                                $(".contact_email").removeClass('error');
                                $(".contact_email").parents('div[data-mandatory]').find('label').removeClass('error');
                            }
                            else {
                                $(".contact_email").addClass('error');
                                $(".contact_email").parents('div[data-mandatory]').find('label').addClass('error');
                                errorCount++;
                            }
                        }
                        if($(".cep").val()!="" && $(".cep").parent().parent().attr('data-mandatory')=='true') {
                            if($(".cep").val().length == 5) {
                                $(".cep").removeClass('error');
                                $(".cep").parents('div[data-mandatory]').find('label').removeClass('error');
                            }
                            else {
                                $(".cep").addClass('error');
                                $(".cep").parents('div[data-mandatory]').find('label').addClass('error');
                                errorCount++;
                            }
                        }
                        if($(".phone_us").val()!="" && $(".phone_us").parents('div[data-mandatory]').is(':visible')) {
                            if($(".phone_us").val().length == 13) {
                                $(".phone_us").removeClass('error');
                                $(".phone_us").parents('div[data-mandatory]').find('label').removeClass('error');
                            }
                            else {
                                $(".phone_us").addClass('error');
                                $(".phone_us").parents('div[data-mandatory]').find('label').addClass('error');
                                errorCount++;
                            }
                        }
                        if(errorCount!=0) {
			    			$(".contactUs_errorMessage").html($("#errormsg").val());
			    		}
			    		else {
			    			$(".contactUs_errorMessage").html("");
                            //$("#contactusform").submit();
							$.ajax({
                                 type: "POST",
                                 url: '/dotcom/studentlending',
                                 data: $('#contactusform').serialize(),
                                 cache: false,
                                 success: function(data) {
                                     if(data.emailstatus =="failed")
                                     {
                                         $('.contactUsForm_div ').hide();
                                         $('.contactUsForm_MessageInfo.success').hide();
                                         $('.contactUsForm_MessageInfo.failure').show();
                                         if($('#contactusform').parents('ul').hasClass('suntrust-faqs-list')) {
                                             var accordTop = $('#contactusform').parents('ul.suntrust-faqs-list').parent('li').offset().top - $('header').innerHeight();
                                             $("body, html").animate({ scrollTop: accordTop }, 1000);
                                         }
                                     }
                                     else{
                                         $('.contactUsForm_div').hide();
                                         $('.contactUsForm_MessageInfo.success').show();
                                         $('.contactUsForm_MessageInfo.failure').hide();
                                         if($('#contactusform').parents('ul').hasClass('suntrust-faqs-list')) {
                                             var accordTop = $('#contactusform').parents('ul.suntrust-faqs-list').parent('li').offset().top - $('header').innerHeight();
                                             $("body, html").animate({ scrollTop: accordTop }, 1000);
                                         }
                                     }
                                 }
				            });
							$('.contactUs_Form input,.contactUs_Form textarea,.contactUs_Form select').val('');
			    		}
			    	});
                });
			        
$(function() {
	$('.cep').mask('00000');
	$('.phone_us').mask('(000)000-0000');
});