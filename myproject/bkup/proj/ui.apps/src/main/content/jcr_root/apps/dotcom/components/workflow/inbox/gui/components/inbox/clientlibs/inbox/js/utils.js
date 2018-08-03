/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2016 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

(function($, Granite, undefined) {
    "use strict";

    /**
     * @namespace
     */
    CQ.Inbox.UI.Utils = CQ.Inbox.UI.Utils || (function() {

        function formatRelativeDate(date) {
            if (typeof date === "number") {
                date = new Date(date);
            }
            var now = new Date();

            var n = parseInt((now - date) / 1000);
            var inThePast = n > 0;

            n = Math.abs(n);
            var unit = "";
            if (n < 60) {
                unit = Granite.I18n.get("seconds");
            } else if (n < 3600) {
                n = parseInt(n / 60);
                unit = n > 1 ? Granite.I18n.get("minutes") : Granite.I18n.get("minute");
            } else if (n < 86400) {
                n = parseInt(n / 3600);
                unit = n > 1 ? Granite.I18n.get("hours") : Granite.I18n.get("hour");
            } else if (n < 31536000) {
                n = parseInt(n / 86400);
                unit = n > 1 ? Granite.I18n.get("days") : Granite.I18n.get("day");
            } else {
                n = parseInt(n / 31536000);
                unit = n > 1 ? Granite.I18n.get("years") : Granite.I18n.get("year");
            }

            if (inThePast) {
                return Granite.I18n.get("{0} {1} ago", [n, unit], "Relative date to the current date");
            } else {
                return Granite.I18n.get("in {0} {1}", [n, unit], "Future relative date to the current date");
            }
        };

        // almostOverdue if the duedate falls to within a day...
        var almostOverdueDeltaInSeconds = 24*60*60;

        function getRelativeDateInfo(date) {
            if (typeof date === "number") {
                date = new Date(date);
            }
            var now = new Date();

            var n = parseInt((now - date) / 1000);
            var inThePast = n > 0;
            var almostOverdue = false;

            n = Math.abs(n);
            if ( !inThePast && n < almostOverdueDeltaInSeconds ) {
                almostOverdue = true;
            }

            var unit = "";
            if (n < 60) {
                unit = Granite.I18n.get("seconds");
            } else if (n < 3600) {
                n = parseInt(n / 60);
                unit = n > 1 ? Granite.I18n.get("minutes") : Granite.I18n.get("minute");
            } else if (n < 86400) {
                n = parseInt(n / 3600);
                unit = n > 1 ? Granite.I18n.get("hours") : Granite.I18n.get("hour");
            } else if (n < 31536000) {
                n = parseInt(n / 86400);
                unit = n > 1 ? Granite.I18n.get("days") : Granite.I18n.get("day");
            } else {
                n = parseInt(n / 31536000);
                unit = n > 1 ? Granite.I18n.get("years") : Granite.I18n.get("year");
            }

            var status = inThePast ? "overdue" : "not-overdue";
            if (almostOverdue) {
                status = "almost-overdue";
            }

            return {
                units: unit,
                number: n,
                past: status
            };
        }

        /**
         * @scope Granite.UI.Extension.Utils
         */
        return {

            getAdminUrl: function() {
                return removeSuffix(Granite.HTTP.getPath());
            },

            formatRelativeDate: function(date) {
                return formatRelativeDate(date);
            },

            getRelativeDateInfo: function(date) {
                return getRelativeDateInfo(date);
            }

        };
    })();

})(Granite.$, Granite);