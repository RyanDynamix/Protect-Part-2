package controller;

import controller.payment.Config;
import dal.DiscountDAO;
import dal.OrderDAO;
import feature.sendEmail.Email;
import java.io.IOException;
import java.util.*;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.*;
import model.Discounts;
import model.Users;
import model.gioHang;
import util.Validate;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;


public class shoppingCartController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users acc = (Users) session.getAttribute("account");

        if (acc == null) {
            response.sendRedirect("auth");
            return;
        }

        OrderDAO dao = new OrderDAO();
        int orderID = dao.findOrderIdNotConfirmed(acc.getUserID());
        List<gioHang> cart = dao.showShoppingCartByID(orderID);

        if (cart != null) {
            cart.forEach(item -> {
                item.setDisString(Validate.doubleToMoney(item.getPrice()));
                item.setTotalString(Validate.doubleToMoney(item.getTotalMoney()));
            });
        }

        // Pass discount information to JSP if it exists
        Double discountAmount = (Double) session.getAttribute("discountAmount");
        if (discountAmount != null) {
            request.setAttribute("discountAmount", discountAmount);
            request.setAttribute("discountAmountFormatted", Validate.doubleToMoney(discountAmount));
            request.setAttribute("originalTotal", session.getAttribute("originalTotal"));
            request.setAttribute("finalTotal", session.getAttribute("finalTotal"));
            request.setAttribute("appliedDiscount", session.getAttribute("appliedDiscount"));
        }

        request.setAttribute("allProductShopping", cart);
        request.getRequestDispatcher("shoppingCart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("orderSubmit".equals(action)) {
            handleOrderSubmit(request, response);
        } else if ("changeQuantity".equals(action)) {
            handleChangeQuantity(request);
            response.sendRedirect("shopping");
        } 
        else if ("applyDiscount".equals(action)){
            handleApplyDiscount(request, response);
            
        }
            
            else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    private void handleApplyDiscount(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users account = (Users) session.getAttribute("account");
        OrderDAO orderDao = new OrderDAO();
        DiscountDAO discountDao = new DiscountDAO();

        // Check if it's an AJAX request
        boolean isAjaxRequest = "XMLHttpRequest".equals(
            request.getHeader("X-Requested-With")
        );

        if (account == null) {
            if (isAjaxRequest) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                    "<div class='alert alert-danger'>Vui lòng đăng nhập để sử dụng mã giảm giá.</div>"
                );
            } else {
                response.sendRedirect("auth");
            }
            return;
        }

        // Get the discount code from the request
        String discountCode = request.getParameter("discountCode");
        
        // Find the order that is not yet confirmed
        int orderID = orderDao.findOrderIdNotConfirmed(account.getUserID());
        
        if (orderID == 0) {
            // No active order found
            if (isAjaxRequest) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                    "<div class='alert alert-danger'>Không có đơn hàng để áp dụng mã giảm giá.</div>"
                );
            } else {
                request.setAttribute("discountError", "Không có đơn hàng để áp dụng mã giảm giá.");
                request.getRequestDispatcher("shoppingCart.jsp").forward(request, response);
            }
            return;
        }

        // Validate the discount code
        Discounts discount = discountDao.getDiscountByCode(discountCode);
        
        if (discount == null) {
            // Discount code not found
            if (isAjaxRequest) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                    "<div class='alert alert-danger'>Mã giảm giá không tồn tại.</div>"
                );
            } else {
                request.setAttribute("discountError", "Mã giảm giá không tồn tại.");
                request.getRequestDispatcher("shoppingCart.jsp").forward(request, response);
            }
            return;
        }

        // Check if discount is active and valid
        if (!discount.isStatus()) {
            if (isAjaxRequest) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                    "<div class='alert alert-danger'>Mã giảm giá đã bị vô hiệu hóa.</div>"
                );
            } else {
                request.setAttribute("discountError", "Mã giảm giá đã bị vô hiệu hóa.");
                request.getRequestDispatcher("shoppingCart.jsp").forward(request, response);
            }
            return;
        }

        // Check discount validity period
        Date now = new Date();
        if (now.before(discount.getStartDate()) || now.after(discount.getEndDate())) {
            if (isAjaxRequest) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                    "<div class='alert alert-danger'>Mã giảm giá đã hết hạn.</div>"
                );
            } else {
                request.setAttribute("discountError", "Mã giảm giá đã hết hạn.");
                request.getRequestDispatcher("shoppingCart.jsp").forward(request, response);
            }
            return;
        }

        // Calculate total order value to check minimum order value
        List<gioHang> cartItems = orderDao.showShoppingCartByID(orderID);
        double totalOrderValue = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        if (totalOrderValue < discount.getMinOrderValue().doubleValue()) {
            if (isAjaxRequest) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(
                    "<div class='alert alert-danger'>Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã giảm giá. Cần tối thiểu " + Validate.doubleToMoney(discount.getMinOrderValue().doubleValue()) + "</div>"
                );
            } else {
                request.setAttribute("discountError", "Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã giảm giá.");
                request.getRequestDispatcher("shoppingCart.jsp").forward(request, response);
            }
            return;
        }

        // Calculate discount amount
        double discountAmount = 0;
        if ("percentage".equals(discount.getType())) {
            // Percentage discount
            discountAmount = totalOrderValue * (discount.getValue().doubleValue() / 100);
        } else {
            // Fixed amount discount
            discountAmount = discount.getValue().doubleValue();
        }

        // Make sure discount doesn't exceed total order value
        if (discountAmount > totalOrderValue) {
            discountAmount = totalOrderValue;
        }

        double finalTotal = totalOrderValue - discountAmount;

        // Store discount information in session for payment processing
        session.setAttribute("appliedDiscount", discount);
        session.setAttribute("discountAmount", discountAmount);
        session.setAttribute("originalTotal", totalOrderValue);
        session.setAttribute("finalTotal", finalTotal);

        // If all checks pass, send success response with calculated amounts
        if (isAjaxRequest) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                "{"
                + "\"success\": true,"
                + "\"discountCode\": \"" + discount.getCode() + "\","
                + "\"discountName\": \"" + discount.getName() + "\","
                + "\"discountType\": \"" + (discount.getType().equals("percentage") ? "Phần trăm" : "Giá trị cố định") + "\","
                + "\"discountValue\": \"" + (discount.getType().equals("percentage") ? discount.getValue() + "%" : Validate.doubleToMoney(discount.getValue().doubleValue()) + " VND") + "\","
                + "\"minOrderValue\": \"" + Validate.doubleToMoney(discount.getMinOrderValue().doubleValue()) + " VND\","
                + "\"startDate\": \"" + discount.getStartDateString() + "\","
                + "\"endDate\": \"" + discount.getEndDateString() + "\","
                + "\"originalTotal\": \"" + Validate.doubleToMoney(totalOrderValue) + "đ\","
                + "\"discountAmount\": \"" + Validate.doubleToMoney(discountAmount) + "đ\","
                + "\"finalTotal\": \"" + Validate.doubleToMoney(finalTotal) + "đ\""
                + "}"
            );
        } else {
            // If not an AJAX request, proceed with the original logic
            request.setAttribute("appliedDiscount", discount);
            request.setAttribute("discountAmount", discountAmount);
            request.setAttribute("originalTotal", totalOrderValue);
            request.setAttribute("finalTotal", finalTotal);
            request.getRequestDispatcher("shoppingCart.jsp").forward(request, response);
        }
    }

    private void handleOrderSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users acc = (Users) session.getAttribute("account");

        if (acc == null) {
            response.sendRedirect("auth");
            return;
        }

        OrderDAO dao = new OrderDAO();
        int orderID = dao.findOrderIdNotConfirmed(acc.getUserID());

        if (orderID == 0) {
            orderID = dao.createNewOrder(acc.getUserID());
        }

        // Lấy danh sách productID đã tick
        String[] selectedProducts = request.getParameterValues("selectedProducts");
        if (selectedProducts == null || selectedProducts.length == 0) {
            response.sendRedirect("shopping?notifyOrder=failed"); // hoặc báo lỗi chưa chọn sản phẩm
            return;
        }

        // Lấy toàn bộ cart của user
        List<gioHang> cart = dao.showShoppingCartByID(orderID);
        // Tạo danh sách các sản phẩm được chọn
        List<gioHang> selectedCart = new ArrayList<>();
        for (String sp : selectedProducts) {
            String[] info = sp.split("___"); // vd: "15___Đen"
            int pid = Integer.parseInt(info[0]);
            String color = info.length > 1 ? info[1] : "";

            for (gioHang g : cart) {
                if (g.getProductID() == pid && (color.isEmpty() || g.getColor().equals(color))) {
                    selectedCart.add(g);
                }
            }
        }

        if (selectedCart.isEmpty()) {
            response.sendRedirect("shopping?notifyOrder=failed");
            return;
        }

        String nameOrder = request.getParameter("nameOrder");
        String phone = request.getParameter("phone");
        String deliveryLocation = request.getParameter("deliveryLocation");
        String paymentMethod = request.getParameter("paymentMethod");

        // Tính tổng tiền của các sản phẩm được chọn
        double totalAmount = selectedCart.stream().mapToDouble(g -> g.getTotalMoney()).sum();
        
        // Shipping fee and shipping discount (effectively free shipping)
        double shippingFee = 50000.0; // 50,000 VND shipping fee
        double shippingDiscount = 50000.0; // 50,000 VND shipping discount (FreeShip program)
        totalAmount = totalAmount + shippingFee - shippingDiscount; // Net effect: no shipping cost
        
        // Check if discount has been applied and use the final total
        Double finalTotal = (Double) session.getAttribute("finalTotal");
        Double discountAmount = (Double) session.getAttribute("discountAmount");
        
        if (finalTotal != null && discountAmount != null && discountAmount > 0) {
            // Recalculate with selected cart total, discount, shipping fee, and shipping discount
            double selectedCartTotal = selectedCart.stream().mapToDouble(g -> g.getTotalMoney()).sum();
            totalAmount = selectedCartTotal - discountAmount + shippingFee - shippingDiscount;
            System.out.println("Using discounted total for selected products: Cart=" + selectedCartTotal + ", Discount=" + discountAmount + ", Shipping=" + shippingFee + ", ShippingDiscount=" + shippingDiscount + ", Final=" + totalAmount);
        }
        
        // Kiểm tra tổng tiền hợp lệ
        if (totalAmount <= 0) {
            session.setAttribute("orderError", "Tổng tiền đơn hàng không hợp lệ: " + totalAmount + "đ. Vui lòng kiểm tra lại giỏ hàng.");
            response.sendRedirect("shopping?notifyOrder=failed");
            return;
        }

        dao.updateOrderConfirmedDB(orderID, nameOrder, phone, deliveryLocation, paymentMethod, totalAmount);

        if ("COD".equalsIgnoreCase(paymentMethod)) {
            dao.confirmOrder(orderID, "COD", "Paid");
            sendOrderEmail(acc.getEmail(), nameOrder, phone, deliveryLocation, selectedCart); // Gửi đúng selectedCart
            
            // Clear discount information from session after successful order
            clearDiscountFromSession(session);
            
            response.sendRedirect("shopping?notifyOrder=success");

        } else if ("PAYOS".equalsIgnoreCase(paymentMethod)) {
            try {
                String clientId = Config.CLIENT_ID;
                String apiKey = Config.API_KEY;
                String checksumKey = Config.CHECKSUM_KEY;
                PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

                // Tạo orderCode duy nhất bằng cách sử dụng timestamp và orderID
                long orderCode = System.currentTimeMillis() / 1000 + orderID;
                int amount = (int) Math.round(totalAmount);
                
                // Debug info
                System.out.println("PayOS Debug - OrderID: " + orderID + ", OrderCode: " + orderCode + ", TotalAmount: " + totalAmount + ", Amount: " + amount);
                
                // Kiểm tra lại amount trước khi gửi đến PayOS
                if (amount <= 0) {
                    throw new IllegalArgumentException("Amount must be positive, got: " + amount);
                }

                // Nếu muốn liệt kê nhiều item, bạn có thể duyệt cart thành List<ItemData>
                ItemData itemData = ItemData.builder()
                        .name("Đơn hàng #" + orderID)
                        .quantity(1)
                        .price(amount)
                        .build();

                PaymentData paymentData = PaymentData.builder()
                        .orderCode(orderCode)
                        .amount(amount)
                        .description("Thanh toán đơn hàng #" + orderID)
                        .returnUrl(Config.RETURN_URL + "?orderID=" + orderID) // Thêm orderID vào return URL
                        .cancelUrl(Config.CANCEL_URL + "?orderID=" + orderID) // Thêm orderID vào cancel URL
                        .item(itemData)
                        .build();

                CheckoutResponseData result = payOS.createPaymentLink(paymentData);

                // Lưu trạng thái đơn hàng là "Chờ thanh toán"
                dao.confirmOrder(orderID, "PAYOS", "Pending");
                response.sendRedirect(result.getCheckoutUrl());

            } catch (Exception ex) {
                ex.printStackTrace();
                // Gửi thông tin lỗi chi tiết hơn về view
                String errorMessage = "Thanh toán PayOS thất bại: " + ex.getMessage();
                if (ex.getMessage().contains("đã tồn tại") || ex.getMessage().contains("already exists")) {
                    errorMessage = "Đơn hàng này đã được tạo thanh toán trước đó. Vui lòng kiểm tra lại hoặc tạo đơn hàng mới.";
                }
                session.setAttribute("orderError", errorMessage);
                response.sendRedirect("shopping?notifyOrder=failed");
            }

        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Phương thức thanh toán không hợp lệ");
        }
    }

    //    private String generateVnpayUrl(int orderID, double totalAmount, HttpServletRequest request) throws IOException {
    //        HttpSession session = request.getSession();
    //        Users acc = (Users) session.getAttribute("account");
    //
    //        // Lưu thông tin đơn hàng vào session
    //        session.setAttribute("pendingOrderID", orderID);
    //        session.setAttribute("pendingAmount", totalAmount);
    //        session.setAttribute("pendingNameOrder", request.getParameter("nameOrder"));
    //        session.setAttribute("pendingPhone", request.getParameter("phone"));
    //        session.setAttribute("pendingDeliveryLocation", request.getParameter("deliveryLocation"));
    //
    //        Map<String, String> vnp_Params = new HashMap<>();
    //        vnp_Params.put("vnp_Version", "2.1.0");
    //        vnp_Params.put("vnp_Command", "pay");
    //        vnp_Params.put("vnp_TmnCode", Config.vnp_TmnCode);
    //        vnp_Params.put("vnp_Amount", String.valueOf((long) (totalAmount * 100)));
    //        vnp_Params.put("vnp_CurrCode", "VND");
    //        vnp_Params.put("vnp_TxnRef", String.valueOf(orderID)); // Sử dụng orderID thực tế
    //        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang #" + orderID);
    //        vnp_Params.put("vnp_OrderType", "other");
    //        vnp_Params.put("vnp_Locale", "vn");
    //        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
    //        vnp_Params.put("vnp_IpAddr", Config.getIpAddress(request));
    //
    //        // Add timestamp
    //        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    //        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    //        String vnp_CreateDate = formatter.format(cld.getTime());
    //        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
    //
    //        cld.add(Calendar.MINUTE, 15); // 15 minutes expiry
    //        String vnp_ExpireDate = formatter.format(cld.getTime());
    //        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
    //
    //        // Build hash và query string theo cách chuẩn
    //        String vnp_SecureHash = Config.hashAllFields(vnp_Params);
    //        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
    //
    //        // Build final URL
    //        StringBuilder query = new StringBuilder();
    //        for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
    //            query.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
    //            query.append("=");
    //            query.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
    //            query.append("&");
    //        }
    //        query.setLength(query.length() - 1); // remove last '&'
    //
    //        return Config.vnp_PayUrl + "?" + query.toString();
    //    }
    private void handleChangeQuantity(HttpServletRequest request) {
        OrderDAO dao = new OrderDAO();
        int orderID = Integer.parseInt(request.getParameter("orderID"));
        int totalItems = Integer.parseInt(request.getParameter("totalItems"));

        for (int i = 0; i < totalItems; i++) {
            int pid = Integer.parseInt(request.getParameter("productID_" + i));
            int qty = Integer.parseInt(request.getParameter("quantity_" + i));
            String color = request.getParameter("color_" + i);
            dao.updateOrderDetailsByOrderID(orderID, pid, qty, color);
        }
    }

    private void sendOrderEmail(String email, String nameOrder, String phone,
            String deliveryLocation, List<gioHang> cart) {
        double sumMoney = 0;
        StringBuilder detail = new StringBuilder();

        for (gioHang item : cart) {
            item.setDisString(Validate.doubleToMoney(item.getPrice()));
            item.setTotalString(Validate.doubleToMoney(item.getTotalMoney()));
            sumMoney += item.getTotalMoney();
            detail.append("<p>")
                    .append(item.getProductName())
                    .append(" – Số lượng: ").append(item.getQuantity())
                    .append(", Màu: ").append(item.getColor())
                    .append(", Thành tiền: ").append(item.getTotalString())
                    .append("</p><hr>");
        }

        detail.append("<h3>Tổng đơn hàng: ")
                .append(Validate.doubleToMoney(sumMoney))
                .append("đ</h3>");

        String subject = "Cảm ơn " + nameOrder + " đã đặt hàng tại HexTech!";
        String html = "<h2>Đơn hàng của bạn</h2>"
                + "<p>Người nhận: " + nameOrder + "</p>"
                + "<p>Điện thoại: " + phone + "</p>"
                + "<p>Địa chỉ: " + deliveryLocation + "</p>"
                + detail
                + "<p>HexTech cảm ơn bạn!</p>";

        Email.sendEmail(email, subject, html);
    }

    private void clearDiscountFromSession(HttpSession session) {
        session.removeAttribute("appliedDiscount");
        session.removeAttribute("discountAmount");
        session.removeAttribute("originalTotal");
        session.removeAttribute("finalTotal");
    }

    @Override
    public String getServletInfo() {
        return "ShoppingCartController handling COD and VNPAY";
    }
}
