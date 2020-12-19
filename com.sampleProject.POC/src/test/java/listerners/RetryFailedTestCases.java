package listerners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utilities.JiraServiceProvider;
import utilities.SendEmailWithAttachments;

public class RetryFailedTestCases  implements IRetryAnalyzer {

	String issueSummary;
	String issueDiscription;
	private int retryCnt = 0;
	// You could mentioned maxRetryCnt (Maximiun Retry Count) as per your requirement. Here I took 2, If any failed testcases then it runs two times
	private int maxRetryCnt =2;

	/**
	 * This method will be called everytime a test fails. It will return TRUE if a test fails and need to be retried, else it returns FALSE
	 */
	public boolean retry(ITestResult result) {

		if (!result.isSuccess()) {
			if (retryCnt < maxRetryCnt) {
				retryCnt++;
				result.setStatus(ITestResult.FAILURE);
				return true;
			} else {
				result.setStatus(ITestResult.FAILURE);
				System.out.println("Executed the failed TestCases");
				//SendEmailWithAttachments.sending_email();
				JiraServiceProvider jirsSP = new JiraServiceProvider();
				issueSummary = result.getMethod().getConstructorOrMethod().getMethod().getName()+ ": Failed due to Assertion/Exception.";
				issueDiscription = result.getThrowable().getMessage();
				//Below method is responsible to create bug in Jira.
				jirsSP.createJiraWusingRest(issueSummary, issueDiscription,"5f81401958899e0070c3bedb");
				//Below method responsible to send email.
				SendEmailWithAttachments.sending_email();
			}
		} else {
			result.setStatus(ITestResult.SUCCESS);
		}
		return false;
	}
}
