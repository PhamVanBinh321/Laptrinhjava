package com.example.service;

import java.math.BigDecimal;

public record SalaryDTO(
        Integer stylistId,
        String  fullName,
        String  level,
        String  avatar,
        BigDecimal baseSalary,
        Double   commissionPercent,
        BigDecimal revenue,
        BigDecimal commissionEarned,
        BigDecimal totalIncome
) {}
