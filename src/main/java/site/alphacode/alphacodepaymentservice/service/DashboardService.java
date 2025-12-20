package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.Revenue;

public interface DashboardService {
    public Revenue getRevenueByMonth(int month, int year);
}
