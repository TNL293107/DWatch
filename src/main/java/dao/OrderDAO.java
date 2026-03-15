package dao;

import model.Order;
import model.OrderDetail;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    /**
     * Save full order (header + details) in one transaction.
     * Tries full schema (PaymentMethod, UserID, PaymentStatus) first; on failure falls back to minimal columns for old DB.
     * Returns the generated OrderID or -1 on failure.
     */
    public int saveOrder(Order order) {
        String sqlOrderFull  = "INSERT INTO Orders (FullName, Email, Phone, Address, Note, TotalAmount, PaymentMethod, UserID, PaymentStatus) VALUES (?,?,?,?,?,?,?,?,?)";
        String sqlOrderNoPay = "INSERT INTO Orders (FullName, Email, Phone, Address, Note, TotalAmount, PaymentMethod, UserID) VALUES (?,?,?,?,?,?,?,?)";
        String sqlOrderMin   = "INSERT INTO Orders (FullName, Email, Phone, Address, Note, TotalAmount) VALUES (?,?,?,?,?,?)";
        String sqlDetail     = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity, UnitPrice) VALUES (?,?,?,?)";
        try (Connection cn = DBUtil.getConnection()) {
            cn.setAutoCommit(false);
            int generatedID = -1;

            try (PreparedStatement ps = cn.prepareStatement(sqlOrderFull, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, order.getFullName());
                ps.setString(2, order.getEmail());
                ps.setString(3, order.getPhone());
                ps.setString(4, order.getAddress());
                ps.setString(5, order.getNote());
                ps.setDouble(6, order.getTotalAmount());
                ps.setString(7, order.getPaymentMethod() != null ? order.getPaymentMethod() : Order.PAYMENT_COD);
                ps.setObject(8, order.getUserID());
                ps.setString(9, order.getPaymentStatus() != null ? order.getPaymentStatus() : Order.PAYMENT_STATUS_UNPAID);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) generatedID = keys.getInt(1); }
            } catch (SQLException e1) {
                cn.rollback();
                cn.setAutoCommit(false);
                try (PreparedStatement ps = cn.prepareStatement(sqlOrderNoPay, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, order.getFullName());
                    ps.setString(2, order.getEmail());
                    ps.setString(3, order.getPhone());
                    ps.setString(4, order.getAddress());
                    ps.setString(5, order.getNote());
                    ps.setDouble(6, order.getTotalAmount());
                    ps.setString(7, order.getPaymentMethod() != null ? order.getPaymentMethod() : Order.PAYMENT_COD);
                    ps.setObject(8, order.getUserID());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) generatedID = keys.getInt(1); }
                } catch (SQLException e2) {
                    cn.rollback();
                    cn.setAutoCommit(false);
                    try (PreparedStatement ps = cn.prepareStatement(sqlOrderMin, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, order.getFullName());
                        ps.setString(2, order.getEmail());
                        ps.setString(3, order.getPhone());
                        ps.setString(4, order.getAddress());
                        ps.setString(5, order.getNote());
                        ps.setDouble(6, order.getTotalAmount());
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) generatedID = keys.getInt(1); }
                    } catch (SQLException e3) { e3.printStackTrace(); }
                }
            }

            if (generatedID == -1) { cn.rollback(); return -1; }

            try (PreparedStatement ps = cn.prepareStatement(sqlDetail)) {
                for (OrderDetail d : order.getDetails()) {
                    ps.setInt(1, generatedID);
                    ps.setInt(2, d.getProductID());
                    ps.setInt(3, d.getQuantity());
                    ps.setDouble(4, d.getUnitPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            cn.commit();
            return generatedID;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Get order header by ID */
    public Order getOrderByID(int orderID) {
        String sql = "SELECT * FROM Orders WHERE OrderID = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = new Order();
                    o.setOrderID(rs.getInt("OrderID"));
                    o.setFullName(rs.getString("FullName"));
                    o.setEmail(rs.getString("Email"));
                    o.setPhone(rs.getString("Phone"));
                    o.setAddress(rs.getString("Address"));
                    o.setNote(rs.getString("Note"));
                    o.setTotalAmount(rs.getDouble("TotalAmount"));
                    o.setOrderDate(rs.getDate("OrderDate"));
                    o.setStatus(rs.getString("Status"));
                    try { o.setPaymentMethod(rs.getString("PaymentMethod")); } catch (SQLException ignored) {}
                    try { o.setPaymentStatus(rs.getString("PaymentStatus")); } catch (SQLException ignored) {}
                    try { o.setUserID(rs.getObject("UserID") != null ? rs.getInt("UserID") : null); } catch (SQLException ignored) {}
                    return o;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Get order details (with product names) for a given order */
    public List<OrderDetail> getOrderDetails(int orderID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.*, p.ProductName FROM OrderDetail od "
                   + "JOIN Product p ON od.ProductID = p.ProductID "
                   + "WHERE od.OrderID = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail d = new OrderDetail();
                    d.setOrderDetailID(rs.getInt("OrderDetailID"));
                    d.setOrderID(rs.getInt("OrderID"));
                    d.setProductID(rs.getInt("ProductID"));
                    d.setQuantity(rs.getInt("Quantity"));
                    d.setUnitPrice(rs.getDouble("UnitPrice"));
                    d.setProductName(rs.getString("ProductName"));
                    list.add(d);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Lấy danh sách đơn hàng theo email (cho lịch sử đơn) */
    public java.util.List<Order> getOrdersByEmail(String email) {
        java.util.List<Order> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE Email = ? ORDER BY OrderDate DESC";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderID(rs.getInt("OrderID"));
                    o.setFullName(rs.getString("FullName"));
                    o.setEmail(rs.getString("Email"));
                    o.setPhone(rs.getString("Phone"));
                    o.setAddress(rs.getString("Address"));
                    o.setTotalAmount(rs.getDouble("TotalAmount"));
                    o.setOrderDate(rs.getDate("OrderDate"));
                    o.setStatus(rs.getString("Status"));
                    try { o.setPaymentMethod(rs.getString("PaymentMethod")); } catch (SQLException ignored) {}
                    try { o.setPaymentStatus(rs.getString("PaymentStatus")); } catch (SQLException ignored) {}
                    list.add(o);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}