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
(function(window, $, URITemplate) {
    "use strict";

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.inbox.openlink",
        handler: function(name, el, config, collection, selections) {
            var dataAttributeName = config.data.linkAttributeName;
            if (dataAttributeName) {
                var url = $(selections).data(dataAttributeName);

                if (config.data.target) {
                    window.open(url, config.data.target);
                } else {
                    window.location = url;
                }
            }
        }
    });
})(window, Granite.$, Granite.URITemplate);
