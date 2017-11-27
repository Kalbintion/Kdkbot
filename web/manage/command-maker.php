<?php 
isUserLoggedIn() or die("You need to log in.");

$channel = getChannelObject($_SESSION['USER']);

echo "
<table class=\"minTable inputs2\">

</table>";

?>