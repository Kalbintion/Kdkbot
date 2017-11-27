<?PHP
if(isset($_GET['time'])) {
	$timeAmount = $_GET['time'];
} else {
	$timeAmount = 5 * 60; // 5 minutes
}

if(isset($_GET['style'])) {
	$styleCode = base64_decode($_GET['style']);
} else {
	$styleCode = "";
}

echo "
<html>
<head>
<title>Kdkbot - Timer Tool</title>
<style>$styleCode</style>
</head>
<body onLoad=\"displayStart()\">
<script>
function countDown() {
	var timeAmount = document.getElementById('timeAmount');
	timeAmount.value = timeAmount.value - 1;
	updateDisplay();
}

function displayStart() {
	updateDisplay();
	var timer = setInterval(countDown, 1000);
}

function updateDisplay() {
	var timeAmount = document.getElementById('timeAmount');
	var timeLeft = timeAmount.value;
	
	var negDisplay = false;
	if(timeLeft < 0) {
		negDisplay = true;
		timeLeft = Math.abs(timeLeft);
	}
	
	var display = document.getElementById('timeDisplay');
	var hour = Math.floor(timeLeft / 60 / 60);
	var hourText = hour.toString().padStart(2, '0');
	var minute = Math.floor(timeLeft / 60) % 60;
	var minuteText = minute.toString().padStart(2, '0');
	var second = Math.floor(timeLeft % 60);
	var secondText = second.toString().padStart(2, '0');
	
	var outDisplay = \"\";
	if(negDisplay) {
		outDisplay += \"-\";
	}
	if(hour > 0) {
		outDisplay += hourText + \":\";
	}
	outDisplay += minuteText + \":\";
	outDisplay += secondText;
	
	display.innerHTML = outDisplay;
}

</script>
<input type=\"hidden\" id=\"timeAmount\" value=\"$timeAmount\" />
<span id=\"timeDisplay\" style=\"font-family: 'Courier New';\">00:00:00</span>
</body>
</html>
";
?>