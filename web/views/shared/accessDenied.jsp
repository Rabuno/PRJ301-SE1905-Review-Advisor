<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Access Denied - Review Advisor</title>
    <%@include file="../../common/resources.jsp" %>
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    <%@include file="../../common/header.jsp" %>

    <div class="container flex-grow-1 d-flex align-items-center justify-content-center">
        <div class="text-center" style="max-width: 520px;">
            <div class="display-4 text-danger mb-2">
                <i class="bi bi-shield-lock"></i>
            </div>
            <h2 class="fw-bold mb-2">Access Denied</h2>
            <p class="text-muted mb-4">Bạn không có quyền truy cập chức năng này.</p>
            <div class="d-flex gap-2 justify-content-center">
                <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/MainController">Go Home</a>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/login.jsp">Login</a>
            </div>
        </div>
    </div>

    <%@include file="../../common/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
