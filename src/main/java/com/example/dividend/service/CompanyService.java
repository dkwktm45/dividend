package com.example.dividend.service;

import com.example.dividend.exception.impl.AlreadTIckerException;
import com.example.dividend.exception.impl.GetTickerFailException;
import com.example.dividend.exception.impl.NoCompanyException;
import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
  private final Trie trie;
  private final Scraper yahooFinaceScraper;
  private final CompanyRepository companyRepository;
  private final DividendRepository dividendRepository;
  public Company save(String ticker) {
    Boolean exists = this.companyRepository.existsByTicker(ticker);
    if (exists) {
      throw new AlreadTIckerException();
    }
    return this.storeCompanyAndDividend(ticker);
  }
  public Page<CompanyEntity> getAllCompany(Pageable pageable) {
    return this.companyRepository.findAll(pageable);
  }
  public void addAutocompleteKeyword(String keyword) {
    trie.put(keyword,null);

  }
  public void deleteAutocompleteKeyword(String keyword) {
    trie.remove(keyword);
  }
  public List<String> getCompanyNamesByKeword(String keyword) {
    Pageable limit = PageRequest.of(0, 10);
    Page<CompanyEntity> companyEntityList =
        companyRepository.findByNameStartingWithIgnoreCase(keyword , limit);
    return companyEntityList.stream()
        .map(i -> i.getName())
        .collect(Collectors.toList());
  }
  private Company storeCompanyAndDividend(String ticker) {
    Company company = this.yahooFinaceScraper.scarapCompanyByTicker(ticker);
    if (ObjectUtils.isArray(company)) {
      throw new GetTickerFailException();
    }
    ScrapedResult scrapedResult = this.yahooFinaceScraper.scrap(company);
    CompanyEntity companyEntity =
        this.companyRepository.save(new CompanyEntity(company));
    List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
        .map(e -> new DividendEntity(companyEntity.getId(), e))
        .collect(Collectors.toList());
    this.dividendRepository.saveAll(dividendEntities);
    return company;
  }

  public String deleteCompany(String ticker) {
    var company = companyRepository.findByTicker(ticker)
        .orElseThrow(() -> new NoCompanyException());
    dividendRepository.deleteAllByCompanyId(company.getId());
    companyRepository.delete(company);

    deleteAutocompleteKeyword(company.getName());
    return company.getName();
  }
}
