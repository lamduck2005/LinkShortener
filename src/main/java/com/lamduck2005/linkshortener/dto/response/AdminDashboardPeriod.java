package com.lamduck2005.linkshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardPeriod {

    /**
     * Số ngày được yêu cầu. Null nếu không truyền days (toàn bộ lịch sử).
     */
    private Integer days;

    /**
     * Thời điểm bắt đầu khoảng thống kê. Có thể null nếu thống kê toàn bộ.
     */
    private Instant from;

    /**
     * Thời điểm kết thúc khoảng thống kê (thường là "hiện tại").
     */
    private Instant to;
}


