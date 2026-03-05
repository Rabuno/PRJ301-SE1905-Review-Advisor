<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Quản lý Phân Quyền Vai Trò - Admin Dashboard</title>
            <!-- MDB CSS -->
            <link href="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/6.4.0/mdb.min.css" rel="stylesheet" />
            <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" />
            <style>
                body {
                    background-color: #f8f9fa;
                }

                .sidebar {
                    background: #fff;
                    box-shadow: 2px 0 15px rgba(0, 0, 0, 0.05);
                    min-height: calc(100vh - 56px);
                }

                .permission-table th {
                    background-color: #f1f3f5;
                    font-weight: 600;
                    text-align: center;
                }

                .permission-table td {
                    vertical-align: middle;
                    text-align: center;
                }

                .permission-table .role-col {
                    text-align: left;
                    font-weight: bold;
                    background-color: #f8f9fa;
                }

                .form-check-input {
                    width: 1.25em;
                    height: 1.25em;
                    cursor: pointer;
                }
            </style>
        </head>

        <body>

            <!-- Navbar -->
            <jsp:include page="/common/header.jsp" />

            <div class="container-fluid">
                <div class="row">
                    <!-- Sidebar -->
                    <nav class="col-md-3 col-lg-2 d-md-block sidebar py-4">
                        <div class="position-sticky">
                            <ul class="nav flex-column">
                                <li class="nav-item">
                                    <a class="nav-link text-dark"
                                        href="${pageContext.request.contextPath}/MainController">
                                        <i class="fas fa-home me-2"></i> Trang chủ
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link active fw-bold text-primary bg-light rounded"
                                        href="${pageContext.request.contextPath}/Admin/RolePermissions">
                                        <i class="fas fa-user-shield me-2"></i> Quản lý Phân Quyền
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </nav>

                    <!-- Main Menu -->
                    <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
                        <div
                            class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-4 border-bottom">
                            <h1 class="h2">Quản lý Phân Quyền Các Vai Trò</h1>
                        </div>

                        <c:if test="${not empty sessionScope.SUCCESS}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                ${sessionScope.SUCCESS}
                                <button type="button" class="btn-close" data-mdb-dismiss="alert"
                                    aria-label="Close"></button>
                            </div>
                            <c:remove var="SUCCESS" scope="session" />
                        </c:if>
                        <c:if test="${not empty sessionScope.ERROR}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${sessionScope.ERROR}
                                <button type="button" class="btn-close" data-mdb-dismiss="alert"
                                    aria-label="Close"></button>
                            </div>
                            <c:remove var="ERROR" scope="session" />
                        </c:if>

                        <div class="card shadow-sm border-0">
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover permission-table mb-0">
                                        <thead>
                                            <tr>
                                                <th class="role-col" style="width: 200px;">Vai Trò (Role)</th>
                                                <c:forEach var="perm" items="${permissions}">
                                                    <th>${perm.permissionCode}</th>
                                                </c:forEach>
                                                <th style="width: 120px;">Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="role" items="${roles}">
                                                <c:set var="rolePermList" value="${rolePermissionsMap[role.roleId]}" />
                                                <tr>
                                                    <form
                                                        action="${pageContext.request.contextPath}/Admin/RolePermissions"
                                                        method="POST">
                                                        <input type="hidden" name="roleId" value="${role.roleId}" />
                                                        <td class="role-col">
                                                            <span class="badge bg-secondary">${role.roleId}</span><br />
                                                            ${role.roleName}
                                                        </td>

                                                        <c:forEach var="perm" items="${permissions}">
                                                            <c:set var="hasPerm" value="false" />
                                                            <c:forEach var="rpm" items="${rolePermList}">
                                                                <c:if test="${rpm == perm.permissionId}">
                                                                    <c:set var="hasPerm" value="true" />
                                                                </c:if>
                                                            </c:forEach>

                                                            <td>
                                                                <div class="form-check d-flex justify-content-center">
                                                                    <input class="form-check-input" type="checkbox"
                                                                        name="permissions_${role.roleId}"
                                                                        value="${perm.permissionId}"
                                                                        id="chk_${role.roleId}_${perm.permissionId}"
                                                                        ${hasPerm ? 'checked' : '' } />
                                                                </div>
                                                            </td>
                                                        </c:forEach>

                                                        <td>
                                                            <button type="submit" class="btn btn-primary btn-sm">
                                                                <i class="fas fa-save me-1"></i> Lưu
                                                            </button>
                                                        </td>
                                                    </form>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </main>
                </div>
            </div>

            <!-- MDB JS -->
            <script type="text/javascript"
                src="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/6.4.0/mdb.min.js"></script>
        </body>

        </html>