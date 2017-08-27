<?php 
isUserLoggedIn() or die("You need to log in.");

$channel = getChannelObject($_SESSION['USER']);

if(isset($_POST['update'])) {
	$output = "";
	for($i = 0; $i < count($_POST['trigger']); $i++) {
		if($_POST['trigger'][$i] == "") { continue; }
		$output .= $_POST['rank'][$i]."|".$_POST['active'][$i]."|".$_POST['trigger'][$i]."|".$_POST['message'][$i]."\r\n";
	}
	
	if(file_put_contents($channel->pathCommands(), $output) === false) {
		echo "<div class=\"boxError\">Couldn't update commands. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated commands.</div><br />";
		qChannelUpdate($_SESSION['USER'], "cmds_cust");
	}
}


$commands_contents = file_get_contents($channel->pathCommands());
$commands = explode("\r\n", $commands_contents);


// Rank | Active | Trigger | Message
echo "
	<div class=\"boxWarning\">To delete a command, make the trigger field blank.</div><br />

	<form action=\"?p=manage/cmds_custom\" method=\"post\">
	<input type=\"hidden\" value=\"" . $_SESSION['USER'] . "\" name=\"update\" />
	<table class=\"minTable inputs table34\" id=\"cmdTable\">
		<tr>
			<td colspan=\"4\"><a href=\"#bottom\">Go To Bottom</a></td>
		</tr>
		<tr>
			<td colspan=\"4\"><input type=\"submit\" value=\"Submit\" /></td>
		</tr>
		<tr>
			<th>Trigger</th>
			<th>Message</th>
			<th style=\"width: 100px;\">Rank</th>
			<th>Active</th>
		</tr>
";

$i = 0; $j = 0;
foreach($commands as $command) {
	$i++; $j++;
	$parts = explode("|", $command, 4);
	if(count($parts) < 4) { continue; }
	echo "
		<tr>
			<td><input name=\"trigger[]\" type=\"text\" value=\"" . $parts[2] . "\"></td>
			<td><input name=\"message[]\" type=\"text\" value=\"" . $parts[3] . "\"></td>
			<td><input name=\"rank[]\" type=\"number\" value=\"" . $parts[0] . "\"></td>
			<td><select name=\"active[]\">";
			if($parts[1] == true) { 
				echo "<option value=\"true\" selected>Yes</option><option value=\"false\">No</option>"; 
			} else { 
				echo "<option value=\"true\">Yes</option><option value=\"false\" selected>No</option>"; 
			}
			echo "</select></td>
		</tr>";

	if($i == 15 && (count($commands) - $j) != 1) {
		echo "<tr>
			<td colspan=\"4\"><input type=\"submit\" value=\"Submit\" /></td>
		</tr>";
		$i = 0;
	}
}

echo "
		<tr>
			<td colspan=\"4\"><input type=\"submit\" value=\"Submit\" /></td>
		</tr>
		<tr>
			<td colspan=\"4\"><input id=\"bottom\" type=\"button\" value=\"Add Command\" onClick=\"addNewCommand()\" /></td>
		</tr>
	</table>
	</form>";

?>