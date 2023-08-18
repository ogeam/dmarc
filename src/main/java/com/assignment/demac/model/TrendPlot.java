package com.assignment.demac.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrendPlot {
  private String date;
  private long failDmarcAlignment;
  //private long alignedDmarcAlignment;
  private int volume;

}
