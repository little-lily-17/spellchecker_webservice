<%-- 
    Document   : dashboard
    Created on : Apr 14, 2017, 3:16:49 PM
    Author     : nurlailifajriyah
--%>

<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype")%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> <html> 
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1256"> 
        <title>Check Your Spelling! Dashboard</title>
        <!--CSS for table-->
        <!--https://www.w3schools.com/html/html_tables.asp-->
        <style>            
            table {
                font-family: arial, sans-serif;
                border-collapse: collapse;
                width: 100%;
            }

            td, th {
                border: 1px solid #dddddd;
                text-align: left;
                padding: 8px;
            }

            tr:nth-child(even) {
                background-color: #dddddd;
            }
        </style>
    </head> 
    <body> 
        <center><h1>Check Your Spelling! Dashboard</h1></center>
        <h2>Top 5 OS Version</h2>
        <table>
            <tr>
                <th>OS Version</th>

                <th>Total</th>
            </tr>
            <%
                ArrayList<JSONObject> topOsResult = (ArrayList<JSONObject>) request.getAttribute("topOsResult");
                int i = 0;
                //if no data in database, print "No Data Available"
                if (topOsResult.size() == 0) {
            %>
            <tr><td>No Data Available</td><td></td></tr>

            <%
                //else, print id and count
            } else {
                while (i < topOsResult.size()) {%>
            <tr>
                <td><%= topOsResult.get(i).isNull("_id") ? "(unknown)" : topOsResult.get(i).get("_id") + ""%> </td>
                <td><%= topOsResult.get(i).isNull("count") ? "(unknown)" : topOsResult.get(i).getInt("count") + ""%> </td>
            </tr>
            <%i++;
                    }
                }%>
        </table>
        <br><br><br>
        <h2>Top 5 Device Model</h2>
        <table>
            <tr>
                <th>Brand</th>
                <th>Type</th>
                <th>Total</th>
            </tr>
            <%
                ArrayList<JSONObject> topDeviceResult = (ArrayList<JSONObject>) request.getAttribute("topDeviceResult");
                int k = 0;
                //if no data in database, print "No Data Available"
                if (topDeviceResult.size() == 0) {
            %>
            <tr><td>No Data Available</td><td></td><td></td></tr>

            <%
                //else, print manufacture, model, and count
            } else {
                while (k < topDeviceResult.size()) {%>
            <tr>
                <td><%= topDeviceResult.get(k).isNull("manufacture") ? "(unknown)" : topDeviceResult.get(k).getString("manufacture") + ""%> </td>
                <td><%= topDeviceResult.get(k).isNull("model") ? "(unknown)" : topDeviceResult.get(k).getString("model") + ""%> </td>     
                <td><%= topDeviceResult.get(k).isNull("count") ? "(unknown)" : topDeviceResult.get(k).getInt("count") + ""%> </td>
            </tr>
            <%k++;
                    }
                }%>
        </table>
        <br><br><br>
        <h2>5 Slowest Processing Time (milliseconds)</h2>
        <table>
            <tr>
                <th>ID</th>
                <th>Processing Time</th>

            </tr>
            <%
                ArrayList<JSONObject> processingTime = (ArrayList<JSONObject>) request.getAttribute("processingTime");
                int l = 0;
                //if no data in database, print "No Data Available"
                if (processingTime.size() == 0) {
            %>
            <tr><td>No Data Available</td><td></td></tr>

            <%
                //else, print id and processingtime
            } else {
                while (l < processingTime.size() && l < 5) {%>
            <tr>
                <td><%= processingTime.get(l).isNull("id") ? "(unknown)" : processingTime.get(l).get("id") + ""%> </td>
                <td><%= processingTime.get(l).isNull("processingtime") ? "(unknown)" : processingTime.get(l).getString("processingtime") + ""%> </td>     
            </tr>
            <%l++;
                    }
                }%>
        </table>
        <br><br><br>
        <h2>5 Highest Latency(milliseconds)</h2>
        <table>
            <tr>
                <th>ID</th>
                <th>API Latency (millisecond)</th>

            </tr>
            <%
                ArrayList<JSONObject> apiLatency = (ArrayList<JSONObject>) request.getAttribute("apilatency");
                int m = 0;
                //if no data in database, print "No Data Available"
                if (apiLatency.size() == 0) {
            %>
            <tr><td>No Data Available</td><td></td></tr>

            <%
            } else {
                //else, print id and latency
                while (m < apiLatency.size() && m < 5) {%>
            <tr>
                <td><%= apiLatency.get(m).isNull("id") ? "(unknown)" : apiLatency.get(m).get("id") + ""%> </td>
                <td><%= apiLatency.get(m).isNull("apilatency") ? "(unknown)" : apiLatency.get(m).getString("apilatency") + ""%> </td>     
            </tr>
            <%m++;
                    }
                }%>
        </table>
        <br><br><br>
        <h2>Apps Log</h2>
        <table>
            <tr>
                <th>ID</th>
                <th>Device Brand</th>
                <th>Device Type</th> 
                <th>OS Version</th>
                <th>Client Input</th>
                <th>Client Output</th>
                <th>API Latency (milliseconds)</th>
                <th>Request Time</th>
                <th>Response Time</th>
            </tr>
            <%
                ArrayList<JSONObject> result = (ArrayList<JSONObject>) request.getAttribute("logResult");
                int j = 0;
                //if no data in database, print "No Data Available"
                if (result.size() == 0) {
            %>
            <tr><td>No Data Available</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>

            <%
            } else {
                while (j < result.size()) {
                    //Date formationg for request and response time
                    Date reqTimeResult = null;
                    Date resTimeResult = null;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMMdd-HH:mm:ss.SSS");
                    if (!(result.get(j).isNull("requesttime"))) {
                        long reqTime = result.get(j).getLong("requesttime");
                        reqTimeResult = new Date(reqTime);
                    }

                    if (!(result.get(j).isNull("responsetime"))) {
                        long resTime = result.get(j).getLong("responsetime");
                        resTimeResult = new Date(resTime);
                    }
                    //Print all log data
            %>
            <tr>
                <td><%= result.get(j).isNull("_id") ? "(unknown)" : result.get(j).get("_id") + ""%> </td>
                <td><%= result.get(j).isNull("manufacture") ? "(unknown)" : result.get(j).getString("manufacture")%> </td>
                <td><%=  result.get(j).isNull("model") ? "(unknown)" : result.get(j).getString("model")%> </td>
                <td><%= result.get(j).isNull("osversion") ? "(unknown)" : result.get(j).getString("osversion")%> </td>
                <td><%=  result.get(j).isNull("input") ? "(unknown)" : result.get(j).getString("input")%> </td>
                <td><%=  result.get(j).isNull("output") ? "(unknown)" : result.get(j).getString("output")%></td>
                <td><%=  result.get(j).isNull("apilatency") ? "(unknown)" : result.get(j).get("apilatency") + ""%></td>
                <td><%=  reqTimeResult == null ? "(unknown)" : sdf.format(reqTimeResult) + ""%></td>
                <td><%=  resTimeResult == null ? "(unknown)" : sdf.format(resTimeResult) + ""%></td>
            </tr>
            <%j++;
                    }
                }%>
        </table>

    </body>
</html>
