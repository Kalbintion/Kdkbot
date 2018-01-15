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
	
	/* Determines if a given string (haystack) contains another string (needle).
	 * Returns true if found, false otherwise
	 */
	function endsWith($haystack, $needle) {
		if(substr($haystack, -strlen($needle), strlen($needle)) === $needle) {
			return true;
		}
		return false;
	}
	
	/* 
	 *
	 */
	function contains($haystack, $needle) {
		if (strpos($haystack, $needle) !== false) {
			return true;
		}
		return false;
	}
	
	function rankNumberToName($number) {
		switch($number) {
			case 1:
				return "normal";
			case 2:
				return "regular";
			case 3:
				return "moderator";
			case 4:
				return "super moderator";
			case 5:
				return "channel operator";
			case PHP_INT_MAX:
				return "max";
			case PHP_INT_MIN:
				return "min";
			case 0:
				return "nobody";
			default:
				return "unknown";
		}
	}
	
	function rankNameToNumber($name) {
		switch($name) {
			case "normal":
			case "n":
				return 1;
			case "regular":
			case "r":
				return 2;
			case "moderator":
			case "mod":
			case "m":
				return 3;
			case "supermoderator":
			case "smod":
			case "supermod":
			case "sm":
				return 4;
			case "channeloperator":
			case "chanop":
			case "co":
			case "op":
			case "owner":
				return 5;
			case "max":
			case "*":
				return Integer.MAX_VALUE;
			case "/":
			case "min":
				return Integer.MIN_VALUE;
			case "nobody":
			default:

		}
	}
?>