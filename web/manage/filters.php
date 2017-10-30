<?PHP
isUserLoggedIn() or die("You need to be logged in to see this page!");

$channel = new Channel(getBaseConfigSetting() . "\\#" . $_SESSION['USER']);

if(isset($_POST['update'])) {
	$output = "";
	for($i = 0; $i < count($_POST['title']); $i++) {
		if($_POST['title'][$i] == "") { continue; }
		// Title||Type||Message||Bypassable||Filter
		$output .= $_POST['title'][$i] . "||" . $_POST['type'][$i] . "||" . $_POST['message'][$i] . "||" . $_POST['bypassable'][$i] . "||" . $_POST['filter'][$i] . "\r\n";
	}
	
	if(file_put_contents($channel->pathFilters(), $output) === false) {
		echo "<div class=\"boxError\">Couldn't update Filters. Please try again later.</div><br />";
	} else {
		echo "<div class=\"boxSuccess\">Successfully updated Filters.</div><br />";
		qChannelUpdate($_SESSION['USER'], "filters");
	}
}

$filters_contents = file_get_contents($channel->pathFilters());
$filters = explode("\r\n", $filters_contents);
asort($filters);

echo "
<div class=\"boxWarning\">To remove a filter, make the Name field blank.</div><br />

<form action=\"?p=manage/filters\" method=\"POST\">
<input type=\"hidden\" value=\"" . $_SESSION['USER'] . "\" name=\"update\" />
<table class=\"minTable inputs2\" id=\"filtersTable\">
	<tr>
		<th>Name</th>
		<th>Type</th>
		<th>Filter</th>
		<th>Message</th>
		<th>Bypassable?</th>
	</tr>	
";

$i = 0; $j = 0;
	foreach($filters as $filter) {
		$i++; $j++;
		$parts = explode("||", $filter, 5);
		if(count($parts) >=2 ) {
			echo "
			<tr>
				<td><input type=\"text\" name=\"title[]\" value=\"" . $parts[0] . "\"></td>
				<td><select name=\"type[]\">";
				if($parts[1] == "0") {
					echo "<option value=\"0\" selected>None</option><option value=\"1\">Purge</option><option value=\"2\">Timeout</option><option value=\"3\">Ban</option><option value=\"4\">Message</option>";
				} elseif($parts[1] == "1") {
					echo "<option value=\"0\">None</option><option value=\"1\" selected>Purge</option><option value=\"2\">Timeout</option><option value=\"3\">Ban</option><option value=\"4\">Message</option>";
				
				} elseif($parts[1] == "2") {
					echo "<option value=\"0\">None</option><option value=\"1\">Purge</option><option value=\"2\" selected>Timeout</option><option value=\"3\">Ban</option><option value=\"4\">Message</option>";
				
				} elseif($parts[1] == "3") {
					echo "<option value=\"0\">None</option><option value=\"1\">Purge</option><option value=\"2\">Timeout</option><option value=\"3\" selected>Ban</option><option value=\"4\">Message</option>";
				
				} elseif($parts[1] == "4") {
					echo "<option value=\"0\">None</option><option value=\"1\">Purge</option><option value=\"2\">Timeout</option><option value=\"3\">Ban</option><option value=\"4\" selected>Message</option>";
				
				}
				echo "</select></td>
				<td><input type=\"text\" name=\"filter[]\" value=\"" . $parts[4] . "\"></td>
				<td><input type=\"text\" name=\"message[]\" value=\"" . $parts[2] . "\"></td>
				<td><select name=\"bypassable[]\">";
				
				if($parts[3] == "true") {
					echo "<option value=\"true\" selected>Yes</option><option value=\"false\">No</option>";
				} else {
					echo "<option value=\"true\">Yes</option><option value=\"false\" selected>No</option>";
				}
				
				echo "</select></td>
			</tr>";
		}
		
		if($i == 15 && (count($filters) - $j) != 1) {
			echo "<tr><td colspan=\"5\"><input type=\"submit\" value=\"Submit\" /></td></tr>";
			$i = 0;
		}
	}

echo "<tr><td colspan=\"5\"><input type=\"submit\" value=\"Submit\" /></td></tr>
		<tr>
			<td colspan=\"5\"><input id=\"bottom\" type=\"button\" value=\"Add Filter\" onClick=\"addNewFilter()\" /></td>
		</tr>";
	
