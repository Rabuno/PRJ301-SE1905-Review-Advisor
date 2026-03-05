<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow p-4">
                <h3 class="text-center mb-4">
                    <c:choose>
                        <c:when test="${not empty requestScope.REVIEW}">Edit Your Review</c:when>
                        <c:otherwise>Share Your Experience</c:otherwise>
                    </c:choose>
                </h3>
                
                <form action="${pageContext.request.contextPath}/ReviewServlet" method="POST">
                    <input type="hidden" name="action" value="${not empty requestScope.REVIEW ? 'update' : 'create'}">
                    <input type="hidden" name="productId" value="${not empty requestScope.REVIEW ? requestScope.REVIEW.productId : param.id}">
                    
                    <c:if test="${not empty requestScope.REVIEW}">
                        <input type="hidden" name="reviewId" value="${requestScope.REVIEW.reviewId}">
                    </c:if>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Rating (1-5 Stars)</label>
                        <select name="rating" class="form-select" required>
                            <option value="5" ${requestScope.REVIEW.rating == 5 ? 'selected' : ''}>5 Stars - Excellent</option>
                            <option value="4" ${requestScope.REVIEW.rating == 4 ? 'selected' : ''}>4 Stars - Good</option>
                            <option value="3" ${requestScope.REVIEW.rating == 3 ? 'selected' : ''}>3 Stars - Average</option>
                            <option value="2" ${requestScope.REVIEW.rating == 2 ? 'selected' : ''}>2 Stars - Poor</option>
                            <option value="1" ${requestScope.REVIEW.rating == 1 ? 'selected' : ''}>1 Star - Terrible</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Review Content</label>
                        <textarea id="reviewContent" name="content" class="form-control" rows="5" 
                                  placeholder="Share your honest thoughts for our AI to analyze..." required>${not empty requestScope.REVIEW ? requestScope.REVIEW.content : ''}</textarea>
                    </div>

                    <c:if test="${not empty requestScope.REVIEW}">
                        <div class="mb-3">
                            <label class="form-label fw-bold text-danger">Reason for Edit (Required for Audit)</label>
                            <input type="text" name="editReason" class="form-control border-danger" placeholder="e.g., Fixing a typo, updating my experience..." required>
                            <small class="text-muted">All review modifications are logged securely to maintain platform trust.</small>
                        </div>
                    </c:if>

                    <div class="p-3 bg-light rounded border mb-3">
                        <p class="text-muted small fw-bold mb-1">LIVE PREVIEW:</p>
                        <div id="previewArea" class="small fst-italic text-secondary">
                            ${not empty requestScope.REVIEW ? requestScope.REVIEW.content : 'Please enter your review to see the preview...'}
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2 fw-bold">
                        <c:choose>
                            <c:when test="${not empty requestScope.REVIEW}">Update Review</c:when>
                            <c:otherwise>Submit Review</c:otherwise>
                        </c:choose>
                    </button>
                    
                    <p class="text-muted mt-2 text-center" style="font-size: 0.75rem;">
                        * Your review will be analyzed by our AI Triage system before being published.
                    </p>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    const contentInput = document.getElementById('reviewContent');
    const previewArea = document.getElementById('previewArea');

    contentInput.addEventListener('input', function() {
        if (this.value.trim().length > 0) {
            previewArea.innerHTML = `<span class="text-dark">"` + this.value + `"</span>`;
        } else {
            previewArea.innerHTML = "Please enter your review to see the preview...";
        }
    });
</script>

<%@include file="../../common/footer.jsp" %>