<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-success fw-bold mb-1">Merchant Dashboard</h2>
            <p class="text-muted small mb-0">Manage your properties and monitor customer feedback.</p>
        </div>
        <a href="#" class="btn btn-outline-success fw-bold shadow-sm">
            + Add New Property
        </a>
    </div>

    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card shadow-sm border-0 border-start border-4 border-primary h-100">
                <div class="card-body">
                    <div class="text-muted small fw-bold text-uppercase mb-1">Total Properties</div>
                    <div class="h3 mb-0 text-dark fw-bold">2</div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm border-0 border-start border-4 border-warning h-100">
                <div class="card-body">
                    <div class="text-muted small fw-bold text-uppercase mb-1">Average Rating</div>
                    <div class="h3 mb-0 text-dark fw-bold">4.8 <span class="text-warning small">★</span></div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm border-0 border-start border-4 border-success h-100">
                <div class="card-body">
                    <div class="text-muted small fw-bold text-uppercase mb-1">Published Reviews</div>
                    <div class="h3 mb-0 text-dark fw-bold">124</div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card shadow-sm border-0 border-start border-4 border-danger h-100">
                <div class="card-body">
                    <div class="text-muted small fw-bold text-uppercase mb-1">Flagged by AI</div>
                    <div class="h3 mb-0 text-danger fw-bold">3</div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-8 mb-4">
            <div class="card shadow-sm border-0 h-100">
                <div class="card-header bg-white fw-bold text-dark border-bottom-0 pt-3">
                    Rating Trends (Last 6 Months)
                </div>
                <div class="card-body">
                    <canvas id="ratingChart" height="100"></canvas>
                </div>
            </div>
        </div>
        
        <div class="col-md-4 mb-4">
            <div class="card shadow-sm border-0 h-100">
                <div class="card-header bg-white fw-bold text-dark border-bottom-0 pt-3">
                    Recent Feedback
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item p-3">
                            <div class="d-flex justify-content-between align-items-center">
                                <strong class="small text-dark">Majestic Saigon</strong>
                                <span class="badge bg-success small">5 ★</span>
                            </div>
                            <div class="small text-muted mt-1">"Perfect stay for my family! Highly recommended."</div>
                        </li>
                        <li class="list-group-item p-3 bg-light">
                            <div class="d-flex justify-content-between align-items-center">
                                <strong class="small text-dark">Majestic Saigon</strong>
                                <span class="badge bg-danger small">Flagged</span>
                            </div>
                            <div class="small text-muted mt-1 fst-italic">"Click here for cheap rooms http://spaml..."</div>
                        </li>
                        <li class="list-group-item p-3">
                            <div class="d-flex justify-content-between align-items-center">
                                <strong class="small text-dark">Ocean View Villa</strong>
                                <span class="badge bg-warning text-dark small">4 ★</span>
                            </div>
                            <div class="small text-muted mt-1">"Great view but the wifi was a bit slow."</div>
                        </li>
                    </ul>
                </div>
                <div class="card-footer bg-white text-center border-top-0 pb-3">
                    <a href="#" class="text-success small fw-bold text-decoration-none">View All Reviews &rarr;</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Logic vẽ biểu đồ đường (Line Chart) cho Rating
    const ctx = document.getElementById('ratingChart').getContext('2d');
    const ratingChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Oct', 'Nov', 'Dec', 'Jan', 'Feb', 'Mar'], // Các tháng
            datasets: [{
                label: 'Average Rating',
                data: [4.2, 4.5, 4.4, 4.7, 4.8, 4.9], // Dữ liệu điểm số demo
                borderColor: '#198754', // Màu xanh success của Bootstrap
                backgroundColor: 'rgba(25, 135, 84, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.4 // Làm cong đường nối
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: false,
                    min: 1,
                    max: 5 // Rating cao nhất là 5 sao
                }
            },
            plugins: {
                legend: {
                    display: false // Ẩn chú thích để giao diện gọn hơn
                }
            }
        }
    });
</script>

<%@include file="../../common/footer.jsp" %>