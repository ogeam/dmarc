package com.assignment.demac.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemarcReport {
  private List<DemarcRecord> records;
  private SpfAlignmentChart spfAlignmentChart;
  private DkimAlignmentChart dkimAlignmentChart;
  private DemarcAlignmentChart demarcAlignmentChart;
  private List<TrendPlot> trendPlots;

}
