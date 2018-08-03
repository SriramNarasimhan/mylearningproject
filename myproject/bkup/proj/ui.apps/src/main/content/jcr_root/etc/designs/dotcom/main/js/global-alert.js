$(document).ready(function(){
	/******** Global Alert Start ****/
	if($('.suntrust-global-alert').is(":visible")){						
		var alert_config;							
		alert_config=$("#globalalertconfig").val();	
		if($("#modecheck").val()!='edit'){
			$.ajax({
				url : alert_config,
				dataType :'text',
				//cache : false, 
				success : function(data) {											
					var hhtml = $(data).find('#globalalert').html();
					$('.suntrust-global-alert').html(hhtml);
					displayGlobalAlert();	
				}
			})
		}
	}
	/******** Global Alert End ****/

	// Emergency messages
    function displayGlobalAlert(){
		var $sunPageGlobal = $(".suntrust-global-alert");
		var $notificationMessage = $('div[data-suntrust-class="notification-message"]'), notificationClass = 'notification-message notification-in-header';
		if ($notificationMessage.length > 0) {
			var $closeTrigger = $notificationMessage
					.find('button[data-suntrust-class="close-notification-message"]'), alertsCookie = getCookie($closeTrigger
					.attr('data-name'));
			if (alertsCookie != $closeTrigger.attr('data-val')) {
				$sunPageGlobal.addClass(notificationClass);
			}
			$closeTrigger.click(function() {
				$sunPageGlobal.removeClass(notificationClass);
				var cookieConfig = {
					expire : 30, // 30days,
					name : $(this).attr('data-name'),
					value : $(this).attr('data-val'),
					secure : window.location.protocol == "https:" ? true
							: false,
					domain : window.location.host,
					path : "/"
				};
				setCookie(cookieConfig.name,
						cookieConfig.value,
						cookieConfig.expire,
						cookieConfig.path,
						cookieConfig.domain,
						cookieConfig.secure);
			});
		}
    }
});