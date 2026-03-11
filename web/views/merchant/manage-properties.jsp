<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@include file="../../common/header.jsp" %>

            <div class="container mt-5 mb-5 flex-grow-1">
                <div class="row justify-content-center">
                    <div class="col-md-8">
                        <div class="card shadow-lg border-0 rounded-3 p-4">
                            <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
                                <h3 class="text-success fw-bold mb-0"><i class="bi bi-house-add"></i> Add New Property
                                </h3>
                                <a href="${pageContext.request.contextPath}/MerchantServlet?action=ManageProperties"
                                    class="btn btn-outline-secondary btn-sm fw-bold">
                                    <i class="bi bi-arrow-left"></i> Back to List
                                </a>
                            </div>

                            <c:if test="${not empty sessionScope.ERROR_MSG}">
                                <div class="alert alert-danger shadow-sm fw-bold" role="alert">
                                    <i class="bi bi-exclamation-triangle-fill me-2"></i>${sessionScope.ERROR_MSG}
                                </div>
                                <c:remove var="ERROR_MSG" scope="session" />
                            </c:if>
                            <c:if test="${not empty sessionScope.SUCCESS_MSG}">
                                <div class="alert alert-success shadow-sm fw-bold" role="alert">
                                    <i class="bi bi-check-circle-fill me-2"></i>${sessionScope.SUCCESS_MSG}
                                </div>
                                <c:remove var="SUCCESS_MSG" scope="session" />
                            </c:if>

                            <form action="${pageContext.request.contextPath}/MerchantServlet" method="POST"
                                enctype="multipart/form-data">
                                <input type="hidden" name="action" value="CreateProperty">

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Property Name <span
                                            class="text-danger">*</span></label>
                                    <input type="text" name="name" class="form-control"
                                        placeholder="Enter property name (e.g., Sunrise Resort)" required>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Category <span
                                            class="text-danger">*</span></label>
                                    <select name="category" class="form-select" required>
                                        <option value="" disabled selected>Select a category...</option>
                                        <option value="Accommodation">Accommodation</option>
                                        <option value="Dining">Dining</option>
                                        <option value="Attraction">Attraction</option>
                                        <option value="Transport">Transport</option>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Price (VND)</label>
                                    <input type="number" name="price" class="form-control" placeholder="e.g. 500000"
                                        min="0" step="1000">
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Image</label>
                                    <input type="file" name="image" class="form-control"
                                        accept="image/jpeg,image/png,image/webp">
                                    <small class="text-muted">Upload a cover photo (JPG, PNG, WEBP. Max 10MB).</small>
                                </div>

                                <div class="mb-4">
                                    <label class="form-label fw-bold">Description <span
                                            class="text-danger">*</span></label>
                                    <textarea name="description" class="form-control" rows="5"
                                        placeholder="Describe the amenities, location, and unique features..."
                                        required></textarea>
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