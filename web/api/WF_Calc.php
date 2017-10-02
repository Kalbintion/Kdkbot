<!DOCTYPE html>
<html>
<head>
<title>Warframe School Point Spend Calculator</title>

<style>
* {
	background-color: #777;
	color: #FFF;
}

table input:hover {
	background-color: rgba(0,0,0,0.3);
}

table:hover {
	background-color: rgba(0,127,0,0.3);
}

table:hover caption, table:hover caption * {
	background-color: rgba(0,127,0,0.3);
}

table {
	display: inline-block;
	float: left;
	border: 1px solid white;
	border-radius: 5px;
	border-collapse: collapse;
	margin: 5px;
	padding: 5px;
}

tr, td {
	padding-left: 5px;
	padding-right: 5px;
	padding-top: 2px;
	padding-bottom: 2px;
	border-bottom: 1px solid white;
	border-top: 1px solid white;
}

tr td:first-child {
	text-align: right;
}

caption {
	margin: 10px;
	text-align: center;
	border: 1px solid white;
	border-radius: 15px;
	font-size: 2em;
}
</style>

</head>
<body>

<script>

function updatetotals() {
	poolArr = [8586, 18916, 31042, 45009, 60858, 78627, 98352, 120066, 143799, 169581, 197439, 227339, 259486, 293724, 330135, 368742, 409565, 452625, 497941, 545532, 595417, 647613, 702137, 759006, 818237, 879845, 943845, 1010252, 1079082, 1150348, 1224064, 1300244, 1378902, 1460050, 1543701, 1629868, 1718563, 1809798, 1903585, 1999936, 2098862, 2200374, 2304483, 2411200, 2520536, 2632502, 2747107, 2864362, 2984277, 3106861, 3232125, 3360078, 3490729, 3624088, 3760164, 3898966, 4040503, 4184784, 4331817, 4481612, 4634176, 4789518, 4947647, 5108570, 5272296, 5438833, 5608188, 5780370, 5955386, 6133244, 6313952, 6497517, 6683247, 7065427, 7260493, 7458453, 7659313, 7863081];
	
	tGrand = 0;
	tMadurai = 0;
	tZenurik = 0;
	tVazarin = 0;
	tUnairu = 0;
	tNaramon = 0;

	tTemp = 0;
	
	stats = document.getElementsByName("mStat");
	for(i = 0; i < stats.length; i++) {
		if(i == 24) {
			tMadurai = +tTemp;
			tGrand += +tTemp;
			tTemp = 0;
		} else if(i == 48) {
			tVazarin = +tTemp;
			tGrand += +tTemp;
			tTemp = 0;
		} else if(i == 72) {
			tNaramon = +tTemp;
			tGrand += +tTemp;
			tTemp = 0;
		} else if(i == 96) {
			tUnairu = +tTemp;
			tGrand += +tTemp;
			tTemp = 0;
		}
		
		if(stats[i].hasAttribute("data-scale")) {
			// Scale before adding
			scaling = stats[i].getAttribute("data-scale").split("/");
			currentRank = stats[i].value;
			for(j = 0; j <= currentRank; j++) {
				tTemp += +scaling[j];
			}
		} else {
			if(i % 24 == 1) {
				// Pool point calculation
				if(stats[i].value>5) {
					offset = (+stats[i].value-6);
					if(offset > poolArr.length) {
						tTemp += 8602.08-5850.214*offset+1096.1311*offset*offset;
					} else {
						tTemp += poolArr[(+stats[i].value-6)];
					}
				} // else: we dont care
			} else {
				// Add to total
				tTemp += +stats[i].value;
			}
		}
	}

	tZenurik = +tTemp;
	tGrand += +tTemp;
	tTemp = 0;

	// Update total displays
	eleMad = document.getElementById("tMadurai");
	eleVaz = document.getElementById("tVazarin");
	eleNar = document.getElementById("tNaramon");
	eleUna = document.getElementById("tUnairu");
	eleZen = document.getElementById("tZenurik");
	eleGra = document.getElementById("tGrand");

	eleMad.innerHTML = tMadurai;
	eleVaz.innerHTML = tVazarin;
	eleNar.innerHTML = tNaramon;
	eleUna.innerHTML = tUnairu;
	eleZen.innerHTML = tZenurik;
	eleGra.innerHTML = tGrand;
	
}

</script>

<table style="width: 200px; margin: auto; float: none !important;">
	<caption>Totals</caption>
	<tr style="width: 200px;">
		<th style="width: 180px;">Madurai</th>
		<td id="tMadurai">0</td>
	</tr>
	<tr style="width: 200px;">
		<th>Vazarin</th>
		<td id="tVazarin">0</td>
	</tr>
	<tr style="width: 200px;">
		<th>Naramon</th>
		<td id="tNaramon">0</td>
	</tr>
	<tr style="width: 200px;">
		<th>Unairu</th>
		<td id="tUnairu">0</td>
	</tr>
	<tr style="width: 200px;">
		<th>Zenurik</th>
		<td id="tZenurik">0</td>
	</tr>
	<tr style="width: 200px;">
		<th>Total</th>
		<td id="tGrand">0</td>
	</tr>
	<tr>
		<td colspan="2"><hr></td>
	</tr>
	<tr>
		<td colspan="2"><input style="width: 100%;" type="button" onClick="updatetotals()" value="Update"></td>
	</tr>
</table>

<br style="clear: both;"/>

