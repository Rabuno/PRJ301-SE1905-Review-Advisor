<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Bảng Điều Khiển Kiểm Duyệt - Review Advisor</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        </head>

        <body class="bg-light">

            <!-- Header Navbar -->
            <jsp:include page="/common/header.jsp" />

            <div class="container mt-5">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="text-danger fw-bold">Bảng Điều Khiển Kiểm Duyệt (Moderation Dashboard)</h2>
                </div>

                <c:if test="${not empty sessionScope.SUCCESS_MSG}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${sessionScope.SUCCESS_MSG}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="SUCCESS_MSG" scope="session" />
                </c:if>

                <div class="card shadow-sm border-0">
                    <div class="card-header bg-dark text-white fw-bold py-3">
                        Danh sách Đánh giá bị Hệ thống Đánh cờ (FLAGGED)
                    </div>
                    <div class="card-body p-0">
                        <table class="table table-hover table-striped mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th class="ps-4">Mã Review</th>
                                    <th>Nội dung bị chặn</th>
                                    <th>Xếp hạng</th>
                                    <th>Thời gian gửi</th>
                                    <th class="text-center">Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${requestScope.FLAGGED_REVIEWS}">
                                    <tr>
                                        <td class="ps-4"><strong>${r.reviewId}</strong></td>
                                        <td class="text-wrap text-danger" style="max-width: 400px;">${r.content}</td>
                                        <td>${r.rating} ⭐</td>
                                        <td>${r.createdAt}</td>
                                        <td class="text-center">
                                            <form action="${pageContext.request.contextPath}/ModeratorServlet"
                                                method="POST" class="d-inline">
                                                <input type="hidden" name="reviewId" value="${r.reviewId}">
                                                <input type="hidden" name="action" value="APPROVE">
                                                <button type="submit" class="btn btn-sm btn-success me-1">Cho
                                                    phép</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/ModeratorServlet"
                                                method="POST" class="d-inline">
                                                <input type="hidden" name="reviewId" value="${r.reviewId}">
                                                <input type="hidden" name="action" value="REJECT">
                                                <button type="submit" class="btn btn-sm btn-danger">Xóa bỏ</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty requestScope.FLAGGED_REVIEWS}">
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-5">
                                            <span class="fs-5">Hệ thống AI hiện chưa phát hiện thêm đánh giá vi phạm
                                                nào.</span>
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>