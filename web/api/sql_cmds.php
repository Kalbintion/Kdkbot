<?php
$dbh = getSQLConn();

function getSQLConn() {
    $details = getSQLData();
    $mysqli = new mysqli($details['host'], $details['user'], $details['pass'], $details['db']);
    return $mysqli;
}

function getSQLData() {
    $path = getBaseConfigSetting();
    $settings_contents = parse_ini_file($path . "\\settings.cfg", false, INI_SCANNER_RAW);
    
    $sql['user'] = $settings_contents['sqlUser'];
    $sql['pass'] = $settings_contents['sqlPass'];
    $sql['host'] = $settings_contents['sqlHost'];
    $sql['db'] = $settings_contents['sqlDB'];
    
    return $sql;
}

function updateDB($query) {
    $dbh = $GLOBALS['dbh'];
    return $dbh->query($query);
}

function queryDB($query) {
    $dbh = $GLOBALS['dbh'];
    return $dbh->query($query);
}

function cleanStr($data) {
    $dbh = $GLOBALS['dbh'];
    return $dbh->escape_string($data);
}

function sql_isInChannel($channel, $platform) {
    $dbh = $GLOBALS['dbh'];
    $stmt = $dbh->prepare("SELECT * FROM channels WHERE channel=? AND platform=?");
    $stmt->bind_param("ss", $channel, $platform);
    $stmt->execute();
    
    $res = $stmt->get_result();
    
    while($row = $res->fetch_assoc()) {
        if($row['channel'] == $channel && $row['platform'] == $platform) {
            if($row['join_channel'] == 0) { return false; } elseif($row['join_channel'] == 1) { return true; }
        }
    }
    
    $stmt->close();    
}

function sql_leaveChannel($channel, $platform) {
    $dbh = $GLOBALS['dbh'];
    $stmt = $dbh->prepare("UPDATE channels SET join_channel=0 WHERE channel=? AND platform=?");
    $stmt->bind_param("ss", $channel, $platform);
    $stmt->execute();    
    $stmt->close();
    $dbh->close();
    
    
}

function sql_joinChannel($channel, $platform) {
    $dbh = $GLOBALS['dbh'];
    $stmt = $dbh->prepare("UPDATE channels SET join_channel=1 WHERE channel=? AND platform=?");
    $stmt->bind_param("ss", $channel, $platform);
    $stmt->execute();
    $stmt->close();   
}

function sql_getChannelPerms($channel) {
    $dbh = $GLOBALS['dbh'];
    $stmt = $dbh->prepare("SELECT * FROM permissions WHERE channel=?");
    $stmt->bind_param("s", $channel);
    $stmt->execute();
    
    $res = $stmt->get_result();
    
    return $res;
}

function sql_updateChannelPerm($channel, $user, $rank) {
    $dbh = $GLOBALS['dbh'];
    $stmt = $dbh->prepare("UPDATE permissions SET level=? WHERE channel=? AND user=?");
    $stmt->bind_param("iss", $rank, $channel, $user);
    $stmt->execute();
    $stmt->close();
}

function sql_insertChannelPerm($channel, $user, $rank) {
    $dbh = $GLOBALS['dbh'];
    $stmt = $dbh->prepare("INSERT INTO permissions(`channel`, `user`, `level`) VALUES(?, ?, ?)");
    $stmt->bind_param("iss", $channel, $user, $rank);
    $stmt->execute();
    $stmt->close();
}

?>