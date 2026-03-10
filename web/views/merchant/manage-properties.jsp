<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-5 mb-5 flex-grow-1">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-lg border-0 rounded-3 p-4">
                <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
                    <h3 class="text-success fw-bold mb-0"><i class="bi bi-house-add"></i> Add New Property</h3>
                    <a href="${pageContext.request.contextPath}/MerchantServlet?action=ManageProperties" class="btn btn-outline-secondary btn-sm fw-bold">
                        <i class="bi bi-arrow-left"></i> Back to List
                    </a>
                </div>

                <c:if test="${not empty requestScope.ERROR_MSG}">
                    <div class="alert alert-danger shadow-sm fw-bold" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.ERROR_MSG}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/MerchantServlet" method="POST">
                    <input type="hidden" name="action" value="CreateProperty">
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold">Property Name <span class="text-danger">*</span></label>
                        <input type="text" name="propertyName" class="form-control" placeholder="Enter property name (e.g., Sunrise Resort)" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Category <span class="text-danger">*</span></label>
                        <select name="category" class="form-select" required>
                            <option value="" disabled selected>Select a category...</option>
                            <option value="Hotel">Hotel</option>
                            <option value="Resort">Resort</option>
                            <option value="Restaurant">Restaurant</option>
                            <option value="Attraction">Attraction</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Image URL</label>
                        <input type="url" name="imageUrl" class="form-control" placeholder="https://example.com/image.jpg">
                        <small class="text-muted">Provide a valid image URL for the cover photo.</small>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-bold">Description <span class="text-danger">*</span></label>
                        <textarea name="description" class="form-control" rows="5" placeholder="Describe the amenities, location, and unique features..." required></textarea>
                    </div>

                    <button type="submit" class="btn btn-success w-100 py-2 fw-bold fs-5">
                        <i class="bi bi-save me-1"></i> Save Property
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>