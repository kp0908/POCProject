package parallelExecution;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SampleChartPage {

	 @Test
	public void graphValidation() {
		WebDriver driver;
		String graphText=null;
		String expectedResults="Czech Republic: 301.9";
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get("http://referencewebapp.qaautomation.net/svg.php");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		// Find the specific pie div & get the text.
//		WebElement VioletePartElm = driver
//				.findElement(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g:nth-child(1) > text"));
//		
//		WebElement lightGreenPath = driver
//				.findElement(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g:nth-child(2) > text"));
//		
//		WebElement yellowPath = driver
//				.findElement(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g:nth-child(3) > text"));
//		
//		WebElement paleYellowPath = driver
//				.findElement(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g:nth-child(4) > text"));
//		
//		WebElement redPath = driver
//				.findElement(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g:nth-child(5) > text"));
//		
//		WebElement orangePath = driver
//				.findElement(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g:nth-child(6) > text"));
//		
//		WebElement lightOrangePath = driver
//				.findElement(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g:nth-child(7) > text"));
//		
//		
//		String str = VioletePartElm.getText().toString();
//		System.out.println("********** " + str);
//		String lightGreen = lightGreenPath.getText().toString();
//		System.out.println("********** " + lightGreen);
//		String yellow = yellowPath.getText().toString();
//		System.out.println("********** " + yellow);
//		String paleYellow = paleYellowPath.getText().toString();
//		System.out.println("********** " + paleYellow);
//		String red = redPath.getText().toString();
//		System.out.println("********** " + red);
//		String orange = orangePath.getText().toString();
//		System.out.println("********** " + orange);
//		String lightOrange = lightOrangePath.getText().toString();
//		System.out.println("********** " + lightOrange);
		
		//Second Approach with storing in list and iterating using For Loop
		 List<WebElement> element=driver.findElements(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g"));
		 System.out.println("To get the size of the webelement available: "+element.size());
		 for(int i=1;i<=element.size();i++)
		 {
			 WebElement webElementPath = driver
						.findElement(By.cssSelector("#svgchart > div > svg > g:nth-child(4) > g:nth-child("+i+") > text"));
			 
			 graphText=webElementPath.getText().toString();
			// String tooltipText = webElementPath.getAttribute(arg0)
					 Actions act = new Actions(driver);
					 act.moveToElement(webElementPath).click().perform();
			 System.out.println("****Chart Value****** " + graphText);
			 if(graphText.equalsIgnoreCase(expectedResults))
			 {
				 System.out.println("Graph Validation Done Successfully");
			 }
		 }
		
		driver.close();
	}

	//@Test
	public void graphValid() {

		WebDriver driver;
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get("http://yuilibrary.com/yui/docs/charts/charts-pie.html");

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		// FIND DIFFERENT SECTIONS IN PIE CHART

		WebElement ViolettePart = driver.findElement(By.xpath("//*[contains(@id,'yui_3_17_2_1')][@fill='#66007f']"));
		WebElement GreenPart = driver.findElement(By.xpath("//*[contains(@id,'yui_3_17_2_1')][@fill='#295454']"));
		WebElement GreyPart = driver.findElement(By.xpath("//*[contains(@id,'yui_3_17_2_1')][@fill='#e8cdb7']"));
		WebElement LightViolettePart = driver
				.findElement(By.xpath("//*[contains(@id,'yui_3_17_2_1')][@fill='#996ab2']"));
		WebElement BrownPart = driver.findElement(By.xpath("//*[contains(@id,'yui_3_17_2_1_')][@fill='#a86f41']"));
		Actions act = new Actions(driver);

		// TOOLTIP OVER PIE CHART

		WebElement ToolTip = driver.findElement(By.xpath("//div[contains(@id,'_tooltip')]"));

		// CLICK EACH SECTION OF PIE CHART AND GET THE TEXT OVER IT

		
		ViolettePart.click();
		System.out.println("Violette Part:" + ToolTip.getText());
		
		GreyPart.click();
		System.out.println("Grey Part:" + ToolTip.getText());
		
		LightViolettePart.click();
		System.out.println("Light Violete Part:" + ToolTip.getText());
		
		GreenPart.click();
		System.out.println("Green Part:" + ToolTip.getText());
		
		// BrownPart.click();
		act.moveToElement(BrownPart).click().perform();
		System.out.println("Brown Part:" + ToolTip.getText());
		
		driver.quit();
	}

}
