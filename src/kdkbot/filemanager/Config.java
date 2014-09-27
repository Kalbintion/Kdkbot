package kdkbot.filemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Config {
	private Path filePath;
	private HashMap<String, String> values = new HashMap<String, String>();
	
	/**
	 * 
	 * @param filePath The path to the file that this config file belongs to
	 */
	public Config(Path filePath) throws Exception {
		this.filePath = filePath;
		verifyExists();
		loadConfigContents();
	}
	
	public Config(String filePath) throws Exception {
		this.filePath = Paths.get(filePath);
		verifyExists();
		loadConfigContents();
	}
	
	public void setPath(Path filePath) {
		this.filePath = filePath;
	}
	
	public void setPath(String filePath) {
		this.filePath = Paths.get(filePath);
	}
	
	public Path getPath() {
		return this.filePath;
	}
	
	public String getSetting(String key) {
		return this.values.get(key);
	}
	
	public void verifyExists() throws Exception {
		if(!Files.exists(this.filePath)) {
			System.out.println("DBG: Doesn't exist: " + this.filePath.toString());
			try {
				System.out.println("DBG: Attempting to create.");
				Files.createFile(this.filePath);
			} catch (IOException e) {
				System.out.println("DBG: Couldn't create, attempting to create directories first.");
				Files.createDirectories(this.filePath.getParent());
			}
		}
	}
	
	public void loadConfigContents() throws Exception {
		FileInputStream fis = new FileInputStream(this.filePath.toAbsolutePath().toString());
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		
		String line;
		while((line = br.readLine()) != null) {
			System.out.println("DBG: " + line);
			String[] args = line.split("=");
			System.out.println("DBG: " + args.length);
			this.values.put(args[0], args[1]);
		}
		
		fis.close();
		isr.close();
		br.close();
	}
}
