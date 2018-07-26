<%--
###########################################################################################
# DESCRIPTION: To get reference on page. Extends 'foundation/components/reference'
#              
# AUTHOR: SRIRAM NARASIMHAN(HCL)
# ENVIRONMENT : AEM 5.6.1
#
# INTERFACE
# COMPONENT REQUIREMENTS:
#   FUNCTIONAL REQUIREMENTS:
#       1) To get parsys on page.
#    
#   
#   AUTHOR INPUT REQUIREMENTS
#       1) NA   
#
# UPDATE HISTORY
# VERSION DEVELOPER                 DATE         COMMENTS
# 1.0     SRIRAM NARASIMHAN(HCL)    05-07-2014   CREATED FILE 
# 
###########################################################################################    
--%>
<jsp:directive.include file="/apps/myproject/global.jsp" />
<jsp:directive.page session="false" import="com.day.cq.wcm.foundation.Image,org.apache.commons.lang3.StringUtils" />

<cq:includeClientLib categories="new.compositelist" />

<jsp:scriptlet>
    pageContext.setAttribute("class",properties.get("layoutclass",""));
    pageContext.setAttribute("id",properties.get("layoutid",""));
    pageContext.setAttribute("fileReference",properties.get("fileReference",""));
    Image image = new Image(resource);
    String imageName=StringUtils.isEmpty(image.getTitle())?image.getName():image.getTitle();
    pageContext.setAttribute("imageName",imageName);
    pageContext.setAttribute("title",properties.get("title",""));
    pageContext.setAttribute("imageAlt",properties.get("imagealt",""));
</jsp:scriptlet>

<c:set var="imageAlt" value="${properties.imagealt}" />
<c:set var="imageList" value="${properties.imagelist}" />


<c:forEach var="items" items="${imageList}">
    <c:set var="listItem" value="${fn:split(items,'|')}" />
    <div class= "text">
        <c:out value="${listItem[0]}" />
    </div>
    <div class= "largeImage">
        <img src="${listItem[1]}" alt ="${imageAlt}" title="${imageName}"/>
    </div>
    <div class= "mediumImage">
        <c:out value="${listItem[2]}" />
    </div>
    <div class= "smallImage">
        <c:out value="${listItem[3]}" />
    </div>
    <div class= "link">
        <c:out value="${listItem[4]}" />
    </div>
    <div class= "style">
        <c:out value="${listItem[5]}" />
    </div>
</c:forEach>
