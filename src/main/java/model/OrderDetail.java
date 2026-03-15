package model;

public class OrderDetail {
    private int    orderDetailID;
    private int    orderID;
    private int    productID;
    private int    quantity;
    private double unitPrice;
    private String productName;

    public OrderDetail() {}

    public OrderDetail(int orderID, int productID, int quantity, double unitPrice) {
        this.orderID   = orderID;
        this.productID = productID;
        this.quantity  = quantity;
        this.unitPrice = unitPrice;
    }

    public int    getOrderDetailID() { return orderDetailID; }
    public void   setOrderDetailID(int v) { orderDetailID = v; }

    public int    getOrderID()    { return orderID; }
    public void   setOrderID(int v) { orderID = v; }

    public int    getProductID()  { return productID; }
    public void   setProductID(int v) { productID = v; }

    public int    getQuantity()   { return quantity; }
    public void   setQuantity(int v) { quantity = v; }

    public double getUnitPrice()  { return unitPrice; }
    public void   setUnitPrice(double v) { unitPrice = v; }

    public String getProductName() { return productName; }
    public void   setProductName(String v) { productName = v; }

    public double getSubtotal() { return unitPrice * quantity; }
}
