/*
 ADOBE CONFIDENTIAL

 Copyright 2016 Adobe Systems Incorporated
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Adobe Systems Incorporated and its suppliers,
 if any.  The intellectual and technical concepts contained
 herein are proprietary to Adobe Systems Incorporated and its
 suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Adobe Systems Incorporated.
 */
(function(window, Granite, $, URITemplate) {
    "use strict";

    var stepbackWorkitemButton = ".inbox-details-workitem-action--stepback";
    var workitemStepBackNS = ".cq-inbox-workitem-stepback";

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.inbox.action.stepbackWorkitem",
        handler: function(name, el, config, collection, selections) {
            var itemId = $(selections).data("foundationCollectionItemId");
            CQ.Inbox.UI.commons.stepBackWorkitem(itemId);
        }
    });

    $(document).off("click" + workitemStepBackNS, stepbackWorkitemButton)
        .on("click" + workitemStepBackNS, stepbackWorkitemButton, function() {
            var successHref = $(this).data("href.success");
            var workitemId = $(".task-path").data("taskPath");
            if (workitemId) {
                CQ.Inbox.UI.commons.stepBackWorkitem(workitemId, successHref);
            }
        });

})(window, Granite, Granite.$, Granite.URITemplate);
