-- Bảng token đặt lại mật khẩu (quên mật khẩu)
USE [DWatchDB]
GO
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'PasswordResetToken')
BEGIN
    CREATE TABLE [dbo].[PasswordResetToken](
        [Token] [nvarchar](64) NOT NULL,
        [Email] [nvarchar](200) NOT NULL,
        [Expiry] [datetime] NOT NULL,
        PRIMARY KEY CLUSTERED ([Token] ASC)
    );
END
GO
