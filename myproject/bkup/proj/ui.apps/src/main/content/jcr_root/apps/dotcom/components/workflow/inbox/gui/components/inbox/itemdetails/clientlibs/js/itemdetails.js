(function($, Granite, undefined) {
    'use strict';

    var ns = ".cq-inbox-taskdetails";
    var taskDetailsNS = ".itemdetails";
    var completeButton = ".completeDetailsTask";
	var updateButton = ".updateDetailsTask";
    var addCommentButton = ".task-comment-addnew";
    var deleteCommentButton = ".task-comment-delete";

    var ui = $(window).adaptTo("foundation-ui");

    /**
     * jQuery plugin that serializes form data as a JSON object.
     * @return {Object} JSON representation of the form data
     */
    $.fn.serializeAsJSON = function() {

        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };

    $(document)
        .off("foundation-contentloaded" + taskDetailsNS)
        .on("foundation-contentloaded" + taskDetailsNS, function(e) {
            var form = $("#updatetaskform");

            var statusValue = $("[name='status']", form).val();
            if ( statusValue !== "ACTIVE" ) {
                $(completeButton).attr("disabled", "disabled");
                $(updateButton).attr("disabled", "disabled");
            } else {
                $(completeButton).removeAttr("disabled");
                $(updateButton).removeAttr("disabled");
            }

            // disable the read-only info elements:
            $(".cq-inbox-info--read-only").attr("readonly", "readonly");
    });

    $(document).on("foundation-contentloaded" + taskDetailsNS, function(e) {
        var $form = $("form.task");
        var projectPath = $form.data("project");

        // find the project path field
        var $cuiSelectList = $(".coral-Autocomplete .coral-SelectList", $form);
        var originalURL = $cuiSelectList.attr("data-task-original-url");
        var updatedURL = $cuiSelectList.attr("data-granite-autocomplete-src");
        if (originalURL === undefined || originalURL === "") {
            $cuiSelectList.attr("data-task-original-url", updatedURL);
            originalURL = updatedURL;
        }
        $cuiSelectList.attr("data-granite-autocomplete-src", originalURL + "&projectPath="+projectPath);

        $('input[name="contentPath"]').blur(function() {
            var selectedImage = $(this).val();
            var res = selectedImage.split(".");
            if(res.length >= 2) {
                $('article img').attr("src", Granite.HTTP.externalize(selectedImage));
                $('article img').width($('article').width());
            } else {
                findThumbnail(selectedImage + ".largeicon.payloadsummary.json");
            }
        });

        $("article img").error(function () {
            $(this).attr("src", Granite.HTTP.externalize("/libs/cq/ui/widgets/themes/default/icons/240x180/page.png"));
        });

        //set calendar ranges
        var startDate = $('.inbox-startdate').attr('value');
        var interimDate = moment(new Date(startDate));
        var minDueDate = interimDate.format("YYYY-MM-DD");
        $('.inbox-duedate').attr('min', minDueDate);

        var dueDate = $('.inbox-duedate').attr('value');
        var interimDate = moment(new Date(dueDate));
        var maxStartDate = interimDate.format("YYYY-MM-DD");
        $('.inbox-startdate').attr('max', maxStartDate);

        //make sure the due date cannot be before the start date
        $('.inbox-startdate').on("change", function(e) {
            var startDate = $('.inbox-startdate').val();
            var interimDate = moment(new Date(startDate));
            var minDueDate = interimDate.format("YYYY-MM-DD");
            $('.inbox-duedate').attr('min', minDueDate);
        });

        //make sure the start date cannot come after the due date
        $('.inbox-duedate').on("change", function(e) {
            var dueDate = $('.inbox-duedate').val();
            var interimDate = moment(new Date(dueDate));
            var maxStartDate = interimDate.format("YYYY-MM-DD");
            $('.inbox-startdate').attr('max', maxStartDate);
        });

        $.each($('.project-taskdetails span[type="datetime"]'), function( index, value ) {
            var newdate = $(this).text()
            var interimDate = moment(new Date(newdate));
            var formattedDate = interimDate.format(Granite.I18n.get("YYYY-MM-DD"));
            // moment.js may return an English invalid date message, translate here
            if (formattedDate == "Invalid date") {
                formattedDate = Granite.I18n.get("Invalid date");
            }
            $(this).text(formattedDate);
        });
    });

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

    $(document)
        .off("foundation-model-dataloaded" + taskDetailsNS)
        .on("foundation-model-dataloaded" + taskDetailsNS, function(e) {

            var form = $("#updatetaskform");

            if ( form.data("model").get("status") !== "ACTIVE" ) {
                $(completeButton).attr("disabled", "disabled");
                $(updateButton).attr("disabled", "disabled");
            } else {
                $(completeButton).removeAttr("disabled");
                $(updateButton).removeAttr("disabled");
            }
    });

    function endsWith(str, suffix) {
        return str.indexOf(suffix, str.length - suffix.length) !== -1;
    }

    function checkTaskAction() {
        var actionSelect = $("#taskCompletionDialog").find("coral-select");
        if ( actionSelect.length > 0 ) {
            // only check the actions if the action select is not hidden
            if (!actionSelect[0].hidden) {
                if (!actionSelect.val()) {
                    return false;
                }
            }
        }
        return true;
    };

    function submitForm($form, completeTask) {
        var postUrl = $form.attr("action");
        var taskId = getUrlVar("item");

        var taskModel = $form.serializeAsJSON();

        // now fix the assignee -> formfield is named 'asignee' to match the persisted task,
        // however taskmanager expects ownerId
        taskModel["ownerId"] = taskModel["assignee"];
        delete taskModel["assignee"];

        taskModel["title"] = taskModel["name"];

        if (completeTask) {
            var actionSelect = $("#taskCompletionDialog").find("select");
            if ( actionSelect.length > 0 ) {
                taskModel["action"] = actionSelect.val();
            }
            taskModel["comment"] = $("[name=comment]", "#taskCompletionDialog").val();

            taskModel["status"] = "COMPLETE";
            return saveTask(postUrl, taskId, taskModel);
        } else {
            return saveTask(postUrl, taskId, taskModel);
        }
    }

    function saveTask(postUrl, taskId, data) {
        var url = Granite.HTTP.externalize(postUrl);

        url += "?taskId=" + encodeURIComponent(taskId);
        if (data["action"]) {
            url += (url.indexOf("?") > 0 ? "&" : "?") + "selectedAction=" + encodeURIComponent(data["action"]);
            delete data["action"];
        }
        // ensure sling treats this as utf-8
        url += "&_charset_=utf-8";

        var jsonTask = createJSONTask(data);

        return $.ajax({
            type: "POST",
            url: url,
            contentType: "application/json",
            processData: false,
            data: JSON.stringify(jsonTask)
        });
    }

    function createJSONTask(formDataAsJSON) {
        var result = {};

        setTaskProperty("description", result, formDataAsJSON);
        setTaskProperty("instructions", result, formDataAsJSON);
        setTaskProperty("name", result, formDataAsJSON);
        setTaskProperty("ownerId", result, formDataAsJSON);
        setTaskProperty("status", result, formDataAsJSON);
        setTaskProperty("value", result, formDataAsJSON);
        setTaskProperty("contentPath", result, formDataAsJSON);

        result.properties = {};
        for (var property in formDataAsJSON) {
            if (formDataAsJSON.hasOwnProperty(property) && !property.startsWith(":")) {
                result.properties[property] = formDataAsJSON[property];
            }
        }

        return result;
    }

    function setTaskProperty(propertyName, result, original) {
        if (original.hasOwnProperty(propertyName) && original[propertyName]) {
            result[propertyName] = original[propertyName];
            delete original[propertyName];
        }
    }

    function returnToPrevious(form) {
        var panelSelector = form.data("foundationFormOutputReplace") || form.data("foundationFormOutputPush");
        if (panelSelector) {
            var contentAPI = $(panelSelector).adaptTo("foundation-content");
            if (contentAPI) {
                contentAPI.back();
                $(document).trigger("foundation-history-change");
            }
        }
    }

    function getUrlVar(key){
        var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search);
        return result && decodeURIComponent(result[1]) || "";
    }

    $(document).on('click' + ns, updateButton, function (e) {
        ui.wait();
        var form = $("#updatetaskform");

        e.preventDefault();

        submitForm(form, false)
            .done(function (data, textStatus, xhr) {
                ui.clearWait();
                if (form.data("redirect")) {
                    window.location = Granite.HTTP.externalize(form.data("redirect"));
                } else {
                    returnToPrevious(form);
                }
            }).fail(function (xhr) {
                ui.clearWait();
                form.trigger("foundation-form-submit-callback", [xhr]);
            });
    });

    $(document).on("submit.foundation-form", "form.foundation-form", function(e) {
        ui.wait();
        var form = $("#updatetaskform");

        e.preventDefault();

        submitForm(form, false)
            .done(function (data, textStatus, xhr) {
                ui.clearWait();
                if (form.data("redirect")) {
                    window.location = Granite.HTTP.externalize(form.data("redirect"));
                } else {
                    returnToPrevious(form);
                }
            }).fail(function (xhr) {
                ui.clearWait();
                form.trigger("foundation-form-submit-callback", [xhr]);
            });
    });
	
	function completeTask() {
		var form = $("#updatetaskform");
        var dialog = $("#taskCompletionDialog");

        var select = dialog.find("coral-select").get(0);
        if (select && select.items) {
            select.items.clear();
        }

        if ($(".cq-projects-admin-task-actions input").length > 0) {
            $(".cq-projects-admin-task-actions input").each(function () {
                select.items.add({
                    content: {
                        innerHTML: $(this).attr('name')
                    },
                    value: $(this).val()
                });
            });
        } else {
            select.hide();
        }

        dialog.get(0).show();
        dialog.find(".task-complete-dialog-submit")
            .off('click' + ns)
            .on('click' + ns, function() {
                var that = this;


                if (!checkTaskAction()) {
                    // this should somehow indicate that the
                    // selected action is invalid.
                    return;
                }

                ui.wait();
                this.disabled = true;

                submitForm(form, true)
                    .done(function() {
                        if (form.data("redirect")) {
                            window.location = Granite.HTTP.externalize(form.data("redirect"));
                        } else {
                            returnToPrevious(form);
                        }
                    })
                    .fail(function (xhr) {
                        form.trigger("foundation-form-submit-callback", [xhr]);
                    })
                    .always(function() {
                        ui.clearWait();
                        that.disabled = false;
                    });
        });
	}

    $(document).on('click' + ns, completeButton, function (e) {
        var beforecomplete = $(completeButton).data("beforecomplete");
        if(beforecomplete) {
            ui.wait();
            var result = beforecomplete();
            result.done(function() {
                completeTask();
            }); 
            result.always(function() {
                ui.clearWait();
            });
        }
        else {
        	completeTask();
        }
    });

    $(document).on('click' + ns, addCommentButton, function(e) {
        var $theButton = $(this);
        var $form = $theButton.closest("form");
        var $commentTarget = $($theButton.data("commentTargetRef"));

        var $mesageField = $form.find("[name='message']");
        var messageText = $mesageField.val();
        if (messageText === undefined || messageText === "" || messageText.trim() === "") {
            return;
        }

        // show the wait spinner
        ui.wait();

        var project = $("#updatetaskform").data("project");

        $theButton.attr('disabled','disabled');
        $mesageField.val("");


        var data = {
            "message" : messageText,
            "task" : $form.find("[name=':postUrl']").val(),
            ":operation" : "granite:comment",
            "_charset_" : "utf-8",
            ":redirect" : Granite.HTTP.externalize($form.find("[name=':redirect']").val()),
            "subFolder" : $form.find("[name='subFolder']").val()
        };

        var ajaxOptions = {
            url: Granite.HTTP.externalize(project),
            type: "post",
            data: data,
            success: function(data, status, request) {
                if (status === "success" ) {
                    $commentTarget.empty().append($(data).children());
                }
                $theButton.removeAttr("disabled");
                ui.clearWait();
            },
            error: function(jqXHR, message, error) {
                $mesageField.val(messageText);
                $theButton.removeAttr("disabled");
                ui.clearWait();
                ui.alert(error, Granite.I18n.get("Unable to add the new comment"), "error");
            }
        };
        $.ajax(ajaxOptions);

     });

    $(document).on('click' + ns, deleteCommentButton, function(e) {
        // show the wait spinner
        ui.wait();

        var $theButton = $(this);
        var $form = $theButton.closest("form");

        var data =  {
            ":operation" : "delete",
            "_charset_" : "utf-8",
            ":redirect" : Granite.HTTP.externalize($form.find("[name=':redirect']").val()) + "&_charset_=utf-8"
        };

        var postUrl = Granite.HTTP.externalize($(this).data("target"));

        var ajaxOptions = {
            url: postUrl,
            type: "post",
            data: data,
            success: function(data, status, request) {
                if (status === "success" ) {
                    $theButton.closest(".cq-tasks-comments").empty().append($(data).children());
                }
                $theButton.removeAttr("disabled");
                ui.clearWait();
            },
            error: function(jqXHR, message, error) {
                ui.clearWait();
                ui.alert(error, Granite.I18n.get("Unable to delete the comment"), "error");
            }
        };
        $.ajax(ajaxOptions);

    });
})(Granite.$, Granite);