echo "</table></form>


<table class=\"minTable inputs2\">
<tr>
	<th>Add Common Filters</th>
</tr>
<tr>
	<td>
		<select id=\"existingFilters\" onChange=\"addExistingFilter(this)\">
			<option value=\"null\" data-filter=\"\" data-name=\"\" data-type=\"\" data-bypassable=\"\" data-message=\"\">Select filter...</option>
			<option value=\"anti-swear\" data-filter=\"\b(([a@]ss|butt?)-?(p[i!1]r[a@]t[e3]|j[[a@]@]bb[e3]r|b[a@]nd[i!1]t|b[a@]ng[e3]r|fuck[e3]r|g[o0()]bl[i!1]n|h[o0()]pp[e3]r|j[a@]ck[e3]r|l[i!1]ck[e3]r)|b[i!1]tcht[i!1]ts|br[o0()]th[e3]rfuck[e3]r|bumbl[e3]fuck|buttfuck([a@]|[e3]r)|n[i!1]gg?([e3]r|[a@])|b[e3][a@]n[e3]r|c[a@]rp[e3]tmunch[e3]r|ch[i!1]n(c|k)|(c[o0()]ck(j[o0()]ck[e3]y|kn[o0()]k[e3]r|m[a@]st[e3]r|m[o0()]ngl[e3]r|m[o0()]ngru[e3]l|m[o0()]nk[e3]y|munch[e3]r|sm[i!1]th|sm[o0()]k[e3]r?|sn[i!1]ff[e3]r|suck[e3]r))|c[o0()][o0()]n|cr[a@]ck[e3]r|(cum(guzzl[e3]r|j[o0()]ck[e3]y))|cuntl[i!1]ck[e3]r|d[i!1]ck(fuck([e3]r)?|m[o0()]ng[e3]r|suck([e3]r)?)|d[[i!1]y]k[e3]|d[o0()]uch[e3]w[a@]ffl[e3]|f[a@]g(b[a@]g|fuck[e3]r|g[i!1]t|g[o0()]t(c[o0()]ck)?|t[a@]rd)?|fl[a@]m[e3]r|fudg[e3]p[a@]ck[e3]r|g[a@]y(b[o0()]b|d[o0()]|fuck([i!1]st)?|l[o0()]rd|t[a@]rd|w[a@]d)|g[o0()][o0()]k|gu[i!1]d[o0()]|h[e3][e3]b|j[i!1]g[a@]b[o0()][o0()]|jungl[e3] ?bunny|l[e3]sb[o0()]|l[e3]zz[i!1][e3]|mcf[a@]gg?[[e3][o0()]]t|n[i!1]g[a@]b[o0()][o0()]|n[i!1]gl[e3]t|p[e3]n[i!1]s(b[a@]ng[e3]r|fuck[e3]r|puff[e3]r)|p[o0()]l[e3]sm[o0()]k[e3]r|p[o0()]ll[o0()]ck|qu[e3][e3]r(b[a@][i!1]t|h[o0()]l[e3])|s[a@]nd ?n[i!1]gg[[e3][a@]]r?|sp[i!1]ck|tw[a@]tw[a@]ffl[e3]|uncl[e3]fuck[e3]r|w[e3]tb[a@]ck|w[o0()]p)\b\" data-name=\"anti-swear\" data-type=\"purge\" data-bypassable=\"yes\" data-message=\"\">Anti-Swear</option>
			<option value=\"link\" data-filter=\"(https?\:(?://|\\\\)[A-Za-z0-9\./-]*)((?:/|\\)?|\.(html?|php|asp))\" data-name=\"link\" data-type=\"message\" data-bypassable=\"yes\" data-message=\"Linked page: %PAGETITLE:%URL%%\">Link</option>
			<option value=\"under-age\" data-filter=\"[iI]'?[mM] \b(?:1[0-7]|[0-9])\b y(ear|r)s?\" data-name=\"under-age\" data-type=\"purge\" data-bypassable=\"yes\" data-message=\"\">Under Age</option>
		</select>
	</td>
</tr>
</table>

";
?>