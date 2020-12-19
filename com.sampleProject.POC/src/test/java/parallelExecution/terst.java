package parallelExecution;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class terst {
	
	@Test
	public void attachedScreenshotToJira() {
		long StartTime = System.currentTimeMillis();
		RestAssured.baseURI="https://testdemopro.atlassian.net";
		Response res = RestAssured.given().relaxedHTTPSValidation().auth().preemptive().basic("kp123@yopmail.com", "2fyecDcgl3v28Yve91GUF145")
				.header("Content-Type", "multipart/form-data")
				.header("X-Atlassian-Token", "no-check")
				.multiPart(new File("F:\\ProjectInfo\\com.sampleProject.POC\\CK-6.png"))
				.when()
				.post("/rest/api/3/issue/TPD-58/attachments");
		long EndTime = System.currentTimeMillis();
		long Duration_sec=TimeUnit.MILLISECONDS.toSeconds(EndTime-StartTime);
		System.out.println("Total elementAndValueVerification responsd time is: "+ Duration_sec +" Sec"); 
		int ad = res.getStatusCode();
		System.out.println("STATUS Code Is " + ad);
		if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {
			System.out.println("*** SCREENSHOT ATTCHED SUCCESSFULLY ***");
		} else {
			System.out.println("=== COULD NOT ATTCHED SCREENSHOT ===");
		}
	}

}
