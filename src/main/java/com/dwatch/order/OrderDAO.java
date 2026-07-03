package com.dwatch.order;

import com.dwatch.common.DBUtil;

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
        String sqlOrderFull  = "INSERT INTO Orders (FullName, Email, Phone, Address, Note, TotalAmount, PaymentMethod, UserID, PaymentStatus, VoucherCode, DiscountAmount) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
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
                ps.setString(10, order.getVoucherCode());
                ps.setObject(11, order.getDiscountAmount());
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

            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE Orders SET Status = ? WHERE OrderID = ? AND Status IS NULL")) {
                ps.setString(1, Order.STATUS_PENDING);
                ps.setInt(2, generatedID);
                ps.executeUpdate();
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
                    try { o.setVoucherCode(rs.getString("VoucherCode")); } catch (SQLException ignored) {}
                    try { o.setDiscountAmount(rs.getObject("DiscountAmount") != null ? rs.getDouble("DiscountAmount") : null); } catch (SQLException ignored) {}
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

    /** Lấy danh sách đơn hàng theo email, có phân trang (mới nhất trước) */
    public java.util.List<Order> getOrdersByEmail(String email, int page, int pageSize) {
        java.util.List<Order> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE Email = ? ORDER BY OrderDate DESC "
                   + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        int offset = (Math.max(1, page) - 1) * pageSize;
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
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

    /** Đếm tổng số đơn hàng theo email (dùng cho phân trang) */
    public int countOrdersByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM Orders WHERE Email = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Lấy trạng thái hiện tại của đơn hàng (dùng để phát hiện thay đổi trước khi gửi email) */
    public String getStatus(int orderID) {
        String sql = "SELECT Status FROM Orders WHERE OrderID = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("Status");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Cập nhật trạng thái xử lý đơn hàng (admin) */
    public boolean updateStatus(int orderID, String newStatus) {
        String sql = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Danh sách tất cả đơn hàng, có phân trang + lọc theo trạng thái (cho admin) */
    public List<Order> getAllOrders(int page, int pageSize, String statusFilter) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Orders ");
        boolean hasFilter = statusFilter != null && !statusFilter.isEmpty();
        if (hasFilter) sql.append("WHERE Status = ? ");
        sql.append("ORDER BY OrderDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        int offset = (Math.max(1, page) - 1) * pageSize;
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasFilter) ps.setString(idx++, statusFilter);
            ps.setInt(idx++, offset);
            ps.setInt(idx, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setOrderID(rs.getInt("OrderID"));
                    o.setFullName(rs.getString("FullName"));
                    o.setEmail(rs.getString("Email"));
                    o.setPhone(rs.getString("Phone"));
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

    /** Đếm tổng số đơn hàng, có lọc theo trạng thái (cho admin, dùng cho phân trang) */
    public int countAllOrders(String statusFilter) {
        boolean hasFilter = statusFilter != null && !statusFilter.isEmpty();
        String sql = hasFilter
            ? "SELECT COUNT(*) FROM Orders WHERE Status = ?"
            : "SELECT COUNT(*) FROM Orders";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (hasFilter) ps.setString(1, statusFilter);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
