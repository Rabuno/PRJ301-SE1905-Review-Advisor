<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Access Denied</title>
</head>
<body>
    <h2>Access Denied</h2>
    <p>Bạn không có quyền truy cập chức năng này.</p>
    <a href="<%=request.getContextPath()%>/login.jsp">Quay lại Login</a>
</body>
</html>