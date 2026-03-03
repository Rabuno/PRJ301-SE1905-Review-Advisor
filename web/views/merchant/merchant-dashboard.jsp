<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-success fw-bold mb-1">Merchant Dashboard</h2>
            <p class="text-muted small mb-0">Manage your properties and monitor customer feedback.</p>
        </div>
        <a href="#" class="btn btn-outline-success fw-bold shadow-sm">+ Add New Property</a>
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
        <div class="col-md-12 mb-4">
            <div class="card shadow-sm border-0 h-100">
                <div class="card-header bg-white fw-bold text-dark border-bottom-0 pt-3">
                    Recent Customer Feedback
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item p-3">
                            <div class="d-flex justify-content-between align-items-center">
                                <strong class="small text-dark">Majestic Saigon Hotel</strong>
                                <span class="badge bg-success small">5 ★</span>
                            </div>
                            <div class="small text-muted mt-1">"Perfect stay for my family! Highly recommended."</div>
                        </li>
                        <li class="list-group-item p-3 bg-light border-start border-3 border-danger">
                            <div class="d-flex justify-content-between align-items-center">
                                <strong class="small text-dark">Majestic Saigon Hotel</strong>
                                <span class="badge bg-danger small">Flagged / Pending</span>
                            </div>
                            <div class="small text-muted mt-1 fst-italic">"Click here for cheap rooms http://spaml..."</div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>