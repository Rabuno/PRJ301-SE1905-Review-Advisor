<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>My Properties - Merchant</title>
        <%@include file="../../common/resources.jsp" %>
    </head>
    <body class="d-flex flex-column min-vh-100">
        <%@include file="../../common/header.jsp" %>

            <div class="container mt-5 mb-5 flex-grow-1">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div>
                        <h2 class="fw-bold mb-1">My Properties</h2>
                        <p class="text-muted mb-0">Manage your listings and keep them up to date.</p>
                    </div>
                    <a class="btn btn-success fw-bold"
                       href="${pageContext.request.contextPath}/MerchantServlet?action=AddProperty">
                        <i class="bi bi-plus-lg"></i> Add Property
                    </a>
                </div>

                <c:if test="${not empty sessionScope.ERROR_MSG}">
                    <div class="alert alert-danger shadow-sm fw-bold" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>${sessionScope.ERROR_MSG}
                    </div>
                    <c:remove var="ERROR_MSG" scope="session" />
                </c:if>
                <c:if test="${not empty sessionScope.SUCCESS_MSG}">
                    <div class="alert alert-success shadow-sm fw-bold" role="alert">
                        <i class="bi bi-check-circle-fill me-2"></i>${sessionScope.SUCCESS_MSG}
                    </div>
                    <c:remove var="SUCCESS_MSG" scope="session" />
                </c:if>

                <div class="row justify-content-center mt-4">
                    <div class="col-md-10">
                        <div class="card shadow-sm border-0">
                            <div class="card-header bg-white fw-bold">
                                Properties List
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th class="ps-3">ID</th>
                                                <th>Name</th>
                                                <th>Category</th>
                                                <th class="text-end">Price</th>
                                                <th>Status</th>
                                                <th class="text-end pe-3">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${not empty requestScope.MERCHANT_PRODUCTS}">
                                                    <c:forEach var="p" items="${requestScope.MERCHANT_PRODUCTS}">
                                                        <tr>
                                                            <td class="ps-3"><span class="badge bg-secondary">${p.productId}</span></td>
                                                            <td class="fw-bold">${p.name}</td>
                                                            <td>${p.category}</td>
                                                            <td class="text-end">
                                                                <c:choose>
                                                                    <c:when test="${p.price > 0}">
                                                                        ${p.price}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted">-</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <span class="badge bg-light text-dark border">${p.status}</span>
                                                            </td>
                                                            <td class="text-end pe-3">
                                                                <a class="btn btn-sm btn-outline-primary"
                                                                   href="${pageContext.request.contextPath}/MerchantServlet?action=EditProperty&id=${p.productId}">
                                                                    <i class="bi bi-pencil-square"></i> Edit
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="5" class="text-center text-muted py-4">
                                                            No properties yet.
                                                        </td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%@include file="../../common/footer.jsp" %>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
