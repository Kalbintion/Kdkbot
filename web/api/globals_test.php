<?PHP
include("globals.php");

echo "<pre>getBaseConfigSetting();\n";
var_dump(getBaseConfigSetting());
echo "</pre><hr>
	<pre>getChannelLocation(channel); // VALID CHANNEL\n";
var_dump(getChannelLocation("kalbintion"));
echo "</pre><hr>
	<pre>getChanneLocation(channel); // INVALID CHANNEL \n";
var_dump(getChannelLocation("thisisntarealchanel"));
echo "</pre><hr>
	<pre>parse_ini_file(.., false);\n";
var_dump(parse_ini_file("../cfg/settings.ini", false));
echo "</pre><hr>
	<pre>parse_ini_file(.., true);\n";
var_dump(parse_ini_file("../cfg/settings.ini", true));
echo "</pre><hr>
	<pre>new Channel(channel);\n";
var_dump(getChannelObject("kalbintion"));
echo "</pre><hr>
	<pre>new Channel(channel); // INVALID CHANNEL\n";
var_dump(getChannelObject("thisisntarealchanel"));
echo "</pre><hr>
	<pre>new Channel(channel)->pathAMA();\n";
var_dump(getChannelObject("kalbintion")->pathAMA());
echo "</pre>";
?>