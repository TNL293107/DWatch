package com.dwatch.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationTest {

    @ParameterizedTest
    @CsvSource({
        "3, 3",
        "1, 1",
        "-5, 1",
        "0, 1",
    })
    void parsePage_validAndInvalidInput(String input, int expected) {
        assertThat(Pagination.parsePage(input)).isEqualTo(expected);
    }

    @Test
    void parsePage_nullOrNonNumeric_defaultsToOne() {
        assertThat(Pagination.parsePage(null)).isEqualTo(1);
        assertThat(Pagination.parsePage("abc")).isEqualTo(1);
    }

    @Test
    void totalPages_computedFromItemCountAndPageSize() {
        Pagination p = new Pagination(1, 10, 25);
        assertThat(p.getTotalPages()).isEqualTo(3);
    }

    @Test
    void totalPages_exactMultiple_noExtraPage() {
        Pagination p = new Pagination(1, 10, 20);
        assertThat(p.getTotalPages()).isEqualTo(2);
    }

    @Test
    void totalPages_zeroItems_isZero() {
        Pagination p = new Pagination(1, 10, 0);
        assertThat(p.getTotalPages()).isEqualTo(0);
    }

    @Test
    void currentPage_neverBelowOne() {
        Pagination p = new Pagination(-3, 10, 25);
        assertThat(p.getCurrentPage()).isEqualTo(1);
    }

    @Test
    void offset_computedFromCurrentPageAndSize() {
        Pagination p = new Pagination(3, 10, 100);
        assertThat(p.getOffset()).isEqualTo(20);
    }
}
