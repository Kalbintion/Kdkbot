<?PHP
	if(!isset($_GET['channel'])) {
		// We need to list the channels for the bot
		$channels = "";
		$fil = file_get_contents("../cfg/settings.cfg");
		$lines = explode("\r\n", $fil);
		
		foreach ($lines as $line) {
			if(substr($line, 0, strlen("channels=")) === "channels=") {
				$channels = $line;
			}
		}
		
		$channels = str_replace("channels=", "", $channels);
		$eChannels = explode(",", $channels);
		
		echo "<table class=\"minTable\"><tr><th colspan=\"2\">User</th><th>Commands</th><th>Twitch</th></tr>";
		foreach ($eChannels as $channel) {
			$channel = str_replace("#", "", $channel);
			echo "<tr><td><img src=\"https://static-cdn.jtvnw.net/jtv-static/404_preview-80x80.png\"></td><td>$channel</td><td><a href=\"?p=channels&channel=$channel\">Commands</a></td><td><a href=\"twitch.tv/$channel\">Twitch</a></td></tr>\r\n";
		}
		echo "</table>";
	} else {
		// We have a channel selected, will need to load in the list of commands and display them.
		$fil = file_get_contents("../cfg/#" . $_GET['channel'] . "/cmds.cfg");
		$commands = explode("\r\n", $fil);
		
		echo "<br /><p>Command list for " . ucwords(strtolower($_GET['channel'])) . "</p>";
		echo "<table class=\"minTable\"><tr><th>Trigger</th><th>Rank</th><th>Active</th><th>Text</th></tr>";
		foreach ($commands as $command) {
			if($command !== "") {
				$command_segments = explode("|", $command, 4);
				echo "<tr><td>" . $command_segments[2] . "</td><td>" . $command_segments[0] . "</td><td>" . $command_segments[1] . "</td><td> " . $command_segments[3] . "</td></tr>";
			}
		}
		echo"</table>";
	}
?>