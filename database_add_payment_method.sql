-- Run this if Orders table already exists without PaymentMethod / PaymentStatus columns
USE [DWatchDB]
GO
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND name = 'PaymentMethod')
BEGIN
    ALTER TABLE [dbo].[Orders] ADD [PaymentMethod] [nvarchar](20) NULL;
END
GO
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND name = 'PaymentStatus')
BEGIN
    ALTER TABLE [dbo].[Orders] ADD [PaymentStatus] [nvarchar](50) NULL;
END
GO
