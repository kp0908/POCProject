package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ArchiveDemo1 {
	
	List<String> filesListInDir = new ArrayList<String>();
	static String filePath = System.getProperty("user.dir");
	
	
    public static void main(String[] args) {

    	String dateTime=timestamp();
    	String dateAndTime = dateTime.replace("-", "_").replace(" ", "_").replace(":", "_");
        File dir = new File(filePath+"\\Screenshots");
        String zipDirName = filePath+"\\ArchiveFolder\\Screenshots_"+dateAndTime+".zip";
        
        ArchiveDemo1 zipFiles = new ArchiveDemo1();
        zipFiles.zipDirectory(dir, zipDirName);
    }
	
	 /**
     * This method populates all the files in a directory to a List
     * @param dir
     * @throws IOException
     */
    public void populateFilesList(File dir) {
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isFile()) 
            	{
            	filesListInDir.add(file.getAbsolutePath());
            	}
            else {
            	System.out.println("File Is Available "+file.isFile());
            }
        }
    }
    

    /**
     * This method zips the directory
     * @param dir
     * @param zipDirName
     */
    public void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            System.out.println(filesListInDir);
            for(String filePath : filesListInDir){
                System.out.println("Zipping "+filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
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

    
	/**
	 * Below Method is to delete the files from the directory.
	 * @param filepath
	 */
	public static void deleteFileFromDirectory(String filepath)
	{
		//System.getProperty("User.dir")+"\\Screenshots"
		File dir = new File(filepath);
		File[] files = dir.listFiles();
		for(File file : files){
			if(file.delete())                      //returns Boolean value  
			{  
			System.out.println(file.getName() + " deleted");   //getting and printing the file name  
			}  
			else  
			{  
			System.out.println("failed");  
			}  
		}
		
	}  
    
}
