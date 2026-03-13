<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm sticky-top mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold text-success"
           href="${pageContext.request.contextPath}/MainController">
            TRIP ADVISOR
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link fw-bold" href="${pageContext.request.contextPath}/MainController">Home</a>
                </li>

                <c:if test="${not empty sessionScope.USER}">
                    <c:if test="${sessionScope.USER.role eq 'CUSTOMER'}">
                        <li class="nav-item">
                            <a class="nav-link text-success fw-bold"
                               href="${pageContext.request.contextPath}/MainController?action=MyReviews">My Reviews</a>
                        </li>
                    </c:if>

                    <c:if test="${sessionScope.USER.role eq 'MERCHANT'}">
                        <li class="nav-item">
                            <a class="nav-link text-primary fw-bold"
                               href="${pageContext.request.contextPath}/MerchantServlet">Merchant</a>
                        </li>
                    </c:if>

                    <c:if test="${sessionScope.USER.role eq 'MODERATOR'}">
                        <li class="nav-item">
                            <a class="nav-link text-danger fw-bold"
                               href="${pageContext.request.contextPath}/ModeratorServlet">Moderation</a>
                        </li>
                    </c:if>

                    <c:if test="${sessionScope.USER.role eq 'AUDITOR'}">
                        <li class="nav-item">
                            <a class="nav-link text-info fw-bold"
                               href="${pageContext.request.contextPath}/views/auditor/audit-logs.jsp">Audit Logs</a>
                        </li>
                    </c:if>

                    <c:if test="${sessionScope.USER.role eq 'ADMIN' or sessionScope.USER.username eq 'admin'}">
                        <li class="nav-item">
                            <a class="nav-link fw-bold text-danger"
                               href="${pageContext.request.contextPath}/ModeratorServlet">Moderation</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link fw-bold text-primary"
                               href="${pageContext.request.contextPath}/Admin/RolePermissions">Manage Roles</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link fw-bold text-primary"
                               href="${pageContext.request.contextPath}/Admin/UserRoles">Manage Users</a>
                        </li>
                    </c:if>
                </c:if>
            </ul>

            <div class="navbar-nav ms-auto align-items-center">
                <c:choose>
                    <c:when test="${empty sessionScope.USER}">
                        <a class="nav-link btn btn-outline-primary px-4 mx-2"
                           href="${pageContext.request.contextPath}/login.jsp">Login</a>
                        <a class="nav-link btn btn-outline-primary px-4 mx-2"
                           href="${pageContext.request.contextPath}/register.jsp">Register</a>
                    </c:when>
                    <c:otherwise>
                        <span class="nav-link me-3">
                            Welcome, <strong>${sessionScope.USER.username}</strong>
                        </span>
                        <a class="nav-link btn btn-sm btn-outline-danger px-3"
                           href="${pageContext.request.contextPath}/LogoutController">Logout</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

