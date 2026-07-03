package com.dwatch.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void labelFor_knownStatus_returnsVietnameseLabel() {
        assertThat(Order.labelFor(Order.STATUS_PENDING)).isEqualTo("Chờ xử lý");
        assertThat(Order.labelFor(Order.STATUS_DELIVERED)).isEqualTo("Đã giao hàng");
        assertThat(Order.labelFor(Order.STATUS_CANCELLED)).isEqualTo("Đã hủy");
    }

    @Test
    void labelFor_nullStatus_defaultsToPendingLabel() {
        assertThat(Order.labelFor(null)).isEqualTo("Chờ xử lý");
    }

    @Test
    void labelFor_unknownStatus_returnsRawValue() {
        assertThat(Order.labelFor("some_future_status")).isEqualTo("some_future_status");
    }

    @Test
    void orderedStatuses_containsAllFiveInLifecycleOrder() {
        assertThat(Order.orderedStatuses()).containsExactly(
            Order.STATUS_PENDING,
            Order.STATUS_CONFIRMED,
            Order.STATUS_SHIPPED,
            Order.STATUS_DELIVERED,
            Order.STATUS_CANCELLED
        );
    }

    @Test
    void getStatusLabel_reflectsInstanceStatus() {
        Order order = new Order();
        order.setStatus(Order.STATUS_SHIPPED);
        assertThat(order.getStatusLabel()).isEqualTo("Đang giao hàng");
    }
}
