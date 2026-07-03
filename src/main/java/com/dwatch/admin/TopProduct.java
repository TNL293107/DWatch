package com.dwatch.admin;

public class TopProduct {
    private String productName;
    private int    totalSold;
    private double revenue;

    public TopProduct() {}

    public TopProduct(String productName, int totalSold, double revenue) {
        this.productName = productName;
        this.totalSold   = totalSold;
        this.revenue     = revenue;
    }

    public String getProductName()        { return productName; }
    public void   setProductName(String v){ productName = v; }

    public int    getTotalSold()          { return totalSold; }
    public void   setTotalSold(int v)     { totalSold = v; }

    public double getRevenue()            { return revenue; }
    public void   setRevenue(double v)    { revenue = v; }
}
