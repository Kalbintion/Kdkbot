<?PHP

?>
<br />
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
	<td><i>viewers</i> will return the amount of viewers that the stream currently has, or it'll say it isn't live if it isn't.</td>
</tr>
<tr>
	<td>uptime</td>
	<td>1</td>
	<td>Yes</td>
	<td><i>uptime</i> will return the amount of time that the stream has been live, or it'll say it isn't live if it isn't.</td>
</tr>
<tr>
	<td>host</td>
	<td>3</td>
	<td>Yes</td>
	<td><i>host channel</i> will host the given <i>channel</i> name, as long as the bot is marked as an editor of the channel.</td>
</tr>
<tr>
	<td>unhost</td>
	<td>3</td>
	<td>Yes</td>
	<td><i>unhost</i> will stop hosting as long as the bot is marked as an editor of the channel.</td>
</tr>
</table>
<br /><br />
<hr>
<br />
<h2>Master Commands</h2>
<p>Master commands are commands that assigned to the bot owner, in this case it is <a href="http://twitch.tv/Kalbintion">Kalbintion</a>. These commands are only able to be used by them, most of which are used for debugging purposes</p>
<table class="minTable">
<tr>
	<th>Trigger</th>
	<th>Rank</th>
	<th>Active</th>
	<th>Info</th>
</tr>
<tr>
	<td>debug</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>debug enable</i> will enable debug messages to the console. <i>debug disable</i> will disable them.</td>
</tr>
<tr>
	<td>msgdupe</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>msgdupe from to</i> will duplicate messages received from the given channel name (<i>from</i>) and send them to the <i>to</i> channel.</td>
</tr>
<tr>
	<td>msgdupeto</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>msgdupeto to</i> will duplciate messages received from the channel the command is used and send them to the <i>to</i> channel.</td>
</tr>
<tr>
	<td>msgbreakall</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>msgbreakall</i> ceases all message duplications between all channels</td>
</tr>
<tr>
	<td>exit</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>exit</i> causes the bot to stop running, exiting with code 0</td>
</tr>
<tr>
	<td>echo</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>echo msg</i> causes the bot to send the specific message (<i>msg</i>) back to the channel the command was used in.</td>
</tr>
<tr>
	<td>echoto</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>echoto channel msg</i> causes the bot to send the specific message (<i>msg</i>) to the provided channel (<i>channel</i>).</td>
</tr>
<tr>
	<td>echotoall</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>echotoall msg</i> causes the bot to send the specific message (<i>msg</i>) to all channels the bot is in.</td>
</tr>
<tr>
	<td>color</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>color name</i> causes the bot to change it's own username color to the provided <i>name</i>.</td>
</tr>
<tr>
	<td>status</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>status msg</i> sends a twitter status update with the given <i>msg</i>.
</tr>
<tr>
	<td>ram?</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>ram?</i> sends to the channel the command was used in, the estimated amount of memory used by the bot.</td>
</tr>
<tr>
	<td>gc</td>
	<td>Master</td>
	<td>Always</td>
	<td><i>gc</i> will suggest two garbage collections be done by the java GC using System.gc();</td>
</tr>
</table>