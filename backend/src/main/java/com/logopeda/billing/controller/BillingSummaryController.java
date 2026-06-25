package com.logopeda.billing.controller;

import com.logopeda.billing.dto.BillingSummaryResponse;
import com.logopeda.billing.service.BillingSummaryService;
import com.logopeda.billing.service.ClinicContext;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing")
public class BillingSummaryController {

    private final BillingSummaryService summaryService;
    private final ClinicContext clinicContext;

    public BillingSummaryController(BillingSummaryService summaryService, ClinicContext clinicContext) {
        this.summaryService = summaryService;
        this.clinicContext = clinicContext;
    }

    @GetMapping("/summary")
    public BillingSummaryResponse summary(
            @RequestHeader(value = "X-Clinic-Id", required = false) String clinicHeader,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        String clinicId = clinicContext.resolveClinicId(clinicHeader);
        return summaryService.buildSummary(clinicId, fromDate, toDate);
    }
}
