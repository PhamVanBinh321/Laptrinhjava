package com.example.service.impl;

import com.example.model.StylistProfile;
import com.example.repository.BookingRepository;
import com.example.repository.StylistProfileRepository;
import com.example.service.SalaryDTO;
import com.example.service.SalaryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final BookingRepository bookingRepo;
    private final StylistProfileRepository profileRepo;

    public SalaryServiceImpl(BookingRepository bookingRepo,
                             StylistProfileRepository profileRepo) {
        this.bookingRepo = bookingRepo;
        this.profileRepo = profileRepo;
    }

    @Override
    public List<SalaryDTO> getSalariesOfMonth(YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end   = month.atEndOfMonth().atTime(23, 59, 59);

        /* ----------------------------------------------------------------
           Lấy doanh thu theo stylist (SUM(total_amount) GROUP BY stylist)
           bookingRepo.sumRevenueGroupByStylist(...) -> List<Object[]>
             – arr[0] : Integer  (stylistId)
             – arr[1] : Number   (Double | BigDecimal | Long … tuỳ DB)
           ---------------------------------------------------------------- */
        Map<Integer, BigDecimal> revenueMap = new HashMap<>();

        bookingRepo.sumRevenueGroupByStylist(start, end)
                   .forEach(arr -> {
                       Integer stylistId = (Integer) arr[0];

                       // Ép về Number trước rồi chuyển sang BigDecimal
                       Number num = (Number) arr[1];
                       BigDecimal revenue = (num == null)
                               ? BigDecimal.ZERO
                               : BigDecimal.valueOf(num.doubleValue());

                       revenueMap.put(stylistId, revenue);
                   });

        /* -------------------- Ghép dữ liệu sang DTO -------------------- */
        List<SalaryDTO> result = new ArrayList<>();

        for (StylistProfile sp : profileRepo.findAll()) {
            Integer uid         = sp.getUser().getId();
            BigDecimal salary   = BigDecimal.valueOf(sp.getSalary());          // lương cơ bản
            BigDecimal revenue  = revenueMap.getOrDefault(uid, BigDecimal.ZERO);

            BigDecimal commission = revenue
                    .multiply(BigDecimal.valueOf(sp.getCommissionPercent()))
                    .divide(BigDecimal.valueOf(100));

            BigDecimal total = salary.add(commission);

            result.add(new SalaryDTO(
                    uid,
                    sp.getUser().getFullName(),
                    sp.getLevel().name(),
                    sp.getUser().getAvatar(),
                    salary,
                    sp.getCommissionPercent(),
                    revenue,
                    commission,
                    total
            ));
        }
        return result;
    }

    /* =================== Cập nhật lương & hoa hồng =================== */
    @Override
    @Transactional
    public void updateBaseSalaryAndCommission(Integer stylistId,
                                              Double newBase,
                                              Double newPercent) {
        StylistProfile sp = profileRepo.findById(stylistId)
                .orElseThrow(() -> new IllegalArgumentException("Stylist not found"));

        sp.setSalary(newBase);             // cột `salary` kiểu DECIMAL
        sp.setCommissionPercent(newPercent);
        // JPA dirty-checking sẽ tự flush khi transaction commit
    }
}
