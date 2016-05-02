package com.dynocloud.server.api;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

//import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.Priorities;
//import javax.ws.rs.WebApplicationException;


@Logged
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    /*
    	public void filter(ContainerRequestContext containerRequest) throws IOException {
    //public void filter(ContainerRequest containerRequest) throws WebApplicationException {
        //GET, POST, PUT, DELETE, ...
        //String method = containerRequest.getMethod();
        // myresource/get/56bCA for example
        //String path = containerRequest.getPath(true);
 
        //We do allow wadl to be retrieve
//        if(method.equals("GET") && (path.equals("application.wadl") || path.equals("application.wadl/xsd0.xsd")){
//            //return containerRequest;
//        }
 
        //Get the authentification passed in HTTP headers parameters
        String auth = containerRequest.getHeaderString(HttpHeaders.AUTHORIZATION);
        System.out.println("auth header: _" + auth);
        //If the user does not have the right (does not provide any HTTP Basic Auth)
        if(auth == null){
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
 
        //lap : loginAndPassword
        String[] lap = BasicAuth.decode(auth);
 
        //If login or password fail
        if(lap == null || lap.length != 2){
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
 
        System.out.println("l: " + lap[0]);
        System.out.println("p: " + lap[1]);
                    
        
        Database_connection link = new Database_connection();
      	 PreparedStatement prep_sql;

       	System.out.println("authenticate [" + lap[0] + ", "+lap[1]+"]");
     	  
     	  		link.Open_link();
     		

     	  		
     	  	try{
     			String query_authenticate= "Select `UserID` from `Users` where `UserName` = ? and `Password` = ?;";
     			prep_sql = link.linea.prepareStatement(query_authenticate);
     			prep_sql.setString(1, lap[0]);
     			prep_sql.setString(2, lap[1]);
     			
     			ResultSet rs_query_authenticate = prep_sql.executeQuery();

     			
     			if (!rs_query_authenticate.next() ) {
     			    System.out.println("rs_query_authenticate no data");
     			  throw new NotAuthorizedException("Invalid username or password");
     			  

     			} else {
         			//while(rs_query_authenticate.next()){
         				String UserID = rs_query_authenticate.getString("UserID");
         				System.out.println("rs_query_authenticate: " + UserID);
         				//something something
         			//}
     			}
     			

     			
     	  }catch(Exception e){

   			System.out.println("Error at query_authenticate: " + e.getMessage());
   			
   			link.Close_link();
   			//throw new NotAuthorizedException("Invalid username or password");
			 containerRequest.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
   		}
   	
   	
   	
     	  link.Close_link();
        
        
        
        
        
        //DO YOUR DATABASE CHECK HERE (replace that line behind)...
//        User authentificationResult =  AuthentificationThirdParty.authentification(lap[0], lap[1]);
// 
//        //Our system refuse login and password
//        if(authentificationResult == null){
//            throw new WebApplicationException(Status.UNAUTHORIZED);
//        }
 
        //TODO : HERE YOU SHOULD ADD PARAMETER TO REQUEST, TO REMEMBER USER ON YOUR REST SERVICE...
 
        //return containerRequest;
    }  	
    	
  */  	
    	//System.out.println("AuthenticationFilter");

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {

            validateToken(token);

        } catch (Exception e) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        }
        
    }

    private void validateToken(String token) throws Exception {

    	Database_connection link = new Database_connection();
      	 PreparedStatement prep_sql;

       	//System.out.println("validateToken [" + token +"]");
     	  
     	  		link.Open_link();
     	    	  		
     	  	try{
     			String query_validateToken= "Select `UserID` from `Session` where `Token` = ?;";
     			prep_sql = link.linea.prepareStatement(query_validateToken);
     			prep_sql.setString(1, token);
     			
     			ResultSet rs_query_validateToken = prep_sql.executeQuery();

     			
     			if (!rs_query_validateToken.next() ) {
     				
     			    System.out.println("query_validateToken no data");
     			  throw new NotAuthorizedException("Invalid session token");
     			  
     			} //else {
    			
         			//	String userid = rs_query_validateToken.getString("UserID");
         				//System.out.println("rs_query_validateToken user identified: "+ userid);
//
     		//	}
     			
     	  }catch(Exception e){

   			System.out.println("Error at query_validateToken: " + e.getMessage());
   			
   			link.Close_link();
   			throw new NotAuthorizedException("Invalid session token");
   		}
   	 	
     	 link.Close_link();	
    	
    }
       
}
