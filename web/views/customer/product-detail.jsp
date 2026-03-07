<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Product Details - Review Advisor</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
        </head>

        <body class="bg-light d-flex flex-column min-vh-100">

            <%@include file="../../common/header.jsp" %>

                <div class="container mt-4 mb-5 flex-grow-1">
                    <div class="row">
                        <div class="col-md-5 mb-4">
                            <div class="card shadow-sm border-0 rounded-3 sticky-top" style="top: 20px;">

                                <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&h=600&fit=crop"
                                    class="card-img-top"
                                    alt="${not empty requestScope.PRODUCT.name ? requestScope.PRODUCT.name : 'Product Image'}">

                                <div class="card-body p-4">
                                    <h2 class="fw-bold text-primary mb-2">${not empty requestScope.PRODUCT ?
                                        requestScope.PRODUCT.name : 'Unknown Product'}</h2>
                                    <div class="mb-3">
                                        <span class="badge bg-info text-dark"><i class="bi bi-tag-fill"></i> Category:
                                            ${requestScope.PRODUCT.description}</span>
                                        <span class="badge bg-success"><i class="bi bi-check-circle-fill"></i>
                                            Verified</span>
                                    </div>

                                    <p class="text-muted lh-lg">
                                        ${not empty requestScope.PRODUCT.description ? requestScope.PRODUCT.description
                                        : 'Chưa có thông tin mô tả chi tiết cho địa điểm này.'}
                                    </p>

                                    <hr>
                                    <div class="d-grid mt-3">
                                        <a href="${pageContext.request.contextPath}/views/customer/write-review.jsp?id=${requestScope.PRODUCT.productId}"
                                            class="btn btn-success btn-lg fw-bold shadow-sm">
                                            <i class="bi bi-pencil-square me-2"></i> Write a Review
                                        </a>
                                    </div>
                                    <p class="text-center text-muted small mt-2">Share your experience to help others!
                                    </p>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-7">
                            <div class="card shadow-sm border-0 bg-white rounded-3 p-4">
                                <div class="d-flex