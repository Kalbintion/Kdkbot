package kdk.dataman;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kdk.Bot;
import kdk.cmds.custom.StringCommand;

// TODO: Change DBMan/self to use sanitized statements
// NOTE: Minimal temporary security risk, db is watched
public class DBFetcher {
	public static DBMan _mgr = null;
	
	public static ArrayList<String> getTwitchChannels() {
		if(_mgr != null) { return getTwitchChannels(_mgr); } else { return null; }
	}
	
	public static ArrayList<String> getTwitchChannels(DBMan mgr) {
		ArrayList<String> CHANS = new ArrayList<String>();
		
		try {
			ResultSet channels = getChannelsRes(mgr, "twitch");
			
			while(channels.next()) {
				boolean join_channel = channels.getBoolean("join_channel");
				if(join_channel) {
					CHANS.add(channels.getString("channel"));
				}
			}
		} catch (SQLException e) {
			return null;
		}
		
		return CHANS;
	}
	
	public static int updateTwitchChannel(String channel, String set_data) {
		if(_mgr != null) {
			updateTwitchChannel(_mgr, channel, set_data);
		} else { return -1;	}
		return 0;
	}
	
	public static void updateTwitchChannel(DBMan mgr, String channel, String set_data) {
		mgr.updateDB("UPDATE channels SET " + set_data + " WHERE channel='" + channel + "'");
	}
	
	public static ResultSet getChannelsRes(String platform) {
		if(_mgr != null) { return getChannelsRes(_mgr, platform); } else { return null; }
	}
	
	public static ResultSet getChannelsRes(DBMan mgr, String platform) {
		return mgr.queryDB("SELECT * FROM channels WHERE platform='" + platform + "'");
	}
	
	public static int leaveTwitchChannel(String channel) {
		if(_mgr != null) { leaveTwitchChannel(_mgr, channel); return 0;} else { return -1; }
	}
	
	public static void leaveTwitchChannel(DBMan mgr, String channel) {
		if(channel.startsWith("#")) {
			channel = channel.substring(1);
		}
		updateTwitchChannel(mgr, channel, "join_channel='0'");
	}
	
	public static int joinTwitchChannel(String channel) {
		if(_mgr != null) { joinTwitchChannel(_mgr, channel); return 0; } else { return -1; } 
	}
	
	public static void joinTwitchChannel(DBMan mgr, String channel) {
		if(channel.startsWith("#")) {
			channel = channel.substring(1);
		}
		updateTwitchChannel(mgr, channel, "join_channel='1'");
	}
	
	public static boolean isInTwitchChannel(String channel) {
		if(_mgr != null) { return isInTwitchChannel(_mgr, channel);	} else { return false; }
	}
	
	public static boolean isInTwitchChannel(DBMan mgr, String channel) {
		return false;
	}
	
	public static String getTwitchIRC() {
		if(_mgr != null) { return getTwitchIRC(_mgr); } else { return null; }
	}
	
	public static String getTwitchIRC(DBMan mgr) {
		return getSetting(mgr, "twitch_irc");
	}
	
	public static String getTwitchOAuth() {
		if(_mgr != null) { return getTwitchOAuth(_mgr); } else { return null; }
	}
	
	public static String getTwitchOAuth(DBMan mgr) {
		return getSetting(mgr, "twitch_oauth");
	}
	
	public static String getTwitchClientID() {
		if(_mgr != null) { return getTwitchClientID(_mgr); } else { return null; }
	}
	
	public static String getTwitchClientID(DBMan mgr) {
		return getSetting(mgr, "twitch_client_id");
	}
	
	public static String getTwitchIRCPort() {
		if(_mgr != null) { return getTwitchIRCPort(_mgr); } else { return null; }
	}
	
	public static String getTwitchIRCPort(DBMan mgr) {
		return getSetting(mgr, "twitch_irc_port");
	}
	
	public static String getTwitchNick() {
		if(_mgr != null) { return getTwitchNick(_mgr); } else { return null; }
	}
	
	public static String getTwitchNick(DBMan mgr) {
		return getSetting(mgr, "twitch_nick");
	}
	
	public static ArrayList<StringCommand> getChannelCommands(String platform, String channel) {
		if(_mgr != null) { return getChannelCommands(_mgr, platform, channel); } else { return null; }
	}
	
