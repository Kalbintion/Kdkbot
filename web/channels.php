<?PHP
	if(isset($_GET['clearCache'])) {
		if($_GET['clearCache'] == "true") {
			// We need to clear image cache before doing anything
			$files = scandir("./imgs-cache/");
			foreach($files as $file) {
				if(!in_array($file, ["..", "."])) {
					unlink("./imgs-cache/".$file);
				}
			}
		}
	}

	if(!isset($_GET['c'])) {
		echo "<br /><span></span>";
	
		// We need to list the channels for the bot
		$channels = "";
		$fil = file_get_contents("../cfg/settings.cfg");
		$lines = explode("\r\n", $fil);
		
		foreach ($lines as $line) {
			if(substr($line, 0, strlen("channels=")) === "channels=") {
				$channels = $line;
			}
		}
		
		$channels = str_replace("channels=", "", $channels);
		$eChannels = explode(",", $channels);
		
		$cfgFile = file_get_contents("../cfg/settings.cfg");
		$cfgFileLines = explode("\r\n", $cfgFile);
		
		foreach($cfgFileLines as $cfgFileLine) {
			if(substr($cfgFileLine, 0, strlen("clientId=")) === "clientId=") { 
				$clientID = explode("=", $cfgFileLine,2 )[1];
			}
		}

		sort($eChannels);
		
		foreach ($eChannels as $channel) {
			$channel = str_replace("#", "", $channel);
			
			if(file_exists("./imgs-cache/$channel.png")) {
				$imgURL = "./imgs-cache/$channel.png";
			} else {
				// We need to download the profile image to cache, less calls to the twitch api later, faster page load
				$ch = curl_init();
				$url = 'https://api.twitch.tv/kraken/channels/' . $channel;
	
				$ch = curl_init($url);
				curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
				curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
				curl_setopt($ch, CURLOPT_HTTPHEADER, array(
				    'Accept: application/vnd.twitchtv.v3+json',
				    'Client-ID: ' . $clientID,
			    ));
				
				$profileData = json_decode(curl_exec($ch), true);
				curl_close($ch);
				
				// $debugData .= "$channel profile data: " . print_r($profileData, true) . "\r\n";
				
				if(isset($profileData["logo"])) {
					$imgURL = $profileData["logo"];
				} else {
					$imgURL = "https://static-cdn.jtvnw.net/jtv-static/404_preview-45x45.png";
				}
				
				// We know they have(n't) a logo, so lets grab it
				$fp = fopen('./imgs-cache/'.$channel.'.png', 'w+');			
				$ch = curl_init($imgURL);
				
				curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
				curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
				
				$data = curl_exec($ch);
				fwrite($fp, $data);
				fclose($fp);
				curl_close($ch);
			}
			
			echo "
				<div class=\"boxChannel\">
					<center><img width=\"50px\" height=\"50px\" src=\"$imgURL\"></center><br />
					<center><a href=\"http://twitch.tv/$channel\">$channel</a><br />
						<a href=\"?p=channels&c=$channel&t=c\">Commands</a><br />
						<a href=\"?p=channels&c=$channel&t=q\">Quotes</a><br />
						<a href=\"?p=channels&c=$channel&t=s\">Stats</a>
					</center><br />
				</div>";
		}
		echo "<hr style=\"clear:both\"><span>There are currently " . count($eChannels) . " channels with kdkbot inside.</span>";
	} else {
		// We have a channel selected, lets see what type of info we need to load
		if(isset($_GET['t'])) {
			if($_GET['t'] == "c") {
				// Commands
				require('channels_commands.php');
			} elseif($_GET['t'] == "q") {
				// Quotes
				require('channels_quotes.php');
			} elseif($_GET['t'] == "s") {
				// Stats
				require('channels_stats.php');
			}
		} else {
			require('error.php');
		}
	}
?>