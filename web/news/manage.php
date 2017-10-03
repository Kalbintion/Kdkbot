<?PHP
isUserLoggedIn() or die("You need to be logged in to see this page!");
isUserNewsMgr() or die("You are not a news manager of this bot!");

echo "";
?>

<br />
<div style="margin: auto auto; width: 75%;">
<h1>Channel Panel</h1>Elements in:
<ul>
<li>Green: Active and fully functional. Please report any bugs to the webmaster.</li>
<li>Yellow: Incomplete and are not fully functional.</li>
<li>Red: Unavailable at this time.</li>
</ul>
</div>
<div style="margin: auto auto; width: 75%;">

<a href="?p=manage">
<div class="button">
<img src="./imgs/door_opened1600.png">
<span class="buttonname">Manage Channel</span>
<span class="buttontext">Manage your channel information instead.</span>
</div>
</a>

<a href="?p=news/add">
<div class="button">
<img src="./imgs/plus-4-xxl.png">
<span class="buttonname">Add</span>
<span class="buttontext">Add a news item for the front page.</span>
</div>
</a>

<a href="?p=news/edit">
<div class="button">
<img src="./imgs/change.png">
<span class="buttonname">Edit</span>
<span class="buttontext">Edit a news item for the front page.</span>
</div>
</a>

<a href="?p=news/remove">
<div class="button">
<img src="./imgs/Minus_icon.png">
<span class="buttonname">Remove</span>
<span class="buttontext">Remove a news item for the front page. Irreversible.</span>
</div>
</a>

</div>
<br style="clear: both;"/>