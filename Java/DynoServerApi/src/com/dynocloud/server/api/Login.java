package com.dynocloud.server.api;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.ArrayList;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
//import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;



@Path("/login")
public class Login {

	@POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response authenticateUser(Credentials credentials) {

		System.out.println("[POST] /login");
		
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        try {

            // Authenticate the user using the credentials provided
            authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(username);

            // Return the token on the response
            return Response.ok("{\"token\":\""+ token + "\"}", MediaType.APPLICATION_JSON).cookie(new NewCookie("token", token)).build();

        } catch (Exception e) {
        	System.out.println("Error authenticating user");
            return Response.status(Response.Status.UNAUTHORIZED).build();
            
        }    
   
    }

    private void authenticate(String username, String password) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    	
    	
    	
    	 Database_connection link = new Database_connection();
       	 PreparedStatement prep_sql;

        	//System.out.println("authenticate [" + username + ", "+password+"]");
      	  
      	  		link.Open_link();
      		

      	  		
      	  	try{
      			String query_authenticate= "Select `UserID` from `Users` where `UserName` = ? and `Password` = ?;";
      			prep_sql = link.linea.prepareStatement(query_authenticate);
      			prep_sql.setString(1, username);
      			prep_sql.setString(2, password);
      			
      			ResultSet rs_query_authenticate = prep_sql.executeQuery();

      			
      			if (!rs_query_authenticate.next() ) {
      			    System.out.println("rs_query_authenticate no data");
      			  throw new NotAuthorizedException("Invalid username or password");
      			} else {
          			//while(rs_query_authenticate.next()){
          				//int UserID = rs_query_authenticate.getInt("UserID");
          				//System.out.println("rs_query_authenticate: " + UserID);
          				//something something
          			//}
      			}
      			

      			
      	  }catch(Exception e){

    			System.out.println("Error at query_authenticate: " + e.getMessage());
    			
    			link.Close_link();
    			throw new NotAuthorizedException("Invalid username or password");
    			
    		}
    	
    	
    	
      	  link.Close_link();
    	

    }

    public String issueToken(String username) throws SQLException {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
   
   	 Database_connection link = new Database_connection();
   	 PreparedStatement prep_sql;

    	//System.out.println("getUserID [" + username + "]");
  	  
  	  		link.Open_link();
  		
  	  	String UserID=null;
  	  		
  	  	try{
  			String query_getUserID = "Select `UserID` from `Users` where `UserName` = ?;";
  			prep_sql = link.linea.prepareStatement(query_getUserID);
  			prep_sql.setString(1, username);
  			
  			ResultSet rs_query_getUserID = prep_sql.executeQuery();

  			while(rs_query_getUserID.next()){
  				UserID = rs_query_getUserID.getString("UserID");
  			}
  			
  	  }catch(Exception e){

			System.out.println("Error at query_getUserID: " + e.getMessage());
			
			link.Close_link();
			throw new SQLException();
		}
			
			
	    	Random random = new SecureRandom();
	        String token = new BigInteger(130, random).toString(32);
	    	
	        
	    try{    
	    	String query_setToken = "Insert into `Session` (`UserID`,`Token`,`DynoCloud`) values (?,?,?);";
  			prep_sql = link.linea.prepareStatement(query_setToken);
  			prep_sql.setString(1, UserID);
  			prep_sql.setString(2, token);
  			prep_sql.setBoolean(3, false);
  			prep_sql.executeUpdate();
  			//System.out.println("issueToken [Execute Insert]");
  			
	        
    }catch(Exception e){

		System.out.println("Error at query_setToken: " + e.getMessage());
		
		link.Close_link();
		throw new SQLException();
	}  

  	link.Close_link();
  	
  	
  	return token;
  	
    }
    
}