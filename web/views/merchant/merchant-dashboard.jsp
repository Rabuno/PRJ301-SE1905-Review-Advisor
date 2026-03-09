<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@include file="../../common/header.jsp" %>

            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

            <div class="container mt-4 mb-5">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2 class="text-success fw-bold mb-1">Merchant Dashboard</h2>
                        <p class="text-muted small mb-0">Manage your properties and monitor customer feedback via Data
                            Visualizations.</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/MerchantServlet?action=ManageProperties"
                        class="btn btn-outline-success fw-bold shadow-sm">+ Manage Properties</a>
                </div>

                <div class="row mb-4">
                    <div class="col-md-3">
                        <div class="card shadow-sm border-0 border-start border-4 border-primary h-100">
                            <div class="card-body">
                                <div class="text-muted small fw-bold text-uppercase mb-1">Total Properties</div>
                                <div class="h3 mb-0 text-dark fw-bold">${not empty requestScope.STATS.totalProperties ?
                                    requestScope.STATS.totalProperties : '0'}</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card shadow-sm border-0 border-start border-4 border-warning h-100">
                            <div class="card-body">
                                <div class="text-muted small fw-bold text-uppercase mb-1">Average Rating</div>
                                <div class="h3 mb-0 text-dark fw-bold">${not empty requestScope.STATS.avgRating ?
                                    requestScope.STATS.avgRating : '0.0'} <span class="text-warning small">★</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card shadow-sm border-0 border-start border-4 border-success h-100">
                            <div class="card-body">
                                <div class="text-muted small fw-bold text-uppercase mb-1">Published Reviews</div>
                                <div class="h3 mb-0 text-dark fw-bold">${not empty requestScope.STATS.publishedCount ?
                                    requestScope.STATS.publishedCount : '0'}</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card shadow-sm border-0 border-start border-4 border-danger h-100">
                            <div class="card-body">
                                <div class="text-muted small fw-bold text-uppercase mb-1">Flagged by AI</div>
                                <div class="h3 mb-0 text-danger fw-bold">${not empty requestScope.STATS.flaggedCount ?
                                    requestScope.STATS.flaggedCount : '0'}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-7 mb-4">
                        <div class="card shadow-sm border-0 h-100 p-3">
                            <h5 class="fw-bold text-dark mb-3">Review Trends (Last 7 Days)</h5>
                            <canvas id="reviewTrendChart"></canvas>
                        </div>
                    </div>

                    <div class="col-md-5 mb-4">
                        <div class="card shadow-sm border-0 h-100">
                            <div class="card-header bg-white fw-bold text-dark border-bottom-0 pt-3">
                                Recent Customer Feedback
                            </div>
                            <div class="card-body p-0">
                                <ul class="list-group list-group-flush">
                                    <c:choose>
                                        <c:when test="${not empty requestScope.RECENT_FEEDBACK}">
                                            <c:forEach var="feedback" items="${requestScope.RECENT_FEEDBACK}">
                                                <li
                                                    class="list-group-item p-3 ${feedback.status == 'FLAGGED' ? 'bg-light border-start border-3 border-danger' : ''}">
                                                    <div class="d-flex justify-content-between align-items-center">
                                                        <strong class="small text-dark">Product ID:
                                                            ${feedback.productId}</strong>
                                                        <c:choose>
                                                            <c:when test="${feedback.status == 'FLAGGED'}">
                                                                <span class="badge bg-danger small">Flagged /
                                                                    Pending</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-success small">${feedback.rating}
                                                                    ★</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div
                                                        class="small text-muted mt-1 ${feedback.status == 'FLAGGED' ? 'fst-italic' : ''}">
                                                        "${feedback.content}"</div>
                                                </li>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="list-group-item p-4 text-center text-muted small">No recent
                                                feedback available.</li>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const ctx = document.getElementById('reviewTrendChart').getContext('2d');

                    const trendChart = new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: ${ not empty requestScope.CHART_LABELS ? requestScope.CHART_LABELS : "['Day 1', 'Day 2', 'Day 3', 'Day 4', 'Day 5', 'Day 6', 'Today']" },
                        datasets: [
                        {
                            label: 'Organic Reviews',
                            data: ${ not empty requestScope.CHART_ORGANIC_DATA ? requestScope.CHART_ORGANIC_DATA : "[12, 19, 15, 25, 22, 30, 28]" },
                        borderColor: '#198754',
                        backgroundColor: 'rgba(25, 135, 84, 0.1)',
                        borderWidth: 2,
                        tension: 0.4,
                        fill: true
                    },
                    {
                        label: 'Flagged (Spam/AI)',
                        data: ${ not empty requestScope.CHART_FLAGGED_DATA ? requestScope.CHART_FLAGGED_DATA : "[1, 3, 0, 2, 5, 1, 0]" },
                    borderColor: '#dc3545',
                    backgroundColor: 'rgba(220, 53, 69, 0.1)',
                    borderWidth: 2,
                    tension: 0.4,
                    fill: true
                    }
                ]
            },
                    options: {
                    responsive: true,
                    plugins: { legend: { position: 'top' } },
                    scales: { y: { beginAtZero: true } }
                }
        });
    });
            </script>

            <%@include file="../../common/footer.jsp" %>