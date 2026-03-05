<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="p-5 text-center bg-light rounded-3 shadow-sm mb-5">
        <h1 class="text-primary fw-bold">Find Your Next Great Stay</h1>
        <p class="text-muted">Read authentic, AI-verified reviews from real travelers.</p>
        <form action="${pageContext.request.contextPath}/MainController" method="GET" class="d-flex justify-content-center mt-4">
            <input type="hidden" name="action" value="search">
            <input type="text" name="txtSearch" class="form-control w-50 me-2" placeholder="Search for hotels, resorts, or destinations...">
            <button type="submit" class="btn btn-primary px-4 fw-bold">Search</button>
        </form>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold text-dark">Popular Destinations</h3>
    </div>

    <div class="row">
        <c:choose>
            <c:when test="${not empty requestScope.PRODUCT_LIST}">
                <c:forEach var="product" items="${requestScope.PRODUCT_LIST}">
                    <div class="col-md-4 mb-4">
                        <div class="card h-100 shadow-sm card-product border-0">
                            <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945" class="card-img-top" alt="Hotel Image" style="height: 200px; object-fit: cover;">
                            <div class="card-body">
                                <h5 class="card-title fw-bold text-dark mb-1">${product.name}</h5>
                                <p class="text-muted small mb-3">
                                    <i class="bi bi-tag-fill text-danger"></i> Category: ${product.category}
                                </p>
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <span class="badge ${product.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">
                                            ${product.status}
                                        </span>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/MainController?action=ViewDetail&id=${product.productId}" class="btn btn-sm btn-outline-primary fw-bold">View Details</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="col-12 text-center py-5">
                    <h5 class="text-muted">No products available at the moment. Please check back later!</h5>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>