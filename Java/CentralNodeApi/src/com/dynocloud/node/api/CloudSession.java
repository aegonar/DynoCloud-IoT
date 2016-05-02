package com.dynocloud.node.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CloudSession {
	
	String token;
	int userID;
	int centralNodeID;
	boolean online;
	
	public CloudSession(){
		
		Database_connection link = new Database_connection();
		PreparedStatement prep_sql;
		
		link.Open_link();
		
		
		try{
			String query_getConfig = "SELECT * FROM Config;";
			prep_sql = link.linea.prepareStatement(query_getConfig);

			ResultSet rs_query_getConfig = prep_sql.executeQuery();
			
				while(rs_query_getConfig.next()){	
					token = rs_query_getConfig.getString("Token");
					online = rs_query_getConfig.getBoolean("DynoCloud");
					userID = rs_query_getConfig.getInt("UserID");
					centralNodeID = rs_query_getConfig.getInt("CentralNodeID");
				}
				
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			link.Close_link();
		}

	link.Close_link();
		

	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getCentralNodeID() {
		return centralNodeID;
	}

	public void setCentralNodeID(int centralNodeID) {
		this.centralNodeID = centralNodeID;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}


	
	
	
}
