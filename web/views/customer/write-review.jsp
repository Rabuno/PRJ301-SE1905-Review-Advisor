<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Write Review - Review Advisor</title>
        <%@include file="../../common/resources.jsp" %>
    </head>
    <body class="d-flex flex-column min-vh-100">
        <%@include file="../../common/header.jsp" %>

            <style>
                /* CSS cho Interactive Star Rating */
                .star-rating {
                    display: flex;
                    flex-direction: row-reverse;
                    /* Đảo ngược để dùng selector ~ */
                    justify-content: flex-end;
                }

                .star-rating input[type="radio"] {
                    display: none;
                    /* Ẩn radio button mặc định */
                }

                .star-rating label {
                    font-size: 2.5rem;
                    color: #e4e5e9;
                    cursor: pointer;
                    transition: color 0.2s;
                    padding-right: 5px;
                }

                /* Đổi màu sao khi hover hoặc khi checked */
                .star-rating input[type="radio"]:checked~label,
                .star-rating label:hover,
                .star-rating label:hover~label {
                    color: #ffc107;
                }
            </style>

            <div class="container mt-4 mb-5">
                <div class="row justify-content-center">
                    <div class="col-md-7">
                        <div class="card shadow-lg border-0 p-4 rounded-3">
                            <h3 class="text-center mb-4 text-primary fw-bold">
                                <c:choose>
                                    <c:when test="${not empty requestScope.REVIEW}">Edit Your Review</c:when>
                                    <c:otherwise>Share Your Experience</c:otherwise>
                                </c:choose>
                            </h3>

                            <c:if test="${not empty requestScope.ERROR}">
                                <div class="alert alert-danger" role="alert">
                                    <strong>Lỗi:</strong> ${requestScope.ERROR}
                                </div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/ReviewServlet" method="POST"
                                enctype="multipart/form-data">
                                <input type="hidden" name="action"
                                    value="${not empty requestScope.REVIEW ? 'update' : 'create'}">
                                <input type="hidden" name="productId"
                                    value="${not empty requestScope.REVIEW ? requestScope.REVIEW.productId : param.id}">

                                <c:if test="${not empty requestScope.REVIEW}">
                                    <input type="hidden" name="reviewId" value="${requestScope.REVIEW.reviewId}">
                                </c:if>

                                <div class="mb-4">
                                    <label class="form-label fw-bold d-block">Your Rating <span
                                            class="text-danger">*</span></label>
                                    <div class="star-rating">
                                        <input type="radio" id="star5" name="rating" value="5"
                                            ${requestScope.REVIEW.rating==5 ? 'checked' : '' } required />
                                        <label for="star5" title="5 Stars">&#9733;</label>

                                        <input type="radio" id="star4" name="rating" value="4"
                                            ${requestScope.REVIEW.rating==4 ? 'checked' : '' } />
                                        <label for="star4" title="4 Stars">&#9733;</label>

                                        <input type="radio" id="star3" name="rating" value="3"
                                            ${requestScope.REVIEW.rating==3 ? 'checked' : '' } />
                                        <label for="star3" title="3 Stars">&#9733;</label>

                                        <input type="radio" id="star2" name="rating" value="2"
                                            ${requestScope.REVIEW.rating==2 ? 'checked' : '' } />
                                        <label for="star2" title="2 Stars">&#9733;</label>

                                        <input type="radio" id="star1" name="rating" value="1"
                                            ${requestScope.REVIEW.rating==1 ? 'checked' : '' } />
                                        <label for="star1" title="1 Star">&#9733;</label>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Review Content <span
                                            class="text-danger">*</span></label>
                                    <textarea id="reviewContent" name="content" class="form-control" rows="5"
                                        placeholder="Share details of your own experience at this place..."
                                        required>${not empty requestScope.REVIEW ? requestScope.REVIEW.content : ''}</textarea>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Upload Evidence (Optional)</label>
                                    <input class="form-control" type="file" id="evidenceImage" name="evidenceImage"
                                        accept="image/png, image/jpeg, image/jpg" multiple>
                                    <small class="text-muted">Attach receipts or photos to verify your experience. This
                                        boosts trust scores.</small>
                                </div>

                                <c:if test="${not empty requestScope.REVIEW}">
                                    <div class="mb-4">
                                        <label class="form-label fw-bold text-danger">Reason for Edit (Required for
                                            Audit) <span class="text-danger">*</span></label>
                                        <input type="text" name="editReason" class="form-control border-danger"
                                            placeholder="e.g., Fixing a typo, updating my experience..." required>
                                    </div>
                                </c:if>

                                <button type="submit" class="btn btn-primary w-100 py-3 fw-bold fs-5">
                                    <c:choose>
                                        <c:when test="${not empty requestScope.REVIEW}">Update Review</c:when>
                                        <c:otherwise>Submit Review</c:otherwise>
                                    </c:choose>
                                </button>

                                <p class="text-muted mt-3 text-center small">
                                    <i class="bi bi-shield-check"></i> Your review is analyzed by AI Triage to ensure
                                    platform integrity.
                                </p>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <%@include file="../../common/footer.jsp" %>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
