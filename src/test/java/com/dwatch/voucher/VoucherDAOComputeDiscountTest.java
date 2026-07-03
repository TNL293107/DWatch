package com.dwatch.voucher;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure-logic tests for VoucherDAO.computeDiscount — does not touch the
 * database (no DBUtil.getConnection() call in that code path).
 */
class VoucherDAOComputeDiscountTest {

    private final VoucherDAO voucherDAO = new VoucherDAO();

    private Voucher percentVoucher(double percent, Double minOrder) {
        Voucher v = new Voucher();
        v.setDiscountType(Voucher.TYPE_PERCENT);
        v.setDiscountValue(percent);
        v.setMinOrderAmount(minOrder);
        return v;
    }

    private Voucher fixedVoucher(double amount, Double minOrder) {
        Voucher v = new Voucher();
        v.setDiscountType(Voucher.TYPE_FIXED);
        v.setDiscountValue(amount);
        v.setMinOrderAmount(minOrder);
        return v;
    }

    @Test
    void percentDiscount_appliedToSubtotal() {
        Voucher v = percentVoucher(10, null);
        assertThat(voucherDAO.computeDiscount(v, 1_000_000)).isEqualTo(100_000);
    }

    @Test
    void fixedDiscount_returnsFlatAmount() {
        Voucher v = fixedVoucher(50_000, null);
        assertThat(voucherDAO.computeDiscount(v, 1_000_000)).isEqualTo(50_000);
    }

    @Test
    void fixedDiscount_neverExceedsSubtotal() {
        Voucher v = fixedVoucher(500_000, null);
        assertThat(voucherDAO.computeDiscount(v, 100_000)).isEqualTo(100_000);
    }

    @Test
    void percentDiscount_neverExceedsSubtotal() {
        Voucher v = percentVoucher(100, null);
        assertThat(voucherDAO.computeDiscount(v, 200_000)).isEqualTo(200_000);
    }

    @Test
    void belowMinOrderAmount_returnsZero() {
        Voucher v = percentVoucher(10, 500_000.0);
        assertThat(voucherDAO.computeDiscount(v, 300_000)).isEqualTo(0);
    }

    @Test
    void atOrAboveMinOrderAmount_appliesDiscount() {
        Voucher v = percentVoucher(10, 500_000.0);
        assertThat(voucherDAO.computeDiscount(v, 500_000)).isEqualTo(50_000);
    }

    @Test
    void nullVoucher_returnsZero() {
        assertThat(voucherDAO.computeDiscount(null, 100_000)).isEqualTo(0);
    }

    @Test
    void zeroOrNegativeSubtotal_returnsZero() {
        Voucher v = percentVoucher(10, null);
        assertThat(voucherDAO.computeDiscount(v, 0)).isEqualTo(0);
        assertThat(voucherDAO.computeDiscount(v, -100)).isEqualTo(0);
    }
}
