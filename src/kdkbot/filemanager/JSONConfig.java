package kdkbot.filemanager;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import kdkbot.Kdkbot;

import com.google.gson.*;

public class JSONConfig {
	private enum SETTING_TYPES {
		STRING, INTEGER, FLOAT, BOOLEAN;
	}
	
	private String filePath;

	/**
	 * A new config instance with a given Path to the file.
	 * @param filePath The path to the file that this config file belongs to
	 */
	public JSONConfig(Path filePath) {
		this.filePath = filePath.toAbsolutePath().toString();
		try {
			if(!Files.exists(filePath)) {
				Files.createFile(filePath);
			}
		} catch (IOException e) {
			try {
				Files.createDirectories(filePath.getParent());
			} catch (IOException e1) {
				Kdkbot.instance.dbg.writeln(this, "Failed to create directory structure to: " + filePath.toAbsolutePath().toString());
			}
		}
	}
	
	/**
	 * A new config instance with a given String to the file.
	 * @param filePath
	 */
	public JSONConfig(String filePath) {
		this(Paths.get(filePath));
	}
	
	public JsonObject getAllSettings() {
		return new JsonParser().parse(filePath).getAsJsonObject();
	}

	public String getSetting(String name) {
		return (String) getSetting(name, SETTING_TYPES.STRING);
	}
	
	public Object getSetting(String name, SETTING_TYPES type) {
		switch(type) {
			case STRING:
				return getAllSettings().get(name).getAsString();
			case INTEGER:
				return getAllSettings().get(name).getAsInt();
			case FLOAT:
				return getAllSettings().get(name).getAsFloat();
			case BOOLEAN:
				return getAllSettings().get(name).getAsBoolean();
			default:
				return null;
		}
	}
	
	public void setSetting(String name, Object value) {
		JsonObject allSettings = getAllSettings();
		allSettings.addProperty(name, value.toString());
	}
	
	public void saveSettings(JsonObject settings) {
		try {
			BufferedWriter write = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(filePath),
						StandardCharsets.UTF_8));
			write.write(settings.toString());
			write.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}
}
