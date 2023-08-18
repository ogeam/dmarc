package com.assignment.demac.service;

import com.assignment.demac.utils.FileProcessorUtil;
import java.io.File;
import java.util.List;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailPullingScheduler {

  @Autowired
  private EmailService emailService;
  @Autowired
  private FeedbackProcessorService feedbackProcessorService;

  private final Logger logger = LoggerFactory.getLogger(FeedbackProcessorService.class);

  @Scheduled(fixedDelay = 50000, initialDelay = 20000)
  public void processEmail() {
    try {
      logger.info("about to start pulling new feedback from email");
      List<String> downloadedFileNames = emailService.downloadNewFeedbackFromEmails();
      for (String fileName : downloadedFileNames) {
        generated.Feedback feedback = FileProcessorUtil.readDownloadedFile(fileName);
        if (feedback!=null) {
          var saveResponse = feedbackProcessorService.SaveFeedback(feedback);
          if (saveResponse) {
            deleteFile(fileName);
            logger.info("file {} processed successfully", fileName);
          }
        }else {
          logger.warn("file: {} has a null feedback", fileName);
        }


      }
    } catch (Exception e) {
      logger.error("error occurred while trying to check for new feedback");
    }
  }

  private void deleteFile(String fileName) {
    try {
      File file = new File(fileName);
      if (file.exists() && file.delete()){
        logger.info("file {} processed and deleted", fileName);
      }
    }catch (Exception ex){
      logger.error("error deleting file:" + fileName,ex);
    }
  }
}
