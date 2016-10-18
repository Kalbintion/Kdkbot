package kdkbot.twitchapi;

public class TwitchMethods {
	private TwitchURLS URLS;
	TwitchMethods() {
		
	}
	
	public String requestAccessTokenURL() {
		String[] scopes = {"channel_editor", "channel_commercial", "channel_check_subscription"};
		return URLS.ACCESS + "response_type=code&client_id=&redirect_uri=&scope=" + implode(" ", scopes);
	}
	
	private static String implode(String separator, String... data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length - 1; i++) {
			if(!data[i].matches(" *")) {
				sb.append(data[i]);
				sb.append(separator);
			}
		}
		sb.append(data[data.length - 1].trim());
		return sb.toString();
	}
}
