<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - Review Advisor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="bg-light d-flex flex-column min-vh-100">
    
    <jsp:include page="/common/header.jsp" />

    <div class="container mt-5 mb-5 flex-grow-1">
        <div class="row justify-content-center">
            <div class="col-md-5">
                <div class="card shadow-lg border-0 rounded-3 p-4">
                    <h2 class="text-center fw-bold text-primary mb-4">Welcome</h2>
                    
                    
                    <c:if test="${not empty requestScope.SUCCESS_MSG and requestScope.SUCCESS_MSG != ''}">
                        <div class="alert alert-success shadow-sm text-center fw-bold" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>${requestScope.SUCCESS_MSG}
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty requestScope.ERROR and requestScope.ERROR != ''}">
                        <div class="alert alert-danger shadow-sm text-center fw-bold" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.ERROR}
                        </div>
                    </c:if>
                    
                    <form action="${pageContext.request.contextPath}/LoginServlet" method="POST">
                        <div class="mb-3">
                            <label class="form-label fw-bold">Username</label>
                            <input type="text" name="txtUsername" class="form-control" placeholder="Enter your username" required>
                        </div>
                        
                        <div class="mb-4">
                            <div class="d-flex justify-content-between">
                                <label class="form-label fw-bold">Password</label>
                                <a href="#" class="text-decoration-none small text-primary">Forgot password?</a>
                            </div>
                            <div class="input-group">
                                <input type="password" id="txtPassword" name="txtPassword" class="form-control border-end-0" placeholder="Enter your password" required>
                                <button class="btn btn-outline-secondary border-start-0 border bg-white" type="button" id="togglePassword">
                                    <i class="bi bi-eye-slash" id="eyeIcon"></i>
                                </button>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2 fw-bold fs-5 mb-3">Login</button>
                        
                        <div class="text-center">
                            <span class="text-muted">Don't have an account?</span> 
                            <a href="${pageContext.request.contextPath}/register.jsp" class="text-decoration-none fw-bold">Register here</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.getElementById('togglePassword').addEventListener('click', function () {
            const passwordInput = document.getElementById('txtPassword');
            const eyeIcon = document.getElementById('eyeIcon');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                eyeIcon.classList.remove('bi-eye-slash');
                eyeIcon.classList.add('bi-eye');
            } else {
                passwordInput.type = 'password';
                eyeIcon.classList.remove('bi-eye');
                eyeIcon.classList.add('bi-eye-slash');
            }
        });
    </script>
    
    <jsp:include page="/common/footer.jsp" />
</body>
</html>