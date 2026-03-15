package model;

import java.util.Date;
import java.util.List;

public class Order {
    public static final String PAYMENT_COD = "COD";
    public static final String PAYMENT_QR = "QR";
    /** Tình trạng thanh toán: hiển thị trong email và trang đơn hàng */
    public static final String PAYMENT_STATUS_PAID   = "Đã thanh toán";
    public static final String PAYMENT_STATUS_UNPAID = "Chưa thanh toán";

    private int    orderID;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String note;
    private double totalAmount;
    private Date   orderDate;
    private String status;
    private String paymentMethod;   // COD, QR
    private String paymentStatus;   // Đã thanh toán / Chưa thanh toán
    private Integer userID;
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

    public String  getPaymentMethod() { return paymentMethod; }
    public void    setPaymentMethod(String v) { paymentMethod = v; }

    public String  getPaymentStatus() { return paymentStatus; }
    public void    setPaymentStatus(String v) { paymentStatus = v; }

    public Integer getUserID()       { return userID; }
    public void    setUserID(Integer v) { userID = v; }

    public List<OrderDetail> getDetails() { return details; }
    public void setDetails(List<OrderDetail> v) { details = v; }
}
