<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Error - Review Advisor</title>
        <%@include file="../../common/resources.jsp" %>
    </head>
    <body class="d-flex flex-column min-vh-100">
        <%@include file="../../common/header.jsp" %>

<div class="container text-center flex-grow-1" style="margin-top: 100px;">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h1 class="display-1 fw-bold text-danger">Oops!</h1>
            <h3 class="mb-4">Something went wrong</h3>
            
            <div class="alert alert-light border shadow-sm p-4">
                <i class="bi bi-exclamation-triangle-fill text-warning fs-1 mb-3 d-block"></i>
                <p class="mb-0 text-muted">
                    <c:choose>
                        <%-- Sửa lại điều kiện test ở đây cho đồng bộ với tên biến ERROR --%>
                        <c:when test="${not empty requestScope.ERROR}">
                            <strong class="text-danger">${requestScope.ERROR}</strong>
                        </c:when>
                        <c:otherwise>
                            Your request could not be processed at this time. Please try again later.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            
            <a href="${pageContext.request.contextPath}/MainController" class="btn btn-primary px-4 mt-3 fw-bold shadow-sm">
                &larr; Return to Home
            </a>
        </div>
    </div>
</div>

<%@include file="../../common/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
