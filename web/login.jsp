<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập - Review Advisor</title>
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
            <h3 class="text-center mb-4 text-success fw-bold">Đăng Nhập</h3>
            
            <c:if test="${not empty requestScope.ERROR}">
                <div class="alert alert-danger small">${requestScope.ERROR}</div>
            </c:if>

            <form action="LoginServlet" method="POST">
                <div class="mb-3">
                    <label class="form-label small fw-bold">Tên đăng nhập</label>
                    <input type="text" name="txtUsername" class="form-control" required placeholder="Nhập username...">
                </div>
                <div class="mb-3">
                    <label class="form-label small fw-bold">Mật khẩu</label>
                    <input type="password" name="txtPassword" class="form-control" required placeholder="Nhập password...">
                </div>
                <button type="submit" class="btn btn-success w-100 py-2">Vào hệ thống</button>
            </form>
            <div class="mt-3 text-center">
                <a href="MainController" class="text-muted small text-decoration-none">← Quay lại trang chủ</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>