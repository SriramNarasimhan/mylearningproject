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
    moment.locale(Granite.I18n.getLocale());

    var ns = ".cq-inbox-list";
    var ui = $(window).adaptTo("foundation-ui");

    window.CQ = window.CQ || {};
    window.CQ.Inbox = window.CQ.Inbox || {};
    window.CQ.Inbox.UI = window.CQ.Inbox.UI || {};

    var projectCalendar = ".cq-inbox-calendar";
    var dialogPath = "/mnt/overlay/cq/inbox/content/inbox/dialogs/taskcompletedialog";

    window.CQ.Inbox.UI.init = function() {
        var aeon = new Coral.Aeon();

        initializeCalendar(aeon);
        setCalendarStatuses(aeon);
        setCalendarFields(aeon);
        setCalendarActions(aeon);
        setCalendarGroups(aeon);
        handleCookies();
        hideAeonButtons();
        setupDataReadyEvent(aeon);

        aeon.initialize();
    };

    //Create the calendar object and setup its basic configurations
    function initializeCalendar (aeon) {
        var inboxMode = $.cookie("aeon-cal-inbox-view");
        if(inboxMode == null) {
            inboxMode = "timeline";
        }

        var filterParm = $('#projectFilter').val();

        $("#CalendarWrapper").append(aeon);
        $('#CalendarWrapper .coral-Aeon').addClass( "cq-inbox-calendarview" );

        var startDate = $.cookie("aeon-cal-inbox-start");
        if(startDate == null || !isDate(startDate)) {
            var interimDate = moment(new Date());
            interimDate.subtract(7, "days");  //display 7 days prior to today
            startDate = interimDate.format("YYYY-MM-DD");
        }

        var endDate = $.cookie("aeon-cal-inbox-end");
        if(endDate == null || !isDate(endDate)) {
            interimDate.add(21, "days");
            var endDate = interimDate.format("YYYY-MM-DD");  //display 21 days after today
        }

        aeon.set({
            url: '/libs/cq/inbox/content/inbox/calendarview/calendardatanode.json',
            cardSize: $('#inboxCardSize').val(),
            groupBy: $('#inboxGrouping').val(),
            mode: inboxMode,
            start: startDate,
            end: endDate,
            baseFilters: {
                calItemActive: '',
                projectFilter: filterParm,
                type: 'post',
                metadata: {
                    metadata_Permission_s: 'group:12345'
                }
            },
            serviceTransformer: function(payload) {
                return payload;
            }
        });
    }

    //Setup the various statuses to be supported by the calendar
    //In Progress, Planned, Due Soon and Past Due
    function setCalendarStatuses (aeon){
        aeon.statuses.add(
            'active',
            {
                icon: 'clock',
                color: "#87beff",
                label: Granite.I18n.get("In Progress"),
                dateLabel: Granite.I18n.get("Start Date: ")
            }
        );

        aeon.statuses.add(
            'activePlanned',
            {
                icon: 'clock',
                color: "#a5d273",
                label: Granite.I18n.get("Planned"),
                dateLabel: Granite.I18n.get("Start Date: ")
            }
        );
        aeon.statuses.add(
            'activeWarning',
            {
                icon: 'clock',
                color: "#fad269",
                label: Granite.I18n.get("Due Soon"),
                dateLabel: Granite.I18n.get("Start Date: ")
            }
        );
        aeon.statuses.add(
            'activeLate',
            {
                icon: 'clock',
                color: "#fa7d73",
                label: Granite.I18n.get("Past Due"),
                dateLabel: Granite.I18n.get("Start Date: ")
            }
        );
    }

    //Add fields to the calendar cards
    function setCalendarFields (aeon) {
        aeon.fields.add({}, {
            content: function (entry) {
                if(entry.priority == "Low")
                    return Granite.I18n.get("Low");
                if(entry.priority == "High")
                    return Granite.I18n.get("High");
                return Granite.I18n.get("Medium");
            }
        });

        aeon.fields.add({}, {
            content: function (entry) {
                return entry.assignedto;
            },
            image: function (entry) {
                return entry.UserThumbnailPath;
            }
        });

        aeon.fields.add({}, {
            content: function (entry) {
                if(entry.startingAt != undefined && entry.startingAt != null)
                    return "<b>"+Granite.I18n.get("Start:")+"</b> " + moment(parseInt(entry.startingAt)*1000).format("YYYY-MM-DD");

                return "<b>"+Granite.I18n.get("Start:")+"</b> "+Granite.I18n.get("NA");
            }
        });

        aeon.fields.add({}, {
            content: function (entry) {
                if(entry.endingAt != undefined && entry.endingAt != null)
                    return "<b>"+Granite.I18n.get("Due:")+"</b> " + moment(parseInt(entry.endingAt)*1000).format("YYYY-MM-DD");

                return "<b>"+Granite.I18n.get("Due:")+"</b> "+Granite.I18n.get("NA");
            }
        });
    }

    //setup Aeon quick actions and their handlers
    function setCalendarActions (aeon) {
        aeon.actions.add({completeTask: true},
            {
                name: Granite.I18n.get("Complete Task"),
                icon: 'check',
                fn: function (payload) {
                    var itemId = payload.id;
                    var taskActions = payload.taskActions;
                    if(taskActions != null)
                        taskActions = JSON.parse(taskActions);
                    var submitAction = "/libs/granite/taskmanager/updatetask";
                    CQ.Inbox.UI.commons.loadDialog(dialogPath).done(function () {
                        CQ.Inbox.UI.commons.completeTask(submitAction, itemId, taskActions);
                    }).fail(function (error) {
                        ui.alert(Granite.I18n.get("Error"), error, "error");
                    });
                }
            }
        );

        aeon.actions.add({completeWI: true},
            {
                name: Granite.I18n.get("Complete Work Item"),
                icon: 'check',
                fn: function (payload) {
                    var itemId = payload.id;
                    var taskActions;
                    var submitAction = "/libs/granite/taskmanager/updatetask";
                    CQ.Inbox.UI.commons.loadDialog(dialogPath).done(function () {
                        CQ.Inbox.UI.commons.completeWorkitem(itemId);
                    }).fail(function (error) {
                        ui.alert(Granite.I18n.get("Error"), error, "error");
                    });
                }
            }
        );

        aeon.actions.add({reassign: true},
            {
                name: Granite.I18n.get("Reassign"),
                icon: 'delegate',
                fn: function (payload) {
                    var ns = ".cq-inbox-list";
                    var ui = $(window).adaptTo("foundation-ui");
                    var dialogPath = "/mnt/overlay/cq/inbox/content/inbox/dialogs/taskreassigndialog";
                    var itemId = payload.id;
                    var submitAction = "/libs/granite/taskmanager/updatetask";

                    var projectPath = ""; //$(selections).data("project-id");

                    if (submitAction && itemId) {
                        CQ.Inbox.UI.commons.loadDialog(dialogPath).done(function () {
                            CQ.Inbox.UI.commons.reassignTask(submitAction, itemId, projectPath);
                        }).fail(function (error) {
                            ui.alert(Granite.I18n.get("Error"), error, "error");
                        });
                    }
                }
            }
        );

        aeon.actions.add({delegate: true},
            {
                name: Granite.I18n.get("Delegate"),
                icon: 'delegate',
                fn: function (payload) {
                    var itemId = payload.id;

                    CQ.Inbox.UI.commons.loadDialog(dialogPath).done(function () {
                        CQ.Inbox.UI.commons.delegateWorkitem(itemId);
                    }).fail(function (error) {
                        ui.alert(Granite.I18n.get("Error"), error, "error");
                    });
                }
            }
        );

        aeon.actions.add({details: true},
            {
                name: Granite.I18n.get("Open"),
                icon: 'edit',
                fn: function (payload) {
                    var href = payload.link;
                    window.location=href;
                }
            }
        );

        aeon.actions.add({project: true},
            {
                name: Granite.I18n.get("Open Project"),
                icon: 'project',
                fn: function (payload) {
                    var href = payload.linkProject;
                    window.location=href;
                }
            }
        );

        aeon.actions.add({payload: true},
            {
                name: Granite.I18n.get("View"),
                icon: 'viewOn',
                fn: function (payload) {
                    window.open(payload.payloadLink,"_blank");
                }
            }
        );

        aeon.actions.add({stepBack: true},
            {
                name: Granite.I18n.get("Step Back"),
                icon: 'undo',
                fn: function (payload) {
                    var itemId = payload.id;
                    CQ.Inbox.UI.commons.stepBackWorkitem(itemId);
                }
            }
        );

        aeon.actions.add({createProject: true},
            {
                name: Granite.I18n.get("Create Project"),
                icon: 'projectAdd',
                fn: function (payload) {
                    var taskId = payload.id;
                    var taskIdParameter = "taskid=" + encodeURIComponent(taskId);
                    var refParameter = "ref="+Granite.HTTP.externalize("/aem/inbox");
                    window.location = Granite.HTTP.externalize("/mnt/overlay/cq/core/content/projects/wizard/newproject.html/content/projects?"+taskIdParameter +"&" + refParameter);
                }
            }
        );
    }

    //define groups.  1 group is defined, 'Schedule' which contains
    // statuses In Progress, Planned, Due Soon and Past Due
    function setCalendarGroups (aeon) {
        aeon.groups.add(Granite.I18n.get("Schedule"),
            [
                {
                    label: Granite.I18n.get("Planned"),
                    criteria: {
                        metadata: {
                            platform: 'planned'
                        }
                    }
                },
                {
                    label: Granite.I18n.get("In Progress"),
                    criteria: {
                        metadata: {
                            platform: 'ontime'
                        }
                    }
                },
                {
                    label: Granite.I18n.get("Due Soon"),
                    criteria: {
                        metadata: {
                            platform: 'warning'
                        }
                    }
                },
                {
                    label: Granite.I18n.get("Past Due"),
                    criteria: {
                        metadata: {
                            platform: 'late'
                        }
                    }
                }
            ]
        );

    }

    // remove the specified cookie and replace it with a new value
    function setInboxCookie(cookie, value) {
        $.removeCookie(cookie);
        // set the cookie with path '/' to ensure a unique value when read
        $.cookie(cookie, value, { path: '/'});
    }

    //setup events that persist user selections in the calendar for a given session
    function handleCookies () {
        //handle date filter selection
        $('.coral-Aeon-datePopover-content').on("click", function(e) {
            if(e.target.getAttribute('handle') != 'reset') {
                setInboxCookie("aeon-cal-inbox-start", $('.coral-Aeon-dateRangePicker--start span[handle="daterangedisplay"]').text());
                setInboxCookie("aeon-cal-inbox-end", $('.coral-Aeon-dateRangePicker--end span[handle="daterangedisplay"]').text());
            }
        });

        $('[handle="timeline"]').on("click", function(e) {
            setInboxCookie("aeon-cal-inbox-view", "timeline");
            //because the groupby does not have a unique id we need to match the selected
            //group with the last item in the list which will be scheduled. The group name changes
            //to the selected language so this is why we compare the selected string with the item
            //we know to be the 'Scheduled' group in the drop down.
            if ($('coral-aeon-settings').attr('groupby') ==
                $($('coral-aeon-settings coral-select[handle="group"] coral-select-item')[1]).text()) {
                $('#projectCalFilter').show();
            } else {
                $('#projectCalFilter').hide();
            }
        });

        $('[handle="column"]').on("click", function(e) {
            setInboxCookie("aeon-cal-inbox-view", "column");
            if ($('coral-aeon-settings').attr('groupby') ==
                $($('coral-aeon-settings coral-select[handle="group"] coral-select-item')[1]).text()) {
                $('#projectCalFilter').show();
            } else {
                $('#projectCalFilter').hide();
            }
        });

        $('[handle="list"]').on("click", function(e) {
            setInboxCookie("aeon-cal-inbox-view", "list");
            $('#projectCalFilter').hide();
        });
    }

    //hide Calendar preference buttons that we don't use
    function hideAeonButtons () {
        $($('.coral-Aeon-actions .aeon-PaneButton')[0]).hide();
        $($('.coral-Aeon-actions .aeon-PaneButton')[1]).hide();
        $($('.coral-Aeon-actions .aeon-PaneButton')[2]).hide();
        $($('.coral-Aeon-actions .aeon-PaneButton')[3]).hide();
    }

    //When the client receives data from our dataprovider Aeon triggers an event
    //and returns an object with all the data
    //Currently we use this to setup our filter buttons and add group counts
    function setupDataReadyEvent (aeon) {
        //data returned from server
        aeon.on("aeon:data:loaded", function(e) {

            var countTot = e.detail.payload.meta.count;
            var countActive = e.detail.payload.meta.activeCount;
            var countLate = e.detail.payload.meta.activeLateCount;
            var countPlan = e.detail.payload.meta.activePlannedCount;
            var countWarn = e.detail.payload.meta.activeWarningCount;

            //check to see if button bar exists
            if($("#projectCalFilter").length < 1) {
                $('<coral-buttongroup id="projectCalFilter" selectionmode="multiple" class="calendarStatusFilter" name="button-group">' +
                    '<button is="coral-button" icon="clock" id="aem-inboxcal-all" class="calendar-filter-button">' + Granite.I18n.get("All ({0})", countTot.toString()) + '</button>' +
                    '<button is="coral-button" icon="clock" id="aem-cal-planned" class="calendar-filter-button">' + Granite.I18n.get("Planned ({0})", countPlan.toString()) + '</button>' +
                    '<button is="coral-button" icon="clock" id="aem-cal-progress" class="calendar-filter-button">' + Granite.I18n.get("In Progress ({0})", countActive.toString()) + '</button>' +
                    '<button is="coral-button" icon="clock" id="aem-cal-soon" class="calendar-filter-button">' + Granite.I18n.get("Due Soon ({0})", countWarn.toString()) + '</button>' +
                    '<button is="coral-button" icon="clock" id="aem-cal-due" class="calendar-filter-button">' + Granite.I18n.get("Past Due ({0})", countLate.toString()) + '</button></coral-buttongroup>')
                    .insertAfter($('.coral-Aeon-navButtons'));

                //add click events on filter buttons
                setFilterButtonsHandlers();
            } else {
                //update totals on button bar
                $("#aem-inboxcal-all coral-button-label").text(Granite.I18n.get("All ({0})", countTot.toString()));
                $("#aem-cal-planned coral-button-label").text(Granite.I18n.get("Planned ({0})", countPlan.toString()));
                $("#aem-cal-progress coral-button-label").text(Granite.I18n.get("In Progress ({0})", countActive.toString()));
                $("#aem-cal-soon coral-button-label").text(Granite.I18n.get("Due Soon ({0})", countWarn.toString()));
                $("#aem-cal-due coral-button-label").text(Granite.I18n.get("Past Due ({0})", countLate.toString()));
            }

            if($.cookie("aeon-cal-project-view") == "list"){
                $('#projectCalFilter').hide();
            } else {
                if ($(".coral-Aeon-resizeContainer").length > 0) {
                    $('#projectCalFilter').show();
                } else {
                    $('#projectCalFilter').hide();
                }
            }
        });
    }

    //add filter button event handlers
    function setFilterButtonsHandlers () {

        // initial state is "All" selected
        $("#aem-inboxcal-all").attr("selected", true);

        $("#aem-cal-planned").click(function () {
            $("#aem-cal-planned").toggleClass("calendar-filter-button-selected");
            updateFilterButtons();
        });
        $("#aem-cal-progress").click(function () {
            $("#aem-cal-progress").toggleClass("calendar-filter-button-selected");
            updateFilterButtons();
        });
        $("#aem-cal-soon").click(function () {
            $("#aem-cal-soon").toggleClass("calendar-filter-button-selected");
            updateFilterButtons();
        });
        $("#aem-cal-due").click(function () {
            $("#aem-cal-due").toggleClass("calendar-filter-button-selected");
            updateFilterButtons();
        });

        $("#aem-inboxcal-all").click(function () {
            if ($(".coral-Aeon-resizeContainer").length > 0) {
                // Show all containers
                $(".coral-Aeon-resizeContainer").show();
            }

            // keep the individual filter button statuses unselected
            $("#aem-cal-planned").removeClass("calendar-filter-button-selected");
            $("#aem-cal-progress").removeClass("calendar-filter-button-selected");
            $("#aem-cal-soon").removeClass("calendar-filter-button-selected");
            $("#aem-cal-due").removeClass("calendar-filter-button-selected");

            $("#aem-cal-planned").attr("selected", false);
            $("#aem-cal-progress").attr("selected", false);
            $("#aem-cal-soon").attr("selected", false);
            $("#aem-cal-due").attr("selected", false);
        });
    };

    //updates filter button states depending on button events
    function updateFilterButtons () {
        // Clicking the "All" button will reset the individual selection status for all buttons,
        // so we need to check all of them whenever any one of them is individually selected
        $("#aem-inboxcal-all").attr("selected", false);

        if ($(".coral-Aeon-resizeContainer").length > 0) {
            // Tasks are grouped by schedule
            if ($("#aem-cal-planned").hasClass("calendar-filter-button-selected") == true)
                $($(".coral-Aeon-resizeContainer")[0]).show();
            else
                $($(".coral-Aeon-resizeContainer")[0]).hide();

            if ($("#aem-cal-progress").hasClass("calendar-filter-button-selected") == true)
                $($(".coral-Aeon-resizeContainer")[1]).show();
            else
                $($(".coral-Aeon-resizeContainer")[1]).hide();

            if ($("#aem-cal-soon").hasClass("calendar-filter-button-selected") == true)
                $($(".coral-Aeon-resizeContainer")[2]).show();
            else
                $($(".coral-Aeon-resizeContainer")[2]).hide();

            if ($("#aem-cal-due").hasClass("calendar-filter-button-selected") == true)
                $($(".coral-Aeon-resizeContainer")[3]).show();
            else
                $($(".coral-Aeon-resizeContainer")[3]).hide();
        }
    }

    function isDate(date) {
        return (new Date(date) !== "Invalid Date") && !isNaN(new Date(date));
    }
})(window, document, Granite, Granite.$, Granite.URITemplate);

