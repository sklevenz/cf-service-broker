<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="com.klaeff.cf.BrokerServlet"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Cloud Foundry Broker</title>
</head>
<body>

	<h1>Cloud Foundry Broker</h1>

	<h2>Cloud Foundry service broker stub implementation. For testing
		purpose only.</h2>
	<hr>

	<ul>
		<li><a href="v2/catalog">catalog</a></li>
	</ul>
	<hr>

	<table border="1">
		<tr>
			<th>Instances Registry</th>
			<th>Bindings Registry</th>
		</tr>
		<tr>
			<td><textarea cols="50" rows="50">
					<%
						out.write(BrokerServlet.instanceRegistryAsJson());
					%>
				</textarea></td>
			<td><textarea cols="50" rows="50">
					<%
						out.write(BrokerServlet.bindingRegistryAsJson());
					%>
				</textarea></td>
		</tr>
	</table>


</body>
</html>