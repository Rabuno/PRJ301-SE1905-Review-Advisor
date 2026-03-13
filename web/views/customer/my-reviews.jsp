<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>My Reviews - Review Advisor</title>
        <%@include file="../../common/resources.jsp" %>
    </head>
    <body class="d-flex flex-column min-vh-100">
        <%@include file="../../common/header.jsp" %>

            <div class="container mt-4 mb-5">
                <div class="row mb-4">
                    <div class="col-12">
                        <h2 class="fw-bold text-success">My Reviews</h2>
                        <p class="text-muted">Track the status of your submitted reviews and AI moderation results.</p>
                    </div>
                </div>

                <div class="row">
                    <div class="col-12">
                        <div class="card shadow-sm border-0">
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th scope="col" class="ps-4">Product ID</th>
                                                <th scope="col">Rating</th>
                                                <th scope="col" style="width: 35%;">Your Review</th>
                                                <th scope="col">Date Submitted</th>
                                                <th scope="col" class="text-center">Status</th>
                                                <th scope="col" class="text-center">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${not empty requestScope.MY_REVIEWS}">
                                                    <c:forEach var="r" items="${requestScope.MY_REVIEWS}">
                                                        <tr>
                                                            <td class="ps-4 fw-bold">${r.productId}</td>
                                                            <td class="text-warning">${r.rating} ★</td>
                                                            <td class="text-muted small">"${r.content}"</td>
                                                            <td class="small">${r.createdAt}</td>
                                                            <td class="text-center">
                                                                <c:choose>
                                                                    <c:when test="${r.status eq 'PUBLISHED'}">
                                                                        <span class="badge bg-success">PUBLISHED</span>
                                                                    </c:when>
                                                                    <c:when test="${r.status eq 'FLAGGED'}">
                                                                        <span class="badge bg-danger">FLAGGED</span>
                                                                        <div class="text-danger small mt-1"
                                                                            style="font-size: 0.7rem;">AI Detected Risk
                                                                        </div>
                                                                    </c:when>
                                                                    <c:when test="${r.status eq 'HIDDEN'}">
                                                                        <span class="badge bg-secondary">HIDDEN</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span
                                                                            class="badge bg-warning text-dark">${r.status}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td class="text-center">
                                                                <a href="${pageContext.request.contextPath}/ReviewServlet?action=edit&reviewId=${r.reviewId}"
                                                                    class="btn btn-sm btn-outline-primary me-1">Edit</a>

                                                                <form
                                                                    action="${pageContext.request.contextPath}/ReviewServlet"
                                                                    method="GET" class="d-inline"
                                                                    onsubmit="return confirm('Are you sure you want to delete this review?');">
                                                                    <input type="hidden" name="action" value="delete">
                                                                    <input type="hidden" name="reviewId"
                                                                        value="${r.reviewId}">
                                                                    <input type="hidden" name="source"
                                                                        value="myreviews">
                                                                    <button type="submit"
                                                                        class="btn btn-sm btn-outline-danger">Delete</button>
                                                                </form>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="6" class="text-center text-muted py-5">
                                                            <span class="fs-5">You haven't written any reviews
                                                                yet.</span>
                                                        </td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="mt-4">
                            <a href="${pageContext.request.contextPath}/MainController"
                                class="btn btn-outline-secondary px-4">&larr; Back to Explore</a>
                        </div>
                    </div>
                </div>
            </div>

            <%@include file="../../common/footer.jsp" %>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
