<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="row">
        <div class="col-md-6 mb-4">
            <h2 class="fw-bold text-primary mb-2">${not empty requestScope.PRODUCT ? requestScope.PRODUCT.name : 'Product Details'}</h2>
            <div class="mb-3">
                <span class="badge bg-info text-dark">Category: ${requestScope.PRODUCT.category}</span>
                <span class="badge bg-success ms-1">Verified</span>
            </div>
            <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945" class="img-fluid rounded-3 mb-4 shadow-sm" alt="Product Image">
            <h5 class="fw-bold">About this place</h5>
            <p class="text-muted lh-lg">
                Experience world-class service right in the heart of the city. We provide top-tier amenities, comfortable rooms, and breathtaking views to make your stay unforgettable. Authenticity is our priority.
            </p>
        </div>
        
        <div class="col-md-6">
            <div class="card shadow-sm border-0 bg-light rounded-3">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="fw-bold mb-0">Customer Reviews</h4>
                        <a href="${pageContext.request.contextPath}/views/customer/write-review.jsp?id=${requestScope.PRODUCT.productId}" class="btn btn-sm btn-success fw-bold px-3">
                            + Write Review
                        </a>
                    </div>
                    
                    <div class="d-flex justify-content-between mb-4 pb-3 border-bottom">
                        <select class="form-select form-select-sm w-auto me-2 shadow-none" name="filterRating">
                            <option value="all">All Ratings</option>
                            <option value="5">5 Stars Only</option>
                            <option value="4">4 Stars & Up</option>
                            <option value="positive">Positive Reviews</option>
                            <option value="critical">Critical Reviews</option>
                        </select>
                        <select class="form-select form-select-sm w-auto shadow-none" name="sortDate">
                            <option value="newest">Sort by: Newest First</option>
                            <option value="oldest">Sort by: Oldest First</option>
                            <option value="helpful">Sort by: Most Helpful</option>
                        </select>
                    </div>
                    
                    <div class="review-list">
                        <c:choose>
                            <c:when test="${not empty requestScope.REVIEWS}">
                                <c:forEach var="r" items="${requestScope.REVIEWS}">
                                    <div class="review-item mb-4 p-3 bg-white rounded-3 shadow-sm border-start border-4 border-primary">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <div>
                                                <strong class="text-dark d-block">User ID: ${r.userId}</strong>
                                                <div class="text-warning small mt-1">
                                                    <c:forEach begin="1" end="${r.rating}">&#9733;</c:forEach>
                                                    <c:forEach begin="${r.rating + 1}" end="5"><span class="text-muted">&#9734;</span></c:forEach>
                                                </div>
                                            </div>
                                            <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill"><small>✓ Verified Stay</small></span>
                                        </div>
                                        <p class="mb-2 text-dark">"${r.content}"</p>
                                        <div class="d-flex justify-content-between align-items-center mt-2">
                                            <small class="text-muted">Posted: ${r.createdAt}</small>
                                            <button class="btn btn-link btn-sm text-secondary text-decoration-none p-0"><small>Helpful?</small></button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center text-muted py-5">
                                    <h1 class="display-1 text-light mb-3">&#128196;</h1>
                                    <p class="mb-0">No published reviews yet.</p>
                                    <p>Be the first to share your experience!</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:if test="${not empty requestScope.REVIEWS}">
                        <nav aria-label="Review pagination" class="mt-4">
                            <ul class="pagination pagination-sm justify-content-center mb-0">
                                <li class="page-item disabled">
                                    <a class="page-link" href="#" tabindex="-1" aria-disabled="true">Previous</a>
                                </li>
                                <li class="page-item active" aria-current="page"><a class="page-link" href="#">1</a></li>
                                <li class="page-item"><a class="page-link" href="#">2</a></li>
                                <li class="page-item"><a class="page-link" href="#">3</a></li>
                                <li class="page-item">
                                    <a class="page-link" href="#">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>