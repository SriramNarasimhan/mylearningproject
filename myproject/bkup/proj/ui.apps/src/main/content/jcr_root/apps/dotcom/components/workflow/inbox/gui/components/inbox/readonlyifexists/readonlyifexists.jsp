<%--
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
--%><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.Config,
                  org.apache.commons.lang.StringUtils,
                  org.apache.sling.api.resource.ValueMap,
                  com.adobe.cq.projects.api.Project,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
    Config cfg = cmp.getConfig();
    String value = cfg.get("value", String.class);
    String contentValue = cmp.getExpressionHelper().get(value, String.class);

    ValueMap vm = resource.adaptTo(ValueMap.class);
    String nonEmptyComponent = vm.get("emptyValueComponent", String.class);

    if (StringUtils.isBlank(contentValue) && !StringUtils.isBlank(nonEmptyComponent)) { %>
        <sling:include resourceType="<%= nonEmptyComponent %>" path="<%= resource.getPath() %>"/>
    <% } else {
        // Add a disabled input text box to display the project name consistent with the form style
        String fieldLabel = vm.get("fieldLabel", String.class);
        %> <label class="coral-Form-fieldlabel"><%= i18n.getVar(fieldLabel) %></label> <%

        Field field = new Field(cfg);
        boolean isMixed = field.isMixed(cmp.getValue());

        Tag tag = cmp.consumeTag();
        AttrBuilder attrs = tag.getAttrs();
        cmp.populateCommonAttrs(attrs);

        attrs.add("type", "text");
        attrs.add("name", "projectName");
        attrs.add("placeholder", i18n.getVar(cfg.get("emptyText", String.class)));
        attrs.add("maxlength", cfg.get("maxlength", Integer.class));
        attrs.addClass("coral-Form-field coral-Textfield");
        attrs.addDisabled(true);

        Project project = resourceResolver.getResource(contentValue).adaptTo(Project.class);
        if (isMixed) {
            attrs.addClass("foundation-field-mixed");
            attrs.add("placeholder", i18n.get("<Mixed Entries>"));
        } else {
            attrs.add("value", project.getTitle());
        }

        String validation = StringUtils.join(cfg.get("validation", new String[0]), " ");
        attrs.add("data-foundation-validation", validation);
        attrs.add("data-validation", validation); // Compatibility

        // @coral
        attrs.add("is", "coral-textfield");
        %> <input <%= attrs.build() %>><%

        // Add a hidden input box to provide the required project path for the form
        %><sling:include resourceType="granite/ui/components/coral/foundation/form/hidden" path="<%= resource.getPath() %>"/>
<%
    }
%>
