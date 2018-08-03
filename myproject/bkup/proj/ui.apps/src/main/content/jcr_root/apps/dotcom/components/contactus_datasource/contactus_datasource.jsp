<%@page session="false" import="
                  org.apache.sling.api.resource.Resource,
                  java.io.InputStream,
                  java.io.StringWriter,
                  org.apache.commons.io.IOUtils,
                  org.apache.sling.api.resource.ResourceUtil,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.List,
                  java.util.ArrayList,
                  java.util.HashMap,
                  java.util.Locale,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.EmptyDataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  com.day.cq.wcm.api.Page,
                  com.day.cq.wcm.api.PageManager,
                  javax.jcr.Node,
                  com.suntrust.dotcom.config.SuntrustDotcomService,
				  org.apache.sling.api.scripting.SlingBindings,
				  org.apache.sling.api.scripting.SlingScriptHelper"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/><%
request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());  
ResourceResolver resolver = resource.getResourceResolver();

//Create an ArrayList to hold data
List<Resource> ResourceList = new ArrayList<Resource>();
SuntrustDotcomService svc = new SuntrustDotcomService();
ValueMap vm = null; 
String content = "";

SlingBindings binder = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
SlingScriptHelper scriptHelper = binder.getSling();           
SuntrustDotcomService dotcomServiceconfig = scriptHelper.getService(SuntrustDotcomService.class);

String filePath = dotcomServiceconfig.getPropertyValue("live.engage.file.path")+"/jcr:content/renditions/original/jcr:content";

Resource res = resourceResolver.getResource(filePath);
if(res != null){
	Node engagementNode = res.adaptTo(Node.class);
	InputStream inputStream = engagementNode.getProperty("jcr:data").getBinary().getStream();
	StringWriter writer = new StringWriter();
	IOUtils.copy(inputStream, writer, "UTF-8");
	content = writer.toString();	
	String[] engagementIds = content.split(",");    
    for (String keyValues : engagementIds) {
    	 String key = (String) keyValues.split("=")[0];
		 String value = (String) keyValues.split("=")[1].trim();		
		 vm = new ValueMapDecorator(new HashMap<String, Object>());   		 		 		 
		 //populate the map
		 vm.put("text",key);
		 vm.put("value",value);		 		 
		 ResourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));
    }		
}
//Create a DataSource that is used to populate the drop-down control
DataSource ds = new SimpleDataSource(ResourceList.iterator());
request.setAttribute(DataSource.class.getName(), ds);
 
%>