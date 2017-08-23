<?PHP
	/* Determines if a given string (haystack) contains another string (needle).
	 * Returns true if found, false otherwise
	 */
	function startsWith($haystack, $needle) {
		if(substr($haystack, 0, strlen($needle)) === $needle) {
			return true;
		}
		return false;
	}
	
	function contains($haystack, $needle) {
		if (strpos($haystack, $needle) !== false) {
			return true;
		}
		return false;
	}
	
	/* Determines if a user is logged into the website by verifying the PHPSESSID
	 * with the stored one. And if they do not match, they're not logged in.
	 * 
	 * NOTE: Webservers should disallow external access to /web/temp/ entirely to 
	 *       prevent session ID stealing.
	 */
	function isUserLoggedIn() {
		if(isset($_COOKIE['PHPSESSID']) && isset($_SESSION['USER'])) {
			if(file_exists("./temp/" . $_SESSION['USER'])) {
				$saved_id = file_get_contents("./temp/" . $_SESSION['USER']);
				if($saved_id === $_COOKIE['PHPSESSID']) {
					return true;
				}
			}
		}
		return false;
	}
	
	/* Gets the base configuration setting location found in the settings.ini file
	 * Returns a string of what was found for a particular value. May return null.
	 */
	function getBaseConfigSetting() {
		$ini_contents = parse_ini_file("./cfg/settings.ini", true);
		return $ini_contents["Bot"]["cfgLocation"];
	}
	
	/* 
	 * Gets the base bot configuration settings back into the bot
	 */
	function getBaseConfigContents() {
		return file_get_contents(getBaseConfigSetting() . "\settings.cfg");
	}
	
	/*
	 * Puts the base bot configuration settings back into the bot
	 */
	function putBaseConfigContents($data) {
		file_put_contents(getBaseConfigSetting() . "\settings.cfg", $data);
	}
	
	function qChannelUpdate($channel, $type) {
		$ini_contents = parse_ini_file("./cfg/settings.ini", true);
		$watcher_loc = $ini_contents["Bot"]["watcherLocation"];
		
		$qContents = file_get_contents($watcher_loc);
		$qContents .= "$channel=$type\r\n";
		
		file_put_contents($watcher_loc, $qContents);
	}
	
	function getChannelLocation($channel) {
		if(!startsWith($channel, "#")) {
			$channel = "#" . $channel;
		}
		$path = getBaseConfigSetting() . "\\$channel";
		if(file_exists($path)) {
			return $path;
		} else {
			return false;
		}
	}
	
	function getChannelObject($channel) {
		$path = getChannelLocation($channel);
		if($path === false) {
			return false;
		} else {
			return new Channel($path);
		}
	}
	
	function getLogLocation($channel) {
		$path = getBaseConfigSetting();
		$settings_contents = file_get_contents($path . "\\settings.cfg");
		$lines = explode("\r\n", $settings_contents);
		foreach($lines as $line) {
			$parts = explode("=", $line, 2);
			if($parts[0] === "logChatLocation") {
				return $parts[1] . $channel . "\\";
			}
		}
		return false;
	}
	
	class Channel {
		public $baseLocation;
		
		public function __construct($baseLocation) {
			$this->baseLocation = $baseLocation;
		}
		
		public function pathAMA() {
			return $this->baseLocation . "\\ama.cfg";
		}
		
		public function pathChannel() {
			return $this->baseLocation . "\\channel.cfg";
		}
		
		public function pathCommands() {
			return $this->baseLocation . "\\cmds.cfg";
		}
		
		public function pathCounters() {
			return $this->baseLocation . "\\counters.cfg";
		}
		
		public function pathEconomy() {
			return $this->baseLocation . "\\economy.cfg";
		}
		
		public function pathEconomyUsers() {
			return $this->baseLocation . "\\economy_users.cfg";
		}
		
		public function pathFilters() {
			return $this->baseLocation . "\\filters.cfg";
		}
		
		public function pathPerms() {
			return $this->baseLocation . "\\perms.cfg";
		}
		
		public function pathQuotes() {
			return $this->baseLocation . "\\quotes.cfg";
		}
		
		public function pathStats() {
			return $this->baseLocation . "\\stats.cfg";
		}
		
		public function pathTimers() {
			return $this->baseLocation . "\\timers.cfg";
		}
		
		public function pathTokens() {
			return $this->baseLocation . "\\tokens.cfg";
		}
	}
?>