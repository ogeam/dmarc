package com.assignment.demac.controller;

import com.assignment.demac.model.DemarcReport;
import com.assignment.demac.service.DemarcRecordLookUpService;
import com.assignment.demac.service.FeedbackReportService;
import com.ctc.wstx.util.StringUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dmarc")
public class ReportController {

  private final FeedbackReportService reportService;
  private final DemarcRecordLookUpService recordLookUpService;
  private final Logger logger = LoggerFactory.getLogger(FeedbackReportService.class);

  public ReportController(FeedbackReportService reportService,
      DemarcRecordLookUpService txtRecordLookUpService) {
    this.reportService = reportService;
    this.recordLookUpService = txtRecordLookUpService;
  }

  @GetMapping("/report")
  public ResponseEntity<DemarcReport> fetchMail(@RequestParam(name = "startDate", defaultValue = "") String startDate,
      @RequestParam(name = "endDate", defaultValue = "") String endDate){
    try {
      logger.info("report request. StartDate: {}, endDate; {}", startDate,endDate);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate fromDate = LocalDate.now();
      LocalDate toDate = LocalDate.now();
      if (!Objects.equals(startDate, "")) fromDate = LocalDate.parse(startDate, formatter);
      if (!Objects.equals(endDate, "")) toDate = LocalDate.parse(endDate, formatter);
      if (toDate.isAfter(fromDate) || toDate.isEqual(fromDate)){
        var report = reportService.getReport(fromDate, toDate);
        if (report !=null)
          return ResponseEntity.ok(report);
      }else{
        logger.info("end date is smaller than start date");
        return ResponseEntity.badRequest().build();
      }


    } catch (DateTimeParseException e) {
      logger.error("date parse error: ",e);
      return ResponseEntity.badRequest().build();
    }catch (Exception ex){
      logger.error("internal server error: ", ex);
      return ResponseEntity.internalServerError().build();
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/dnsTxtLookUp")
  public ResponseEntity<String> getTxtDnsLookup(@RequestParam(name = "dmarcHostName") String hostName){
    if (Strings.isNotBlank(hostName)){
      hostName = "_dmarc." + hostName;
      String txtRecord = recordLookUpService.getTxtRecord(hostName);
      if (txtRecord.isEmpty()) return ResponseEntity.notFound().build();
      if (Strings.isNotEmpty(txtRecord))  return ResponseEntity.ok(txtRecord);
    }
    return ResponseEntity.badRequest().build();
  }



}
