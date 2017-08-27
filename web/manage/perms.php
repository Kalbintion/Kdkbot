<?php
isUserLoggedIn() or die("You need to log in.");

$channel = getChannelObject($_SESSION['USER']);

if(isset($_POST['update'])) {
	$output = "";
	for($i = 0; $i < count($_POST['user']); $i++) {
		if($_POST['user'][$i] == "") { continue; }
		$output .= $_POST['user'][$i]."=".$_POST['rank'][$i]."\r\n";
	}
	
	if(file_put_contents($channel->pathPerms(), $output) === false) {
		echo "<div class=\"boxError\">Couldn't update permissions. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated permissions.</div><br />";
		qChannelUpdate($_SESSION['USER'], "perms");
	}
}

$perm_contents = file_get_contents($channel->pathPerms());
$perms = explode("\r\n", $perm_contents);
asort($perms);

// echo "<pre>".print_r($perms,true)."</pre>";

echo "
	<div class=\"boxWarning\">To remove a person, make the User field blank.</div><br />

	<form action=\"?p=manage/perms\" method=\"POST\">
	<input type=\"hidden\" value=\"" . $_SESSION['USER'] . "\" name=\"update\" />
	<table class=\"minTable inputs2\" id=\"permTable\">
		<tr>
			<th>User</th>
			<th>Rank</th>
		</tr>";

$i = 0; $j = 0;
	foreach($perms as $perm) {
		$i++; $j++;
		$parts = explode("=", $perm, 2);
		if(count($parts) >=2 ) {
			echo "<tr><td><input type=\"text\" name=\"user[]\" value=\"" . $parts[0] . "\"></td><td><input type=\"number\" name=\"rank[]\" value=\"" . $parts[1] . "\" /></td></tr>";
		}
		
		if($i == 15 && (count($perms) - $j) != 1) {
			echo "<tr>
			<td colspan=\"2\"><input type=\"submit\" value=\"Submit\" /></td>
		</tr>";
			$i = 0;
		}
	}

echo "
		<tr>
			<td colspan=\"2\"><input type=\"submit\" value=\"Submit\" /></td>
		</tr>
		<tr>
			<td colspan=\"2\"><input id=\"bottom\" type=\"button\" value=\"Add Command\" onClick=\"addNewUser()\" /></td>
		</tr>
	</table>
	</form>
"

?>