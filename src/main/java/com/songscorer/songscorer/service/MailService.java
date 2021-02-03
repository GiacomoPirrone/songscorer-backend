package com.songscorer.songscorer.service;

import com.songscorer.songscorer.exceptions.SymphonyzeException;
import com.songscorer.songscorer.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@AllArgsConstructor
@Slf4j
class MailService {

    private final MailContentBuilder mailContentBuilder;

    @Async
    public void sendMail(NotificationEmail notificationEmail) {

        // Details for connecting to smtp server so that we can send emails from the backend
        final String username = "";
        final String password = "";
        final String host = "localhost";

        System.out.println("TLSEmail Start");

        // Set the property details
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "465");

        // Set the SSL Factory
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");


        // creating Session instance referenced to
        // Authenticator object to pass in
        // Session.getInstance argument
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    // Override the getPasswordAuthentication method
                    protected PasswordAuthentication
                    getPasswordAuthentication() {
                        return new PasswordAuthentication("username",
                                "password");
                    }
                });
        try {

            // Instantiate the message, mime message mainly used for abstraction
            Message message = new MimeMessage(session);

            /*
             * Set which email this message is coming from, in this case
             * it is coming from our verification email address
             */
            message.setFrom(new InternetAddress(""));

            /*
             * Set which email you are sending the verification email to,
             * in this case it is the newly registered user
             */
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(notificationEmail.getRecipient()));
            message.setSubject(notificationEmail.getSubject());
            message.setText(notificationEmail.getBody());

            /*
             * Prior to sending the message, we must create a transport object and connect
             * to the smtp server using the correct credentials which is the
             * mail server, SSL port, username (verify@symphonyze.com), and password
             */
            Transport transport = session.getTransport("smtps");
            transport.connect("mail.privateemail.com", 465, username, password);

            // In case we need to send to multiple addresses, we pass an array of recipients
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            log.info("Activation email sent!");
            System.out.println("Done");

        } catch (MessagingException e) {
            throw new SymphonyzeException("Exception occured when sending email to " + notificationEmail.getRecipient());
        }

    }
}