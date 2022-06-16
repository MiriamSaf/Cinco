package cinco;

public class StaffMember extends employee  {
	private int currentStaffTicketNumber;
	
	public StaffMember() {
		this.currentStaffTicketNumber=0;
	}
	
	public void increaseStaffTicketCount(){
		this.currentStaffTicketNumber++;
	}
	public void decreaseStaffTicketCount() {
		this.currentStaffTicketNumber--;
	}
	public int getStaffTicketCount() {
		return this.currentStaffTicketNumber;
	}
}
