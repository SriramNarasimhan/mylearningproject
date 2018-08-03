<%--
  ADOBE CONFIDENTIAL
  Copyright 2015 Adobe Systems Incorporated
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
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="org.apache.commons.lang3.StringUtils,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ValueMap,
                  java.util.Date,
                  java.util.Locale,
                  java.text.SimpleDateFormat,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag" %><%--###
label
=========
.. granite:servercomponent:: /libs/granite/ui/components/coral/foundation/form/textfield
   :supertype: /libs/granite/ui/components/coral/foundation/form/field

   A text field component.
   It extends :granite:servercomponent:`Field </libs/granite/ui/components/coral/foundation/form/field>` component.
   It has the following content structure:
   .. gnd:gnd::
      [granite:FormTextField] > granite:FormField

      /**
       * The name that identifies the field when submitting the form.
       */
      - name (String)

      /**
       * The value of the field.
       */
      - value (StringEL)

      /**
       * A hint to the user of what can be entered in the field.
       */
      - emptyText (String) i18n

      /**
       * Indicates if the field is in disabled state.
       */
      - disabled (Boolean)

      /**
       * Indicates if the field is mandatory to be filled.
       */
      - required (Boolean)

      /**
       * Indicates if the value can be automatically completed by the browser.
       *
       * See also `MDN documentation regarding autocomplete attribute <https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input>`_.
       */
      - autocomplete (String) = 'off'

      /**
       * The ``autofocus`` attribute to lets you specify that the field should have input focus when the page loads,
       * unless the user overrides it, for example by typing in a different control.
       * Only one form element in a document can have the ``autofocus`` attribute.
       */
      - autofocus (Boolean)

      /**
       * The name of the validator to be applied. E.g. ``foundation.jcr.name``.
       * See :doc:`validation </jcr_root/libs/granite/ui/components/coral/foundation/clientlibs/foundation/js/validation/index>` in Granite UI.
       */
      - validation (String) multiple

      /**
       * The maximum number of characters (in Unicode code points) that the user can enter.
       */
      - maxlength (Long)
###--%>
<%
    Config cfg = cmp.getConfig();
    ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());

    String type = cfg.get("type", String.class);

    AttrBuilder attrs = new AttrBuilder(request, xssAPI);
    attrs.add("type", type);

    String name = cfg.get("name", String.class);
    String value = vm.get("value", String.class);
    if (name.compareToIgnoreCase("status") == 0) {
      value = i18n.getVar(value);
    }

    if(type.compareToIgnoreCase("datetime") == 0){
        attrs.add("dateFormat", cfg.get("displayedFormat", String.class));

        if (value != null) {
            //Date is always a string in format 'Wed Oct 26 16:50:34 CEST 2016'
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date date = sdf.parse(value);

            //Reformat to ISO-8601 '2016-10-26T16:50:34.000-01:00'
            if (date != null) {
                SimpleDateFormat sdfiso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                value = sdfiso.format(date);
            }
        }
    }
%>
<span class="cq-inbox-label-value" <%= attrs.build() %>><%= value %></span>

