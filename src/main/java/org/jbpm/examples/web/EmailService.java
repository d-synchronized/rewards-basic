package org.jbpm.examples.web;

import java.util.List;

/**
 * The Interface EmailService.
 */
public interface EmailService {

    Boolean sentEmail(String emailAddress, String to, String subject, String content);

    Boolean sentEmail(final String emailAddress, final String to, final String subject, final String content, final List<String> ccEmails);

}
