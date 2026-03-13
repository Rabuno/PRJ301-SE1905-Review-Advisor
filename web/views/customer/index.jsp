<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Home - Review Advisor</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

        <style>
            /* Tùy chỉnh Hero Section */
            .hero-section {
                /* Sử dụng ảnh nền (bạn có thể thay bằng ảnh thật trong thư mục assets) */
                background: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)), url('https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?auto=format&fit=crop&w=1920&q=80') center/cover no-repeat;
                border-radius: 16px;
                color: white;
                padding: 80px 20px;
            }

            /* Thanh tìm kiếm */
            .search-box {
                background: rgba(255, 255, 255, 0.95);
                border-radius: 50px;
                padding: 6px;
                box-shadow: 0 8px 32px rgba(0,0,0,0.15);
            }
            .search-box input {
                border: none !important;
                box-shadow: none !important;
                background: transparent;
                font-size: 1.1rem;
            }
            .search-box .btn-search {
                border-radius: 50px;
                padding: 10px 30px;
                font-weight: 600;
                background-color: #34d399; /* Xanh lá Tripadvisor style */
                border-color: #34d399;
                color: #000;
            }
            .search-box .btn-search:hover {
                background-color: #10b981;
                border-color: #10b981;
            }

            /* Danh mục nổi bật */
            .category-card {
                border: 1px solid #e9ecef;
                border-radius: 12px;
                padding: 20px;
                text-align: center;
                transition: all 0.2s;
                background: white;
                text-decoration: none;
                color: #333;
                display: block;
            }
            .category-card:hover {
                box-shadow: 0 4px 15px rgba(0,0,0,0.05);
                transform: translateY(-3px);
                border-color: #34d399;
            }
            .category-icon {
                font-size: 2rem;
                color: #34d399;
                margin-bottom: 10px;
            }

            /* Thẻ sản phẩm */
            .card-product {
                transition: transform 0.3s ease, box-shadow 0.3s ease;
                border: 1px solid #f1f3f5;
                border-radius: 16px;
                overflow: hidden;
                display: flex;
                flex-direction: column;
                background: #fff;
            }
            .card-product:hover {
                transform: translateY(-5px);
                box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important;
            }
            .card-img-wrapper {
                position: relative;
                overflow: hidden;
            }
            .card-img-top {
                height: 200px;
                object-fit: cover;
                transition: transform 0.5s ease;
            }
            .card-product:hover .card-img-top {
                transform: scale(1.05);
            }
            .badge-category {
                position: absolute;
                top: 15px;
                left: 15px;
                background: rgba(0, 0, 0, 0.7);
                color: white;
                padding: 5px 12px;
                border-radius: 20px;
                font-size: 0.75rem;
                font-weight: 600;
                backdrop-filter: blur(4px);
            }

            .ta-rating {
                color: #00aa6c;
                font-size: 0.9rem;
            }
            .desc-text {
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                color: #6c757d;
                font-size: 0.9rem;
                margin-bottom: 15px;
            }
        </style>
    </head>

    <body class="bg-light d-flex flex-column min-vh-100">

        <%@include file="../../common/header.jsp" %>

        <div class="container mt-4 mb-5 flex-grow-1">

            <div class="hero-section text-center shadow-sm mb-5">
                <h1 class="fw-bold mb-3 display-4">Explore the Best Places</h1>
                <p class="fs-5 mb-4 text-light opacity-75">Discover honest reviews for hotels, restaurants, and attractions.</p>

                <form action="${pageContext.request.contextPath}/MainController" method="GET" class="d-flex justify-content-center">
                    <input type="hidden" name="action" value="search">
                    <div class="search-box w-75 w-md-50 d-flex align-items-center">
                        <span class="ps-3 pe-2 text-muted"><i class="bi bi-search fs-5"></i></span>
                        <input type="text" name="txtSearch" class="form-control" placeholder="Where to? (e.g. Hanoi, Hotel, Pizza...)">
                        <button type="submit" class="btn btn-search">Search</button>
                    </div>
                </form>
            </div>

            <div class="mb-5">
                <h4 class="fw-bold text-dark mb-3">Browse by Category</h4>
                <div class="row g-3">
                    <div class="col-6 col-md-3">
                        <a href="#" class="category-card">
                            <i class="bi bi-buildings category-icon"></i>
                            <h6 class="mb-0 fw-bold">Hotels</h6>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="#" class="category-card">
                            <i class="bi bi-cup-hot category-icon"></i>
                            <h6 class="mb-0 fw-bold">Restaurants</h6>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="#" class="category-card">
                            <i class="bi bi-camera category-icon"></i>
                            <h6 class="mb-0 fw-bold">Attractions</h6>
                        </a>
                    </div>
                    <div class="col-6 col-md-3">
                        <a href="#" class="category-card">
                            <i class="bi bi-balloon-heart category-icon"></i>
                            <h6 class="mb-0 fw-bold">Cafes</h6>
                        </a>
                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3 class="fw-bold text-dark mb-0">Top Destinations For You</h3>
            </div>

            <div class="row g-4">
                <c:choose>
                    <c:when test="${not empty requestScope.PRODUCT_LIST}">
                        <c:forEach var="product" items="${requestScope.PRODUCT_LIST}">
                            <div class="col-12 col-sm-6 col-lg-4">
                                <div class="card h-100 shadow-sm card-product">

                                    <div class="card-img-wrapper">
                                        <span class="badge-category">
                                            <i class="bi bi-tag-fill me-1"></i> ${not empty product.category ? product.category : 'Place'}
                                        </span>
                                        <c:choose>
                                            <c:when test="${not empty product.imageUrl}">
                                                <img src="<c:url value='${product.imageUrl}' />"
                                                     class="card-img-top" 
                                                     alt="${product.name}"
                                                     onerror="this.onerror=null; this.src='<c:url value="/assets/default/default.jpg" />'">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="<c:url value='/assets/default/default.jpg' />" 
                                                     class="card-img-top" 
                                                     alt="Default Image">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="card-body d-flex flex-column p-4">

                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <h5 class="card-title fw-bold text-dark mb-0 text-truncate" title="${product.name}">${product.name}</h5>
                                        </div>

                                        <<div class="ta-rating mb-2">
                                            <c:forEach var="i" begin="1" end="5">
                                                <c:choose>
                                                    <%-- Trường hợp 1: Biến đếm nhỏ hơn hoặc bằng rating -> In sao đầy --%>
                                                    <c:when test="${i <= product.rating}">
                                                        <i class="bi bi-circle-fill"></i>
                                                    </c:when>

                                                    <%-- Trường hợp 2: Biến đếm - 0.5 nhỏ hơn hoặc bằng rating -> In sao nửa --%>
                                                    <c:when test="${(i - 0.5) <= product.rating}">
                                                        <i class="bi bi-circle-half"></i>
                                                    </c:when>

                                                    <%-- Trường hợp 3: Còn lại -> In sao rỗng --%>
                                                    <c:otherwise>
                                                        <i class="bi bi-circle"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>

                                            <span class="text-muted ms-1 small">(${product.reviewCount} Reviews)</span>
                                        </div>
                                        <span class="text-muted ms-1 small">(Reviews)</span>
                                    </div>

                                    <p class="desc-text" title="${product.description}">
                                        ${product.description}
                                    </p>

                                    <div class="mt-auto mb-3">
                                        <span class="badge ${product.status == 'ACTIVE' ? 'bg-success-subtle text-success' : 'bg-secondary-subtle text-secondary'} border rounded-pill">
                                            Status: ${not empty product.status ? product.status : 'ACTIVE'}
                                        </span>
                                    </div>

                                    <div class="mt-auto border-top pt-3">
                                        <a href="${pageContext.request.contextPath}/MainController?action=ViewDetail&id=${product.productId}"
                                           class="btn btn-dark w-100 fw-bold rounded-pill">
                                            View Details
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>

                <c:otherwise>
                    <div class="col-12">
                        <div class="text-center py-5 bg-white rounded-4 shadow-sm border">
                            <i class="bi bi-search text-muted" style="font-size: 4rem;"></i>
                            <h4 class="text-dark fw-bold mt-3">No products found</h4>
                            <p class="text-muted mb-0">We couldn't find any places matching your search.</p>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <%@include file="../../common/footer.jsp" %>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>