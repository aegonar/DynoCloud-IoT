import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendMessage {
	
	public static void main (String[] args) {
	
		Telemetry telemetry = new Telemetry();
		
		telemetry.setCentralNodeID(1);
		telemetry.setUserID(2);
		telemetry.setEnclosureNodeID(2);
		telemetry.setTEMP((float) 85.309934);
		telemetry.setRH((float) 48.952413);
		telemetry.setOPTIONAL_LOAD(60581);
		telemetry.setHEAT_LOAD(60581);
		telemetry.setUV_STATUS(1);
		telemetry.setHUMI_STATUS(0);
		telemetry.setDateTime("04-18-16 17:53:11");
		
		ObjectMapper mapper = new ObjectMapper();
		String telemetryJsonString = null;
		
		try {
			telemetryJsonString = mapper.writeValueAsString(telemetry);
			
		} catch (JsonProcessingException e) {
			
			System.out.println("Error mapping to json: " + e.getMessage());
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
		}
		
		System.out.println(telemetryJsonString);
	
	Header auth = new Header();
	auth.setKey("Authorization");
	auth.setKey("Bearer 3p35vittr361q4socmtqhmeos6");
	
	Header mediaType = new Header();
	mediaType.setKey("Content-Type");
	mediaType.setKey("application/json");
	
	ArrayList<Header> headerList = new ArrayList<Header>();
	headerList.add(auth);
	headerList.add(mediaType);
	
	MessageRequest message = new MessageRequest();
	message.setHeaderList(headerList);
	message.setMethod("POST");
	message.setPath("/publish");
	message.setPayload(telemetryJsonString);
	

	String messageJsonString = null;
	
	try {
		messageJsonString = mapper.writeValueAsString(message);
		
	} catch (JsonProcessingException e) {
		
		System.out.println("Error mapping to json: " + e.getMessage());
//		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JSON mapping error").build();
	}
	
	System.out.println();
	System.out.println(messageJsonString);
	
	
	
	
	MessageRequest mFromJSON = null;
	try {
		mFromJSON = mapper.readValue(messageJsonString, MessageRequest.class);
	} catch (JsonParseException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (JsonMappingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	//Telemetry tTromJSON = mapper.readValue(mFromJSON.getPayload(), Telemetry.class);
	
	
	String url = "http://localhost/node_api/publish";
	URL obj;
	HttpURLConnection con = null;
	
	try {
		
		obj = new URL(url);
		
	try {
		
		con = (HttpURLConnection) obj.openConnection();
		
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		//con.setRequestProperty("Authorization", "Bearer 3p35vittr361q4socmtqhmeos6");

		String urlParameters = mFromJSON.getPayload();
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		System.out.println(response.toString());
				
	} catch (MalformedURLException e) {
		System.out.println("Error connecting to Server");
	}
	} catch (IOException e) {
		System.out.println("Server Response: Malformed Message");
	}
	
	
	}

}
