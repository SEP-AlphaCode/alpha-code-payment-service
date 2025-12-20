package site.alphacode.alphacodepaymentservice.service;

import site.alphacode.alphacodepaymentservice.dto.response.DashboardRevenueResponse;
import site.alphacode.alphacodepaymentservice.dto.response.RevenueDto;

public interface DashboardService {
    RevenueDto getRevenue(int year, Integer month, Integer day);
    DashboardRevenueResponse getDashboardRevenue(int year, Integer month, Integer day);
}
