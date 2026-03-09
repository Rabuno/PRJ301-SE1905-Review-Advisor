<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@include file="../../common/header.jsp" %>

            <div class="container mt-5 mb-5 flex-grow-1">
                <c:if test="${not empty sessionScope.SUCCESS_MSG}">
                    <div class="alert alert-success alert-dismissible fade show fw-bold" role="alert">
                        <i class="bi bi-check-circle-fill me-2"></i>${sessionScope.SUCCESS_MSG}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <c:remove var="SUCCESS_MSG" scope="session" />
                </c:if>

                <%-- Khi có danh sách sản phẩm (action=ManageProperties) --%>
                    <c:choose>
                        <c:when test="${not empty requestScope.PRODUCTS}">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h3 class="text-success fw-bold mb-0">
                                    <i class="bi bi-building"></i> My Properties
                                </h3>
                                <a href="${pageContext.request.contextPath}/MerchantServlet?action=AddProperty"
                                    class="btn btn-success fw-bold shadow-sm">
                                    <i class="bi bi-plus-circle"></i> Add New Property
                                </a>
                            </div>

                            <div class="row g-4">
                                <c:forEach var="p" items="${requestScope.PRODUCTS}">
                                    <div class="col-md-6 col-lg-4">
                                        <div class="card shadow-sm border-0 h-100">
                                            <img src="${p.imageUrl}" class="card-img-top" alt="${p.name}"
                                                style="height: 180px; object-fit: cover;">
                                            <div class="card-body">
                                                <h5 class="card-title fw-bold text-truncate">${p.name}</h5>
                                                <p class="text-muted small mb-1">
                                                    <i class="bi bi-tag"></i> ${p.category}
                                                </p>
                                                <p class="card-text small text-truncate text-muted">${p.description}</p>
                                            </div>
                                            <div class="card-footer bg-white border-0 pb-3">
                                                <a href="${pageContext.request.contextPath}/MerchantServlet?action=EditProperty&productId=${p.productId}"
                                                    class="btn btn-outline-warning btn-sm fw-bold w-100">
                                                    <i class="bi bi-pencil"></i> Edit / Change Image
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>

                        <%-- Khi là form thêm mới (action=AddProperty hoặc mặc định) --%>
                            <c:otherwise>
                                <div class="row justify-content-center">
                                    <div class="col-md-8">
                                        <div class="card shadow-lg border-0 rounded-3 p-4">
                                            <div
                                                class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
                                                <h3 class="text-success fw-bold mb-0">
                                                    <i class="bi bi-house-add"></i> Add New Property
                                                </h3>
                                                <a href="${pageContext.request.contextPath}/MerchantServlet?action=ManageProperties"
                                                    class="btn btn-outline-secondary btn-sm fw-bold">
                                                    <i class="bi bi-arrow-left"></i> Back to List
                                                </a>
                                            </div>

                                            <c:if test="${not empty requestScope.ERROR_MSG}">
                                                <div class="alert alert-danger shadow-sm fw-bold" role="alert">
                                                    <i
                                                        class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.ERROR_MSG}
                                                </div>
                                            </c:if>

                                            <form action="${pageContext.request.contextPath}/MerchantServlet"
                                                method="POST" enctype="multipart/form-data">
                                                <input type="hidden" name="action" value="CreateProperty">

                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">Property Name <span
                                                            class="text-danger">*</span></label>
                                                    <input type="text" name="propertyName" class="form-control"
                                                        placeholder="Enter property name (e.g., Sunrise Resort)"
                                                        required>
                                                </div>

                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">Category <span
                                                            class="text-danger">*</span></label>
                                                    <select name="category" class="form-select" required>
                                                        <option value="" disabled selected>Select a category...</option>
                                                        <option value="Hotel">Hotel</option>
                                                        <option value="Resort">Resort</option>
                                                        <option value="Restaurant">Restaurant</option>
                                                        <option value="Attraction">Attraction</option>
                                                    </select>
                                                </div>

                                                <div class="mb-3">
                                                    <label class="form-label fw-bold">
                                                        <i class="bi bi-image text-success"></i> Property Image
                                                        (Optional)
                                                    </label>
                                                    <input type="file" name="productImage" class="form-control"
                                                        accept="image/*">
                                                    <small class="text-muted">Upload an image from your computer to use
                                                        as the cover photo.</small>
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
                            </c:otherwise>
                    </c:choose>
            </div>

            <%@include file="../../common/footer.jsp" %>