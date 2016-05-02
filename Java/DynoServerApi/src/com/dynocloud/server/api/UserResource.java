package com.dynocloud.server.api;

import java.sql.PreparedStatement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/user")
public class UserResource {
		
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		
		System.out.println("["+user.getUserName()+"] [POST] /user");
        
        link.Open_link();
        
        String token = null;
		
		try{
			String query_register = "INSERT INTO Users (`UserName`,`Password`,`Name`,`LastName`,`Email`,`Phone`) VALUES (?,?,?,?,?,?);";
			prep_sql = link.linea.prepareStatement(query_register);
							
			prep_sql.setString(1, user.getUserName());
			prep_sql.setString(2, user.getPassword());
			prep_sql.setString(3, user.getName());
			prep_sql.setString(4, user.getLastName());
			prep_sql.setString(5, user.getEmail());
			prep_sql.setString(6, user.getPhone());
	
			int  rs_query_register = prep_sql.executeUpdate();
			
			if (rs_query_register == 0){
				return Response.status(Response.Status.FORBIDDEN).entity("Username or password error").build();
			}
				
			Login log = new Login();
	        token = log.issueToken(user.getUserName());               
	        
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			return Response.status(Response.Status.CONFLICT).entity("Username or password error").build();
				
		}

	link.Close_link();

	//return Response.ok(token).build();
	//return Response.status(Response.Status.OK).build();
	
//	ObjectMapper mapper = new ObjectMapper();
//	String jsonString = null;
//	
//	try {
//		jsonString = mapper.writeValueAsString(token);
//		
//	} catch (JsonProcessingException e) {
//		
//		System.out.println("Error mapping to json: " + e.getMessage());
//		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
//	}

	return Response.ok("{\"token\":\""+ token + "\"}", MediaType.APPLICATION_JSON).build();
  
	
	}
	
	@Logged
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(User user, @Context HttpHeaders headers) {
		
	  Session session = new Session(headers);
	  User currentUser = session.getUser();
      	  
      System.out.println("["+currentUser.getUserName()+"] [PUT] /user");
      
        link.Open_link();
      		
		try{
			String query_updateUser = "UPDATE Users SET `UserName`=?,`Name`=?,`LastName`=?,`Email`=?,`Phone`=? WHERE `UserID`=?;";
			prep_sql = link.linea.prepareStatement(query_updateUser);
							
			prep_sql.setString(1, user.getUserName());
			prep_sql.setString(2, user.getName());
			prep_sql.setString(3, user.getLastName());
			prep_sql.setString(4, user.getEmail());
			prep_sql.setString(5, user.getPhone());
			prep_sql.setInt(6, currentUser.getUserID());
	
			prep_sql.executeUpdate();
//			int  rs_query_updateUser = prep_sql.executeUpdate();
//			
//			if (rs_query_updateUser == 0){
//				return Response.status(Response.Status.CONFLICT).entity("Username or email already in use").build();			
//			}               
//	        
		}catch(Exception e){

			System.out.println("Error: " + e.getMessage());
			
			link.Close_link();
			
			//return Response.status(Response.Status.CONFLICT).entity("Error updating user").build();
			return Response.status(Response.Status.CONFLICT).entity("Username or email already in use").build();
				
		}

	link.Close_link();
	
	return Response.status(Response.Status.OK).build();
	}
	
	@Logged
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@Context HttpHeaders headers) {
			
		Session session = new Session(headers);
        User currentUser = session.getUser();

		System.out.println("["+currentUser.getUserName()+"] [GET] /user");
        
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		
		try {
			jsonString = mapper.writeValueAsString(currentUser);
			
		} catch (JsonProcessingException e) {
			
			System.out.println("Error mapping to json: " + e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
		}

	  return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();	  
	}
} 