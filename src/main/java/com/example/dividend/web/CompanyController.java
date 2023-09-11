package com.example.dividend.web;

import com.example.dividend.model.Company;
import com.example.dividend.service.CacheService;
import com.example.dividend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/comapny")
@RequiredArgsConstructor
public class CompanyController {
  private final CompanyService companyService;
  private final CacheService cacheService;
  private final Logger logger = LoggerFactory.getLogger(CompanyController.class);
  @PostMapping
  public ResponseEntity<?> addCompany(@RequestBody Company req) {
    logger.info("[post] /comapny 요청 데이터 : {}", req.toString());

    String ticker = req.getTicker().trim();

    if (ObjectUtils.isArray(ticker)) {
      throw new RuntimeException("ticker is empty");
    }
    Company company = this.companyService.save(ticker);
    this.companyService.addAutocompleteKeyword(company.getName());
    return ResponseEntity.ok(company);
  }
  /**
   * 자동 완성 기능
   * */
  @GetMapping("/autocomplete")
  @PreAuthorize("hasRole('WRITE')")
  public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
    logger.info("[get] /comapny/autocomplete 요청 데이터 : {}", keyword);
    var result = companyService.getCompanyNamesByKeword(keyword);
    return ResponseEntity.ok(result);
  }

  /**
   * Pageable구현
   * */
  @GetMapping
  @PreAuthorize("hasRole('READ')")
  public ResponseEntity<?> searchCompany(final Pageable pageable) {
    logger.info("[get] /comapny 요청 데이터 : {}", pageable.toString());
    return ResponseEntity.ok(this.companyService.getAllCompany(pageable));
  }
  @PreAuthorize("hasRole('WRITE')")
  @DeleteMapping("/{ticker}")
  public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
    logger.info("[delete] /comapny/{ticker} 요청 데이터 : {}", ticker);
    String companyName = companyService.deleteCompany(ticker);
    cacheService.clearFinanceCache(companyName);
    return ResponseEntity.ok(companyName);
  }


}
