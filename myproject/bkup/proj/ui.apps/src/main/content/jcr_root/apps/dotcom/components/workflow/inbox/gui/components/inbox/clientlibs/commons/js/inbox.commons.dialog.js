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

(function($, Granite, undefined) {
    "use strict";

    /**
     * Maps dialog paths to their deferred objects.
     *
     * @private
     */
    var deferreds = {};

    /**
     * Loads a dialog and appends it to the DOM. Returns a promise that is resolved when the dialog is available in the DOM. Calling this function multiple times with the same dialog path doesn't result in multiple loads, but returns promises that are bound to the same load event.
     *
     * @param {string} dialogPath - path to the dialog
     * @returns {Object} a jQuery promise bound to the load event of the specified dialog
     */
    window.CQ.Inbox.UI.commons.loadDialog = function (dialogPath) {
        // get existing deferred, if present
        var deferred = deferreds[dialogPath];
        if (!deferred) {
            // create and store a new deferred object
            var deferred = $.Deferred();
            deferreds[dialogPath] = deferred;

            // load the dialog
            var url = Granite.HTTP.externalize(dialogPath + ".html?ch_ck=" + Date.now());
            $.get(url).done(function (dialogHtml) {
                // append the dialog
                var $dialog = $(Granite.UI.Foundation.Utils.processHtml(dialogHtml));
                $(document.body).append($dialog);
                // trigger 'cui-contentloaded' to initialized CoralUI 2 components
                $dialog.trigger('cui-contentloaded');

                // defer the resolve to let coral do its thing.
                setTimeout(function() {
                    // signal load success
                    deferred.resolve();
                }, 10);
            }).fail(function() {
                // signal load failure
                deferred.reject(Granite.I18n.get("Unable to load " + dialogPath));
            });
        }

        // return promise
        return deferred.promise();
    };

})(Granite.$, Granite);
