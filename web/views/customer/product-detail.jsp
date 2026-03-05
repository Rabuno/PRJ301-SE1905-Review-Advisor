<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="row">
        <div class="col-md-7">
            <h3 class="mb-3">${not empty requestScope.PRODUCT ? requestScope.PRODUCT.name : 'Product Details'}</h3>
            <span class="badge bg-info text-dark mb-3">Category: ${requestScope.PRODUCT.category}</span>
            <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945" class="img-fluid rounded mb-3 shadow-sm" alt="Hotel Image">
            <p class="text-muted">
                Experience world-class service right in the heart of the city. We provide top-tier amenities, comfortable rooms, and breathtaking views to make your stay unforgettable.
            </p>
        </div>
        
        <div class="col-md-5">
            <div class="card shadow-sm border-0 p-4 bg-light">
                <h4 class="mb-3">Community Reviews</h4>
                <hr class="mt-0 mb-4">
                
                <c:choose>
                    <c:when test="${not empty requestScope.REVIEWS}">
                        <c:forEach var="r" items="${requestScope.REVIEWS}">
                            <div class="review-item mb-3 p-3 bg-white rounded shadow-sm border-start border-4 border-success">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <strong class="text-dark">User ID: ${r.userId}</strong>
                                    <span class="verified-badge">✓ Verified</span>
                                </div>
                                <div class="text-warning small mb-2">${r.rating} ★</div>
                                <p class="small mb-0 text-muted">"${r.content}"</p>
                                <div class="text-end text-muted mt-2" style="font-size: 0.7rem;">
                                    Posted: ${r.createdAt}
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center text-muted mb-4">
                            <p>No published reviews yet. Be the first to review!</p>
                        </div>
                    </c:otherwise>
                </c:choose>
                 
                <div class="mt-4 text-center">
                    <a href="${pageContext.request.contextPath}/views/customer/write-review.jsp?id=${requestScope.PRODUCT.productId}" class="btn btn-success w-100 py-2 fw-bold">
                        Write a Review
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>