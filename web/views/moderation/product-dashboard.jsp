<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Product Moderation - Review Advisor</title>
        <%@include file="../../common/resources.jsp" %>
    </head>
    <body class="bg-light d-flex flex-column min-vh-100">
        <jsp:include page="/common/header.jsp" />

        <div class="container mt-5 mb-5 flex-grow-1">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold mb-1 text-danger">Product Moderation</h2>
                    <p class="text-muted mb-0">Listings are AI-scanned first. Approve to publish, reject to deactivate.</p>
                </div>
                <a class="btn btn-outline-secondary fw-bold" href="${pageContext.request.contextPath}/ModeratorServlet">
                    Review Moderation
                </a>
            </div>

            <c:if test="${not empty sessionScope.SUCCESS_MSG}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${sessionScope.SUCCESS_MSG}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="SUCCESS_MSG" scope="session" />
            </c:if>

            <div class="card shadow-sm border-0">
                <div class="card-header bg-dark text-white fw-bold py-3">
                    <i class="bi bi-building-check"></i> Pending Products
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th class="ps-3">ID</th>
                                    <th>Name</th>
                                    <th>Category</th>
                                    <th>Merchant</th>
                                    <th>Status</th>
                                    <th class="text-end pe-3">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty requestScope.PENDING_PRODUCTS}">
                                        <c:forEach var="p" items="${requestScope.PENDING_PRODUCTS}">
                                            <tr>
                                                <td class="ps-3"><span class="badge bg-secondary">${p.productId}</span></td>
                                                <td class="fw-bold">${p.name}</td>
                                                <td>${p.category}</td>
                                                <td class="small text-muted">${p.merchantId}</td>
                                                <td><span class="badge bg-warning text-dark">${p.status}</span></td>
                                                <td class="text-end pe-3">
                                                    <form action="${pageContext.request.contextPath}/ModeratorProducts" method="POST" class="d-inline">
                                                        <input type="hidden" name="productId" value="${p.productId}">
                                                        <input type="hidden" name="action" value="APPROVE">
                                                        <button class="btn btn-sm btn-success fw-bold me-1" type="submit">
                                                            <i class="bi bi-check-circle"></i> Approve
                                                        </button>
                                                    </form>
                                                    <form action="${pageContext.request.contextPath}/ModeratorProducts" method="POST" class="d-inline">
                                                        <input type="hidden" name="productId" value="${p.productId}">
                                                        <input type="hidden" name="action" value="REJECT">
                                                        <button class="btn btn-sm btn-danger fw-bold" type="submit">
                                                            <i class="bi bi-slash-circle"></i> Reject
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="6" class="text-center text-muted py-5">
                                                No pending products.
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

        <%@include file="../../common/footer.jsp" %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

