<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.Feedbacks" %>
<%
    Feedbacks f = (Feedbacks) request.getAttribute("feedback");
    if (f == null) f = new Feedbacks();
%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8" />
    <title>Chỉnh sửa phản hồi</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- App favicon -->
    <link rel="shortcut icon" href="Admin/assets/images/favicon.ico">

    <!-- Theme Config Js -->
    <script src="Admin/assets/js/hyper-config.js"></script>

    <!-- App css -->
    <link href="Admin/assets/css/app-saas.min.css" rel="stylesheet" type="text/css" id="app-style" />

    <!-- Icons css -->
    <link href="Admin/assets/css/icons.min.css" rel="stylesheet" type="text/css" />
</head>

<body>
    <!-- Begin page -->
    <div class="wrapper">

        <!-- ========== Topbar Start ========== -->
        <div class="navbar-custom">
            <div class="topbar container-fluid">
                <div class="d-flex align-items-center gap-lg-2 gap-1">

                    <!-- Sidebar Menu Toggle Button -->
                    <button class="button-toggle-menu">
                        <i class="mdi mdi-menu"></i>
                    </button>

                    <!-- Horizontal Menu Toggle Button -->
                    <button class="navbar-toggle" data-bs-toggle="collapse" data-bs-target="#topnav-menu-content">
                        <div class="lines">
                            <span></span>
                            <span></span>
                            <span></span>
                        </div>
                    </button>

                    <!-- Topbar Search Form -->
                    <div class="app-search dropdown d-none d-lg-block">
                        <form>
                            <div class="input-group">
                                <input type="search" class="form-control dropdown-toggle" placeholder="Search..." id="top-search">
                                <span class="mdi mdi-magnify search-icon"></span>
                                <button class="input-group-text btn btn-primary" type="submit">Search</button>
                            </div>
                        </form>
                    </div>
                </div>

                <ul class="topbar-menu d-flex align-items-center gap-3">
                    <li class="dropdown d-lg-none">
                        <a class="nav-link dropdown-toggle arrow-none" data-bs-toggle="dropdown" href="#" role="button" aria-haspopup="false" aria-expanded="false">
                            <i class="ri-search-line font-22"></i>
                        </a>
                        <div class="dropdown-menu dropdown-menu-animated dropdown-lg p-0">
                            <form class="p-3">
                                <input type="search" class="form-control" placeholder="Search ..." aria-label="Recipient's username">
                            </form>
                        </div>
                    </li>
                    
                    <li class="dropdown notification-list">
                        <a class="nav-link dropdown-toggle arrow-none" data-bs-toggle="dropdown" href="#" role="button" aria-haspopup="false" aria-expanded="false">
                            <i class="ri-notification-3-line font-22"></i>
                            <span class="noti-icon-badge"></span>
                        </a>
                        <div class="dropdown-menu dropdown-menu-end dropdown-menu-animated dropdown-lg py-0">
                            <div class="p-2 border-top-0 border-start-0 border-end-0 border-dashed border">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <h6 class="m-0 font-16 fw-semibold"> Notification</h6>
                                    </div>
                                    <div class="col-auto">
                                        <a href="javascript: void(0);" class="text-dark text-decoration-underline">
                                            <small>Clear All</small>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </li>

                    <li class="d-none d-sm-inline-block">
                        <a class="nav-link" data-bs-toggle="offcanvas" href="#theme-settings-offcanvas">
                            <i class="ri-settings-3-line font-22"></i>
                        </a>
                    </li>

                    <li class="d-none d-sm-inline-block">
                        <div class="nav-link" id="light-dark-mode" data-bs-toggle="tooltip" data-bs-placement="left" title="Dark - Light">
                            <i class="ri-moon-line font-22"></i>
                        </div>
                    </li>

                    <li class="d-none d-md-inline-block">
                        <a class="nav-link" href="#" data-toggle="fullscreen">
                            <i class="ri-fullscreen-line font-22"></i>
                        </a>
                    </li>

                    <li class="dropdown">
                                                 <a class="nav-link dropdown-toggle arrow-none nav-user px-2" data-bs-toggle="dropdown" href="#" role="button" aria-haspopup="false" aria-expanded="false">
                             <span class="account-user-avatar">
                                 <img src="Admin/assets/images/users/anhcuakleqingcamcopy.png" alt="user-image" width="32" class="rounded-circle">
                             </span>
                            <span class="d-lg-flex flex-column gap-1 d-none">
                                <h5 class="my-0">Staff</h5>
                                <h6 class="my-0 fw-normal">Staff</h6>
                            </span>
                        </a>
                        <div class="dropdown-menu dropdown-menu-end dropdown-menu-animated profile-dropdown">
                            <!-- item-->
                            <a class="dropdown-item" href="javascript:void(0);" onclick="logout()">
                                <button type="button" id="loginBtn" class="btn btn-sm m-1 bg-primary text-white rounded-5 px-sm-3 px-2">
                                    <i class="mdi mdi-logout me-1"></i>
                                    <span>Logout</span>
                                </button>
                            </a>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <!-- ========== Topbar End ========== -->

        <!-- ========== Left Sidebar Start ========== -->
        <div class="leftside-menu">

            <!-- Sidebar Hover Menu Toggle Button -->
            <div class="button-sm-hover" data-bs-toggle="tooltip" data-bs-placement="right" title="Show Full Sidebar">
                <i class="ri-checkbox-blank-circle-line align-middle"></i>
            </div>

            <!-- Full Sidebar Menu Close Button -->
            <div class="button-close-fullsidebar">
                <i class="ri-close-fill align-middle"></i>
            </div>

            <!-- Sidebar -left -->
            <div class="h-100" id="leftside-menu-container" data-simplebar>
                                 <!-- Leftbar User -->
                 <div class="leftbar-user">
                     <a href="pages-profile.jsp">
                         <img src="Admin/assets/images/users/anhcuakleqingcamcopy.png" alt="user-image" height="42" class="rounded-circle shadow-sm">
                         <span class="leftbar-user-name mt-2">Staff</span>
                     </a>
                 </div>

                <!--- Sidemenu -->
                <ul class="side-nav">

                    <li class="side-nav-title">Ứng dụng</li>

                    <li class="side-nav-item">
                        <a href="${pageContext.request.contextPath}/Admin/calendar.jsp" class="side-nav-link">
                            <i class="uil-calender"></i>
                            <span class="badge bg-danger text-white float-end">Mới!</span>
                            <span> Lịch trình </span>
                        </a>
                    </li>

                    <li class="side-nav-item">
                        <a data-bs-toggle="collapse" href="#sidebarEcommerce" aria-expanded="false" aria-controls="sidebarEcommerce" class="side-nav-link">
                            <i class="uil-store"></i>
                            <span> Quản lý </span>
                            <span class="menu-arrow"></span>
                        </a>
                        <div class="collapse" id="sidebarEcommerce">
                            <ul class="side-nav-second-level">
                                <li>
                                    <a href="feedback">Phản hồi</a>
                                </li>
                            </ul>
                        </div>
                    </li>

                                         <!-- Help Box -->
                     <div class="help-box text-white text-center">
                         <a href="javascript: void(0);" class="float-end close-btn btn">
                         </a>
                         <img src="Admin/assets/images/svg/help-icon.svg" height="90" alt="Helper Icon Image" />
                         <h5 class="mt-3">Web admin</h5>
                         <p class="mb-3">Tối ưu thời gian quản lý với UI mới.</p>
                         <a href="javascript: void(0);"></a>
                         <div class="text-center ">
                             <a class="btn btn-outline-primary" href="#" type="button">Quyền riềng tư & Bảo mật</a>
                             <a class="btn bg-gradient-primary w-100" href="#" type="button">Trách nhiệm & Pháp lý</a>
                         </div>
                     </div>
                    <!-- end Help Box -->

                </ul>
                <!--- End Sidemenu -->

                <div class="clearfix"></div>
            </div>
        </div>
        <!-- ========== Left Sidebar End ========== -->

        <!-- ============================================================== -->
        <!-- Start Page Content here -->
        <!-- ============================================================== -->

        <div class="content-page">
            <div class="content">

                <!-- Start Content-->
                <div class="container-fluid">

                    <!-- start page title -->
                    <div class="row">
                        <div class="col-12">
                            <div class="page-title-box">
                                <div class="page-title-right">
                                    <ol class="breadcrumb m-0">
                                        <li class="breadcrumb-item"><a href="index">Trang chủ</a></li>
                                        <li class="breadcrumb-item"><a href="feedback">Phản hồi</a></li>
                                        <li class="breadcrumb-item active">Chỉnh sửa phản hồi</li>
                                    </ol>
                                </div>
                                <h4 class="page-title">Chỉnh sửa phản hồi</h4>
                            </div>
                        </div>
                    </div>
                    <!-- end page title -->

                    <div class="row">
                        <div class="col-lg-6">
                            <div class="card">
                                <div class="card-header">
                                    <h4 class="header-title">Thông tin phản hồi</h4>
                                </div>
                                <div class="card-body">
    <form method="post" action="feedback">
        <input type="hidden" name="id" value="<%= f.getId() %>"/>

                                        <div class="mb-3">
                                            <label for="productID" class="form-label">Mã sản phẩm <span class="text-danger">*</span></label>
                                            <input type="number" class="form-control" id="productID" name="productID" value="<%= f.getProductID() %>" required>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label for="userID" class="form-label">Mã người dùng <span class="text-danger">*</span></label>
                                            <input type="number" class="form-control" id="userID" name="userID" value="<%= f.getUserID() %>" required>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label for="note" class="form-label">Nội dung phản hồi</label>
                                            <textarea class="form-control" id="note" name="note" rows="4" placeholder="Nhập nội dung phản hồi..."><%= f.getNote() != null ? f.getNote() : "" %></textarea>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label for="rating" class="form-label">Đánh giá <span class="text-danger">*</span></label>
                                            <select class="form-select" id="rating" name="rating" required>
                                                <option value="">-- Chọn đánh giá --</option>
            <%
                                                    for (int i = 1; i <= 5; i++) {
                    String selected = (f.getRating() == i) ? "selected" : "";
            %>
                                                <option value="<%= i %>" <%= selected %>><%= i %> sao</option>
            <%
                }
            %>
        </select>
                                        </div>
                                        
                                        <div class="text-end">
                                            <a href="feedback" class="btn btn-secondary me-2">
                                                <i class="mdi mdi-arrow-left me-1"></i>Quay lại
                                            </a>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="mdi mdi-content-save me-1"></i>Lưu thay đổi
                                            </button>
                                        </div>
    </form>
                                </div>
                            </div>
