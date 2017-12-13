<?php
isUserLoggedIn()or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

if(isset($_POST['update'])) {
	$output_content = "";
	
	for($i=0; $i < count($_POST['commands']); $i++) {
		$output_content .= $_POST['commands'][$i] . "=" . $_POST['values'][$i] . "\r\n";
	}

	if(file_put_contents($channel->pathEconomyTitles(), $output_content) === false) {
		echo "<div class=\"boxError\">Couldn't update economy title prices. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated economy title prices.</div><br />";
		qChannelUpdate($_SESSION['USER'], "economy");
	}
}

$titles_data = file_get_contents($channel->pathEconomyTitles());
$titles = array();
$lines = explode("\r\n", $titles_data);
asort($lines);
foreach($lines as $line) {
	$parts = explode("=", $line, 2);
	if(count($parts) >= 2) {
		$titles[$parts[0]] = $parts[1];
	}
}

echo "
<form action=\"?p=manage/economy/title\" method=\"post\">
<input type=\"hidden\" value=\"".$_SESSION['USER']."\" name=\"update\" />
<center><a href=\"?p=manage/economy\">Back</a></center>
<center><h1>Economy User Settings</h1></center>
<table class=\"inputs settings2 mintable\">
	<tr>
		<th>Command</th>
		<th>Amount</th>
	</tr>";
	
	foreach($titles as $key => $val) {
		echo "<tr><td><input type=\"hidden\" value=\"$key\" name=\"titles[]\">$key</td><td><input type=\"number\" value=\"$val\" name=\"values[]\" /></td></tr>";
	}
	
echo "
	<tr>
		<td colspan=\"2\"><input type=\"submit\" value=\"Save Changes\" /></td>
	</tr>
</table>
</form>";

?>