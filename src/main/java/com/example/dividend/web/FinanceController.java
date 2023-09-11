package com.example.dividend.web;

import com.example.dividend.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/finance")
public class FinanceController {
  private final FinanceService financeService;
  private final Logger logger = LoggerFactory.getLogger(FinanceController.class);
  @GetMapping("/dividend/{companyName}")
  public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
    logger.info("[get] /finance/dividend/{companyName} : {}", companyName);
    var result = financeService.getDividendByCompanyName(companyName);
    return ResponseEntity.ok(result);
  }
}
