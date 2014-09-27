import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @deprecated Replaced by kdkbot.filemanager.Config;
 */
public class KdkbotConfig {
	private File file;
	private FileInputStream fileInStream;
	private InputStreamReader fileStreamRead;
	private BufferedReader fileBuffRead;
	
	public KdkbotConfig(String filePath) throws Exception {
		try {
			file = new File(this.getCleanFileName(filePath));
		} catch(NullPointerException e) {
			System.out.println("No file provided");
		}
		if(!this.file.exists()) {
			System.out.println("DBG: Attempting to create file at " + file.getAbsolutePath());
			this.file.createNewFile();
			System.out.println("WARN: Had to create file for " + this.file.getAbsolutePath());
		}
	}
	
	public ArrayList<String> getFileLines() throws Exception {
		ArrayList<String> fileLines = new ArrayList<String>();
		
		if(this.file.exists()) {
			// Ready file for reading
			fileInStream = new FileInputStream(this.file);
			fileStreamRead = new InputStreamReader(fileInStream);
			fileBuffRead = new BufferedReader(fileStreamRead);
			
			// Read the lines
			String line;
			while((line = fileBuffRead.readLine()) != null) {
				fileLines.add(line);
			}
			
			// Close off streams
			fileInStream.close();
			fileStreamRead.close();
			fileBuffRead.close();
			
			// Return the file lines
			return fileLines;
		}
		
		return new ArrayList<String>();
	}
	
	public String getCleanFileName(String filePath) {
		return filePath.replace("#", "");
	}
}
