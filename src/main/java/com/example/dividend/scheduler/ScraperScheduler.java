package com.example.dividend.scheduler;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j @EnableCaching
@RequiredArgsConstructor
public class ScraperScheduler {
  private final CompanyRepository companyRepository;
  private final Scraper yahooFinanceScraper;
  private final DividendRepository dividendRepository;
  @CacheEvict(value = CacheKey.KEY_FINANCE , allEntries = true)
  @Scheduled(cron = "${scheduler.scrap.yahoo}")
  public void yahooFinaceScheduling() {
    log.info("scraping scheduler is started");
    List<CompanyEntity> companyEntityList = companyRepository.findAll();
    for (var company : companyEntityList) {
      log.info("scraping scheduler is started -> " + company.getName());
      ScrapedResult scrapedResult =
          yahooFinanceScraper.scrap(new Company(company.getTicker(),
              company.getName()));

      scrapedResult.getDividends().stream()
          .map(e -> new DividendEntity(company.getId(), e))
          .forEach(e -> {
            boolean exists =
                this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),
                    e.getDate());
            if (!exists) {
              dividendRepository.save(e);
              log.info("insert new dividend -> {}",e.toString());
            }
          });
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
}
