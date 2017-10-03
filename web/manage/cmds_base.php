<?php
isUserLoggedIn()or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

function getBaseCommands() {
	$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);
	$contents = file_get_contents($channel->pathChannel());
	
	$lines = explode("\r\n", $contents);
	asort($lines);
	
	$channelObj = [];
	
	foreach($lines as $line) {
		$parts = explode("=", $line, 2);
		if(count($parts) >= 2) {
			if(strpos($parts[0], "trigger") !== false || strpos($parts[0], "availability") !== false || strpos($parts[0], "rank") !== false) {
				// We have an actual command setting
				$cmdName = str_replace("trigger", "", $parts[0]);
				$cmdName = str_replace("rank", "", $cmdName);
				$cmdName = str_replace("availability", "", $cmdName);
				$cmdPart = "";
				if(strpos($parts[0], "trigger") !== false) {
					$cmdPart = "trigger";
				} elseif(strpos($parts[0], "availability") !== false) {
					$cmdPart = "availability";
				} elseif(strpos($parts[0], "rank") !== false) {
					$cmdPart = "rank";
				}
				$channelObj[$cmdName][$cmdPart] = $parts[1];
			}
		}
	}
	
	return $channelObj;
}

echo "<br />
";

if(isset($_POST['update'])) { 
	// echo "UPDATE REQUEST FOUND"; echo "<br><pre>".print_r($_POST,true)."</pre>";
	
	$output = array();
	
	$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);
	$contents = file_get_contents($channel->pathChannel());
	
	$lines = explode("\r\n", $contents);
	asort($lines);
	
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
	
	if(file_put_contents($channel->pathChannel(), $output_content) === false) {
		echo "<div class=\"boxError\">Couldn't update base commands. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated base commands.</div><br />";
		qChannelUpdate($_SESSION['USER'], "cmds_base");
	}
}

echo "
<form action=\"?p=manage/cmds_base\" method=\"post\">
<input type=\"hidden\" value=\"".$_SESSION['USER']."\" name=\"update\" />
<h1>Channel Information For: " . $_SESSION['USER'] . "</h1>

<h1>Base Commands</h1>
<table class=\"settingsCmds inputs2 minTable\">";

$baseCmdsObj = getBaseCommands();
asort($baseCmdsObj);
foreach($baseCmdsObj as $key=>$val) {
	echo "<tr><td colspan=\"6\"><h3>$key</h3></td></tr>";
	echo "<tr>
			<td>Trigger</td><td><input type=\"text\" name=\"trigger" . $key . "\" value=\"".$val['trigger']."\" /></td>
			<td>Rank</td><td><input type=\"number\" name=\"rank".$key."\" value=\"".$val['rank']."\" /></td>
			<td>Active</td><td><input type=\"checkbox\" name=\"availability".$key."\" checked=\"".$val['availability']."\" /></td>
		  </tr>";
}


echo "
<tr>
	<td style=\"text-align: center;\" colspan=\"6\"><input type=\"submit\" value=\"Save Changes\" /></td>
</tr>
</table>
";

?>