// src/main/java/com/example/service/FinanceReportService.java
package com.example.service;

import com.example.dto.FinanceReportDTO;

import java.time.YearMonth;

public interface FinanceReportService {
    FinanceReportDTO buildReport(YearMonth month);
}
