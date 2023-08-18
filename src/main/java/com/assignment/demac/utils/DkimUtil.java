package com.assignment.demac.utils;

import com.assignment.demac.model.DkimResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import generated.Feedback.Record.AuthResults.Dkim;
import java.util.Objects;
import org.apache.logging.log4j.util.Strings;

public class DkimUtil {

  public static DkimResult from(String json) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(json, DkimResult.class);
  }

  public static DkimResult from(Dkim dkim) {
    if (dkim == null)
      return defaultDkim();
    DkimResult dkimResult = new DkimResult();
    dkimResult.setResult(dkim.getResult());
    dkimResult.setDomain(dkim.getDomain());
    dkimResult.setSelector(dkim.getSelector());
    dkimResult.setDemarc(Objects.equals(dkim.getResult(), "pass") ? "aligned" : "fail");
    return dkimResult;
  }

  private static DkimResult defaultDkim() {
    DkimResult dkimResult = new DkimResult();
    dkimResult.setResult("none");
    dkimResult.setDomain("none");
    dkimResult.setSelector(Strings.EMPTY);
    dkimResult.setDemarc("fail");
    return dkimResult;
  }

  public static String toJson(DkimResult dkim) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(dkim);
    } catch (JsonProcessingException e) {
      System.out.println("error occur in dkim tojson");
    }
    return null;
  }

}
