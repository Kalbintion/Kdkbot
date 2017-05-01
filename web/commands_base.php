<h2>Base Commands</h2>
<p>Base commands are commands that are found in every channel kdkbot is a part of. The information presented here are for the default values. Some commands may have been disabled or modified in permission level requirements on a per channel basis.</p>
<table class="minTable">
<tr>
	<th>Trigger</th>
	<th>Rank</th>
	<th>Active</th>
	<th>Info</th>
</tr>
<tr>
	<td>ama</td>
	<td>1</td>
	<td>Yes</td>
	<td><i>ama</i> incorporates an "ask me anything" system in which questions can be added to be viewed later through a queuing system.</td>
</tr>
<tr>
	<td>quote</td>
	<td>1</td>
	<td>Yes</td>
	<td><i>quote</i> incorporates a storage system for memorable comments by the streamer(s), for use later on.</td>
</tr>
<tr>
	<td>channel</td>
	<td>5</td>
	<td>Always</td>
	<td><i>channel</i> offers various channel specific settings such as the trigger prefix, base command permission levels, etc.</td>
</tr>
<tr>
	<td>commands</td>
	<td>1</td>
	<td>Yes</td>
	<td><i>commands</i> incorporates a custom command system for creating channel specific user commands.</td>
</tr>
<tr>
	<td>counter</td>
	<td>2</td>
	<td>Yes</td>
	<td><i>counter</i> incorporates a counting system. Useful for tracking number of deaths, kills, etc.</td>
</tr>
<tr>
	<td>perm</td>
	<td>3</td>
	<td>Yes</td>
	<td><i>perm</i> is a core command that is designed to give other users a particular permission level.</td>
</tr>
<tr>
	<td>stats</td>
	<td>0</td>
	<td>Yes</td>
	<td><i>stats</i> gives the user specific statistical information about them in regards to the channel it was used in. Information such as number of messages sent and time spent in channel.</td>
</tr>
<tr>
	<td>msges</td>
	<td>0</td>
	<td>Yes</td>
	<td><i>msges</i> gives the user specific statistical information about them in regards to the channel it was used in. This tells the user the amount of messages they have sent with the number of characters in those messages since a date with their first join time.</td>
</tr>
<tr>
	<td>time</td>
	<td>0</td>
	<td>Yes</td>
	<td><i>time</i> gives the user specific statistical information about them in regards to the channel it was used in. This tells the user the amount of time they have spent in a channel since a particular date.</td>
</tr>
<tr>
	<td>bits</td>
	<td>0</td>
	<td>Yes</td>
	<td><i>bits</i> gives the user specific statistical information about them in regards to the channel it was used in. This tells the user the amount of time they have spent in a channel since a particular date.</td>
</tr>
<tr>
	<td>timers</td>
	<td>3</td>
	<td>Yes</td>
	<td><i>timers</i> incorporates a repeatable message to be sent to the channel based on a given delay.</td>
</tr>
<tr>
	<td>urban</td>
	<td>2</td>
	<td>Yes</td>
	<td><i>urban</i> requests the information for the first result found on the urban dictionary website.</td>
</tr>
<tr>
	<td>permit</td>
	<td>3</td>
	<td>Yes</td>
	<td><i>permit</i> allows a user to bypass certain filters created with the <i>filters</i> command.</td>
</tr>
<tr>
	<td>game</td>
	<td>1, 3</td>
	<td>Yes</td>
	<td><i>game</i>, if not provided anything after, will request the streamers current game name. This requires a permission rank of 1 or higher to use. If something is after <i>game</i>, it will attempt to send the new games title to twitch. This requires a permission rank of 3 or higher to use.</td>
</tr>
<tr>
	<td>status</td>
	<td>1, 3</td>
	<td>Yes</td>
	<td><i>status</i>, if not provided anything after, will request the streamers current stream title. This requires a permission rank of 1 or higher to use. If something is after <i>status</i>, it will attempt to send the new streams title to twitch. This requires a permission rank of 3 or higher to use.</td>
</tr>
<tr>
	<td>viewers</td>
	<td>1</td>
	<td>Yes</td>
	<td><i>viewers</i> will return the amount of viewers that the stream currently has, or it'll say it isn't live if it isn't. If a username is supplied, it will get the viewer count of the given user, or let you know if they are not online.</td>
</tr>
<tr>
	<td>uptime</td>
	<td>1</td>
	<td>Yes</td>
	<td><i>uptime</i> will return the amount of time that the stream has been live, or it'll say it isn't live if it isn't. If a username is supplied, it will get the uptime of the given user, or let you know if they are not online.</td>
</tr>
<tr>
	<td>host</td>
	<td>3</td>
	<td>Yes</td>
	<td><i>host channel</i> will host the given <i>channel</i> name, as long as the bot is marked as an editor of the channel. If no username is supplied, it will get the currently hosted channel, or let you know if you are not hosting.</td>
</tr>
<tr>
	<td>unhost</td>
	<td>3</td>
	<td>Yes</td>
	<td><i>unhost</i> will stop hosting as long as the bot is marked as an editor of the channel.</td>
</tr>
</table>