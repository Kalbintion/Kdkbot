<?php
isUserLoggedIn() or die("You need to be logged in to see this page!");

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

$channelObj = getChannelSettings();

if(isset($_POST['update'])) { echo "UPDATE REQUEST FOUND"; echo "<br><pre>".print_r($_POST,true)."</pre>"; }

echo "
<br />
<form action=\"?p=manage/channel\" method=\"post\">
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
</table>";
?>