<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Home - Review Advisor</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
        <link href="https://cdn.jsdelivr.net/npm/aos@2.3.1/dist/aos.css" rel="stylesheet">

        <style>
            /* ========== ROOT & GLOBAL STYLES ========== */
            :root {
                --primary-color: #34d399;
                --primary-dark: #10b981;
                --secondary-color: #6366f1;
                --danger-color: #ef4444;
                --warning-color: #f59e0b;
                --success-color: #10b981;
                --bg-light: #f9fafb;
                --bg-lighter: #f3f4f6;
                --text-dark: #1f2937;
                --text-muted: #6b7280;
                --border-color: #e5e7eb;
                --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
                --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
                --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
                --transition-fast: 0.2s cubic-bezier(0.4, 0, 0.2, 1);
                --transition-base: 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                --transition-slow: 0.5s cubic-bezier(0.4, 0, 0.2, 1);
            }

            * {
                transition: color var(--transition-fast), 
                            background-color var(--transition-fast);
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                color: var(--text-dark);
                background: var(--bg-light);
                overflow-x: hidden;
            }

            /* ========== HERO SECTION ========== */
            .hero-section {
                background: linear-gradient(135deg, rgba(52, 211, 153, 0.95) 0%, rgba(16, 185, 129, 0.95) 100%),
                            url('https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?auto=format&fit=crop&w=1920&q=80') center/cover;
                position: relative;
                overflow: hidden;
                border-radius: 24px;
                color: white;
                padding: 100px 20px;
                margin: 0 auto;
                box-shadow: var(--shadow-xl);
                animation: fadeInDown 0.8s ease-out;
            }

            .hero-section::before {
                content: '';
                position: absolute;
                top: -50%;
                right: -10%;
                width: 500px;
                height: 500px;
                background: rgba(255, 255, 255, 0.1);
                border-radius: 50%;
                animation: float 6s ease-in-out infinite;
                z-index: 0;
            }

            .hero-section::after {
                content: '';
                position: absolute;
                bottom: -30%;
                left: -5%;
                width: 400px;
                height: 400px;
                background: rgba(255, 255, 255, 0.05);
                border-radius: 50%;
                animation: float 8s ease-in-out infinite reverse;
                z-index: 0;
            }

            .hero-section > * {
                position: relative;
                z-index: 1;
            }

            .hero-section h1 {
                font-size: clamp(2rem, 8vw, 3.5rem);
                font-weight: 800;
                letter-spacing: -1px;
                margin-bottom: 1rem;
                text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
                animation: slideInUp 0.8s ease-out 0.1s both;
            }

            .hero-section p {
                font-size: clamp(1rem, 2vw, 1.25rem);
                opacity: 0.95;
                margin-bottom: 2rem;
                animation: slideInUp 0.8s ease-out 0.2s both;
            }

            /* ========== SEARCH BOX ========== */
            .search-box-wrapper {
                animation: slideInUp 0.8s ease-out 0.3s both;
            }

            .search-box {
                background: rgba(255, 255, 255, 0.98);
                border-radius: 60px;
                padding: 8px 12px;
                box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
                display: flex;
                align-items: center;
                gap: 8px;
                backdrop-filter: blur(10px);
                border: 1px solid rgba(255, 255, 255, 0.2);
                transition: var(--transition-base);
            }

            .search-box:hover,
            .search-box:focus-within {
                box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
                transform: translateY(-2px);
            }

            .search-box input {
                border: none !important;
                box-shadow: none !important;
                background: transparent;
                font-size: 1.1rem;
                font-weight: 500;
                color: var(--text-dark);
                flex: 1;
                outline: none;
            }

            .search-box input::placeholder {
                color: var(--text-muted);
            }

            .search-box .btn-search {
                border-radius: 50px;
                padding: 12px 32px;
                font-weight: 700;
                background-color: var(--primary-color);
                border: none;
                color: white;
                text-transform: uppercase;
                font-size: 0.85rem;
                letter-spacing: 0.5px;
                cursor: pointer;
                transition: var(--transition-base);
                white-space: nowrap;
            }

            .search-box .btn-search:hover {
                background-color: var(--primary-dark);
                transform: scale(1.05);
                box-shadow: 0 8px 20px rgba(16, 185, 129, 0.3);
            }

            .search-box .btn-search:active {
                transform: scale(0.98);
            }

            /* ========== CATEGORY SECTION ========== */
            .category-section {
                margin: 80px 0;
            }

            .category-section h4 {
                font-size: 2rem;
                font-weight: 800;
                margin-bottom: 3rem;
                position: relative;
                padding-bottom: 1rem;
            }

            .category-section h4::after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 0;
                width: 60px;
                height: 4px;
                background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
                border-radius: 2px;
            }

            .category-card {
                border: 1px solid var(--border-color);
                border-radius: 16px;
                padding: 30px 20px;
                text-align: center;
                transition: all var(--transition-base);
                background: white;
                text-decoration: none;
                color: var(--text-dark);
                display: flex;
                flex-direction: column;
                align-items: center;
                cursor: pointer;
                position: relative;
                overflow: hidden;
                box-shadow: var(--shadow-sm);
            }

            .category-card::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
                transition: left var(--transition-slow);
            }

            .category-card:hover::before {
                left: 100%;
            }

            .category-card:hover {
                box-shadow: var(--shadow-lg);
                transform: translateY(-8px);
                border-color: var(--primary-color);
                background: linear-gradient(135deg, rgba(52, 211, 153, 0.05), rgba(99, 102, 241, 0.05));
            }

            .category-icon {
                font-size: 2.5rem;
                color: var(--primary-color);
                margin-bottom: 15px;
                transition: var(--transition-base);
                display: inline-block;
            }

            .category-card:hover .category-icon {
                color: var(--secondary-color);
                transform: rotate(-10deg) scale(1.15);
            }

            .category-card h6 {
                font-weight: 700;
                font-size: 1.1rem;
                transition: var(--transition-base);
            }

            /* ========== PRODUCT CARDS ========== */
            .product-section {
                margin: 60px 0;
            }

            .product-section h3 {
                font-size: 2rem;
                font-weight: 800;
                margin-bottom: 3rem;
                position: relative;
                padding-bottom: 1rem;
            }

            .product-section h3::after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 0;
                width: 60px;
                height: 4px;
                background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
                border-radius: 2px;
            }

            .card-product {
                border: 1px solid var(--border-color);
                border-radius: 16px;
                overflow: hidden;
                display: flex;
                flex-direction: column;
                background: white;
                box-shadow: var(--shadow-sm);
                transition: all var(--transition-base);
                position: relative;
                height: 100%;
            }

            .card-product:hover {
                transform: translateY(-12px);
                box-shadow: var(--shadow-xl);
                border-color: var(--primary-color);
            }

            .card-img-wrapper {
                position: relative;
                overflow: hidden;
                background: var(--bg-light);
                height: 240px;
            }

            .card-img-top {
                height: 100%;
                object-fit: cover;
                transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
                width: 100%;
            }

            .card-product:hover .card-img-top {
                transform: scale(1.08) rotate(2deg);
            }

            .badge-category {
                position: absolute;
                top: 15px;
                left: 15px;
                background: linear-gradient(135deg, rgba(52, 211, 153, 0.95), rgba(16, 185, 129, 0.95));
                color: white;
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 0.75rem;
                font-weight: 700;
                backdrop-filter: blur(8px);
                text-transform: uppercase;
                letter-spacing: 0.5px;
                box-shadow: 0 4px 15px rgba(52, 211, 153, 0.3);
                animation: slideInDown 0.5s ease-out;
            }

            .card-body {
                padding: 24px;
                display: flex;
                flex-direction: column;
                flex-grow: 1;
            }

            .card-title {
                font-size: 1.2rem;
                font-weight: 700;
                margin-bottom: 12px;
                color: var(--text-dark);
                transition: var(--transition-base);
            }

            .card-product:hover .card-title {
                color: var(--primary-color);
            }

            .ta-rating {
                display: flex;
                align-items: center;
                gap: 4px;
                margin-bottom: 15px;
            }

            .ta-rating i {
                color: var(--warning-color);
                font-size: 1rem;
                transition: var(--transition-base);
            }

            .ta-rating i:hover {
                transform: scale(1.2) rotate(10deg);
            }

            .ta-rating .review-count {
                color: var(--text-muted);
                font-size: 0.9rem;
                margin-left: auto;
                background: var(--bg-lighter);
                padding: 4px 10px;
                border-radius: 12px;
                transition: var(--transition-base);
            }

            .ta-rating:hover .review-count {
                background: var(--primary-color);
                color: white;
            }

            .desc-text {
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                color: var(--text-muted);
                font-size: 0.95rem;
                margin-bottom: auto;
                line-height: 1.6;
                transition: var(--transition-base);
            }

            .card-product:hover .desc-text {
                color: var(--text-dark);
            }

            .status-badge {
                display: inline-block;
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 0.8rem;
                font-weight: 700;
                margin-top: 15px;
                margin-bottom: 15px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                transition: var(--transition-base);
            }

            .status-badge.active {
                background: linear-gradient(135deg, rgba(16, 185, 129, 0.1), rgba(34, 197, 94, 0.1));
                color: var(--success-color);
                border: 1px solid var(--success-color);
            }

            .status-badge.inactive {
                background: rgba(107, 114, 128, 0.1);
                color: var(--text-muted);
                border: 1px solid var(--text-muted);
            }

            .btn-details {
                background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
                color: white;
                border: none;
                border-radius: 12px;
                padding: 12px 24px;
                font-weight: 700;
                font-size: 0.95rem;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                transition: all var(--transition-base);
                cursor: pointer;
                position: relative;
                overflow: hidden;
                box-shadow: 0 4px 15px rgba(52, 211, 153, 0.3);
            }

            .btn-details::before {
                content: '';
                position: absolute;
                top: 50%;
                left: 50%;
                width: 0;
                height: 0;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.3);
                transform: translate(-50%, -50%);
                transition: width var(--transition-base), height var(--transition-base);
            }

            .btn-details:hover::before {
                width: 300px;
                height: 300px;
            }

            .btn-details:hover {
                transform: translateY(-3px);
                box-shadow: 0 8px 25px rgba(52, 211, 153, 0.4);
            }

            .btn-details:active {
                transform: translateY(-1px);
            }

            /* ========== EMPTY STATE ========== */
            .empty-state {
                text-align: center;
                padding: 60px 30px;
                background: white;
                border-radius: 20px;
                box-shadow: var(--shadow-md);
                animation: fadeInUp 0.6s ease-out;
            }

            .empty-state-icon {
                font-size: 5rem;
                color: var(--primary-color);
                margin-bottom: 20px;
                opacity: 0.8;
                animation: float 3s ease-in-out infinite;
            }

            .empty-state h4 {
                font-size: 1.5rem;
                font-weight: 700;
                color: var(--text-dark);
                margin-bottom: 10px;
            }

            .empty-state p {
                color: var(--text-muted);
                font-size: 1rem;
                margin: 0;
            }

            /* ========== ANIMATIONS ========== */
            @keyframes fadeInDown {
                from {
                    opacity: 0;
                    transform: translateY(-30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            @keyframes slideInUp {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            @keyframes float {
                0%, 100% {
                    transform: translateY(0px) translateX(0px);
                }
                33% {
                    transform: translateY(-15px) translateX(10px);
                }
                66% {
                    transform: translateY(10px) translateX(-10px);
                }
            }

            @keyframes slideInDown {
                from {
                    opacity: 0;
                    transform: translateY(-10px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            /* ========== RESPONSIVE DESIGN ========== */
            @media (max-width: 768px) {
                .hero-section {
                    padding: 60px 15px;
                    border-radius: 16px;
                }

                .hero-section h1 {
                    font-size: 2rem;
                }

                .hero-section p {
                    font-size: 1rem;
                }

                .search-box {
                    flex-direction: column;
                    gap: 12px;
                }

                .search-box input {
                    width: 100%;
                }

                .search-box .btn-search {
                    width: 100%;
                }

                .category-section h4,
                .product-section h3 {
                    font-size: 1.5rem;
                }

                .card-product {
                    box-shadow: var(--shadow-md);
                }

                .card-product:hover {
                    transform: translateY(-8px);
                }
            }

            @media (max-width: 576px) {
                .hero-section {
                    padding: 50px 12px;
                }

                .hero-section h1 {
                    font-size: 1.75rem;
                }

                .category-section h4,
                .product-section h3 {
                    font-size: 1.3rem;
                    margin-bottom: 2rem;
                }

                .card-img-wrapper {
                    height: 180px;
                }

                .card-body {
                    padding: 16px;
                }

                .btn-details {
                    padding: 10px 20px;
                    font-size: 0.85rem;
                }
            }

            /* ========== SCROLLBAR STYLING ========== */
            ::-webkit-scrollbar {
                width: 8px;
                height: 8px;
            }

            ::-webkit-scrollbar-track {
                background: var(--bg-light);
            }

            ::-webkit-scrollbar-thumb {
                background: var(--primary-color);
                border-radius: 4px;
            }

            ::-webkit-scrollbar-thumb:hover {
                background: var(--primary-dark);
            }
        </style>
    </head>

    <body class="bg-light d-flex flex-column min-vh-100">

        <%@include file="../../common/header.jsp" %>

        <main class="container mt-5 mb-5 flex-grow-1">

            <!-- HERO SECTION -->
            <div class="hero-section text-center shadow-sm mb-5">
                <h1 class="fw-bold mb-3">Explore the Best Places</h1>
                <p class="fs-5 mb-4 text-light">Discover honest reviews for hotels, restaurants, and attractions.</p>

                <div class="search-box-wrapper d-flex justify-content-center">
                    <form action="${pageContext.request.contextPath}/MainController" method="GET" class="w-100" style="max-width: 600px;">
                        <input type="hidden" name="action" value="search">
                        <div class="search-box w-100">
                            <span class="text-muted"><i class="bi bi-search fs-5"></i></span>
                            <input type="text" name="txtSearch" class="form-control" placeholder="Where to? (e.g. Hanoi, Hotel, Pizza...)">
                            <button type="submit" class="btn btn-search">Search</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- CATEGORY SECTION -->
            <div class="category-section" data-aos="fade-up">
                <h4 class="fw-bold text-dark">Browse by Category</h4>
                <div class="row g-4">
                    <div class="col-6 col-md-3" data-aos="fade-up" data-aos-delay="0">
                        <a href="#" class="category-card">
                            <i class="bi bi-buildings category-icon"></i>
                            <h6 class="mb-0 fw-bold">Hotels</h6>
                        </a>
                    </div>
                    <div class="col-6 col-md-3" data-aos="fade-up" data-aos-delay="100">
                        <a href="#" class="category-card">
                            <i class="bi bi-cup-hot category-icon"></i>
                            <h6 class="mb-0 fw-bold">Restaurants</h6>
                        </a>
                    </div>
                    <div class="col-6 col-md-3" data-aos="fade-up" data-aos-delay="200">
                        <a href="#" class="category-card">
                            <i class="bi bi-camera category-icon"></i>
                            <h6 class="mb-0 fw-bold">Attractions</h6>
                        </a>
                    </div>
                    <div class="col-6 col-md-3" data-aos="fade-up" data-aos-delay="300">
                        <a href="#" class="category-card">
                            <i class="bi bi-balloon-heart category-icon"></i>
                            <h6 class="mb-0 fw-bold">Cafes</h6>
                        </a>
                    </div>
                </div>
            </div>

            <!-- PRODUCTS SECTION -->
            <div class="product-section" data-aos="fade-up">
                <h3 class="fw-bold text-dark">Top Destinations For You</h3>

                <div class="row g-4">
                    <c:choose>
                        <c:when test="${not empty requestScope.PRODUCT_LIST}">
                            <c:forEach var="product" items="${requestScope.PRODUCT_LIST}" varStatus="status">
                                <div class="col-md-4 mb-4" data-aos="fade-up" data-aos-delay="${status.index * 100}">
                                    <div class="card-product">
                                        
                                        <div class="card-img-wrapper">
                                            <c:choose>
                                                <c:when test="${not empty product.imageUrl}">
                                                    <img src="<c:url value='${product.imageUrl}' />"
                                                         class="card-img-top" 
                                                         alt="${product.name}">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600&h=400&fit=crop" 
                                                         class="card-img-top" 
                                                         alt="Default Image">
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="card-body">
                                            <h5 class="card-title text-truncate" title="${product.name}">${product.name}</h5>

                                            <div class="ta-rating">
                                                <c:forEach var="i" begin="1" end="5">
                                                    <c:choose>
                                                        <c:when test="${i <= product.rating}">
                                                            <i class="bi bi-star-fill"></i>
                                                        </c:when>
                                                        <c:when test="${(i - 0.5) <= product.rating}">
                                                            <i class="bi bi-star-half"></i>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="bi bi-star"></i>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                                <span class="review-count">${product.reviewCount} Reviews</span>
                                            </div>

                                            <p class="desc-text" title="${product.description}">
                                                ${product.description}
                                            </p>

                                            <div class="status-badge ${product.status == 'ACTIVE' ? 'active' : 'inactive'}">
                                                ${not empty product.status ? product.status : 'ACTIVE'}
                                            </div>

                                            <a href="${pageContext.request.contextPath}/MainController?action=ViewDetail&id=${product.productId}"
                                               class="btn btn-details w-100">
                                                View Details
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>

                        <c:otherwise>
                            <div class="col-12">
                                <div class="empty-state">
                                    <div class="empty-state-icon"><i class="bi bi-inbox"></i></div>
                                    <h4>No products available</h4>
                                    <p>We are currently updating our listings. Please check back later!</p>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </main>

        <%@include file="../../common/footer.jsp" %>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/aos@2.3.1/dist/aos.js"></script>
        <script>
            // Initialize AOS (Animate On Scroll)
            AOS.init({
                duration: 800,
                easing: 'ease-in-out-quad',
                once: true,
                offset: 100
            });

            // Smooth scroll behavior
            document.documentElement.style.scrollBehavior = 'smooth';

            // Add ripple effect to buttons
            document.querySelectorAll('.btn-details').forEach(button => {
                button.addEventListener('click', function(e) {
                    const rect = this.getBoundingClientRect();
                    const x = e.clientX - rect.left;
                    const y = e.clientY - rect.top;
                    
                    const ripple = document.createElement('span');
                    ripple.style.left = x + 'px';
                    ripple.style.top = y + 'px';
                });
            });

            // Lazy load images
            if ('IntersectionObserver' in window) {
                const imageObserver = new IntersectionObserver((entries, observer) => {
                    entries.forEach(entry => {
                        if (entry.isIntersecting) {
                            const img = entry.target;
                            img.src = img.dataset.src || img.src;
                            observer.unobserve(img);
                        }
                    });
                });

                document.querySelectorAll('img').forEach(img => imageObserver.observe(img));
            }
        </script>
    </body>
</html>