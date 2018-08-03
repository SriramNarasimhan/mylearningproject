/*
 * ADOBE CONFIDENTIAL
 * __________________
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
 */

(function($, Granite, XSS, undefined) {
    "use strict";

    var createWizardRel = ".cq-project-admin-addtaskwizard";
    var ui = $(window).adaptTo("foundation-ui");
    var projectPathRegExp = "(\\?|&)" + "projectPath" + "(\\[\\])?=([^&]*)";
    var projectDetails = "/projects/details.html";

    // Handle the add task back button
    $(document).fipo("tap.foundation-wizard-control", "click.foundation-wizard-control", ".foundation-wizard-control", function(e) {
        if ($(this).data("foundation-wizard-control-action") != "cancel") return;

        var ref = $(".urlParameters").data("ref");
        if (!ref || ref.length == 0) {
            var result = location.search.match(new RegExp(projectPathRegExp));
            if (!result) return;
            var previousHref = $("[coral-wizardview-previous]").attr("href");
            ref = (previousHref || projectDetails) + result[3];
        }

        e.preventDefault();
        window.location = Granite.HTTP.externalize(ref);
    });

    function doSubmit(form) {
        var targetUrl = Granite.HTTP.externalize($("[name='projectPath']").val());

        //if there is not target URL then this is an ad-hoc task and we set a default url
        if(!targetUrl) {
            targetUrl = "/libs/granite/taskmanager/createtask?_charset_=utf-8";
        }

        var ajaxOptions = null;
        if (targetUrl.indexOf("/libs/granite/taskmanager/createtask") > -1) {

            var container = {};
            var data = {};

            var properties = {};
            //Gathering the Data
            //and removing undefined keys(buttons)
            $.each(form.serializeArray(), function(i, item) {
                //var input = $(v);

                if ("assignee" === item.name) {
                    var assignee = item.value.trim();
                    if (assignee.length > 0)
                        data["ownerId"] = assignee;
                } else if ("sendEmailNotification" === item.name) {
                    var emailOption = item.value.trim();
                    if (emailOption.length > 0)
                        properties["sendEmailNotification"] = emailOption;
                }else {
                    data[item.name] = item.value;
                }
                delete data["undefined"];
            });

            data["properties"] = properties;

            //container["properties"] = data;

            ajaxOptions = {
                type: "post",
                contentType : "application/json",
                processData: false,
                data : JSON.stringify(data),
                url: targetUrl
            };
        }
        else {
            ajaxOptions = {
                type: "post",
                data: form.serialize(),
                url: targetUrl
            };
        }

        $.ajax(ajaxOptions)
            .done(function(html) {
                var $html = $(html);

                var title = $html.find(".foundation-form-response-title").next().text();
                var message = $html.find(".foundation-form-response-description").next().html();
                var redirect = "/aem/inbox";

                if(title.length == 0) {
                    title = Granite.I18n.get("Task Created");
                    message = Granite.I18n.get("Task {0} has been created",XSS.getXSSValue(html.name));
                }

                ui.prompt(title, message, "success", [{
                    text: Granite.I18n.get("Done"),
                    primary: true,
                    handler: function() {
                        var result = location.search.match(new RegExp(projectPathRegExp));
                        if (result) {
                            var previousHref = $("[coral-wizardview-previous]").attr("href");
                            redirect = (previousHref || projectDetails) + result[3];
                        }
                        window.location = Granite.HTTP.externalize(redirect);
                    }
                }]);

            })
            .fail(function(xhr, error, errorThrown) {
                if (error === "error") {
                    var $html = $(xhr.responseText);
                    ui.alert($html.find(".foundation-form-response-title").next().html() || Granite.I18n.get("Error"),
                        $html.find(".foundation-form-response-description").next().html(),
                        "error");
                } else {
                    ui.alert(Granite.I18n.get("Error"), errorThrown, "error");
                }
            });
    }

    $(document).on("foundation-contentloaded" + createWizardRel, function(e) {
        var $form = $("form.foundation-form" + createWizardRel);
        $form.submit(function(e) {
            e.stopImmediatePropagation();
            e.preventDefault();
            doSubmit($form);
        });
    });

    $(document).on("foundation-contentloaded" + createWizardRel, function(e) {
        updateUserPickerSrc();
        var $form = $("form" + createWizardRel);
        var $projectPathField = $("[name='projectPath']", $form);

        $('[name="contentPath"] input').blur(function() {
            var selectedImage = $(this).val();
            var res = selectedImage.split(".");
            if(res.length >= 2) {
                $('article img').attr("src", Granite.HTTP.externalize($(this).val()));
                $('article img').width($('article').width());
            } else {
                findThumbnail(selectedImage + ".largeicon.payloadsummary.json");
            }
        });

        $("article img").error(function () {
            $(this).attr("src", Granite.HTTP.externalize("/libs/cq/ui/widgets/themes/default/icons/240x180/page.png"));
        });

        $('input[name="name"]').change(function() {
            // node name must conform to URI restrictions
            var value = $(this).val().trim();
            value = value.replace(/[#%"\.\/:\[\]*|\s]/g, "-");
            $('input[name="_nodeNameHint"]').val(value);
        });

        $('input[name="projectPath"]').change(function() {
            $('input[name="assignee"]').val("");
        });

        //make sure the due date cannot be before the start date
        $('.inbox-startdate').on("change", function(e) {
            var startDate = $('.project-startdate').val();
            var interimDate = moment(new Date(startDate));
            var minDueDate = interimDate.format("YYYY-MM-DD");

            var today = moment().format("YYYY-MM-DD");

            //the min date is today or minDueDate.  Whichever one is more current.
            minDueDate = minDueDate > today ? minDueDate : today;
            $('.project-duedate').attr('min', minDueDate);
        });

        //make sure the start date cannot come after the due date
        $('.inbox-duedate').on("change", function(e) {
            var dueDate = $('.inbox-duedate').val();
            var maxStartDate = moment(new Date(dueDate)).format("YYYY-MM-DD");
            $('.inbox-startdate').attr('max', maxStartDate);
        });

        $projectPathField.on("change", function() {
            updateUserPickerSrc();

            var $assigneeSubmitField = $("input[name='assignee']", $form);

            // logged CUI-2304 .. the following lines should be replaced by
            //  $assigneeSubmitField.closest(".granite-autocomplete").data("autocomplete").clear()
            $assigneeSubmitField.val("");
            $assigneeSubmitField.siblings("input").val("");
            $assigneeSubmitField.closest(".granite-autocomplete").data("autocomplete")._lastSelected = "";
        });

    });

    function updateUserPickerSrc() {
        var $form = $("form" + createWizardRel);
        // find the project path field
        var $projectPathField = $("[name='projectPath']", $form);
        var $cuiSelectList = $(".coral-Autocomplete .coral-SelectList", $form);
        var originalURL = $cuiSelectList.attr("data-task-original-url");
        var updatedURL = $cuiSelectList.attr("data-granite-autocomplete-src");
        if (originalURL === undefined || originalURL === "") {
            $cuiSelectList.attr("data-task-original-url", updatedURL);
            originalURL = updatedURL;
        }
        $cuiSelectList.attr("data-granite-autocomplete-src", originalURL + "&projectPath="+$projectPathField.val());
    }

    function findThumbnail(payloadUrl) {
        var ajaxOptions = {
            url: Granite.HTTP.externalize(payloadUrl),
            type: "get",
            success: function (data, status, request) {
                if (status === "success") {
                    $('article img').attr("src", Granite.HTTP.externalize(data.icon));
                    $('article img').width($('article').width());
                    $('article .label h4').text(data.title);
                }
                //ui.clearWait();
            },
            error: function (jqXHR, message, error) {
                $(this).attr("src", Granite.HTTP.externalize("/libs/cq/ui/widgets/themes/default/icons/240x180/page.png"));
            }
        };
        $.ajax(ajaxOptions);
    }

})(Granite.$, Granite, _g.XSS);