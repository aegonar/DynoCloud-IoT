package com.dynocloud.server.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.ArrayList;
import java.util.Date;


@Path("/publish")
public class TelemetryResource {
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
//	@GET
//  	@Produces(MediaType.APPLICATION_JSON)
//	public String GetTelemetry() throws Exception{
//		
//		System.out.println("Telemetry [GET]");
//		
//		String result="Telemetry";
//		
//		return result;
//	}
	
	@Logged
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public ArrayList<Telemetry> GetTelemetry(@Context HttpHeaders headers) {
	  
	  System.out.println("Telemetry [GET]");
	  
		Session session = new Session(headers);
        User currentUser = session.getUser();
        
        int userID=currentUser.getUserID();
	  
	  link.Open_link();
		
	  ArrayList<Telemetry> list = new ArrayList<Telemetry>();
		
		try{
			String query_telemetry = "SELECT * FROM Telemetry where `UserID` = ?";
			prep_sql = link.linea.prepareStatement(query_telemetry);
			
			prep_sql.setInt(1, userID);
			
			ResultSet rs_query_telemetry = prep_sql.executeQuery();
			System.out.println("executeQuery");
			
				while(rs_query_telemetry.next()){
					
					Telemetry telemetry = new Telemetry();
					
					//`EnclosureNodeID`,`TEMP`,`RH`,`OPTIONAL_LOAD`,`HEAT_LOAD`,`UV_STATUS`,`HUMI_STATUS`
					
					telemetry.setCLIENTID(rs_query_telemetry.getInt("EnclosureNodeID"));
					telemetry.setTEMP(rs_query_telemetry.getInt("TEMP"));
					telemetry.setRH(rs_query_telemetry.getInt("RH"));
					telemetry.setOPTIONAL_LOAD(rs_query_telemetry.getInt("OPTIONAL_LOAD"));
					telemetry.setHEAT_LOAD(rs_query_telemetry.getInt("HEAT_LOAD"));
					telemetry.setUV_STATUS(rs_query_telemetry.getInt("UV_STATUS"));
					telemetry.setHUM_STATUS(rs_query_telemetry.getInt("HUMI_STATUS"));
					
//					private int CLIENTID;
//					private float TEMP;
//					private float RH;
//					private float IR_PW;
//					private float IC_PW;
//					private int UV_STATUS;
//					private int HUMI_STATUS;
					
					
					list.add(telemetry);

				}
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
		}

	link.Close_link();
	    
  return list;
  }
  	@Logged
	  	@POST
	    @Consumes({MediaType.APPLICATION_JSON})
	    //@Produces({MediaType.APPLICATION_JSON})
	    public Response postTelemetry(Telemetry telemetry, @Context HttpHeaders headers) throws Exception{
	  		
	  		
		Session session = new Session(headers);
        User currentUser = session.getUser();
        
        int userID=currentUser.getUserID();
        
        System.out.println("["+currentUser.getUserName()+"] [POST] /publish " +telemetry);
    		          
	        link.Open_link();
			
			try{
				String query_telemetry = "INSERT INTO Telemetry (`DateTime`,`EnclosureNodeID`,`TEMP`,`RH`,`OPTIONAL_LOAD`,`HEAT_LOAD`,`UV_STATUS`,`HUM_STATUS`,`HEAT_STATUS`,`OPTIONAL_STATUS`,`HUM_OR`,`HEAT_OR`,`UV_OR`,`OPTIONAL_OR`,`CentralNodeID`,`UserID`)"
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

						prep_sql = link.linea.prepareStatement(query_telemetry);
						
						prep_sql.setTimestamp(1, parseDate(telemetry.getDateTime()));
						prep_sql.setInt(2, telemetry.getCLIENTID());
						prep_sql.setFloat(3, telemetry.getTEMP());
						prep_sql.setFloat(4, telemetry.getRH());
						prep_sql.setFloat(5, telemetry.getOPTIONAL_LOAD());
						prep_sql.setFloat(6, telemetry.getHEAT_LOAD());
						prep_sql.setInt(7, telemetry.getUV_STATUS());
						prep_sql.setInt(8, telemetry.getHUM_STATUS());
						prep_sql.setInt(9, telemetry.getHEAT_STATUS());
						prep_sql.setInt(10, telemetry.getOPTIONAL_STATUS());
						prep_sql.setInt(11, telemetry.getHUM_OR());
						prep_sql.setInt(12, telemetry.getHEAT_OR());
						prep_sql.setInt(13, telemetry.getUV_OR());
						prep_sql.setInt(14, telemetry.getOPTIONAL_OR());

						prep_sql.setInt(15, telemetry.getCentralNodeID());
						prep_sql.setInt(16, userID);
							
						prep_sql.executeUpdate();
				
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();
				
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error publishing telemetry").build();
								
			}

		link.Close_link();
		
		return Response.status(Response.Status.OK).build();
	    
	    }

	private static java.sql.Timestamp parseDate(String s) {
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;

		try {
	
			date = formatter.parse(s);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
	return new java.sql.Timestamp(date.getTime());
	
	}
	  	
} 