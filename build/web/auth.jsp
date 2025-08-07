<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vn">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login page</title>
        <link rel="stylesheet" href="./css/authCss/cssAuth.css?v=<%= System.currentTimeMillis() %>">
        <link rel="shortcut icon" href="./img_svg/mainPage/logo-color.png">
        <script src="https://kit.fontawesome.com/54f0cb7e4a.js" crossorigin="anonymous"></script>
        <style>
            @import url('https://fonts.googleapis.com/css2?family=Pacifico&display=swap');
            
            /* ✅ NOTIFICATION BANNER STYLES */
            .notification-banner {
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                z-index: 9999;
                padding: 15px 20px;
                text-align: center;
                font-weight: 600;
                font-size: 16px;
                animation: slideDown 0.5s ease-out;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            
            .notification-success {
                background: linear-gradient(135deg, #4CAF50, #45a049);
                color: white;
                border-bottom: 3px solid #2e7d32;
            }
            
            .notification-error {
                background: linear-gradient(135deg, #f44336, #d32f2f);
                color: white;
                border-bottom: 3px solid #c62828;
            }
            
            .notification-blocked {
                background: linear-gradient(135deg, #ff9800, #f57c00);
                color: white;
                border-bottom: 3px solid #ef6c00;
            }
            
            .notification-banner i {
                margin-right: 10px;
                font-size: 18px;
            }
            
            .notification-close {
                position: absolute;
                right: 15px;
                top: 50%;
                transform: translateY(-50%);
                background: none;
                border: none;
                color: inherit;
                font-size: 20px;
                cursor: pointer;
                padding: 5px;
            }
            
            @keyframes slideDown {
                from {
                    transform: translateY(-100%);
                    opacity: 0;
                }
                to {
                    transform: translateY(0);
                    opacity: 1;
                }
            }
            
            .fade-out {
                animation: fadeOut 0.5s ease-out forwards;
            }
            
            @keyframes fadeOut {
                to {
                    opacity: 0;
                    transform: translateY(-100%);
                }
            }
        </style>
        <script src="https://kit.fontawesome.com/3a767ca8aa.js" crossorigin="anonymous"></script>
    </head>

    <body>
        <!-- ✅ NOTIFICATION BANNER -->
        <div id="notificationBanner" class="notification-banner" style="display: none;">
            <i id="notificationIcon"></i>
            <span id="notificationMessage"></span>
            <button class="notification-close" onclick="closeNotification()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        
        <%-- Thông báo đăng nhập --%>
        <c:if test="${not empty notifyAuth}">
            <input type="hidden" id="notifyAuth" value="${notifyAuth}" />
            <c:remove var="notifyAuth" scope="request" />
        </c:if>
        
        <%-- Thông báo đăng kí --%>
        <c:if test="${not empty notifySigup}">
            <input type="hidden" id="notifySigup" value="${notifySigup}" />
            <c:remove var="notifySigup" scope="request" />
        </c:if>

        <h2 class="pacifico-regular">HexTech - Công nghệ thay đổi cuộc sống</h2>
        <a class="back-btn" href="userMainPage.jsp"><i class="fas fa-backward"></i> Trang chủ</a>
        <div class="container" id="container">
            <div class="form-container sign-up-container">
                <form action="auth?action=signup" method="POST">
                    <h1>Đăng ký</h1>
                    <div class="social-container">
                        <a href="#" class="social"><i class="fab fa-facebook-f"></i></a>
                        <%-- Sửa lại tên HexTech77 ở đây (Đăng kí)   (Đẫ thêm uri HexTech bên gg)   --%>     
                        <a href="https://accounts.google.com/o/oauth2/auth?scope=email%20profile%20openid&redirect_uri=http://localhost:8080/HexTacos/loginGoogle&response_type=code&client_id=217200688737-k99p0tk3hkv77vui4cf1jkkqdheorca2.apps.googleusercontent.com&approval_prompt=force" 
                           class="social"><i class="fab fa-google-plus-g"></i></a>
                        <%-- End sửa lại tên HexTech77 ở đây --%>
                        <a href="#" class="social"><i class="fab fa-linkedin-in"></i></a>
                    </div>
                    <span>hoặc sử dụng email của bạn để đăng ký</span>
                    <input type="text" name="fullname" placeholder="Họ và tên" required />
                    <input type="email" name="email" placeholder="Email" required />
                    <input type="password" name="password" placeholder="Mật khẩu" required/>
                    <input type="tel" name="phoneNumber" placeholder="Số điện thoại" pattern="[0-9]{10}" title="Số điện thoại phải có 10 chữ số" required/>
                    <button style="cursor: pointer;">Đăng ký</button>
                </form>
            </div>
            <div class="form-container sign-in-container">
                <form action="auth?action=login" method="POST">
                    <h1>Đăng nhập</h1>
                    <div class="social-container">
                        <a href="#" class="social"><i class="fab fa-facebook-f"></i></a>
                        <%-- Sửa lại tên HexTech77 ở đây (Đăng nhập) --%>
                        <a href="https://accounts.google.com/o/oauth2/auth?scope=email%20profile%20openid&redirect_uri=http://localhost:8080/HexTacos/loginGoogle&response_type=code&client_id=217200688737-k99p0tk3hkv77vui4cf1jkkqdheorca2.apps.googleusercontent.com&approval_prompt=force" 
                           class="social"><i class="fab fa-google-plus-g"></i></a>
                        <%-- End sửa lại tên HexTech77 ở đây --%>
                        <a href="#" class="social"><i class="fab fa-linkedin-in"></i></a>
                    </div>
                    <span>Sử dụng tài khoản của bạn</span>
                    <input type="text" placeholder="Email" name="email" />
                    <input type="password" placeholder="Mật khẩu" name="password" />
                    <a href="forgot?action=forgot">Quên mật khẩu?</a>
                    <button type="submit" name="action" value="login" style="cursor: pointer;">Đăng nhập</button>
                </form>
            </div>
            <div class="overlay-container">
                <div class="overlay">
                    <div class="overlay-panel overlay-left">
                        <h1>Welcome Back!</h1>
                        <p> Để nhận được những ưu đãi đặc biệt, vui lòng đăng nhập tài khoản của bạn.</p>
                        <button class="ghost" id="signIn">Đăng nhập</button>
                    </div>
                    <div class="overlay-panel overlay-right">
                        <h1>Hello, Friend!</h1>
                        <p>Đăng ký tài khoản của bạn để bắt đầu hành trình cùng chúng tôi và tận hưởng những ưu đãi đặc biệt.</p>
                        <button class="ghost" id="signUp">Đăng ký</button>
                    </div>
                </div>
            </div>

        </div>

        <div class="login-mobile">
            <div class="btn">
                <form class="register-form" action="auth?action=signup" method="POST">
                    <h1>Tạo tài khoản</h1>
                    <div class="social-container">
                        <a href="#" class="social"><i class="fab fa-facebook-f"></i></a>
                        <%-- Sửa lại tên HexTech77 ở đây (Đăng kí) --%>
                        <a href="https://accounts.google.com/o/oauth2/auth?scope=email%20profile%20openid&redirect_uri=http://localhost:8080/HexTacos/loginGoogle&response_type=code&client_id=217200688737-k99p0tk3hkv77vui4cf1jkkqdheorca2.apps.googleusercontent.com&approval_prompt=force" 
                           class="social"><i class="fab fa-google-plus-g"></i></a>
                        <%-- End sửa lại tên HexTech77 ở đây --%>
                        <a href="#" class="social"><i class="fab fa-linkedin-in"></i></a>
                    </div>
                    <span>sử dụng email để đăng nhập</span>
                    <input type="text" name="fullname" placeholder="Họ và tên" required />
                    <input type="email" name="email" placeholder="Email" required />
                    <input type="password" name="password" placeholder="Mật khẩu" required/>
                    <input type="tel" name="phoneNumber" placeholder="Số điện thoại" pattern="[0-9]{10}" title="Số điện thoại phải có 10 chữ số" required/>
                    <button>Đăng ký</button>
                    <p class="toggle-container"><a href="#" id="signInLink">Đã có tài khoản? Đăng nhập ngay</a></p>
                </form>
            </div>

            <div>
                <form class="login-form" action="auth?action=login" method="POST">
                    <h1>Đăng nhập</h1>
                    <div class="social-container">
                        <a href="#" class="social"><i class="fab fa-facebook-f"></i></a>
                        <%-- Sửa lại tên HexTech77 ở đây (Đăng nhập) --%>
                        <a href="https://accounts.google.com/o/oauth2/auth?scope=email%20profile%20openid&redirect_uri=http://localhost:8080/HexTacos/loginGoogle&response_type=code&client_id=217200688737-k99p0tk3hkv77vui4cf1jkkqdheorca2.apps.googleusercontent.com&approval_prompt=force" 
                           class="social"><i class="fab fa-google-plus-g"></i></a>
                        <%-- End sửa lại tên HexTech77 ở đây --%>
                        <a href="#" class="social"><i class="fab fa-linkedin-in"></i></a>
                    </div>
                    <span>hoặc sử dụng tài khoản của bạn</span>
                    <input type="text" placeholder="Email" name="email" />
                    <input type="password" placeholder="Mật khẩu" name="password" />
                    <div class="quenMK">
                        <a href="forgot?action=forgot">Quên mật khẩu?</a>
                    </div>
                    <button>Đăng nhập</button>
                    <p class="toggle-container"><a href="#" id="signUpLink">Chưa có tài khoản? Đăng ký ngay</a></p>
                </form>
            </div>
        </div>

        <script>
            // ✅ NOTIFICATION BANNER FUNCTIONS
            function showNotification(type, message, icon) {
                const banner = document.getElementById('notificationBanner');
                const iconElement = document.getElementById('notificationIcon');
                const messageElement = document.getElementById('notificationMessage');
                
                // Clear previous classes
                banner.className = 'notification-banner';
                
                // Set content
                iconElement.className = icon;
                messageElement.textContent = message;
                
                // Add type-specific class
                banner.classList.add('notification-' + type);
                
                // Show banner
                banner.style.display = 'block';
                
                // Auto hide after 6 seconds
                setTimeout(function() {
                    closeNotification();
                }, 6000);
            }
            
            function closeNotification() {
                const banner = document.getElementById('notificationBanner');
                banner.classList.add('fade-out');
                
                setTimeout(function() {
                    banner.style.display = 'none';
                    banner.classList.remove('fade-out');
                }, 500);
            }

            // ✅ BALANCED NOTIFICATION HANDLER - Protect nhưng vẫn hoạt động
            window.onload = function () {
                console.log("🔍 [DEBUG] auth.jsp loaded - Balanced protection active...");
                
                // ✅ HIDE BANNER INITIALLY
                var banner = document.getElementById('notificationBanner');
                if (banner) {
                    banner.style.display = 'none';
                    banner.className = 'notification-banner';
                }
                
                // ✅ PROCESS LOGIN NOTIFICATIONS
                var notifyAuthField = document.getElementById('notifyAuth');
                if (notifyAuthField) {
                    var notifyAuth = notifyAuthField.value.trim();
                    console.log("🔍 [DEBUG] Processing notifyAuth:", "'" + notifyAuth + "'");
                    
                    if (notifyAuth === "success") {
                        console.log("✅ [SUCCESS] Login successful - showing green banner");
                        showNotification('success', '🎉 Đăng nhập thành công! Chào mừng bạn quay trở lại!', 'fas fa-check-circle');
                    } else if (notifyAuth === "failed") {
                        console.log("❌ [FAILED] Login failed - showing red banner");
                        showNotification('error', '❌ Đăng nhập thất bại! Vui lòng kiểm tra email và mật khẩu.', 'fas fa-exclamation-triangle');
                    } else if (notifyAuth === "blocked") {
                        console.log("🚨 [BLOCKED] Account blocked - showing orange banner");
                        showNotification('blocked', '🚨 Tài khoản của bạn đã bị khóa! Liên hệ bộ phận chăm sóc khách hàng (0858723794) để được hỗ trợ.', 'fas fa-ban');
                    }
                    notifyAuthField.remove();
                } else {
                    console.log("✅ [FRESH] No notification data - clean page load");
                }
                
                // ✅ PROCESS SIGNUP NOTIFICATIONS
                var notifySignupField = document.getElementById('notifySigup');
                if (notifySignupField) {
                    var notifySignup = notifySignupField.value.trim();
                    console.log("🔍 [DEBUG] Processing notifySignup:", "'" + notifySignup + "'");
                    
                    if (notifySignup === "success") {
                        console.log("✅ [SUCCESS] Signup successful - showing green banner");
                        showNotification('success', '🎉 Đăng ký thành công! Chào mừng bạn đến với HexTech!', 'fas fa-user-plus');
                    } else if (notifySignup === "failed") {
                        console.log("❌ [FAILED] Signup failed - showing red banner");
                        showNotification('error', '❌ Đăng ký thất bại! Email này đã được sử dụng.', 'fas fa-user-times');
                    }
                    notifySignupField.remove();
                } else {
                    console.log("✅ [FRESH] No signup notification - normal");
                }
            };
        </script>

        <script src="./myJs/authJs/myCodeAuth.js"></script>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    </body>

</html>