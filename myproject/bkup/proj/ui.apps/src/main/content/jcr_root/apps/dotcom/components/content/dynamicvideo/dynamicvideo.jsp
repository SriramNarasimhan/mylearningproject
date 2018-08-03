<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

--%><%@page session="false"
            import="com.day.cq.commons.jcr.JcrConstants,
                    com.day.cq.commons.jcr.JcrUtil,
                    com.day.cq.dam.api.Asset,
                    com.day.cq.dam.api.s7dam.utils.PublishUtils,
                	com.day.cq.dam.commons.util.DynamicMediaServicesConfigUtil,
                    com.day.cq.i18n.I18n,
                    com.day.cq.wcm.api.WCMMode,
                    com.day.cq.wcm.api.components.DropTarget,
                    com.day.cq.wcm.foundation.Placeholder,
                    com.day.text.Text,
                    org.apache.sling.api.resource.PersistableValueMap,
                    javax.jcr.Node,
                    javax.jcr.RepositoryException,
					javax.jcr.Session,
                  	java.util.Locale,
					org.apache.commons.lang.StringUtils,
                  	java.util.ResourceBundle"
        %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
    boolean isPreview = ((WCMMode.fromRequest(request) == WCMMode.EDIT)
            || (WCMMode.fromRequest(request) == WCMMode.DESIGN));

    Locale pageLocale = currentPage.getLanguage(true);
    ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
    I18n i18n = new I18n(resourceBundle);

    //Get settings
    String width = properties.get("width","-1");
    String height = properties.get("height","-1");
    String viewerPreset = properties.get("s7ViewerPreset", String.class);
    String fileReference = properties.get("fileReference",String.class);	
    String stageSize = "";
    String productionImageServerUrl = properties.get("imageserverurl",String.class);
    String imageServerUrl = request.getContextPath() + "/is/image/";	
    String productionVideoServerUrl = properties.get("videoserverurl",String.class);
    String videoServerUrl = "";
	String caption ="";

    // According to the documentation, on the publish instance the normal WCM capabilities are disabled.
    // We only want to use the published content when running on the publish node(s), otherwise we will
    // be referencing content that might not have been published yet.
    if (WCMMode.fromRequest(request) != WCMMode.DISABLED) {
        // Image Server URL
        PublishUtils publishUtils = sling.getService(PublishUtils.class);
        productionImageServerUrl = publishUtils.externalizeImageDeliveryUrl(resource, imageServerUrl);

        //Video Server URL
        String videoProxyServlet = DynamicMediaServicesConfigUtil.getServiceUrl(resource.getResourceResolver());
        String videoRegistrationId = DynamicMediaServicesConfigUtil.getRegistrationId(resource.getResourceResolver());
        if (videoRegistrationId != null && videoRegistrationId.contains("|")){
            videoRegistrationId = videoRegistrationId.substring(0, videoRegistrationId.indexOf("|"));
        }
        String videoPublicKey = DynamicMediaServicesConfigUtil.getPublicKey(resource.getResourceResolver());
        String previewVideoProxyUrl = "";
        String productionVideoProxyUrl = "";
        if (videoProxyServlet != null) {
            if (!videoProxyServlet.endsWith("/")) {
                //add trailing /
                videoProxyServlet += "/";
            }
            if (videoRegistrationId != null) {
                previewVideoProxyUrl = videoProxyServlet + "private/" + videoRegistrationId;
            }
            if (videoPublicKey != null) {
                productionVideoProxyUrl = videoProxyServlet + "public/" + videoPublicKey;
            }
        }

        // Always use the preview view proxy when in preview mode
        videoServerUrl = previewVideoProxyUrl;

        // Save the production image delivery and video proxy to be used in publish instance
        try {
            PersistableValueMap props = resource.adaptTo(PersistableValueMap.class);
            props.put("imageserverurl", productionImageServerUrl);
            props.put("videoserverurl", productionVideoProxyUrl);
            props.save();
        } catch (Exception e) {
            log.error("Unable to save imageserverurl and videoserverurl", e);
        }
    } else {
        // publish instance is using imageserverurl and videoserverurl that we store during authoring
        imageServerUrl = productionImageServerUrl;
        videoServerUrl = productionVideoServerUrl;
    }

    // Derive stage size
    if (Integer.parseInt(width) > 0 && Integer.parseInt(height) > 0) {
        stageSize = width + "," + height;
    }

    //Get viewer path
    Session currentSession = resourceResolver.adaptTo(Session.class);
    Node root = currentSession.getRootNode();
    String VIEWER_CONFIG_NODE = "etc/dam/viewers/default/jcr:content";
    String VIEWER_JS_FOLDER = "/html5/js/";
    String s7viewerRootPath = "/etc/dam/viewers/isv/";
    String s7viewerVersion = "5.0.1";
    String s7viewerPath = "";
    String s7sdkRootPath = "/etc/dam/viewers/sdk/";
    String s7sdkVersion = "2.7.1";
    String s7sdkPath = s7sdkRootPath + "2.7.1/js/";
    String contextPath = request.getContextPath();

    //Grab viewer version and root path from viewers settting
    if(root.hasNode(VIEWER_CONFIG_NODE)) {
        Node viewerNode = root.getNode(VIEWER_CONFIG_NODE);
        String viewerKey = "html5.video";
        try {
            if (viewerNode.hasProperty("sdkVersion")){
                s7sdkVersion = viewerNode.getProperty("sdkVersion").getString();
            }
            if (viewerNode.hasProperty("sdkRootPath")) {
                s7sdkRootPath = viewerNode.getProperty("sdkRootPath").getString();
            }
            if (viewerNode.hasProperty("viewerVersion")){
                s7viewerVersion = viewerNode.getProperty("viewerVersion").getString();
            }
            if (viewerNode.hasProperty("viewerRootPath")) {
                s7viewerRootPath = viewerNode.getProperty("viewerRootPath").getString();
            }
            s7sdkPath = contextPath + s7sdkRootPath + s7sdkVersion + "/js/";
            s7viewerPath = contextPath + s7viewerRootPath + s7viewerVersion + VIEWER_JS_FOLDER;
            if(viewerNode.hasProperty(viewerKey)){
                s7viewerPath = s7viewerPath + viewerNode.getProperty(viewerKey).getString();
            }

        } catch (RepositoryException rex){
        }

    }

    //Get asset information
    String assetType = null;
	String serverCaptionUrl = null;
	String captionUrl = null;
    boolean isVideo = false;
    String embeddedCode = "";
    String dmType = "";
    if (fileReference != null) {
        Resource assetResource = resourceResolver.getResource(fileReference);
        if (assetResource != null) {
            Asset asset = assetResource.adaptTo(Asset.class);
            Node assetNode = assetResource.adaptTo(Node.class);			
            if (assetNode.hasProperty("jcr:content/dam:s7damType")) {
                dmType = assetNode.getProperty("jcr:content/dam:s7damType").getString();
                assetType = asset.getMimeType();				
                isVideo = (assetType.startsWith("video"));
            }			
                assetType = asset.getMetadataValue("dc:caption");
				serverCaptionUrl= StringUtils.replace(imageServerUrl, "/is/image/","/is/content");	
				captionUrl=serverCaptionUrl+assetType;			
				if (assetType != null) {
				caption=captionUrl+",1";
				}
				log.info("caption"+caption);	
        }
    }
	

    String idPrefix = JcrUtil.createValidName(resource.getPath());
    String componentId = xssAPI.encodeForHTMLAttr(idPrefix);
    componentId = componentId + Long.toString(System.currentTimeMillis());
    componentId = componentId.replaceAll("-","_");
    String viewerContainerId = componentId + "s7video_div";
    String viewerInstanceId = componentId + "s7videoviewer";
