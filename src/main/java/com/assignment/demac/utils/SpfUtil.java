package com.assignment.demac.utils;

import com.assignment.demac.model.SpfResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import generated.Feedback.Record.AuthResults.Spf;
import java.util.Objects;

public class SpfUtil {

  public static SpfResult from(String json) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return  mapper.readValue(json, SpfResult.class);
  }

  public static SpfResult from(Spf spf) {
    if (spf == null)
      return defaultSpf();
    SpfResult spfResult = new SpfResult();
    spfResult.setDemarc(Objects.equals(spf.getResult(), "pass") ? "aligned" : "fail");
    spfResult.setResult(spf.getResult());
    spfResult.setDomain(spf.getDomain());
    spfResult.setScope(spf.getScope());
    return spfResult;
  }

  private static SpfResult defaultSpf() {
    SpfResult spfResult = new SpfResult();
    spfResult.setDemarc("fail");
    spfResult.setResult("none");
    spfResult.setDomain("none");
    spfResult.setScope("none");
    return spfResult;
  }

  public static String toJson(SpfResult spf){
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(spf);
    } catch (JsonProcessingException e) {
      System.out.println("error converting spf to json");
    }
    return null;
  }


}
