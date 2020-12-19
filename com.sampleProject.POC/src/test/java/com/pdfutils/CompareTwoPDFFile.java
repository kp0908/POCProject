package com.pdfutils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;

public class CompareTwoPDFFile {
	
	
	/**
	 * Below method is to comparing two PDF files.
	 * It is used to read the text from PDF file and then validate if the text is present or not.
	 * @param actualPDFFile
	 * @param expectedPDFFile
	 */
	public static void comparePDF(String actualPDFFile, String expectedPDFFile ) {
		
		String currentDirectory = System.getProperty("user.dir");
		File pdftest1= new File(currentDirectory+"\\Files\\PDFFile\\"+actualPDFFile);
		File pdftest2= new File(currentDirectory+"\\Files\\PDFFile\\"+expectedPDFFile);
		boolean flag=true;
		String message =null;
		try {
		//Creating the object for the files
		PDDocument pdf1 = PDDocument.load(pdftest1);
	    PDDocument pdf2 = PDDocument.load(pdftest2);
	    PDPageTree pdf1pages = pdf1.getDocumentCatalog().getPages();
	    //System.out.println(pdf1pages.getCount());
	    PDPageTree pdf2pages = pdf2.getDocumentCatalog().getPages();
	    //System.out.println(pdf2pages.getCount());
	    if (pdf1pages.getCount() != pdf2pages.getCount())
        {
            message = "Number of pages in the files ("+actualPDFFile+" , "+expectedPDFFile+") do not match. actualPDFFile has "+pdf1pages.getCount()+" no pages, while expectedPDFFile has "+pdf2pages.getCount()+" no of pages";
            flag=false;
        }
	    else
	    {
	    	message ="Number of pages in the files ("+actualPDFFile+" , "+expectedPDFFile+") is matched";
	    	System.out.println(message);
	    }
	    PDFTextStripper pdfStripper = new PDFTextStripper();
        if(flag==true) {
	    for (int i = 0; i < pdf1pages.getCount(); i++)
        {
        	System.out.println(i + 1);
            pdfStripper.setStartPage(i + 1);
            pdfStripper.setEndPage(i + 1);
            String pdf1PageText = pdfStripper.getText(pdf1);
            String pdf2PageText = pdfStripper.getText(pdf2);
            if (!pdf1PageText.equals(pdf2PageText))
            {
                message = "Contents of the files ("+actualPDFFile+" , "+expectedPDFFile+") do not match on Page no: " + (i + 1)+" actualPDFFile is : "+pdf1PageText+" , while expectedPDFFile is : "+pdf2PageText;
                System.out.println(message);
                System.out.println("******** fff ********");
//              System.out.println("pdf1PageText is " + pdf1PageText);
//              System.out.println("pdf2PageText is " + pdf2PageText);
                String difference = StringUtils.difference(pdf1PageText, pdf2PageText);
                System.out.println("Difference is Page no: '" + (i + 1)+"' in PFF Filename '"+expectedPDFFile+"' : "+difference);
               // break;
            }
				else 
					{
						message = "Contents of the files (" + actualPDFFile + " , " + expectedPDFFile + ") is matched";
						System.out.println(message);
					}
				}
			}
        else
			{
				System.out.println(message);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		//comparePDF("progress_chart - Bar Chart.pdf", "progress_chart - Bar Chart - V0.2.pdf");
		//comparePDF("Sample.pdf", "SamplePDF1.pdf");
		comparePDF("JhonTest.pdf", "JhonTest2.pdf");
	}

}
