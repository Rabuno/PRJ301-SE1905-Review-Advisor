<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Product Details - Review Advisor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>

<body class="bg-light d-flex flex-column min-vh-100">

    <%@include file="../../common/header.jsp" %>

    <div class="container mt-4 mb-5 flex-grow-1">
        <div class="row">
            <div class="col-md-5 mb-4">
                <div class="card shadow-sm border-0 rounded-3 sticky-top" style="top: 20px;">
                    <img src="${not empty requestScope.PRODUCT.imageUrl ? requestScope.PRODUCT.imageUrl : 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&h=600&fit=crop'}"
                        class="card-img-top" alt="${requestScope.PRODUCT.name}">

                    <div class="card-body p-4">
                        <h2 class="fw-bold text-primary mb-2">${not empty requestScope.PRODUCT ? requestScope.PRODUCT.name : 'Unknown Product'}</h2>
                        <div class="mb-3">
                            <span class="badge bg-info text-dark">
                                <i class="bi bi-tag-fill"></i> Category: ${not empty requestScope.PRODUCT.category ? requestScope.PRODUCT.category : 'General'}
                            </span>
                            <span class="badge bg-success">
                                <i class="bi bi-check-circle-fill"></i> Verified
                            </span>
                        </div>
                        <p class="text-muted lh-lg">
                            ${not empty requestScope.PRODUCT.description ? requestScope.PRODUCT.description : 'No description available for this product.'}
                        </p>

                        <hr>
                        <div class="d-grid mt-3">
                            <a href="${pageContext.request.contextPath}/views/customer/write-review.jsp?id=${requestScope.PRODUCT.productId}"
                                class="btn btn-success btn-lg fw-bold shadow-sm">
                                <i class="bi bi-pencil-square me-2"></i> Write a Review
                            </a>
                        </div>
                        <p class="text-center text-muted small mt-2">Share your experience to help others!</p>
                    </div>
                </div>
            </div>

            <div class="col-md-7">
                <div class="card shadow-sm border-0 bg-white rounded-3 p-4">
                    <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-3">
                        <h4 class="fw-bold mb-0"><i class="bi bi-chat-right-text text-primary"></i> Customer Reviews</h4>
                        <a href="${pageContext.request.contextPath}/views/customer/write-review.jsp?id=${requestScope.PRODUCT.productId}"
                            class="btn btn-outline-success fw-bold btn-sm px-3">
                            <i class="bi bi-plus-lg"></i> Add Review
                        </a>
                    </div>

                    <div class="review-list">
                        <c:choose>
                            <c:when test="${not empty requestScope.REVIEWS}">
                                <c:forEach var="r" items="${requestScope.REVIEWS}">
                                    <div class="review-item mb-4 p-3 bg-light rounded-3 shadow-sm border-start border-4 border-primary">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <div>
                                                <strong class="text-dark d-block">
                                                    <i class="bi bi-person-circle text-secondary me-1"></i> User ID: ${r.userId}
                                                </strong>
                                                <div class="text-warning small mt-1">
                                                    <c:forEach begin="1" end="${r.rating}">&#9733;</c:forEach>
                                                    <c:forEach begin="${r.rating + 1}" end="5"><span class="text-muted">&#9734;</span></c:forEach>
                                                </div>
                                            </div>
                                            <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill">
                                                <small><i class="bi bi-shield-check"></i> Verified</small>
                                            </span>
                                        </div>
                                        <p class="mb-2 text-dark fs-6 lh-base">"${r.content}"</p>
                                        <div class="d-flex justify-content-between align-items-center mt-3 pt-2 border-top">
                                            <small class="text-muted"><i class="bi bi-clock"></i> Posted: ${r.createdAt}</small>
                                            <button class="btn btn-sm btn-outline-secondary px-2 py-0">
                                                <small><i class="bi bi-hand-thumbs-up"></i> Helpful</small>
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center text-muted py-5">
                                    <h1 class="display-1 text-light mb-3"><i class="bi bi-journal-x"></i></h1>
                                    <h5 class="fw-bold">No reviews yet</h5>
                                    <p class="mb-3">Be the first to share your experience about this place!</p>
                                    <a href="${pageContext.request.contextPath}/views/customer/write-review.jsp?id=${requestScope.PRODUCT.productId}"
                                        class="btn btn-success fw-bold">
                                        Write the first review
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%@include file="../../common/footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
