<?php 
isUserLoggedIn() or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

if(isset($_POST['update'])) {
	// echo "<pre>".print_r($_POST,true)."</pre>";
	
	$output = "";
	for($i = 0; $i < count($_POST['question']); $i++) {
		if($_POST['question'][$i] == "") { continue; }
		$output .= $_POST['question'][$i]."\r\n";
	}
	
	if(file_put_contents($channel->pathAMA(), $output) === false) {
		echo "<div class=\"boxError\">Couldn't update AMA questions. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated AMA questions.</div><br />";
		qChannelUpdate($_SESSION['USER'], "ama");
	}
}

$ama_contents = file_get_contents($channel->pathAMA());
$amas = explode("\r\n", $ama_contents);

// echo "<pre>".print_r($amas,true)."</pre>";

// Question
echo "
	<div class=\"boxWarning\">To remove a question, Make it blank.</div><br />
		
	<form action=\"?p=manage/ama\" method=\"POST\">
	<input type=\"hidden\" value=\"" . $_SESSION['USER'] . "\" name=\"update\" />
	<table class=\"table34 minTable inputs2\" id=\"amaTable\">
	<tr>
		<th style=\"width:80%;\">Question</th>
	</tr>";
		
	$i = 0; $j = 0;
	foreach($amas as $ama) {
		$i++; $j++;
		if($ama != "") {
			echo "<tr><td><textarea name=\"question[]\">$ama</textarea></td></tr>\r\n";
		}
		
		if($i == 15 && (count($amas) - $j) != 1) {
			echo "<tr>
					<td><input type=\"submit\" value=\"Submit\" /></td>
				</tr>";
			$i = 0;
		}
	}
	
echo "
		<tr>
			<td><input type=\"submit\" value=\"Submit\" /></td>
		</tr>
		<tr>
			<td colspan=\"2\"><input type=\"button\" value=\"Add Question\" onClick=\"addNewAMA()\" /></td>
		</tr>
	</table>
	</form>
";
?>