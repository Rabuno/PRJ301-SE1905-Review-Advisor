<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
        .verified-badge { font-size: 0.75rem; background-color: #e8f5e9; color: #2e7d32; padding: 2px 8px; border-radius: 10px; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top mb-4">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/MainController">TRIP ADVISOR</a>
        <div class="navbar-nav ms-auto">
            <c:choose>
                <c:when test="${empty sessionScope.USER}">
                    <a class="nav-link btn btn-outline-primary px-4 mx-2" href="${pageContext.request.contextPath}/login.jsp">Login</a>
                </c:when>
                <c:otherwise>
                    <span class="nav-link">Welcome, <strong>${sessionScope.USER.username}</strong></span>
                    
                    <a class="nav-link text-success fw-bold mx-2" href="${pageContext.request.contextPath}/views/customer/my-reviews.jsp">My Reviews</a>
                    
                    <c:if test="${sessionScope.USER.hasPermission('PERM_MODERATE_ACTION')}">
                        <a class="nav-link text-danger fw-bold mx-2" href="${pageContext.request.contextPath}/ModeratorServlet">Moderation</a>
                    </c:if>

                    <c:if test="${sessionScope.USER.hasPermission('PERM_AUDIT_ACTION')}">
                        <a class="nav-link text-primary fw-bold mx-2" href="${pageContext.request.contextPath}/views/auditor/audit-logs.jsp">Audit Logs</a>
                    </c:if>

                    <c:if test="${sessionScope.USER.hasPermission('PERM_MERCHANT_ACTION')}">
                        <a class="nav-link text-warning fw-bold mx-2" href="${pageContext.request.contextPath}/views/merchant/merchant-dashboard.jsp">Merchant Board</a>
                    </c:if>

                    <a class="nav-link text-secondary" href="${pageContext.request.contextPath}/LogoutController">Logout</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>