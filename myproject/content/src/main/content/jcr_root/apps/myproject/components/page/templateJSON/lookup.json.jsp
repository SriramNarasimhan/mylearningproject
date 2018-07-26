<%@include file="/apps/myproject/global.jsp"%>

<%@ page import="org.apache.sling.commons.json.io.*,com.cdyne.ws.weatherws.*" %><%
String zip = request.getParameter("zip");
Weather ww = new com.cdyne.ws.weatherws.Weather();
WeatherSoap ws = ww.getWeatherSoap();
WeatherReturn wr = ws.getCityWeatherByZIP(zip);
 
 
JSONWriter writer = new JSONWriter(response.getWriter());
writer.object();
writer.key("zip");
writer.value(zip);
 
writer.key("city");
writer.value(wr.getCity());
 
writer.key("state");
writer.value(wr.getState());
 
writer.key("description");
writer.value(wr.getDescription());
 
writer.key("wind");
writer.value(wr.getWind());
 
writer.key("temperature");
writer.value(wr.getTemperature());
 
writer.key("humidity");
writer.value(wr.getRelativeHumidity());
writer.endObject();
%>