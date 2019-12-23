<%--
  Created by IntelliJ IDEA.
  User: n2god
  Date: 23/12/2019
  Time: 18:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8"
         language="java"
         pageEncoding="UTF-8"
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" charset="UTF-8" content="text/html">
    <title>Witaj</title>
</head>
<body>
<h1>
    Witaj <%= session.getAttribute("username")%>
</h1>
<h2>
    Twoje uprawnienia: <%=session.getAttribute("privigiles")%>
</h2>
</body>
</html>
