package kdkbot.twitchapi;

import java.util.HashMap;

public class Headers {
	private HashMap<String, String> headers;
	
	public void addHeader(String headerName, String value) {
		headers.put(headerName, value);
	}
	
	public void removeHeader(String headerName) {
		headers.remove(headerName);
	}
	
	public String compileHeaderList() {
		return "";
	}
}
