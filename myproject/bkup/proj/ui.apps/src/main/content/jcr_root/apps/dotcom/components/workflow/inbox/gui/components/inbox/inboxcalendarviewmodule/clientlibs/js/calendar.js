/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2016 Adobe Systems Incorporated
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
 */
(function(window, document, Granite, $, URITemplate) {
    "use strict";

    var registry = $(window).adaptTo("foundation-registry");

    function handleMode(collection, config) {
        var modeChangeHandler = function(e, mode, group) {
            if (e._foundationLayoutTable) {
                return;
            }
            if (mode !== "default") {
                return;
            }
            if (collection[0].dataset.foundationModeGroup !== group) {
                return;
            }

            collection.data("foundation-layout-table.internal.bulkSelection", true);

            var prevSelections = collection.find(".foundation-selections-item").each(function() {
                this.selected = false;
            });

            collection.removeData("foundation-layout-table.internal.bulkSelection");
        };

        $(document).on("foundation-mode-change", modeChangeHandler);

        return function() {
            $(document).off("foundation-mode-change", modeChangeHandler);
        };
    }

    registry.register("foundation.layouts", {
        name: "foundation-layout-calendar",
        doLayout: function(el, config) {
            var collection = $(el);

            // foundation-layout-calendar is exclusive to manage the layout of foundation-collection only
            if (!collection.hasClass("foundation-collection")) {
                return;
            }

            if (collection.data("foundation-layout-calendar.internal.init")) {
                return;
            }

            var stack = [];

            stack.push((function() {
                collection.data("foundation-layout-calendar.internal.stack", stack);

                return function() {
                    collection.removeData("foundation-layout-calendar.internal.stack");
                };
            })());

            stack.push((function() {
                collection.data("foundation-layout-calendar.internal.init", true);

                return function() {
                    collection.removeData("foundation-layout-calendar.internal.init");
                };
            })());

            stack.push(handleMode(collection, config));

            requestAnimationFrame(function() {
                // trigger collection event after Coral upgrade
                collection.trigger("foundation-selections-change");
            });
        },
        clean: function(el, config) {
            var collection = $(el);

            var stack = collection.data("foundation-layout-calendar.internal.stack");
            if (stack) {
                Granite.UI.Foundation.Utils.everyReverse(stack, function(v) {
                    if (v) {
                        v();
                    }
                    return true;
                });
            }
        }
    });
})(window, document, Granite, Granite.$, Granite.URITemplate);
