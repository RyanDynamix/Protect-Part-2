package controller;

import dal.ChatDAO;
import feature.chatAI.ChatGPTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import model.ChatMessage;
import model.Users;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet(name="ChatController", urlPatterns={"/chat"})
public class ChatController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ChatController</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChatController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("account");
        
        if (user == null) {
            response.sendRedirect("auth");
            return;
        }
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "getHistory":
                getChatHistory(request, response, user);
                break;
            case "getUnreadCount":
                getUnreadCount(request, response, user);
                break;
            default:
                // Hiển thị trang chat
                showChatPage(request, response, user);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("account");
        
        if (user == null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Vui lòng đăng nhập để sử dụng chat");
            response.getWriter().write(jsonResponse.toString());
            return;
        }
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "sendMessage":
                sendMessage(request, response, user);
                break;
            case "markAsRead":
                markAsRead(request, response, user);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                break;
        }
    }
    
    private void showChatPage(HttpServletRequest request, HttpServletResponse response, Users user) 
            throws ServletException, IOException {
        ChatDAO chatDAO = new ChatDAO();
        List<ChatMessage> chatHistory = chatDAO.getChatHistoryByUserID(user.getUserID());
        
        request.setAttribute("chatHistory", chatHistory);
        request.getRequestDispatcher("chat.jsp").forward(request, response);
    }
    
    private void sendMessage(HttpServletRequest request, HttpServletResponse response, Users user) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String userMessage = request.getParameter("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Tin nhắn không được để trống");
            response.getWriter().write(jsonResponse.toString());
            return;
        }
        
        try {
            ChatDAO chatDAO = new ChatDAO();
            
            // Lấy lịch sử chat để tạo context
            List<ChatMessage> chatHistory = chatDAO.getChatHistoryByUserID(user.getUserID());
            List<ChatGPTService.Message> conversationHistory = ChatGPTService.createConversationHistory(chatHistory);
            
            // Gọi ChatGPT API
            String aiResponse = ChatGPTService.getChatGPTResponse(userMessage, conversationHistory);
            
            // Lưu tin nhắn vào database
            ChatMessage chatMessage = new ChatMessage(user.getUserID(), userMessage, aiResponse, "user");
            int messageID = chatDAO.insertChatMessage(chatMessage);
            
            if (messageID > 0) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("messageID", messageID);
                jsonResponse.addProperty("userMessage", userMessage);
                jsonResponse.addProperty("aiResponse", aiResponse);
                jsonResponse.addProperty("timestamp", chatMessage.getTimestamp().toString());
                
                response.getWriter().write(jsonResponse.toString());
            } else {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Không thể lưu tin nhắn");
                response.getWriter().write(jsonResponse.toString());
            }
            
        } catch (Exception e) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Có lỗi xảy ra: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
    
    private void getChatHistory(HttpServletRequest request, HttpServletResponse response, Users user) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            ChatDAO chatDAO = new ChatDAO();
            List<ChatMessage> chatHistory = chatDAO.getChatHistoryByUserID(user.getUserID());
            
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(chatHistory);
            response.getWriter().write(jsonResponse);
            
        } catch (Exception e) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Có lỗi xảy ra: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
    
    private void getUnreadCount(HttpServletRequest request, HttpServletResponse response, Users user) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            ChatDAO chatDAO = new ChatDAO();
            int unreadCount = chatDAO.getUnreadMessageCount(user.getUserID());
            
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("unreadCount", unreadCount);
            response.getWriter().write(jsonResponse.toString());
            
        } catch (Exception e) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Có lỗi xảy ra: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
    
    private void markAsRead(HttpServletRequest request, HttpServletResponse response, Users user) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String messageIDStr = request.getParameter("messageID");
        if (messageIDStr == null || messageIDStr.trim().isEmpty()) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Message ID không hợp lệ");
            response.getWriter().write(jsonResponse.toString());
            return;
        }
        
        try {
            int messageID = Integer.parseInt(messageIDStr);
            ChatDAO chatDAO = new ChatDAO();
            boolean success = chatDAO.updateMessageAsRead(messageID);
            
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", success);
            response.getWriter().write(jsonResponse.toString());
            
        } catch (NumberFormatException e) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Message ID không hợp lệ");
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Có lỗi xảy ra: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }

    @Override
    public String getServletInfo() {
        return "Chat Controller for AI Chat functionality";
    }
} 