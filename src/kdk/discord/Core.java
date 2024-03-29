package kdk.discord;

import kdk.Bot;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class Core {
	public static IDiscordClient createClient(String token, boolean login) {
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(token);
		clientBuilder.registerListener(new Events());
		try {
			if (login) {
				return clientBuilder.login();
			} else {
				return clientBuilder.build();
			}
		} catch(DiscordException e) {
			Bot.inst.logger.logln("DiscordException");
			e.printStackTrace();
			System.exit(2);
			return null;
		}
	}
}
