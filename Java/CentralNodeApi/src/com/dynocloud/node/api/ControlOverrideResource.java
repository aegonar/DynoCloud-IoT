package com.dynocloud.node.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/override")
public class ControlOverrideResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	@GET
	@Path("{EnclosureNodeID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOverrides(@PathParam("EnclosureNodeID") int EnclosureNodeID,
								@Context HttpHeaders headers) {
     
      System.out.println("[GET] /override/"+EnclosureNodeID);
	  
	  link.Open_link();
		
	  ArrayList<ControlOverride> list = new ArrayList<ControlOverride>();
		
		try{
			String query_getOverrides = "SELECT * FROM OverrideHistory where `EnclosureNodeID`=?";
			prep_sql = link.linea.prepareStatement(query_getOverrides);

			prep_sql.setInt(1, EnclosureNodeID);
			
			ResultSet rs_query_getOverrides= prep_sql.executeQuery();
			
				while(rs_query_getOverrides.next()){
					
					ControlOverride controlOverride = new ControlOverride();
							
					controlOverride.setEnclosureNodeID(rs_query_getOverrides.getInt("EnclosureNodeID"));
//					controlOverride.setCentralNodeID(rs_query_getOverrides.getInt("CentralNodeID"));
//					controlOverride.setUserID(rs_query_getOverrides.getInt("UserID"));
					controlOverride.setHUM_OR(rs_query_getOverrides.getInt("HUM_OR"));
					controlOverride.setHEAT_OR(rs_query_getOverrides.getInt("HEAT_OR"));
					controlOverride.setUV_OR(rs_query_getOverrides.getInt("UV_OR"));
					controlOverride.setOPTIONAL_OR(rs_query_getOverrides.getInt("OPTIONAL_OR"));
					controlOverride.setHUM_STATUS(rs_query_getOverrides.getInt("HUM_STATUS"));
					controlOverride.setHEAT_STATUS(rs_query_getOverrides.getInt("HEAT_STATUS"));
					controlOverride.setUV_STATUS(rs_query_getOverrides.getInt("UV_STATUS"));
					controlOverride.setOPTIONAL_STATUS(rs_query_getOverrides.getInt("OPTIONAL_STATUS"));
					
					Timestamp myTimestamp = rs_query_getOverrides.getTimestamp("DateTime");
					String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
					controlOverride.setDateTime(S);
					
					list.add(controlOverride);

				}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading overrides").build();
			
		}

	link.Close_link();
	
	ObjectMapper mapper = new ObjectMapper();
	String jsonString = null;
	
	try {
		jsonString = mapper.writeValueAsString(list);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}

  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
  
  }

	@POST
	@Path("{EnclosureNodeID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postOverride(@PathParam("EnclosureNodeID") int EnclosureNodeID,
									ControlOverride controlOverride, @Context HttpHeaders headers){
	  	  
      System.out.println("[POST] /override/"+EnclosureNodeID);
      
	  link.Open_link();
			
		try{
			String query_postOverride = "INSERT INTO OverrideHistory (`EnclosureNodeID`,`DateTime`,`HUM_OR`,`HEAT_OR`,`UV_OR`,`OPTIONAL_OR`,`HUM_STATUS`,`HEAT_STATUS`,`UV_STATUS`,`OPTIONAL_STATUS`) VALUES (?,now(),?,?,?,?,?,?,?,?);";
			prep_sql = link.linea.prepareStatement(query_postOverride);

			prep_sql.setInt(1, EnclosureNodeID);
			prep_sql.setInt(2, controlOverride.getHUM_OR());
			prep_sql.setInt(3, controlOverride.getHEAT_OR());
			prep_sql.setInt(4, controlOverride.getUV_OR());
			prep_sql.setInt(5, controlOverride.getOPTIONAL_OR());
			prep_sql.setInt(6, controlOverride.getHUM_STATUS());
			prep_sql.setInt(7, controlOverride.getHEAT_STATUS());
			prep_sql.setInt(8, controlOverride.getUV_STATUS());
			prep_sql.setInt(9, controlOverride.getOPTIONAL_STATUS());
			
			prep_sql.executeUpdate();

		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error creating override").build();
			
		}

	link.Close_link();
	
	EnclosureNodeOverride enclosureNodeOverride = new EnclosureNodeOverride(controlOverride);
	SendToEnclosureNode mqtt = new SendToEnclosureNode(enclosureNodeOverride, EnclosureNodeID);
	mqtt.sendToNode();
	
	CloudSession cloudSession = new CloudSession();	
	if(cloudSession.isOnline()){	
		SendToDynoServer sendToDynoServer = new SendToDynoServer(controlOverride, "POST", "IoT/override/"+cloudSession.getCentralNodeID()+"/"+EnclosureNodeID);	
		sendToDynoServer.sendToServer();
	}
	
	return Response.status(Response.Status.OK).build();
  
  }
	
	class EnclosureNodeOverride {
		
		@JsonProperty("HUM_OR")
		int HUM_OR;
		@JsonProperty("HEAT_OR")
		int HEAT_OR;
		@JsonProperty("UV_OR")
		int UV_OR;
		@JsonProperty("OPTIONAL_OR")
		int OPTIONAL_OR;
		
		@JsonProperty("HUM_STATUS")
		int HUM_STATUS;
		@JsonProperty("HEAT_STATUS")
		int HEAT_STATUS;
		@JsonProperty("UV_STATUS")
		int UV_STATUS;
		@JsonProperty("OPTIONAL_STATUS")
		int OPTIONAL_STATUS;
		
		@JsonProperty("HUM_OR")
		public int getHUM_OR() {
			return HUM_OR;
		}
		@JsonProperty("HUM_OR")
		public void setHUM_OR(int hUM_OR) {
			HUM_OR = hUM_OR;
		}
		@JsonProperty("HEAT_OR")
		public int getHEAT_OR() {
			return HEAT_OR;
		}
		@JsonProperty("HEAT_OR")
		public void setHEAT_OR(int hEAT_OR) {
			HEAT_OR = hEAT_OR;
		}
		@JsonProperty("UV_OR")
		public int getUV_OR() {
			return UV_OR;
		}
		@JsonProperty("UV_OR")
		public void setUV_OR(int uV_OR) {
			UV_OR = uV_OR;
		}
		@JsonProperty("OPTIONAL_OR")
		public int getOPTIONAL_OR() {
			return OPTIONAL_OR;
		}
		@JsonProperty("OPTIONAL_OR")
		public void setOPTIONAL_OR(int oPTIONAL_OR) {
			OPTIONAL_OR = oPTIONAL_OR;
		}
		@JsonProperty("HUM_STATUS")
		public int getHUM_STATUS() {
			return HUM_STATUS;
		}
		@JsonProperty("HUM_STATUS")
		public void setHUM_STATUS(int hUM_STATUS) {
			HUM_STATUS = hUM_STATUS;
		}
		@JsonProperty("HEAT_STATUS")
		public int getHEAT_STATUS() {
			return HEAT_STATUS;
		}
		@JsonProperty("HEAT_STATUS")
		public void setHEAT_STATUS(int hEAT_STATUS) {
			HEAT_STATUS = hEAT_STATUS;
		}
		@JsonProperty("UV_STATUS")
		public int getUV_STATUS() {
			return UV_STATUS;
		}
		@JsonProperty("UV_STATUS")
		public void setUV_STATUS(int uV_STATUS) {
			UV_STATUS = uV_STATUS;
		}
		@JsonProperty("OPTIONAL_STATUS")
		public int getOPTIONAL_STATUS() {
			return OPTIONAL_STATUS;
		}
		@JsonProperty("OPTIONAL_STATUS")
		public void setOPTIONAL_STATUS(int oPTIONAL_STATUS) {
			OPTIONAL_STATUS = oPTIONAL_STATUS;
		}
			
		public EnclosureNodeOverride(ControlOverride controlOverride){
			this.HUM_OR = controlOverride.getHUM_OR();
			this.HEAT_OR = controlOverride.getHEAT_OR();
			this.UV_OR = controlOverride.getUV_OR();
			this.OPTIONAL_OR = controlOverride.getOPTIONAL_OR();
			this.HUM_STATUS = controlOverride.getHUM_STATUS();
			this.HEAT_STATUS = controlOverride.getHEAT_STATUS();
			this.UV_STATUS = controlOverride.getUV_STATUS();
			this.OPTIONAL_STATUS = controlOverride.getOPTIONAL_STATUS();
		}
	}
	
} 