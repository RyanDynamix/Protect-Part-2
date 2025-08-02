<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chat với AI - HexTech Shop</title>
    <link rel="stylesheet" href="./css/bootstrap.min.css">
    <link rel="shortcut icon" href="./img_svg/mainPage/logo-color.png">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    <style>
        .chat-container {
            max-width: 800px;
            margin: 20px auto;
            border: 1px solid #ddd;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        
        .chat-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 20px;
            text-align: center;
        }
        
        .chat-header h4 {
            margin: 0;
            font-weight: 600;
        }
        
        .chat-messages {
            height: 400px;
            overflow-y: auto;
            padding: 20px;
            background-color: #f8f9fa;
        }
        
        .message {
            margin-bottom: 15px;
            display: flex;
            align-items: flex-start;
        }
        
        .message.user {
            justify-content: flex-end;
        }
        
        .message.ai {
            justify-content: flex-start;
        }
        
        .message-content {
            max-width: 70%;
            padding: 12px 16px;
            border-radius: 18px;
            word-wrap: break-word;
        }
        
        .message.user .message-content {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-bottom-right-radius: 4px;
        }
        
        .message.ai .message-content {
            background: white;
            color: #333;
            border: 1px solid #e0e0e0;
            border-bottom-left-radius: 4px;
        }
        
        .message-time {
            font-size: 11px;
            color: #999;
            margin-top: 5px;
            text-align: center;
        }
        
        .message.user .message-time {
            color: rgba(255, 255, 255, 0.8);
        }
        
        .chat-input-container {
            padding: 20px;
            background: white;
            border-top: 1px solid #e0e0e0;
        }
        
        .chat-input-group {
            display: flex;
            gap: 10px;
        }
        
        .chat-input {
            flex: 1;
            border: 1px solid #ddd;
            border-radius: 25px;
            padding: 12px 20px;
            outline: none;
            transition: border-color 0.3s;
        }
        
        .chat-input:focus {
            border-color: #667eea;
        }
        
        .send-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        .send-btn:hover {
            transform: scale(1.05);
        }
        
        .send-btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }
        
        .typing-indicator {
            display: none;
            padding: 12px 16px;
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 18px;
            border-bottom-left-radius: 4px;
            margin-bottom: 15px;
            max-width: 70%;
        }
        
        .typing-dots {
            display: flex;
            gap: 4px;
        }
        
        .typing-dot {
            width: 8px;
            height: 8px;
            background: #999;
            border-radius: 50%;
            animation: typing 1.4s infinite ease-in-out;
        }
        
        .typing-dot:nth-child(1) { animation-delay: -0.32s; }
        .typing-dot:nth-child(2) { animation-delay: -0.16s; }
        
        @keyframes typing {
            0%, 80%, 100% {
                transform: scale(0.8);
                opacity: 0.5;
            }
            40% {
                transform: scale(1);
                opacity: 1;
            }
        }
        
        .welcome-message {
            text-align: center;
            color: #666;
            font-style: italic;
            margin: 20px 0;
        }
        
        .ai-avatar {
            width: 35px;
            height: 35px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
            margin-right: 10px;
            flex-shrink: 0;
        }
        
        .user-avatar {
            width: 35px;
            height: 35px;
            background: #e0e0e0;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #666;
            font-weight: bold;
            margin-left: 10px;
            flex-shrink: 0;
        }
        
        .back-btn {
            position: absolute;
            top: 15px;
            left: 20px;
            background: rgba(255, 255, 255, 0.2);
            border: none;
            color: white;
            padding: 8px 12px;
            border-radius: 5px;
            text-decoration: none;
            transition: background 0.3s;
        }
        
        .back-btn:hover {
            background: rgba(255, 255, 255, 0.3);
            color: white;
        }
    </style>
