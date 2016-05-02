package com.dynocloud.node.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/login")
public class IoTLogin {

	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	@POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response authenticateUser(Credentials credentials) {

		System.out.println("[POST] /login");
		
		boolean alreadyOnline = false;
		
		link.Open_link();
		
		try{
			String query_getOnline = "SELECT * FROM Config;";
			prep_sql = link.linea.prepareStatement(query_getOnline);

			ResultSet rs_query_getOnline = prep_sql.executeQuery();
			
				while(rs_query_getOnline.next()){	
					
					alreadyOnline = rs_query_getOnline.getBoolean("DynoCloud");
				}
				
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
			link.Close_link();
		}

		link.Close_link();
		
		if(alreadyOnline)
			return Response.status(Response.Status.FORBIDDEN).entity("Already loged in to DynoCloud").build();
			
		
		
		String url = "http://dynocare.xyz/api/IoT/login";
		
		//System.out.println("Path: " + url);
		
		URL obj = null;
		HttpURLConnection con = null;
			
			try {
				obj = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to DynoCloud").build();
			}
			
			try {
				con = (HttpURLConnection) obj.openConnection();
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to DynoCloud").build();
			}
			
			try {
				con.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to DynoCloud").build();
			}
			//System.out.println("Method: " + mFromJSON.getMethod());
			
			//for( Header header : mFromJSON.getHeaderList()){
				
				con.setRequestProperty("Content-Type", "application/json");
				//System.out.println("Header: " + header.getKey() + ": " + header.getValue());
				
		    	ObjectMapper mapper = new ObjectMapper();
	        	String jsonString = null;
	        	
	        	try {
	        		jsonString = mapper.writeValueAsString(credentials);
	        		
	        	} catch (JsonProcessingException e) {
	        		
	        		System.out.println("Error mapping to json: " + e.getMessage());
	        		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	        	}
			

			String urlParameters = jsonString;
				
			//String urlParameters = mFromJSON.getPayload();
			
			con.setDoOutput(true);
			DataOutputStream wr;
			try {
				wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to DynoCloud").build();
				//null payload

			}
				

			int responseCode = 0;
			try {
				responseCode = con.getResponseCode();
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to DynoCloud").build();
			}

			System.out.println("[POST] " +url+ " - Response Code: " + responseCode);
			if(responseCode == 401){
				return Response.status(Response.Status.NOT_FOUND).entity("Incorrect DynoCloud credentials").build();
			} else if(responseCode != 200){
				return Response.status(Response.Status.FORBIDDEN).entity("DynoCloud error").build();
			}

			BufferedReader in;
			StringBuffer response = null;
			
			try {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error connecting to DynoCloud").build();
			}

			
			//System.out.println("CN resp: "+response.toString());
			
			//-------------------------------------------------------------------					
			//ObjectMapper mapper = new ObjectMapper();
			IoTCredentials centralNodeCredentials = null;
				
			try {
				centralNodeCredentials = mapper.readValue(response.toString(), IoTCredentials.class);
			} catch (Exception e1) {
				System.out.println("Error mapping to json: " + e1.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
			}	
			
			//System.out.println(centralNodeCredentials);
			//------------------------------------------------------------------- 				
			
			link.Open_link();
	
			try{
				String query_postProfile = "UPDATE Config set `UserID`=?,`Token`=?,`CentralNodeID`=?,`UserName`=?,`DynoCloud`=? where `DynoCloud`=0;";
				
				prep_sql = link.linea.prepareStatement(query_postProfile);
				
				prep_sql.setInt(1, centralNodeCredentials.getUserID());
				prep_sql.setString(2, centralNodeCredentials.getToken());
				prep_sql.setInt(3, centralNodeCredentials.getCentralNodeID());
				prep_sql.setString(4, centralNodeCredentials.getUserName());
				prep_sql.setBoolean(5, true);
				
				prep_sql.executeUpdate();
	
			}catch(Exception e){
	
				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating config").build();
				
			}
			
		link.Close_link();	
		
		//------------------------------------------------------------------- 
			
		//-------------------------------------------------------------------
		//------------------------------------------------------------------- 
			
			DaemonControl telemetryDaemon = new DaemonControl("telemetry");
			telemetryDaemon.Stop();
			
			DaemonControl queueDaemon = new DaemonControl("queue");
			queueDaemon.Restart();
			
			DaemonControl requestDaemon = new DaemonControl("request");
			requestDaemon.Restart();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		//------------------------------------------------------------------- 
		//------------------------------------------------------------------- 
		
		 link.Open_link();
			
		 
		 try{
				String query_getProfiles = "SELECT * FROM PetProfiles;";
				prep_sql = link.linea.prepareStatement(query_getProfiles);
				
				//prep_sql.setInt(1, userID);
				
				ResultSet rs_query_getProfiles= prep_sql.executeQuery();
				
					while(rs_query_getProfiles.next()){
						
						PetProfile profile = new PetProfile();
								
						profile.setPetProfileID(rs_query_getProfiles.getString("PetProfileID"));
						//profile.setUserID(rs_query_getProfiles.getInt("UserID"));
						//profile.setName(rs_query_getProfiles.getString("Name"));
						profile.setDay_Temperature_SP(rs_query_getProfiles.getFloat("Day_Temperature_SP"));
						profile.setDay_Humidity_SP(rs_query_getProfiles.getFloat("Day_Humidity_SP"));
						profile.setNight_Temperature_SP(rs_query_getProfiles.getFloat("Night_Temperature_SP"));
						profile.setNight_Humidity_SP(rs_query_getProfiles.getFloat("Night_Humidity_SP"));
						profile.setTemperature_TH(rs_query_getProfiles.getFloat("Temperature_TH"));
						profile.setHumidity_TH(rs_query_getProfiles.getFloat("Humidity_TH"));

						profile.setDayTime(rs_query_getProfiles.getString("DayTime"));
						profile.setNightTime(rs_query_getProfiles.getString("NightTime"));
						
						SendToDynoServer sendToDynoServer = new SendToDynoServer(profile, "POST", "IoT/profiles");	
						sendToDynoServer.sendToServer();

					}
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading profiles").build();
				
			}

		link.Close_link();
		
		//------------------------------------------------------------------- 
		
		link.Open_link();
		
		  //ArrayList<Module> list = new ArrayList<Module>();
			
			try{
				String query_getModules = "SELECT * FROM EnclosureNode;";
				prep_sql = link.linea.prepareStatement(query_getModules);
				
				ResultSet rs_query_getModules= prep_sql.executeQuery();
				
					while(rs_query_getModules.next()){
						
						Module module = new Module();
								
						module.setEnclosureNodeID(rs_query_getModules.getInt("EnclosureNodeID"));
						module.setName(rs_query_getModules.getString("Name"));
						module.setOPTIONAL_LOAD(rs_query_getModules.getInt("OPTIONAL_LOAD"));
						module.setPetProfileID(rs_query_getModules.getString("PetProfileID"));
						
						module.setCentralNodeID(centralNodeCredentials.getCentralNodeID());
						module.setUserID(centralNodeCredentials.getUserID());

						SendToDynoServer sendToDynoServer = new SendToDynoServer(module, "POST", "IoT/module");	
						sendToDynoServer.sendToServer();

					}
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading profiles").build();
				
			}

		link.Close_link();
		
		//-------------------------------------------------------------------
		//------------------------------------------------------------------- 
		//------------------------------------------------------------------- 
		//------------------------------------------------------------------- 
		/*
		 link.Open_link();
			
		  //ArrayList<Telemetry> list = new ArrayList<Telemetry>();
			
			try{
				String query_telemetry = "SELECT * FROM Telemetry;";
				prep_sql = link.linea.prepareStatement(query_telemetry);
				
				ResultSet rs_query_telemetry = prep_sql.executeQuery();

					while(rs_query_telemetry.next()){
						
						Telemetry telemetry = new Telemetry();

						telemetry.setCentralNodeID(centralNodeCredentials.getCentralNodeID());
						telemetry.setUserID(centralNodeCredentials.getUserID());
						
						telemetry.setEnclosureNodeID(rs_query_telemetry.getInt("EnclosureNodeID"));
						
						telemetry.setTEMP(rs_query_telemetry.getFloat("TEMP"));
						telemetry.setRH(rs_query_telemetry.getFloat("RH"));
						
						telemetry.setOPTIONAL_LOAD(rs_query_telemetry.getFloat("OPTIONAL_LOAD"));
						telemetry.setHEAT_LOAD(rs_query_telemetry.getFloat("HEAT_LOAD"));
						
						telemetry.setHUM_OR(rs_query_telemetry.getInt("HUM_OR"));
						telemetry.setHEAT_OR(rs_query_telemetry.getInt("HEAT_OR"));
						telemetry.setUV_OR(rs_query_telemetry.getInt("UV_OR"));
						telemetry.setOPTIONAL_OR(rs_query_telemetry.getInt("OPTIONAL_OR"));
						
						telemetry.setUV_STATUS(rs_query_telemetry.getInt("UV_STATUS"));
						telemetry.setHUM_STATUS(rs_query_telemetry.getInt("HUM_STATUS"));
						telemetry.setHEAT_STATUS(rs_query_telemetry.getInt("HEAT_STATUS"));
						telemetry.setOPTIONAL_STATUS(rs_query_telemetry.getInt("OPTIONAL_STATUS"));
						
							
						Timestamp myTimestamp = rs_query_telemetry.getTimestamp("DateTime");
						String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
						telemetry.setDateTime(S);
								
						SendToDynoServer sendToDynoServer = new SendToDynoServer(telemetry, "POST", "publish");	
						sendToDynoServer.sendToServer();;

					}
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading telemetry").build();
				
			}

			link.Close_link();
		*/
		//-------------------------------------------------------------------
		//------------------------------------------------------------------- 
		//------------------------------------------------------------------- 
		
		telemetryDaemon.Start();
	
        return Response.status(Response.Status.OK).build();  
   
    }
    
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLogin() {
      
      System.out.println("[GET] /login");
		
		 link.Open_link();

		 IoTCredentials IoTCredentials = new IoTCredentials();
			
			try{
				String query_getOnline = "SELECT * FROM Config;";
				prep_sql = link.linea.prepareStatement(query_getOnline);

				ResultSet rs_query_getOnline = prep_sql.executeQuery();
				
					while(rs_query_getOnline.next()){	
						
						IoTCredentials.setOnline(rs_query_getOnline.getBoolean("DynoCloud"));
						IoTCredentials.setUserID(rs_query_getOnline.getInt("UserID"));
						IoTCredentials.setCentralNodeID(rs_query_getOnline.getInt("CentralNodeID"));
						IoTCredentials.setUserName(rs_query_getOnline.getString("UserName"));
												
					}
					
			}catch(Exception e){
				System.out.println("Error: " + e.getMessage());
				link.Close_link();
			}

		link.Close_link();
		
		//-----------------------------------------
		
		if(IoTCredentials.isOnline()){
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = null;
			
			try {
				jsonString = mapper.writeValueAsString(IoTCredentials);
				
			} catch (JsonProcessingException e) {
				
				System.out.println("Error mapping to json: " + e.getMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
			}
			
			return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
			
		} else {
			return Response.status(Response.Status.OK).entity("{\"online\":false}").build();
		}
		
	}
	
	void sync(){
		
	}
	
}