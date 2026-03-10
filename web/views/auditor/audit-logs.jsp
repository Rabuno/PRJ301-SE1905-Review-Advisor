<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-primary fw-bold mb-1">System Audit Logs</h2>
            <p class="text-muted small mb-0">HashChain Integrity Tracking System for Security and Transparency</p>
        </div>
        
        <form action="${pageContext.request.contextPath}/AuditServlet" method="POST">
            <input type="hidden" name="action" value="VerifyIntegrity">
            <button type="submit" class="btn btn-danger shadow-sm fw-bold">
                <i class="bi bi-shield-check"></i> Verify Data Integrity
            </button>
        </form>
    </div>

    <c:if test="${not empty requestScope.INTEGRITY_WARNING}">
        <div class="alert alert-danger fw-bold shadow-sm">
            🚨 SYSTEM ALERT: Data tampering detected! The HashChain has been broken at Log ID: ${requestScope.BROKEN_LOG_ID}.
        </div>
    </c:if>
    <c:if test="${requestScope.INTEGRITY_STATUS == 'VALID'}">
        <div class="alert alert-success fw-bold shadow-sm">
            ✅ VERIFIED: The HashChain is fully intact. No data tampering detected.
        </div>
    </c:if>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0 table-responsive">
            <table class="table table-hover table-striped mb-0 text-center align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>Log ID</th>
                        <th>Timestamp</th>
                        <th>User ID</th>
                        <th>Action</th>
                        <th>Details</th>
                        <th>Previous Hash</th>
                        <th>Current Hash</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty requestScope.AUDIT_LIST}">
                            <tr>
                                <td colspan="7" class="text-center text-muted py-4">
                                    <i class="bi bi-inbox fs-3 d-block mb-2"></i>
                                    No audit logs available in the system yet.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="log" items="${requestScope.AUDIT_LIST}">
                                <tr class="${log.logId == requestScope.BROKEN_LOG_ID ? 'table-danger' : ''}">
                                    <td>${log.logId}</td>
                                    <td>${log.timestamp}</td>
                                    <td>${log.userId}</td>
                                    <td><span class="badge bg-secondary">${log.action}</span></td>
                                    <td class="small text-start">${log.details}</td>
                                    <td class="font-monospace small text-muted" title="${log.previousHash}">
                                        ${log.previousHash.substring(0, 15)}...
                                    </td>
                                    <td class="font-monospace small text-primary" title="${log.currentHash}">
                                        ${log.currentHash.substring(0, 15)}...
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
    
    <div class="mt-3 small text-muted">
        * <strong>Note:</strong> Hashes are truncated for display purposes. Hover over the hash to see the full SHA-256 string.
    </div>
</div>

<%@include file="../../common/footer.jsp" %>