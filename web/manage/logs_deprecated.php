<?php
isUserLoggedIn() or die("You need to log in.");

if(isset($_GET['log'])) {
	// We are displaying log information instead of displaying all available logs for channel
	echo "<center><h3><a href=\"?p=manage/logs\">Back</a></h3></center>";
	echo "<h3>Chat History from " . $_GET['log'] . "</h3>";
	echo "<pre class=\"chatHistory\">";
	$logLoc = getLogLocation($_SESSION['USER']);
	
	$contents = file_get_contents($logLoc . "\\" . $_GET['log']);
	print($contents);
	echo "</pre>";
} else {
	$logLoc = getLogLocation($_SESSION['USER']);
	$items = scandir($logLoc);
	arsort($items);
	
	echo "<h1>Log Files for ".$_SESSION['USER']."</h1>";
	foreach($items as $item) {
		if($item !== ".." && $item !== ".") {
			echo "<a href=\"?p=manage/logs&log=".$item."\"><div class=\"logItem\">$item</div></a>";
		}
	}
	
	echo "<div style=\"visibility: hidden; clear: both;\"></div>";
}
?>