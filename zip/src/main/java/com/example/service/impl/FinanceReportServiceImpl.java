// src/main/java/com/example/service/impl/FinanceReportServiceImpl.java
package com.example.service.impl;

import com.example.dto.*;
import com.example.model.StylistProfile;
import com.example.repository.BookingRepository;
import com.example.repository.StylistProfileRepository;
import com.example.service.FinanceReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinanceReportServiceImpl implements FinanceReportService {

    private final BookingRepository bookingRepo;
    private final StylistProfileRepository profileRepo;

    public FinanceReportServiceImpl(BookingRepository bookingRepo,
                                    StylistProfileRepository profileRepo) {
        this.bookingRepo = bookingRepo;
        this.profileRepo = profileRepo;
    }

    @Override
    public FinanceReportDTO buildReport(YearMonth month) {

        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end   = month.atEndOfMonth().atTime(23, 59, 59);

        /* ===== 1. Tổng doanh thu ===== */
        double revenue = bookingRepo.sumRevenue(start, end);

        /* ===== 2. Chi phí lương + hoa hồng stylist ===== */
        // revenue của từng stylist
        var revenueMap = new java.util.HashMap<Integer, Double>();
        bookingRepo.sumRevenueGroupByStylist(start, end)
                   .forEach(arr -> revenueMap.put((Integer) arr[0],
                                                  (Double)  arr[1]));

        double salaryCost     = 0;
        double commissionCost = 0;
        for (StylistProfile sp : profileRepo.findAll()) {
            salaryCost += sp.getSalary();

            double stylistRevenue = revenueMap.getOrDefault(sp.getUser().getId(), 0d);
            commissionCost += stylistRevenue * sp.getCommissionPercent() / 100d;
        }

        double profit = revenue - salaryCost - commissionCost;

        /* ===== 3. Thống kê dịch vụ ===== */
        List<ServiceStatDTO> svcStats = new ArrayList<>();
        bookingRepo.serviceStats(start, end).forEach(arr ->
                svcStats.add(new ServiceStatDTO(
                        (String) arr[0],
                        (Long)   arr[1],
                        (Double) arr[2])));

        /* ===== 4. Top khách hàng ===== */
        List<TopCustomerDTO> topCus = new ArrayList<>();
        bookingRepo.topCustomers(start, end).forEach(arr ->
                topCus.add(new TopCustomerDTO(
                        (String) arr[0],
                        (String) arr[1],
                        (Long)   arr[2],
                        (Double) arr[3])));

        /* ===== 5. Build DTO ===== */
        return new FinanceReportDTO(
                revenue,
                salaryCost,
                commissionCost,
                profit,
                svcStats,
                topCus
        );
    }
}
