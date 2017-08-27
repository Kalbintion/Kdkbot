<?php
isUserLoggedIn() or die("You need to log in.");

$channel = getChannelObject($_SESSION['USER']);
$user_stats = file_get_contents($channel->pathStats());
$stats = explode("\r\n", $user_stats);

if(isset($_GET['reset'])) {
	// We need to reset a user, lets find and reset stats
	for($i = 0; $i < count($stats); $i++) {
		$parts = explode(":", $stats[$i]);
		if(isset($parts) && isset($parts[0]) && $parts[0] === $_GET['reset']) {
			// We've found the user to reset
			$stats[$i] = $_GET['reset'] . ":0:0:0:0:0:0:0:0";
			
			// Put changes back to file
			file_put_contents($channel->pathStats(), implode("\r\n", $stats));
			
			// Have kdkbot reload for this channel
			qChannelUpdate($_SESSION['USER'], "stats");
		}
	}
}

echo "<h1>User Stats for ".$_SESSION['USER']."</h1>";
echo "<br /><center><p>Stats list for " . ucwords(strtolower($_SESSION['USER'])) . ". Total number of users: " . count($stats) . " </p>";
echo "<table class=\"minTable borderHeader\">
				<tr>
					<th>User</th>
					<th>First Join<br />(DD/MM/YYYY)</th>
					<th>Time Spent</th>
					<th>Message<br />Count</th>
					<th>Character<br />Count</th>
					<th>Bits Count</th>
					<th>Last Seen<br />(DD/MM/YYYY)</th>
					<!--<th>Reset</th>-->
				</tr>
	";

$idx_start = 0;
$idx_limit = 200;
if(isset($_GET['start'])) { $idx_start = $_GET['start']; }
if(isset($_GET['limit'])) { $idx_limit = $_GET['limit']; }
if(!isset($_GET['t'])) { $_GET['t'] = 0; }
asort($stats);

$tCount = count($stats);
$tOffset = 1;
echo "Page: ";
while($tCount > 0 ) {
	if($idx_start == (($tOffset - 1) * $idx_limit)) {
		echo "$tOffset ";
	} else {
		echo "<a href=\"?p=manage/stats&t=" . $_GET['t'] . "&start=" . (($tOffset - 1) * $idx_limit) . "\">$tOffset</a>&nbsp;";
	}
	
	$tOffset++;
	$tCount -= $idx_limit;
}
echo "</center><br />";

$startShowing = $idx_start;
$stopShowing = $idx_start + $idx_limit;
$currentNumber = 0;

foreach($stats as $stat) {
	if($currentNumber >= $startShowing && $currentNumber <= $stopShowing && isset($stat) && $stat !== "") {
		$stat_segments = explode(":", $stat);
		echo "<tr>
						<td>" . $stat_segments[0] . "</td>
						<td>" . implode("<br />", explode(" ", unixToTimestamp($stat_segments[1]), 2)) . "</td>
						<td>" . timeFromSeconds($stat_segments[2]) . "</td>
						<td>" . $stat_segments[3] . "</td>
						<td>" . $stat_segments[6] . "</td>
						<td>" . $stat_segments[7] . "</td>
						<td>" . implode("<br />", explode(" ", unixToTimestamp($stat_segments[5]), 2)). "</td>
						<!--<td><form action=\"?p=manage/stats&reset=".$stat_segments[0]."\" method=\"post\"><input type=\"submit\" value=\"Reset\" /></form></td>-->
					</tr>";
		
	}
	$currentNumber++;
}
echo "</table>";

function unixToTimestamp($time) {
	return date("d/m/Y h:i:s A", $time / 1000);
}

function timeFromSeconds($time) {
	$seconds = $time % 60;
	$minutes = $time / 60 % 60;
	$hours = $time / 60 / 60 % 60;
	$days = floor($time / 60 / 60 / 24);
	
	$out = $days . "D " . $hours . "H " . $minutes . "M " . $seconds . "S";
	
	return $out;
}
?>