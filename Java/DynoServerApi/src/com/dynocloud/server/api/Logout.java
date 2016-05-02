package com.dynocloud.server.api;


import java.sql.PreparedStatement;
//import java.sql.ResultSet;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/logout")

public class Logout{

	@POST
    //@Produces("application/json")
    public Response delteToken(@Context HttpHeaders headers) {

		System.out.println("POST] /logout");
		

        String authorizationHeader = 
        		headers.getHeaderString(HttpHeaders.AUTHORIZATION);


        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        
    	Database_connection link = new Database_connection();
     	 PreparedStatement prep_sql;
        
        //try {
  	
        	  
        	  		link.Open_link();
        		

        	  		
        	  	try{
        			String query_deleteToken= "Delete FROM Session where `Token` = ?;";
        			prep_sql = link.linea.prepareStatement(query_deleteToken);
        			prep_sql.setString(1, token);
        			

          			int rs_query_deleteToken = prep_sql.executeUpdate();

          			if(rs_query_deleteToken==0){
          				
         			    System.out.println("query_validateToken no data");
       			 // throw new NotAuthorizedException("Invalid session token");
       			return Response.status(Response.Status.UNAUTHORIZED).build();
       			  
        	  	}else{
        	  		
        	  		System.out.println("logout [" + token +"]");
        	  	   return Response.ok(token).entity("logout is called, authorizationHeader : " + token).build();
        	  	   
        	  	}



        			
        	  }catch(Exception e){

      			System.out.println("Error at query_deleteToken: " + e.getMessage());
      			
      			link.Close_link();
      			//throw new NotAuthorizedException("Invalid session token");
      			return Response.status(Response.Status.UNAUTHORIZED).build();
      		}
      	
      	
      	
//        	 link.Close_link();
//        	
//        	
//        	
//             Response.ok(token).build();

//        } catch (Exception e) {
//        	System.out.println("Error authenticating user");
//        	return Response.status(Response.Status.UNAUTHORIZED).build();
//            
//        } 
        

    }


    
}