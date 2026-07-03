package com.dwatch.admin;

import com.dwatch.common.DBUtil;
import com.dwatch.order.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregate queries for the admin dashboard. Reads across Orders,
 * OrderDetail, and Product — kept in admin since it's a cross-feature,
 * back-office concern rather than belonging to any single domain.
 */
public class StatsDAO {

    /** Tổng doanh thu (không tính đơn đã hủy) */
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(TotalAmount), 0) FROM Orders WHERE Status IS NULL OR Status <> ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, Order.STATUS_CANCELLED);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Số lượng đơn hàng theo từng trạng thái */
    public Map<String, Integer> getOrderCountByStatus() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String status : Order.orderedStatuses()) counts.put(status, 0);

        String sql = "SELECT Status, COUNT(*) AS cnt FROM Orders GROUP BY Status";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("Status");
                if (status != null) counts.put(status, rs.getInt("cnt"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return counts;
    }

    /** Sản phẩm bán chạy nhất theo số lượng, kèm doanh thu từ sản phẩm đó */
    public List<TopProduct> getTopProducts(int limit) {
        List<TopProduct> list = new ArrayList<>();
        String sql = "SELECT TOP (?) p.ProductName, SUM(od.Quantity) AS totalSold, "
                   + "SUM(od.Quantity * od.UnitPrice) AS revenue "
                   + "FROM OrderDetail od JOIN Product p ON od.ProductID = p.ProductID "
                   + "GROUP BY p.ProductName ORDER BY totalSold DESC";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TopProduct(
                        rs.getString("ProductName"),
                        rs.getInt("totalSold"),
                        rs.getDouble("revenue")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Doanh thu theo từng ngày, trong N ngày gần nhất */
    public List<RevenuePoint> getRevenueByDay(int lastNDays) {
        List<RevenuePoint> list = new ArrayList<>();
        String sql = "SELECT CAST(OrderDate AS DATE) AS OrderDay, SUM(TotalAmount) AS Revenue "
                   + "FROM Orders WHERE OrderDate >= DATEADD(day, ?, GETDATE()) "
                   + "GROUP BY CAST(OrderDate AS DATE) ORDER BY OrderDay";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, -lastNDays);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new RevenuePoint(rs.getDate("OrderDay"), rs.getDouble("Revenue")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
