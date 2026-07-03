package com.dwatch.voucher;

import java.util.Date;

public class Voucher {
    public static final String TYPE_PERCENT = "percent";
    public static final String TYPE_FIXED   = "fixed";

    private int    voucherID;
    private String code;
    private String discountType;
    private double discountValue;
    private Double minOrderAmount;
    private Integer maxUses;
    private int    usedCount;
    private Date   expiryDate;
    private boolean active;

    public Voucher() {}

    public int     getVoucherID()             { return voucherID; }
    public void    setVoucherID(int v)        { voucherID = v; }

    public String  getCode()                  { return code; }
    public void    setCode(String v)          { code = v; }

    public String  getDiscountType()          { return discountType; }
    public void    setDiscountType(String v)  { discountType = v; }

    public double  getDiscountValue()         { return discountValue; }
    public void    setDiscountValue(double v) { discountValue = v; }

    public Double  getMinOrderAmount()        { return minOrderAmount; }
    public void    setMinOrderAmount(Double v){ minOrderAmount = v; }

    public Integer getMaxUses()               { return maxUses; }
    public void    setMaxUses(Integer v)      { maxUses = v; }

    public int     getUsedCount()             { return usedCount; }
    public void    setUsedCount(int v)        { usedCount = v; }

    public Date    getExpiryDate()            { return expiryDate; }
    public void    setExpiryDate(Date v)      { expiryDate = v; }

    public boolean isActive()                 { return active; }
    public void    setActive(boolean v)       { active = v; }
}
