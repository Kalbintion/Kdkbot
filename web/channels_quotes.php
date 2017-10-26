<?PHP
	$filPath = "../cfg/#" . $_GET['c'] . "/quotes.cfg";
	if(file_exists($filPath)) {
		$fil = file_get_contents($filPath);
		$quotes_contents = explode("\r\n", $fil);
		$quotes = array();
		foreach ($quotes_contents as $quotes_line) {
			if($quotes_line !== "") {
			$parts = explode(": ", $quotes_line, 2);
			$id = $parts[0];
			$quotes[$id]["id"] = $id;
			$quotes[$id]["msg"] = $parts[1];
			}
		}
		sort($quotes);
		
		echo "<br /><h1>Quotes list for " . ucwords(strtolower($_GET['c'])) . "</h1>";
		echo "<table class=\"minTable\"><tr><th>ID</th><th>Quote Text</th></tr>";
		
		foreach ($quotes as $key => $val) {
			if($key !== "") {
				echo "<tr><td>" . $val["id"] . "</td><td>" . $val["msg"] . "</td></tr>";
			}
		}
		if(count($quotes) <= 1) { 
			echo "<tr><td colspan=\"2\">There are no quotes for this channel :(</td></tr>";
		}
		echo"</table>";
	} else {
		require("error.php");
	}
?>