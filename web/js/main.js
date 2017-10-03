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

function addNewQuote() {
	$tbl = document.getElementById("quoteTable");
	$ntr = document.createElement("tr");
	
	$ntr.innerHTML = "<tr><td><input style=\"width: 80px;\" type=\"number\" name=\"id[]\" value=\"\"></td><td><textarea name=\"quote[]\"></textarea></td></tr>\r\n";
	
	$tbl.appendChild($ntr);
}

function addNewAMA() {
	$tbl = document.getElementById("amaTable");
	$ntr = document.createElement("tr");
	
	$ntr.innerHTML = "<tr><td><textarea name=\"question[]\"></textarea></td></tr>\r\n";
	
	$tbl.appendChild($ntr);
}

function addNewFilter() {
	$tbl = document.getElementById("filtersTable");
	$ntr = document.createElement("tr");
	
	$ntr.innerHTML = "<tr><td><input type=\"text\" name=\"title[]\" value=\"\"></td><td><select name=\"type[]\"><option value=\"0\" selected>None</option><option value=\"1\">Purge</option><option value=\"2\">Timeout</option><option value=\"3\">Ban</option><option value=\"4\">Message</option></select></td><td><input type=\"text\" name=\"filter[]\" value=\"\"></td><td><input type=\"text\" name=\"message[]\" value=\"\"></td><td><select name=\"bypassable[]\"><option value=\"true\" selected>Yes</option><option value=\"false\">No</option></td></tr>\r\n";
	
	$tbl.appendChild($ntr);
}

function addNewCounter() {
	$tbl = document.getElementById("countersTable");
	$ntr = document.createElement("tr");
	
	$ntr.innerHTML = "<tr><td><input type=\"text\" name=\"name[]\" value=\"\" /></td><td><input type=\"number\" name=\"value[]\" value=\"\" /></td></tr>\r\n";
	
	$tbl.appendChild($ntr);
}

function encodeText(obj) {
	obj.value = obj.value.replace(/</g, "&lt;").replace(/>/g, "&gt;");
}