package com.assignment.demac.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
//import org.springframework.data.annotation.Id;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "demarcRecord")
public class DemarcRecordDao {
  @Id()
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public long id;
  private String fromDomain;
  private String ipAddress;
  private String server;
  private String countryName;
  private String countryIsoCode;
  private int volume;
  private String policyApplied;
  private String reporter;
  private String emailContact;
  private String demarcAlignment;
  private String spfJson;
  private String dkimJson;
  private LocalDate dateFrom;
  private LocalDate dateTo;

  @Transient
  private DkimResult dkim;
  @Transient
  private SpfResult spf;
}
