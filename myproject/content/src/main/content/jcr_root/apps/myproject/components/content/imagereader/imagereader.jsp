<%--
###########################################################################################
# DESCRIPTION: To fetch images and its related metadata from chosen path in DAM asynchronously
#              
# AUTHOR: SRIRAM NARASIMHAN(CTS)
# ENVIRONMENT : AEM 5.6.1
#
# INTERFACE
# COMPONENT REQUIREMENTS:
#   FUNCTIONAL REQUIREMENTS:
#       1) To get images from dam.
#    
#   
#   AUTHOR INPUT REQUIREMENTS
#       1) NA   
#
# UPDATE HISTORY
# VERSION DEVELOPER                 DATE         COMMENTS
# 1.0     SRIRAM NARASIMHAN(CTS)    07-19-2014   CREATED FILE 
# 
###########################################################################################    
--%>
<jsp:directive.include file="/apps/myproject/global.jsp" />
<jsp:directive.page session="false" import="org.apache.sling.commons.json.io.*"/>

<jsp:scriptlet>
    pageContext.setAttribute("damPath",properties.get("damPath",""));
</jsp:scriptlet>

<input type="hidden" id="damPath" value="${properties.damPath}"/>

<div id="damimages">Response from servlet goes here(Few metadata properties displayed)</div>

<script>
$(document).ready(function(dialog){
        var dampath = $('#damPath').val();
        var response='';
        $.ajax({
            url:'/bin/imagereader',          
            type:"GET",
            cache:false,
            contentType: "application/json",
            dataType: "json",
            data:{"dampath":dampath}, //passing value(s) to servlet
            error: function(error) {
                alert("error"+error);
            },
            success: function(response) {
                var parsed = JSON.stringify(response);
                if($.trim(parsed).length < 3){
                    parsed = parsed.replace("{", "");
                    parsed = parsed.replace("}", "");
                }
 	          	if(parsed.length > 0){
	                var obj = JSON.parse(parsed);
	                if (obj != null && $.trim(obj) != '') {
	                   var count = response.image.length; 
		               for(var c=0;c<count;c++) {
		                   var imagelist = "<img " + "src='" + response.image[c] +".thumb.319.319.png"+"'/>";
							$('#damimages').append(imagelist );
		                   var Bitsperpixel = obj['dam:Bitsperpixel'][c];
		                   var Fileformat = obj['dam:Fileformat'][c];
		                   var mimeType = obj['dam:MIMEtype'][c];
	
		                   $('#damimages').append("dam:Bitsperpixel="+Bitsperpixel);
		                   $('#damimages').append("|dam:Fileformat="+Fileformat);
		                   $('#damimages').append("|dam:MIMEtype="+mimeType );
						   $('#damimages').append("<br/><br/>");
		               }
		            }
            	}
            }
        });
	});
</script>