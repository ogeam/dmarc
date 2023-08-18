package com.assignment.demac.repository;

import com.assignment.demac.model.DemarcRecordDao;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DemarcRepository extends CrudRepository<DemarcRecordDao, Long> {

  /**
   * repository logic to get records
   */
  @Query(
      value = "SELECT * FROM demarc_record WHERE date_from >= :dateFrom AND date_to <= :dateTo",
      nativeQuery = true
  )
  Optional<Collection<DemarcRecordDao>> getRecordBetweenStartDateAndEndDate(
      @Param("dateFrom") LocalDate startDAte, @Param("dateTo") LocalDate endDate);
}
