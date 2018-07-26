<%--

  Query component.


--%><%
%><%@include file="/apps/myproject/global.jsp"%>
<cq:includeClientLib categories="jquerysamples" />
<html>
<head>
<meta charset="UTF-8">
<title>Adobe CQ Persist Page</title>

<script>
$(document).ready(function() {
      
       var aDataSet = [
                       ['','','','',''],
                       ['','','','','']
                   ];
                
 
      $('#dynamic').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="example"></table>' );
      $('#example').dataTable( {
          "aaData": aDataSet,
          "aoColumns": [
              { "sTitle": "First Name" },
              { "sTitle": "Last Name" },
              { "sTitle": "Address", "sClass": "center" },
              { "sTitle": "Description", "sClass": "center" }
          ]
      } );
 
 
    $('body').hide().fadeIn(5000);
      
$('#submit').click(function() {
    var failure = function(err) {
      //  $(".main").unmask();
        alert("Unable to retrive data "+err);
          
    };
      
    //Get the user-defined values to persist in the database
    var myFirst= $('#first').val() ;
    var myLast= $('#last').val() ;
    var myDescription= $('#description').val() ;
    var myAddress= $('#address').val() ;
      
    var url = location.pathname.replace(".html", "/_jcr_content.persist.json") + "?first="+ myFirst +"&last="+myLast +"&desc="+myDescription +"&address="+myAddress;
      
    //$(".main").mask("Saving Data...");
  
    $.ajax(url, {
        dataType: "text",
        success: function(rawData, status, xhr) {
            var data;
            try {
                data = $.parseJSON(rawData);
                  
                //Set the fields in the forum
                $('#custId').val(data.pk);
             
            } catch(err) {
                failure(err);
            }
        },
        error: function(xhr, status, err) {
            failure(err);
        }
    });
  });
 
 
//Get customer data -- called when the submitget button is clicked
//this method populates the data grid with data retrieved from the //Adobe CQ JCR
$('#submitget').click(function() {
    var failure = function(err) {
          alert("Unable to retrive data "+err);
      };
       
    //Get the query filter value from drop down control
    var filter=   $('#custQuery').val() ;
        
    var url = location.pathname.replace(".html", "/_jcr_content.query.json") + "?filter="+ filter;
        
    $.ajax(url, {
        dataType: "text",
        success: function(rawData, status, xhr) {
            var data;
            try {
                data = $.parseJSON(rawData);
                  
                //Set the fields in the forum
                var myXML = data.xml;

                var loopIndex = 0;

                //Reference the data grid, clear it, and add new records
                //queried from the Adobe CQ JCR
                 var oTable = $('#example').dataTable();
                 oTable.fnClearTable(true);
 
                 //Loop through this function for each Customer element
                 //in the returned XML
                 $(myXML).find('Customer').each(function(){
                         
                    var $field = $(this);
                    var firstName = $field.find('First').text();
                     
                    var lastName = $field.find('Last').text();
                    var Description = $field.find('Description').text();
                    var Address = $field.find('Address').text();    
 
                    //Set the new data
                    oTable.fnAddData( [
                        firstName,
                        lastName,
                        Address,
                        Description,]
                    );
            
                    });
            
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
        <p class="logo">Adobe CQ JCR Customer Persist/Query Application</p>
    </div>
    <div class="content">
    <div class="main">
    <h1>CQ Data Persist Example</h1>
      
    <form name="signup" id="signup">
<table>
    <tr>
    <td>
    <label for="first">First Name:</label>
    </td>
     <td>
    <input type="first" id="first" name="first" value="" />
    </td>
    </tr>
    <tr>
    <td>
    <label for="last">Last Name:</label>
    </td>
     <td>
    <input type="last" id="last" name="last" value="" />
    </td>
    </tr>
     <tr>
    <td>
    <label for="address">Address:</label>
    </td>
     <td>
    <input type="address" id="address" name="address" value="" />
    </td>
    </tr>
     <tr>
    <td>
   <label for="description">Description:</label>
    </td>
    <td>
        <select id="description"  >
            <option>Active Customer</option>
            <option>Past Customer</option> 
        </select>
    </td>
    </tr>
     <tr>
    <td>
    <label for="custId">Customer Id:</label>
    </td>
     <td>
    <input type="custId" id="custId" name="custId" value="" readonly="readonly"/>
    </td>
    </tr>
</table>
        <div>
            <input type="button" value="Add Customer!"  name="submit" id="submit" value="Submit">
        </div>
    </form>
    </div>
</div>

    <div id="container">
     <form name="custdata" id="custdata">

    <h1>Query Customer Data from the AEM JCR</h1>
   <div>
     <select id="custQuery"  >
            <option>All Customers</option>
            <option>Active Customer</option>
            <option>Past Customer</option>   
        </select>
    </div>
    <div id="dynamic"></div>
    <div class="spacer"></div>

   <div>
      <input type="button" value="Get Customers!"  name="submitget" id="submitget" value="Submit">
    </div>
   </form>
      
</div>
</body>
</html>