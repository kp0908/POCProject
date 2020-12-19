package utilities;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

public class SendEmailWithAttachments {
	
	public static void sending_email()
    {
       //Create Attachment
        EmailAttachment emailAttachment=new EmailAttachment();
        emailAttachment.setPath(System.getProperty("user.dir")+"\\test-output\\emailable-report.html");
        emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
        emailAttachment.setDescription("Report Attachment");
        emailAttachment.setName("TestNG Report");
        
       //Initialize a new multi part email instance
        MultiPartEmail email=new MultiPartEmail();
        
        //Set email host
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(465);
        
        //Set email authentication username and password
        email.setAuthenticator(new DefaultAuthenticator("Your GmailID","Your Password"));
        
        //Set email host SSL to true
        email.setSSL(true);
        
        try {
            //Set From email address
            email.setFrom("testkarantestparmar2020@gmail.com");
        } catch (EmailException e) {
            e.printStackTrace();
        }
        
        //Set email Subject line
        email.setSubject("Automation Test Email");
        try {
            //Set Email Message
            email.setMsg("This is a test email from Automation Framework.\n\n\n Regards, \n Karan Parmar");
             
        } catch (EmailException e) {
            e.printStackTrace();
        }
        try {
            //Set Email To Address
            email.addTo("To MailAddress");

           
        } catch (EmailException e) {
            e.printStackTrace();
        }
        //add the attachment
        try {
        	
            email.attach(emailAttachment);
        } catch (EmailException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //Send Email
            email.send();
            System.out.println("Email Sent Successfully..!!");
            
        } catch (EmailException e) {
            e.printStackTrace();
        }

    }

}
