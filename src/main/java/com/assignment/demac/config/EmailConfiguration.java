package com.assignment.demac.config;

import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;
import java.util.Properties;
//import javax.mail.Authenticator;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Store;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfiguration {
  @Value("${email.protocol}")
  private String protocol;

  @Value("${email.host}")
  private String host;

  @Value("${email.port}")
  private String port;

  @Value("${email.username}")
  private String userName;
  @Value("${email.password}")
  private String password;

  private final String oaut2Password = "suajyixnlrwqmpgv";

  @Bean("EmailProperty")
  public Store getServerProperties() throws MessagingException {
    Properties properties = new Properties();

//    properties.put("mail.imap.ssl.enable", "true"); // required for Gmail
//    properties.put("mail.imap.sasl.enable", "true");
//    properties.put("mail.imap.sasl.mechanisms", "XOAUTH2");
//    properties.put("mail.imap.auth.login.disable", "true");
//    properties.put("mail.imap.auth.plain.disable", "true");
    //properties.put("key","AIzaSyAI6fW6Czv0T9hb7f6vn4lV-QYPSFpihzs");



    properties.put("mail.store.protocol", protocol);
    properties.put(String.format("mail.%s.host", protocol), host);
    properties.put(String.format("mail.%s.port", protocol), port);
    properties.setProperty(
        String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
    properties.setProperty(String.format("mail.%s.ssl.enable",protocol),
        String.valueOf(Boolean.valueOf(true)));
    properties.setProperty(
        String.format("mail.%s.socketFactory.fallback", protocol),
        String.valueOf(Boolean.valueOf(false)));
    properties.setProperty(
        String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));

    Session session = Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName,oaut2Password);
      }
    });

    Store store = session.getStore(protocol);

    store.connect();
    return store;
  }
}
