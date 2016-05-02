package com.dynocloud.server.api;

public class User {
	private int userID;
	private String userName;
	private String password;
	private String name;
	private String lastName;
	private String email;
	private String phone;
	
	private boolean DynoCloud;
	private int centralNodeID;
	
	public boolean isDynoCloud() {
		return DynoCloud;
	}
	public void setDynoCloud(boolean dynoCloud) {
		DynoCloud = dynoCloud;
	}
	public int getCentralNodeID() {
		return centralNodeID;
	}
	public void setCentralNodeID(int centralNodeID) {
		this.centralNodeID = centralNodeID;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Override
	public String toString() {
		return "User [userID=" + userID + ", userName=" + userName + ", password=" + password + ", name=" + name
				+ ", lastName=" + lastName + ", email=" + email + ", phone=" + phone + ", DynoCloud=" + DynoCloud
				+ ", centralNodeID=" + centralNodeID + "]";
	}

}
