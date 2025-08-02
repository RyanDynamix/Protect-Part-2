package feature.chatAI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatGPTService {
    
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = ""; // Thay bằng API key thật
    private static final String MODEL = "gpt-3.5-turbo";
    private static final int MAX_TOKENS = 1000;
    
    private static final Logger LOGGER = Logger.getLogger(ChatGPTService.class.getName());
    
    public static class Message {
        private String role;
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class ChatRequest {
        private String model;
        private List<Message> messages;
        private int max_tokens;
        private double temperature;
        
        public ChatRequest(String model, List<Message> messages, int max_tokens, double temperature) {
            this.model = model;
            this.messages = messages;
            this.max_tokens = max_tokens;
            this.temperature = temperature;
        }
        
        // Getters and setters
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }
        public int getMax_tokens() { return max_tokens; }
        public void setMax_tokens(int max_tokens) { this.max_tokens = max_tokens; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
    }
    
    public static class ChatResponse {
        private String id;
        private String object;
        private long created;
        private String model;
        private Choice[] choices;
        private Usage usage;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getObject() { return object; }
        public void setObject(String object) { this.object = object; }
        public long getCreated() { return created; }
        public void setCreated(long created) { this.created = created; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Choice[] getChoices() { return choices; }
        public void setChoices(Choice[] choices) { this.choices = choices; }
        public Usage getUsage() { return usage; }
        public void setUsage(Usage usage) { this.usage = usage; }
    }
    
    public static class Choice {
        private int index;
        private Message message;
        private String finish_reason;
        
        // Getters and setters
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
        public String getFinish_reason() { return finish_reason; }
        public void setFinish_reason(String finish_reason) { this.finish_reason = finish_reason; }
    }
    
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
        
        // Getters and setters
        public int getPrompt_tokens() { return prompt_tokens; }
        public void setPrompt_tokens(int prompt_tokens) { this.prompt_tokens = prompt_tokens; }
        public int getCompletion_tokens() { return completion_tokens; }
        public void setCompletion_tokens(int completion_tokens) { this.completion_tokens = completion_tokens; }
        public int getTotal_tokens() { return total_tokens; }
        public void setTotal_tokens(int total_tokens) { this.total_tokens = total_tokens; }
    }
    
    public static String getChatGPTResponse(String userMessage, List<Message> conversationHistory) {
        try {
            // Tạo context cho AI về HexTech Shop
            String systemPrompt = """
                Bạn là trợ lý AI của HexTech Shop - một cửa hàng bán điện thoại và máy tính bảng trực tuyến. 
                Bạn có thể giúp khách hàng:
                - Tư vấn về sản phẩm (iPhone, Samsung, OPPO, Xiaomi, iPad, Galaxy Tab...)
                - Hướng dẫn mua hàng và thanh toán
                - Giải đáp thắc mắc về chính sách bảo hành, đổi trả
                - Hỗ trợ kỹ thuật cơ bản
                - Thông tin về khuyến mãi và mã giảm giá
                
                Hãy trả lời bằng tiếng Việt, thân thiện và hữu ích. Nếu không biết câu trả lời, hãy đề xuất liên hệ với nhân viên hỗ trợ.
                """;
            
            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", systemPrompt));
            
            // Thêm lịch sử hội thoại
            if (conversationHistory != null) {
                messages.addAll(conversationHistory);
            }
            
            // Thêm tin nhắn hiện tại
            messages.add(new Message("user", userMessage));
            
            ChatRequest request = new ChatRequest(MODEL, messages, MAX_TOKENS, 0.7);
            
            String response = sendRequest(request);
            return parseResponse(response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting ChatGPT response", e);
            return "Xin lỗi, tôi đang gặp sự cố kỹ thuật. Vui lòng thử lại sau hoặc liên hệ với nhân viên hỗ trợ.";
        }
    }
    
    private static String sendRequest(ChatRequest request) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setDoOutput(true);
        
        Gson gson = new Gson();
        String jsonInput = gson.toJson(request);
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        
        return response.toString();
    }
    
    private static String parseResponse(String jsonResponse) {
        try {
            Gson gson = new Gson();
            ChatResponse chatResponse = gson.fromJson(jsonResponse, ChatResponse.class);
            
            if (chatResponse != null && chatResponse.getChoices() != null && chatResponse.getChoices().length > 0) {
                return chatResponse.getChoices()[0].getMessage().getContent();
            } else {
                return "Xin lỗi, tôi không thể xử lý yêu cầu của bạn lúc này.";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing ChatGPT response", e);
            return "Xin lỗi, có lỗi xảy ra khi xử lý phản hồi.";
        }
    }
    
    // Phương thức để tạo context từ lịch sử chat
    public static List<Message> createConversationHistory(List<model.ChatMessage> chatHistory) {
        List<Message> messages = new ArrayList<>();
        
        if (chatHistory != null) {
            for (model.ChatMessage chat : chatHistory) {
                // Thêm tin nhắn của user
                messages.add(new Message("user", chat.getMessage()));
                
                // Thêm phản hồi của AI
                if (chat.getResponse() != null && !chat.getResponse().isEmpty()) {
                    messages.add(new Message("assistant", chat.getResponse()));
                }
            }
        }
        
        return messages;
    }
} 