	public static ArrayList<StringCommand> getChannelCommands(DBMan mgr, String platform, String channel) {
		ArrayList<StringCommand> cmds = new ArrayList<StringCommand>();
		ResultSet rs = mgr.queryDB("SELECT * FROM commands WHERE platform=\"" + platform + "\" AND channel=\"" + channel + "\"");
		try {
			while(rs.next()) {
				StringCommand cmd = new StringCommand(rs.getInt("id"), rs.getString("trigger"), rs.getString("message"), rs.getInt("level"), rs.getBoolean("available"));
				cmds.add(cmd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return cmds;
	}
	
	public static boolean addChannelCommand(String platform, String channel, StringCommand cmd) {
		if(_mgr != null) { return addChannelCommand(_mgr, platform, channel, cmd); } else { return false; }
	}
	
	public static boolean addChannelCommand(DBMan mgr, String platform, String channel, StringCommand cmd) {
		int res = _mgr.updateDB("INSERT INTO commands (`channel`, `trigger`, `available`, `editable`, `level`, `message`, `platform`, `discord_permitted_channels`) VALUES (\"" + channel + "\", \"" + cmd.getTrigger() + "\", " + cmd.getAvailability() + ", true, " + cmd.getPermissionLevel() + ", \"" + cmd.getMessage() + "\", \"" + platform + "\", null);");
		if(res > 0) { return true; }
		return false;
	}
	
	public static int updateChannelCommand(String platform, String channel, StringCommand cmd) {
		return -1; // General Error
	}
	
	public static int updateChannelCommand(String platform, String channel, StringCommand cmd, int targetID) {
		return -1; // General Error
	}
	
	public static String getTwitterOAuthConsumer() {
		if(_mgr != null) { return getTwitterOAuthConsumer(_mgr); } else { return null; }
	}
	
	public static String getTwitterOAuthConsumer(DBMan mgr) {
		return getSetting(mgr, "twitter_consumer_oauth");
	}
	
	public static String getTwitterOAuth() {
		if(_mgr != null) { return getTwitterOAuth(_mgr); } else { return null; }
	}
	
	public static String getTwitterOAuth(DBMan mgr) {
		return getSetting(mgr, "twitter_oauth");
	}
	
	public static String getTwitterSecret() {
		if(_mgr != null) { return getTwitterSecret(_mgr); } else { return null; }
	}
	
	public static String getTwitterSecret(DBMan mgr) {
		return getSetting(mgr, "twitter_oauth_secret");
	}
	
	public static String getTwitterSecretConsumer() {
		if(_mgr != null) { return getTwitterSecretConsumer(_mgr); } else { return null; }
	}
	
	public static String getTwitterSecretConsumer(DBMan mgr) {
		return getSetting(mgr, "twitter_consumer_secret");
	}
	
	public static String getMasterCommand() {
		if(_mgr != null) { return getSetting(_mgr, "master_commands"); } else { return null; }
	}
	
	public static String getMasterCommand(DBMan mgr) {
		return getSetting(mgr, "master_commands");
	}
	
	public static boolean getWebEnabled() {
		if(_mgr != null) { return getWebEnabled(_mgr); } else { return false; }
	}
	
	public static boolean getWebEnabled(DBMan mgr) {
		return Boolean.parseBoolean(getSetting(mgr, "web_enabled"));
	}
	
	public static String getWatcherLoc() {
		if(_mgr != null) { return getWatcherLoc(_mgr); } else { return null; }
	}
	
	public static String getWatcherLoc(DBMan mgr) {
		return getSetting(mgr, "watcher_loc");
	}
	
	public static String getWebPath() {
		if(_mgr != null) { return getWebPath(_mgr); } else { return null; }
	}
	
	public static String getWebPath(DBMan mgr) {
		return getSetting(mgr, "web_loc");
	}
	
	public static String getSetting(String find_on) {
		if(_mgr != null) { return getSetting(_mgr, find_on); } else { return null; }
	}
	
	public static String getSetting(DBMan mgr, String find_on) {
		return getSetting(mgr, "settings", "value", find_on);
	}
	
	
	public static String getSetting(DBMan mgr, String table, String column, String find_on) {
		System.out.println("Query: " + "SELECT " + column + " FROM " + table + " WHERE name='" + find_on + "'");
		ResultSet rs = mgr.queryDB("SELECT " + column + " FROM " + table + " WHERE name='" + find_on + "'");
		try {
			while(rs.next()) {
				return rs.getString(column);
			}
			return null;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public static HashMap<String, Integer> getChannelPerms(String channel) {
		if(_mgr != null) { return getChannelPerms(_mgr, channel); } else { return null; }
	}
	
	public static HashMap<String, Integer> getChannelPerms(DBMan mgr, String channel) {
		HashMap<String, Integer> users = new HashMap<String, Integer>();
		
		if(channel.startsWith("#")) { channel = channel.substring(1); }
		
		ResultSet rs = mgr.queryDB("SELECT user, level FROM permissions WHERE channel='" + channel + "'");
		
		try {
			while(rs.next()) {
				users.put(rs.getString("user"), Integer.parseInt(rs.getString("level")));
			}
		} catch (SQLException e) {
			mgr.writeSQLError(e);
			return null;
		} catch (NumberFormatException e) {
			Bot.instance.dbg.writeln("NFE: " + e.getMessage());
		}
		
		return users;
	}
	
	public static int setChannelPerm(String channel, String user, int level) {
		if(_mgr != null) { return setChannelPerm(_mgr, channel, user, level); } else { return -1; }
	}
	
	public static int setChannelPerm(DBMan mgr, String channel, String user, int level) {
		if(channel.startsWith("#")) { channel = channel.substring(1); }
		
		if(getUserPerm(channel, user) > Integer.MIN_VALUE) {
			return mgr.updateDB("UPDATE permissions SET level=" + level + " WHERE channel='" + channel + "' AND user='" + user + "'");
		} else {
			return mgr.updateDB("INSERT INTO permissions (channel, user, level) VALUES('" + channel + "', '" + user + "', " + level +")");
		}
	}
	
	public static int getUserPerm(String channel, String user) {
		if(_mgr != null) { return getUserPerm(_mgr, channel, user); } else { return 0; }
	}
	
	public static int getUserPerm(DBMan mgr, String channel, String user) {
		ResultSet rs = mgr.queryDB("SELECT level FROM permissions WHERE channel='" + channel + "' AND user='" + user + "'");
		try {
			while(rs.next()) {
				return rs.getInt("level");
			}
		} catch (SQLException e) {
			mgr.writeSQLError(e);
			return Integer.MIN_VALUE;
		}
		return Integer.MIN_VALUE;
	}
}
