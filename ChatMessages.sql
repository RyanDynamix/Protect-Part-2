-- Tạo bảng ChatMessages để lưu trữ lịch sử chat
USE [BanHangOnlineNew]
GO

CREATE TABLE [dbo].[ChatMessages](
    [messageID] [int] IDENTITY(1,1) PRIMARY KEY NOT NULL,
    [userID] [int] NOT NULL,
    [message] [nvarchar](1000) NOT NULL,
    [response] [nvarchar](2000) NULL,
    [timestamp] [datetime] NOT NULL DEFAULT GETDATE(),
    [messageType] [nvarchar](10) NOT NULL, -- 'user' hoặc 'ai'
    [isRead] [bit] NOT NULL DEFAULT 0
);

-- Thêm foreign key constraint
ALTER TABLE [dbo].[ChatMessages]
ADD CONSTRAINT [FK_ChatMessages_Users]
    FOREIGN KEY ([userID]) REFERENCES [dbo].[Users] ([userID]);

-- Tạo index để tối ưu hiệu suất truy vấn
CREATE INDEX [IX_ChatMessages_UserID] ON [dbo].[ChatMessages] ([userID]);
CREATE INDEX [IX_ChatMessages_Timestamp] ON [dbo].[ChatMessages] ([timestamp]);
CREATE INDEX [IX_ChatMessages_IsRead] ON [dbo].[ChatMessages] ([isRead]);

-- Thêm dữ liệu mẫu (tùy chọn)
INSERT INTO [dbo].[ChatMessages] ([userID], [message], [response], [messageType], [isRead])
VALUES 
(2, 'Xin chào, tôi muốn tìm hiểu về iPhone 15', 'Xin chào! Tôi rất vui được giúp bạn tìm hiểu về iPhone 15. iPhone 15 là sản phẩm mới nhất của Apple với nhiều tính năng đột phá như chip A17 Pro, camera 48MP, và thiết kế Titanium. Bạn có muốn biết thêm về thông số kỹ thuật hoặc giá cả không?', 'user', 1),
(2, 'iPhone 15 có giá bao nhiêu?', 'iPhone 15 có nhiều phiên bản với giá khác nhau:\n- iPhone 15 128GB: 22.990.000 VNĐ\n- iPhone 15 256GB: 25.990.000 VNĐ\n- iPhone 15 Plus 128GB: 25.990.000 VNĐ\n- iPhone 15 Plus 256GB: 28.990.000 VNĐ\n\nHiện tại chúng tôi đang có chương trình khuyến mãi giảm giá lên đến 2.000.000 VNĐ. Bạn có muốn đặt hàng không?', 'user', 1),
(2, 'Có chương trình trả góp không?', 'Có! Chúng tôi có chương trình trả góp 0% lãi suất qua thẻ tín dụng với các ngân hàng đối tác như Vietcombank, Techcombank, BIDV... Bạn có thể trả góp từ 3-12 tháng tùy theo ngân hàng. Bạn muốn tôi hướng dẫn thủ tục trả góp không?', 'user', 0);

GO 