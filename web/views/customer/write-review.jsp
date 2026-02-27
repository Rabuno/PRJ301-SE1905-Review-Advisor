<%@include file="../../common/header.jsp" %>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow p-4">
                <h3 class="text-center mb-4">Chia s? tr?i nghi?m c?a b?n</h3>
                <form action="CreateController" method="POST">
                    <input type="hidden" name="productId" value="${param.id}">
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold">Ch?m ?i?m (1-5 sao)</label>
                        <select name="rating" class="form-select" required>
                            <option value="5">5 sao - Tuy?t v?i</option>
                            <option value="4">4 sao - T?t</option>
                            <option value="3">3 sao - Bình th??ng</option>
                            <option value="2">2 sao - Kém</option>
                            <option value="1">1 sao - R?t t?</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">N?i dung ?ánh giá</label>
                        <textarea id="reviewContent" name="content" class="form-control" rows="5" 
                                  placeholder="Hãy chia s? th?t tâm ?? AI c?a chúng tôi ghi nh?n chính xác nh?t..." required></textarea>
                    </div>

                    <div class="p-3 bg-light rounded border mb-3">
                        <p class="text-muted small fw-bold mb-1">XEM TR??C (PREVIEW):</p>
                        <div id="previewArea" class="small italic text-secondary">Vui lòng nh?p n?i dung ?? xem tr??c...</div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2">G?i ?ánh giá</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    const contentInput = document.getElementById('reviewContent');
    const previewArea = document.getElementById('previewArea');

    contentInput.addEventListener('input', function() {
        if (this.value.trim().length > 0) {
            previewArea.innerHTML = `<span class="text-dark">"${this.value}"</span>`;
        } else {
            previewArea.innerHTML = "Vui lòng nh?p n?i dung ?? xem tr??c...";
        }
    });
</script>
</body>
</html>