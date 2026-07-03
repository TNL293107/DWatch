-- Backfill Orders.Status for granular order lifecycle tracking.
-- The Status column already exists (nvarchar(50), nullable) but was never
-- populated by the app before this change. Safe to re-run.
USE [DWatchDB]
GO
UPDATE [dbo].[Orders] SET [Status] = 'pending' WHERE [Status] IS NULL;
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Orders_Status' AND object_id = OBJECT_ID(N'[dbo].[Orders]'))
BEGIN
    CREATE INDEX [IX_Orders_Status] ON [dbo].[Orders] ([Status]);
END
GO
