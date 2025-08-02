# Protect-Part-2

## HexTech Shop - Hệ thống bán hàng trực tuyến

### Tính năng ChatAI với ChatGPT

Dự án đã được tích hợp tính năng **ChatAI** sử dụng API của ChatGPT để hỗ trợ khách hàng.

#### 🚀 **Tính năng ChatAI:**

1. **Trợ lý AI thông minh**
   - Tư vấn sản phẩm (iPhone, Samsung, OPPO, Xiaomi, iPad, Galaxy Tab...)
   - Hướng dẫn mua hàng và thanh toán
   - Giải đáp thắc mắc về chính sách bảo hành, đổi trả
   - Hỗ trợ kỹ thuật cơ bản
   - Thông tin về khuyến mãi và mã giảm giá

2. **Giao diện chat đa dạng**
   - **Trang chat đầy đủ**: `/chat` - Giao diện chat toàn màn hình
   - **Widget chat**: Nhúng vào các trang khác, hiển thị dạng popup

3. **Tính năng nâng cao**
   - Lưu trữ lịch sử chat theo từng user
   - Context-aware: AI nhớ được cuộc hội thoại trước đó
   - Real-time typing indicator
   - Notification badge cho tin nhắn chưa đọc
   - Responsive design cho mobile

#### 🔧 **Cài đặt:**

1. **Database:**
   ```sql
   -- Chạy script tạo bảng ChatMessages
   EXEC ChatMessages.sql
   ```

2. **API Key:**
   - Cập nhật API key OpenAI trong `ChatGPTService.java`
   ```java
   private static final String API_KEY = "";
   ```

3. **Dependencies:**
   - Gson library cho JSON parsing
   - Jakarta EE cho web framework

#### 📁 **Cấu trúc files:**

```
src/java/
├── model/
│   └── ChatMessage.java          # Model cho tin nhắn chat
├── dal/
│   └── ChatDAO.java             # Data Access Layer cho chat
├── controller/
│   └── ChatController.java      # Controller xử lý chat
└── feature/chatAI/
    └── ChatGPTService.java      # Service tích hợp ChatGPT API

web/
├── chat.jsp                     # Trang chat đầy đủ
├── chat-widget.jsp              # Widget chat nhỏ
└── userMainPage.jsp             # Trang chủ (đã tích hợp widget)
```

#### 🎯 **Cách sử dụng:**

1. **Truy cập chat đầy đủ:**
   - URL: `http://localhost:8080/HexTacos/chat`
   - Yêu cầu đăng nhập

2. **Sử dụng widget chat:**
   - Hiển thị ở góc phải dưới mọi trang
   - Click để mở/đóng
   - Có notification badge cho tin nhắn mới

#### 🔒 **Bảo mật:**

- Chỉ user đã đăng nhập mới có thể sử dụng chat
- Session-based authentication
- Input validation và sanitization
- Rate limiting (có thể thêm)

#### 💡 **Tùy chỉnh:**

1. **Thay đổi prompt system:**
   - Chỉnh sửa `systemPrompt` trong `ChatGPTService.java`

2. **Thay đổi giao diện:**
   - CSS trong `chat.jsp` và `chat-widget.jsp`

3. **Thêm tính năng:**
   - File upload
   - Voice chat
   - Multi-language support

#### 📊 **Monitoring:**

- Log chat history trong database
- Track usage statistics
- Monitor API calls và costs

---

**Lưu ý:** Đảm bảo có API key OpenAI hợp lệ và đủ credits để sử dụng ChatGPT API.