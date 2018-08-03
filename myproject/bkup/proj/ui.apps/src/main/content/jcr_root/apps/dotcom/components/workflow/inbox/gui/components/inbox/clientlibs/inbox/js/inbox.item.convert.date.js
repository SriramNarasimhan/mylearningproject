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
(function(Granite, $, undefined) {
    $(document).on("foundation-contentloaded", function(evt) {
        $(".inbox-item-raw-date").each(function(index, element) {
            var dateAsString = $(this).data("raw-date");

            if (dateAsString) {
                $(this).removeClass("inbox-item-raw-date");

                var dateAsDate = Date.parse(dateAsString);

                // check if we're overdue
                var dueMoment = moment(dateAsDate);
                var diff =  dueMoment.diff(moment());

                if ($(this).hasClass("inbox-item-mark-overdue")) {
                    if (diff < 0) {
                        //task is overdue!
                        $(this).attr("color", "red");
                        $(this).append(Granite.I18n.get("Overdue"));
                    } else if (diff < 14400000) { // less than 24 hours
                        //task is due today!
                        $(this).attr("color", "orange");
                        $(this).append(Granite.I18n.get("Today"));
                    } else {
                        $(this).attr("quiet", "");
                        var dueDateMoment = moment(dateAsDate);
                        $(this).append(CQ.Inbox.UI.Utils.formatRelativeDate(dueDateMoment.toDate()));
                    }
                } else {
                    var dueDateMoment = moment(dateAsDate);
                    $(this).append(CQ.Inbox.UI.Utils.formatRelativeDate(dueDateMoment.toDate()));
                }
            }
        });

    });
})(Granite, Granite.$);