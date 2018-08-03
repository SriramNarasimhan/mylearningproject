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

    var workitemDetailsNS = ".cq-inbox-workitem-complete";

    var completeWorkitemButton = ".inbox-details-workitem-action--complete";
    var delegateWorkitemButton = ".inbox-details-workitem-action--delegate";
    var stepbackWorkitemButton = ".inbox-details-workitem-action--stepback";

    $(document)
        .off("foundation-contentloaded" + workitemDetailsNS)
        .on("foundation-contentloaded" + workitemDetailsNS, function(e) {
            var form = $("#updateworkitemform");

            var statusField = $("[name='status']", form);
            if (statusField && statusField.length) {
                var statusValue = statusField.val();
                if (statusValue !== "ACTIVE") {
                    $(completeWorkitemButton).attr("disabled", "disabled");
                    $(delegateWorkitemButton).attr("disabled", "disabled");
                    $(stepbackWorkitemButton).attr("disabled", "disabled");

                } else {
                    $(completeWorkitemButton).removeAttr("disabled");
                    $(delegateWorkitemButton).removeAttr("disabled");
                    $(stepbackWorkitemButton).removeAttr("disabled");
                }
            }

            // disable the read-only info elements:
            $(".cq-inbox-info--read-only",form).attr("readonly", "readonly");
    });

})(window, Granite, Granite.$, Granite.URITemplate);