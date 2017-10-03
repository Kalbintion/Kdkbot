<?PHP
isUserLoggedIn() or die("You need to be logged in to see this page!");
isUserNewsMgr() or die("You are not a news manager of this bot!");

if(isset($_POST['update'])) {
	// We are updating a news item
	$file = $_POST['update'];
	if(file_put_contents("./news-data/" . $file, str_replace("&gt;", ">", str_replace("&lt;", "<", $_POST['content'])))) {
		echo "<div class=\"boxSuccess\">Successfully updated news item.</div><br />";
	} else {
		echo "<div class=\"boxError\">Couldn't update news item. Please try again later.</div><br />";
	}
}

if(isset($_GET['log'])) {
echo "
	<form action=\"?p=news/edit\" method=\"POST\" enctype=\"application/x-www-form-urlencoded\">
	<input type=\"hidden\" value=\"" . $_GET['log'] . "\" name=\"update\" />
	<table class=\"table34 minTable inputs2\">
		<tr>
			<td><textarea rows=\"30\" cols=\"50\" name=\"content\" id=\"content\">". file_get_contents("./news-data/" . $_GET['log']) . "</textarea></td>
		</tr>
		<tr>
			<td><input type=\"submit\" value=\"Update\" onclick=\"encodeText(document.getElementById('content'));true\"></td>
		</tr>
	</table>
	</form>
";
} else {
	$logLoc = "./news-data/";
	$items = scandir($logLoc);
	arsort($items);
	
	echo "<h1>Edit News Items</h1><br />";
	foreach($items as $item) {
		if($item !== ".." && $item !== ".") {
			echo "<a href=\"?p=news/edit&log=".$item."\"><div class=\"logItem\">$item</div></a>";
		}
	}
	
	echo "<div style=\"visibility: hidden; clear: both;\"></div>";
}

?>