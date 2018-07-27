<?php 
$cfgContents = getBaseConfigContents();
$lines = explode("\r\n", $cfgContents);
asort($lines);
$cfgArr = array();
foreach($lines as $line) {
	$parts = explode("=", $line, 2);
	if(count($parts) >= 2) {
		$cfgArr[$parts[0]] = $parts[1];
	}
}

if(isset($_GET['t'])) {
	if($_GET['t'] == "j") {
		// Joining a channel
		sql_joinChannel($_SESSION['USER'], "twitch");
		
		qChannelUpdate($_SESSION['USER'], "join");
	} else if($_GET['t'] == "l") {
		// Leaving a channel
		sql_leaveChannel($_SESSION['USER'], "twitch");
		
		qChannelUpdate($_SESSION['USER'], "leave");
	}
}
echo "<h1>Channel Information For: " . $_SESSION['USER'] . "</h1>
<h1>Join/Leave</h1>";

if(sql_isInChannel($_SESSION['USER'], "twitch")) {
	echo "<a href=\"?p=manage/jl&t=l\">
			<div class=\"boxError\">Click here to have Kdkbot leave</div>
		  </a>";
} else { 
	echo "<a href=\"?p=manage/jl&t=j\">
			<div class=\"boxSuccess\">Click here to have Kdkbot join</div>
		  </a>";
}
?>
