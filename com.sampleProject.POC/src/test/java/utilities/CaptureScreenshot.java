package utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

public class CaptureScreenshot {
	
	
	/**
	 * Below method will capture the screenshot and add the PNG file specific folder location
	 * @param driver
	 * @param result
	 * @param filePath
	 */
    public static void takeScreenShotOnFailure( WebDriver driver,ITestResult result, String filePath) {
    	String dateTime=timestamp();
    	String dateTimeWithPNG = dateTime.replace("-", "_").replace(" ", "_").replace(":", "_") + ".png";
    	System.out.println("***** Error "+result.getName()+" test has failed *****");
    	String methodName=result.getName().toString().trim();
      	TakesScreenshot takeScreenshot=(TakesScreenshot)driver;
      	File scrFile = takeScreenshot.getScreenshotAs(OutputType.FILE); 
           try {
				FileUtils.copyFile(scrFile, new File(filePath+methodName+"_"+dateTimeWithPNG));
				System.out.println("***Placed screen shot in "+filePath+" ***");
			} catch (Exception e) {
				e.printStackTrace();
			}
   }

    /**
     * Create date and time
     * @return
     */
     
    public static String timestamp() {
        return new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date());
    }
    
}
