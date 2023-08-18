package com.assignment.demac.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemarcRecord {
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
  private LocalDate dateFrom;
  private LocalDate dateTo;
  private DkimResult dkim;
  private SpfResult spf;
}
