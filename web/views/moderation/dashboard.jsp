<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h2>Moderator Dashboard</h2>
<c:forEach var="review" items="${reviews}">
    <div style="border:1px solid gray; margin:10px; padding:10px;">
        <p><strong>ID:</strong> ${review.reviewId}</p>
        <p><strong>Content:</strong> ${review.content}</p>
        <p><strong>Rating:</strong> ${review.rating}</p>

        <form method="post" action="${pageContext.request.contextPath}/moderator">
            <input type="hidden" name="reviewId" value="${review.reviewId}" />
            <button name="action" value="publish">Publish</button>
            <button name="action" value="hide">Hide</button>
        </form>
    </div>
</c:forEach>