<%@include file="../../common/header.jsp" %>
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Khám phá các ?i?m ??n hot nh?t</h2>
        <form action="SearchController" method="GET" class="input-group w-25">
            <input type="text" name="txtSearch" class="form-control" placeholder="Tìm khách s?n...">
            <button type="submit" class="btn btn-primary">Tìm</button>
        </form>
    </div>

    <div class="row">
        <div class="col-md-4 mb-4">
            <div class="card h-100 shadow-sm card-product">
                <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945" class="card-img-top" alt="Hotel">
                <div class="card-body">
                    <h5 class="card-title">Khách s?n Majestic Saigon</h5>
                    <p class="text-muted small">Qu?n 1, TP. H? Chí Minh</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="text-warning">????? (120 reviews)</span>
                        <a href="MainController?action=ViewDetail&id=H001" class="btn btn-sm btn-outline-success">Chi ti?t</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>