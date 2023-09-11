package com.example.dividend.service;

import com.example.dividend.model.constants.CacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CacheService {
  private final CacheManager cacheManager;

  public void clearFinanceCache(String companyName) {
    cacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
  }
}
