<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>${not empty requestScope.PRODUCT ? requestScope.PRODUCT.name : 'Product Details'} - Review Advisor</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

        <style>
            /* CSS phong cách TripAdvisor */
            .ta-title {
                font-size: 2rem;
                font-weight: 800;
                color: #000;
            }
            .ta-rating-stars {
                color: #00aa6c;
                font-size: 1.1rem;
            }

            /* Chỉnh lại CSS để hiển thị 1 ảnh to duy nhất */
            .img-main {
                width: 100%;
                height: 450px;
                object-fit: cover;
                border-radius: 12px;
            }

            .sticky-booking-widget {
                top: 100px;
                box-shadow: 0 4px 16px rgba(0,0,0,0.1);
                border: 1px solid #e0e0e0;
                border-radius: 12px;
            }
            .btn-ta-primary {
                background-color: #f2b203;
                color: #000;
                font-weight: bold;
                border-radius: 24px;
                padding: 12px 24px;
                border: none;
            }
            .btn-ta-primary:hover {
                background-color: #e5a000;
            }
            .review-avatar {
                width: 48px;
                height: 48px;
                border-radius: 50%;
                object-fit: cover;
            }
            .review-card {
                border-bottom: 1px solid #e0e0e0;
                padding-bottom: 1.5rem;
                margin-bottom: 1.5rem;
            }
        </style>
    </head>

    <body class="bg-white d-flex flex-column min-vh-100">

        <%@include file="../../common/header.jsp" %>

        <div class="container mt-3 mb-5 flex-grow-1">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/MainController" class="text-decoration-none text-dark">Home</a></li>
                    <li class="breadcrumb-item"><a href="#" class="text-decoration-none text-dark">${not empty requestScope.PRODUCT.category ? requestScope.PRODUCT.category : 'General'}</a></li>
                    <li class="breadcrumb-item active" aria-current="page">${requestScope.PRODUCT.name}</li>
                </ol>
            </nav>

            <h1 class="ta-title mb-2">${not empty requestScope.PRODUCT ? requestScope.PRODUCT.name : 'Unknown Product'}</h1>

            <div class="d-flex align-items-center mb-3">
                <div class="ta-rating-stars me-2">
                    <i class="bi bi-circle-fill"></i><i class="bi bi-circle-fill"></i><i class="bi bi-circle-fill"></i><i class="bi bi-circle-fill"></i><i class="bi bi-circle-half"></i>
                </div>
                <span class="text-muted text-decoration-underline me-3">${TOTAL_REVIEWS} Reviews</span>
                <span class="badge bg-light text-dark border"><i class="bi bi-trophy text-warning"></i> Travelers' Choice 2024</span>
            </div>

            <div class="mb-5 shadow-sm rounded-4 overflow-hidden">
                <c:choose>
                    <%-- NẾU CÓ ẢNH TRONG DATABASE: Hiện ảnh thật --%>
                    <c:when test="${not empty product.imageUrl}">
                        <img src="<c:url value='${product.imageUrl}' />"
                             class="card-img-top" 
                             alt="${product.name}" 
                             style="height: 220px; object-fit: cover;">
                    </c:when>

                    <%-- NẾU KHÔNG CÓ ẢNH (NULL): Hiện ảnh default.jpg --%>
                    <c:otherwise>
                        <img src="<c:url value='/assets/default/default.jpg' />" 
                             class="card-img-top" 
                             alt="Default Image" 
                             style="height: 220px; object-fit: cover;">
                    </c:otherwise>
                </c:choose>F
            </div>

            <div class="row">
                <div class="col-lg-8 pe-lg-5">
                    <h3 class="fw-bold mb-3">About this product</h3>
                    <p class="fs-6 lh-lg text-dark">
                        ${not empty requestScope.PRODUCT.description ? requestScope.PRODUCT.description : 'No description available for this product.'}
                    </p>
                    <hr class="my-4">

                    <h3 class="fw-bold mb-4" id="reviews-section">Reviews</h3>
                    <div class="row mb-5">
                        <div class="col-md-3 text-center">
                            <h1 class="display-3 fw-bold mb-0">${TOTAL_REVIEWS > 0 ? AVERAGE_RATING : '0.0'}</h1>
                            <div class="col-md-3 text-center">
                                <h1 class="display-3 fw-bold mb-0">${TOTAL_REVIEWS > 0 ? AVERAGE_RATING : '0.0'}</h1>
                                <div class="ta-rating-stars mb-2">
                                    <%-- Lấy giá trị rating hiện tại, nếu chưa có review thì mặc định là 0 --%>
                                    <c:set var="currentRating" value="${TOTAL_REVIEWS > 0 ? AVERAGE_RATING : 0}" />

                                    <%-- Lặp 5 lần để in ra 5 biểu tượng --%>
                                    <c:forEach var="i" begin="1" end="5">
                                        <c:choose>
                                            <%-- Nếu điểm lớn hơn hoặc bằng vị trí hiện tại -> Tròn đặc --%>
                                            <c:when test="${currentRating >= i}">
                                                <i class="bi bi-circle-fill text-success"></i>
                                            </c:when>
                                            <%-- Nếu điểm lớn hơn hoặc bằng vị trí hiện tại trừ 0.5 -> Tròn khuyết (ví dụ: 4.5) --%>
                                            <c:when test="${currentRating >= (i - 0.5)}">
                                                <i class="bi bi-circle-half text-success"></i>
                                            </c:when>
                                            <%-- Còn lại -> Tròn rỗng --%>
                                            <c:otherwise>
                                                <i class="bi bi-circle text-success"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                                <p class="text-muted">${TOTAL_REVIEWS} reviews</p>
                            </div>
                        </div>
                        <div class="col-md-9">
                            <div class="d-flex align-items-center mb-1">
                                <span class="me-2" style="width: 40px;">5 <i class="bi bi-star-fill text-muted small"></i></span>
                                <div class="progress flex-grow-1" style="height: 12px;">
                                    <div class="progress-bar bg-success" style="width: ${TOTAL_REVIEWS > 0 ? (STAR_COUNTS[5] * 100 / TOTAL_REVIEWS) : 0}%;"></div>
                                </div>
                                <span class="ms-2 small text-muted">${not empty STAR_COUNTS ? STAR_COUNTS[5] : 0}</span>
                            </div>

                            <div class="d-flex align-items-center mb-1">
                                <span class="me-2" style="width: 40px;">4 <i class="bi bi-star-fill text-muted small"></i></span>
                                <div class="progress flex-grow-1" style="height: 12px;">
                                    <div class="progress-bar bg-success" style="width: ${TOTAL_REVIEWS > 0 ? (STAR_COUNTS[4] * 100 / TOTAL_REVIEWS) : 0}%;"></div>
                                </div>
                                <span class="ms-2 small text-muted">${not empty STAR_COUNTS ? STAR_COUNTS[4] : 0}</span>
                            </div>

                            <div class="d-flex align-items-center mb-1">
                                <span class="me-2" style="width: 40px;">3 <i class="bi bi-star-fill text-muted small"></i></span>
                                <div class="progress flex-grow-1" style="height: 12px;">
                                    <div class="progress-bar bg-warning" style="width: ${TOTAL_REVIEWS > 0 ? (STAR_COUNTS[3] * 100 / TOTAL_REVIEWS) : 0}%;"></div>
                                </div>
                                <span class="ms-2 small text-muted">${not empty STAR_COUNTS ? STAR_COUNTS[3] : 0}</span>
                            </div>

                            <div class="d-flex align-items-center mb-1">
                                <span class="me-2" style="width: 40px;">2 <i class="bi bi-star-fill text-muted small"></i></span>
                                <div class="progress flex-grow-1" style="height: 12px;">
                                    <div class="progress-bar bg-warning" style="width: ${TOTAL_REVIEWS > 0 ? (STAR_COUNTS[2] * 100 / TOTAL_REVIEWS) : 0}%;"></div>
                                </div>
                                <span class="ms-2 small text-muted">${not empty STAR_COUNTS ? STAR_COUNTS[2] : 0}</span>
                            </div>

                            <div class="d-flex align-items-center mb-1">
                                <span class="me-2" style="width: 40px;">1 <i class="bi bi-star-fill text-muted small"></i></span>
                                <div class="progress flex-grow-1" style="height: 12px;">
                                    <div class="progress-bar bg-danger" style="width: ${TOTAL_REVIEWS > 0 ? (STAR_COUNTS[1] * 100 / TOTAL_REVIEWS) : 0}%;"></div>
                                </div>
                                <span class="ms-2 small text-muted">${not empty STAR_COUNTS ? STAR_COUNTS[1] : 0}</span>
                            </div>
                        </div>
                    </div>

                    <div class="mb-4">
                        <a href="${pageContext.request.contextPath}/views/customer/write-review.jsp?id=${requestScope.PRODUCT.productId}" class="btn btn-outline-dark fw-bold rounded-pill px-4 py-2">
                            Write a review
                        </a>
                    </div>

                    <div class="review-list">
                        <c:choose>
                            <c:when test="${not empty requestScope.REVIEWS}">
                                <c:forEach var="r" items="${requestScope.REVIEWS}">
                                    <div class="review-card">
                                        <div class="d-flex align-items-center mb-2">
                                            <img src="https://ui-avatars.com/api/?name=${r.userId}&background=random" class="review-avatar me-3" alt="User">
                                            <div>
                                                <strong class="text-dark fs-6">${r.userId}</strong>
                                                <div class="text-muted small">
                                                    <span class="ta-rating-stars me-2">
                                                        <c:forEach begin="1" end="5" var="i">
                                                            <c:choose>
                                                                <c:when test="${i <= r.rating}"><i class="bi bi-circle-fill"></i></c:when>
                                                                <c:otherwise><i class="bi bi-circle"></i></c:otherwise>
                                                            </c:choose>
                                                        </c:forEach>
                                                    </span>
                                                    Reviewed on ${r.createdAt}
                                                </div>
                                            </div>
                                        </div>
                                        <p class="text-dark lh-base mt-2">${r.content}</p>
                                        <button class="btn btn-sm btn-light fw-bold border rounded-pill px-3 mt-2">
                                            <i class="bi bi-hand-thumbs-up me-1"></i> Helpful
                                        </button>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5 bg-light rounded-3">
                                    <h5>No reviews yet</h5>
                                    <p>Be the first to share your experience!</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="col-lg-4 mt-4 mt-lg-0">
                    <div class="sticky-top sticky-booking-widget bg-white p-4">
                        <h3 class="fw-bold mb-0">From <span class="text-dark">$${not empty requestScope.PRODUCT.price ? requestScope.PRODUCT.price : '199.00'}</span></h3>
                        <p class="text-muted small">Lowest Price Guarantee</p>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Select Date and Travelers</label>
                            <input type="date" class="form-control mb-2">
                            <select class="form-select">
                                <option>2 Adults</option>
                                <option>1 Adult</option>
                                <option>Family (2 Adults, 2 Children)</option>
                            </select>
                        </div>

                        <button class="btn btn-ta-primary w-100 fs-5 mb-3">Check Availability</button>

                        <ul class="list-unstyled small lh-lg mb-0 text-dark">
                            <li><i class="bi bi-calendar-x me-2"></i> Free cancellation.</li>
                            <li><i class="bi bi-phone me-2"></i> Mobile ticket accepted.</li>
                            <li><i class="bi bi-clock me-2"></i> Duration: Flexible.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <%@include file="../../common/footer.jsp" %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>