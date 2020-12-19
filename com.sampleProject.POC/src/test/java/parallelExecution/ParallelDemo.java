package parallelExecution;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import utilities.CaptureScreenshot;


public class ParallelDemo {

	WebDriver driver;
	String baseUrl="https://www.lntinfotech.com";
//	String baseUrl="https://www.wikipedia.org";
//	String searchText = "Selenium";
	static String filePath = System.getProperty("user.dir")+"\\Screenshots\\";

/**
 * Below method will initialized the Web Browser 
 * WebDriverManager class is used to avoid version conflict in chromedriver.exe and chrome browser.
 * @param browser
 */
	@Parameters("browser")
	@BeforeMethod
	public void setup(String browser) {
		
		if(browser.toLowerCase().equalsIgnoreCase("chrome"))
		{
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
		}
		else if(browser.toLowerCase().equalsIgnoreCase("firefox"))
		{
			WebDriverManager.firefoxdriver().setup();
			driver= new FirefoxDriver();
		}
		else if(browser.toLowerCase().equalsIgnoreCase("ie"))
		{
			WebDriverManager.iedriver().setup();
			driver = new InternetExplorerDriver();
		}
		else
		{
			System.out.println("Browser Not Found");
		}
	}	
	
	
	@Test
	public void testScriptLTIFailCase() {
		String expectedTitle = "LTI";
		driver.manage().window().maximize();
		driver.get(baseUrl);		
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		String actualtitle = driver.getTitle();
		Assert.assertEquals(actualtitle, expectedTitle);
	}
	
	@Test
	public void testScriptLTIPassCase() {
		String expectedTitle = "LTI - Larsen & Toubro Infotech";
		driver.manage().window().maximize();
		driver.get(baseUrl);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		String actualtitle = driver.getTitle();
		Assert.assertEquals(actualtitle, expectedTitle);
	}
	
	
	@AfterMethod
	public void tearDown(ITestResult result) {
		if(ITestResult.FAILURE==result.getStatus())
		{
		CaptureScreenshot.takeScreenShotOnFailure(driver, result, filePath);
		}
		if(driver!=null) {
		driver.quit();
		System.out.println("Driver Quit Successfully");
		}
	}

}
