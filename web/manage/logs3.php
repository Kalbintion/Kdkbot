<?php
isUserLoggedIn() or die("You need to log in.");

if(isset($_GET['log'])) {
	// We are displaying log information instead of displaying all available logs for channel
	echo "<center><h3><a href=\"?p=manage/logs\">Back</a></h3></center>";
	echo "<center><h3>Chat History from " . $_GET['log'] . "</h3></center>";
	echo "<table class=\"mintable table34\"><tr><th>Time</th><th>From</th><th>Message</th></tr>";
	$logLoc = getLogLocation($_SESSION['USER']);
	
	
	date_default_timezone_set("America/Chicago");
	
	$contents = file_get_contents($logLoc . "\\" . $_GET['log']);
	$lines = explode("\r\n", $contents);
	foreach($lines as $line) {
		$parts = explode(" ", $line, 5);
		if(count($parts) >= 3) {
			$parts[0] = date('Y-m-d H:i:s', $parts[0] / 1000);
			
			switch($parts[2]) {
				case "PRIVMSG":
					$parts[1] = explode("!", substr($parts[1], 1), 2)[0];
					$parts[4] = substr($parts[4], 1);
					echo "<tr><td>" . $parts[0] . "</td><td>" . $parts[1] . "</td><td>" . $parts[4] . "</td></tr>";
					break;
				case "PART":
					$parts[1] = explode("!", substr($parts[1], 1), 2)[0];
					echo "<tr><td>" . $parts[0] . "</td><td>" . $parts[1] . "</td><td>Left Channel</td></tr>";
					break;
				case "JOIN":
					$parts[1] = explode("!", substr($parts[1], 1), 2)[0];
					echo "<tr><td>" . $parts[0] . "</td><td>" . $parts[1] . "</td><td>Joined Channel</td></tr>";
					break;
				default:
					if($parts[1] == ">>>PRIVMSG") {
						$parts = explode(" ", $line, 4);
						$parts[0] = date('Y-m-d H:i:s', $parts[0] / 1000);
						$parts[3] = substr($parts[3], 1);
						echo "<tr><td> " . $parts[0] . "</td><td>kdkbot</td><td>" . $parts[3] . "</td></tr>";
					} else {
						echo "<tr><td colspan=\"3\">$line</td></tr>";
					}
					break;
			}
		}
	}
	echo "</table>";
} else {
	$logLoc = getLogLocation($_SESSION['USER']);
	$items = scandir($logLoc);
	arsort($items);
	
	echo "<h1>Log Files for ".$_SESSION['USER']."</h1>";
	foreach($items as $item) {
		if($item !== ".." && $item !== ".") {
			echo "<a href=\"?p=manage/logs2&log=".$item."\"><div class=\"logItem\">$item</div></a>";
		}
	}
	
	echo "<div style=\"visibility: hidden; clear: both;\"></div>";
}
?>