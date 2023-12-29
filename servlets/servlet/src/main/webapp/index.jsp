<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "https://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<script>
//when the webpage is loaded
jQuery(document).ready(function($) {
$("form").submit(function () {
    console.log("Disabling submits");
    // prevent duplicate form submissions
    $(this).find(":submit").attr('disabled', 'disabled');
});
});

</script>


<title>First JSP</title>
</head>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Enumeration" %>
<body>
<h3>Hi (L)user</h3><br>
<strong>Current Time is</strong>: <%=new Date() %>
<%
Enumeration keys = session.getAttributeNames();
while (keys.hasMoreElements())
{
  String key = (String)keys.nextElement();
  out.println(key + ": " + session.getValue(key) + "<br>");
}
%>
<form action="/servlet/Inc" method="POST">
  <input id="incsubmit" type="submit" value="Increment">
</form>
</body>
</html>