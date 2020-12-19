
package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ReadExcel {

	/**
     * Below method is used to Create the Defect though calling JIRA API
     * It will mark the Summary, Description,Reporter,Priority and some additional checks
     * @param summary
     * @param description
     * @param reporterName
     * @param priority
     */
	public static void  createJiraUsingRest(String summary, String description, String priority,String reporterName) {
			long StartTime;
			long EndTime ;
			long Duration_sec;
		StartTime = System.currentTimeMillis();
		RestAssured.baseURI="https://testdemopro.atlassian.net";
		String data1 = "{\"fields\": {\"summary\": \"" + summary
				+ "\",\"issuetype\": {\"name\": \"Bug\"},\"project\": {\"key\": \"TPD\"},\"description\": {\"type\": \"doc\",\"version\": 1,\"content\": [{\"type\": \"paragraph\",\"content\": [{\"text\": \""
				+ description + "\",\"type\": \"text\"}]}]},\"reporter\": {\"id\": \"" + reporterName
				+ "\"},\"priority\": {\"name\": \""+priority+"\"},\"labels\": [\"TestAuto\"],\"environment\": {\"type\": \"doc\",\"version\": 1,\"content\": [{\"type\": \"paragraph\",\"content\": [{\"text\": \"UAT\",\"type\": \"text\"}]}]},\"assignee\": {\"id\": \"5f81401958899e0070c3bedb\"}}}";
		Response res = RestAssured.given().relaxedHTTPSValidation().auth().preemptive()
				.basic("Your Jira mail", "Your Jira Token")
				.header("Content-Type", "application/json")
				.body(data1).when().post("/rest/api/3/issue");
		EndTime = System.currentTimeMillis();
		Duration_sec = TimeUnit.MILLISECONDS.toSeconds(EndTime - StartTime);
		System.out.println("Total responsd time is: " + Duration_sec + " Sec");	
		if (res.statusCode() == 200 || res.statusCode() == 201) {
			System.out.println("Bug created successfully");
		} else {
			System.out.println("Error While Creating Bug "+res.statusCode());
		}
	}
	
	/**
	 * This method will be open the Excel file, open Sheetname and read the data based on the cell and row values, 
	 * the data will be read and retrieved from the Excel files.
	 */
	public static void readExcelUtilities() {
		String fileDirectory = System.getProperty("user.dir");
		// Specify the path of file
		String path = fileDirectory+"\\Files\\OverAllReport.xlsx";
		try {
			// load file
			FileInputStream fis = new FileInputStream(path);

			// Load workbook
			Workbook workbook = new XSSFWorkbook(fis);

			// Load sheet- Here we are loading second sheet
			Sheet sheet = workbook.getSheetAt(1);

			int lastRow = sheet.getLastRowNum();
			System.out.println("Last row- " + lastRow);
			for (int i = 1; i <= lastRow; i++) {
				Row row = sheet.getRow(i);
				Cell str = row.getCell(4);
				
				//converting any type of cell data to string data
				str.setCellType(Cell.CELL_TYPE_STRING);
				int lastCell = row.getLastCellNum();
				System.out.println("Last Cell- " + lastCell);

				// getRow() specify which row we want to read and getCell() specify which column
				// to read.
				// getStringCellValue() specify that we are reading String data.
				for (int j = 2; j < lastCell; j++) {
					Cell cell = row.getCell(j);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					String value = cell.getStringCellValue();

					String cellvalue = row.getCell(8).getStringCellValue().toString();
					if (value.toLowerCase().equalsIgnoreCase("fail")
							&& cellvalue.toLowerCase().equalsIgnoreCase("yes")) {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						String testCaseDes = sheet.getRow(i).getCell(3).getStringCellValue();
						String testCaseEx = sheet.getRow(i).getCell(4).getStringCellValue();
						String testCaseactual = sheet.getRow(i).getCell(5).getStringCellValue();
						String priority = sheet.getRow(i).getCell(15).getStringCellValue();
						//Will call the method to create the bug in jira with required details
						createJiraUsingRest(testCaseDes + " Failed due to Exception/Error",testCaseEx + " " + testCaseactual, priority, "5f81401958899e0070c3bedb");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException  {
		readExcelUtilities();		
	}
}
