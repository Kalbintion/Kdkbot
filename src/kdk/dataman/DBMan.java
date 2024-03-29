package kdk.dataman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBMan {
	private static Connection _con = null;
	private static Statement _stmt = null;
	private static String _host = "";
	private static String _user = "";
	private static String _pass = "";
	public static boolean connected = false;
	private static boolean recursed = false;
	
	public DBMan(Connection con) {
		_con = con;
		createStmt();
	}
	
	public DBMan(String host, String user, String password) {
		try {
			_con = DriverManager.getConnection("jdbc:mysql://" + host + "/kdkbot?user=" + user + "&password=" + password);
			_host = host;
			_user = user;
			_pass = password;
			createStmt();
		} catch (SQLException e) {
			writeSQLError(e);
		}
	}
	
	// Attempts to re-establish connection
	public void reconnect() {
		try {
			_con = DriverManager.getConnection("jdbc:mysql://" + _host + "/kdkbot?user=" + _user + "&password=" + _pass);
			createStmt();
		} catch (SQLException e) {
			writeSQLError(e);
		}
	}
	
	private void createStmt() {
		try {
			_stmt = _con.createStatement();
			connected = true;
		} catch (SQLException e) {
			writeSQLError(e);
		}
	}
	
	public void writeSQLError(SQLException e) {
		kdk.Bot.inst.dbg.writeln("SQLException: " + e.getMessage());
		kdk.Bot.inst.dbg.writeln("SQLState: " + e.getSQLState());
		kdk.Bot.inst.dbg.writeln("VendorError:" + e.getErrorCode());
	}
	
	public String queryDBStr(String query) {
		if(_stmt == null) { return ""; }
		if(_con == null) { return ""; }
		
		try {
			String out = "";
			ResultSet rs = queryDB(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next()) {
				for(int i = 1; i <= rsmd.getColumnCount(); i++) {
					out += rsmd.getColumnLabel(i) + "=" + rs.getObject(i) + ", ";
				}
				
				out += "\n";
			}
			
			return out;
		} catch (SQLException e) {
			writeSQLError(e);
			return null;
		}
	}
	
	/**
	 * Returns a ResultSet for the given query
	 * @param query The SQL query to execute
	 * @return The resulting set for the query, null if connection or an error occurred.
	 */
	public ResultSet queryDB(String query) {
		if(_stmt == null) { return null; }
		if(_con == null) { return null; }
		
		try {
			ResultSet rs = _stmt.executeQuery(query);
			recursed = false;
			return rs;
		} catch (SQLException e) {
			writeSQLError(e);
			if(!recursed) {
				reconnect();
				recursed = true;
				return queryDB(query);
			}
			return null;
		}
	}
	
	/**
	 * 
	 * @param query
	 * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
	 */
	public int updateDB(String query) {
		if(_stmt == null) { return -1; }
		if(_con == null) { return -2; }
		
		try {
			int ret = _stmt.executeUpdate(query);
			recursed = false;
			return ret;
		} catch (SQLException e) {
			writeSQLError(e);
			if(!recursed) {
				reconnect();
				recursed = true;
				return updateDB(query);
			}
			return -3;
		}
	}
}
