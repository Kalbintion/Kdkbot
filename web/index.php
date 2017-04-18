<html>
<head>
<title>Kdkbot</title>
<link rel="stylesheet" href="styles/main.css">
<meta charset="utf-8" />
</head>
<body>
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
			<li class="menuButton"><a href="https://api.twitch.tv/kraken/oauth2/authorize?response_type=code&client_id=gia5fjuorx23n2e0wyfiii0soafxop0&redirect_uri=http%3A%2F%2Ftfk.zapto.org%2Fkdkbot%2F%3Fp%3Dtwitch&scope=channel_read+channel_editor+channel_commercial+channel_subscriptions&state=p%3Dauth&forceverify=true">Authorize</a></li>
		</ul>
	</div>
	<div class="contentBody">
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
	</div>	
	<div class="contentFooter">
		&copy; 2017 Kalbintion. All Rights Reserved.
	</div>
	<br />
</div>
</body>
</html>