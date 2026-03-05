<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Gán Vai Trò Người Dùng - Admin Dashboard</title>
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

                .user-table th {
                    background-color: #f1f3f5;
                    font-weight: 600;
                    text-align: center;
                }

                .user-table td {
                    vertical-align: middle;
                    text-align: center;
                }

                .user-table .user-col {
                    text-align: left;
                    font-weight: bold;
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
                                    <a class="nav-link text-dark"
                                        href="${pageContext.request.contextPath}/Admin/RolePermissions">
                                        <i class="fas fa-user-shield me-2"></i> Quản lý Phân Quyền
                                    </a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link active fw-bold text-primary bg-light rounded"
                                        href="${pageContext.request.contextPath}/Admin/UserRoles">
                                        <i class="fas fa-users-cog me-2"></i> Gán Vai Trò Users
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </nav>

                    <!-- Main Menu -->
                    <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
                        <div
                            class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-4 border-bottom">
                            <h1 class="h2">Quản lý Vai Trò Người Dùng</h1>
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
                                    <table class="table table-bordered table-hover user-table mb-0">
                                        <thead>
                                            <tr>
                                                <th style="width: 150px;">Mã người dùng (ID)</th>
                                                <th class="user-col">Tên đăng nhập (Username)</th>
                                                <th style="width: 250px;">Vai Trò (Role)</th>
                                                <th style="width: 120px;">Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="targetUser" items="${users}">
                                                <tr>
                                                    <form action="${pageContext.request.contextPath}/Admin/UserRoles"
                                                        method="POST">
                                                        <input type="hidden" name="userId"
                                                            value="${targetUser.userId}" />

                                                        <td><span class="badge bg-secondary">${targetUser.userId}</span>
                                                        </td>
                                                        <td class="user-col">${targetUser.username}</td>

                                                        <td>
                                                            <select class="form-select form-select-sm" name="roleId">
                                                                <c:forEach var="role" items="${roles}">
                                                                    <option value="${role.roleId}"
                                                                        ${targetUser.roleId==role.roleId ? 'selected'
                                                                        : '' }>
                                                                        ${role.roleName}
                                                                    </option>
                                                                </c:forEach>
                                                            </select>
                                                        </td>

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