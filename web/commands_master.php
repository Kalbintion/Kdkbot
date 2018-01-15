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