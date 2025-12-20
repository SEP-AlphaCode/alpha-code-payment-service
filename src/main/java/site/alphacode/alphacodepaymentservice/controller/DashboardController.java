package site.alphacode.alphacodepaymentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.alphacode.alphacodepaymentservice.dto.response.Revenue;
import site.alphacode.alphacodepaymentservice.service.DashboardService;
import site.alphacode.alphacodepaymentservice.service.implement.DashboardServiceImpl;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/revenue")
    public Revenue getRevenue(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return dashboardService.getRevenueByMonth(month, year);
    }
}