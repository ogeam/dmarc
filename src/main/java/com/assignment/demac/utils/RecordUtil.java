package com.assignment.demac.utils;

import com.assignment.demac.model.DemarcRecordDao;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Country;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordUtil {


  private final DatabaseReader reader;

  public RecordUtil(DatabaseReader reader) {
    this.reader = reader;
  }

  public void enrichUsingIP(String ipAddress, DemarcRecordDao recordDao) {

    try{
      InetAddress address  = InetAddress.getByName(ipAddress);

      recordDao.setServer(address.getHostName());
      System.out.printf("IP address: %s, HostName: %s %n", ipAddress,address.getHostName());

      Country country = reader.country(address).getCountry();

      recordDao.setCountryName(country.getName());
      recordDao.setCountryIsoCode(country.getIsoCode());
      System.out.printf("Country : %s, Iso Code: %s", country.getName(), country.getIsoCode() );


    } catch (UnknownHostException e) {
      System.out.println(e.getMessage());
    } catch (IOException | GeoIp2Exception e) {
      System.out.println("Ip resolution error: " + e.getMessage());
    }

  }

}
