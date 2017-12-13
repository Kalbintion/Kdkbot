<?php
isUserLoggedIn()or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

if(isset($_POST['update'])) {
	$output_content = "";
	
	for($i=0; $i < count($_POST['commands']); $i++) {
		if($_POST['values'][$i] !== "0" && $_POST['values'][$i] !== "") {
			$output_content .= $_POST['commands'][$i] . "=" . $_POST['values'][$i] . "\r\n";
		}
	}

	if(file_put_contents($channel->pathEconomyCommands(), $output_content) === false) {
		echo "<div class=\"boxError\">Couldn't update economy command prices. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated economy command prices.</div><br />";
		qChannelUpdate($_SESSION['USER'], "economy");
	}
}

$cmds_data = file_get_contents($channel->pathEconomyCommands());
$cmds = array();
$lines = explode("\r\n", $cmds_data);
asort($lines);
foreach($lines as $line) {
	$parts = explode("=", $line, 2);
	if(count($parts) >= 2) {
		$cmds[$parts[0]] = $parts[1];
	}
}

echo "
<form action=\"?p=manage/economy/commands\" method=\"post\">
<input type=\"hidden\" value=\"".$_SESSION['USER']."\" name=\"update\" />
<div class=\"boxWarning\">To remove a command cost, either enter '0' or leave the commands price blank.</div><br />
<center><a href=\"?p=manage/economy\">Back</a></center>
<center><h1>Economy Command Settings</h1></center>
<table id=\"econCmdTable\" class=\"inputs settings2 mintable\">
	<tr>
		<th>Command</th>
		<th>Amount</th>
	</tr>";
	
	foreach($cmds as $key => $val) {
		if($val !== "null") {
			echo "<tr><td><input type=\"hidden\" value=\"$key\" name=\"commands[]\">$key</td><td><input type=\"number\" value=\"$val\" name=\"values[]\" /></td></tr>";
		}
	}
	
echo "
	<tr>
		<td colspan=\"2\"><input type=\"submit\" value=\"Save Changes\" /></td>
	</tr>
	<tr>
		<td colspan=\"2\"><input type=\"button\" onClick=\"addNewEconCmd()\" value=\"Add New\" /></td>
	</tr>
</table>
</form>";

?>