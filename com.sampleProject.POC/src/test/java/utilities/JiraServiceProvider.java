package utilities;


import java.io.File;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;


public class JiraServiceProvider {

     
     public String project;
     long StartTime;
     long EndTime ;
     long Duration_sec;
     /**
      * Below method is used to Create the Defect though calling JIRA API
      * It will mark the Summary, Description Reporter and some additional checks
      * @param summary
      * @param description
      * @param reporterName
      */
	public void  createJiraWusingRest(String summary, String description, String reporterName) {
		StartTime = System.currentTimeMillis();
		String data = "{\"fields\": {\"summary\": \"" + summary
				+ "\",\"issuetype\": {\"name\": \"Bug\"},\"project\": {\"key\": \"TPD\"},\"description\": {\"type\": \"doc\",\"version\": 1,\"content\": [{\"type\": \"paragraph\",\"content\": [{\"text\": \""
				+ description + "\",\"type\": \"text\"}]}]},\"reporter\": {\"id\": \"" + reporterName
				+ "\"},\"priority\": {\"name\": \"Low\"},\"labels\": [\"TestAuto\"],\"environment\": {\"type\": \"doc\",\"version\": 1,\"content\": [{\"type\": \"paragraph\",\"content\": [{\"text\": \"UAT\",\"type\": \"text\"}]}]},\"assignee\": {\"id\": \"5f81401958899e0070c3bedb\"}}}";
		
		RestAssured.baseURI="https://testdemopro.atlassian.net";
		int index= summary.lastIndexOf(":");
		String text=summary.substring(0, index);
		Response res = RestAssured.given().relaxedHTTPSValidation().auth().preemptive()
				.basic("Jira mailID", "Your Jira Token")
				.header("Content-Type", "application/json")
				.body(data).when().post("/rest/api/3/issue");
		EndTime = System.currentTimeMillis();
		Duration_sec = TimeUnit.MILLISECONDS.toSeconds(EndTime - StartTime);
		System.out.println("Total createJiraWusingRest response time is: " + Duration_sec + " Sec");
		JSONObject jsonObjectForKey = new JSONObject(res.asString());
		String issueURL = jsonObjectForKey.get("self").toString();
		addCommentForIssue(issueURL);
		if (res.statusCode() == 200 || res.statusCode() == 201) {
			System.out.println("Bug created successfully");
		} else {
			System.out.println("Error While Creating Bug "+res.statusCode());
		}
		attachedScreenshotToJira(issueURL,text);
	}
     
 /**
  * Below Method will add the Comment in Jira for Specific IssueID/Bug.
  * @param issueID
  */
	public void addCommentForIssue(String issueID) {
		StartTime = System.currentTimeMillis();
		String data1 = "{\"body\": {\"type\": \"doc\",\"version\": 1,\"content\": [{\"type\": \"paragraph\",\"content\": [{\"text\": \"This Bug Is Raise By The Automation.\",\"type\": \"text\"}]}]}}";
		RestAssured.given().relaxedHTTPSValidation().auth().preemptive()
				.basic("Jira mailID", "Your Jira Token")
				.header("Content-Type", "application/json")
				.body(data1).when().post(issueID + "/comment");
		EndTime = System.currentTimeMillis();
		Duration_sec = TimeUnit.MILLISECONDS.toSeconds(EndTime - StartTime);
		System.out.println("Total addCommentForIssue response time is: " + Duration_sec + " Sec");  	 
     }
	
	/**
	 * Below Method upload the screenshots in JIRA from the specific location/ failed test cases screenshots
	 * @param issueID
	 * @param screenshotName
	 */
	public void attachedScreenshotToJira(String issueID, String screenshotName) {
		
		Response res = null;
		boolean flag;
		long StartTime = System.currentTimeMillis();
		File folder = new File(System.getProperty("user.dir") + "\\Screenshots");
		File[] listOfFiles = folder.listFiles();
		for (File files : listOfFiles) {
			if (files.isFile()) {
				//System.out.println(files.getName());
				String fileName = files.getName();
				flag = fileName.startsWith(screenshotName);
				//System.out.println(flag);
				if (flag == true) {
					res = RestAssured.given().relaxedHTTPSValidation().auth().preemptive()
							.basic("Jira mailID", "Your Jira Token")
							.header("Content-Type", "multipart/form-data").header("X-Atlassian-Token", "no-check")
							.multiPart(new File(folder + "\\" + fileName)).when().post(issueID + "/attachments");
				}
			}
		}
		long EndTime = System.currentTimeMillis();
		long Duration_sec = TimeUnit.MILLISECONDS.toSeconds(EndTime - StartTime);
		System.out.println("Total attachedScreenshotToJira response time is: " + Duration_sec + " Sec");
		if (res.statusCode() == 200 || res.statusCode() == 201) {
			System.out.println("SCREENSHOT ATTCHED SUCCESSFULLY");
		} else {
			System.out.println("Error While Attaching Screenshots "+res.statusCode());
		}
	}
}
