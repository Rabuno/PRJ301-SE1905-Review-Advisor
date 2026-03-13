<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Home - Review Advisor</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

        <style>

            :root{
                --primary:#34d399;
                --primary-dark:#10b981;
                --text:#1f2937;
                --bg:#f9fafb;
                --border:#e5e7eb;
            }

            body{
                font-family:'Segoe UI',sans-serif;
                background:var(--bg);
                color:var(--text);
                overflow-x:hidden;
            }

            /* HERO */

            .hero{
                background:linear-gradient(135deg,#34d399,#10b981);
                border-radius:24px;
                color:white;
                padding:90px 20px;
                text-align:center;
                box-shadow:0 20px 40px rgba(0,0,0,.1);
            }

            .hero h1{
                font-weight:800;
                font-size:3rem;
                margin-bottom:10px;
            }

            .hero p{
                opacity:.9;
            }

            /* SEARCH */

            .search-box{
                background:white;
                border-radius:60px;
                padding:8px 10px;
                display:flex;
                gap:10px;
                align-items:center;
                box-shadow:0 10px 25px rgba(0,0,0,.1);
            }

            .search-box input{
                border:none;
                flex:1;
                outline:none;
            }

            .search-btn{
                background:var(--primary);
                border:none;
                color:white;
                border-radius:40px;
                padding:10px 25px;
                font-weight:600;
            }

            .search-btn:hover{
                background:var(--primary-dark);
            }

            /* CATEGORY */

            .category-card{
                border:1px solid var(--border);
                border-radius:16px;
                padding:30px;
                text-align:center;
                background:white;
                transition:.3s;
                cursor:pointer;
            }

            .category-card:hover{
                transform:translateY(-6px);
                box-shadow:0 10px 20px rgba(0,0,0,.1);
            }

            .category-icon{
                font-size:2rem;
                color:var(--primary);
                margin-bottom:10px;
            }

            /* PRODUCT CARD */

            .card-product{
                background:white;
                border:1px solid var(--border);
                border-radius:16px;
                overflow:hidden;
                transition:.3s;
                height:100%;
            }

            .card-product:hover{
                transform:translateY(-10px);
                box-shadow:0 15px 30px rgba(0,0,0,.15);
            }

            .card-img-wrapper{
                height:220px;
                overflow:hidden;
            }

            .card-img-top{
                width:100%;
                height:100%;
                object-fit:cover;
                transition:transform .5s;
            }

            .card-product:hover img{
                transform:scale(1.08);
            }

            .card-body{
                padding:20px;
            }

            .ta-rating i{
                color:#f59e0b;
            }

            .desc{
                color:#6b7280;
                font-size:.9rem;
            }

            .btn-details{
                background:linear-gradient(135deg,#34d399,#10b981);
                border:none;
                color:white;
                padding:10px;
                border-radius:10px;
                font-weight:600;
            }

            /* REVEAL ANIMATION */

            .reveal{
                opacity:0;
                transform:translateY(40px);
                transition:all .6s cubic-bezier(.4,0,.2,1);
            }

            .reveal.active{
                opacity:1;
                transform:translateY(0);
            }

        </style>
    </head>

    <body class="d-flex flex-column min-vh-100">

        <%@include file="../../common/header.jsp"%>

        <main class="container mt-5 mb-5 flex-grow-1">

            <!-- HERO -->

            <div class="hero mb-5">

                <h1>Explore the Best Places</h1>
                <p>Discover honest reviews for hotels, restaurants, and attractions.</p>

                <form action="${pageContext.request.contextPath}/MainController" method="GET" class="mt-4 mx-auto" style="max-width:600px">

                    <input type="hidden" name="action" value="search">

                    <div class="search-box">

                        <i class="bi bi-search"></i>

                        <input type="text" name="txtSearch" placeholder="Where to?">

                        <button class="search-btn">Search</button>

                    </div>

                </form>

            </div>

            <!-- CATEGORY -->

            <div class="mb-5">

                <h3 class="fw-bold mb-4">Browse by Category</h3>

                <div class="row g-4">

                    <div class="col-6 col-md-3 reveal">
                        <div class="category-card">
                            <i class="bi bi-buildings category-icon"></i>
                            <h6>Hotels</h6>
                        </div>
                    </div>

                    <div class="col-6 col-md-3 reveal">
                        <div class="category-card">
                            <i class="bi bi-cup-hot category-icon"></i>
                            <h6>Restaurants</h6>
                        </div>
                    </div>

                    <div class="col-6 col-md-3 reveal">
                        <div class="category-card">
                            <i class="bi bi-camera category-icon"></i>
                            <h6>Attractions</h6>
                        </div>
                    </div>

                    <div class="col-6 col-md-3 reveal">
                        <div class="category-card">
                            <i class="bi bi-balloon-heart category-icon"></i>
                            <h6>Cafes</h6>
                        </div>
                    </div>

                </div>

            </div>

            <!-- PRODUCTS -->

            <div>

                <h3 class="fw-bold mb-4">Top Destinations</h3>

                <div class="row g-4">

                    <c:choose>

                        <c:when test="${not empty requestScope.PRODUCT_LIST}">

                            <c:forEach var="product" items="${requestScope.PRODUCT_LIST}">

                                <div class="col-md-4 reveal">

                                    <div class="card-product">

                                        <div class="card-img-wrapper">

                                            <c:choose>

                                                <c:when test="${not empty product.imageUrl}">

                                                    <img src="${pageContext.request.contextPath}/${product.imageUrl}" class="card-img-top">

                                                </c:when>

                                                <c:otherwise>

                                                    <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600" class="card-img-top">

                                                </c:otherwise>

                                            </c:choose>

                                        </div>

                                        <div class="card-body">

                                            <h5 class="fw-bold">${product.name}</h5>

                                            <div class="ta-rating mb-2">

                                                <c:set var="avgRating" value="${empty requestScope.PRODUCT_AVG_RATINGS[product.productId] ? 0 : requestScope.PRODUCT_AVG_RATINGS[product.productId]}"/>

                                                <c:forEach var="i" begin="1" end="5">

                                                    <c:choose>

                                                        <c:when test="${i <= avgRating}">
                                                            <i class="bi bi-star-fill"></i>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <i class="bi bi-star"></i>
                                                        </c:otherwise>

                                                    </c:choose>

                                                </c:forEach>

                                            </div>

                                            <p class="desc">${product.description}</p>

                                            <a href="${pageContext.request.contextPath}/MainController?action=ViewDetail&id=${product.productId}" class="btn btn-details w-100">
                                                View Details
                                            </a>

                                        </div>

                                    </div>

                                </div>

                            </c:forEach>

                        </c:when>

                        <c:otherwise>

                            <div class="text-center p-5 bg-white rounded shadow-sm">
                                <h4>No products available</h4>
                            </div>

                        </c:otherwise>

                    </c:choose>

                </div>

            </div>

        </main>

        <%@include file="../../common/footer.jsp"%>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

        <script>

            /* reveal animation */

            const observer = new IntersectionObserver(entries => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.classList.add("active")
                        observer.unobserve(entry.target)
                    }
                })
            }, {threshold: 0.1})

            document.querySelectorAll(".reveal").forEach(el => {
                observer.observe(el)
            })

        </script>

    </body>
</html>
