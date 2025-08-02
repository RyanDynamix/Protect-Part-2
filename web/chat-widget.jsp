<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<style>
    .chat-widget {
        position: fixed;
        bottom: 20px;
        right: 20px;
        z-index: 1000;
        font-family: Arial, sans-serif;
    }
    
    .chat-widget-button {
        width: 60px;
        height: 60px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        border-radius: 50%;
        color: white;
        font-size: 24px;
        cursor: pointer;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .chat-widget-button:hover {
        transform: scale(1.1);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
    }
    
    .chat-widget-popup {
        position: fixed;
        bottom: 90px;
        right: 20px;
        width: 350px;
        height: 500px;
        background: white;
        border-radius: 15px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        display: none;
        flex-direction: column;
        overflow: hidden;
        z-index: 1001;
    }
    
    .chat-widget-header {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 15px;
        text-align: center;
        position: relative;
    }
    
    .chat-widget-header h5 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
    }
    
    .chat-widget-close {
        position: absolute;
        top: 10px;
        right: 15px;
        background: none;
        border: none;
        color: white;
        font-size: 20px;
        cursor: pointer;
        padding: 0;
        width: 30px;
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .chat-widget-messages {
        flex: 1;
        overflow-y: auto;
        padding: 15px;
        background: #f8f9fa;
    }
    
    .chat-widget-message {
        margin-bottom: 10px;
        display: flex;
        align-items: flex-start;
    }
    
    .chat-widget-message.user {
        justify-content: flex-end;
    }
    
    .chat-widget-message.ai {
        justify-content: flex-start;
    }
    
    .chat-widget-message-content {
        max-width: 80%;
        padding: 8px 12px;
        border-radius: 15px;
        font-size: 14px;
        word-wrap: break-word;
    }
    
    .chat-widget-message.user .chat-widget-message-content {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-bottom-right-radius: 5px;
    }
    
    .chat-widget-message.ai .chat-widget-message-content {
        background: white;
        color: #333;
        border: 1px solid #e0e0e0;
        border-bottom-left-radius: 5px;
    }
    
    .chat-widget-input-container {
        padding: 15px;
        background: white;
        border-top: 1px solid #e0e0e0;
    }
    
    .chat-widget-input-group {
        display: flex;
        gap: 8px;
    }
    
    .chat-widget-input {
        flex: 1;
        border: 1px solid #ddd;
        border-radius: 20px;
        padding: 8px 15px;
        outline: none;
        font-size: 14px;
    }
    
    .chat-widget-input:focus {
        border-color: #667eea;
    }
    
    .chat-widget-send-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 50%;
        width: 35px;
        height: 35px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        font-size: 16px;
    }
    
    .chat-widget-send-btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }
    
    .chat-widget-typing {
        display: none;
        padding: 8px 12px;
        background: white;
        border: 1px solid #e0e0e0;
        border-radius: 15px;
        border-bottom-left-radius: 5px;
        margin-bottom: 10px;
        max-width: 80%;
    }
    
    .chat-widget-typing-dots {
        display: flex;
        gap: 3px;
    }
    
    .chat-widget-typing-dot {
        width: 6px;
        height: 6px;
        background: #999;
        border-radius: 50%;
        animation: typing 1.4s infinite ease-in-out;
    }
    
    .chat-widget-typing-dot:nth-child(1) { animation-delay: -0.32s; }
    .chat-widget-typing-dot:nth-child(2) { animation-delay: -0.16s; }
    
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
    
    .chat-widget-welcome {
        text-align: center;
        color: #666;
        font-size: 13px;
        margin: 10px 0;
        font-style: italic;
    }
    
    .notification-badge {
        position: absolute;
        top: -5px;
        right: -5px;
        background: #ff4757;
        color: white;
        border-radius: 50%;
        width: 20px;
        height: 20px;
        font-size: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        display: none;
    }
    
    @media (max-width: 768px) {
        .chat-widget-popup {
            width: calc(100vw - 40px);
            height: 60vh;
            right: 20px;
            left: 20px;
        }
    }
</style>

