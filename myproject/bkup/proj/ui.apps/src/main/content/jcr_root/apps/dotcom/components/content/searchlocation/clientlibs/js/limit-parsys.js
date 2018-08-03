// for touchui design mode
(function(){
    var pathName = window.location.pathname,
        EAEM_COMPONENT_LIMIT = "eaemComponentLimit";
 
    if( !pathName.endsWith("dialogwrapper.html") ){
        return;
    }
}());
 
// for touchui edit mode
(function ($document, gAuthor) {
	
    var pathName = window.location.pathname;

    if( pathName.endsWith("dialogwrapper.html") ){
        return;
    }
 
    var EAEM_COMPONENT_LIMIT = "eaemComponentLimit";
 
    $(extendComponentDrop);
 
    function extendComponentDrop(){
        var dropController = gAuthor.ui.dropController,
            compDragDrop;

		if (dropController !== undefined) {
			compDragDrop = dropController.get(gAuthor.Component.prototype.getTypeName());
 		
	        //handle drop action
	        if (compDragDrop !== undefined) {
				compDragDrop.handleDrop = function(dropFn){
		            return function (event) {
		                if(showError(event.currentDropTarget.targetEditable)){
		                    return;
		                }
		 
		                return dropFn.call(this, event);
		            };
		        }(compDragDrop.handleDrop);
			}
	 
	        //handle insert action
	        gAuthor.edit.actions.openInsertDialog = function(openDlgFn){
	            return function (editable) {
	                if(showError(editable)){
	                    return;
	                }
	 
	                return openDlgFn.call(this, editable);
	            }
	        }(gAuthor.edit.actions.openInsertDialog);
	
	        //handle paste action
	        var insertAction = gAuthor.edit.Toolbar.defaultActions["INSERT"];
	
	        insertAction.handler = function(insertHandlerFn){
	            return function(editableBefore, param, target){
	                if(showError(editableBefore)){
	                    return;
	                }
	 
	                return insertHandlerFn.call(this, editableBefore, param, target)
	            }
	        }(insertAction.handler);
	
	        //handle copymove action
	        var copymoveAction = gAuthor.edit.Toolbar.defaultActions["PASTE"];
	
	        copymoveAction.handler = function(insertHandlerFn){
	            return function(editableBefore, param, target){
	                if(showError(editableBefore)){
	                    return;
	                }
	 
	                return insertHandlerFn.call(this, editableBefore, param, target)
	            }
	        }(copymoveAction.handler);
	 
	        function showError(editable){
	            var limit = isWithinLimit(editable);
	 
	            if(!limit.isWithin){
	                showErrorAlert("Limit exceeded, allowed - " + limit.currentLimit);
	                return true;
	            }
	 
	            return false;
	        }
		}
    } 
 
    function getChildEditables(parsys){
        var editables = gAuthor.edit.findEditables(),
            children = [], parent;
 
        _.each(editables, function(editable){
            parent = editable.getParent();
 
            if(parent && (parent.path === parsys.path)){
                children.push(editable);
            }
        });
 
        return children;
    }
 
    function showErrorAlert(message, title){
        var fui = $(window).adaptTo("foundation-ui"),
            options = [{
                text: "OK",
                warning: true
            }];
 
        message = message || "Unknown Error";
        title = title || "Error";
 
        fui.prompt(title, message, "error", options);
    }
	
	function getParsysPath(editable){
	    var parsys = editable.getParent(),
	        designSrc = parsys.config.designDialogSrc,
	        result = {}, param;
	    designSrc = designSrc.substring(designSrc.indexOf("?") + 1);
	
	    designSrc.split(/&/).forEach( function(it) {
	        if (_.isEmpty(it)) {
	            return;
	        }
	        param = it.split("=");
	        result[param[0]] = param[1];
	    });
	
	    return decodeURIComponent(result["policyContentPath"]);
	}

    function isWithinLimit(editable){
		var path = getParsysPath(editable),
		children = getChildEditables(editable.getParent()),
				isWithin = true, currentLimit = "";
		$.ajax( { url: path + ".2.json", async: false } ).done(function(data){
			var numberOfPromosAuthoredInProductsTab = parseInt($("iframe#ContentFrame").contents().find('.search_result_filters_products').children("div").length);
		    var numberOfPromosAuthoredInFAQsTab = parseInt($("iframe#ContentFrame").contents().find('.search_result_filters_faqs').children("div").length);
		    var numberOfPromosAuthoredInAllResultsTab = parseInt($("iframe#ContentFrame").contents().find('.search_result_filters_all_results').children("div").length);
	
			numberOfPromosInProductsTab = parseInt($("iframe#ContentFrame").contents().find('.number-of-promo-components-in-products').val());
			numberOfPromosInFAQsTab = parseInt($("iframe#ContentFrame").contents().find('.number-of-promo-components-in-faqs').val());		
			numberOfPromosInAllResultsTab = parseInt($("iframe#ContentFrame").contents().find('.number-of-promo-components-in-all-results').val());

			if(path.indexOf("faq_promo_par")<0 && path.indexOf("product_promo_par")<0 && path.indexOf("all_results_promo_par")<0){
		        return;
		    }

			if(path.indexOf("faq_promo_par")>=0){
				isWithin = numberOfPromosAuthoredInFAQsTab <= numberOfPromosInFAQsTab;
				currentLimit = numberOfPromosInFAQsTab;
			}
			if(path.indexOf("product_promo_par")>=0){
			    isWithin = numberOfPromosAuthoredInProductsTab <= numberOfPromosInProductsTab;
			    currentLimit = numberOfPromosInProductsTab;
			}
			if(path.indexOf("all_results_promo_par")>=0){
				isWithin = numberOfPromosAuthoredInAllResultsTab <= numberOfPromosInAllResultsTab;
				currentLimit = numberOfPromosInAllResultsTab;
			}
		});
 
        return {
            isWithin: isWithin,
            currentLimit: currentLimit
        };
    }

})($(document), Granite.author);