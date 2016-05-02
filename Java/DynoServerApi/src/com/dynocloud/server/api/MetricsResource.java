package com.dynocloud.server.api;

import javax.ws.rs.GET;
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
//import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/metrics")
public class MetricsResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
		
	@Logged
	@GET
	@Path("{CentralNodeID}/{EnclosureNodeID}/{Timeframe}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMetrics(@PathParam("CentralNodeID") int CentralNodeID, @PathParam("EnclosureNodeID") int EnclosureNodeID, 
							@PathParam("Timeframe") int Timeframe, @Context HttpHeaders headers) {
	  	
	  Session session = new Session(headers);
      User currentUser = session.getUser();       
      int userID=currentUser.getUserID();
      
      System.out.println("["+currentUser.getUserName()+"] [GET] /metrics/"+CentralNodeID+"/"+EnclosureNodeID+"/"+Timeframe);

	  link.Open_link();
			  
	  ArrayList<Metrics> list = new ArrayList<Metrics>();
	  
	  LocalDateTime now = LocalDateTime.now();;		
      LocalDateTime past = now.minus(Timeframe, ChronoUnit.HOURS);
     // LocalDateTime nowInc = now.plus(0, ChronoUnit.HOURS);
      	
      int index=0;
      int masterIndex=0;
      
		try{
			String query_metrics = "SELECT * FROM Telemetry where `UserID` = ? AND `CentralNodeID` = ? AND `EnclosureNodeID` = ? AND `DateTime`  >=  ?  AND `DateTime` < ? ORDER BY `DateTime` DESC;";
			prep_sql = link.linea.prepareStatement(query_metrics);
			
			prep_sql.setInt(1, userID);
			prep_sql.setInt(2, CentralNodeID);
			prep_sql.setInt(3, EnclosureNodeID);
			prep_sql.setString(4, past+"");
			prep_sql.setString(5, now+"");
			
			ResultSet rs_query_metrics= prep_sql.executeQuery();
			
			while(rs_query_metrics.next()){
				
				if(index != Timeframe){
					index++;
				} else {
				
					Metrics metrics = new Metrics();
					
					metrics.setCentralNodeID(rs_query_metrics.getInt("CentralNodeID"));
					metrics.setUserID(rs_query_metrics.getInt("UserID"));
					metrics.setEnclosureNodeID(rs_query_metrics.getInt("EnclosureNodeID"));
					
					metrics.setTEMP(rs_query_metrics.getFloat("TEMP"));
					metrics.setRH(rs_query_metrics.getFloat("RH"));
					
					metrics.setOPTIONAL_LOAD(rs_query_metrics.getFloat("OPTIONAL_LOAD"));
					metrics.setHEAT_LOAD(rs_query_metrics.getFloat("HEAT_LOAD"));
					
					metrics.setHUM_OR(rs_query_metrics.getInt("HUM_OR"));
					metrics.setHEAT_OR(rs_query_metrics.getInt("HEAT_OR"));
					metrics.setUV_OR(rs_query_metrics.getInt("UV_OR"));
					metrics.setOPTIONAL_OR(rs_query_metrics.getInt("OPTIONAL_OR"));
					
					metrics.setUV_STATUS(rs_query_metrics.getInt("UV_STATUS"));
					
//					if(rs_query_metrics.getInt("HUM_STATUS") == 1)
//						metrics.setHUM_STATUS(100);
//					else
//						metrics.setHUM_STATUS(0);
					metrics.setHUM_STATUS(rs_query_metrics.getInt("HUM_STATUS"));
					metrics.setHEAT_STATUS(rs_query_metrics.getInt("HEAT_STATUS"));
					metrics.setOPTIONAL_STATUS(rs_query_metrics.getInt("OPTIONAL_STATUS"));
					
						
					Timestamp myTimestamp = rs_query_metrics.getTimestamp("DateTime");
					String S = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myTimestamp);			
					metrics.setDateTime(S);
					
					list.add(metrics);
					
					index=0;
				}
				
				
				masterIndex++;

			}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error loading metrics").build();
				
		}

	link.Close_link();
	
	System.out.println("---Total: " + masterIndex);
	System.out.println("---Reduced: " + list.size());
	
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
	
	
	class ChartLine{
		
		int index;
		Metrics metrics;
		
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public Metrics getMetrics() {
			return metrics;
		}
		public void setMetrics(Metrics metrics) {
			this.metrics = metrics;
		}	
		
	}
} 