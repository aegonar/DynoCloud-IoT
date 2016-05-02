//<<<<<<< HEAD
package com.dynocloud.test;
//=======
//
//>>>>>>> master

import java.io.*;
import java.sql.*;
import java.util.Properties;



public class Database_connection{

	/** The driver namespace being in use	 */
	private String driverName = "";
	/** The server address, note to be encapsulated	 */
	private String serverName = "";
	/** The database name	 */
	private String database = "";
	/** The connection string url  */
	private String url = "";
	/** The user name: 	 */
	private String username = "";
	/** The password	 */
	private String password = "";
	/** Connection arguments	 */
	private String arguments = "";		/*AG*/	
	/** The connection object to be used 	 */
	public Connection linea;
		
	/** Blank default constructor	 */
	public Database_connection(){
		//this.init();					/*AG*/
		url = "jdbc:mysql://localhost/server";
		username="root";
		password="root";
		driverName="com.mysql.jdbc.Driver";
	}

/*-------------------------------------------------------------------------------------------------*/
	
	/** Constructor	initializer */
	
	private void init(){
		
		String properties_file = null;
		String db_con_mode = null;
		
			try{
				db_con_mode = System.getenv("db_con_mode");
					if(db_con_mode.equals("")){
						String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
						String className = Thread.currentThread().getStackTrace()[1].getClassName();
						String message = ("Error: Envvar \"db_con_mode\" is not set as \"daily\", \"monthly\", etc.");
						stopProgramExecution(methodName, className, message);
					}else{
						//System.out.println("Using " + db_con_mode + " database connection configuration.");
						properties_file = db_con_mode + "_DBconnection.properties";
					}
			}catch(NullPointerException e){
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				String className = Thread.currentThread().getStackTrace()[1].getClassName();
				String message = ("Variables not set, set envvar \"db_con_mode\" as \"daily\", \"monthly\", etc.");
				stopProgramExecution(methodName, className, message);
			}
	
		String file_location = "";
		
			try{
				file_location = System.getenv("db_con_files");
					if(file_location.equals("")){
					String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
					String className = Thread.currentThread().getStackTrace()[1].getClassName();
					String message = ("Error: Envvar \"db_con_files\" is not set as a path to the database connection configuration files.");
					stopProgramExecution(methodName, className, message);
					}
			}catch(NullPointerException e){
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				String className = Thread.currentThread().getStackTrace()[1].getClassName();
				String message = ("Variables not set, set envvar \"db_con_files\" as a path to the database connection configuration files.");
				stopProgramExecution(methodName, className, message);
			}
		
		Properties properties = new Properties();
		
		 	try{
		 		properties.load(new FileInputStream(file_location + properties_file));
		 			driverName = properties.getProperty("driverName");
		    		serverName = properties.getProperty("serverName");
		    		database = properties.getProperty("database");
		    		username = properties.getProperty("username");
		    		password = properties.getProperty("password");
		    		arguments = properties.getProperty("arguments");
		 	}catch(NullPointerException e){
		        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		        String className = Thread.currentThread().getStackTrace()[1].getClassName();
				String message = ("Error: DB connection properties file not found.");
				stopProgramExecution(methodName, className, message);
		 	}catch(IOException e){
		 		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		 		String className = Thread.currentThread().getStackTrace()[1].getClassName();
				String message = ("Error reading DB connection properties file.");
				stopProgramExecution(methodName, className, message);
	        }
	 	
	 	url = "jdbc:mysql://" + serverName + "/" + database + arguments;
	}

/*-------------------------------------------------------------------------------------------------*/
	
	/** This method opens a database connection with the set connection string	 */
	public void Open_link(){
		//System.out.println("Opening Database connection link (//" + serverName + "/" + database + arguments + ").");
		try{
			Class.forName(driverName);
			linea =  DriverManager.getConnection(url, username, password);
			//System.out.println("Database connection link established.");
		}
		catch (SQLException e) {
			System.out.println("Error connecting to Database (//" + serverName + "/" + database + arguments + ").");
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String className = Thread.currentThread().getStackTrace()[1].getClassName();
			String message = ("Error: " + e.getMessage() );
			stopProgramExecution(methodName, className, message);
			return;
		}
		catch (ClassNotFoundException f) {
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				String className = Thread.currentThread().getStackTrace()[1].getClassName();
				String message = ("SQL jar missing or not found, check classpath.");
				stopProgramExecution(methodName, className, message);
		}
	}
	
	
	/** This method closes the database link */
	public void Close_link(){
		//System.out.println("Closing Database connection link (//" + serverName + "/" + database + ").");
		try{
			if(linea != null && !linea.isClosed()) linea.close();
			//System.out.println("Database connection link closed.");
		}
		catch(SQLException e){
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String className = Thread.currentThread().getStackTrace()[1].getClassName();
			String message = ("There was an error closing the database connection");
			stopProgramExecution(methodName, className, message);
		}
	}
	
	
	/** @param value is the String to set up the mysql driver name */
	public void Set_driver_name(String value){
		driverName = value;
	}
	/** @param value is the String to set up the server name */
	public void Set_server_name(String value){
		serverName = value;
	}
	/** @param value is the String to set up the database name/schema */
	public void Set_dbname(String value){
		database = value;
	}
	/** @param value is the String used to set up the url for the connection */
	public void Set_url(String value){
		url = value;
	}
	/** @param value is the String to set up the user name */
	public void Set_username(String value){
		username = value;
	}
	/** @param value is the string to set up the password */
	public void Set_password(String value){
		password = value;
	}
	
	/** @param value is the string to set up the password */
	public void Set_arguments(String value){		/*AG*/
		arguments = value;
	}
	
	
	/** @return The driver name set up */
	public String Get_driver_name(){
		return driverName;
	}
	/** @return The server name set up */
	public String Get_server_name(){
		return serverName;
	}
		/** @return The set up database */
	public String Get_dbname(){
		return database;
	}	
	/** @return The setup url */
	public String Get_url(){
		return url;
	}	
	/** @return The set up user name */
	public String Get_username(){
		return username;		
	}
	/** @return The set up password */
	public String Get_password(){
		return password;
	}	
	/** @return The set up DB arguments */
	public String Get_arguments(){				/*AG*/
		return arguments;
	}
	
	private static void stopProgramExecution(String methodName, String className, String message) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackTraceElement main = stack[stack.length - 1];
		String programName = main.getClassName();
		System.out.println("***************************************************************************************");
		System.out.println("*ERROR*ERROR*ERROR*ERROR*ERROR*ERROR*ERROR*ERROR**ERROR*ERROR*ERROR*ERROR**ERROR*ERROR*");
		System.out.println("***************************************************************************************");
		System.out.println("Error in program ---> " + programName);
		System.out.println("Error in class   ---> " + className);
		System.out.println("Error in method  ---> " + methodName);
		System.out.println("Error message    ---> " + message);
		System.out.println("***************************************************************************************");
		System.out.println("*ERROR*ERROR*ERROR*ERROR*ERROR*ERROR*ERROR*ERROR**ERROR*ERROR*ERROR*ERROR**ERROR*ERROR*");
		System.out.println("***************************************************************************************");
	}
}
