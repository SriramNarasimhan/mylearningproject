<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
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
--%><%@page session="false"
            import="com.day.cq.wcm.api.components.ComponentContext,
            org.apache.sling.api.SlingHttpServletRequest,
            org.apache.sling.api.SlingHttpServletResponse,
            org.apache.sling.api.wrappers.SlingHttpServletResponseWrapper,
            org.apache.sling.commons.json.JSONObject,
            java.io.IOException,
			org.apache.commons.lang.StringUtils,
            java.io.PrintWriter,
            java.io.StringWriter" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%
    String openPayloadUrl=null;
    String title = "";
    String description = "";

    String thumbnailUrl = "/libs/cq/ui/widgets/themes/default/icons/240x180/page.png";
    String payloadPath = cmp.getValue().get("contentPath");
	String workItemThumbnailUrl = cmp.getValue().get("thumbnailUrl");
    boolean isWorkItemThumbnailUrlSet = false;
    if(StringUtils.isNotBlank(workItemThumbnailUrl)){
		thumbnailUrl = workItemThumbnailUrl;
        isWorkItemThumbnailUrlSet = true;
    }

    Map<String, String> extraContent = new HashMap<String, String>();

    if (payloadPath != null) {

        if (resourceResolver.getResource(payloadPath) != null) {
        String payloadSummary = getPayloadSummaryJSON(slingRequest, response, payloadPath + ".largeicon.payloadsummary.json");
            if ( payloadSummary != null ) {
                JSONObject jsonObject = new JSONObject(payloadSummary);

                if (jsonObject.has("openPayloadUrl")) {
                    openPayloadUrl = jsonObject.get("openPayloadUrl").toString();
                }

                if ( !isWorkItemThumbnailUrlSet && jsonObject.has("icon") ) {
                    thumbnailUrl = jsonObject.get("icon").toString();
                }

                if ( jsonObject.has("description") ) {
                    description = jsonObject.get("description").toString();                    
                }

                if ( jsonObject.has("title") ) {
                    title = jsonObject.get("title").toString();                    
                }

                if ( jsonObject.has("extra")) {
                    Object obj = jsonObject.get("extra");
                    if (obj instanceof JSONObject) {
                        JSONObject extraObject = (JSONObject) obj;
                        Iterator<String> keys = extraObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = extraObject.getString(key);
                            extraContent.put(key, value);                            
                        }
                    }
                }

            }
        } else {
            openPayloadUrl = payloadPath;
            title = payloadPath;
        }
    }
    thumbnailUrl = request.getContextPath() + thumbnailUrl;
    title = xssAPI.filterHTML(title);
    description = xssAPI.filterHTML(description);
%>
<article class="card-asset taskpayloadinfo foundation-collection-item">
<% if (openPayloadUrl != null) {%>
    <a href="<%= xssAPI.getValidHref(openPayloadUrl) %>" target="_blank" x-cq-linkchecker="skip">
<% } else { %>
    <a>
<% } %>
       <span class="image">
           <img src="<%= xssAPI.encodeForHTMLAttr(thumbnailUrl) %>">
       </span>
        <div class="label">
            <h4><%= xssAPI.encodeForHTML(i18n.getVar(title)) %></h4>
            <p class="description extra-info"><%= xssAPI.encodeForHTML(i18n.getVar(description)) %></p>
<% if ( extraContent.size() > 0 ) {
        Iterator<String> keys = extraContent.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            String value = extraContent.get(key);
            %>
            <h4><%= xssAPI.encodeForHTML(i18n.getVar(key)) %>:</h4>
            <p class="extra-info"><%= xssAPI.encodeForHTML(i18n.getVar(value)) %></p>
<%        }
%>

<% } %>
        </div>
    </a>
</article>


<%!
    private String getPayloadSummaryJSON(SlingHttpServletRequest slingRequest, HttpServletResponse response, String path)
            throws ServletException, IOException {

        // Response wrapper that captures into a String
        final HttpServletResponse wrapper = new SlingHttpServletResponseWrapper((SlingHttpServletResponse)response) {
            private final StringWriter writer = new StringWriter();
            private final PrintWriter pw = new PrintWriter(writer);

            public PrintWriter getWriter() {
                return pw;
            }

            public String toString() {
                return writer.toString();
            }
        };

        // Dispatch request, describe errors in JSON
        Object oldContextAttributeValue = null;
        try {
            oldContextAttributeValue = slingRequest.getAttribute(ComponentContext.CONTEXT_ATTR_NAME);
            slingRequest.setAttribute(ComponentContext.CONTEXT_ATTR_NAME, null);
            RequestDispatcher requestDispatcher = slingRequest.getRequestDispatcher(path);
            requestDispatcher.include(slingRequest, wrapper);
        } catch(Exception e) {
            return "{ exception : '" + e.toString() + " (" + path + ")'}";
        } finally {
            slingRequest.setAttribute(ComponentContext.CONTEXT_ATTR_NAME, oldContextAttributeValue);
        }
        return wrapper.toString();
    }
%>
