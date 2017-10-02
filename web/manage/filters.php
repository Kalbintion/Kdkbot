<?PHP
isUserLoggedIn() or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

if(isset($_POST['update'])) {
	$output = "";
	for($i = 0; $i < count($_POST['title']); $i++) {
		if($_POST['title'][$i] == "") { continue; }
		// Title||Type||Message||Bypassable||Filter
		$output .= $_POST['title'][$i] . "||" . $_POST['type'][$i] . "||" . $_POST['message'][$i] . "||" . $_POST['bypassable'][$i] . "||" . $_POST['filter'][$i] . "\r\n";
	}
	
	if(file_put_contents($channel->pathFilters(), $output) === false) {
		echo "<div class=\"boxError\">Couldn't update Filters. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated Filters.</div><br />";
		qChannelUpdate($_SESSION['USER'], "filters");
	}
}

$filters_contents = file_get_contents($channel->pathFilters());
$filters = explode("\r\n", $filters_contents);
asort($filters);

echo "
<div class=\"boxWarning\">To remove a filter, make the Name field blank.</div><br />

<form action=\"?p=manage/filters\" method=\"POST\">
<input type=\"hidden\" value=\"" . $_SESSION['USER'] . "\" name=\"update\" />
<table class=\"minTable inputs2\" id=\"filtersTable\">
	<tr>
		<th>Name</th>
		<th>Type</th>
		<th>Filter</th>
		<th>Message</th>
		<th>Bypassable?</th>
	</tr>	
";

$i = 0; $j = 0;
	foreach($filters as $filter) {
		$i++; $j++;
		$parts = explode("||", $filter, 5);
		if(count($parts) >=2 ) {
			echo "
			<tr>
				<td><input type=\"text\" name=\"title[]\" value=\"" . $parts[0] . "\"></td>
				<td><select name=\"type[]\">";
				if($parts[1] == "0") {
					echo "<option value=\"0\" selected>None</option><option value=\"1\">Purge</option><option value=\"2\">Timeout</option><option value=\"3\">Ban</option><option value=\"4\">Message</option>";
				} elseif($parts[1] == "1") {
					echo "<option value=\"0\">None</option><option value=\"1\" selected>Purge</option><option value=\"2\">Timeout</option><option value=\"3\">Ban</option><option value=\"4\">Message</option>";
				
				} elseif($parts[1] == "2") {
					echo "<option value=\"0\">None</option><option value=\"1\">Purge</option><option value=\"2\" selected>Timeout</option><option value=\"3\">Ban</option><option value=\"4\">Message</option>";
				
				} elseif($parts[1] == "3") {
					echo "<option value=\"0\">None</option><option value=\"1\">Purge</option><option value=\"2\">Timeout</option><option value=\"3\" selected>Ban</option><option value=\"4\">Message</option>";
				
				} elseif($parts[1] == "4") {
					echo "<option value=\"0\">None</option><option value=\"1\">Purge</option><option value=\"2\">Timeout</option><option value=\"3\">Ban</option><option value=\"4\" selected>Message</option>";
				
				}
				echo "</select></td>
				<td><input type=\"text\" name=\"filter[]\" value=\"" . $parts[4] . "\"></td>
				<td><input type=\"text\" name=\"message[]\" value=\"" . $parts[2] . "\"></td>
				<td><select name=\"bypassable[]\">";
				
				if($parts[3] == "true") {
					echo "<option value=\"true\" selected>Yes</option><option value=\"false\">No</option>";
				} else {
					echo "<option value=\"true\">Yes</option><option value=\"false\" selected>No</option>";
				}
				
				echo "</select></td>
			</tr>";
		}
		
		if($i == 15 && (count($filters) - $j) != 1) {
			echo "<tr><td colspan=\"5\"><input type=\"submit\" value=\"Submit\" /></td></tr>";
			$i = 0;
		}
	}

echo "<tr><td colspan=\"5\"><input type=\"submit\" value=\"Submit\" /></td></tr>
		<tr>
			<td colspan=\"5\"><input id=\"bottom\" type=\"button\" value=\"Add Filter\" onClick=\"addNewFilter()\" /></td>
		</tr>";
	
echo "</table></form>";
?>