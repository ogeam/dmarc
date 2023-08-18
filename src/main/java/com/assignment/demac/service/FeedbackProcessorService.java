package com.assignment.demac.service;

import com.assignment.demac.model.DemarcRecordDao;
import com.assignment.demac.model.DkimResult;
import com.assignment.demac.model.SpfResult;
import com.assignment.demac.repository.DemarcRepository;
import com.assignment.demac.utils.DkimUtil;
import com.assignment.demac.utils.FileProcessorUtil;
import com.assignment.demac.utils.SpfUtil;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Country;
import generated.Feedback;
import generated.Feedback.Record;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeedbackProcessorService {


  @Value("${feedback.download.dir}")
  private String downloadDirectory;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final DemarcRepository repo;
  private final DatabaseReader reader;

  public FeedbackProcessorService(DemarcRepository repo, DatabaseReader reader) {
    this.repo = repo;
    this.reader = reader;
  }

  public List<Feedback> processDownloadedFeedbackFiles(){
    List<generated.Feedback> feedbacks = new ArrayList<>();
    File dir = new File(downloadDirectory);
    if (dir.exists()){
      File[] files = dir.listFiles();
      if (files!=null){
        for (File file: files) {
          Feedback feedback = FileProcessorUtil.readDownloadedFile(file.getPath());
          if (feedback!=null) feedbacks.add(feedback);
        }
      }
    }
    return feedbacks;
  }

  public boolean SaveFeedback(Feedback feedback) {
    try {
      logger.info("feedback {}", feedback.getReportMetadata().getReportId());
      for (Record record : feedback.getRecord()) {
        DemarcRecordDao demarcRecord = buildDemarcRecord(record);
        demarcRecord.setReporter(feedback.getReportMetadata().getOrgName());
        demarcRecord.setEmailContact(feedback.getReportMetadata().getEmail());
        long startDate = feedback.getReportMetadata().getDateRange().getBegin();
        long endDate = feedback.getReportMetadata().getDateRange().getEnd();
        demarcRecord.setDateFrom(
            LocalDate.ofInstant(Instant.ofEpochSecond(startDate), ZoneId.systemDefault()));
        demarcRecord.setDateTo(
            LocalDate.ofInstant(Instant.ofEpochSecond(endDate), ZoneId.systemDefault()));

        enrichUsingIP(demarcRecord);

        var spfResult = SpfUtil.from(record.getAuthResults().getSpf());
        var dkimResult = DkimUtil.from(record.getAuthResults().getDkim());

        demarcRecord.setSpfJson(SpfUtil.toJson(spfResult));
        demarcRecord.setDkimJson(DkimUtil.toJson(dkimResult));
        demarcRecord.setDemarcAlignment(getDemarcAlignment(spfResult, dkimResult));

        DemarcRecordDao dao = saveRecordToDb(demarcRecord);
        if (dao.getId() != 0) {
          logger.info("record with ip {} saved.", demarcRecord.getIpAddress());
          return true;
        } else {
          logger.info("record with ip {} failed to save.", demarcRecord.getIpAddress());
        }
      }
    }catch (Exception ex){
      logger.error("error processing feedback ",ex);
    }
    return false;
  }

  private String getDemarcAlignment(SpfResult spfResult, DkimResult dkimResult) {
    if ((Objects.equals(spfResult.getResult(), "pass") && Objects.equals(dkimResult.getResult(),
        "pass")) || (Objects.equals(spfResult.getResult(), "none") && Objects.equals(
        dkimResult.getResult(),
        "pass")) || (Objects.equals(spfResult.getResult(), "pass") && Objects.equals(
        dkimResult.getResult(),
        "none")))
      return "aligned";
    else
      return "fail";
  }

  private DemarcRecordDao saveRecordToDb(DemarcRecordDao demarcRecord) {
    try{
      return repo.save(demarcRecord);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
    return demarcRecord;
  }

  private DemarcRecordDao buildDemarcRecord(Record record) {
    return DemarcRecordDao.builder()
        .fromDomain(record.getIdentifiers().getHeaderFrom())
        .ipAddress(record.getRow().getSourceIp())
        .volume(record.getRow().getCount())
        .policyApplied(record.getRow().getPolicyEvaluated().getDisposition())
        .build();
  }

  private void enrichUsingIP(DemarcRecordDao recordDao) {

    try{
      InetAddress address  = InetAddress.getByName(recordDao.getIpAddress());

      recordDao.setServer(address.getHostName());
      System.out.printf("IP address: %s, HostName: %s %n", recordDao.getIpAddress(),address.getHostName());

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