<table>
	<caption><img src="https://vignette.wikia.nocookie.net/warframe/images/1/17/FocusLensMadurai_b.png/revision/latest/scale-to-width-down/30?cb=20151206123922"> Madurai</caption>
	<tr>
		<td>Unspent Points</td>
		<td colspan="2"><input type="number" name="mStat" id="mUnspent" value="0" min="0" max="9999999"></td>
	</tr>
	<tr>
		<td>Pool Points</td>
		<td colspan="2"><input type="number" name="mStat" id="mPool" value="0" min="0" max="9999"></td>
	</tr>
	<tr>
		<th>Skill</th>
		<th>Rank</th>
		<th>XP To Next</th>
	</tr> 

	<tr>
		<td>Phoenix Gaze</td>
		<td><input type="number" name="mStat" id="mBase" value="0" min="0" max="4" data-scale="0/50000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" id="mBaseNxt" value="0" min="0" max="150000"></td>
	</tr>
	<tr>
		<td>Phoenix Gaze Mastery</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="150000"></td>
	</tr>
	<tr>
		<td>Blazing Fury</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="300000"></td>
	</tr>
	<tr>
		<td>Searing Wrath</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="300000"></td>
	</tr>
	<tr>
		<td>Burning Rage</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="300000"></td>
	</tr>
	<tr>
		<td>Phoenix Flash</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="450000"></td>
	</tr>
	<tr>
		<td>Rising Ashes</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="450000"></td>
	</tr>
	<tr>
		<td>Chimera Breath</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="150000"></td>
	</tr>
	<tr>
		<td>Hades Touch</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="300000"></td>
	</tr>
	<tr>
		<td>Meteorite</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="300000"></td>
	</tr>
	<tr>
		<td>Dragon Fire</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="300000"></td>
	</tr>
</table>

<table>
	<caption><img src="https://vignette.wikia.nocookie.net/warframe/images/9/98/FocusLensVazarin_b.png/revision/latest/scale-to-width-down/30?cb=20151206123924"> Vazarin</caption>
	<tr>
		<td>Unspent Points</td>
		<td colspan="2"><input type="number" name="mStat" id="vUnspent" value="0" min="0" max="9999999"></td>
	</tr>
	<tr>
		<td>Pool Points</td>
		<td colspan="2"><input type="number" name="mStat" id="vPool" value="0" min="0" max="9999"></td>
	</tr>
	<tr>
		<th>Skill</th>
		<th>Rank</th>
		<th>XP To Next</th>
	</tr>
	<tr>
		<td>Mending Tides</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Mending Tides Mastery</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/120000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>New Moon</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Disciplined Approach</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Retaliation</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Mending Shower</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Polluted Waters</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Protection Ward</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Commanding Words</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/15000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Strengthen Defenses</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Guardian Presence</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
</table>

<table>
	<caption><img src="https://vignette.wikia.nocookie.net/warframe/images/6/6d/FocusLensNaramon_b.png/revision/latest/scale-to-width-down/30?cb=20151206123923"> Naramon</caption>
	<tr>
		<td>Unspent Points</td>
		<td colspan="2"><input type="number" name="mStat" id="vUnspent" value="0" min="0" max="9999999"></td>
	</tr>
	<tr>
		<td>Pool Points</td>
		<td colspan="2"><input type="number" name="mStat" id="vPool" value="0" min="0" max="9999"></td>
	</tr>
	<tr>
		<th>Skill</th>
		<th>Rank</th>
		<th>XP To Next</th>
	</tr>
	<tr>
		<td>Mind Spike</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Mind Spike Mastery</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Mind Blast</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Traumatic Redirection</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Tactical Strike</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Reveal Weakness</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Sundering Blast</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Cloaking Aura</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Strategic Execution</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Deadly Intent</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Shadow Step</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
</table>

<table>
	<caption><img src="https://vignette.wikia.nocookie.net/warframe/images/7/78/FocusLensUnairu_b.png/revision/latest/scale-to-width-down/30?cb=20151206123924"> Unairu</caption>
	<tr>
		<td>Unspent Points</td>
		<td colspan="2"><input type="number" name="mStat" id="vUnspent" value="0" min="0" max="9999999"></td>
	</tr>
	<tr>
		<td>Pool Points</td>
		<td colspan="2"><input type="number" name="mStat" id="vPool" value="0" min="0" max="9999"></td>
	</tr>
	<tr>
		<th>Skill</th>
		<th>Rank</th>
		<th>XP To Next</th>
	</tr>
	<tr>
		<td>Basilisk Flare</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Basilisk Flare Mastery</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/120000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Stone Shape</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Medusa Skin</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Mighty Blows</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Scorched Earth</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Lasting Judgement</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Crushing Force</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Weight of Justice</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Stone Armor</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Eroded Defenses</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
</table>

<table>
	<caption><img src="https://vignette.wikia.nocookie.net/warframe/images/1/1e/FocusLensZenurik_b.png/revision/latest/scale-to-width-down/30?cb=20151206123925"> Zenurik</caption>
	<tr>
		<td>Unspent Points</td>
		<td colspan="2"><input type="number" name="mStat" id="vUnspent" value="0" min="0" max="9999999"></td>
	</tr>
	<tr>
		<td>Pool Points</td>
		<td colspan="2"><input type="number" name="mStat" id="vPool" value="0" min="0" max="9999"></td>
	</tr>
	<tr>
		<th>Skill</th>
		<th>Rank</th>
		<th>XP To Next</th>
	</tr>
	<tr>
		<td>Void Pulse</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Void Pulse Mastery</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Time Stream</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Temporal Storm</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Energy Overflow</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Systemic Override</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Energy Spike</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Energy Surge</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Rift Sight</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/50000/150000/225000/300000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Umbra Lance</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/80000/225000/337500/450000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
	<tr>
		<td>Magnetic Aftershock</td>
		<td><input type="number" name="mStat" value="0" min="0" max="4" data-scale="0/25000/75000/112500/150000"></td>
		<td><input type="number" name="mStat" value="0" min="0" max="999999"></td>
	</tr>
</table>

</body>
</html>