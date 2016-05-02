//<<<<<<< HEAD
package com.dynocloud.test;
//
//=======
//>>>>>>> master
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

//<<<<<<< HEAD
public class dbtest {
//=======
//public class test {
//>>>>>>> master
	
	private static Database_connection link = new Database_connection();
	private static PreparedStatement prep_sql;
	
//<<<<<<< HEAD
	public static void main (String[] args){
//=======
//	public static void main (String a[]){
//>>>>>>> master
			

		link.Open_link();
		
		while(true){	
		
			try{				
				String query = "INSERT INTO Telemetry (`DateTime`,`UserID`, `CentralNodeID`, `EnclosureNodeID`,`Temperature`,`Humidity`,`Load_IR`,`Load_IC`,`State_UV`,`State_HUM`) VALUES (now(),'2', '1', '1','80.9','50.2','90.0','75.0','1','1');";
				
				System.out.println(LocalDateTime.now());
				prep_sql = link.linea.prepareStatement(query);
				
				//prep_sql.setTimestamp(1, parseDate("04/13/16 22:11:26"));
				
				prep_sql.executeUpdate();

				
				
			}catch(Exception e){

				System.out.println("Error: " + e.getMessage());
				
				link.Close_link();

			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//link.Close_link();
		
	}
	
//	private static java.sql.Timestamp getCurrentTimeStamp() {
//
//		java.util.Date today = new java.util.Date();
//		return new java.sql.Timestamp(today.getTime());
//
//	}
	
	private static java.sql.Timestamp parseDate(String s) {
		
		DateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		Date date = null;

		try {
	
			date = formatter.parse(s);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
	return new java.sql.Timestamp(date.getTime());
	
	}

}
