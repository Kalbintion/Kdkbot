<?php 
isUserLoggedIn() or die("You need to be logged in to see this page!");
?>

<a href="?p=manage">
<div class="button">
<img src="./imgs/door_opened1600.png">
<span class="buttonname">Back</span>
<span class="buttontext">Go back to the main channel configuration page.</span>
</div>
</a>

<a href="?p=manage/tools/timer">
<div class="button">
<img src="./imgs/blank_120.png">
<span class="buttonname">Timers</span>
<span class="buttontext">Stream Overlay. Manage on-stream timers.</span>
</div>
</a>