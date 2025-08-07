package com.example.dto;

import java.util.List;

public class FinanceReportDTO {

    private double totalRevenue;
    private double salaryCost;
    private double commissionCost;
    private double profit;

    private List<ServiceStatDTO> serviceStats;
    private List<TopCustomerDTO> topCustomers;

    /* ------------ Constructors ------------ */
    public FinanceReportDTO() {
    }

    public FinanceReportDTO(double totalRevenue,
                            double salaryCost,
                            double commissionCost,
                            double profit,
                            List<ServiceStatDTO> serviceStats,
                            List<TopCustomerDTO> topCustomers) {
        this.totalRevenue = totalRevenue;
        this.salaryCost = salaryCost;
        this.commissionCost = commissionCost;
        this.profit = profit;
        this.serviceStats = serviceStats;
        this.topCustomers = topCustomers;
    }

    /* ------------ Getters ------------ */
    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getSalaryCost() {
        return salaryCost;
    }

    public double getCommissionCost() {
        return commissionCost;
    }

    public double getProfit() {
        return profit;
    }

    public List<ServiceStatDTO> getServiceStats() {
        return serviceStats;
    }

    public List<TopCustomerDTO> getTopCustomers() {
        return topCustomers;
    }

    /* ------------ Setters ------------ */
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public void setSalaryCost(double salaryCost) {
        this.salaryCost = salaryCost;
    }

    public void setCommissionCost(double commissionCost) {
        this.commissionCost = commissionCost;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public void setServiceStats(List<ServiceStatDTO> serviceStats) {
        this.serviceStats = serviceStats;
    }

    public void setTopCustomers(List<TopCustomerDTO> topCustomers) {
        this.topCustomers = topCustomers;
    }

    /* ------------ toString, equals, hashCode (optional) ------------ */
    @Override
    public String toString() {
        return "FinanceReportDTO{" +
                "totalRevenue=" + totalRevenue +
                ", salaryCost=" + salaryCost +
                ", commissionCost=" + commissionCost +
                ", profit=" + profit +
                ", serviceStats=" + serviceStats +
                ", topCustomers=" + topCustomers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinanceReportDTO)) return false;

        FinanceReportDTO that = (FinanceReportDTO) o;
        return Double.compare(that.totalRevenue, totalRevenue) == 0 &&
                Double.compare(that.salaryCost, salaryCost) == 0 &&
                Double.compare(that.commissionCost, commissionCost) == 0 &&
                Double.compare(that.profit, profit) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(totalRevenue);
        result = 31 * result + Double.hashCode(salaryCost);
        result = 31 * result + Double.hashCode(commissionCost);
        result = 31 * result + Double.hashCode(profit);
        return result;
    }
}