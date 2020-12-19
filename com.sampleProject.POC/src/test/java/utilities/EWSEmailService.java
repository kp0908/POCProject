package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.MessageBody;  

public class EWSEmailService {

	public static void sendeMail() {
		Properties prop = null;
		String propFileName = null;
		String projectDirectory = null;
		String fileName = null;
		String attachNewFileName = null;
		String pathToAttach = null;
		FileInputStream ip;
		ExchangeService service;

		List<String> to_EmailAddress = new ArrayList<String>();
		List<String> cc_EmailAddress = new ArrayList<String>();
		List<String> bcc_EmailAddress = new ArrayList<String>();

		String mailBody = "<p>Hello Team,</p>" 
				+ "<p>This email is from the test Automation Framework.</p>" 
				+ "<br>"
				+ "<br>" 
				+ "<i><b>Regards,</i></b><br>" 
				+ "<b>Your Signature</b><br>"
				+ "<p style=\"color:red;\"><i><b>Note:</b> This is auto generated email do not reply.</i></p>";
		Base64.Decoder decoder = Base64.getDecoder();

		try {
			prop = new Properties();
			propFileName = "mailconfiguration.properties";
			pathToAttach = "Screenshots\\CK-6.png";
			// pathToAttach="test-output\\emailable-report.html";

			projectDirectory = System.getProperty("user.dir");
			ip = new FileInputStream(projectDirectory + "\\" + propFileName);
			prop.load(ip);
		} catch (Exception e) {
			e.printStackTrace();
		}

		service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		try {
			service.setUrl(new URI(prop.getProperty("URL")));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ExchangeCredentials credentials = new WebCredentials(prop.getProperty("from_MailID"),
				new String(decoder.decode(prop.getProperty("mailPassword"))));
		service.setCredentials(credentials);
		try {
			EmailMessage msg = new EmailMessage(service);
			msg.setSubject("Test Automation Mail");
			msg.setBody(new MessageBody(mailBody));

			// Attaching file on mail
			File attachFile = new File(projectDirectory + "\\" + pathToAttach);
			FileAttachment fileAttachment = msg.getAttachments().addFileAttachment(attachFile.toString());
			fileName = fileAttachment.getName();
			attachNewFileName = fileName.replaceAll("[/<>+^:,-]", "_");
			fileAttachment.setName(attachNewFileName);

			if (prop.getProperty("to_MailID").length() > 1 && !(prop.getProperty("to_MailID").isEmpty()) == true) {
				String[] toemails = prop.getProperty("to_MailID").split(",");
				for (String tomail : toemails) {
					if (tomail != null) {
						// Add element to the list
						to_EmailAddress.add(tomail.trim());
					}
					msg.getToRecipients().add(tomail);
				}
				if (prop.getProperty("cc_MailID").length() > 1) {
					String[] ccemails = prop.getProperty("cc_MailID").split(",");
					for (String ccmail : ccemails) {
						if (ccmail != null) {
							// Add element to the list
							cc_EmailAddress.add(ccmail.trim());
						}
						msg.getCcRecipients().add(ccmail);
					}
				}
				if (prop.getProperty("bcc_MailID").length() > 1) {
					String[] bccemails = prop.getProperty("bcc_MailID").split(",");
					for (String bccmail : bccemails) {
						if (bccmail != null) {
							// Add element to the list
							bcc_EmailAddress.add(bccmail.trim());
						}
						msg.getBccRecipients().add(bccmail);
					}
				}
				//msg.save();			//Save the mail
				msg.sendAndSaveCopy();		// Sending mail
				System.out.println("Email Sent Successfully..!!");
			} else {
				System.out.println("Please provide atleast one mail recipient");
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
