package com.assignment.demac.service;


import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
//import com.sun.mail.imap.IMAPStore;
import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Part;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
//import java.util.Properties;
//import javax.mail.internet.InternetAddress;
//import org.springframework.beans.factory.annotation.Qualifier;
//import javax.mail.Address;
//import javax.mail.Flags;
//import javax.mail.Folder;
//import javax.mail.Message;
//import javax.mail.Message.RecipientType;
//import javax.mail.MessagingException;
//import javax.mail.NoSuchProviderException;
//import javax.mail.Session;
//import javax.mail.Store;
//import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final Store store;
  @Value("${email.username}")
  private String email;
  @Value("${email.token}")
  private String oauthToken;
  @Value("${feedback.download.dir}")
  private String downloadDirectory;

  private final Logger logger = LoggerFactory.getLogger(EmailService.class);

  public EmailService(Store store){
    this.store = store;
  }

  public List<String> downloadNewFeedbackFromEmails() {
    try {
      logger.info("about to connect to email inbox");
      Folder inbox = store.getFolder("INBOX");
      inbox.open(Folder.READ_WRITE);
      logger.info("Connected and open email inbox");
      int messageCount = inbox.getMessageCount();
      logger.info("gotten {} message(s) from inbox", messageCount);
      Message[] messages = inbox.getMessages(1, messageCount);
      List<String> downloadedFileNames = new ArrayList<>();
      createDownloadDirectoryIfNotCreated();
      for (Message message : messages) {
        if (!message.getFlags().contains(Flags.Flag.SEEN)) {
          String fileName = downloadAttachments(message);
          if (fileName!= null) downloadedFileNames.add(fileName);
        }
      }
      inbox.close(false);
      logger.info("downloaded {} attachment(s) from inbox", downloadedFileNames.size());
      return downloadedFileNames;
    } catch (NoSuchProviderException ex) {
      logger.error("Provider Error:", ex);
    } catch (MessagingException ex) {
      logger.error("Message Error: ", ex);
    }
    return Collections.emptyList();
  }

  private void createDownloadDirectoryIfNotCreated() {
    File dir = new File(downloadDirectory);
    if (!dir.exists())
      dir.mkdir();
  }

  private String downloadAttachments(Message message) {
    logger.info("In download attachment");
    try {
      String contentType = message.getContentType();
      if (contentType.contains("multipart")) {
        //content may contain attachment
        Multipart multiPart = (Multipart) message.getContent();
        int numberOfParts = multiPart.getCount();
        for (int count = 0; count < numberOfParts; count++) {
          MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(count);
          String disposition = part.getDisposition();
          if (Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
            //this part is an attachment
            logger.info("about to download file from email");
            Path path = Path.of(downloadDirectory, part.getFileName());
            String fileName = path.toString();
            logger.info("file name is {}", fileName);
            if (fileName.endsWith(".zip") || fileName.endsWith(".gz") || fileName.endsWith(
                ".xml")) {
              part.saveFile(path.toFile());
              logger.info("file {} downloaded", path);
              return fileName;
            }
          }
        }
      }
    } catch (MessagingException | IOException ex) {
      logger.error("Attachment Download Error: ", ex);
    }
    logger.warn("file not download");
    return null;
  }

  private String parseAddresses(Address[] address) {

    StringBuilder listOfAddress = new StringBuilder();
    if ((address == null) || (address.length < 1))
      return null;

    if (!(address[0] instanceof InternetAddress))
      return null;

    for (Address value : address) {
      InternetAddress internetAddress =
          (InternetAddress) value;
      listOfAddress.append(internetAddress.getAddress()).append(",");
    }
    return listOfAddress.toString();
  }

//  public void connectImap() throws Exception {
//
//    try {
//      IMAPStore imapStore = OAuth2Authenticator.connectToImap("imap.gmail.com",
//          993,
//          email,
//          oauthToken,
//          true);
//      System.out.println("Successfully authenticated to IMAP.\n");
//    } catch (Exception e) {
//      System.out.println("Error: " + e.toString());
//    } finally {
//      System.out.println("finish imap");
//    }
//
////    try {
////      SMTPTransport smtpTransport = OAuth2Authenticator.connectToSmtp("smtp.gmail.com",
////          587,
////          email,
////          oauthToken,
////          true);
////      System.out.println("Successfully authenticated to SMTP.");
////    } catch (Exception e) {
////      System.out.println("Error: " + e.toString());
////    } finally {
////      System.out.println("finish smtp");
////    }
//
//
//  }


  public void readFeedback(){

    try{
      //FileInputStream fis = new FileInputStream("C:\\Users\\Wahab Owolabi\\OneDrive\\Desktop\\Ayokunmi\\test\\emailsrvr.com!interswitchng.com!1691107200!1691193600!ab4b4159-19b2-4569-9a97-33e003560b41.zip");
      //ZipInputStream zipFile = new ZipInputStream(fis);
      //ZipFile zipFile = new ZipFile("C:\\Users\\Wahab Owolabi\\OneDrive\\Desktop\\Ayokunmi\\test\\emailsrvr.com!interswitchng.com!1691107200!1691193600!ab4b4159-19b2-4569-9a97-33e003560b41.zip");
      FileInputStream fis = new FileInputStream("C:\\data\\demarc_feedback\\protection.outlook.com!interswitchng.com!1691020800!1691107200.xml.gz");

//      Enumeration<? extends ZipEntry> entries = zipFile.entries();
//      //System.out.println("zip file count: " + entries.);
//      while(entries.hasMoreElements()){
//        System.out.println("found entry");
//        ZipEntry entry = entries.nextElement();
//        JAXBContext context = JAXBContext.newInstance(generated.Feedback.class);
//        InputStream stream = zipFile.getInputStream(entry);
//        generated.Feedback feedback = (generated.Feedback) context.createUnmarshaller().unmarshal(stream);
//        zipFile.close();
//        System.out.println("completed");
//      }

      GZIPInputStream gis = new GZIPInputStream(fis);
      JAXBContext context = JAXBContext.newInstance(generated.Feedback.class);
      generated.Feedback feedback = (generated.Feedback) context.createUnmarshaller().unmarshal(gis);
      System.out.println("completed");

      //File xmlFile = new File("");
      //ObjectMapper mapper = new XmlMapper();
      //Feedback feedback = mapper.readValue(zipFile, Feedback.class);
    } catch (StreamReadException e) {
      System.out.printf("Stream exception. Error: %s", e.getMessage());
    } catch (DatabindException e) {
      System.out.printf("Data binding exception. Error: %s", e.getMessage());
    } catch (IOException e) {
      System.out.printf("IO exception. Error: %s", e.getMessage());
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

}
