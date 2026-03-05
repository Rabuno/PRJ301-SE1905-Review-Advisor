<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - Review Advisor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        .login-container { margin-top: 100px; max-width: 400px; }
    </style>
</head>
<body>
<div class="container login-container">
    <div class="card shadow border-0">
        <div class="card-body p-4">
            <h3 class="text-center mb-4 text-success fw-bold">Welcome Back</h3>
            
            <c:if test="${not empty requestScope.ERROR}">
                <div class="alert alert-danger small">${requestScope.ERROR}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/LoginServlet" method="POST">
                <div class="mb-3">
                    <label class="form-label small fw-bold">Username</label>
                    <input type="text" name="txtUsername" class="form-control" required placeholder="Enter username...">
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-bold">Password</label>
                    <input type="password" name="txtPassword" class="form-control" required placeholder="Enter password...">
                </div>
                <button type="submit" class="btn btn-success w-100 py-2">Sign In</button>
            </form>
            <div class="mt-3 text-center">
                <a href="${pageContext.request.contextPath}/MainController" class="text-muted small text-decoration-none">&larr; Back to Home</a>
            </div>
             <div class="mt-3 text-center">
                <span class="text-muted small">Don't have an account? </span>
                <a href="${pageContext.request.contextPath}/register.jsp" class="text-success small fw-bold text-decoration-none">Register</a>
            </div>    
        </div>
    </div>
</div>
</body>
</html>