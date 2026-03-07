<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Review Advisor - Trustworthy Reviews</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .navbar-brand { font-weight: bold; color: #34e0a1 !important; }
        .card-product:hover { transform: translateY(-5px); transition: 0.3s; cursor: pointer; }
        .verified-badge { background-color: #e8f5e9; color: #2e7d32; padding: 2px 8px; border-radius: 10px; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top mb-4">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/MainController">TRIP ADVISOR</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link fw-bold" href="${pageContext.request.contextPath}/MainController">Home</a>
                    </li>
                    
                    <c:if test="${not empty sessionScope.USER}">
                        <c:if test="${sessionScope.USER.role eq 'CUSTOMER' or sessionScope.USER.role eq 'Customer'}">
                            <li class="nav-item">
                                <a class="nav-link text-success fw-bold" href="${pageContext.request.contextPath}/views/customer/my-reviews.jsp">My Reviews</a>
                            </li>
                        </c:if>
                        
                        <c:if test="${sessionScope.USER.role eq 'MERCHANT' or sessionScope.USER.role eq 'Merchant'}">
                            <li class="nav-item">
                                <a class="nav-link text-primary fw-bold" href="${pageContext.request.contextPath}/views/merchant/merchant-dashboard.jsp">Merchant Dashboard</a>
                            </li>
                        </c:if>
                        
                        <c:if test="${sessionScope.USER.role eq 'MODERATOR' or sessionScope.USER.role eq 'Moderator'}">
                            <li class="nav-item">
                                <a class="nav-link text-warning fw-bold" href="${pageContext.request.contextPath}/views/moderation/dashboard.jsp">Moderation Alerts</a>
                            </li>
                        </c:if>

                        <c:if test="${sessionScope.USER.role eq 'AUDITOR' or sessionScope.USER.role eq 'Auditor'}">
                            <li class="nav-item">
                                <a class="nav-link text-info fw-bold" href="${pageContext.request.contextPath}/views/auditor/audit-logs.jsp">Audit Logs</a>
                            </li>
                        </c:if>
                        
                        <c:if test="${sessionScope.USER.role eq 'ADMIN' or sessionScope.USER.username eq 'admin'}">
                            <li class="nav-item"><a class="nav-link fw-bold text-primary" href="${pageContext.request.contextPath}/Admin/RolePermissions">Manage Roles</a></li>
                            <li class="nav-item"><a class="nav-link fw-bold text-primary" href="${pageContext.request.contextPath}/Admin/UserRoles">Manage Users</a></li>
                        </c:if>
                    </c:if>
                </ul>

                <div class="navbar-nav ms-auto align-items-center">
                    <c:choose>
                        <c:when test="${empty sessionScope.USER}">
                            <a class="nav-link btn btn-outline-primary px-4 mx-2" href="${pageContext.request.contextPath}/login.jsp">Login</a>
                            <a class="nav-link btn btn-outline-primary px-4 mx-2" href="${pageContext.request.contextPath}/register.jsp">Register</a>
                        </c:when>
                        <c:otherwise>
                            <span class="nav-link me-3">Welcome, <strong>${sessionScope.USER.username}</strong></span>
                            <a class="nav-link btn btn-sm btn-outline-danger px-3" href="${pageContext.request.contextPath}/LogoutController">Logout</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </nav>