</div>

                        <div class="col-lg-6">
                            <div class="card">
                                <div class="card-header">
                                    <h4 class="header-title">Hướng dẫn</h4>
                                </div>
                                <div class="card-body">
                                    <div class="alert alert-info">
                                        <h5 class="alert-heading">Lưu ý khi chỉnh sửa phản hồi:</h5>
                                        <ul class="mb-0">
                                            <li>Mã sản phẩm và mã người dùng là bắt buộc</li>
                                            <li>Đánh giá từ 1 đến 5 sao</li>
                                            <li>Nội dung phản hồi không bắt buộc</li>
                                            <li>Kiểm tra kỹ thông tin trước khi lưu</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- end row-->

                </div> <!-- container -->

            </div> <!-- content -->

            <!-- Footer Start -->
            <footer class="footer">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-6">
                            2025 
                        </div>
                        <div class="col-md-6">
                            <div class="text-md-end footer-links d-none d-md-block">
                                <a href="javascript: void(0);">About</a>
                                <a href="javascript: void(0);">Support</a>
                                <a href="javascript: void(0);">Contact Us</a>
                            </div>
                        </div>
                    </div>
                </div>
            </footer>
            <!-- end Footer -->

        </div>

        <!-- ============================================================== -->
        <!-- End Page content -->
        <!-- ============================================================== -->

    </div>
    <!-- END wrapper -->

         <!-- Bootstrap JS -->
     <script src="Admin/assets/js/vendor.min.js"></script>
     <script src="Admin/assets/js/app.min.js"></script>
     
     <script>
         // Logout function
         function logout() {
             // Clear session storage and local storage
             if (typeof(Storage) !== "undefined") {
                 sessionStorage.clear();
                 localStorage.clear();
             }
             
             // Redirect to auth.jsp with correct context path
             window.location.href = '${pageContext.request.contextPath}/auth.jsp';
         }
     </script>
</body>
</html>