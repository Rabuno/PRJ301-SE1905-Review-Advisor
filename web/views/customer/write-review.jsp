<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../../common/header.jsp" %>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow p-4">
                <h3 class="text-center mb-4">Share Your Experience</h3>
                <form action="ReviewServlet" method="POST">
                    <input type="hidden" name="productId" value="${param.id}">
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold">Rating (1-5 Stars)</label>
                        <select name="rating" class="form-select" required>
                            <option value="5">5 Stars - Excellent</option>
                            <option value="4">4 Stars - Good</option>
                            <option value="3">3 Stars - Average</option>
                            <option value="2">2 Stars - Poor</option>
                            <option value="1">1 Star - Terrible</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Review Content</label>
                        <textarea id="reviewContent" name="content" class="form-control" rows="5" 
                                  placeholder="Share your honest thoughts for our AI to analyze..." required></textarea>
                    </div>

                    <div class="p-3 bg-light rounded border mb-3">
                        <p class="text-muted small fw-bold mb-1">LIVE PREVIEW:</p>
                        <div id="previewArea" class="small fst-italic text-secondary">Please enter your review to see the preview...</div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2">Submit Review</button>
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
            previewArea.innerHTML = `<span class="text-dark">"${this.value}"</span>`;
        } else {
            previewArea.innerHTML = "Please enter your review to see the preview...";
        }
    });
</script>
<%@include file="../../common/footer.jsp" %>