<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Moderation Dashboard - Review Advisor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="bg-light">

    <jsp:include page="/common/header.jsp" />

    <div class="container mt-5 mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="text-danger fw-bold">Moderation Dashboard (AI Triage)</h2>
            <p class="text-muted mb-0">Review and take action on content flagged by the AI system.</p>
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
                <i class="bi bi-shield-lock"></i> System Flagged Reviews (Pending Review)
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover table-striped align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4">Review ID</th>
                                <th>Flagged Content</th>
                                <th>Rating</th>
                                <th>Submitted Time</th>
                                <th class="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty requestScope.FLAGGED_REVIEWS}">
                                    <c:forEach var="r" items="${requestScope.FLAGGED_REVIEWS}">
                                        <tr>
                                            <td class="ps-4"><strong>${r.reviewId}</strong></td>
                                            <td class="text-wrap text-danger" style="max-width: 400px;">"${r.content}"</td>
                                            <td class="text-warning">${r.rating} ★</td>
                                            <td class="small">${r.createdAt}</td>
                                            <td class="text-center">
                                                <button type="button" class="btn btn-sm btn-info text-white me-1 fw-bold" data-bs-toggle="modal" data-bs-target="#evidenceModal_${r.reviewId}" title="Drill-down Evidence">
                                                    <i class="bi bi-search"></i> Evidence
                                                </button>
                                                
                                                <form action="${pageContext.request.contextPath}/ModeratorServlet" method="POST" class="d-inline">
                                                    <input type="hidden" name="reviewId" value="${r.reviewId}">
                                                    <input type="hidden" name="action" value="APPROVE">
                                                    <button type="submit" class="btn btn-sm btn-success me-1 fw-bold">Approve</button>
                                                </form>
                                                
                                                <form action="${pageContext.request.contextPath}/ModeratorServlet" method="POST" class="d-inline">
                                                    <input type="hidden" name="reviewId" value="${r.reviewId}">
                                                    <input type="hidden" name="action" value="REJECT">
                                                    <button type="submit" class="btn btn-sm btn-danger fw-bold">Reject</button>
                                                </form>
                                            </td>
                                        </tr>

                                        <div class="modal fade" id="evidenceModal_${r.reviewId}" tabindex="-1" aria-labelledby="evidenceModalLabel_${r.reviewId}" aria-hidden="true">
                                            <div class="modal-dialog modal-lg modal-dialog-centered">
                                                <div class="modal-content">
                                                    <div class="modal-header bg-dark text-white">
                                                        <h5 class="modal-title fw-bold" id="evidenceModalLabel_${r.reviewId}">
                                                            <i class="bi bi-shield-exclamation text-warning"></i> XAI Evidence Pack - Alert Details #${r.reviewId}
                                                        </h5>
                                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body p-4 text-start">
                                                        <div class="row">
                                                            <div class="col-md-6 border-end">
                                                                <h6 class="fw-bold text-primary mb-3"><i class="bi bi-database-check"></i> 1. System & Audit Evidence</h6>
                                                                <ul class="list-group list-group-flush small">
                                                                    <li class="list-group-item px-0 d-flex justify-content-between align-items-center">
                                                                        Burst Score (Reviews/30m)
                                                                        <span class="badge bg-danger rounded-pill">${not empty r.evidence.burstScore ? r.evidence.burstScore : 'High'}</span>
                                                                    </li>
                                                                    <li class="list-group-item px-0 d-flex justify-content-between align-items-center">
                                                                        Text Similarity (Duplicate)
                                                                        <span class="badge bg-warning text-dark rounded-pill">${not empty r.evidence.similarity ? r.evidence.similarity : 'N/A'}</span>
                                                                    </li>
                                                                    <li class="list-group-item px-0 d-flex justify-content-between align-items-center">
                                                                        Account Age
                                                                        <span class="badge bg-secondary rounded-pill">${not empty r.evidence.accountAge ? r.evidence.accountAge : 'Unknown'}</span>
                                                                    </li>
                                                                    <li class="list-group-item px-0 d-flex justify-content-between align-items-center">
                                                                        Edit History
                                                                        <span class="badge bg-secondary rounded-pill">${not empty r.evidence.editCount ? r.evidence.editCount : '0'} Edits</span>
                                                                    </li>
                                                                </ul>
                                                            </div>
                                                            
                                                            <div class="col-md-6 ps-4">
                                                                <h6 class="fw-bold text-warning mb-3"><i class="bi bi-robot"></i> 2. AI Model Features (Top-K)</h6>
                                                                <p class="small text-muted mb-3">The Machine Learning model identified these top contributors to the spam classification:</p>
                                                                
                                                                <div class="mb-3">
                                                                    <div class="d-flex justify-content-between small mb-1 fw-bold">
                                                                        <span>${not empty r.aiFeatures[0].name ? r.aiFeatures[0].name : 'Primary AI Factor'}</span>
                                                                        <span class="text-danger">${not empty r.aiFeatures[0].score ? r.aiFeatures[0].score : '80'}%</span>
                                                                    </div>
                                                                    <div class="progress" style="height: 8px;">
                                                                        <div class="progress-bar bg-danger progress-bar-striped progress-bar-animated" role="progressbar" style="width: ${not empty r.aiFeatures[0].score ? r.aiFeatures[0].score : '80'}%;"></div>
                                                                    </div>
                                                                </div>
                                                                
                                                                <div class="mb-3">
                                                                    <div class="d-flex justify-content-between small mb-1 fw-bold">
                                                                        <span>${not empty r.aiFeatures[1].name ? r.aiFeatures[1].name : 'Secondary AI Factor'}</span>
                                                                        <span class="text-warning text-dark">${not empty r.aiFeatures[1].score ? r.aiFeatures[1].score : '60'}%</span>
                                                                    </div>
                                                                    <div class="progress" style="height: 8px;">
                                                                        <div class="progress-bar bg-warning" role="progressbar" style="width: ${not empty r.aiFeatures[1].score ? r.aiFeatures[1].score : '60'}%;"></div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer bg-light d-flex justify-content-between">
                                                        <span class="text-muted small">Overall Risk Score: <strong class="text-danger fs-6">${not empty r.riskScore ? r.riskScore : '0.90'} (Critical)</strong></span>
                                                        <div>
                                                            <button type="button" class="btn btn-secondary fw-bold" data-bs-dismiss="modal">Close</button>
                                                            <form action="${pageContext.request.contextPath}/ModeratorServlet" method="POST" class="d-inline">
                                                                <input type="hidden" name="reviewId" value="${r.reviewId}">
                                                                <input type="hidden" name="action" value="REJECT">
                                                                <button type="submit" class="btn btn-danger fw-bold"><i class="bi bi-slash-circle"></i> Confirm & Ban</button>
                                                            </form>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-5">
                                            <h1 class="display-4 text-light mb-3"><i class="bi bi-check-circle"></i></h1>
                                            <span class="fs-5">No flagged reviews detected by the AI system at the moment.</span>
                                            <p class="small mt-2">All platform content is currently safe and verified.</p>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <%@include file="../../common/footer.jsp" %>
</body>
</html>