<?php 
isUserLoggedIn() or die("You need to log in.");

$channel = getChannelObject($_SESSION['USER']);

if(isset($_POST['update'])) {
	// echo "<pre>".print_r($_POST,true)."</pre>";
	
	$output = "";
	for($i = 0; $i < count($_POST['id']); $i++) {
		if($_POST['id'][$i] == "") { continue; }
		$output .= $_POST['id'][$i].":  ".$_POST['quote'][$i]."\r\n";
	}
	
	if(file_put_contents($channel->pathQuotes(), $output) === false) {
		echo "<div class=\"boxError\">Couldn't update quotes. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated quotes.</div><br />";
		qChannelUpdate($_SESSION['USER'], "quotes");
	}
	
}


$quote_contents = file_get_contents($channel->pathQuotes());
$quotes = explode("\r\n", $quote_contents);
sort($quotes);

// echo "<pre>".print_r($quotes,true)."</pre>";

// ID:  "Quote"
echo "
	<div class=\"boxWarning\">To remove a quote, make the ID field blank.<br />Caution re-indexing quotes as people may not be aware of the renumbering.</div><br />
		
	<form action=\"?p=manage/quotes\" method=\"POST\">
	<input type=\"hidden\" value=\"" . $_SESSION['USER'] . "\" name=\"update\" />
	<table class=\"minTable inputs2\" id=\"quoteTable\">
	<tr>
		<th>ID</th>
		<th style=\"width:90%;\">Quote</th>
	</tr>";

	$i = 0; $j = 0;
	foreach($quotes as $quote) {
		$i++; $j++;
		$parts = explode(":  ", $quote, 2);
		if(count($parts) >=2 ) {
			echo "<tr><td><input style=\"width: 80px;\" type=\"number\" name=\"id[]\" value=\"" . $parts[0] . "\"></td><td><textarea name=\"quote[]\">" . $parts[1] . "</textarea></td></tr>\r\n";
		}
		
		if($i == 15 && (count($quotes) - $j) != 1) {
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
		<td colspan=\"2\"><input type=\"button\" value=\"Add Quote\" onClick=\"addNewQuote()\" /></td>
	</tr>
	</table>
	</form>";

?>