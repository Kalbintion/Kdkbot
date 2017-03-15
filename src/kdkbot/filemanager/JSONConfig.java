package kdkbot.filemanager;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;

public final class JSONConfig {
	private String filePath;
	private JsonObject settings;
	
	private enum SETTING_TYPES {
		STRING, INTEGER, FLOAT, BOOLEAN;
	}
	

	/**
	 * Creates a new JSON Configuration with a specified file path
	 * @param filePath The location to the file to use
	 */
	private JSONConfig(String filePath) {
		this.filePath = filePath;
		this.settings = getAllSettings();
	}
	
	/**
	 * Obtains all of settings from the provided file for the class as a JsonObject
	 * @return A JsonObject containing all of the JSON data
	 */
	public JsonObject getAllSettings() {
		return new JsonParser().parse(filePath).getAsJsonObject();
	}

	/**
	 * Gets a particular setting based on name
	 * @param name The name of the setting to obtain
	 * @return The value, as a String, of the provided name
	 */
	public String getSetting(String name) {
		return (String) getSetting(name, SETTING_TYPES.STRING);
	}
	
	/**
	 * Gets a particular setting based on name and data type
	 * @param name The name of the setting to obtain
	 * @param type The type of data it is to be
	 * @return The value, as an Object, of the provided name and type
	 */
	public Object getSetting(String name, SETTING_TYPES type) {
		switch(type) {
			case STRING:
				return this.settings.get(name).getAsString();
			case INTEGER:
				return this.settings.get(name).getAsInt();
			case FLOAT:
				return this.settings.get(name).getAsFloat();
			case BOOLEAN:
				return this.settings.get(name).getAsBoolean();
			default:
				return null;
		}
	}
	
	/**
	 * Sets a particular setting based on name
	 * @param name The name of the setting to set
	 * @param value The value to set the setting name
	 */
	public void setSetting(String name, Object value) {
		this.settings.addProperty(name, value.toString());
	}
	
	/**
	 * Saves the settings to file
	 * @param settings
	 */
	public void saveSettings(JsonObject settings) {
		try (BufferedWriter write = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(filePath),
						StandardCharsets.UTF_8))) {
			write.write(settings.toString());
			write.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
