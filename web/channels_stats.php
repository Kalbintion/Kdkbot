<?PHP
	$filPath = "../cfg/#" . $_GET['c'] . "/stats.cfg";
	if(file_exists($filPath)) {
		$fil = file_get_contents($filPath);
		$stats = explode("\r\n", $fil);
		asort($stats);
		// return (this.userName + ":" + this.firstJoin + ":" + this.timeSpent + ":" + this.messageCount + ":" + 
		//         this.lastJoin + ":" + this.lastLeave + ":" + this.characterCount + ":" + this.bitsCount + ":" + this.bitsDate);
		
		echo "<br /><center><p>Stats list for " . ucwords(strtolower($_GET['c'])) . ". Total number of users: " . count($stats) . " </p>";

		
		$idx_start = 0;
		$idx_limit = 200;
		if(isset($_GET['start'])) { $idx_start = $_GET['start']; }
		if(isset($_GET['limit'])) { $idx_limit = $_GET['limit']; }
		
		$tCount = count($stats);
		$tOffset = 1;
		echo "Page: ";
		while($tCount > 0 ) {
			if($idx_start == (($tOffset - 1) * $idx_limit)) {
				echo "$tOffset ";
			} else {
				echo "<a href=\"?p=channels&c=" . $_GET['c'] . "&t=" . $_GET['t'] . "&start=" . (($tOffset - 1) * $idx_limit) . "\">$tOffset</a> ";
			}
			
			$tOffset++;
			$tCount -= $idx_limit;
		}
		echo "</center><br />";
		
		echo "<table class=\"minTable borderHeader\">
				<tr>
					<th>User</th>
					<th>First Join<br />(DD/MM/YYYY)</th>
					<th>Time Spent</th>
					<th>Message<br />Count</th>
					<th>Character<br />Count</th>
					<th>Bits Count</th>
					<th>Last Seen<br />(DD/MM/YYYY)</th>
				</tr>";
		for ($i = $idx_start; $i <= $idx_start + $idx_limit; $i++) {
			if(isset($stats[$i]) && $stats[$i] !== "") {
				$stat_segments = explode(":", $stats[$i]);
				echo "<tr>
						<td>" . $stat_segments[0] . "</td>
						<td>" . implode("<br />", explode(" ", unixToTimestamp($stat_segments[1]), 2)) . "</td>
						<td>" . timeFromSeconds($stat_segments[2]) . "</td>
						<td>" . $stat_segments[3] . "</td>
						<td>" . $stat_segments[6] . "</td>
						<td>" . $stat_segments[7] . "</td>
						<td>" . implode("<br />", explode(" ", unixToTimestamp($stat_segments[5]), 2)). "</td>
					</tr>";
			}
		}
		if(count($stats) <= 1) { 
			echo "<tr><td colspan=\"2\">There are no quotes for this channel :(</td></tr>";
		}
		echo"</table>";
	} else {
		require("error.php");
	}
	
	function unixToTimestamp($time) {
		return date("d/m/Y h:i:s A", $time / 1000);
	}
	
	function timeFromSeconds($time) {
		$seconds = $time % 60;
		$minutes = $time / 60 % 60;
		$hours = $time / 60 / 60 % 60;
		$days = floor($time / 60 / 60 / 24);
		
		$out = $days . "D " . $hours . "H " . $minutes . "M " . $seconds . "S";
		
		return $out;
	}
?>