package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;

public class EWSEmailService {
	
	/**
	 *  This will read mail from top-most in the inbox is unread and later mark the mail as read.
	 *  Also it will give status update on mails(Delivery and UnDelivery)
	 * @param service
	 * @param subject			Set by the team while sending the email	 
	 * @param fetchMails  		This can customize as per requirement
	 * @param fromEmailID		Sender mail address
	 * @return					Delivery and UnDelivery mail status 
	 */
	public static String readInboxMailsForStatus(ExchangeService service,String subject, int fetchMails, String fromEmailID) {
		String message="";
		Set<String> setDeliverable = new HashSet<String>();
		Set<String> setUndeliverable = new HashSet<String>();
		//Regex to validate email id format
		String regex="[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
		String successEmails = "";
		String failedEmails = "";
		
		ItemView view = new ItemView(fetchMails);
		
		try {
			FindItemsResults<Item> findResults;
			findResults = service.findItems(WellKnownFolderName.Inbox,view);
			
			//Iterate the inbox in Mailbox
			for (Item item : findResults.getItems()) {
				item.load();
				
				// To checks if is mail is Unread mode
				if((boolean) item.getPropertyBag().getObjectFromPropertyDefinition(EmailMessageSchema.IsRead)==false) {			
				//System.out.println("Subject : " + item.getSubject().trim());
				if (item.getSubject().equalsIgnoreCase("Relayed: " + subject)) {
					//Set the property to read mail
					item.getPropertyBag().setObjectFromPropertyDefinition(EmailMessageSchema.IsRead, true);
					//Mark mail as read
					item.update(ConflictResolutionMode.AutoResolve);
					setDeliverable.add(fromEmailID);
					String emailBody = item.getBody().toString();	
					//System.out.println(emailBody);					
					Matcher match = Pattern.compile(regex).matcher(emailBody);
					while (match.find()) {
						String email = match.group();
						if (!setDeliverable.contains(email)) {
							if (!setUndeliverable.contains(email)) {
								setDeliverable.add(email);
							}
						}
					}
					//System.out.println("Relayed Email Sent Successfully To : " + setDeliverable);
					String emailIDs = "";
					setDeliverable.remove(fromEmailID);
					for (String emails : setDeliverable) {
						emailIDs += emails + ",";
					}					
						int count = emailIDs.lastIndexOf(",");
							if(count>0) {
									successEmails = emailIDs.substring(0, count);
									//System.out.println("Suucessfully Sent to mailId: "+successEmails);
								}
					

				} else if (item.getSubject().equalsIgnoreCase("Undeliverable: " + subject)) {
					//Set the property to read mail
					item.getPropertyBag().setObjectFromPropertyDefinition(EmailMessageSchema.IsRead, true);
					//Mark mail as read
					item.update(ConflictResolutionMode.AutoResolve);
					setDeliverable.add(fromEmailID);
					String emailBody = item.getBody().toString();
					//System.out.println(emailBody);					
					Matcher match = Pattern.compile(regex).matcher(emailBody);
					while (match.find()) {
						String email = match.group();
						if (!setUndeliverable.contains(email)) {
							if (!setDeliverable.contains(email)) {
								if (!email.contains("PROD.OUTLOOK.COM")) {
									setUndeliverable.add(email);
								}
							}
						}
					}
					//System.out.println("Undeliverable Email Not Sent To  : " + setUndeliverable);
					String emailIDs = "";
					for (String emails : setUndeliverable) {
						emailIDs += emails + ",";
					}
					int count = emailIDs.lastIndexOf(",");
						if(count>0) {
							failedEmails = emailIDs.substring(0, count);
							//System.out.println("Failed to sent mail "+failedEmails);
						}
				}
			}
		}
			
	} catch (Exception e) 
			{
				e.printStackTrace();
			}
		
		if(!(failedEmails.isEmpty())==true && !(successEmails.isEmpty())==true ) {
			message = "Failed To Deliver Mail To " + failedEmails + " and Successfully Delivered Mail To " + successEmails;
		}
		else if (failedEmails.isEmpty()==true) {
			message = "Successfully Delivered Mail To " + successEmails;			
		}
		else if(successEmails.isEmpty()==true) {
			message = "Failed To Deliver Mail To " + failedEmails;		
		}
		return message;
	}
	
