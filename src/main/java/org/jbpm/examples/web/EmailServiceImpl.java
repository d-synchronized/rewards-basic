package org.jbpm.examples.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailServiceImpl implements EmailService {

    @Resource(mappedName = "java:jboss/mail/knowledge-helpdesk")
    private final Session emailSession;

    public EmailServiceImpl(final Session session) {
        emailSession = session;
    }

    public Boolean sentEmail(final String emailAddress, final String to, final String subject, final String content) {
        return sentEmail(emailAddress, to, subject, content, null);
    }

    public Boolean sentEmail(final String emailAddress, final String to, final String subject, final String content, final List<String> ccEmails) {
        final SimpleEmail simpleEmail = new SimpleEmail();
        simpleEmail.setAuthenticator(new DefaultAuthenticator("username", "password"));// FIXME configure the username and
                                                                                       // password of your gmail account in
                                                                                       // order to send out the emaisl
        try {
            simpleEmail.setFrom(emailAddress);
            simpleEmail.addTo(to);
            final Collection<InternetAddress> ccEmailAddresses = new ArrayList<InternetAddress>();
            for (final String ccEmailAddress : ccEmails) {
                InternetAddress internetAddress = null;
                try {
                    internetAddress = new InternetAddress("CCEMAILADDRESS");
                } catch (final AddressException addressException) {
                    System.out.println("Error occurred while sending the emails to the Cc email addresses., Error - " + addressException);
                }
                ccEmailAddresses.add(internetAddress);
            }
            simpleEmail.setCc(ccEmailAddresses);
            simpleEmail.setStartTLSEnabled(true);
            simpleEmail.setSSLOnConnect(true);
            simpleEmail.setSubject(subject);
            simpleEmail.setMsg(content);
            simpleEmail.setSmtpPort(587);
            simpleEmail.setDebug(true);
            simpleEmail.setHostName("smtp.gmail.com");
            simpleEmail.send();
        } catch (final EmailException emailException) {
            System.out.println("Error occurred while sending in email " + emailException);
        }
        return true;
    }

}
