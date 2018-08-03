/* * ADOBE CONFIDENTIAL
 *
 * Copyright 2014 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 *
 */
(function(document, $) {
    "use strict";

    var selectionLength;
    var selector = ".cq-common-admin-timeline-toolbar-actions-workflow";
    var workflowDialog = ".cq-common-admin-timeline-toolbar-actions-workflow-dialog";
    var ns = "cq-common-admin-timeline-toolbar-actions-workflow";
    var eventName = "cq-common-admin-timeline-change";
    var timelineSelector = ".cq-common-admin-timeline";
    var timelineNs = "cq-common-admin-timeline";

    var MODAL_HTML =
        "<div class=\"coral-Modal-header\">" + "<h2 class=\"coral-Modal-title coral-Heading coral-Heading--2\"></h2>" + "<i class=\"coral-Modal-typeIcon coral-Icon coral-Icon--sizeS\"></i>" + "<button type=\"button\" " + "class=\"coral-MinimalButton coral-Modal-closeButton\" " + "data-dismiss=\"modal\">" + "<i class=\"coral-Icon coral-Icon--sizeXS coral-Icon--close " + "coral-MinimalButton-icon\"></i>" + "</button>" + "</div>" + "<div class=\"coral-Modal-body legacy-margins\"></div>" + "<div class=\"coral-Modal-footer\"></div>";

    // workflow layer: listener of the select workflow field: handle "mandatory"
        $(document).on("change", '#workflow-title-dam-timeline-text-field', function (e){	
            setWorkflowTitleRequired();
        });
        $(document).on("hover", '#workflow-title-dam-timeline-text-field', function (e){	
            setWorkflowTitleRequired();
        });
        $(document).on("mouseleave", '#workflow-title-dam-timeline-text-field', function (e){	
            setWorkflowTitleRequired();
        });
        $(document).on("mouseenter", '#workflow-title-dam-timeline-text-field', function (e){	
            setWorkflowTitleRequired();
        });
        $(document).on("click", '#workflow-title-dam-timeline-text-field', function (e){	
            setWorkflowTitleRequired();
        });
        $(document).on("blur", '#workflow-title-dam-timeline-text-field', function (e){	
            setWorkflowTitleRequired();
        });
        $(document).off("change", '#workflow-model-select-field-dam-timeline').on("change", '#workflow-model-select-field-dam-timeline', function (e) {
          setWorkflowTitleRequired();
        });
    	function setWorkflowTitleRequired() {
			if($('#workflow-title-dam-timeline-text-field').val() =='' || $('#workflow-model-select-field-dam-timeline').val() == '' ){
				$('#start-workflow-dam-timeline-button-field').prop('disabled', true);			
			}
			if($('#workflow-title-dam-timeline-text-field').val() !='' && $('#workflow-model-select-field-dam-timeline').val() != '' ){
				$('#start-workflow-dam-timeline-button-field').prop('disabled', false);
			}
		}  

    // workflow layer: listener of form submit event
    $(document).off("submit", selector + "-form").on("submit", selector + "-form", function(e) {
        e.preventDefault();
        submitWorkflowForm();
    });

    // workflow layer: listener of the start button
    $(document).off("click." + ns, selector + "-ok").on("click." + ns, selector + "-ok", function (e) {
        var button = $(selector + "-ok");
        var form = button.closest("form");
        submitWorkflowForm(button, form);
    });

     $(document).on("click." + ns, selector + "-action-ok", function (e) {
        var button = $(selector + "-action-ok");
        var form = button.parent().siblings().find("form");
        submitWorkflowForm(button, form);
    });

    function submitWorkflowForm(button, form) {

        //hide the workflow dialog if action is initiated from lister page.
        if($(workflowDialog)) {
            $(workflowDialog).hide();
        }

        if (button.hasClass("disabled")) {
            // mandatory select workflow field empty
            return;
        }

        var paths = [];
        if ($(timelineSelector).data("paths")) {
            paths = $(timelineSelector).data("paths");
        }

        //when workflow is started from create button in action bar on assets page.
        if(paths.length === 0) {
            $(".cq-damadmin-admin-childpages.foundation-collection .foundation-collection-item.is-selected").each(function() {
                paths.push($(this).data("foundation-collection-item-id"));
            });
        }

        //when workflow is started from create button in action bar on collections page.
        if(paths.length === 0) {
            $(".cq-damadmin-admin-childcollections.foundation-collection .foundation-collection-item.is-selected").each(function() {
                paths.push($(this).data("foundation-collection-item-id"));
            });
        }

        var data = form.serializeArray();

        selectionLength = paths.length;
        var count = 0;
        var maxLimit = $("#aem-assets-rail-timeline").data("multipleworkflowlimit");
        var workflowName = form.find("coral-selectlist-item.is-selected").text();

        for (var i = 0; i < paths.length; i++) {
            if (!paths[i]) {
                continue;
            }
            var type = $(".foundation-collection-assets-meta[data-foundation-collection-meta-path=\"" + paths[i] + "\"]").data('foundation-collection-meta-type');

            // when timeline is opened through assetdetails or annotation page.
            if(!type) {
                type = $(".foundation-content-path").data("foundation-content-type");
            }
            if (type == "collection" || type == "directory") {
                var name, url;
                if (type == "collection") {
                    name = "collectionPath";
                    url = "/mnt/overlay/dam/gui/content/collections/collectionassets.html?collectionPath=" + paths[i];
                    //for collection there will be single path only so collection name used to track completion of workflows
                    data.push({
                        name: name,
                        value: paths[i]
                    });
                    if(!maxLimit) {
                        maxLimit = $("#aem-asset-collections-rail-timeline").data("multipleworkflowlimit");
                    }
                } else {
                    name = "folderPath";
                    type = "folder";
                    url = "/mnt/overlay/dam/gui/content/assets/folderassets.html?folderPath=" + paths[i];
                }
                url = url+"&_charset_=utf-8";

                $.ajax({
                    url: Granite.HTTP.externalize(url),
                    async: false,
                    type: "GET",
                    success: function(html) {
                        var assets = $(html).text();
                        if (assets.charAt(assets.length - 1) == ";") {
                            assets = assets.substring(0, assets.length - 1)
                        }
                        var assetArr = assets.split(";");
                        if (!(assetArr) || (assetArr && assetArr[0] == "")) {
                            return;
                        }
                        for (var j = 0; j < assetArr.length; j++) {
                            data.push({
                                name: "payload",
                                value: assetArr[j]
                            });
                            count++;
                        }
                    }
                });

            } else if (type == "asset") {
                data.push({
                    name: "payload",
                    value: paths[i]
                });
                count++;
            }
			
        }

        if ($("#workflowLargeAssetsWarning").length === 0) {
            var insertModal = $("<div>", {
                "class": "coral-Modal",
                "id": "workflowLargeAssetsWarning",
                "style": "width:30rem"
            }).hide().html(MODAL_HTML);
            $(document.body).append(insertModal);
        }
        var close = {
            label: Granite.I18n.get("Close"),
            click: function() {
                this.hide();
            }
        };
        var proceed = {
            label: Granite.I18n.get("Proceed"),
            className: 'coral-Button--primary',
            click: function() {

                $.ajax({
                    url: form.attr("action"),
                    type: form.attr("method") || "post",
                    data: data,
                    success: function() {
                        // reset select workflow field but only when workflow is triggered through timeline.
                        if($(selector + "-select").length > 0) {
                            $(selector + "-select")[0].selectedIndex = 0;
                            var firstOption = $(selector + "-select").find("option").first().text();
                            $(selector + "-select-button .coral-Select-button-text").text(firstOption);
                            disable();
                            form.find("input[type=text]").val("");
                        }
                        startWorkflowCallback(true);
                        //show timeline after workflow creation
                        var timelinePanelTrigger = $('.granite-toggleable-control [data-granite-toggleable-control-target*="timeline"]');
                        if (timelinePanelTrigger.length > 0 && type === "asset") {
                            timelinePanelTrigger[0].selected = true;
                        }
                    },
                    error: function() {
                        startWorkflowCallback(false);
                    }
                });
                this.hide();
            }
        };
        var buttons, content;
        if (count == 0) {
            buttons = [close];
            if (type == "folder") {
                content = Granite.I18n.get('You are trying to trigger a workflow on an empty folder.');
            } else if (type == "collection") {
                content = Granite.I18n.get('You are trying to trigger a workflow on an empty collection.');
            } else {
                content = Granite.I18n.get('You are trying to trigger a workflow on an empty {0}.', type);
            }
        } else if (count <= maxLimit) {
            buttons = [close, proceed];
            content = Granite.I18n.get('You are going to trigger {0} workflow on {1} asset(s). Please confirm to proceed.', [workflowName, count]);
        } else {
            buttons = [close];
            content = Granite.I18n.get('Maximum number of assets that can be selected for multiple workflow launch is : {0}. You have selected {1} assets across your selection(s). Please reduce the selected count accordingly.', [maxLimit, count]);
        }
        var modal = new CUI.Modal({
            element: '#workflowLargeAssetsWarning',
            heading: Granite.I18n.get('Warning'),
            content: content,
            type: "notice",
            buttons: buttons
        });
    }

    function disable() {
        $(selector + "-select-button").addClass("is-invalid");
        $(selector + "-ok").attr("disabled", "disabled");
        $(selector + "-action-ok").attr("disabled", "disabled");
    }

    function startWorkflowCallback(status) {
        // todo: handle errors
        if (selectionLength > 1) {
            if (status) {
                var content = Granite.I18n.get("Workflows started for multiple items");
                getNotificationSlider().notify({
                    content: content,
                    type: "success",
                    closable: true,
                    className: "notification-alert--absolute"
                });
            }
            hideActionButton();
        } else {
            // single selection: refresh events and alerts
            $(timelineSelector).trigger(eventName + "." + timelineNs, {
                refresh: true
            });

        }
    }

})(document, Granite.$);
