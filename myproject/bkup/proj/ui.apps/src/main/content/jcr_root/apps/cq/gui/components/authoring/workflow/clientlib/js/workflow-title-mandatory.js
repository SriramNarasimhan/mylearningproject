(function(document, $) {
    "use strict";
	$(document).on("foundation-contentloaded", function () {
		$('#workflow-model-select-field').on('change', function (e) {
			setWorkflowTitleRequired();
		});	
		$('#workflow-model-select-field').on('click', function (e) {
			setWorkflowTitleRequired();
		});	
		$('#workflow-title-text-field').change(function() {		
			setWorkflowTitleRequired();
		});
		$('#workflow-title-text-field').hover(function() {		
			setWorkflowTitleRequired();
		});
		$('#workflow-title-text-field').mouseleave(function() {		
			setWorkflowTitleRequired();
		});
		$('#workflow-title-text-field').mouseenter(function() {		
			setWorkflowTitleRequired();
		});
		$('#workflow-title-text-field').click(function() {		
			setWorkflowTitleRequired();
		});
		$('#workflow-title-text-field').blur(function() {		
			setWorkflowTitleRequired();
		});
		function setWorkflowTitleRequired() {
			if($('#workflow-title-text-field').val() =='' || $('#workflow-model-select-field').val() == '' ){
				$('#start-workflow-button-field').prop('disabled', true);			
			}
			if($('#workflow-title-text-field').val() !='' && $('#workflow-model-select-field').val() != '' ){
				$('#start-workflow-button-field').prop('disabled', false);
			}
		}
	});  
})(document,Granite.$);

