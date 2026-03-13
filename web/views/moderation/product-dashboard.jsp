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
                                    <th>Status</th>
                                    <th>AI Risk</th>
                                    <th>AI Labels</th>
                                    <th class="text-end pe-3">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty requestScope.PENDING_ITEMS}">
                                        <c:forEach var="item" items="${requestScope.PENDING_ITEMS}">
                                            <tr>
                                                <td class="ps-3"><span class="badge bg-secondary">${item.product.productId}</span></td>
                                                <td class="fw-bold">${item.product.name}</td>
                                                <td>${item.product.category}</td>
                                                <td><span class="badge bg-warning text-dark">${item.product.status}</span></td>
                                                <td>
                                                    <span class="badge bg-danger">${item.riskScoreDisplay}</span>
                                                </td>
                                                <td style="min-width: 220px;">
                                                    <c:forEach var="l" items="${item.labels}" end="2">
                                                        <span class="badge bg-light text-dark border me-1">${l}</span>
                                                    </c:forEach>
                                                    <c:if test="${empty item.labels}">
                                                        <span class="text-muted small">none</span>
                                                    </c:if>
                                                </td>
                                                <td class="text-end pe-3">
                                                    <button type="button" class="btn btn-sm btn-info text-white fw-bold me-1"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#detail_${item.product.productId}">
                                                        <i class="bi bi-search"></i> Detail
                                                    </button>
                                                    <form action="${pageContext.request.contextPath}/ModeratorProducts" method="POST" class="d-inline">
                                                        <input type="hidden" name="productId" value="${item.product.productId}">
                                                        <input type="hidden" name="action" value="APPROVE">
                                                        <button class="btn btn-sm btn-success fw-bold me-1" type="submit">
                                                            <i class="bi bi-check-circle"></i> Approve
                                                        </button>
                                                    </form>
                                                    <form action="${pageContext.request.contextPath}/ModeratorProducts" method="POST" class="d-inline">
                                                        <input type="hidden" name="productId" value="${item.product.productId}">
                                                        <input type="hidden" name="action" value="REJECT">
                                                        <button class="btn btn-sm btn-danger fw-bold" type="submit">
                                                            <i class="bi bi-slash-circle"></i> Reject
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>

                                            <div class="modal fade" id="detail_${item.product.productId}" tabindex="-1" aria-hidden="true">
                                                <div class="modal-dialog modal-lg modal-dialog-centered">
                                                    <div class="modal-content">
                                                        <div class="modal-header bg-dark text-white">
                                                            <h5 class="modal-title fw-bold">
                                                                <i class="bi bi-robot text-warning"></i> AI Detail for ${item.product.productId}
                                                            </h5>
                                                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <div class="mb-2">
                                                                <div class="fw-bold">${item.product.name}</div>
                                                                <div class="text-muted small">${item.product.category}</div>
                                                            </div>
                                                            <div class="alert alert-light border">
                                                                <div class="d-flex justify-content-between">
                                                                    <span class="fw-bold">AI Risk Score</span>
                                                                    <span class="badge bg-danger">${item.riskScoreDisplay}</span>
                                                                </div>
                                                                <div class="mt-2">
                                                                    <span class="fw-bold">Labels:</span>
                                                                    <c:forEach var="l" items="${item.labels}">
                                                                        <span class="badge bg-light text-dark border ms-1">${l}</span>
                                                                    </c:forEach>
                                                                    <c:if test="${empty item.labels}">
                                                                        <span class="text-muted small ms-1">none</span>
                                                                    </c:if>
                                                                </div>
                                                            </div>

                                                            <h6 class="fw-bold">Top Reasons</h6>
                                                            <c:choose>
                                                                <c:when test="${not empty item.reasons}">
                                                                    <ul class="list-group">
                                                                        <c:forEach var="r" items="${item.reasons}" end="4">
                                                                            <li class="list-group-item">
                                                                                <div class="d-flex justify-content-between">
                                                                                    <span class="fw-bold">${r.feature}</span>
                                                                                    <span class="text-danger small">${r.weight}</span>
                                                                                </div>
                                                                                <div class="text-muted small">${r.description}</div>
                                                                            </li>
                                                                        </c:forEach>
                                                                    </ul>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="text-muted small">No AI reasons.</div>
                                                                </c:otherwise>
                                                            </c:choose>

                                                            <div class="mt-3">
                                                                <h6 class="fw-bold">Listing Description</h6>
                                                                <div class="small text-muted">${item.product.description}</div>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="7" class="text-center text-muted py-5">
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
