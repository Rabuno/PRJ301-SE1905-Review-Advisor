<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Edit Property - Merchant</title>
        <%@include file="../../common/resources.jsp" %>
    </head>
    <body class="d-flex flex-column min-vh-100">
        <%@include file="../../common/header.jsp" %>

            <div class="container mt-5 mb-5 flex-grow-1">
                <div class="row justify-content-center">
                    <div class="col-md-8">
                        <div class="card shadow-lg border-0 rounded-3 p-4">
                            <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
                                <h3 class="text-warning fw-bold mb-0">
                                    <i class="bi bi-pencil-square"></i> Edit Property
                                </h3>
                                <a href="${pageContext.request.contextPath}/MerchantServlet?action=ManageProperties"
                                    class="btn btn-outline-secondary btn-sm fw-bold">
                                    <i class="bi bi-arrow-left"></i> Back to List
                                </a>
                            </div>

                            <c:if test="${not empty requestScope.ERROR_MSG}">
                                <div class="alert alert-danger shadow-sm fw-bold" role="alert">
                                    <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope.ERROR_MSG}
                                </div>
                            </c:if>

                            <c:if test="${not empty requestScope.PRODUCT}">
                                <div class="mb-3 text-center">
                                    <c:set var="img" value="${requestScope.PRODUCT.imageUrl}"/>
                                    <img src="${empty img ? pageContext.request.contextPath.concat('/assets/default/default.jpg') : (img.startsWith(pageContext.request.contextPath) ? img : pageContext.request.contextPath.concat(img.startsWith('/') ? '' : '/').concat(img))}"
                                         alt="Current Photo" class="img-fluid rounded shadow-sm"
                                         style="max-height: 200px; object-fit: cover; width: 100%;"
                                         onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/default/default.jpg';">
                                    <p class="text-muted small mt-1">Current product image</p>
                                </div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/MerchantServlet" method="POST"
                                enctype="multipart/form-data">
                                <input type="hidden" name="action" value="UpdateProperty">
                                <input type="hidden" name="productId" value="${requestScope.PRODUCT.productId}">

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Property Name <span
                                            class="text-danger">*</span></label>
                                    <input type="text" name="name" class="form-control"
                                        value="${requestScope.PRODUCT.name}" required>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Category <span
                                            class="text-danger">*</span></label>
                                    <select name="category" class="form-select" required>
                                        <option value="Hotels" ${requestScope.PRODUCT.category=='Hotels' ? 'selected' : ''}>Hotels</option>
                                        <option value="Restaurants" ${requestScope.PRODUCT.category=='Restaurants' ? 'selected' : ''}>Restaurants</option>
                                        <option value="Attractions" ${requestScope.PRODUCT.category=='Attractions' ? 'selected' : ''}>Attractions</option>
                                        <option value="Cafes" ${requestScope.PRODUCT.category=='Cafes' ? 'selected' : ''}>Cafes</option>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Price (VND)</label>
                                    <input type="number" name="price" class="form-control" min="0" step="1000"
                                           value="${requestScope.PRODUCT.price}">
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Status</label>
                                    <div class="form-control bg-light">
                                        <span class="badge bg-light text-dark border">${requestScope.PRODUCT.status}</span>
                                        <span class="text-muted small ms-2">Managed by Moderator/Admin after AI scan.</span>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">
                                        <i class="bi bi-image text-success"></i> Change Product Image
                                    </label>
                                    <input type="file" name="image" class="form-control" accept="image/*">
                                    <small class="text-muted">
                                        Leave empty to keep the current image. Accepted: JPG, PNG, GIF (max 10MB).
                                    </small>
                                </div>

                                <div class="mb-4">
                                    <label class="form-label fw-bold">Description <span
                                            class="text-danger">*</span></label>
                                    <textarea name="description" class="form-control" rows="5"
                                        required>${requestScope.PRODUCT.description}</textarea>
                                </div>

                                <button type="submit" class="btn btn-warning w-100 py-2 fw-bold fs-5 text-white">
                                    <i class="bi bi-save me-1"></i> Save Changes
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <%@include file="../../common/footer.jsp" %>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
