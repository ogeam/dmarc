package com.assignment.demac.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import generated.Feedback.Record.AuthResults.Spf;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpfResult {

  private String domain;
  private String result;
  private String scope;
  private String demarc;
}
