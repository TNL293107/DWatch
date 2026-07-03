package com.dwatch.order;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Order {
    public static final String PAYMENT_COD = "COD";
    public static final String PAYMENT_QR = "QR";
    /** Tình trạng thanh toán: hiển thị trong email và trang đơn hàng */
    public static final String PAYMENT_STATUS_PAID   = "Đã thanh toán";
    public static final String PAYMENT_STATUS_UNPAID = "Chưa thanh toán";

    /** Trạng thái xử lý đơn hàng (khác với tình trạng thanh toán ở trên) */
    public static final String STATUS_PENDING   = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_SHIPPED   = "shipped";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";

    private static final Map<String, String> STATUS_LABELS = new LinkedHashMap<>();
    static {
        STATUS_LABELS.put(STATUS_PENDING,   "Chờ xử lý");
        STATUS_LABELS.put(STATUS_CONFIRMED, "Đã xác nhận");
        STATUS_LABELS.put(STATUS_SHIPPED,   "Đang giao hàng");
        STATUS_LABELS.put(STATUS_DELIVERED, "Đã giao hàng");
        STATUS_LABELS.put(STATUS_CANCELLED, "Đã hủy");
    }

    /** Danh sách trạng thái hợp lệ, theo đúng thứ tự vòng đời đơn hàng (cho dropdown admin) */
    public static String[] orderedStatuses() {
        return STATUS_LABELS.keySet().toArray(new String[0]);
    }

    public static String labelFor(String statusCode) {
        if (statusCode == null) return STATUS_LABELS.get(STATUS_PENDING);
        return STATUS_LABELS.getOrDefault(statusCode, statusCode);
    }

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
    private String voucherCode;
    private Double discountAmount;
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

    public String getStatusLabel()  { return labelFor(status); }

    public String  getPaymentMethod() { return paymentMethod; }
    public void    setPaymentMethod(String v) { paymentMethod = v; }

    public String  getPaymentStatus() { return paymentStatus; }
    public void    setPaymentStatus(String v) { paymentStatus = v; }

    public Integer getUserID()       { return userID; }
    public void    setUserID(Integer v) { userID = v; }

    public String  getVoucherCode()     { return voucherCode; }
    public void    setVoucherCode(String v) { voucherCode = v; }

    public Double  getDiscountAmount()  { return discountAmount; }
    public void    setDiscountAmount(Double v) { discountAmount = v; }

    public List<OrderDetail> getDetails() { return details; }
    public void setDetails(List<OrderDetail> v) { details = v; }
}
