<?php 
include("./api/globals.php");

session_start();
if(isset($_COOKIE['PHPSESSID'])) {
	$id = $_COOKIE['PHPSESSID'];
} else {
	$id = session_create_id();
}

// Create Auth URL for current session id
$auth_url = "https://api.twitch.tv/kraken/oauth2/authorize?response_type=code&client_id=gia5fjuorx23n2e0wyfiii0soafxop0&redirect_uri=http%3A%2F%2Ftfk.zapto.org%2Fkdkbot%2F%3Fp%3Dtwitch&scope=channel_read+channel_editor+channel_commercial+channel_subscriptions&state=$id&forceverify=true";
?>
<html>
<head>
<title>Kdkbot</title>
<link rel="stylesheet" href="styles/main.css">
<meta charset="utf-8" />
</head>
<body>
<script src="js/main.js"></script>
<div class="contentMain">
	<br />
	<div class="contentHeader">
		<span>#Kdkbot</span>
	</div>
	<div class="contentMenu">
		<ul>
			<li class="menuButton"><a href="?p=">Home</a></li>
			<li class="menuButton"><a href="?p=channels">Channels</a></li>
			<li class="menuButton"><a href="https://github.com/Kalbintion/Kdkbot/wiki">Wiki</a></li>
			<li class="menuButton"><a href="?p=contact">Contact Me</a></li>
			<li class="menuButton"><a href="?p=basecommands">Base Commands</a></li>
			<?php 
				if(!isUserLoggedIn()) {
					echo "<li class=\"menuButton\"><a href=\"$auth_url\">Login</a></li>";
				} else {
					echo "<li class=\"menuButton\"><a href=\"?p=manage\">Channel</a></li>";
					echo "<li class=\"menuButton\"><a href=\"?p=logout\">Logout</a></li>";
				}
				
			?>
		</ul>
	</div>
	<div class="contentBody">
		<br />
		<?PHP
			if(isset($_GET['p'])) {
				$p = str_replace(".", "", $_GET['p']);
			} else {
				$p = "";
			}
			if(file_exists($p . ".php")) {
				include($p . ".php");
			} else if($p == "") {
				include("news.php");
			} else {
				include("error.php");
			}
		?>
		<br /><br />
	</div>	
	<div class="contentFooter">
		<br /><br />
		&copy; 2017 Kalbintion. All Rights Reserved.
	</div>
	<br />
</div>
</body>
</html>