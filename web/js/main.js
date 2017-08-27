function addNewCommand() {
	$tbl = document.getElementById("cmdTable");
	$ntr = document.createElement("tr");
	
	$ntr.innerHTML = "<td><input name=\"trigger[]\" type=\"text\" value=\"\"></td><td><input name=\"message[]\" type=\"text\" value=\"\"></td><td><input name=\"rank[]\" type=\"number\" value=\"0\"></td><td><select name=\"active[]\"><option value=\"true\" selected>Yes</option><option value=\"false\">No</option></select></td>"
	
	$tbl.appendChild($ntr);
}

function addNewUser() {
	$tbl = document.getElementById("permTable");
	$ntr = document.createElement("tr");
	
	$ntr.innerHTML = "<tr><td><input type=\"text\" name=\"user[]\" value=\"\"></td><td><input type=\"number\" name=\"rank[]\" value=\"\" /></td></tr>";
	
	$tbl.appendChild($ntr);
}