
<%@include file="/apps/myproject/global.jsp"%>

<cq:includeClientLib categories="jquerysamples" />
<html>
<head>
<meta charset="UTF-8">
<title>Adobe CQ Dynamic Web Service Weather Page</title>
<style>
#signup .indent label.error {
  margin-left: 0;
}
#signup label.error {
  font-size: 0.8em;
  color: #F00;
  font-weight: bold;
  display: block;
  margin-left: 215px;
}
#signup  input.error, #signup select.error  {
  background: #FFA9B8;
  border: 1px solid red;
}
</style>
<script>
$(document).ready(function() {
      
    $('body').hide().fadeIn(5000);
      
$('#submit').click(function() {
    var failure = function(err) {
         alert("Unable to retrive data "+err);
          
    };
      
      
    //Get the ZIP COde value to pass to the CQ Web Service
    var myZip = $('#mydropdown').val() ; 
      
    var url = location.pathname.replace(".html", "/_jcr_content.lookup.json") + "?zip="+myZip;
      
     
    $.ajax(url, {
        dataType: "text",
        success: function(rawData, status, xhr) {
            var data;
            try {
                data = $.parseJSON(rawData);
                  
                //Set the fields in the forum
                $('#city').val(data.city); 
                $('#state').val(data.state);
                $('#description').val(data.description);
                $('#wind').val(data.wind);
                $('#temp').val(data.temperature);
                $('#hum').val(data.humidity);
                } catch(err) {
                failure(err);
            }
        },
        error: function(xhr, status, err) {
            failure(err);
        } 
    });
  });
  
}); // end ready
</script>
</head>
<body>
    <div class="wrapper">
        <div class="header">
            <p class="logo">Adobe CQ Weather Page </p>
        </div>
        <div class="content">
            <div class="main">
                <h1>CQ Web Service Example</h1>
                <form name="signup" id="signup">
                    <table> 
                        
                        <tr>
                            <td> 
                                <label for="zip" class="label">Enter US Zip Code:</label>
                            </td> 
                            <td>    
                                <select name="mydropdown" id="mydropdown" style="width: 200px;" >
                                    <option value="90210">90210</option>
                                    <option value="95101">95101</option>
                                    <option value="94101">94101</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td> 
                                <label for="city" class="label">City</label>
                            </td>
                            <td>      
                                <input name="city" type="text" id="city" readonly="readonly">
                            </td> 
                        </tr>
                        <tr>
                            <td>
                                <label for="state" class="label">State</label>
                            </td> 
                            <td>  
                                <input name="state" type="text" id="state" readonly="readonly">
                            </td>
                        </tr>
                        <tr>
                            <td>             
                                <label for="description" class="label">Description</label>
                            </td>
                            <td>     
                                <input name="description" type="text" id="description" readonly="readonly">
                            </td> 
                        </tr>
                        <tr>
                            <td>
                                <label for="wind" class="label">Wind</label>
                            </td>
                            <td>     
                                <input name="wind" type="text" id="wind" readonly="readonly">
                            </td>
                        </tr>                
                        <tr>
                            <td>
                                <label for="temp" class="label">temperature</label>
                            </td>   
                            <td> 
                                <input name="temp" type="text" id="temp" readonly="readonly">
                            </td>
                        </tr>
                        <tr>
                            <td>
                                
                                <label for="hum" class="label">Humidity</label>
                            </td>
                            <td>
                                <input name="hum" type="text" id="hum" readonly="readonly">
                            </td>
                        </tr>  
                        <tr>
                            <td>
                            </td>
                            <td>
                                <input type="button" value="Get Weather!"  name="submit" id="submit" value="Submit">
                            </td>
                        </tr> 
                	</table>
                </form>
            </div>
        </div>
        
    </div>
</body>
</html>