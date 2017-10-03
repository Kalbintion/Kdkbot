<?php 
isUserLoggedIn() or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

if(isset($_POST['update'])) {
	// echo "<pre>".print_r($_POST,true)."</pre>";
	
	$out = "";
	
	for($i = 0; $i < count($_POST['name']); $i++) {
		if($_POST['name'][$i] == "") { continue; }
	
		// NAME|TIME|FLAGS|MESSAGE
		$out .= $_POST['name'][$i] . "|" . $_POST['time'][$i] . "|";
		// Handle which flags to have
		$flags_out = "";
		if($_POST['flag_live'][$i] == "true") {
			$flags_out .= "REQUIRES_LIVE+";
		}
		if($_POST['flag_msg'][$i] >= "1") {
			$flags_out .= "REQUIRES_MSG_COUNT=" . $_POST['flag_msg'][$i] . "+";
		}
		if($_POST['flag_game'][$i] != "") {
			$flags_out .= "REQUIRES_GAME=" . $_POST['flag_game'][$i] . "+";
		}
		
		if(endsWith($flags_out, "+")) { $flags_out = substr($flags_out, 0, -1); }
		
		$out .= $flags_out . "|";
		$out .= $_POST['msg'][$i] . "\r\n";
	}
	
	// echo "<pre>$out</pre>";
	
	if(file_put_contents($channel->pathTimers(), $out) === false) {
		echo "<div class=\"boxError\">Couldn't update Timers. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated Timers.</div><br />";
		qChannelUpdate($_SESSION['USER'], "timers");
	}
}

$timers_contents = file_get_contents($channel->pathTimers());
$timers = explode("\r\n", $timers_contents);

// echo "<pre>".print_r($timers,true)."</pre>";

// Question
echo "
	<div class=\"boxWarning\">To remove a timer, Make the Name blank.<br />Updating timers will interrupt all timers, resetting their delay.</div><br />
		
	<form action=\"?p=manage/timers\" method=\"POST\">
	<input type=\"hidden\" value=\"" . $_SESSION['USER'] . "\" name=\"update\" />
	<table class=\"minTable inputs2\" id=\"timerTable\">
	<tr>
		<th colspan=\"3\">&nbsp;</th>
		<th colspan=\"3\">Flags</th>
	</tr>
	<tr>
		<th>Name</th>
		<th>Delay (sec)</th>
		<th>Message</th>
		<th>Reqs<br/>Live</th>
		<th>Reqs Message<br />Count</th>
		<th>Reqs Game</th>
	</tr>";

	$i = 0; $j = 0;
	foreach($timers as $timer) {
		$i++; $j++;
		if($timer != "") {
			$parts = explode("|", $timer, 5);
			$flags = explode("+", $parts[2]);
			
			
			$flag_live = false;
			$flag_msg = 0;
			$flag_game = "";
			
			foreach($flags as $flag) {
				$flag_parts = explode("=", $flag, 2);
				switch($flag_parts[0]) {
					case "REQUIRES_LIVE":
						$flag_live = true;
						break;
					case "REQUIRES_MSG_COUNT":
						$flag_msg = explode("=", $flag, 2)[1];
						break;
					case "REQUIRES_GAME":
						$flag_game = explode("=", $flag, 2)[1];
						break;
				}
			}
			
			echo "
				<tr>
					<td><input type=\"text\" name=\"name[]\" value=\"" . $parts[0] . "\" /></td>
					<td><input type=\"number\" name=\"time[]\" value=\"" . $parts[1] . "\" max=\"10000\" min=\"1\"/></td>
					<td><input type=\"text\" name=\"msg[]\" value=\"" . $parts[3] . "\" /></td>
					<td><select name=\"flag_live[]\">";
					if($flag_live) { echo "<option value=\"true\" selected>Yes</option><option value=\"false\">No</option>"; }
					else { echo "<option value=\"true\">Yes</option><option value=\"false\" selected>No</option>"; }
					
			echo "</td>
					<td><input type=\"number\" name=\"flag_msg[]\" value=\"$flag_msg\" min=\"0\" max=\"10000\"></td>
					<td><input type=\"text\" name=\"flag_game[]\" value=\"$flag_game\"></td>
				</tr>\r\n";
		}
		
		if($i == 15 && (count($timers) - $j) != 1) {
			echo "<tr>
					<td colspan=\"7\"><input type=\"submit\" value=\"Submit\" /></td>
				</tr>";
			$i = 0;
		}
	}
	
echo "
		<tr>
			<td colspan=\"7\"><input type=\"submit\" value=\"Submit\" /></td>
		</tr>
		<tr>
			<td colspan=\"7\"><input type=\"button\" value=\"Add Timer\" onClick=\"addNewTimer()\" /></td>
		</tr>
	</table>
	</form>
";
?>