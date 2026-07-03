package com.dwatch.admin;

import java.util.Date;

public class RevenuePoint {
    private Date   date;
    private double revenue;

    public RevenuePoint() {}

    public RevenuePoint(Date date, double revenue) {
        this.date    = date;
        this.revenue = revenue;
    }

    public Date   getDate()          { return date; }
    public void   setDate(Date v)    { date = v; }

    public double getRevenue()       { return revenue; }
    public void   setRevenue(double v) { revenue = v; }
}
