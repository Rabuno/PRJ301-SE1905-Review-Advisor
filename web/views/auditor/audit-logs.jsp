<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="../../common/header.jsp" %>

<div class="container mt-4 mb-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-primary fw-bold mb-1">System Audit Logs</h2>
            <p class="text-muted small mb-0">HashChain Integrity Tracking System</p>
        </div>
        
        <form action="AuditServlet" method="POST">
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
                                <td>1</td>
                                <td>2026-03-02 10:00:00</td>
                                <td>U005</td>
                                <td><span class="badge bg-warning text-dark">UPDATE_REVIEW</span></td>
                                <td class="small text-start">Changed rating from 1 to 5</td>
                                <td class="font-monospace small text-muted">0000000000000000</td>
                                <td class="font-monospace small text-primary">a1b2c3d4e5f6g7h8</td>
                            </tr>
                            <tr class="table-danger">
                                <td>2</td>
                                <td>2026-03-02 14:30:00</td>
                                <td>U009</td>
                                <td><span class="badge bg-danger">DELETE_REVIEW</span></td>
                                <td class="small text-start">Deleted negative review</td>
                                <td class="font-monospace small text-danger fw-bold">INVALID_HASH_LINK</td>
                                <td class="font-monospace small text-primary">x9y8z7w6v5u4t3s2</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="log" items="${requestScope.AUDIT_LIST}">
                                <tr class="${log.logId == requestScope.BROKEN_LOG_ID ? 'table-danger' : ''}">
                                    <td>${log.logId}</td>
                                    <td>${log.timestamp}</td>
                                    <td>${log.userId}</td>
                                    <td>
                                        <span class="badge bg-secondary">${log.action}</span>
                                    </td>
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