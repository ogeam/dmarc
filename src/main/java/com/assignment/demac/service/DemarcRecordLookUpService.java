package com.assignment.demac.service;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemarcRecordLookUpService {

  private final Logger logger = LoggerFactory.getLogger(DemarcRecordLookUpService.class);

  public String getTxtRecord(String hostName) {
    logger.info("trying to get txt record for hostname: {}", hostName);
    Hashtable<String, String> env = new Hashtable<>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
    try {
      DirContext dirContext = new InitialDirContext(env);
      Attributes attrs = dirContext.getAttributes(hostName, new String[]{"TXT"});
      Attribute attr = attrs.get("TXT");

      String txtRecord = "";

      if (attr != null) {
        logger.info("getting TXT");
        txtRecord = attr.get().toString();
        logger.info("gotten TXT for hostname: {} value: {}", hostName, txtRecord);
      }

      return txtRecord;

    } catch (NamingException e) {
      logger.error("Error getting TXT record:", e);
    }
    return null;
  }

}
