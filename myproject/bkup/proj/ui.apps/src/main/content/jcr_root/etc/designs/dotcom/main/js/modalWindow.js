// Modal Window jQuery Plugin
(function ($) {

    var methods = {
        init: function(options) {
            var $modalWindowContainer = $(this),
                $modalWindow = $modalWindowContainer.find('[data-modal-window]'),
                $closeButton = $modalWindowContainer.find('[data-suntrust-class="close-modal"]'),
                $overlay = $('[data-suntrust-class="suntrust-overlay"]'),
                $clickSafety = $('[data-click-safety]'),
                closeModal = _.bind(
                    function() {
                        methods.close.apply(this);
                    }, this);

            // Register close modal events
            $closeButton.click(closeModal);
            $overlay.click(closeModal);

            $modalWindowContainer.addClass('active');

            // Center the modal
            var centeredHeight;
            function centerVertically(){
                centeredHeight = $modalWindow.outerHeight() / -2;
                $modalWindow.css({
                    'margin-top': centeredHeight
                });
            }

            centerVertically();
            $(window).resize(_.throttle(centerVertically, 250));

            $clickSafety.click(function(e){
                e.stopPropagation();
            });

            var $suntrustCompareTitleRow = $('.suntrust-compare-title-row');

            if ($suntrustCompareTitleRow.length > 0) {
                $suntrustCompareTitleRow.each(function(){
                    var $suntrustCompareTitle = $(this).find('.suntrust-compare-title'),
                        $suntrustCompareTitleHeight = $suntrustCompareTitle.innerHeight() - 1; // -1 for border

                        $(this).height($suntrustCompareTitleHeight);
                });
            }

            return this;
        },
        close: function(options) {

            var $modalWindowContainer = $(this),
                $modalWindow = $modalWindowContainer.find('[data-modal-window]'),
                $closeButton = $modalWindowContainer.find('[data-suntrust-class="close-modal"]');
            $('[data-suntrust-class="suntrust-modal-inner"]').empty();
            $closeButton.off();
            $modalWindowContainer.removeClass('active');

            return this;
        }
    };

    $.fn.modalWindow = function(method) {
        // Method calling logic
                    //alert();
        if (methods[method]) {
            return methods[ method ].apply(this, Array.prototype.slice.call(arguments, 1));
        }
        else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        }
        else {
            $.error('Method ' + method + ' does not exist on jQuery.suntrust.modalWindow');
        }

    };

})(jQuery); 