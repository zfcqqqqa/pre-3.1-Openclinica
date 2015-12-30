/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.core;

import org.akaza.openclinica.dao.core.SQLInitServlet;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author thickerson
 */
public class EmailEngine {
    public static String getSMTPHost() {
        return SQLInitServlet.getField(SMTPHostField);
    }

    public static String getAdminEmail() {
        return SQLInitServlet.getField(AdminEmailField);
    }

    public static String SMTPHostField = "smtpHost";
    public static String AdminEmailField = "adminEmail";

    Properties props = new Properties();
    Session s = null;
    MimeMessage message = null;
    MimeMultipart mm = new MimeMultipart();
    MimeBodyPart mbp = new MimeBodyPart();

    /** Creates a new instance of emailEngine */
    public EmailEngine(String smtphost, String connectionTimeout) {
        Integer.parseInt(connectionTimeout);
        props.put("mail.smtp.host", smtphost);
        props.put("mail.smtp.connectiontimeout", connectionTimeout);
        s = Session.getDefaultInstance(props);
        message = new MimeMessage(s);
    }

    public EmailEngine(String smtphost) {
        props.put("mail.smtp.host", smtphost);
        // YW 07-26-2007 << avoid infinite timeout hanging up process.
        props.put("mail.smtp.connectiontimeout", "90000");
        // YW >>
        s = Session.getDefaultInstance(props);
        message = new MimeMessage(s);
    }

    public void process(String to, String from, String subject, String body) throws MessagingException {
        InternetAddress from2 = new InternetAddress(from);
        message.setFrom(from2);
        InternetAddress to2 = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, to2);
        message.setSubject(subject);
        // YW <<
        mbp.setContent(body, "text/plain");
        // YW >>
        mm.addBodyPart(mbp);
        message.setContent(mm);
        Transport.send(message);
    }

}
