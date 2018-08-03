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
(function (document, Granite, $) {
    "use strict";

    var DEFAULT_INBOX_SETTINGS = {
        sortByProperty: 'startTime',
        sortOrder: 'DESC'
    };

    var ns = ".cq-inbox-settings";
    var viewSettingsAction = undefined; // the 'View Settings' action element
    var inboxSettingsDialogURL = "/mnt/overlay/cq/inbox/content/inbox/dialogs/inboxsettingsdialog.html";
    var ui = $(window).adaptTo("foundation-ui");

    function toggleViewSettingsAction(selectedView, $collection) {
        var foundationModeGroup = $collection.data('foundationModeGroup');
        if (!foundationModeGroup) {
            // use a default. Looks like calendar doesn't have a data-foundation-mode-group setting ...
            foundationModeGroup = "cq-workflow-inbox-list";
        }

        var collectionSelector = foundationModeGroup === 'granite-omnisearch-result' ? '#granite-omnisearch-result' : "." + foundationModeGroup;

        var cycleButton = $("[data-granite-collection-switcher-target='" + collectionSelector + "']").get(0);
        var URITemplate = $collection.data("inboxSettingsDialogUriTemplate") || inboxSettingsDialogURL;

        if (cycleButton) {
            //explicitly test to see if we get one of the expected views back
            //to avoid omni search breaking the viewsettings drop down
            if (selectedView === "list") {
                var actionObject = {
                    target: collectionSelector,
                    action: "cq.inbox.action.showsettingsdialog",
                    data: {
                        src: URITemplate
                    },
                    relScope: "none"
                };
            } else if (selectedView === "calendar") {
                //get rid of action bar if it is not closed
                $('.granite-collection-selectionbar .foundation-mode-switcher-item').removeClass('foundation-mode-switcher-item-active');
                var actionObject = {
                    target: collectionSelector,
                    action: "cq.inbox.action.showcalendarsettings",
                    relScope: "none"
                };
            }
            var actionText = Granite.I18n.get("View Settings");
            var actionHTML = '<coral-cyclebutton-action icon="gear" class="foundation-collection-action">' + actionText + '</coral-quickactions-item>';
            var $action = $(actionHTML);

            $action.data("foundationCollectionAction", actionObject);
            if(cycleButton.actions != undefined && $('.foundation-layout-panel-content').length == 1) {
                if(viewSettingsAction != undefined) {
                    cycleButton.actions.remove(viewSettingsAction);
                }
                viewSettingsAction = cycleButton.actions.add($action.get(0));
            }
        }
    }

    $(document).on("foundation-layout-perform", ".foundation-collection", function (e) {
        var $collection = $(e.target);
        var config = $collection.data("foundationLayout");

        if ("calendar" == config.layoutId) {
            // calendar doens't trigger selection changes
            // -> hide the View Settings action
            toggleViewSettingsAction(config.layoutId, $collection);
        } else {
            $(document).one("foundation-selections-change", ".foundation-collection", function (e) {
                var showAction = (config.layoutId === "list");
                toggleViewSettingsAction(config.layoutId, $collection);
            });
        }
    });

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.inbox.action.showsettingsdialog",
        handler: function(name, el, config, collection, selections) {
            CQ.Inbox.UI.commons.loadDialog(inboxSettingsDialogURL).done(function () {
                showInboxSettingsDialog();
            }).fail(function (error) {
                ui.alert(Granite.I18n.get("Error"), error, "error");
            });
        }
    });

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq.inbox.action.showcalendarsettings",
        handler: function(name, el, config, collection, selections) {
            $('aeon-pane-button[handle="settingbutton"]').click();

            $('.coral-Aeon-settings-buttons [handle="apply"]')
                .off('click' + ns)
                .on("click" + ns, function () {

                    var groupBySelected = $('.coral-Well coral-checkbox[handle="enablegroup"]').attr('checked') == "checked";
                    var groupVal = $('.coral-Well coral-select[handle="group"] span').text()
                    var card = $('.coral-Aeon-settings-sizeOptions [name="size"]:checked').val();
                    var calPrefs = {cardSize:card, groupBySelected:groupBySelected, groupByValue:groupVal};

                    var data = {
                        'cq.inbox.calSettings': JSON.stringify(calPrefs)
                    };

                    postUserPreferences(data);
                });
        }
    });

    function showInboxSettingsDialog() {
        var dialog = $("#inboxSettingsDialog");
        var form = $("form", dialog);

        // need to set the current defaults
        getCurrentUserPreferences().done(function(data) {
            // select the property name
            selectItem('sortByProperty', data.sortByProperty);
            selectItem('sortOrder', data.sortOrder);

            dialog.get(0).show();
            dialog.find(".inbox-settings-dialog-save")
                .off('click' + ns)
                .on('click' + ns, function() {
                    var that = this;

                    ui.wait();
                    this.disabled = true;

                    var formData = form.serializeAsJSON();
                    // remove the unused @Delete keys
                    delete formData['sortByProperty@Delete'];
                    delete formData['sortOrder@Delete'];

                    var data = {
                        'cq.inbox.settings': JSON.stringify(formData)
                    };

                    postUserPreferences(data)
                        .done(function() {
                            var omniSearchForm = $(".granite-omnisearch-form");
                            if (omniSearchForm.length > 0) {
                                // we're in omnisearch -> just re-submit the search form
                                omniSearchForm.submit();
                            } else {
                                window.location.reload(true);
                            }
                        })
                        .fail(function (xhr) {
                            form.trigger("foundation-form-submit-callback", [xhr]);
                        })
                        .always(function() {
                            dialog.get(0).hide();
                            ui.clearWait();
                            that.disabled = false;
                        });
                });
        }).fail(function(data) {
            console.error("Failed to load user preferences" + data);
        });

    }

    function selectItem(selectName, selectedValue) {
        var $select = $("coral-select[name='" + selectName +"']");
        if ($select.length==1) {
            var coralSelect = $select.get(0);
            var selectItems = coralSelect.items.getAll();
            for (var index = 0; index < selectItems.length; index++) {
                if (selectItems[index].value == selectedValue) {
                    selectItems[index].selected = true;
                    break;
                }
            }
        }
    }

    function getCurrentUserPreferences() {
        var deferred = $.Deferred();
        getUserPromise().then(function (user) {
            $.get(Granite.HTTP.externalize(user.home + "/preferences/cq.inbox.settings"))
                .done(function (inboxSettingsFromServer) {
                    var inboxSettings = JSON.parse(inboxSettingsFromServer);
                    deferred.resolve(inboxSettings);
                })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    if (jqXHR.status==404) {
                        // no settings found, resolve with the defaults
                        deferred.resolve(DEFAULT_INBOX_SETTINGS)
                    } else {
                        deferred.reject(Granite.I18n.get("Unable to load " + dialogPath));
                    }
                });
        });
        return deferred.promise();
    }

    function postUserPreferences(data) {
        var deferred = $.Deferred();

        getUserPromise().then(function (user) {
            $.ajax({
                type: "POST",
                url: Granite.HTTP.externalize(user.home + "/preferences"),
                data: data
            }).done(function(data, textStatus, jqXHR) {
                deferred.resolve(data, textStatus, jqXHR);
            }).fail(function(jqXHR, textStatus, errorThrown) {
                deferred.reject(jqXHR, textStatus, errorThrown);
            })
        }).fail(function (jqXHR, textStatus, errorThrown) {
            deferred.reject(jqXHR, textStatus, errorThrown);
        });
        return deferred.promise();
    };

    var cachedUserPromise;
    function getUserPromise() {
        if (!cachedUserPromise) {
            cachedUserPromise = $.ajax({url: Granite.HTTP.externalize("/libs/granite/security/currentuser.json")});
        }
        return cachedUserPromise;
    }

})(document, Granite, Granite.$);