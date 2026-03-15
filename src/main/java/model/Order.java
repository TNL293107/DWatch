package model;

import java.util.Date;
import java.util.List;

public class Order {
    private int    orderID;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String note;
    private double totalAmount;
    private Date   orderDate;
    private String status;
    private List<OrderDetail> details;

    public Order() {}

    // Getters & Setters
    public int    getOrderID()      { return orderID; }
    public void   setOrderID(int v) { orderID = v; }

    public String getFullName()     { return fullName; }
    public void   setFullName(String v) { fullName = v; }

    public String getEmail()        { return email; }
    public void   setEmail(String v) { email = v; }

    public String getPhone()        { return phone; }
    public void   setPhone(String v) { phone = v; }

    public String getAddress()      { return address; }
    public void   setAddress(String v) { address = v; }

    public String getNote()         { return note; }
    public void   setNote(String v) { note = v; }

    public double getTotalAmount()  { return totalAmount; }
    public void   setTotalAmount(double v) { totalAmount = v; }

    public Date   getOrderDate()    { return orderDate; }
    public void   setOrderDate(Date v) { orderDate = v; }

    public String getStatus()       { return status; }
    public void   setStatus(String v) { status = v; }

    public List<OrderDetail> getDetails() { return details; }
    public void setDetails(List<OrderDetail> v) { details = v; }
}