<div class="chat-widget">
    <button class="chat-widget-button" onclick="toggleChatWidget()" id="chatWidgetBtn">
        <i class='bx bx-message-rounded-dots'></i>
        <span class="notification-badge" id="notificationBadge">0</span>
    </button>
    
    <div class="chat-widget-popup" id="chatWidgetPopup">
        <div class="chat-widget-header">
            <button class="chat-widget-close" onclick="toggleChatWidget()">
                <i class='bx bx-x'></i>
            </button>
            <h5><i class='bx bx-bot'></i> Trợ lý AI HexTech</h5>
            <small>Tư vấn sản phẩm & hỗ trợ khách hàng</small>
        </div>
        
        <div class="chat-widget-messages" id="chatWidgetMessages">
            <div class="chat-widget-welcome">
                <i class='bx bx-message-rounded-dots'></i>
                Xin chào! Tôi có thể giúp gì cho bạn?
            </div>
        </div>
        
        <div class="chat-widget-typing" id="chatWidgetTyping">
            <div class="chat-widget-typing-dots">
                <div class="chat-widget-typing-dot"></div>
                <div class="chat-widget-typing-dot"></div>
                <div class="chat-widget-typing-dot"></div>
            </div>
        </div>
        
        <div class="chat-widget-input-container">
            <div class="chat-widget-input-group">
                <input type="text" id="chatWidgetInput" class="chat-widget-input" placeholder="Nhập tin nhắn..." maxlength="200">
                <button id="chatWidgetSendBtn" class="chat-widget-send-btn" onclick="sendWidgetMessage()">
                    <i class='bx bx-send'></i>
                </button>
            </div>
        </div>
    </div>
</div>

<script>
    let chatWidgetOpen = false;
    let unreadCount = 0;
    
    // Toggle chat widget
    function toggleChatWidget() {
        const popup = document.getElementById('chatWidgetPopup');
        const btn = document.getElementById('chatWidgetBtn');
        
        if (chatWidgetOpen) {
            popup.style.display = 'none';
            chatWidgetOpen = false;
        } else {
            popup.style.display = 'flex';
            chatWidgetOpen = true;
            // Reset unread count when opening
            unreadCount = 0;
            updateNotificationBadge();
            // Focus on input
            setTimeout(() => {
                document.getElementById('chatWidgetInput').focus();
            }, 100);
        }
    }
    
    // Update notification badge
    function updateNotificationBadge() {
        const badge = document.getElementById('notificationBadge');
        if (unreadCount > 0) {
            badge.style.display = 'flex';
            badge.textContent = unreadCount > 99 ? '99+' : unreadCount;
        } else {
            badge.style.display = 'none';
        }
    }
    
    // Add message to widget
    function addWidgetMessage(message, response, messageType) {
        const messagesContainer = document.getElementById('chatWidgetMessages');
        const messageDiv = document.createElement('div');
        messageDiv.className = `chat-widget-message ${messageType}`;
        
        if (messageType === 'ai') {
            messageDiv.innerHTML = `
                <div class="chat-widget-message-content">
                    ${response}
                </div>
            `;
        } else {
            messageDiv.innerHTML = `
                <div class="chat-widget-message-content">
                    ${message}
                </div>
            `;
        }
        
        messagesContainer.appendChild(messageDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
        
        // Increment unread count if widget is closed
        if (!chatWidgetOpen && messageType === 'ai') {
            unreadCount++;
            updateNotificationBadge();
        }
    }
    
    // Show/hide typing indicator
    function showWidgetTyping() {
        document.getElementById('chatWidgetTyping').style.display = 'block';
        document.getElementById('chatWidgetMessages').scrollTop = document.getElementById('chatWidgetMessages').scrollHeight;
    }
    
    function hideWidgetTyping() {
        document.getElementById('chatWidgetTyping').style.display = 'none';
    }
    
    // Send message from widget
    function sendWidgetMessage() {
        const input = document.getElementById('chatWidgetInput');
        const sendBtn = document.getElementById('chatWidgetSendBtn');
        const message = input.value.trim();
        
        if (!message) return;
        
        // Disable input and button
        input.disabled = true;
        sendBtn.disabled = true;
        
        // Add user message immediately
        addWidgetMessage(message, '', 'user');
        input.value = '';
        
        // Show typing indicator
        showWidgetTyping();
        
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
            hideWidgetTyping();
            
            if (data.success) {
                // Add AI response
                addWidgetMessage('', data.aiResponse, 'ai');
            } else {
                // Show error message
                addWidgetMessage('', 'Xin lỗi, có lỗi xảy ra: ' + data.message, 'ai');
            }
        })
        .catch(error => {
            hideWidgetTyping();
            addWidgetMessage('', 'Xin lỗi, có lỗi xảy ra khi kết nối với server.', 'ai');
            console.error('Error:', error);
        })
        .finally(() => {
            // Re-enable input and button
            input.disabled = false;
            sendBtn.disabled = false;
            input.focus();
        });
    }
    
    // Enter key to send
    document.getElementById('chatWidgetInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendWidgetMessage();
        }
    });
    
    // Check for unread messages periodically
    function checkUnreadMessages() {
        fetch('chat?action=getUnreadCount')
        .then(response => response.json())
        .then(data => {
            if (data.unreadCount > 0 && !chatWidgetOpen) {
                unreadCount = data.unreadCount;
                updateNotificationBadge();
            }
        })
        .catch(error => console.error('Error checking unread messages:', error));
    }
    
    // Check unread messages every 30 seconds
    setInterval(checkUnreadMessages, 30000);
    
    // Initial check
    checkUnreadMessages();
</script> 