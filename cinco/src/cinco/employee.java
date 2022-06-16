package cinco;

import cinco.authentication.Group;

public abstract class employee implements cinco.authentication.User {
	private String username;
	private String password;
	private Group group;
	private String phNumber;
	private String lastName;
	private String firstName;
	
	@Override
	public String getUserName() {
		return username;
	}
	@Override
	public void setUserName(String username) {
		this.username=username;
	}
	@Override
	public String getPassword() {
		return password;
	}
	@Override
	public void setPassword(String password) {
		this.password=password;
	}
	
	@Override
	public Group getGroup() {
		return this.group;
	}
	@Override
	public void setGroup(Group group) {
		this.group = group;
	}
	
	@Override
	public String getPhNumber() {
		return this.phNumber;
	}
	@Override
	public void setPhNumber(String phNumber) {
		this.phNumber = phNumber;
	}
	

	
	@Override
	public String getLName() {
		return this.lastName;
	}
	@Override
	public void setLName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public String getFName() {
		return this.firstName;
	}
	@Override
	public void setFName(String firstName) {
		this.firstName = firstName;
	}
}
