<?php
isUserLoggedIn()or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

if(isset($_POST['update'])) { 
	$output = array();
	
	$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);
	$contents = file_get_contents($channel->pathEconomy());
	
	$lines = explode("\r\n", $contents);
	
	foreach($lines as $line) {
		$parts = explode("=", $line, 2);
		if(count($parts) >= 2) {
			$output[$parts[0]] = $parts[1];
		}
	}
	
	foreach($_POST as $varName => $varValue) {
		if($varName == "update") { continue; }
		$output[$varName] = $_POST[$varName];
	}
	
	$output_content = "";
	
	foreach($output as $key => $val) {
		$output_content .= "$key=$val\r\n";
	}

	if(file_put_contents($channel->pathEconomy(), $output_content) === false) {
		echo "<div class=\"boxError\">Couldn't update economy settings. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated economy settings.</div><br />";
		qChannelUpdate($_SESSION['USER'], "economy");
	}
}

$settings_data = file_get_contents($channel->pathEconomy());
$settings = array();
$lines = explode("\r\n", $settings_data);
foreach($lines as $line) {
	$parts = explode("=", $line, 2);
	if(count($parts) >= 2) {
		$settings[$parts[0]] = $parts[1];
	}
}

echo "
<form action=\"?p=manage/economy/settings\" method=\"post\">
<input type=\"hidden\" value=\"".$_SESSION['USER']."\" name=\"update\" />
<center><a href=\"?p=manage/economy\">Back</a></center>
<center><h1>Economy Settings</h1></center>
<table class=\"inputs settings2 mintable\">
	<tr>
		<th colspan=\"2\"><h2>Basic Settings</h2></th>
	</tr>
	<tr>
		<th>Currency Name:&nbsp;</th>
		<td><input type=\"text\" name=\"name\" value=\"" . $settings['name'] . "\" /></td>
	</tr>
	<tr>
		<th>Symbol:&nbsp;</th>
		<td><input type=\"text\" name=\"symbol\" value=\"" . $settings['symbol'] . "\" /></td>
	</tr>
	<tr>
		<th>Symbol In Back:&nbsp;</th>
		<td><select name=\"symbol-in-back\">";
		
		if($settings['symbol-in-back'] == "false") {
			echo "<option value=\"true\">Yes</option><option value=\"false\" selected>No</option>";
		} else {
			echo "<option value=\"true\">Yes</option><option value=\"false\">No</option>";
		}
		
echo "		
			</select></td>
	</tr>
	<tr>
		<th colspan=\"2\"><h2>Amounts</h2></th>
	</tr>
	<tr>
		<th>Per Join</th>
		<td><input type=\"number\" name=\"amountPerJoin\" value=\"" . $settings['amountPerJoin'] . "\" /></td>
	</tr>
	<tr>
		<th>Per Leave</th>
		<td><input type=\"number\" name=\"amountPerLeave\" value=\"" . $settings['amountPerLeave'] . "\" /></td>
	</tr>
	<tr>
		<th>Per Message</th>
		<td><input type=\"number\" name=\"amountPerLine\" value=\"" . $settings['amountPerLine'] . "\" /></td>
	</tr>
	<tr>
		<th>Per Minute</th>
		<td><input type=\"number\" name=\"amountPerMinute\" value=\"" . $settings['amountPerMinute'] . "\" /></td>
	</tr>
	<tr>
		<th>Per Word</th>
		<td><input type=\"number\" name=\"amountPerWord\" value=\"" . $settings['amountPerWord'] . "\" /></td>
	</tr>
	<tr>
		<th>Per Letter</th>
		<td><input type=\"number\" name=\"amountPerLetter\" value=\"" . $settings['amountPerLetter'] . "\" /></td>
	</tr>
	
	<tr>
		<th>Per Link</th>
		<td><input type=\"number\" name=\"amountPerLink\" value=\"" . $settings['amountPerLink'] . "\" /></td>
	</tr>
	<tr>
		<td colspan=\"2\"><input type=\"submit\" value=\"Save Changes\" /></td>
	</tr>
</table>
</form>";

?>