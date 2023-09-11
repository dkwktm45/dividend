package com.example.dividend.persist;

import com.example.dividend.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity,
    Long> {
  Optional<List<DividendEntity>> findAllByCompanyId(Long id);

  @Transactional
  void deleteAllByCompanyId(Long id);
  boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime dateTime);
}