	/**
	 * This method is to read txt file line by line to a String and then encyrpt the string in Base64 format.
	 * @return
	 */
	public static String getEncryptPassword() {
		String line=null;
		String encryptedPassword=null;
		try  
		{  
		File file=new File("password.txt");    //creates a new file instance  
		FileReader fr=new FileReader(file);   //reads the file  
		BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream 
		line=br.readLine();
		Base64.Encoder encoder = Base64.getEncoder();  
		encryptedPassword=encoder.encodeToString(line.getBytes());  //encrypt text
		br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedPassword;
	}


	/**
	 * This method will Exchange Web Services(EWS) API’s we can build client application
	 * which can send the email messages from the exchange server
	 * 
	 */
	public static void sendeMail() {
		Properties prop = null;
		String propFileName = null;
		String projectDefaultDirectory = null;
		String fileName = null;
		String attachNewFileName = null;
		
		FileInputStream input;
		ExchangeService service;
		String password=null;
		Base64.Decoder decoder;
		
		String URL=null;
		String subject=null;
		String from_MailId=null;
		String to_Mail=null;
		String cc_MailID=null;
		String bcc_MailID=null;
		String emailBody=null;
		String pathToAttach = null;
		String deliveryFlag=null;
		String attachmentFlag=null;
		String getDeliveryMessage=null;

		password=getEncryptPassword();
		decoder = Base64.getDecoder();

		try {
			prop = new Properties();
			propFileName = "mailconfiguration.properties";

			projectDefaultDirectory = System.getProperty("user.dir");
			input = new FileInputStream(projectDefaultDirectory + "/" + propFileName);
			prop.load(input); 	//loading properties file
	
			//Reading all values from the properties file
			URL=prop.getProperty("URL").trim();
			subject=prop.getProperty("Subject").trim();
			pathToAttach=prop.getProperty("pathForAttachments").trim();
			from_MailId=prop.getProperty("from_MailID").trim();
			to_Mail=prop.getProperty("to_MailID").trim();
			cc_MailID=prop.getProperty("cc_MailID").trim();
			bcc_MailID=prop.getProperty("bcc_MailID").trim();
			emailBody=prop.getProperty("mailBody").trim();
			//by default deliveryFlag is set to No
			deliveryFlag=prop.getProperty("deliveryStatus").trim();
			//by default attachmentFlag is set to No
			attachmentFlag=prop.getProperty("needAttachment").trim();
			
		} catch (Exception e) {
			e.printStackTrace();
	}

		service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		try {
			
			service.setUrl(new URI(URL));	// Set URL of the EWS
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Set credentials: username and  password
		ExchangeCredentials credentials = new WebCredentials(from_MailId,new String(decoder.decode(password)));
		service.setCredentials(credentials);
		
		try {
			// Create a new email message.
			EmailMessage msg = new EmailMessage(service);
			msg.setSubject(subject);
			
			msg.setBody(new MessageBody(emailBody));
			if(deliveryFlag.equalsIgnoreCase("Yes") && !(deliveryFlag.isEmpty()) == true)
			{
				//Set the delivery receipt requested
				setIsDeliveryReceiptRequested(true, msg);
			}
			
			
			//Attaching the attachment
			if(attachmentFlag.equalsIgnoreCase("Yes") && !(attachmentFlag.isEmpty()) == true) {
				if (pathToAttach.length() >= 1 && !pathToAttach.isEmpty() == true) {
					String[] attachments = pathToAttach.split(",");
					// Iteration for attachment
					for (String attachment : attachments) {
						// Attaching file on mail
						File attachFile = new File(projectDefaultDirectory + "/" + attachment);
						FileAttachment fileAttachment = msg.getAttachments().addFileAttachment(attachFile.toString());
						// This line of code is optional- To get the fileName
						fileName = fileAttachment.getName();
						// This line of code is optional-if we have any special character then we need
						// to fetch fileName and replace with underscore(_)
						attachNewFileName = fileName.replaceAll("[/<>+^:,-]", "_");
						fileAttachment.setName(attachNewFileName);
					}
				}
			else {
					System.out.println("No attachment to attach");
				}
		}
			Pattern pattern = Pattern.compile("^.+@.+\\..+$");

			if (pattern.matcher(to_Mail).matches() && !(to_Mail.isEmpty()) == true) {
				if (to_Mail.trim().length() >= 1) {
					String[] toemails = to_Mail.split(",");
					for (String tomail : toemails) {
						if (!tomail.trim().isEmpty() && tomail.trim() != null) {
							msg.getToRecipients().add(tomail.trim());
						}
					}
				}

				if (pattern.matcher(cc_MailID).matches() && !(cc_MailID.isEmpty()) == true) {
					if (cc_MailID.trim().length() >= 1) {
						String[] ccemails = cc_MailID.split(",");
						for (String ccmail : ccemails) {
							if (!ccmail.trim().isEmpty() && ccmail.trim() != null) {
								msg.getCcRecipients().add(ccmail);
								
							}
						}
					}
				}

				if (pattern.matcher(bcc_MailID).matches() && !(bcc_MailID.isEmpty()) == true) {
					if (bcc_MailID.trim().length() >= 1) {
						String[] bccemails = bcc_MailID.split(",");
						for (String bccmail : bccemails) {
							if (!bccmail.trim().isEmpty() && bccmail.trim() != null) {
								msg.getBccRecipients().add(bccmail);
							}
						}
					}
				}
				
				// Sending emails 
				msg.save();
				//msg.sendAndSaveCopy();
				System.out.println("Email Sent..!!");
				 
			} else {
				System.out.println("Please provide atleast one email recipient");
			}
			
			//If need to know whether mail is delivery or not. if Yes, then enable the deliveryFlag 
			//in mailconfiguration.properties file
			if(deliveryFlag.equalsIgnoreCase("Yes") && !(deliveryFlag.isEmpty()) == true) {
				try {
					//TimeUnit.MINUTES.sleep(1);
					getDeliveryMessage=readInboxMailsForStatus(service, subject, 10, from_MailId);
					System.out.println(getDeliveryMessage);
				}
				catch (Exception e) {
					System.out.println("Exception log:");
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	   * Sets to checks if is delivery receipt requested.
	   *
	   * @param value the new checks if is delivery receipt requested
	   * @throws Exception the exception
	   */

	public static void setIsDeliveryReceiptRequested(Boolean value, EmailMessage msg) {
		try {
			msg.getPropertyBag().setObjectFromPropertyDefinition(EmailMessageSchema.IsDeliveryReceiptRequested, value);
		} catch (Exception e) {
			System.out.println("Exception logs:");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		sendeMail();
	}
}
