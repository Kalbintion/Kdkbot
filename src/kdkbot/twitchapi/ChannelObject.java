package kdkbot.twitchapi;

import java.util.HashMap;

public class ChannelObject {
	public boolean mature;
	public String status;
	public String broadcaster_language;
	public String display_name;
	public String game;
	public long delay;
	public String language;
	public int _id;
	public String name;
	public String created_at;
	public String updated_at;
	public String logo;
	public String banner;
	public String video_banner;
	public String background;
	public String profile_banner;
	public String profile_banner_background_color;
	public boolean partner;
	public String url;
	public long views;
	public long followers;
	public HashMap<String, String> _links;
	public String email;
	public String stream_key;
	
	public ChannelObject(String channel) {
		
	}
}
