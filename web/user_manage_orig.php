<?php
isset($_SESSION['USER']) or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

function getChannelSettings() {
	$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);
	$contents = file_get_contents($channel->pathChannel());

	$lines = explode("\r\n", $contents);
	asort($lines);
	
	$channelObj = [];
	
	foreach($lines as $line) {
		$parts = explode("=", $line, 2);
		if(count($parts) >= 2) {
			if(strpos($parts[0], "trigger") !== false || strpos($parts[0], "availability") !== false || strpos($parts[0], "rank") !== false) {
				
			} else {
				// We have an actual command setting
				$channelObj[$parts[0]] = $parts[1];
			}
		}
	}
	
	return $channelObj;
}

function getBaseCommands() {
	$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);
	$contents = file_get_contents($channel->pathChannel());
	
	$lines = explode("\r\n", $contents);
	asort($lines);
	
	$channelObj = [];
	
	foreach($lines as $line) {
		$parts = explode("=", $line, 2);
		if(count($parts) >= 2) {
			if(strpos($parts[0], "trigger") !== false || strpos($parts[0], "availability") !== false || strpos($parts[0], "rank") !== false) {
				// We have an actual command setting
				$cmdName = str_replace("trigger", "", $parts[0]);
				$cmdName = str_replace("rank", "", $cmdName);
				$cmdName = str_replace("availability", "", $cmdName);
				$cmdPart = "";
				if(strpos($parts[0], "trigger") !== false) {
					$cmdPart = "trigger";
				} elseif(strpos($parts[0], "availability") !== false) {
					$cmdPart = "availability";
				} elseif(strpos($parts[0], "rank") !== false) {
					$cmdPart = "rank";
				}
				$channelObj[$cmdName][$cmdPart] = $parts[1];
			}
		}
	}
	
	return $channelObj;
}

function getCustomCommands() {
	
}

$channelObj = getChannelSettings();

echo "<br />
<div class=\"boxWarning\"><center><img src=\"https://cdnd.icons8.com/wp-content/uploads/2015/06/under-construction-2.gif\" width=\"120px\" height=\"100px\"><br /><br />This page is currently under construction and may not be fully functional at this time. </center></div>


<hr>
";

if(isset($_POST['update'])) { echo "UPDATE REQUEST FOUND"; echo "<br><pre>".print_r($_POST,true)."</pre>"; }

echo "
<form action=\"?p=user_manage\" method=\"post\">
<input type=\"hidden\" value=\"".$_SESSION['USER']."\" name=\"update\" />
<h1>Channel Information For: " . $_SESSION['USER'] . "</h1>
<h1>Basic Channel Settings</h1>
<table class=\"settings minTable table34\">
<tr>
	<td style=\"width: 200px; text-align: right;\">Command Processing</td>
	<td>";
if($channelObj['commandProcessing'] === "true") { 
	echo "<select id=\"commandProcessing\"><option selected=\"selected\" value=\"true\">Yes</option><option value=\"false\">No</option></select>";
} else {
	echo "<select id=\"commandProcessing\"><option value=\"true\">Yes</option><option selected=\"selected\" value=\"false\">No</option></select>";
}
echo "
</tr>
<tr>
	<td style=\"width: 200px; text-align: right;\">Command Prefix</td>
	<td><input name=\"commandPrefix\" type=\"text\" value=\"" . $channelObj["commandPrefix"] . "\" size=\"5\"/></td>
</tr>
<tr>
	<td style=\"width: 200px; text-align: right;\">Message Prefix</td>
	<td><input name=\"msgPrefix\" type=\"text\" value=\"" . $channelObj['msgPrefix'] . "\"></td>
</tr>
<tr>
	<td style=\"width: 200px; text-align: right;\">Message Suffix</td>
	<td><input name=\"msgSuffix\" type=\"text\" value=\"" . $channelObj['msgSuffix'] . "\"></td>
</tr>
<tr>
	<td style=\"width: 200px; text-align: right;\">Language</td>
	<td><select name=\"lang\"><option value\"enUS\">English - US</option></select></td>
</tr>
<tr>
	<td style=\"width: 200px; text-align: right;\">Log Chat</td>
	<td>";
if($channelObj['logChat'] === "true") {
	echo "<select id=\"logChat\"><option selected=\"selected\" value=\"true\">Yes</option><option value=\"false\">No</option></select>";
} else {
	echo "<select id=\"logChat\"><option value=\"true\">Yes</option><option selected=\"selected\" value=\"false\">No</option></select>";
}
echo "
<tr>
	<td style=\"text-align: center;\" colspan=\"2\"><input type=\"submit\" value=\"Save Changes\" /></td>
</tr>
</table>



<h1>Commands</h1>
<table class=\"settingsCmds minTable table34\">
<tr>
	<td colspan=\"6\">Base Commands</td>
</tr>";

$baseCmdsObj = getBaseCommands();
asort($baseCmdsObj);
foreach($baseCmdsObj as $key=>$val) {
	echo "<tr><td colspan=\"6\">$key</td></tr>";
	echo "<tr>
			<td>Trigger</td><td><input type=\"text\" name=\"trigger" . $key . "\" value=\"".$val['trigger']."\" /></td>
			<td>Rank</td><td><input type=\"number\" name=\"rank".$key."\" value=\"".$val['rank']."\" /></td>
			<td>Active</td><td><input type=\"checkbox\" name=\"availability".$key."\" checked=\"".$val['availability']."\" /></td>
		  </tr>";
}


echo "
<tr>
	<td style=\"text-align: center;\" colspan=\"2\"><input type=\"submit\" value=\"Save Changes\" /></td>
</tr>
</table>

<table class=\"minTable table34\">
<tr>
	<th colspan=\"3\">Custom Commands</th>
</tr>

<tr>
	<td style=\"text-align: center;\" colspan=\"2\"><input type=\"submit\" value=\"Save Changes\" /></td>
</tr>
</table>

<h1>Timers</h1>
<table class=\"minTable table34\">


<tr>
	<td style=\"text-align: center;\" colspan=\"2\"><input type=\"submit\" value=\"Save Changes\" /></td>
</tr>
</table>

<h1>Filters</h1>
<table class=\"minTable table34\">


<tr>
	<td style=\"text-align: center;\" colspan=\"2\"><input type=\"submit\" value=\"Save Changes\" /></td>
</tr>
</table>

</form>
";

?>