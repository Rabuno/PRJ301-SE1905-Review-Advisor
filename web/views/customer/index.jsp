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
        <div class="col-md-4 mb-4">
            <div class="card h-100 shadow-sm card-product border-0">
                <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945" class="card-img-top" alt="Hotel Image" style="height: 200px; object-fit: cover;">
                <div class="card-body">
                    <h5 class="card-title fw-bold text-dark mb-1">Majestic Saigon Hotel</h5>
                    <p class="text-muted small mb-3"><i class="bi bi-geo-alt-fill text-danger"></i> District 1, Ho Chi Minh City</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="text-warning fw-bold">4.8 ★</span>
                            <span class="text-muted small">(120 verified reviews)</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/MainController?action=ViewDetail&id=H001" class="btn btn-sm btn-outline-primary fw-bold">View Details</a>
                    </div>
                </div>
            </div>
        </div>
        
        <c:forEach var="product" items="${requestScope.PRODUCT_LIST}">
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow-sm card-product border-0">
                    <div class="card-body">
                        <h5 class="card-title fw-bold text-dark mb-1">${product.name}</h5>
                        <p class="text-muted small mb-3">${product.location}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <span class="text-warning fw-bold">${product.averageRating} ★</span>
                            </div>
                            <a href="${pageContext.request.contextPath}/MainController?action=ViewDetail&id=${product.productId}" class="btn btn-sm btn-outline-primary fw-bold">View Details</a>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>