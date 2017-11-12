<?php
isUserLoggedIn() or die("You need to log in.");

if(isset($_GET['log'])) {
	// We are displaying log information instead of displaying all available logs for channel
	echo "<center><h3><a href=\"?p=manage/logs\">Back</a></h3></center>";
	echo "<h3>Chat History from " . $_GET['log'] . "</h3>";
	echo "<pre class=\"chatHistory\" style=\"background-color: #000;\">";
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
					if(startsWith($parts[4], "ACTION")) {
						$parts[4] = substr($parts[4], 8);
						$parts[4] = substr($parts[4], 0, -1);
						echo "<i><span style=\"color: #666;\">" . $parts[0] . "</span> <span style=\"color: #66F\">@" . $parts[1] . "</span> " . $parts[4] . "</i>\r\n";
					} else {
						echo "<span style=\"color: #666;\">" . $parts[0] . "</span> <span style=\"color: #66F\">@" . $parts[1] . "</span>: " . $parts[4] . "\r\n";
					}
					break;
				case "PART":
					$parts[1] = explode("!", substr($parts[1], 1), 2)[0];
					echo "<span style=\"color: #666;\">" . $parts[0] . "</span> <span style=\"color: #66F\">@" . $parts[1] . "</span> <span style=\"color: #F00;\">Left Channel</span>\r\n";
					break;
				case "JOIN":
					$parts[1] = explode("!", substr($parts[1], 1), 2)[0];
					echo "<span style=\"color: #666;\">" . $parts[0] . "</span> <span style=\"color: #66F\">@" . $parts[1] . "</span> <span style=\"color: #0F0;\">Joined Channel</span>\r\n";
					break;
				default:
					if($parts[1] == ">>>PRIVMSG") {
						$parts = explode(" ", $line, 4);
						$parts[0] = date('Y-m-d H:i:s', $parts[0] / 1000);
						$parts[3] = substr($parts[3], 1);
						echo "<span style=\"color: #666;\">" . $parts[0] . "</span> <span style=\"color: #33F\">@Kdkbot</span>: " . $parts[3] . "\r\n";
					} else {
						echo $line . "\r\n";
					}
					break;
			}
		}
	}
	echo "</pre>";
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