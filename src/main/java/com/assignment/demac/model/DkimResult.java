package com.assignment.demac.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DkimResult {
  private String result;
  private String domain;
  private String selector;
  private String demarc;

}
