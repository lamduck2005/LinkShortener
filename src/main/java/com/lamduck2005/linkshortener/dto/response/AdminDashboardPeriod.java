package com.lamduck2005.linkshortener.dto.response;

import java.time.Instant;

public record AdminDashboardPeriod(
        /**
         * Số ngày được yêu cầu. Null nếu không truyền days (toàn bộ lịch sử).
         */
        Integer days,

        /**
         * Thời điểm bắt đầu khoảng thống kê. Có thể null nếu thống kê toàn bộ.
         */
        Instant from,

        /**
         * Thời điểm kết thúc khoảng thống kê (thường là "hiện tại").
         */
        Instant to
) {}


