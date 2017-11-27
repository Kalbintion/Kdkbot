<?PHP
echo "
<HTML>
<HEAD>
</HEAD>
<BODY>
Leave this window open for audio commands to play.
<audio controls autoplay>
  <source src=\"audio-cache\\notify.wav\" type=\"audio/wav\">
Your browser does not support the audio element.
</audio>
<IFRAME SRC=\"tools\\audio-executor.php\" SEAMLESS style=\"width: 100%; height: 100%;\"></IFRAME>
</BODY>
</HTML>
";
?>