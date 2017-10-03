<!--<div class="boxWarning"><center><img src="https://cdnd.icons8.com/wp-content/uploads/2015/06/under-construction-2.gif" width="120px" height="100px"><br /><br />This website is currently under construction! If you are here for a channels list of commands, feel free to click "Channels" above and select the appropriate channel!</center></div>
<hr>-->

<?PHP
$files = scandir("./news-data/", SCANDIR_SORT_DESCENDING);

$limiter = 0;
$limiter_max = getNewsLimiter();

foreach($files as $file) {
	if($file == "." || $file == "..") { continue; }
	if($limiter < $limiter_max) {
		$data = file_get_contents("./news-data/" . $file);
		echo $data;
		echo "<hr>";
		$limiter++;
	}
}
?>