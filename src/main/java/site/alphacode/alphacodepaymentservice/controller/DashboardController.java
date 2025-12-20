package site.alphacode.alphacodepaymentservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.alphacode.alphacodepaymentservice.dto.response.DashboardRevenueResponse;
import site.alphacode.alphacodepaymentservice.dto.response.RevenueDto;
import site.alphacode.alphacodepaymentservice.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard APIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/revenue")
    public DashboardRevenueResponse getRevenue(
            @RequestParam int year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day
    ) {
        return dashboardService.getDashboardRevenue(year, month, day);
    }
}