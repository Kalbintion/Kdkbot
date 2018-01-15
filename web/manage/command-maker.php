<?php 
isUserLoggedIn() or die("You need to log in.");

$channel = getChannelObject($_SESSION['USER']);

echo "
<script>
function updateHelpDisplay() {

}
</script>
<table class=\"minTable inputs2\">
	<tr>
		<th>Trigger</th>
		<th>Rank</th>
		<th>Active</th>
	</tr>
	<tr>
		<td><input name=\"trigger\" type=\"text\" value=\"\"></td>
		<td><input name=\"rank\" type=\"number\" value=\"0\"></td>
		<td><select name=\"active\"><option value=\"true\">Yes</option><option value=\"false\">No</option></select></td>
	</tr>
	<tr>
		<th colspan=\"3\">Message</th>
	</tr>
	<tr>
		<td colspan=\"3\"><textarea rows=\"10\" name=\"message\"></textarea></td>
	</tr>
</table>
<br />
<table class=\"minTable inputs2\">
	<tr>
		<th>Percent-Args</th>
	</tr>
	<tr>
		<td>
			<select onChange=\"updateHelpDisplay()\">
				<option>--- General ---</option>
				<option value=\"%ARGS%\" data-help=\"Replaced with all text after a command\">%ARGS%</option>
				<option value=\"%ARGS:!%\" data-help=\"Replaced with the text after a command based on position provided\">%ARGS:...%</option>
				<option value=\"%RND:!%\" data-help=\"Replaced with a random number from 0 to one specified\">%RND:...%</option>
				<option value=\"%RND:!,!%\" data-help=\"Replaced with a random number from two specified numbers\">%RND:...,...%</option>
				<option value=\"%MATH:!%\" data-help=\"Calculates the mathematical expression provided\">%MATH:...%</option>
				<option value=\"%GAME:!%\" data-help=\"Replaced with the name of the game of provided user\">%GAME:...%</option>
				<option value=\"%TITLE:!%\" data-help=\"Replaced with the stream title of the provided user\">%TITLE:...%</option>
				<option></option>
				<option>--- Text ---</option>
				<option value=\"%UPPER:!%\" data-help=\"Converts all text provided to its upper-case equivalent\">%UPPER:text%</option>
				<option value=\"%LOWER:!%\" data-help=\"Converts all text provided to its lower-case equivalent\">%LOWER:text%</option>
				<option value=\"%REPLACE:!,!,!%\" data-help=\"Replaces text given with another bit of text in the overall text provided\">%REPLACE:...,...,...%</option>
				<option value=\"%FLIP:!%\" data-help=\"Converts all text provided to its upside-down equivalent\">%FLIP:text%</option>
				<option value=\"%PICK:!%\" data-help=\"Picks one thing out of a selection provided, separated by commas\">%PICK:option 1,option 2,...%</option>
				<option value=\"%REVERSE:!%\" data-help=\"Reverses all the text provided\">%REVERSE:...%</option>
				<option value=\"%LEET:!%\" data-help=\"Converts all text provided to its leet equivalent\">%LEET:text%</option>
				<option></option>
				<option>--- Special ---</option>
				<option value=\"%CMD:!%\" data-help=\"Replaced with the contents of another command\">%CMD:...%</option>
				<option value=\"%CNTR:!%\" data-help=\"Replaced with the value of a counter\">%CNTR:...%</option>
				<option value=\"%CNTR++:!%\" data-help=\"Replaced with the value of a counter\">%CNTR:...%</option>
				<option value=\"%CNTR--:!%\" data-help=\"Replaced with the value of a counter and increments it\">%CNTR++:...%</option>
				<option value=\"%CNTR--:!%\" data-help=\"Replaced with the value of a counter and decrements it\">%CNTR--:...%</option>
				<option value=\"%PAGETITLE:!%\" data-help=\"Replaced with the page title of a provided URL\">%PAGETITLE:...%</option>
				<option value=\"%YTVIEWS:!%\" data-help=\"Provides the number of video views on a given video ID\">%YTVIEWS:...%</option>
				<option></option>
				<option>--- Warframe ---</option>
				<option value=\"%WFM:!%\">%WFM:...%</option>
				<option value=\"%WFS:!%\">%WFS:...%</option>
				<option value=\"%WFN%\">%WFN%</option>
				<option value=\"%WFNF%\">%WFNF%</option>
				<option value=\"%WFA%\">%WFA%</option>
				<option value=\"%WFSO%\">%WFSO%</option>
				<option value=\"%WFSY%\">%WFSY%</option>
				<option value=\"%WFV%\">%WFV%</option>
				<option value=\"%WFSS%\">%WFSS%</option>
				<option value=\"%WFI%\">%WFI%</option>
				<option value=\"%WFBAD%\">%WFBAD%</option>
				<option value=\"%WFB%\">%WFB%</option>
				<option value=\"%WFD%\">%WFD%</option>
				<option value=\"%WFDR%\">%WFDR%</option>
			</select>
		</td>
	</tr>
	<tr>
		<th>Help</th>
	</tr>
	<tr>
		<td id=\"percent-help\">No help to display.</td>
	</tr>
	<tr>
		<td><input type=\"button\" value=\"Add To Command\"></td>
	</tr>
</table>
";

?>