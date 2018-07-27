<?php
isUserLoggedIn() or die("You need to log in.");

$channel = getChannelObject($_SESSION['USER']);

if(isset($_POST['update'])) {
	for($i = 0; $i < count($_POST['user']); $i++) {
		if($_POST['user'][$i] == "") { continue; }
		if($_POST['isNew'][$i] == 0) {
		    sql_updateChannelPerm($_SESSION['USER'], $_POST['user'][$i], $_POST['rank'][$i]);
		} elseif ($_POST['isNew'][$i] == 1) {
		    sql_insertChannelPerm($_SESSION['USER'], $_POST['user'][$i], $_POST['rank'][$i]);
		}
	}
	
	echo "<div class=\"boxSuccess\">Successfully updated permissions.</div><br />";
	
	qChannelUpdate($_SESSION['USER'], "perms");
}

$perm_contents = sql_getChannelPerms($_SESSION['USER']);
$perms = array();
while($row = $perm_contents->fetch_assoc()) {
    array_push($perms, $row);
}

echo "
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
        echo "<tr><td><input type=\"hidden\" value=\"0\" name=\"isNew[]\" /><input type=\"text\" name=\"user[]\" value=\"" . $perm['user'] . "\"</td><td><input type=\"number\" name=\"rank[]\" value=\"" . $perm['level'] . "\" /></td></tr>";
		
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
			<td colspan=\"2\"><input id=\"bottom\" type=\"button\" value=\"Add User\" onClick=\"addNewUser()\" /></td>
		</tr>
	</table>
	</form>
"

?>