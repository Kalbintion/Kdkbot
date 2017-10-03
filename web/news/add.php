<?PHP
isUserLoggedIn() or die("You need to be logged in to see this page!");
isUserNewsMgr() or die("You are not a news manager of this bot!");

if(isset($_POST['update'])) {
	echo "<pre>".print_r($_POST, true)."</pre>";
	$out = "
			<div class=\"boxNews\">
			<div class=\"boxNewsDate\">";
			
			$date_parts = explode("-", $_POST['date']);
			switch($date_parts[1]) {
				case "01":
					$out .= $date_parts[2] . " Jan, " . $date_parts[0];
					break;
				case "02":
					$out .= $date_parts[2] . " Feb, " . $date_parts[0];
					break;
				case "03":
					$out .= $date_parts[2] . " Mar, " . $date_parts[0];
					break;
				case "04":
					$out .= $date_parts[2] . " Apr, " . $date_parts[0];
					break;
				case "05":
					$out .= $date_parts[2] . " May, " . $date_parts[0];
					break;
				case "06":
					$out .= $date_parts[2] . " Jun, " . $date_parts[0];
					break;
				case "07":
					$out .= $date_parts[2] . " Jul, " . $date_parts[0];
					break;
				case "08":
					$out .= $date_parts[2] . " Aug, " . $date_parts[0];
					break;
				case "09":
					$out .= $date_parts[2] . " Sep, " . $date_parts[0];
					break;
				case "10":
					$out .= $date_parts[2] . " Oct, " . $date_parts[0];
					break;
				case "11":
					$out .= $date_parts[2] . " Nov, " . $date_parts[0];
					break;
				case "12":
					$out .= $date_parts[2] . " Dec, " . $date_parts[0];
					break;
			}
			
	$out .= " - " . $_POST['title'] . "</div>
			<div class=\"boxNewsContent\">". $_POST['content'] . "<br /><br />" . $_POST['sig'] . "</div></div>";
	
	file_put_contents("./news-data/" . $_POST['date'], $out);
}

echo "
	<form action=\"?p=news/add\" method=\"POST\" enctype=\"application/x-www-form-urlencoded\">
	<input type=\"hidden\" value=\"addnews\" name=\"update\" />
	<table class=\"table34 minTable inputs2\">
		<tr>
			<td style=\"text-align: right;\" width=\"100px;\">Date:</td>
			<td><input type=\"text\" name=\"date\" value=\"" . date('Y-m-d') . "\"></td>
		</tr>
		<tr>
			<td style=\"text-align: right;\">Title:</td>
			<td><input type=\"text\" name=\"title\" /></td>
		</tr>
		<tr>
			<td style=\"text-align: right;\">Text:</td>
			<td><textarea rows=\"10\" cols=\"50\" name=\"content\" id=\"content\" /></textarea></td>
		</tr>
		<tr>
			<td style=\"text-align: right;\">Signature:</td>
			<tD><input type=\"text\" name=\"sig\" value=\"- " . $_SESSION['USER'] . "\"></td>
		</tr>
		<tr>
			<td colspan=\"2\"><input type=\"submit\" value=\"Add News\" onclick=\"encodeText(document.getElementById('content'));true\" /></td>
		</tr>
	</table>
	</form>
";

?>