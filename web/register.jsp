<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - Review Advisor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        .register-container { margin-top: 50px; max-width: 500px; margin-bottom: 50px; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold text-success" href="MainController">TRIP ADVISOR</a>
    </div>
</nav>

<div class="container register-container">
    <div class="card shadow border-0">
        <div class="card-body p-5">
            <h3 class="text-center mb-4 text-success fw-bold">Create an Account</h3>
            
            <c:if test="${not empty requestScope.ERROR}">
                <div class="alert alert-danger small">${requestScope.ERROR}</div>
            </c:if>

            <form action="RegisterServlet" method="POST" id="registerForm">
                
                <div class="mb-3">
                    <label class="form-label small fw-bold">Full Name</label>
                    <input type="text" name="txtFullName" class="form-control" required placeholder="John Doe">
                </div>

                <div class="mb-3">
                    <label class="form-label small fw-bold">Email Address</label>
                    <input type="email" name="txtEmail" class="form-control" required placeholder="name@example.com">
                </div>

                <div class="mb-3">
                    <label class="form-label small fw-bold">Username</label>
                    <input type="text" name="txtUsername" class="form-control" required placeholder="Choose a username...">
                </div>
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label small fw-bold">Password</label>
                        <input type="password" id="password" name="txtPassword" class="form-control" required placeholder="••••••••">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label small fw-bold">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="txtConfirmPassword" class="form-control" required placeholder="••••••••">
                        <div id="passwordError" class="text-danger small mt-1 d-none">Passwords do not match!</div>
                    </div>
                </div>

                <div class="form-check mb-4 small text-muted">
                    <input class="form-check-input" type="checkbox" value="" id="termsCheck" required>
                    <label class="form-check-label" for="termsCheck">
                        I agree to the <a href="#" class="text-success text-decoration-none">Terms of Service</a> and <a href="#" class="text-success text-decoration-none">Privacy Policy</a>.
                    </label>
                </div>

                <button type="submit" class="btn btn-success w-100 py-2 fw-bold">Sign Up</button>
            </form>
            
            <div class="mt-4 text-center">
                <span class="text-muted small">Already have an account? </span>
                <a href="login.jsp" class="text-success small fw-bold text-decoration-none">Log in here</a>
            </div>
        </div>
    </div>
</div>

<script>
    const form = document.getElementById('registerForm');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const errorMsg = document.getElementById('passwordError');

    form.addEventListener('submit', function(event) {
        if (password.value !== confirmPassword.value) {
            event.preventDefault(); // Ngăn không cho form gửi đi
            errorMsg.classList.remove('d-none'); // Hiện thông báo lỗi
            confirmPassword.classList.add('is-invalid'); // Đổi màu viền input thành đỏ
        } else {
            errorMsg.classList.add('d-none');
            confirmPassword.classList.remove('is-invalid');
        }
    });
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>