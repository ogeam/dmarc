package com.assignment.demac.utils;

import generated.Feedback;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileProcessorUtil {

  private static final Logger logger = LoggerFactory.getLogger(FileProcessorUtil.class);

  public static generated.Feedback readDownloadedFile(String path) {
    logger.info("about to read file {}", path);
    if (path.endsWith(".xml")) {
      return readXMLFile(path);
    } else if (path.endsWith(".zip")) {
      return readZIPFile(path);
    } else if (path.endsWith(".gz")) {
      return readGZFile(path);
    } else {
      return null;
    }
  }

  private static generated.Feedback readXMLFile(String path) {
    logger.info("reading xml file {}", path);
    try {
      File file = new File(path);
      JAXBContext context = JAXBContext.newInstance(generated.Feedback.class);
      Feedback feedback = (Feedback) context.createUnmarshaller().unmarshal(file);
      logger.info("read xml file completed {}", path);
      return feedback;
    } catch (JAXBException e) {
      logger.error("error reading xml file", e);
    }
    return null;
  }

  private static generated.Feedback readZIPFile(String path) {
    try(ZipFile zipFile = new ZipFile(path)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      if (!entries.hasMoreElements()) return null;
      ZipEntry entry = entries.nextElement();
      InputStream stream = zipFile.getInputStream(entry);
      JAXBContext jaxbContext = JAXBContext.newInstance(Feedback.class);
      Feedback feedback = (Feedback) jaxbContext.createUnmarshaller().unmarshal(stream);
      logger.info("read zip file completed {}", path);
      return feedback;
    } catch (IOException | JAXBException e) {
      logger.error("error reading zip file file", e);
    }
    return null;
  }

  private static generated.Feedback readGZFile(String path) {
    try(FileInputStream fis = new FileInputStream(path)) {
      GZIPInputStream gis = new GZIPInputStream(fis);
      JAXBContext context = JAXBContext.newInstance(generated.Feedback.class);
      generated.Feedback feedback = (generated.Feedback) context.createUnmarshaller().unmarshal(gis);
      logger.info("read gzip file completed {}", path);
      return feedback;
    } catch (IOException | JAXBException e) {
      logger.error("error reading gzip file file", e);
    }
    return null;
  }
}
