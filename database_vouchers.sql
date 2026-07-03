-- Voucher / discount codes at checkout.
USE [DWatchDB]
GO
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Voucher')
BEGIN
    CREATE TABLE [dbo].[Voucher](
        [VoucherID]      [int] IDENTITY(1,1) NOT NULL,
        [Code]           [nvarchar](50) NOT NULL,
        [DiscountType]   [nvarchar](10) NOT NULL, -- 'percent' | 'fixed'
        [DiscountValue]  [decimal](18,2) NOT NULL,
        [MinOrderAmount] [decimal](18,2) NULL,
        [MaxUses]        [int] NULL,
        [UsedCount]      [int] NOT NULL CONSTRAINT DF_Voucher_UsedCount DEFAULT (0),
        [ExpiryDate]     [datetime] NULL,
        [IsActive]       [bit] NOT NULL CONSTRAINT DF_Voucher_IsActive DEFAULT (1),
        PRIMARY KEY CLUSTERED ([VoucherID] ASC)
    );
END
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'UQ_Voucher_Code' AND object_id = OBJECT_ID(N'[dbo].[Voucher]'))
BEGIN
    CREATE UNIQUE INDEX [UQ_Voucher_Code] ON [dbo].[Voucher] ([Code]);
END
GO

-- Additive, nullable columns on Orders so the existing multi-tier saveOrder
-- fallback (full/no-pay/minimal INSERT) keeps working on older schemas.
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND name = 'VoucherCode')
BEGIN
    ALTER TABLE [dbo].[Orders] ADD [VoucherCode] [nvarchar](50) NULL;
END
GO
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND name = 'DiscountAmount')
BEGIN
    ALTER TABLE [dbo].[Orders] ADD [DiscountAmount] [decimal](18,2) NULL;
END
GO
