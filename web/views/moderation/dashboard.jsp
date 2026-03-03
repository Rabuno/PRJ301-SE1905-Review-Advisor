<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-5 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-danger fw-bold mb-1">Moderation Dashboard</h2>
            <p class="text-muted small mb-0">Hệ thống phân tích và đánh cờ đánh giá vi phạm (AI Triage)</p>
        </div>
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
            Danh sách Đánh giá bị Đánh cờ (FLAGGED REVIEWS)
        </div>
        <div class="card-body p-0 table-responsive">
            <table class="table table-hover table-striped mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">Review ID</th>
                        <th style="width: 25%;">Nội dung nghi ngờ</th>
                        <th style="width: 25%;">Giải thích Hệ thống (Evidence-based)</th>
                        <th style="width: 20%;">Phân tích AI (Model-based)</th>
                        <th class="text-center">Hành động xử lý</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${requestScope.FLAGGED_REVIEWS}">
                        <tr>
                            <td class="ps-4 font-monospace"><strong>#${r.reviewId}</strong></td>
                            <td class="text-wrap">
                                <span class="badge bg-secondary mb-1">${r.rating} ⭐</span><br>
                                <span class="text-danger">${r.content}</span>
                            </td>
                            
                            <td class="small text-muted">
                                <ul class="mb-0 ps-3">
                                    <li>Tốc độ đăng (Burst): <span class="text-danger fw-bold">${not empty r.burstScore ? r.burstScore : 'Cao'}</span></li>
                                    <li>Độ trùng lặp: <span class="text-warning fw-bold">${not empty r.similarityScore ? r.similarityScore : '> 0.9'}</span></li>
                                    <li>Thiết bị: <span class="text-dark">${not empty r.deviceCount ? r.deviceCount : 'Nhiều IP'}</span></li>
                                </ul>
                            </td>

                            <td class="small">
                                <span class="badge bg-danger mb-1">Spam Confidence: 94%</span><br>
                                <em>Top N-grams: "click here", "cheap room"</em>
                            </td>

                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/ModeratorServlet" method="POST" class="d-inline mb-1">
                                    <input type="hidden" name="reviewId" value="${r.reviewId}">
                                    <input type="hidden" name="action" value="APPROVE">
                                    <button type="submit" class="btn btn-sm btn-outline-success w-100 mb-1">✅ An toàn (Bỏ qua)</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/ModeratorServlet" method="POST" class="d-inline">
                                    <input type="hidden" name="reviewId" value="${r.reviewId}">
                                    <input type="hidden" name="action" value="REJECT">
                                    <button type="submit" class="btn btn-sm btn-danger w-100">🗑️ Xóa & Phạt User</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <c:if test="${empty requestScope.FLAGGED_REVIEWS}">
                        <tr>
                            <td colspan="5" class="text-center text-muted py-5">
                                <span class="fs-5">Hệ thống AI hiện chưa phát hiện thêm đánh giá vi phạm nào.</span>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>