package cinco;

import cinco.authentication.Group;

public class technician extends employee {
	private int level;
	private int currentTicketNumber;

	public technician() {}
	
	public technician(String username, String password, Group group, int level) {
		this.setUserName(username);
		this.setPassword(password);
		this.setGroup(group);
		this.level=level;
		this.currentTicketNumber= 0;
		
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level=level;
	}
	
	public void increaseTicketCount(){
		this.currentTicketNumber++;
	}
	public void decreaseTicketCount() {
		this.currentTicketNumber--;
	}
	public int getTicketCount() {
		return this.currentTicketNumber;
	}
	
}
