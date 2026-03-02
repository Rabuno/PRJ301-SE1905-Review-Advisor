<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
                                    <th scope="col" class="ps-4">Hotel/Service</th>
                                    <th scope="col">Rating</th>
                                    <th scope="col" style="width: 40%;">Your Review</th>
                                    <th scope="col">Date Submitted</th>
                                    <th scope="col" class="text-center">Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td class="ps-4 fw-bold">Majestic Saigon</td>
                                    <td class="text-warning">★★★★★</td>
                                    <td class="text-muted small">"Excellent service, very clean!"</td>
                                    <td class="small">Oct 25, 2026</td>
                                    <td class="text-center">
                                        <span class="badge bg-success">PUBLISHED</span>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="ps-4 fw-bold">Rex Hotel</td>
                                    <td class="text-warning">★★★★☆</td>
                                    <td class="text-muted small">"Good location, but breakfast could be better."</td>
                                    <td class="small">Oct 26, 2026</td>
                                    <td class="text-center">
                                        <span class="badge bg-warning text-dark">PENDING</span>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="ps-4 fw-bold">Ocean View Villa</td>
                                    <td class="text-warning">★★★★★</td>
                                    <td class="text-muted small">"BEST HOTEL EVER CLICK HERE TO GET DISCOUNT http://spam-link.com"</td>
                                    <td class="small">Oct 27, 2026</td>
                                    <td class="text-center">
                                        <span class="badge bg-danger">FLAGGED</span>
                                        <div class="text-danger small mt-1" style="font-size: 0.7rem;">AI Detected: Spam/Promo</div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/MainController" class="btn btn-outline-secondary px-4">&larr; Back to Explore</a>
            </div>
        </div>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>