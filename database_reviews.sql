-- Product ratings & reviews.
USE [DWatchDB]
GO
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Review')
BEGIN
    CREATE TABLE [dbo].[Review](
        [ReviewID]    [int] IDENTITY(1,1) NOT NULL,
        [ProductID]   [int] NOT NULL,
        [UserID]      [int] NULL,
        [AuthorName]  [nvarchar](200) NULL,
        [Rating]      [int] NOT NULL CONSTRAINT CK_Review_Rating CHECK ([Rating] BETWEEN 1 AND 5),
        [Comment]     [nvarchar](1000) NULL,
        [CreatedDate] [datetime] NOT NULL CONSTRAINT DF_Review_CreatedDate DEFAULT (GETDATE()),
        [IsApproved]  [bit] NOT NULL CONSTRAINT DF_Review_IsApproved DEFAULT (1),
        PRIMARY KEY CLUSTERED ([ReviewID] ASC)
    );
END
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Review_ProductID' AND object_id = OBJECT_ID(N'[dbo].[Review]'))
BEGIN
    CREATE INDEX [IX_Review_ProductID] ON [dbo].[Review] ([ProductID]);
END
GO