</head>
<body>
    <div class="chat-container">
        <div class="chat-header">
            <a href="homePage" class="back-btn">
                <i class='bx bx-arrow-back'></i> Quay lại
            </a>
            <h4><i class='bx bx-bot'></i> Trợ lý AI HexTech</h4>
            <small>Tôi có thể giúp bạn tư vấn sản phẩm, hướng dẫn mua hàng và giải đáp thắc mắc</small>
        </div>
        
        <div class="chat-messages" id="chatMessages">
            <div class="welcome-message">
                <i class='bx bx-message-rounded-dots'></i>
                Xin chào! Tôi là trợ lý AI của HexTech Shop. Bạn có thể hỏi tôi về sản phẩm, giá cả, hoặc bất kỳ thắc mắc nào khác nhé!
            </div>
            
            <c:forEach var="message" items="${chatHistory}">
                <div class="message ${message.messageType}">
                    <c:if test="${message.messageType == 'ai'}">
                        <div class="ai-avatar">AI</div>
                    </c:if>
                    
                    <div class="message-content">
                        <c:if test="${message.messageType == 'user'}">
                            ${message.message}
                        </c:if>
                        <c:if test="${message.messageType == 'ai'}">
                            ${message.response}
                        </c:if>
                        <div class="message-time">
                            <fmt:formatDate value="${message.timestamp}" pattern="HH:mm" />
                        </div>
                    </div>
                    
                    <c:if test="${message.messageType == 'user'}">
                        <div class="user-avatar">Bạn</div>
                    </c:if>
                </div>
            </c:forEach>
        </div>
        
        <div class="typing-indicator" id="typingIndicator">
            <div class="typing-dots">
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
            </div>
        </div>
        
        <div class="chat-input-container">
            <div class="chat-input-group">
                <input type="text" id="messageInput" class="chat-input" placeholder="Nhập tin nhắn của bạn..." maxlength="500">
                <button id="sendBtn" class="send-btn" onclick="sendMessage()">
                    <i class='bx bx-send'></i>
                </button>
            </div>
        </div>
    </div>

    <script src="./myJs/userJs/app.min.js"></script>
    <script>
        const chatMessages = document.getElementById('chatMessages');
        const messageInput = document.getElementById('messageInput');
        const sendBtn = document.getElementById('sendBtn');
        const typingIndicator = document.getElementById('typingIndicator');
        
        // Auto scroll to bottom
        function scrollToBottom() {
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }
        
        // Format time
        function formatTime(timestamp) {
            const date = new Date(timestamp);
            return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
        }
        
        // Add message to chat
        function addMessage(message, response, messageType, timestamp) {
            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${messageType}`;
            
            if (messageType === 'ai') {
                messageDiv.innerHTML = `
                    <div class="ai-avatar">AI</div>
                    <div class="message-content">
                        ${response}
                        <div class="message-time">${formatTime(timestamp)}</div>
                    </div>
                `;
            } else {
                messageDiv.innerHTML = `
                    <div class="message-content">
                        ${message}
                        <div class="message-time">${formatTime(timestamp)}</div>
                    </div>
                    <div class="user-avatar">Bạn</div>
                `;
            }
            
            chatMessages.appendChild(messageDiv);
            scrollToBottom();
        }
        
        // Show/hide typing indicator
        function showTypingIndicator() {
            typingIndicator.style.display = 'block';
            scrollToBottom();
        }
        
        function hideTypingIndicator() {
            typingIndicator.style.display = 'none';
        }
        
        // Send message
        function sendMessage() {
            const message = messageInput.value.trim();
            if (!message) return;
            
            // Disable input and button
            messageInput.disabled = true;
            sendBtn.disabled = true;
            
            // Add user message immediately
            addMessage(message, '', 'user', new Date());
            messageInput.value = '';
            
            // Show typing indicator
            showTypingIndicator();
            
            // Send to server
            const formData = new FormData();
            formData.append('action', 'sendMessage');
            formData.append('message', message);
            
            fetch('chat', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                hideTypingIndicator();
                
                if (data.success) {
                    // Add AI response
                    addMessage('', data.aiResponse, 'ai', data.timestamp);
                } else {
                    // Show error message
                    addMessage('', 'Xin lỗi, có lỗi xảy ra: ' + data.message, 'ai', new Date());
                }
            })
            .catch(error => {
                hideTypingIndicator();
                addMessage('', 'Xin lỗi, có lỗi xảy ra khi kết nối với server.', 'ai', new Date());
                console.error('Error:', error);
            })
            .finally(() => {
                // Re-enable input and button
                messageInput.disabled = false;
                sendBtn.disabled = false;
                messageInput.focus();
            });
        }
        
        // Enter key to send
        messageInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
        
        // Initial scroll to bottom
        scrollToBottom();
    </script>
</body>
</html> 