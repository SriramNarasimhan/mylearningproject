<%@page session="false"%><%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Login component

--%><%
%><%@ page import="com.day.cq.i18n.I18n,
                   com.day.cq.personalization.UserPropertiesUtil,
                   com.day.cq.wcm.api.WCMMode,
                   com.day.cq.wcm.foundation.forms.FormsHelper,
                   com.day.text.Text, org.apache.sling.auth.core.AuthUtil" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.adobe.granite.security.user.util.AuthorizableUtil" %>
<%
%><%@include file="/apps/myproject/global.jsp"%><%

    String id = Text.getName(resource.getPath());
    I18n i18n = new I18n(slingRequest);

	String action = "/bin/security_check";

	String sectionLabel = properties.get("./sectionLabel", "");
    if (sectionLabel != null) {
        sectionLabel = i18n.getVar(sectionLabel);
    }
    String usernameLabel = properties.get("./usernameLabel", "Username");
    if (usernameLabel == null) {
        usernameLabel = i18n.get("Username");
    } else {
        usernameLabel = i18n.getVar(usernameLabel);
    }
    String passwordLabel = properties.get("./passwordLabel", "Password");
    if (passwordLabel == null) {
        passwordLabel = i18n.get("Password");
    } else {
        passwordLabel = i18n.getVar(passwordLabel);
    }
	String emailLabel = properties.get("./emailLabel", "Enter your E-mail address");
    if (emailLabel == null) {
        emailLabel = i18n.get("E-mail");
    } else {
        emailLabel = i18n.getVar(emailLabel);
    }
    String loginLabel = properties.get("./loginLabel", "Sign in");
    if (loginLabel == null) {
        loginLabel = i18n.get("Sign In");
    } else {
        loginLabel = i18n.getVar(loginLabel);
    }

%>

<c:set var="redirectTo" value="${properties.redirectTo}" />

<div class="err" id="add_err"></div>

<div id="login_section">
    <table class="login-form">
        <tr>
            <td class="label"><label class="username"><%= xssAPI.encodeForHTML(usernameLabel) %></label></td>
            <td><input id="username" type="text"
                        name="j_username"  autofocus/></td>
        </tr>
        <tr>
            <td class="label"><label class="password"><%= xssAPI.encodeForHTML(passwordLabel) %></label></td>
            <td><input id="password" type="password"
                        autocomplete="off" name="j_password"/></td>
        </tr>
        <tr>
            <td><a style="text-decoration: underline" id="forgotPassword">Forgot Password</a></td>
            <td><input id="login" class="form_button_submit" type="submit" value="<%= xssAPI.encodeForHTMLAttr(loginLabel) %>"></td>
        </tr>
    </table>
</div>

<div id="forgotPassword_section">
    <table>
        <tbody>
            <tr>
                <td valign="top">
                    <br>
                    <span class="fields"><%= xssAPI.encodeForHTML(emailLabel) %></span>
                    <input type="email" id="email" autocomplete="off" name="j_email"/>
                </td>
            </tr>
            <tr>
                <td><a style="text-decoration: underline" id="backtoLogin">Go back to Login</a></td>
                <td><input id="sendPassword" type="submit" value="Send Password"></td>
            </tr>
        </tbody>
    </table>
</div>


<script>
$(document).ready(function() {
    $("#add_err").css('display', 'none', 'important');
    $("#forgotPassword_section").css('display', 'none', 'important');
    var userName = $("#userName").val();
    var redirectToLogin = $("#redirectTo").val();
     $("#login").click(function(){
         var username=$("#username").val();
         var password=$("#password").val();
         var email=$("#email").val();
          $.ajax({
           type: "POST",
           url: "/bin/security_check",
           data: {"username":username,"password":password,"email":email},
           success: function(response){
                var status = response;
                if(status.toString()=="loginsuccess"){
                 $("#add_err").html("Authenticated user. Redirecting to dealer page...");
                    window.location.href="${redirectTo}.html";
                }
                else {
                $("#add_err").css('display', 'inline', 'important');
                 $("#add_err").html("Wrong username or password");
                }
           },
           beforeSend:function()
           {
            $("#add_err").css('display', 'inline', 'important');
            $("#add_err").html("<img src='/libs/granite/ui/clientlibs/legacy/themes/default/jquery/mobile/images/ajax-loader.png' /> Loading...")
           }
          });
        return false;
    });
    $('#forgotPassword').click(function() {
        $('#forgotPassword_section').show();
       $('#login_section').hide();
    });
    $('#backtoLogin').click(function() {
       $('#forgotPassword_section').hide();
       $('#login_section').show();
    });
    $("#sendPassword").click(function(){
         var email=$("#email").val();
          $.ajax({
           type: "POST",
           url: "/bin/security_check",
           data: {"email":email},
           success: function(response){
               var status = response;
             if(status.toString()=="sendpassword"){
             alert("Mail sent.. Check your inbox");
            }
           },
           beforeSend:function()
           {
            $("#add_err").css('display', 'inline', 'important');
            $("#add_err").html("<img src='/libs/granite/ui/clientlibs/legacy/themes/default/jquery/mobile/images/ajax-loader.png' /> Loading...")
           }
          });
        return false;
    });
});
</script>