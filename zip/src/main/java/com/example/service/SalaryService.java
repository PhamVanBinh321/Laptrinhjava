package com.example.service;

import java.time.YearMonth;
import java.util.List;

public interface SalaryService {

    /**
     * Lấy danh sách thu nhập của stylist trong 1 tháng
     * @param month (YearMonth 2025-08 …)
     */
    List<SalaryDTO> getSalariesOfMonth(YearMonth month);

    void updateBaseSalaryAndCommission(Integer stylistId,
                                       Double newBase,
                                       Double newPercent);
}



// SalaryDTO record moved to its own file SalaryDTO.java