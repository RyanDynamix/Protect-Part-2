package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ChatMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatDAO extends DBContext {
    
    public int insertChatMessage(ChatMessage chatMessage) {
        connection = getConnection();
        String sql = """
                     INSERT INTO [dbo].[ChatMessages]
                                ([userID]
                                ,[message]
                                ,[response]
                                ,[timestamp]
                                ,[messageType]
                                ,[isRead])
                          VALUES (?,?,?,?,?,?)
                     """;
        try {
            preStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preStatement.setInt(1, chatMessage.getUserID());
            preStatement.setString(2, chatMessage.getMessage());
            preStatement.setString(3, chatMessage.getResponse());
            preStatement.setTimestamp(4, chatMessage.getTimestamp());
            preStatement.setString(5, chatMessage.getMessageType());
            preStatement.setBoolean(6, chatMessage.isRead());

            int affectedRows = preStatement.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = preStatement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ChatDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public List<ChatMessage> getChatHistoryByUserID(int userID) {
        List<ChatMessage> chatHistory = new ArrayList<>();
        connection = getConnection();
        String sql = """
                     SELECT [messageID]
                           ,[userID]
                           ,[message]
                           ,[response]
                           ,[timestamp]
                           ,[messageType]
                           ,[isRead]
                       FROM [dbo].[ChatMessages]
                       WHERE [userID] = ?
                       ORDER BY [timestamp] ASC
                     """;
        try {
            preStatement = connection.prepareStatement(sql);
            preStatement.setInt(1, userID);
            resultSet = preStatement.executeQuery();
            
            while (resultSet.next()) {
                ChatMessage chatMessage = new ChatMessage(
                    resultSet.getInt("messageID"),
                    resultSet.getInt("userID"),
                    resultSet.getString("message"),
                    resultSet.getString("response"),
                    resultSet.getTimestamp("timestamp"),
                    resultSet.getString("messageType"),
                    resultSet.getBoolean("isRead")
                );
                chatHistory.add(chatMessage);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ChatDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chatHistory;
    }
    
    public boolean updateMessageAsRead(int messageID) {
        connection = getConnection();
        String sql = """
                     UPDATE [dbo].[ChatMessages]
                     SET [isRead] = 1
                     WHERE [messageID] = ?
                     """;
        try {
            preStatement = connection.prepareStatement(sql);
            preStatement.setInt(1, messageID);
            int affectedRows = preStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ChatDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public int getUnreadMessageCount(int userID) {
        connection = getConnection();
        String sql = """
                     SELECT COUNT(*) as unreadCount
                     FROM [dbo].[ChatMessages]
                     WHERE [userID] = ? AND [isRead] = 0
                     """;
        try {
            preStatement = connection.prepareStatement(sql);
            preStatement.setInt(1, userID);
            resultSet = preStatement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("unreadCount");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ChatDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public boolean deleteChatMessage(int messageID) {
        connection = getConnection();
        String sql = """
                     DELETE FROM [dbo].[ChatMessages]
                     WHERE [messageID] = ?
                     """;
        try {
            preStatement = connection.prepareStatement(sql);
            preStatement.setInt(1, messageID);
            int affectedRows = preStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ChatDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
} 