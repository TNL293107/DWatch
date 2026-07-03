package com.dwatch.voucher;

import com.dwatch.common.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {

    /** Tìm voucher còn hiệu lực theo mã (đang bật, chưa hết hạn, chưa dùng hết lượt). Null nếu không hợp lệ. */
    public Voucher findValidByCode(String code) {
        String sql = "SELECT * FROM Voucher WHERE Code = ? AND IsActive = 1 "
                   + "AND (ExpiryDate IS NULL OR ExpiryDate > GETDATE()) "
                   + "AND (MaxUses IS NULL OR UsedCount < MaxUses)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Tính số tiền được giảm cho một voucher trên tổng tiền đơn hàng.
     * Trả về 0 nếu chưa đạt giá trị đơn hàng tối thiểu. Số tiền giảm không
     * bao giờ vượt quá subtotal.
     */
    public double computeDiscount(Voucher voucher, double subtotal) {
        if (voucher == null || subtotal <= 0) return 0;
        if (voucher.getMinOrderAmount() != null && subtotal < voucher.getMinOrderAmount()) return 0;

        double discount;
        if (Voucher.TYPE_PERCENT.equals(voucher.getDiscountType())) {
            discount = subtotal * (voucher.getDiscountValue() / 100.0);
        } else {
            discount = voucher.getDiscountValue();
        }
        return Math.min(Math.max(discount, 0), subtotal);
    }

    /** Tăng số lượt đã dùng, chỉ khi chưa vượt MaxUses (an toàn khi có nhiều request đồng thời). */
    public boolean incrementUsage(int voucherID) {
        String sql = "UPDATE Voucher SET UsedCount = UsedCount + 1 "
                   + "WHERE VoucherID = ? AND (MaxUses IS NULL OR UsedCount < MaxUses)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, voucherID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean insertVoucher(Voucher v) {
        String sql = "INSERT INTO Voucher (Code, DiscountType, DiscountValue, MinOrderAmount, MaxUses, ExpiryDate, IsActive) "
                   + "VALUES (?,?,?,?,?,?,?)";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v.getDiscountType());
            ps.setDouble(3, v.getDiscountValue());
            ps.setObject(4, v.getMinOrderAmount());
            ps.setObject(5, v.getMaxUses());
            ps.setTimestamp(6, v.getExpiryDate() != null ? new Timestamp(v.getExpiryDate().getTime()) : null);
            ps.setBoolean(7, v.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateVoucher(Voucher v) {
        String sql = "UPDATE Voucher SET Code=?, DiscountType=?, DiscountValue=?, MinOrderAmount=?, "
                   + "MaxUses=?, ExpiryDate=?, IsActive=? WHERE VoucherID=?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getCode());
            ps.setString(2, v.getDiscountType());
            ps.setDouble(3, v.getDiscountValue());
            ps.setObject(4, v.getMinOrderAmount());
            ps.setObject(5, v.getMaxUses());
            ps.setTimestamp(6, v.getExpiryDate() != null ? new Timestamp(v.getExpiryDate().getTime()) : null);
            ps.setBoolean(7, v.isActive());
            ps.setInt(8, v.getVoucherID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deactivateVoucher(int voucherID) {
        String sql = "UPDATE Voucher SET IsActive = 0 WHERE VoucherID = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, voucherID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Voucher getVoucherByID(int voucherID) {
        String sql = "SELECT * FROM Voucher WHERE VoucherID = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, voucherID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Voucher> getAllVouchers() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM Voucher ORDER BY VoucherID DESC";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Voucher map(ResultSet rs) throws SQLException {
        Voucher v = new Voucher();
        v.setVoucherID(rs.getInt("VoucherID"));
        v.setCode(rs.getString("Code"));
        v.setDiscountType(rs.getString("DiscountType"));
        v.setDiscountValue(rs.getDouble("DiscountValue"));
        Object minOrder = rs.getObject("MinOrderAmount");
        v.setMinOrderAmount(minOrder != null ? rs.getDouble("MinOrderAmount") : null);
        Object maxUses = rs.getObject("MaxUses");
        v.setMaxUses(maxUses != null ? rs.getInt("MaxUses") : null);
        v.setUsedCount(rs.getInt("UsedCount"));
        v.setExpiryDate(rs.getTimestamp("ExpiryDate"));
        v.setActive(rs.getBoolean("IsActive"));
        return v;
    }
}