%>
<%-- Due to JS loading issue, we have to load viewer as soon as the component is added to the page --%>
<% if (s7viewerPath != null && s7sdkPath != null) { %>
<script type="text/javascript" src="<%= xssAPI.getValidHref(s7sdkPath)%>s7sdk/utils/Utils.js"></script>
<script type="text/javascript" src="<%= xssAPI.getValidHref(s7viewerPath)%>"></script>
<% } %>

<% if (isPreview) { %>
<cq:includeClientLib categories="cq.dam.scene7.dynamicvideo" />
<% } %>
<% if (isVideo && s7viewerPath != null) { %>
<style type="text/css">
    <%=viewerContainerId%>.s7videoviewer {
        width: 100%;
        height: auto;
    }
</style>
<div class="<%= DropTarget.CSS_CLASS_PREFIX + "video"%>" style="width:100%;">
    <div id="<%=viewerContainerId%>" style="width:100%;"></div>
</div>
<script type="text/javascript">
    var <%=viewerInstanceId%> = new s7viewers.VideoViewer({
        "containerId" : "<%=viewerContainerId%>",
        "params" : {
            "serverurl" : "<%= xssAPI.getValidHref(imageServerUrl)%>",
            "contenturl" : "<%=request.getContextPath()%>/",
            "posterimage" : "<%= xssAPI.getValidHref(fileReference)%>",
			<% if (caption != null && !caption.isEmpty()) { %>
            "caption" : "<%= xssAPI.getValidHref(caption)%>",
            <% } %>
            <% if (!stageSize.isEmpty()) { %>
            "stagesize" : "<%= xssAPI.encodeForJSString(stageSize)%>",
            <% } %>
            <% if (viewerPreset != null && !viewerPreset.isEmpty()) {%>
            "config" : "<%= xssAPI.getValidHref(viewerPreset)%>",
            <% } else { %>
            "config" : "/etc/dam/presets/viewer/Video_social",
            <% } %>
            <% if (videoServerUrl != null && !videoServerUrl.isEmpty()) { %>
            "videoserverurl" : "<%= xssAPI.getValidHref(videoServerUrl)%>",
            <% } %>
            <% if (dmType.equalsIgnoreCase("Video")) { /* for non-AVS case, native playback only*/%>
            "playback" : "native",
            <% } %>
            "asset" : "<%=xssAPI.getValidHref(fileReference)%>"},
		"handlers":{
			"trackEvent":function(objID, compClass, instName, timeStamp, eventInfo) {
				var eventType = eventInfo.split(",")[0];
				if(eventType=="PLAY")
				{
					if(track_video)
					{
						playOnevideo();
					}
				}
			}
		}
    }).init();
</script>
<% } else if (isPreview) {
    String classicPlaceholder = "<div class=\"" + DropTarget.CSS_CLASS_PREFIX
            + "video" + (WCMMode.fromRequest(request) == WCMMode.EDIT ? " cq-video-placeholder" : "")
            + "\"></div>";
    String placeholder = Placeholder.getDefaultPlaceholder(slingRequest, component,
            classicPlaceholder, "cq-dd-video");

%><%=placeholder%><% } %>
