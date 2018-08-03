$(function() {
	var n = $("#skip-nav");
	n.find(".skip-nav-link").focus(function() {
		$("#skip-nav").addClass("sun-active");
	}), n.find(".skip-nav-link").blur(function() {
		$("#skip-nav").removeClass("sun-active");
    });
	$('#adv_search').on('focus keydown', function(event){
			if (event.keyCode == 13) {
				$('.sun-header-search-form').toggleClass("sun-active");
				$('.sun-toggle-overlay').toggleClass("sun-active");
				setTimeout(function(){
					$('input#sun-header-search-input').focus();
				},1);
					$('input#sun-header-search-input').blur(function() {
						if($(this).val().length == 0){
							$('.sun-header-search-form').toggleClass("sun-active");
							$('.sun-toggle-overlay').toggleClass("sun-active");
						}
					});
			}
	});
	$('.sun-search-and-mobile-menu-container-toggle').on("click", $(document), function() {
			var t = $(this),
				n = $('.sun-search-and-mobile-menu-container');
			t.toggleClass("sun-active"), n.toggleClass("sun-active")
	});
	$('.sun-header-search-icon').on("click", $(document), function() {
		var n = $('.sun-header-search-form');
		n.toggleClass("sun-active");
	});
	$(".sun-header-search-icon").on("click", $(document), function() {
		var i = $(this),
			t = i.data("sun-toggle-overlay-target"),
			n = $('.sun-toggle-overlay');
		n.toggleClass("sun-active").data("sun-target", t);
	});
	$('.sun-toggle-overlay').on("click", $(document), function() {
		var n = $(this),
			t = $('.sun-header-search-form');
		n.removeClass("sun-active"), t.removeClass("sun-active"), $(".sun-header-search-cancel-button").removeClass("sun-active");
	});
	var n, t;
	$('.sun-header-search-cancel-button').on("click", $(document), function(n) {
		var t = $(this);
		n.preventDefault(), t.removeClass("sun-active"), t.prev("input").val("").focus(), $(".suntrust-autocomplete").empty()
	});
	$('[type="search"]').on({
		keyup: function() {
			var i = $(this),
				t = i.val().length;
			t > 0 ? n.addClass("sun-active") : n.removeClass("sun-active")
		},
		focus: function() {
			var t = $(this);
			n = t.next('.sun-header-search-cancel-button'), t.closest(".suntrust-header-search-container").addClass("search-active")
		},
		blur: function() {
			var n = $(this);
			if ($(this).val().length > 0) return !1;
			n.closest(".suntrust-header-search-container").removeClass("search-active");
		}
	}, $(document));
    document.activeElement.attributes.type && document.activeElement.attributes.type.value === "search" && (t = $(document.activeElement), n = $('.sun-header-search-cancel-button'));
	if($('.sun-search-and-mobile-menu-container').is('.hide-search')){
		$('.sun-search-and-mobile-menu-container-toggle').hide();
	}
});