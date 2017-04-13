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
			<li class="menuButton"><a href="?p=basecommands">Base Commands (Temp Redirect)</a></li>
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