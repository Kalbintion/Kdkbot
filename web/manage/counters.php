<?PHP
isUserLoggedIn() or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

if(isset($_POST['update'])) {
	$output = "";
	for($i = 0; $i < count($_POST['name']); $i++) {
		if($_POST['name'][$i] == "") { continue; }
		// Name|Value
		$output .= $_POST['name'][$i] . "|" . $_POST['value'][$i] . "\r\n";
	}
	
	if(file_put_contents($channel->pathCounters(), $output) === false) {
		echo "<div class=\"boxError\">Couldn't update Counters. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated Counters.</div><br />";
		qChannelUpdate($_SESSION['USER'], "filters");
	}
}

$counters_contents = file_get_contents($channel->pathCounters());
$counters = explode("\r\n", $counters_contents);
asort($counters);

echo "
<div class=\"boxWarning\">To remove a counter, make the Name field blank.</div><br />

<form action=\"?p=manage/counters\" method=\"POST\">
<input type=\"hidden\" value=\"" . $_SESSION['USER'] . "\" name=\"update\" />
<table class=\"minTable inputs2\" id=\"countersTable\">
	<tr>
		<th>Name</th>
		<th>Value</th>
	</tr>	
";

$i = 0; $j = 0;
	foreach($counters as $counter) {
		$i++; $j++;
		$parts = explode("|", $counter, 2);
		if(count($parts) >=2 ) {
			echo "
			<tr>
				<td><input type=\"text\" name=\"name[]\" value=\"" . $parts[0] . "\" /></td>
				<td><input type=\"number\" name=\"value[]\" value=\"" . $parts[1] . "\" /></td>
			</tr>";
		}
		
		if($i == 15 && (count($filters) - $j) != 1) {
			echo "<tr><td colspan=\"5\"><input type=\"submit\" value=\"Submit\" /></td></tr>";
			$i = 0;
		}
	}

echo "<tr><td colspan=\"2\"><input type=\"submit\" value=\"Submit\" /></td></tr>
<tr>
			<td colspan=\"2\"><input id=\"bottom\" type=\"button\" value=\"Add Counter\" onClick=\"addNewCounter()\" /></td>
		</tr>";
	
echo "</table></form>";
?>