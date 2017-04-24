<?PHP
	$filPath = "../cfg/#" . $_GET['c'] . "/cmds.cfg";
	if(file_exists($filPath)) {
		$fil = file_get_contents($filPath);
		$commands = explode("\r\n", $fil);
		
		echo "<br /><p>Command list for " . ucwords(strtolower($_GET['c'])) . "</p>";
		echo "<table class=\"minTable\"><tr><th>Trigger</th><th>Rank</th><th>Active</th><th>Text</th></tr>";
		foreach ($commands as $command) {
			if($command !== "") {
				$command_segments = explode("|", $command, 4);
				echo "<tr><td>" . $command_segments[2] . "</td><td>" . $command_segments[0] . "</td><td>" . $command_segments[1] . "</td><td> " . $command_segments[3] . "</td></tr>";
			}
		}
		echo"</table>";
	} else {
		require("error.php");
	}
?>