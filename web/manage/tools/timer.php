<?PHP
isUserLoggedIn() or die("You need to be logged in to see this page!");

echo "
<script>
function formatURL() {
	url_display = document.getElementById('timerURL');
	timer_name = document.getElementById('name');
	timer_time = document.getElementById('time');
	timer_style = document.getElementById('style');
	
	url_display.value = \"http://tfk.zapto.org/kdkbot/tools/timers?time=\" + timer_time.value + \"&style=\" + btoa(timer_style.value);
}
</script>

<h1><a href=\"?p=manage\">Manage</a> → <a href=\"?p=manage/tools\">Tools</a> → <a href=\"?p=manage/tools/timer\">Timers</a></h1>
<hr>
<div>
<a href=\"?p=manage/tools/timer&m=n\"><div class=\"menuItem\"><img src=\"./imgs/plus-4-xxl.png\" style=\"width: 20px; height: 20px;\" /> New</div></a>";
if(isset($_GET['m']) && $_GET['m'] == "e") {
	echo "<a href=\"?p=manage/tools/timer&delete=" . $_GET['i'] . "&m=s\"><div class=\"menuItem\"><img src=\"./imgs/Minus_icon.png\" style=\"wdith: 20px; height: 20px;\" /> Delete</div></a>";
}
echo "</div>
<hr style=\"clear: both;\">
";

$USER = $_SESSION['USER'];
$file_data = file_get_contents("./tools-data/$USER/timers.kdk");
$lines = explode("\r\n", $file_data);
$items = array();
foreach($lines as $line) {
	$parts = explode("|", $line);
	if(count($parts) == 3) {
		$items[$parts[0]]["name"] = $parts[0];
		$items[$parts[0]]["time"] = $parts[1];
		$items[$parts[0]]["style"] = $parts[2];
	}
}

if(isset($_GET['delete'])) {
	unset($items[$_GET['delete']]);
	$_POST['name'] = $_GET['delete'];
}

if(isset($_GET['m']) && $_GET['m'] == "s") {
    echo "<pre>".print_r($items, true)."</pre>";
    echo "<pre>".print_r($_POST, true)."</pre>";
    
	// Modify array data
	if(isset($_POST['name']) && isset($_POST['time']) && isset($_POST['style'])) {
		$items[$_POST['name']]["name"] = $_POST['name'];
		$items[$_POST['name']]["time"] = $_POST['time'];
		$items[$_POST['name']]["style"] = base64_encode($_POST['style']);
	}
	
	// Re-save data
	$out_data = "";
	foreach($items as $value) {
	    $out_data .= $value["name"] . "|" . $value["time"] . "|" . $value["style"] . "\r\n";
	}
	
	if(file_put_contents("./tools-data/$USER/timers.kdk", $out_data) === false) {
	    echo "<div class=\"boxError\">Could not update timer: " . $_POST['name'] . "</div>"; // Fail
	} else {
	    echo "<div class=\"boxSuccess\">Successfully updated timer: " . $_POST['name'] . "</div>"; // Success
	}
}

if(isset($_GET['m']) && $_GET['m'] !== "s") {
	if($_GET['m'] == "n") {
		echo "<form action=\"?p=manage/tools/timer&m=s\" method=\"POST\"><table class=\"inputs settings minTable \">
				<tr>
					<td>Name:</td>
					<td><input type=\"text\" onKeyPress=\"formatURL()\" onChange=\"formatURL()\" id=\"name\" name=\"name\" placeholder=\"Must be unique...\" value=\"\"></td>
				</tr>
				<tr>
					<td>Time:</td>
					<td><input type=\"number\" onKeyPress=\"formatURL()\" onChange=\"formatURL()\" id=\"time\" name=\"time\" placeholder=\"in seconds\"></td>
				</tr>
				<tr>
					<td>Style:</td>
					<td><textarea name=\"style\" onKeyPress=\"formatURL()\" onChange=\"formatURL()\" id=\"style\"  cols=\"50\" rows=\"10\" placeholder=\"CSS Styling Code\"></textarea></td>
				</tr>
				<tr>
					<td colspan=\"2\"><input type=\"submit\" value=\"Save\"></td>
				</tr>
				<tr>
					<td>URL:</td>
					<td><input type=\"text\" id=\"timerURL\" value=\"\" disabled></input></td>
				</tr>
			  </table></form>";
	} elseif ($_GET['m'] == "e") {
	    // TODO: Cause the formatUrl() function to proc once on load
		echo "<form action=\"?p=manage/tools/timer&m=s\" method=\"POST\"><table class=\"inputs settings minTable\">
				<tr>
					<td>Name:</td>
					<td><input type=\"text\" onKeyPress=\"formatURL()\" onChange=\"formatURL()\" id=\"name\" name=\"name\" placeholder=\"Must be unique...\" value=\"".$items[$_GET['i']]['name']."\"></td>
				</tr>
				<tr>
					<td>Time:</td>
					<td><input type=\"number\" onKeyPress=\"formatURL()\" onChange=\"formatURL()\" id=\"time\" name=\"time\" value=\"".$items[$_GET['i']]['time']."\" placeholder=\"in seconds\"></td>
				</tr>
				<tr>
					<td>Style:</td>
					<td><textarea name=\"style\" onKeyPress=\"formatURL()\" onChange=\"formatURL()\" cols=\"50\" rows=\"10\" placeholder=\"CSS Styling Code\" id=\"style\" >".base64_decode($items[$_GET['i']]['style'])."</textarea></td>
				</tr>
				<tr>
					<td colspan=\"2\"><input type=\"submit\" value=\"Update\"></td>
				</tr>
				<tr>
					<td>URL:</td>
					<td><input type=\"text\" id=\"timerURL\" value=\"\" disabled></input></td>
				</tr>
			  </table></form>";
	}
} else {
    // TODO: Show no saved timer message if there are none.
	foreach($items as $value) {
		echo "<a href=\"?p=manage/tools/timer&m=e&i=".$value['name']."\"><div class=\"logItem\">".$value['name']."</div></a>";
	}
}
?>