package com.assignment.demac.service;

import com.assignment.demac.model.DemarcAlignmentChart;
import com.assignment.demac.model.DemarcRecord;
import com.assignment.demac.model.DemarcRecordDao;
import com.assignment.demac.model.DemarcReport;
import com.assignment.demac.model.DkimAlignmentChart;
import com.assignment.demac.model.SpfAlignmentChart;
import com.assignment.demac.model.TrendPlot;
import com.assignment.demac.repository.DemarcRepository;
import com.assignment.demac.utils.DkimUtil;
import com.assignment.demac.utils.SpfUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FeedbackReportService {

  private final DemarcRepository repository;
  private final Logger logger = LoggerFactory.getLogger(FeedbackReportService.class);

  public FeedbackReportService(DemarcRepository repository) {
    this.repository = repository;
  }

  public DemarcReport getReport(LocalDate dateStart, LocalDate dateEnd){
    try {
      var recordDaoOptional = repository.getRecordBetweenStartDateAndEndDate(dateStart,dateEnd);
      if (recordDaoOptional.isPresent()) {
        var records = recordDaoOptional.get();
        logger.info("{} record fetch from db", records.size());
        List<DemarcRecord> demarcRecords = new ArrayList<>();
        for (DemarcRecordDao recordDao : records) {
          DemarcRecord demarcRecord = BuildDemarcReport(recordDao);
          if (demarcRecord != null) {
            demarcRecords.add(demarcRecord);
          }
        }
        if (!demarcRecords.isEmpty()) {
          logger.info("demarc records builds. count: {}", demarcRecords.size());
          DemarcReport report = new DemarcReport();
          int spfPassCount = 0, spfFailCount = 0;
          int dkimPassCount = 0, dkimFailCount = 0;
          int dmarcPassCount = 0, dmarcFailCount = 0;

          for (DemarcRecord record : demarcRecords) {
            if (Objects.equals(record.getSpf().getDemarc(), "aligned"))
              spfPassCount++;
            else
              spfFailCount++;

            if (Objects.equals(record.getDkim().getDemarc(), "aligned"))
              dkimPassCount++;
            else
              dkimFailCount++;

            if (Objects.equals(record.getDemarcAlignment(), "aligned"))
              dmarcPassCount++;
            else
              dmarcFailCount++;
          }
          double totalRecords = demarcRecords.size();
          int spfPassCore = (int) ((spfPassCount/totalRecords) * 360);
          int spfFailCore = (int)((spfFailCount/totalRecords) * 360);
          int dkimPassScore = (int)((dkimPassCount/totalRecords) * 360);
          int dkimFailScore = (int)((dkimFailCount/totalRecords) * 360);
          int demarcPassScore = (int)((dmarcPassCount/totalRecords) * 360);
          int demarcFailScore = (int)((dmarcFailCount/totalRecords) * 360);
          logger.info("finish calculating fail and pass scores");

          report.setTrendPlots(getTrendPlots(demarcRecords));
          report.setRecords(demarcRecords);
          report.setDkimAlignmentChart(new DkimAlignmentChart(dkimFailScore,dkimPassScore));
          report.setSpfAlignmentChart(new SpfAlignmentChart(spfFailCore,spfPassCore));
          report.setDemarcAlignmentChart(new DemarcAlignmentChart(demarcFailScore,demarcPassScore));
          logger.info("finished building report");
          return report;
        }

      }
      logger.info("no record that match the date. startDate: {} endDate: {}",dateStart,dateEnd);
    }catch (Exception ex){
      logger.error("Error in getting records", ex);
    }
    return null;
  }

  private List<TrendPlot> getTrendPlots(List<DemarcRecord> dmarcRecords) {
    logger.info("about to calculate plot trends");
    List<TrendPlot> plots = new ArrayList<>();
    Map<LocalDate, List<DemarcRecord>> dateGroup = dmarcRecords.stream()
        .collect(Collectors.groupingBy(DemarcRecord::getDateFrom));

    for (var item : dateGroup.entrySet()) {
      String date = item.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      int volumeSum = item.getValue().stream().mapToInt(DemarcRecord::getVolume).sum();
      long failedDmarcAlignment = item.getValue().stream().filter(it -> Objects.equals(
          it.getDemarcAlignment(), "fail")).count();
//      long alignedDmarcAlignment = item.getValue().stream().filter(it -> Objects.equals(
//          it.getDemarcAlignment(), "aligned")).count();
      logger.info("Trend plot data. Date: {}, Volume: {}, DmarcFail: {}", date, volumeSum,
          failedDmarcAlignment);
      plots.add(new TrendPlot(date, failedDmarcAlignment, volumeSum));
    }
    logger.info("{} plot record calculated", plots.size());
    return plots;
  }

  private DemarcRecord BuildDemarcReport(DemarcRecordDao record) {
    try {
      return DemarcRecord.builder()
          .fromDomain(record.getFromDomain())
          .ipAddress(record.getIpAddress())
          .server(record.getServer())
          .countryName(record.getCountryName())
          .countryIsoCode(record.getCountryIsoCode())
          .volume(record.getVolume())
          .policyApplied(record.getPolicyApplied())
          .reporter(record.getReporter())
          .emailContact(record.getEmailContact())
          .dateFrom(record.getDateFrom())
          .dateTo(record.getDateTo())
          .spf(SpfUtil.from(record.getSpfJson()))
          .dkim(DkimUtil.from(record.getDkimJson()))
          .demarcAlignment(record.getDemarcAlignment())
          .build();
    } catch (JsonProcessingException e) {
      logger.error("Error building demarcRecord", e);
    }

    return null;
  }
}
