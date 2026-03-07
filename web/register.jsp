<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Register - Review Advisor</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
            <style>
                .strength-meter {
                    height: 5px;
                    background-color: #e9ecef;
                    border-radius: 3px;
                    margin-top: 5px;
                    transition: all 0.3s;
                }

                .strength-weak {
                    width: 33%;
                    background-color: #dc3545;
                }

                .strength-medium {
                    width: 66%;
                    background-color: #ffc107;
                }

                .strength-strong {
                    width: 100%;
                    background-color: #198754;
                }
            </style>
        </head>

        <body class="bg-light d-flex flex-column min-vh-100">
            <jsp:include page="/common/header.jsp" />

            <div class="container mt-5 mb-5 flex-grow-1">
                <div class="row justify-content-center">
                    <div class="col-md-5">
                        <div class="card shadow-lg border-0 rounded-3 p-4">
                            <h2 class="text-center fw-bold text-success mb-4">Create an Account</h2>

                            <c:if test="${not empty requestScope.ERROR and requestScope.ERROR != ''}">
                                <div class="alert alert-danger shadow-sm text-center fw-bold" role="alert">
                                    <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.ERROR}
                                </div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/RegisterServlet" method="POST"
                                id="registerForm">
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Username</label>
                                    <input type="text" name="txtUsername" class="form-control" required>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Password</label>
                                    <div class="input-group">
                                        <input type="password" id="txtPassword" name="txtPassword"
                                            class="form-control border-end-0" required>
                                        <button class="btn btn-outline-secondary border-start-0 border bg-white"
                                            type="button" onclick="toggleVis('txtPassword', 'eye1')">
                                            <i class="bi bi-eye-slash" id="eye1"></i>
                                        </button>
                                    </div>
                                    <div id="strengthMeter" class="strength-meter"></div>
                                    <small id="strengthText" class="text-muted mt-1 d-block">Enter a password to see its
                                        strength.</small>
                                </div>

                                <div class="mb-4">
                                    <label class="form-label fw-bold">Confirm Password</label>
                                    <div class="input-group">
                                        <input type="password" id="txtConfirm" name="txtConfirmPassword"
                                            class="form-control border-end-0" required>
                                        <button class="btn btn-outline-secondary border-start-0 border bg-white"
                                            type="button" onclick="toggleVis('txtConfirm', 'eye2')">
                                            <i class="bi bi-eye-slash" id="eye2"></i>
                                        </button>
                                    </div>
                                    <small id="matchMessage" class="mt-1 d-block"></small>
                                </div>

                                <div class="mb-4 p-3 bg-light border border-secondary-subtle rounded">
                                    <div class="form-check ms-1">
                                        <input class="form-check-input border-primary shadow-sm" type="checkbox"
                                            id="roleMerchant" name="roleType" value="merchant"
                                            style="transform: scale(1.3); cursor: pointer; margin-top: 0.3rem;">
                                        <label class="form-check-label fw-bold text-primary ms-2" for="roleMerchant"
                                            style="cursor: pointer;">
                                            Register as a Merchant
                                        </label>
                                        <small class="d-block text-muted mt-1">Check this box if you want to manage
                                            properties and reply to customer reviews.</small>
                                    </div>
                                </div>

                                <div class="mb-4 p-3 bg-light border border-secondary-subtle rounded">
                                    <div class="form-check ms-1">
                                        <input class="form-check-input border-primary shadow-sm" type="checkbox"
                                            id="roleMerchant" name="roleType" value="merchant"
                                            style="transform: scale(1.3); cursor: pointer; margin-top: 0.3rem;">
                                        <label class="form-check-label fw-bold text-primary ms-2" for="roleMerchant"
                                            style="cursor: pointer;">
                                            Register as a Merchant
                                        </label>
                                        <small class="d-block text-muted mt-1">Check this box if you want to manage
                                            properties and reply to customer reviews.</small>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-success w-100 py-2 fw-bold fs-5 mb-3"
                                    id="btnRegister" disabled>Register</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                const pwdInput = document.getElementById('txtPassword');
                const confirmInput = document.getElementById('txtConfirm');
                const strengthMeter = document.getElementById('strengthMeter');
                const strengthText = document.getElementById('strengthText');
                const matchMessage = document.getElementById('matchMessage');
                const btnRegister = document.getElementById('btnRegister');

                function toggleVis(inputId, iconId) {
                    const input = document.getElementById(inputId);
                    const icon = document.getElementById(iconId);
                    if (input.type === 'password') {
                        input.type = 'text';
                        icon.className = 'bi bi-eye';
                    } else {
                        input.type = 'password';
                        icon.className = 'bi bi-eye-slash';
                    }
                }

                function validateForm() {
                    const val = pwdInput.value;
                    const confirmVal = confirmInput.value;
                    let strength = 0;
                    let isValid = true;

                    if (val.length >= 6) strength += 1;
                    if (val.match(/(?=.*[0-9])/)) strength += 1;
                    if (val.match(/(?=.*[!@#\$%\^&\*])/)) strength += 1;

                    strengthMeter.className = 'strength-meter';
                    if (val.length === 0) {
                        strengthText.innerText = 'Enter a password to see its strength.';
                        isValid = false;
                    } else if (strength === 1) {
                        strengthMeter.classList.add('strength-weak');
                        strengthText.innerText = 'Weak (Needs numbers or special characters)';
                        strengthText.className = 'text-danger mt-1 d-block small';
                    } else if (strength === 2) {
                        strengthMeter.classList.add('strength-medium');
                        strengthText.innerText = 'Medium (Good, but can be better)';
                        strengthText.className = 'text-warning mt-1 d-block small';
                    } else if (strength >= 3) {
                        strengthMeter.classList.add('strength-strong');
                        strengthText.innerText = 'Strong (Excellent!)';
                        strengthText.className = 'text-success mt-1 d-block small';
                    }

                    if (confirmVal.length > 0) {
                        if (val === confirmVal) {
                            matchMessage.innerText = 'Passwords match!';
                            matchMessage.className = 'text-success mt-1 d-block small';
                        } else {
                            matchMessage.innerText = 'Passwords do not match!';
                            matchMessage.className = 'text-danger mt-1 d-block small';
                            isValid = false;
                        }
                    } else {
                        matchMessage.innerText = '';
                        isValid = false;
                    }

                    btnRegister.disabled = !isValid;
                }

                pwdInput.addEventListener('input', validateForm);
                confirmInput.addEventListener('input', validateForm);
            </script>

            <jsp:include page="/common/footer.jsp" />
        </body>

        </html>