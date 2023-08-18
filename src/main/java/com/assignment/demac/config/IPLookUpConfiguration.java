package com.assignment.demac.config;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import java.io.File;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class IPLookUpConfiguration {

  @Bean
  public DatabaseReader getDatabaseReader() {
    DatabaseReader reader = null;
    try {
      File ipDatabase = new ClassPathResource("GeoLite2-Country.mmdb").getFile();
      reader = new DatabaseReader.Builder(ipDatabase).withCache(new CHMCache()).build();
    } catch (IOException e) {
      System.out.printf("unable to load ip database file. Error: %s", e);
    }
    return  reader;
  }
}
