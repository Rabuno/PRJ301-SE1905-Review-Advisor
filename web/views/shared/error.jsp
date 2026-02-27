<%@include file="../../common/header.jsp" %>
<div class="container text-center" style="margin-top: 100px;">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h1 class="display-1 fw-bold text-danger">Oops!</h1>
            <h3 class="mb-4">?ã có l?i x?y ra</h3>
            <div class="alert alert-light border shadow-sm">
                <p class="mb-0 text-muted">${requestScope.ERROR_MESSAGE != null ? requestScope.ERROR_MESSAGE : "Yêu c?u c?a b?n không th? th?c hi?n ???c vào lúc này."}</p>
            </div>
            <a href="MainController" class="btn btn-primary px-4 mt-3">Quay v? trang ch?</a>
        </div>
    </div>
</div>
<%@include file="../../common/footer.jsp" %>