<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home - Review Advisor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        .card-product {
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        .card-product:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
        }
        .hero-section {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 15px;
        }
    </style>
</head>
<body class="bg-light d-flex flex-column min-vh-100">

    <%@include file="../../common/header.jsp" %>

    <div class="container mt-4 mb-5 flex-grow-1">
        
        <div class="p-5 text-center hero-section shadow-sm mb-5">
            <h1 class="text-primary fw-bold mb-3"><i class="bi bi-search-heart"></i> Find Your Next Great Stay</h1>
            <p class="text-muted fs-5">Read authentic, AI-verified reviews from real travelers.</p>
            <form action="${pageContext.request.contextPath}/MainController" method="GET" class="d-flex justify-content-center mt-4">
                <input type="hidden" name="action" value="search">
                <div class="input-group w-50 shadow-sm">
                    <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
                    <input type="text" name="txtSearch" class="form-control border-start-0 py-2" placeholder="Search for hotels, resorts, or destinations...">
                    <button type="submit" class="btn btn-primary px-4 fw-bold">Search</button>
                </div>
            </form>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3 class="fw-bold text-dark"><i class="bi bi-stars text-warning"></i> Popular Destinations</h3>
        </div>

        <div class="row">
            <c:choose>
                <c:when test="${not empty requestScope.PRODUCT_LIST}">
                    <c:forEach var="product" items="${requestScope.PRODUCT_LIST}">
                        <div class="col-md-4 mb-4">
                            <div class="card h-100 shadow-sm card-product border-0 rounded-3 overflow-hidden">
                                <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600&h=400&fit=crop" class="card-img-top" alt="Hotel Image" style="height: 220px; object-fit: cover;">
                                
                                <div class="card-body d-flex flex-column">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <h5 class="card-title fw-bold text-dark mb-0">${product.name}</h5>
                                        <span class="badge ${product.status == 'ACTIVE' ? 'bg-success-subtle text-success border border-success' : 'bg-secondary-subtle text-secondary border border-secondary'} rounded-pill">
                                            ${product.status}
                                        </span>
                                    </div>
                                    
                                    <p class="text-muted small mb-3">
                                        <i class="bi bi-geo-alt-fill text-danger"></i> Category: <strong>${product.category}</strong>
                                    </p>
                                    
                                    <div class="mt-auto pt-3 border-top">
                                        <a href="${pageContext.request.contextPath}/MainController?action=ViewDetail&id=${product.productId}" class="btn btn-outline-primary w-100 fw-bold">
                                            View Details
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                
                <c:otherwise>
                    <div class="col-12 text-center py-5 bg-white rounded-3 shadow-sm">
                        <h1 class="display-1 text-muted mb-3"><i class="bi bi-inbox"></i></h1>
                        <h4 class="text-secondary fw-bold">No products available at the moment.</h4>
                        <p class="text-muted">We are currently updating our listings. Please check back later!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <%@include file="../../common/footer.jsp" %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>