function addNewCommand() {
	$tbl = document.getElementById("cmdTable");
	$ntr = document.createElement("tr");
	
	$ntr.innerHTML = "<td><input name=\"trigger[]\" type=\"text\" value=\"\"></td><td><input name=\"message[]\" type=\"text\" value=\"\"></td><td><input name=\"rank[]\" type=\"number\" value=\"0\"></td><td><select name=\"active[]\"><option value=\"true\" selected>Yes</option><option value=\"false\">No</option></select></td>"
	
	$tbl.appendChild($ntr);
}