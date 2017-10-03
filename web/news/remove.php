<?PHP
isUserLoggedIn() or die("You need to be logged in to see this page!");
isUserNewsMgr() or die("You are not a news manager of this bot!");

if(isset($_GET['log'])) {
	if(unlink("./news-data/" . $_GET['log'])) {
		echo "<div class=\"boxSuccess\">Successfully removed news item.</div><br />";
	} else {
		echo "<div class=\"boxError\">Couldn't remove news item. Please try again later.</div><br />";
	
	}
}

$logLoc = "./news-data/";
$items = scandir($logLoc);
arsort($items);

echo "<h1>Remove News Items</h1><br /><div class=\"boxError\">Clicking on a news item below will remove it. This is a permanent thing and cannot be undone.</div>";
foreach($items as $item) {
	if($item !== ".." && $item !== ".") {
		echo "<a href=\"?p=news/remove&log=".$item."\"><div class=\"logItem\">$item</div></a>";
	}
}

echo "<div style=\"visibility: hidden; clear: both;\"></div>";

?>